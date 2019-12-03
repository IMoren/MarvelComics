package ru.stm.marvelcomics.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.stm.marvelcomics.domain.Comics;
import ru.stm.marvelcomics.domain.ComicsPage;

/**
 * PageDTO определает содержание страницы комикса из Comics и ComicsPage
 */
@Getter
@Setter
@Builder
public class PageDTO {

    long id;

    String title;

    String image;

    public static PageDTO view (Comics comics, ComicsPage page){
        return PageDTO.builder()
                .id(comics.getId())
                .title(comics.getTitle())
                .image(page.getPathFile())
                .build();
    }
}
