package ru.stm.marvelcomics.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.springframework.lang.NonNull;
import ru.stm.marvelcomics.util.Const;

import javax.persistence.*;

/**
 * <h2>ComicsPage содержит данные о странице комикса</h2>
 *
 * @see Comics#Comics()
 */
@Builder
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

    /**
     * ComicsPage конструктор со всеми параметрами
     *
     * @param order    произвольное число, определяющее порядок следования страниц
     * @param pathFile ссылка на файл-изображение
     * @param comics   объект {@link Comics#Comics()}, которому принадлежит эта страница
     */
    public ComicsPage(Long order, @NonNull String pathFile, Comics comics) {
        this.order = order;
        this.pathFile = pathFile;
        this.comics = comics;
    }

    public ComicsPage() {
    }

    /**
     * Возвращает путь к файлу-изображению в виде строки
     *
     * @return pathFile
     */
    public String getPathFile() {
        return String.format("%s/%d/%s", Const.COMICS_DIR, comics.getId(), pathFile);
    }

    /**
     * Возвращает число, используется для упорядочивания страниц
     *
     * @return order
     */
    public Long getOrder() {
        return this.order;
    }

    /**
     * Возвращает объект {@link Comics#Comics()}, которому принадлежит эта страница
     *
     * @return {@link Comics#Comics()}
     */

    public Comics getComics() {
        return this.comics;
    }

    /**
     * Устанавливает число на основе которого строится порядок страниц
     *
     * @param order число
     */
    public void setOrder(Long order) {
        this.order = order;
    }

    /**
     * Устанавливает название файла-изображения сформированное в методе {@link ru.stm.marvelcomics.service.FileService#getFilename()}
     *
     * @param pathFile название файла
     */
    public void setPathFile(@NonNull String pathFile) {
        this.pathFile = pathFile;
    }

    /**
     * Устанавливает объект {@link Comics#Comics()}, которому принадлежит эта страница
     *
     * @param comics {@link Comics#Comics()}
     */

    public void setComics(Comics comics) {
        this.comics = comics;
    }
}
