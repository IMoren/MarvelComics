package ru.stm.marvelcomics.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.NonNull;
import ru.stm.marvelcomics.util.Const;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <h2>Char (character) содержит данные о персонаже комикса</h2>
 *
 * @see Comics#Comics()
 */


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

    /**
     * Конструктор со всеми параметрами
     *
     * @param id          уникальный идентификатор персонажа
     * @param name        имя персонажа
     * @param createDate  дата первого упоминания о персонаже
     * @param portrait    имя фаила  изображения персонажа
     * @param description краткое описание возможностей и характера персонажа
     * @param biography   биография
     * @param comicsList  коллекция {@link Comics#Comics()} в которых персонаж задействован
     */
    public Char(Long id, @NonNull String name, String createDate, String portrait, String description, String biography, Collection<Comics> comicsList) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
        this.portrait = portrait;
        this.description = description;
        this.biography = biography;
        this.comicsList = comicsList;
    }

    public Char() {
    }

    /**
     * Добавляет {@link Comics#Comics()} в список комиксов
     * в которых задействован персонаж
     *
     * @param comics
     */
    public void addComics(Comics comics) {
        comicsList.add(comics);
    }

    /**
     * Удаляет {@link Comics#Comics()} из списка комиксов, в которых задействован персонаж
     *
     * @param comics
     */
    public void removeComics(Comics comics) {
        comicsList.remove(comics);
    }

    /**
     * Возвращает путь к файлу изображению персонажа
     *
     * @return portrait
     */
    public String getPortrait() {
        return String.format("%s/%d/%s", Const.CHARACTER_DIR, id, portrait);
    }

    /**
     * Возвращает уникальный идентификатор персонажа
     *
     * @return id
     */

    public Long getId() {
        return this.id;
    }

    /**
     * Возвращает имя персонажа
     *
     * @return name
     */
    @NonNull
    public String getName() {
        return this.name;
    }

    /**
     * Возвращает дату первого упоминания о персонаже
     *
     * @return createDate
     */
    public String getCreateDate() {
        return this.createDate;
    }

    /**
     * Возвращает краткое описание возможностей и характера персонажа
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Возвращает текст с биографией персоажа
     *
     * @return biography
     */
    public String getBiography() {
        return this.biography;
    }

    /**
     * Возвращает коллекцию {@link Comics#Comics()}, в которых задействован персонаж</br>
     * Это поле <b>не</b> входит в json-объект "character"
     *
     * @return comicsList
     */
    public Collection<Comics> getComicsList() {
        return this.comicsList;
    }

    /**
     * Устанавливает уникальный идентификатор персонажа
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Устанавливает имя персонажа
     *
     * @param name
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }

    /**
     * Устанавливает дату первого упоминания о персонаже
     *
     * @param createDate
     */
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**
     * Устанавливает имя файла изображения персонажа, сформированное в методе {@link ru.stm.marvelcomics.service.FileService#getFilename()}
     *
     * @param portrait
     */
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    /**
     * Устанавливает краткое описание возможностей и характера персонажа
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Устанавливает текст с биографией персоажа
     *
     * @param biography
     */
    public void setBiography(String biography) {
        this.biography = biography;
    }

    /**
     * Устанавливает коллекцию {@link Comics#Comics()}, в которых задействован персонаж
     *
     * @param comicsList
     */
    public void setComicsList(Collection<Comics> comicsList) {
        this.comicsList = comicsList;
    }
}
