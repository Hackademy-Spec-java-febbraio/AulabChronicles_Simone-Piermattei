package it.aulab.aulabchronicles.controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import it.aulab.aulabchronicles.dtos.ArticleDto;
import it.aulab.aulabchronicles.dtos.UserDto;
import it.aulab.aulabchronicles.models.User;
import it.aulab.aulabchronicles.services.ArticleService;
import it.aulab.aulabchronicles.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;
    

    
    // * Rotta di home
    @GetMapping("/")
    public String home(Model viewModel) {

        List<ArticleDto> articles = articleService.readAll();
        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());
        
        List<ArticleDto> lastThreeArticles = articles.stream().limit(3).collect(Collectors.toList());
        
        viewModel.addAttribute("articles", lastThreeArticles);
        
        return "home";
    }


    
    // * Rotta per la Registrazione
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }
    
    // * Rotta per il Login
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    
    
    // * Rotta per il salvataggio della registrazione
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes,
    HttpServletRequest request, HttpServletResponse response) {
        
        User existingUser = userService.findUserByEmail(userDto.getEmail());
        
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
            "There is already an account registered with the same email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";
        }
        userService.saveUser(userDto, redirectAttributes, request, response);
        
        redirectAttributes.addFlashAttribute("successMessage", "Registrazione avvenuta con successo!");
        return "redirect:/register?success";
        
    }


    // * Rotta per la ricerca degli articoli in base all'utente

    @GetMapping("/search/{id}")
    public String userArticleSearch(@PathVariable("id")Long id, Model viewModel) {
        User user = userService.find(id);
        viewModel.addAttribute("title", "Tutti gli articoli trovati per utente" + user.getUsername());
        
        List<ArticleDto> articles = articleService.searchByAuthor(user);
        viewModel.addAttribute("articles", articles);
        return "articles/articles";
    }
    
    
}


