package it.aulab.aulabchronicles.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import it.aulab.aulabchronicles.models.Image;


public interface ImageRepository extends JpaRepository<Image, Long> {

    @Modifying
    @Query(value = "DELETE FROM images WHERE path = :path", nativeQuery = true)
    void deleteByPath(@Param("path") String path);
}
