package ru.stm.marvelcomics.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.stm.marvelcomics.domain.Char;
import ru.stm.marvelcomics.domain.Comics;
import ru.stm.marvelcomics.domain.dto.CharacterDTO;
import ru.stm.marvelcomics.domain.dto.ComicsDTO;

/**
 * <h2>Интерфейс для работы с репозиторием персонажей </h2>
 *
 * @see Char#Char()
 */
public interface CharacterService {
    /**
     * Получеие списка объектов {@link ru.stm.marvelcomics.domain.dto.CharacterDTO#preview(Char)} персонажей в кратком содержании в отсортированном порядке
     *
     * @param sort   установлен "name"
     * @param limit  ограничение количества результатов
     * @param offset пропустить элементы
     * @return 0 или несколько объектов {@link ru.stm.marvelcomics.domain.dto.CharacterDTO#preview(Char)}
     */
    Flux<CharacterDTO> get(String sort, int limit, int offset);

    /**
     * Поиск персонажа по его id
     *
     * @param id уникальный идентификатор
     * @return 0 или 1 объект {@link Char#Char()}
     */
    Mono<Char> getById(long id);

    /**
     * Получение списка комиксов в которых задействован персонаж с id
     *
     * @param id
     * @return 0 или несколько объектов {@link ru.stm.marvelcomics.domain.dto.ComicsDTO#preview(Comics)}
     */
    Flux<ComicsDTO> getComics(long id);

    /**
     * Добавление персонажа</br>
     * Загрузка файла изображения, если есть
     *
     * @param character объект {@link Char#Char()}
     * @param file      1 файл изображение
     * @return объект {@link Char#Char()}
     */
    Mono<Char> add(Char character, Mono<FilePart> file);

    /**
     * Изменение данных персонажа</br>
     * Загрузка файла изображения, если есть</br>
     * Если получен новый файл, старый файл будет удален с диска
     *
     * @param character объект {@link Char#Char()}
     * @param file      1 файл изображение
     * @return объект {@link Char#Char()}
     */
    Mono<Char> update(Char character, Mono<FilePart> file);

    /**
     * Добавление комикса в список кмиксов, в которых задействован персонаж
     *
     * @param id       id персонажа
     * @param comicsId id комикса
     * @return void
     * @throws org.springframework.http.HttpStatus.400 Если параметры заданы некорректно
     */
    Mono<Object> addComics(long id, long comicsId);

    /**
     * Удаление персонажа</br>
     * Удаление файла изображения с диска
     *
     * @param id id персонажа
     * @return void
     */
    Mono<Void> delete(long id);

    /**
     * Удаление комикса из сприска комиксов, в которых персонаж задействован
     *
     * @param id       id персонажа
     * @param comicsId id комикса
     * @return void
     * @throws org.springframework.http.HttpStatus.400 Если параметры заданы некорректно
     */
    Mono<Void> deleteComics(Long id, Long comicsId);

}
