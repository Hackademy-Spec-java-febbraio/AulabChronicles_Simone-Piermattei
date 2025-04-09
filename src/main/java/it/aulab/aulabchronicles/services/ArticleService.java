package it.aulab.aulabchronicles.services;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import it.aulab.aulabchronicles.dtos.ArticleDto;
import it.aulab.aulabchronicles.models.Article;
import it.aulab.aulabchronicles.models.Category;
import it.aulab.aulabchronicles.models.Image;
import it.aulab.aulabchronicles.models.User;
import it.aulab.aulabchronicles.repositories.ArticleRepository;
import it.aulab.aulabchronicles.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class ArticleService implements CrudService<ArticleDto, Article, Long> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ImageService imageService;

    @Override
    public List<ArticleDto> readAll() {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findAll()) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    @Override
    public ArticleDto read(Long key) {
        Optional<Article> optArticle = articleRepository.findById(key);
        if (optArticle.isPresent()) {
            return modelMapper.map(optArticle.get(), ArticleDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author id=" + key + " not found");
        }

    }

    @Override
    public ArticleDto create(Article article, Principal principal, MultipartFile file) {
        String url = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = (userRepository.findById(userDetails.getId())).get();
            article.setUser(user);
        }

        if (!file.isEmpty()) {
            try {
                CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                url = futureUrl.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        article.setIsAccepted(null);

        ArticleDto dto = modelMapper.map(articleRepository.save(article), ArticleDto.class);
        if (!file.isEmpty()) {
            imageService.saveImageOnDB(url, article);
        }
        return dto;

    }

    @Override
    @Transactional // È buona pratica rendere @Transactional i metodi che modificano dati
    public ArticleDto update(Long key, Article updatedArticleData, MultipartFile newFile) {
        String newImageUrl = null; // URL della nuova immagine, se caricata

        // 1. Recupera l'articolo esistente dal database
        Article existingArticle = articleRepository.findById(key)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article id=" + key + " not found"));

        // 2. Preserva dati immutabili (ID e Utente)
        // Copia i dati dal DTO/form sull'entità esistente per l'aggiornamento
        existingArticle.setTitle(updatedArticleData.getTitle());
        existingArticle.setSubtitle(updatedArticleData.getSubtitle());
        existingArticle.setBody(updatedArticleData.getBody());
        existingArticle.setCategory(updatedArticleData.getCategory());
        // Non impostare le immagini qui, verranno gestite separatamente
        // Non impostare isAccepted qui, verrà calcolato dopo

        boolean imageAdded = false;
        boolean contentChanged = false; // Potremmo non aver bisogno di questo flag se confrontiamo direttamente

        // 3. Gestione Aggiunta Nuova Immagine
        if (newFile != null && !newFile.isEmpty()) {
            imageAdded = true; // Segna che una nuova immagine è stata aggiunta

            // Carica la nuova immagine sul cloud
            try {
                CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(newFile);
                newImageUrl = futureUrl.get(); // Ottieni l'URL dall'operazione asincrona
            } catch (Exception e) {
                // Gestisci l'errore di caricamento
                e.printStackTrace(); // Logga l'errore
                // Potresti voler lanciare un'eccezione specifica o restituire un errore
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Caricamento nuova immagine fallito", e);
            }
        }
        // Nota: Non eliminiamo immagini esistenti qui. Stiamo solo *aggiungendo*.

        // 4. Rilevamento Modifiche Contenuto (Opzionale ma utile per isAccepted)
        // Confronta i campi rilevanti tra i dati ricevuti e quelli esistenti *prima*
        // dell'aggiornamento
        // Questo confronto è più complesso ora che modifichiamo `existingArticle`
        // direttamente.
        // Un approccio alternativo è confrontare `updatedArticleData` con una copia
        // dell'originale,
        // oppure semplicemente assumere che se si chiama update, qualcosa potrebbe
        // essere cambiato.
        // Per semplicità, consideriamo che l'aggiunta di un'immagine o la chiamata a
        // update
        // con dati potenzialmente diversi richieda revisione.

        // 5. Imposta Stato Accettazione
        // Se una nuova immagine è stata aggiunta OPPURE se assumiamo che i dati
        // potrebbero
        // essere cambiati (dato che è un update), l'articolo necessita di revisione.
        // Una logica più precisa confronterebbe i campi prima/dopo.
        if (imageAdded /* || contentChanged */ ) { // Se vuoi essere più preciso, implementa contentChanged
            existingArticle.setIsAccepted(null);
        }
        // Altrimenti, lo stato isAccepted rimane quello che era in existingArticle.

        // 6. Salva Articolo Aggiornato (con i campi di testo/categoria aggiornati)
        // Salva l'articolo *prima* di salvare il record della *nuova* immagine nel DB,
        // così l'articolo ha un ID aggiornato e stato per la relazione.
        Article savedArticle = articleRepository.save(existingArticle);

        // 7. Salva Record DB Nuova Immagine (se applicabile)
        if (newImageUrl != null) {
            // Passa l'entità Article *salvata* per stabilire correttamente la relazione
            // ImageServiceImpl.saveImageOnDB creerà un nuovo record Image associato
            imageService.saveImageOnDB(newImageUrl, savedArticle);

            // Ricarica l'articolo per assicurarsi che la lista `images` sia aggiornata
            // nel DTO restituito. Questo passaggio è cruciale.
            savedArticle = articleRepository.findById(savedArticle.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Articolo scomparso dopo salvataggio immagine")); // Gestione errore robusta
        }

        // 8. Restituisci DTO dell'articolo aggiornato (che ora include la nuova
        // immagine nella lista)
        return modelMapper.map(savedArticle, ArticleDto.class);
    }

    @Override
    @Transactional // Anche la delete dovrebbe essere transazionale
    public void delete(Long key) {
        Article article = articleRepository.findById(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Article id=" + key + " not found for deletion"));

        // Prima di eliminare l'articolo, elimina le immagini associate (cloud + DB)
        if (article.getImages() != null && !article.getImages().isEmpty()) {
            // Crea una copia della lista per evitare ConcurrentModificationException se
            // deleteImage modifica la lista originale tramite cascade/JPA.
            List<Image> imagesToDelete = new ArrayList<>(article.getImages());
            for (Image image : imagesToDelete) {
                try {
                    // Assumiamo che deleteImage gestisca sia cloud che DB
                    imageService.deleteImage(image.getPath());
                } catch (IOException e) {
                    // Logga l'errore ma continua a provare a eliminare l'articolo
                    System.err.println("Errore durante l'eliminazione dell'immagine " + image.getPath()
                            + " per l'articolo " + key + ": " + e.getMessage());
                    // Potresti voler accumulare errori o avere una strategia diversa
                }
            }
        }

        // Ora elimina l'articolo stesso
        // Le relazioni (es. con Image) dovrebbero essere gestite da JPA (es. cascade)
        // o essere state pulite manualmente (come fatto sopra per le immagini).
        articleRepository.delete(article);
    }

    // ... (metodi searchByCategory, searchByAuthor, setIsAccepted, search rimangono
    // invariati) ...

    // --- Metodo Ausiliario (Opzionale) per Confronto Contenuto ---
    // Potresti creare un metodo privato per confrontare i campi rilevanti
    // tra due oggetti Article per determinare `contentChanged` in modo più pulito.
    private boolean hasContentChanged(Article original, Article updatedData) {
        if (!original.getTitle().equals(updatedData.getTitle()))
            return true;
        if (!original.getSubtitle().equals(updatedData.getSubtitle()))
            return true;
        if (!original.getBody().equals(updatedData.getBody()))
            return true;

        // Confronto Categoria (gestendo i null)
        Category originalCat = original.getCategory();
        Category updatedCat = updatedData.getCategory();
        if (originalCat == null && updatedCat != null)
            return true;
        if (originalCat != null && updatedCat == null)
            return true;
        if (originalCat != null && updatedCat != null && !originalCat.getId().equals(updatedCat.getId()))
            return true; // Confronta ID se non null

        // Aggiungi altri campi se necessario (es. publishDate se modificabile)

        return false;
    }

    public List<ArticleDto> searchByCategory(Category category) {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findByCategory(category)) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    public List<ArticleDto> searchByAuthor(User user) {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findByUser(user)) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    public void setIsAccepted(Boolean result, Long id) {
        Article article = articleRepository.findById(id).get();
        article.setIsAccepted(result);
        articleRepository.save(article);
    }

    public List<ArticleDto> search(String keyword) {
        List<Article> articles;
        if (keyword == null || keyword.trim().isEmpty()) {
            articles = articleRepository.searchAllAccepted();
        } else {
            articles = articleRepository.search(keyword);
        }
        return articles.stream()
                .map(article -> modelMapper.map(article, ArticleDto.class))
                .collect(Collectors.toList());
    }
}
