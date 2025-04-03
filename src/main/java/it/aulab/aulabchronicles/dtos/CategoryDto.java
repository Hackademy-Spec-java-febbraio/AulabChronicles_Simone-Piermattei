package it.aulab.aulabchronicles.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    
    private Long id;
    private String name;
    private Integer numberOfArticle;
}
