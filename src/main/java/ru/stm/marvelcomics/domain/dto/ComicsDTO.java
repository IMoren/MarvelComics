package ru.stm.marvelcomics.domain.dto;

import lombok.Builder;
import lombok.Setter;
import ru.stm.marvelcomics.domain.Comics;

/**
 * <h2>ComicsDTO определяет краткое содержание  {@link Comics#Comics()}</h2>
 *
 * @see Comics#Comics()
 */
@Setter
@Builder
public class ComicsDTO {

    private Long id;

    private String title;

    private String cover;

    /**
     * Формирует краткое содержание из полей класса {@link Comics#Comics()}
     *
     * @param comics
     * @return ComicsDTO
     */
    public static ComicsDTO preview(Comics comics) {
        return ComicsDTO.builder()
                .id(comics.getId())
                .title(comics.getTitle())
                .cover(comics.getCover())
                .build();
    }

    /**
     * Возвращает уникальный идентифокатор комикса
     * @return id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Возвращает название комикса
     * @return title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Возвращает путь к файлу изображению обложки комикса
     * @return cover
     */
    public String getCover() {
        return this.cover;
    }
}
