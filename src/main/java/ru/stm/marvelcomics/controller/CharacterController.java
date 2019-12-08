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
import ru.stm.marvelcomics.domain.Comics;
import ru.stm.marvelcomics.domain.dto.CharacterDTO;
import ru.stm.marvelcomics.domain.dto.ComicsDTO;
import ru.stm.marvelcomics.service.CharacterService;
import ru.stm.marvelcomics.util.Const;
import ru.stm.marvelcomics.util.Validation;

/**
 * <h2>Обработка rest запросов на адрес ... /v1/public/character</h2>
 *
 * @see Char#Char()
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
     * @param limit  Не обязательный. Ограничение количества выдаваемых результатов</b>
     *               Если не задан или задан некорректно, принимает значение по умолчанию
     * @param offset Не обязательный. Кол-во элементов, которые должны быть пропущены</b>
     *               Если не задан или задан некорректно, принимает значение по умолчанию
     * @return json список объектов {@link CharacterDTO#preview(Char)} персонажей в кратком содержании
     */
    @GetMapping
    public Flux<CharacterDTO> get(
            @RequestParam(required = false, defaultValue = Const.LIMIT) Integer limit,
            @RequestParam(required = false, defaultValue = Const.OFFSET) Integer offset) {
        return characterService.get("name", Validation.LimitIsValid(limit), Validation.OffsetIsValid(offset));
    }

    /**
     * Просмотр полной информации персонажа
     *
     * @param id id персонажа
     * @return json объект {@link Char#Char()} персонажа со всеми полями
     */

    @GetMapping(value = "/{id}")
    public Mono<Char> getCharacter(
            @PathVariable long id) {
        return characterService.getById(id);
    }

    /**
     * Просмотр списка комиксов, в которых задействован персонаж
     *
     * @param id id персонажа
     * @return json список объектов {@link ComicsDTO#preview(Comics)} ()} комиксов в кратком содержании
     */
    @GetMapping(value = "/{id}/comics")
    public Flux<ComicsDTO> getComics(
            @PathVariable long id) {
        return characterService.getComics(id);
    }

    /**
     * Создать нового персонажа
     *
     * @param jsonCharacter json-объект "character" с обязательным полем "name"
     * @param img           Не обязательный. Файл-изображение
     * @return json - объект {@link Char#Char()} нового персонажа со всеми полями
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Char> addCharacter(
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
     * Изменение данных персонаже
     * <p>
     * Предполагается, что будет удобнее редактрировать данные персонажа прямо на странице этого персонажа,</br>
     * поэтому присутствует id в адресе</br>
     * При этом не важно будет ли совпадать id в адресе с полем "id" в объекте json "сharacter"</br>
     * Изменения затронут персонажа с тем id, который указан в json</br>
     * Поля отсутвующие в json будут заменены пустыми значениями
     *
     * @param jsonCharacter json-объект "character" с обязательными полями "id", "name".
     * @param img           Не обязательный. Файл-изображение. Заменяет старое изображение на новое</br>
     * @return json объект {@link Char#Char()} персонажа со всеми полями с обновленными значениями
     * @throws HttpStatus.400, если тело запроса содержит некорректные данные
     * @throws HttpStatus.404, если персонажа с этим id не нашлось
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Char> changeCharacter(
            @RequestPart("character") String jsonCharacter,
            @RequestPart(name = "img", required = false) Mono<FilePart> img) {
        Char character;
        try {
            character = jsonParser.readValue(jsonCharacter, Char.class);
        } catch (JsonProcessingException e) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Недопустимый синтаксис или незаполнены обязательные поля: character {\"name\":\"name\"}"));
        }
        return characterService.update(character, img);
    }

    /**
     * Добавление комикса в список комиксов, в которых задействован персонаж
     *
     * @param id       id персонажа
     * @param comicsId id комикса
     * @return void
     * @throws HttpStatus.400 если данные некорректны
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

    /**
     * Удаление комикса из списка комиксов, в которых задействован персонаж
     *
     * @param id       id персонажа
     * @param comicsId id комикса
     * @return
     */
    @DeleteMapping("/{id}/comics")
    public Mono<Void> deleteComics(
            @PathVariable("id") Long id,
            @RequestParam("comics_id") Long comicsId) {
        return characterService.deleteComics(id, comicsId);
    }
}
