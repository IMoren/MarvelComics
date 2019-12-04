package ru.stm.marvelcomics.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.stm.marvelcomics.domain.dto.CharacterDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <h2>Comic содержит данные о комиксе</h2>
 *
 * @see Char#Char()
 * @see ComicsPage#ComicsPage()
 */
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

    /**
     * Конструктор класса со всеми параметрами
     *
     * @param id         id комикса
     * @param title      название комикса
     * @param release    дата публикации
     * @param cover      имя файла-изображения. Изображение обложки комикса
     * @param pages      коллекция {@link ComicsPage#ComicsPage()} - страницы комикса
     * @param characters список персонажей {@link Char#Char()}, задействованных в комиксе
     */
    public Comics(Long id, String title, String release, String cover, Collection<ComicsPage> pages, Collection<Char> characters) {
        this.id = id;
        this.title = title;
        this.release = release;
        this.cover = cover;
        this.pages = pages;
        this.characters = characters;
    }

    public Comics() {
    }

    /**
     * Добавление персонажа, задействованного в комиксе
     *
     * @param character объект {@link Char#Char()}
     */
    public void addCharacter(Char character) {
        characters.add(character);
    }

    /**
     * Удаление персонажа, задействованного в комиксе
     *
     * @param character объект {@link Char#Char()}
     */
    public void removeCharacter(Char character) {
        characters.remove(character);
    }

    /**
     * Добавление страницы комикса
     *
     * @param page объект {@link ComicsPage#ComicsPage()}
     */
    public void addPage(ComicsPage page) {
        pages.add(page);
        page.setComics(this);
    }

    /**
     * Удаление страницы комикса
     *
     * @param page {@link ComicsPage#ComicsPage()}
     */
    public void removePage(ComicsPage page) {
        pages.remove(page);
        page.setComics(null);
    }

    /**
     * Возвращает уникальный идентификатор комикса
     *
     * @return id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Возвращает название комикса
     *
     * @return title
     */

    public String getTitle() {
        return this.title;
    }

    /**
     * Возвращает дату публикации
     *
     * @return release
     */
    public String getRelease() {
        return this.release;
    }

    /**
     * Возвращает путь к файлу. Изображение обложки комикса
     *
     * @return cover
     */
    public String getCover() {
        return this.cover;
    }

    /**
     * Возвращает коллекцию {@link ComicsPage#ComicsPage()} - страниц комикса</br>
     * Это поле <b>не</b> входит в json-объект "comics"
     *
     * @return pages
     */
    public Collection<ComicsPage> getPages() {
        return this.pages;
    }

    /**
     * Возвращает коллекцию {@link Char#Char()} - персонажей, задействованных в комиксе</br>
     * Это поле <b>не</b> входит в json-объект "comics"
     *
     * @return characters
     */
    public Collection<Char> getCharacters() {
        return this.characters;
    }

    /**
     * Устанавливает уникальный идентификатор комикса
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Устанавливает название комикса
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Устанавливает дату публикации
     *
     * @param release
     */
    public void setRelease(String release) {
        this.release = release;
    }

    /**
     * Устанавливает название файла-изображения, сформированное в методе {@link ru.stm.marvelcomics.service.FileService#getFilename()}</br>
     * Изображение обложки комикса
     *
     * @param cover
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * Возвращает коллекцию {@link ComicsPage#ComicsPage()} - страниц комикса
     *
     * @param pages
     */
    public void setPages(Collection<ComicsPage> pages) {
        this.pages = pages;
    }

    /**
     * Устанавливает коллекцию {@link Char#Char()} - персонажей, задействованных в комиксе
     *
     * @param characters
     */
    public void setCharacters(Collection<Char> characters) {
        this.characters = characters;
    }
}
