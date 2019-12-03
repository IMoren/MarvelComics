package ru.stm.marvelcomics.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.stm.marvelcomics.domain.dto.CharacterDTO;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Comic содержит данные о комиксе
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comics")
public class Comics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Название
     */
    @OrderBy
    private String title;

    /**
     * Дата публикации
     */
    @OrderBy
    private String release;

    /**
     * Путь к файлу изображения. Обложжка комикса.
     */
    private String cover;

    @JsonIgnore
    @OneToMany(targetEntity = ComicsPage.class, mappedBy = "comics", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<ComicsPage> pages = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(targetEntity = Char.class)
    @JoinTable(name = "comics_has_character",
            joinColumns = @JoinColumn(name = "comics_id"),
            inverseJoinColumns = @JoinColumn(name = "character_id"))
    private Collection<Char> characters = new ArrayList<>();


    public void addCharacter(Char character) {
        characters.add(character);
    }

    public void removeCharacter(Char character){
        characters.remove(character);
    }

    public void addPage(ComicsPage page) {
        pages.add(page);
        page.setComics(this);
    }

    public void removePage(ComicsPage page) {
        pages.remove(page);
        page.setComics(null);
    }

    public Collection<CharacterDTO> getCharacter() {
        return characters.stream()
                .map(CharacterDTO::preview)
                .collect(Collectors.toList());
    }
}
