package ru.stm.marvelcomics.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.stm.marvelcomics.domain.Char;
import ru.stm.marvelcomics.domain.Comics;
import ru.stm.marvelcomics.domain.ComicsPage;

/**
 * <h2>Интерфейс для работы с репозиторием комиксов и страниц комиксов</h2>
 *
 * @see Comics#Comics()
 * @see ru.stm.marvelcomics.domain.dto.PageDTO#view(Comics, ComicsPage)
 */

public interface ComicsService {
    /**
     * Получение списка объектов {@link ru.stm.marvelcomics.domain.dto.ComicsDTO#preview(Comics)} комиксов в кратком содержании
     *
     * @param sort   параметр сортировки
     * @param limit  ограничение количества резулльтатов
     * @param offset пропустить элементы
     * @return 0 или несколько объектов {@link ru.stm.marvelcomics.domain.dto.ComicsDTO#preview(Comics)}
     */
    Flux<Object> get(String sort, int limit, int offset);

    /**
     * Поиск комикса по его id</br>
     * Просмотр страниц комикса, если order задан корректно
     *
     * @param id    id комикса
     * @param order номер страницы
     * @return или 1 объект {@link ru.stm.marvelcomics.domain.dto.PageDTO#view(Comics, ComicsPage)}
     * @throws org.springframework.http.HttpStatus.404 если нет комикса с таким id
     */
    Mono<Object> getById(long id, int order);

    /**
     * Получание списка персонажей, задействованных в комиксе
     *
     * @param id id комикса
     * @return 0 или несколько {@link ru.stm.marvelcomics.domain.dto.CharacterDTO#preview(Char)}
     */
    Flux<Object> getCharacters(long id);

    /**
     * Добавление новго комикса</br>
     * Загрузка файла изображения, если есть
     *
     * @param comics объект {@link Comics#Comics()}
     * @param file   1 файл изображения
     * @return новый объект {@link Comics#Comics()} со всеми полями
     */
    Mono<Object> add(Comics comics, Mono<FilePart> file);

    /**
     * Изменение анных комикса</br>
     * Загрузка файла изображения, если есть</br>
     * Если получен новый файл, старый файл будет удален с диска
     *
     * @param comics объект {@link Comics#Comics()}
     * @param file   1 файл изображения
     * @return объект {@link Comics#Comics()} с обновленными данными
     * @throws org.springframework.http.HttpStatus.404 елси в базе нет комикса, для которого пришли новые данные
     */
    Mono<Object> update(Comics comics, Mono<FilePart> file);

    /**
     * Удаление комикса</br>
     * Удаление всех связанных с ним файлов с диска
     *
     * @param id id комикса
     * @return void
     */
    Mono<Void> delete(long id);

    /**
     * Добавление персонажа в список персонажеи, задействованных в комиксе
     *
     * @param id          id комикса
     * @param characterId id персонажа
     * @return void
     * @throws org.springframework.http.HttpStatus.404 если параметры заданы некорректно
     */

    Mono<Void> addCharacter(long id, long characterId);

    /**
     * Удаление персонажа из списка персонажей, задействованных в комиксе
     *
     * @param id          id комикса
     * @param characterId id персонажа
     * @return void
     */
    Mono<Void> deleteCharacter(long id, long characterId);

    /**
     * Добавление страниц в комикс</br>
     * Загрузка файлов на диск
     *
     * @param id    id комикса
     * @param order параметр, определяющии порядок следования страниц
     * @param files 1 или несколько файлов
     * @return void
     */
    Mono<Object> addPage(long id, long order, Flux<FilePart> files);

    /**
     * Удаление страницы из комикса
     *
     * @param id       id комикса
     * @param fileName имя файла
     * @return void
     */
    Mono<Void> deletePage(long id, String fileName);

}
