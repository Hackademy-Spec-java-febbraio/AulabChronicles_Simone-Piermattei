package it.aulab.aulabchronicles.services;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// Importa SLF4j logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
// Rimuovi import non usati (LinkedMultiValueMap, MultiValueMap) se non servono più
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional; // Usa l'annotazione di Spring

import it.aulab.aulabchronicles.models.Article;
import it.aulab.aulabchronicles.models.Image;
import it.aulab.aulabchronicles.repositories.ImageRepository;
import it.aulab.aulabchronicles.utils.StringManipulation;
// Rimuovi import jakarta.transaction.Transactional se non usato

@Service
public class ImageServiceImpl implements ImageService {

    // Logger SLF4j
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Autowired
    private ImageRepository imageRepository;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String supabaseBucket;

    // Rimuovi @Value("${supabase.image}") se non serve più

    // Istanza RestTemplate riutilizzabile
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void saveImageOnDB(String apiUrl, Article article) {
        // Salva direttamente l'URL API restituito da Supabase
        logger.info("Salvataggio immagine nel DB con URL API: {}", apiUrl);
        imageRepository.save(Image.builder().path(apiUrl).article(article).build());
    }

    @Override
    @Async // Mantieni Async per l'upload
    public CompletableFuture<String> saveImageOnCloud(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.error("Tentativo di upload file nullo o vuoto.");
            return CompletableFuture.failedFuture(new IllegalArgumentException("File is empty or null"));
        }

        String originalFilename = file.getOriginalFilename(); // Ottieni nome originale per logging
        try {
            String nameFile = UUID.randomUUID().toString() + "_" + originalFilename;
            // String extension = StringManipulation.getFileExtension(nameFile); // Non
            // necessario per Supabase API
            // if (extension == null || extension.isEmpty()) {
            // logger.error("Impossibile determinare l'estensione per il file: {}",
            // nameFile);
            // return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid
            // file extension"));
            // }

            // URL API per l'upload
            String apiUrl = supabaseUrl + "/storage/v1/object/" + supabaseBucket + "/" + nameFile;
            logger.info("Tentativo upload su Supabase URL: {}", apiUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            // Supabase richiede Content-Type per l'upload diretto dei byte
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // O il MediaType specifico se noto

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity,
                    String.class);

            logger.info("Upload su Supabase completato: Status {}, URL API: {}", response.getStatusCode(), apiUrl);
            return CompletableFuture.completedFuture(apiUrl); // Restituisce l'URL API

        } catch (IOException e) {
            logger.error("Errore I/O durante lettura file per upload: {}", originalFilename, e);
            return CompletableFuture.failedFuture(e);
        } catch (RestClientException e) {
            logger.error("Errore API Supabase durante upload file: {}", originalFilename, e);
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) { // Catch generico per sicurezza
            logger.error("Errore imprevisto durante upload file: {}", originalFilename, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Transactional // Usa l'annotazione di Spring
    public void deleteImage(String imagePath) throws IOException { // imagePath qui è l'URL API salvato nel DB

        String apiUrl = imagePath; // Usiamo direttamente imagePath (che è l'URL API)
        logger.info("Tentativo eliminazione immagine da DB e Cloud. URL API: {}", apiUrl);

        // 1. Elimina dal Database
        try {
            // Chiama deleteByPath e ottieni il numero di righe eliminate
            int deletedCount = imageRepository.deleteByPath(imagePath);
            if (deletedCount == 0) {
                logger.warn("Nessuna immagine trovata nel DB per il path: {}", imagePath);
                // Non è un errore fatale, potremmo voler comunque tentare l'eliminazione dal
                // cloud
            } else {
                logger.info("Immagine/i ({}) eliminata/e dal DB per il path: {}", deletedCount, imagePath);
            }
        } catch (Exception e) {
            logger.error("Errore durante l'eliminazione dell'immagine dal DB per il path: {}", imagePath, e);
            // Rilancia come RuntimeException per causare rollback e segnalare fallimento
            // critico
            throw new RuntimeException("Errore DB durante eliminazione immagine: " + imagePath, e);
        }

        // 2. Elimina dal Cloud Storage
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Usa l'istanza restTemplate della classe
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.DELETE, entity, String.class);
            logger.info("Risposta eliminazione cloud: Status {}, Body: {}", response.getStatusCode(),
                    response.getBody());
        } catch (HttpClientErrorException e) {
            // Logga l'errore HTTP specifico
            logger.error("Errore HTTP {} durante eliminazione cloud: URL {}, Body: {}",
                    e.getStatusCode(), apiUrl, e.getResponseBodyAsString(), e);

            // Se l'errore è 404 (Not Found), logga solo un warning e non rilanciare.
            // Significa che l'oggetto non esiste (più) su Supabase, che per la delete è ok.
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Oggetto non trovato (404) su Supabase durante tentativo DELETE: {}. Considerato OK.",
                        apiUrl);
                // Non rilanciare l'eccezione
            } else {
                // Per altri errori HTTP (400, 401, 403, 5xx), rilancia per indicare fallimento
                // e permettere al chiamante (ArticleService) di gestire il rollback.
                throw new IOException(
                        "Fallita eliminazione immagine dal cloud (HTTP " + e.getStatusCode() + "): " + apiUrl, e);
            }
        } catch (RestClientException e) {
            // Cattura altri errori RestTemplate (es. timeout, connessione)
            logger.error("Errore RestClient durante eliminazione cloud: URL {}", apiUrl, e);
            // Rilancia per far fallire la transazione
            throw new IOException("Fallita eliminazione immagine dal cloud (RestClientException): " + apiUrl, e);
        }
        // Se arriviamo qui senza eccezioni (o con solo 404 gestito), l'operazione è
        // considerata riuscita
    }
}
