package ru.stm.marvelcomics.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.stm.marvelcomics.domain.Comics;

/**
 * ComicsDTO определяет краткое содержание Comics
 */
@Setter
@Getter
@Builder
public class ComicsDTO {
    private Long id;

    private String title;

    private String cover;

    public static ComicsDTO preview (Comics comics){
        return ComicsDTO.builder()
                .id(comics.getId())
                .title(comics.getTitle())
                .cover(comics.getCover())
                .build();
    }
}
