package it.aulab.aulabchronicles.repositories;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import it.aulab.aulabchronicles.models.Article;
import it.aulab.aulabchronicles.models.Category;
import it.aulab.aulabchronicles.models.User;

public interface ArticleRepository extends ListCrudRepository<Article, Long> {
    List<Article> findByCategory(Category category);
    List<Article> findByUser(User user);
    
}
