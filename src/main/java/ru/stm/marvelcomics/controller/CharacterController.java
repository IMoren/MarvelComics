package ru.stm.marvelcomics.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.stm.marvelcomics.domain.Char;
import ru.stm.marvelcomics.service.CharacterService;
import ru.stm.marvelcomics.util.Const;
import ru.stm.marvelcomics.util.Validation;

/**
 * Обработка rest запросов на адрес ... /v1/public/character
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/public/character")
public class CharacterController {
    private final CharacterService characterService;
    private ObjectMapper jsonParser = new ObjectMapper();

    /**
     * Просмотр списка всех персонажей в лексографическом порядке
     *
     * @param limit  Не обязательный.
     * @param offset Не обязательный.
     * @return json список персонажей (имя и изображение)
     */
    @GetMapping
    public Flux<Object> get(
            @RequestParam(required = false, defaultValue = Const.LIMIT) Integer limit,
            @RequestParam(required = false, defaultValue = Const.OFFSET) Integer offset) {
        return characterService.get("name", Validation.LimitIsValid(limit), Validation.OffsetIsValid(offset));
    }

    /**
     * Просмотр полной информации персонажа
     *
     * @param id id персонажа
     * @return json профиль персонажа со всеми полями
     */

    @GetMapping(value = "/{id}")
    public Mono<Object> getCharacter(
            @PathVariable long id) {
        return characterService.getById(id);
    }

    /**
     * Просмотр списка комиксов, в которых задействован персонаж
     *
     * @param id - id персонажа
     * @return json список комиксов (название и обложка)
     */
    @GetMapping(value = "/{id}/comics")
    public Flux<Object> getComics(
            @PathVariable long id) {
        return characterService.getComics(id);
    }

    /**
     * Создать нового персонажа
     *
     * @param jsonCharacter json-объект "character" с обязательным полем "name"
     * @param img           Не обязательный. Файл-изображение
     * @return json профиль нового персонажа со всеми полями
     */

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Object> addCharacter(
            @RequestPart("character") String jsonCharacter,
            @RequestPart(name = "img", required = false) Mono<FilePart> img) {

        Char character;
        try {
            character = jsonParser.readValue(jsonCharacter, Char.class);
        } catch (JsonProcessingException e) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Недопустимый синтаксис или незаполнены обязательные поля: character {\"name\":\"name\"}"));
        }
        return characterService.add(character, img);
    }

    /**
     * Изменениие данных существующего профиля персонажа
     *
     * @param character json-объект "character" с обязательными полями "id", "name".
     * @param img       Не обязательный. Файл-изображение. Заменяет старое изображение на новое
     * @return json профиль нового персонажа со всеми полями с обновленными значениями
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Object> changeCharacter(
            @RequestPart("character") Char character,
            @RequestPart(name = "img", required = false) Mono<FilePart> img) {
        return characterService.update(character, img);
    }

    /**
     * Добавление комикса в список комиксов, в которых задействован персонаж
     *
     * @param id       id персонажа
     * @param comicsId id комикса
     * @return void
     */
    @PostMapping("/{id}/comics")
    public Mono<Object> addComics(
            @PathVariable("id") Long id,
            @RequestParam("comics_id") Long comicsId) {
        return characterService.addComics(id, comicsId);
    }

    /**
     * Удаление профиля персонажа
     *
     * @param id id персонажа
     */
    @DeleteMapping("/{id}")
    public Mono<Void> deleteCharacter(@PathVariable("id") long id) {
        return characterService.delete(id);
    }

    @DeleteMapping("/{id}/comics")
    public Mono<Void> deleteComics(
            @PathVariable("id") Long id,
            @RequestParam("comics_id") Long comicsId) {
        return characterService.deleteComics(id, comicsId);
    }

}
