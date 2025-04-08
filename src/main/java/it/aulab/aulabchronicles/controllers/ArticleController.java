package it.aulab.aulabchronicles.controllers;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.aulab.aulabchronicles.dtos.ArticleDto;
import it.aulab.aulabchronicles.dtos.CategoryDto;
import it.aulab.aulabchronicles.models.Article;
import it.aulab.aulabchronicles.models.Category;
import it.aulab.aulabchronicles.repositories.ArticleRepository;
import it.aulab.aulabchronicles.services.ArticleService;
import it.aulab.aulabchronicles.services.CrudService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDto, Category, Long> categoryService;
    @Autowired
    private ArticleService articleService;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    private ModelMapper modelMapper;

    // * Rotta index degli articoli
    @GetMapping
    public String articlesIndex(Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli");

        // Recupera solo gli articoli accettati dal repository
        List<Article> acceptedArticles = articleRepository.findByIsAcceptedTrue();

        // Converti Article in ArticleDto
        List<ArticleDto> articles = acceptedArticles.stream()
                .map(article -> modelMapper.map(article, ArticleDto.class))
                .collect(Collectors.toList());

        // Ordina gli articoli accettati per data di pubblicazione (discendente)
        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());

        viewModel.addAttribute("articles", articles);
        return "articles/articles";
    }

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

    // * Rotta per il dettaglio articolo
    @GetMapping("detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Dettaglio Articolo");
        viewModel.addAttribute("article", articleService.read(id));
        return "articles/detail";
    }

    // * Rotta dettaglio di un articolo per il revisore

    @GetMapping("revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";
    }

    // * Rotta dedicata all'azione del revisore

    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId,
            RedirectAttributes redirectAttributes) {
        if (action.equals("accept")) {
            articleService.setIsAccepted(true, articleId);
            redirectAttributes.addFlashAttribute("successMessage", "Articolo accettato con successo!");

        } else if (action.equals("reject")) {
            articleService.setIsAccepted(false, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo rifiutato con successo!");

        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Azione non valida!");

        }
        return "redirect:/revisor/dashboard";
    }

    // * Rotta per la ricerca di un articolo
    
    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli trovati");

        List<ArticleDto> articles = articleService.search(keyword);
        //List<ArticleDto> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList()); //Removed

        viewModel.addAttribute("articles", articles); //Use articles directly

        return "articles/articles";
    }

}
