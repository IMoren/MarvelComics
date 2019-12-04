package ru.stm.marvelcomics.domain.dto;

import lombok.Builder;
import lombok.Setter;
import ru.stm.marvelcomics.domain.Comics;
import ru.stm.marvelcomics.domain.ComicsPage;

/**
 * <h2>PageDTO определает содержание страницы комикса</h2>
 *
 * @see Comics#Comics()
 * @see ComicsPage#ComicsPage()
 */
@Setter
@Builder
public class PageDTO {

    long id;

    String title;

    String image;

    /**
     * Формирует содержание страницы из полей классов {@link Comics#Comics()} и {@link ComicsPage#ComicsPage()}
     *
     * @param comics
     * @param page
     * @return PageDTO
     */
    public static PageDTO view(Comics comics, ComicsPage page) {
        return PageDTO.builder()
                .id(comics.getId())
                .title(comics.getTitle())
                .image(page.getPathFile())
                .build();
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getImage() {
        return this.image;
    }
}
