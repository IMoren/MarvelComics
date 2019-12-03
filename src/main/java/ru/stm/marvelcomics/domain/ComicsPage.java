package ru.stm.marvelcomics.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.stm.marvelcomics.util.Const;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * ComicsPage содержит данные о странице комикса
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comics_page")
public class ComicsPage {

    /**
     * Номер страницы комикса
     */

    @OrderBy
    @Column(name = "order_page")
    private Long order;

    @Id
    @NonNull
    /**
     * Путь к файлу изображения. Страница комикса.
     */
    private String pathFile;

    /**
     * Комикс, который содержит эту страницу
     */

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Comics comics;

    public String getPathFile() {
        return String.format("%s/%d/%s", Const.COMICS_DIR, comics.getId(), pathFile);
    }
}
