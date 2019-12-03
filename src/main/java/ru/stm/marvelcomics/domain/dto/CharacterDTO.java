package ru.stm.marvelcomics.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.stm.marvelcomics.domain.Char;

/**
 * CharacterDTO определяет краткое содержание Character
 */
@Setter
@Getter
@Builder
public class CharacterDTO {

    private long id;

    private String name;

    private String portrait;

    public static CharacterDTO preview(Char character) {
        return CharacterDTO.builder()
                .id(character.getId())
                .name(character.getName())
                .portrait(character.getPortrait())
                .build();
    }
}
