package it.aulab.aulabchronicles.dtos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.aulab.aulabchronicles.models.Category;
import it.aulab.aulabchronicles.models.Image;
import it.aulab.aulabchronicles.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArticleDto {
    
    private Long id;
    private String title;
    private String subtitle;
    private String body;
    private LocalDate publishDate;
    private Boolean isAccepted;
    private User user;
    private Category category;
    private List<Image> images = new ArrayList<>();
}
