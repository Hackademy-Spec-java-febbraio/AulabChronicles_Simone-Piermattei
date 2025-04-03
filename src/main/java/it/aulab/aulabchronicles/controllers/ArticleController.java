package it.aulab.aulabchronicles.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.aulabchronicles.dtos.ArticleDto;
import it.aulab.aulabchronicles.dtos.CategoryDto;
import it.aulab.aulabchronicles.models.Article;
import it.aulab.aulabchronicles.models.Category;
import it.aulab.aulabchronicles.services.ArticleService;
import it.aulab.aulabchronicles.services.CategoryService;
import it.aulab.aulabchronicles.services.CrudService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDto, Category, Long> categoryService;
    @Autowired
    private ArticleService articleService;

    // * Rotta per la creazione di un articolo
    @GetMapping("create")
    public String articleCreate(Model viewModel) {
        viewModel.addAttribute("title", "Crea un articolo");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "articles/create";
    }

    // * Rotta per lo store di un articolo

    @PostMapping("/save")
    public String articleStore(@Valid @ModelAttribute("article") Article article,
            BindingResult result,
            Model viewModel,
            RedirectAttributes redirectAttributes,
            Principal principal,
            MultipartFile file) {

        // Controllo degli errori con validazioni
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Crea un articolo");
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "articles/create";
        }

        articleService.create(article, principal, file);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo creato con successo!");
        return "redirect:/";
    }

}
