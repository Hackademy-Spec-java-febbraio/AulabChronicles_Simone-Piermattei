package it.aulab.aulabchronicles.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import it.aulab.aulabchronicles.dtos.ArticleDto;
import it.aulab.aulabchronicles.dtos.CategoryDto;
import it.aulab.aulabchronicles.models.Category;
import it.aulab.aulabchronicles.services.ArticleService;
import it.aulab.aulabchronicles.services.CategoryService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelMapper modelMapper;

    // * Rotta per la ricerca dell'articolo in base alla categoria

    @GetMapping("/search/{id}")
    public String categorySearch(@PathVariable("id") Long id, Model viewModel) {
        CategoryDto category = categoryService.read(id);

        viewModel.addAttribute("title", "Tutti gli articoli trovati per categoria" + category.getName());

        List<ArticleDto> articles = articleService.searchByCategory(modelMapper.map(category, Category.class));
        viewModel.addAttribute("articles", articles);
        return "articles/articles";
    }

    // * Rotta per la creazione di una categoria

    @GetMapping("/create")
    public String categoryCreate(Model viewModel) {
        viewModel.addAttribute("title", "Crea una categoria");
        viewModel.addAttribute("category", new Category());
        return "categories/create";
    }

    // * Rotta per la memorizzazione di una categoria.

    @PostMapping
    public String categoryStore(@Valid @ModelAttribute("category") Category category,
            Model viewModel,
            RedirectAttributes redirectAttributes,
            BindingResult result) {

        // Controllo degli errori
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Crea una categoria");
            viewModel.addAttribute("category", category);
            return "categories/create";
        }
        categoryService.create(category, null, null);
        redirectAttributes.addFlashAttribute("succesMessage", "Categoria creata con successo!");
        return "redirect:/admin/dashboard";

    }

}
