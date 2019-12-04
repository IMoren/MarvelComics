package ru.stm.marvelcomics.domain.dto;

import lombok.Builder;
import lombok.Setter;
import ru.stm.marvelcomics.domain.Char;

/**
 * <h2>CharacterDTO определяет краткое содержание {@link Char#Char()}</h2>
 *
 * @see Char#Char()
 */
@Setter
@Builder
public class CharacterDTO {

    private long id;

    private String name;

    private String portrait;

    /**
     * Формирует краткое содержание из полей класса {@link Char#Char()}
     *
     * @param character
     * @return CharacterDTO
     */
    public static CharacterDTO preview(Char character) {
        return CharacterDTO.builder()
                .id(character.getId())
                .name(character.getName())
                .portrait(character.getPortrait())
                .build();
    }

    /**
     * Возвращает уникальный идентификатор персонажа
     *
     * @return id
     */
    public long getId() {
        return this.id;
    }

    /**
     * Возвращает имя персонажа
     *
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Возвращает путь к файлу изображению персонажа
     *
     * @return portrait
     */
    public String getPortrait() {
        return this.portrait;
    }
}
