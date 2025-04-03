package it.aulab.aulabchronicles.repositories;

import org.springframework.data.repository.ListCrudRepository;

import it.aulab.aulabchronicles.models.Category;

public interface CategoryRepository extends ListCrudRepository<Category, Long> {
    
}
