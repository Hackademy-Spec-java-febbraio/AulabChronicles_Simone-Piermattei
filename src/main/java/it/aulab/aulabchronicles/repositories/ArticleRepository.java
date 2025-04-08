package it.aulab.aulabchronicles.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import it.aulab.aulabchronicles.models.Article;
import it.aulab.aulabchronicles.models.Category;
import it.aulab.aulabchronicles.models.User;

public interface ArticleRepository extends ListCrudRepository<Article, Long> {
    List<Article> findByCategory(Category category);

    List<Article> findByUser(User user);

    List<Article> findByIsAcceptedTrue();

    List<Article> findByIsAcceptedFalse();

    List<Article> findByIsAcceptedIsNull();

    @Query("SELECT a from Article a WHERE a.isAccepted = true AND (" +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.subtitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.user.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Article> search(@Param("searchTerm") String searchTerm);

    @Query("SELECT a FROM Article a WHERE a.isAccepted = true")
    List<Article> searchAllAccepted();

}
