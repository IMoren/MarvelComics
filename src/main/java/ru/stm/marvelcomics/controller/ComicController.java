package ru.stm.marvelcomics.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.stm.marvelcomics.domain.Comics;
import ru.stm.marvelcomics.service.ComicsService;
import ru.stm.marvelcomics.util.Const;
import ru.stm.marvelcomics.util.Validation;

/**
 * Обработка rest запросов на адрес ... /v1/public/comics
 */
@Log4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/public/comics")
public class ComicController {
    private final ComicsService comicsService;
    private ObjectMapper jsonParser = new ObjectMapper();

    /**
     * Просмотр списка всех комиксов в отсортированном виде
     *
     * @param sort   Не обязательный. Поумолчанию "title".
     *               Допустимые значения:
     *               "title" - по названию,
     *               "release" - по дате публикации.
     * @param limit  Не обязательный.
     * @param offset Не обязательный.
     * @return json список комиксов (название, дата публикации, изображение обложки)
     */

    @GetMapping
    public Flux<Object> getComics(
            @RequestParam(required = false, defaultValue = "title") String sort,
            @RequestParam(required = false, defaultValue = Const.LIMIT) Integer limit,
            @RequestParam(required = false, defaultValue = Const.OFFSET) Integer offset) {
        return comicsService.get(sort, Validation.LimitIsValid(limit), Validation.OffsetIsValid(offset));
    }

    /**
     * Просмотр полной информации о комиксе
     *
     * @param id    - id комикса
     * @param order Не обязательный. Номер страницы
     * @return json-объект comics со всеми полями или страницу комикса
     */
    @GetMapping("/{id}")
    public Mono<Object> getComicsById(
            @PathVariable long id,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer order) {
        return comicsService.getById(id, order);
    }

    /**
     * Просмотр списка персонажей, задействованных в комиксе
     *
     * @param id - id комикса
     * @return json список персонажей (имя, портрет)
     */
    @GetMapping("/{id}/characters")
    public Flux<Object> getCharacters(@PathVariable long id) {
        return comicsService.getCharacters(id);
    }

    /**
     * Создать новый комикс
     *
     * @param jsonComics json-объект character c обязателным полем "title"
     * @param file       Не обязательный. Файл-изображение
     * @return json объект нового комикса со всеми полями
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Object> addComics(
            @RequestPart("comics") String jsonComics,
            @RequestPart(name = "img", required = false) Mono<FilePart> file) {
        Comics comics = null;
        try {
            comics = jsonParser.readValue(jsonComics, Comics.class);
        } catch (JsonProcessingException e) {
            log.debug("Некорректный json: " + jsonComics);
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Недопустимый синтаксис или незаполнены обязательные поля: comics {\"title\":\"title\"}"));
        }
        return comicsService.add(comics, file);
    }

    /**
     * Добавление страниц комикса
     *
     * @param id     id комикса
     * @param order  Не обязательный.
     *               Произвольное число, на основе которого будет выстроен порядок страниц.
     *               Для нескольких файлов порядок будет соответствовать порядку их добавления.
     * @param images Файл-изображения. Один или несколько.
     * @return void;
     */
    @PostMapping(value = "/{id}")
    public Mono<Object> addPage(
            @PathVariable Long id,
            @RequestParam(required = false) Long order,
            @RequestPart Flux<FilePart> images) {
        return comicsService.addPage(id, order, images);
    }

    /**
     * Изменение данных комикса
     *
     * @param id         id комикса
     * @param jsonComics json-объект character c обязателным полем "title"
     *                   Если поля указаны не все, то будут заменены пустыми значениями
     * @param img        Файл-изображение
     * @return json-объект comics со всеми полями с обновленными значениями
     */

    @PutMapping("/{id}")
    public Mono<Object> putComics(
            @PathVariable Integer id,
            @RequestPart("comics") String jsonComics,
            @RequestPart(name = "img", required = false) Mono<FilePart> img) {
        Comics comics;
        try {
            comics = jsonParser.readValue(jsonComics, Comics.class);
        } catch (JsonProcessingException e) {
            log.debug("Некорректный json: " + jsonComics);
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Недопустимый синтаксис или незаполнены обязательные поля: comics {\"title\":\"title\"}"));
        }
        return comicsService.update(comics, img);
    }

    /**
     * Удаление комикса
     * Удаление отдельной страницы, если задан параметр files
     *
     * @param id   id комиска
     * @param file Не обязательный. Название файла
     * @return void
     */

    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteComics(
            @PathVariable long id,
            @RequestParam(required = false) String file) {
        if (file == null) {
            comicsService.delete(id);
        } else {
            comicsService.deletePage(id, file);
        }
        return Mono.empty();
    }
}
