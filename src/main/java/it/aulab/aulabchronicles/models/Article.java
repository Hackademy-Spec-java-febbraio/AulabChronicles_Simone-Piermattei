package it.aulab.aulabchronicles.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotEmpty
    @Size(max = 100)
    private String title;

    @Column(nullable = false, length = 100)
    @NotEmpty
    @Size(max = 100)
    private String subtitle;

    @Column(nullable = false)
    @NotEmpty
    @Size(max = 1000)
    private String body;

    @Column(name = "publish_date", nullable = false)
    @NotNull
    private LocalDate publishDate;

    @Column(name = "is_accepted", nullable = true)
    private Boolean isAccepted;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "articles" })
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({ "articles" })
    private Category category;

    @OneToMany(mappedBy = "article")
    @JsonIgnoreProperties({ "article" })
    private List<Image> images = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Article article = (Article) o;
        // Confronta i campi rilevanti, escludendo la lista 'images' per semplicità
        // e assicurati che user e category non siano null prima di chiamare metodi su
        // di essi
        return Objects.equals(id, article.id) && // È buona norma includere l'ID se non nullo
                Objects.equals(title, article.title) &&
                Objects.equals(subtitle, article.subtitle) &&
                Objects.equals(body, article.body) &&
                Objects.equals(publishDate, article.publishDate) &&
                Objects.equals(user, article.user) && // Assumendo che User abbia un equals corretto
                Objects.equals(category, article.category); // Assumendo che Category abbia un equals corretto
        // Rimosso il confronto di images.getPath()
    }

    @Override
    public int hashCode() {
        // Genera hashCode basato sugli stessi campi usati in equals
        return Objects.hash(id, title, subtitle, body, publishDate, user, category);
        // Rimosso images dal calcolo hash
    }
}
