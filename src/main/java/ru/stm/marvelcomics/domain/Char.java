package ru.stm.marvelcomics.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.stm.marvelcomics.util.Const;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ComicsCharacter содержит данные о персонаже комикса
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "characters")
public class Char {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Имя персонажа
     */
    @OrderBy
    @NonNull
    private String name;

    /**
     * Дата первого упоминания о персонаже
     */
    private String createDate;

    /**
     * Путь к файлу изображения. Портрет персонажа
     */
    private String portrait;

    /**
     * Краткое описание персонажа
     */
    private String description;

    /**
     * Биография
     */
    private String biography;

    @JsonIgnore
    @ManyToMany(targetEntity = Comics.class, fetch = FetchType.EAGER, mappedBy = "characters")
    private Collection<Comics> comicsList = new ArrayList<>();


    public void addComics (Comics comics){
        comicsList.add(comics);
    }

    public void removeComics(Comics comics) {
        comicsList.remove(comics);
    }

    public String getPortrait(){
        return String.format("%s/%d/%s", Const.CHARACTER_DIR, id, portrait);
    }

}
