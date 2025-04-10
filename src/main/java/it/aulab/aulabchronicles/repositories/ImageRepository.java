package it.aulab.aulabchronicles.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import it.aulab.aulabchronicles.models.Image;


public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Elimina le immagini dal database basandosi sul loro path (URL).
     * Utilizza una query nativa per l'eliminazione diretta.
     *
     * @param path L'URL (path) dell'immagine da eliminare.
     * @return Il numero di record immagine eliminati dal database.
     */
    @Modifying // Necessario per query di modifica (INSERT, UPDATE, DELETE)
    @Query(value = "DELETE FROM images WHERE path = :path", nativeQuery = true)
    int deleteByPath(@Param("path") String path); // Modificato per restituire long
}
