package ru.stm.marvelcomics.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.stm.marvelcomics.domain.Comics;
import ru.stm.marvelcomics.domain.ComicsPage;
import ru.stm.marvelcomics.domain.dto.CharacterDTO;
import ru.stm.marvelcomics.domain.dto.ComicsDTO;
import ru.stm.marvelcomics.domain.dto.PageDTO;
import ru.stm.marvelcomics.repository.ComicsPageRepository;
import ru.stm.marvelcomics.repository.ComicsRepository;
import ru.stm.marvelcomics.service.ComicsService;
import ru.stm.marvelcomics.service.FileService;
import ru.stm.marvelcomics.util.Const;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <h2>Сервис для работы с репозиторием комиксов и страниц комиксов</h2>
 *
 * @see Comics#Comics()
 * @see ComicsPage#ComicsPage()
 */
@RequiredArgsConstructor
@Service
public class ComicsServiceImpl implements ComicsService {
    private final ComicsRepository comicsRepo;
    private final ComicsPageRepository pageRepo;

    @Override
    public Flux<ComicsDTO> get(String sort, int limit, int offset) {
        return Flux.fromStream(comicsRepo
                .findAll(Sort.by(sort))
                .stream()
                .skip(offset)
                .limit(limit)
                .map(ComicsDTO::preview));
    }

    @Override
    public Comics getComicsById(Long id) {
        return Optional.of(comicsRepo.findById(id).get())
                .orElseGet(null);
    }

    @Override
    public Mono<Object> getById(long id, int order) {
        if (!comicsRepo.existsById(id)) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("по id = %d ничего не найдено", id)));
        }
        Comics comics = comicsRepo.findById(id).get();
        if (order <= 0) {
            return Mono.just(comics);
        }
        return getPage(comics, order);
    }

    @Override
    public Flux<CharacterDTO> getCharacters(long id) {
        return Flux.fromStream(comicsRepo.findById(id).get()
                .getCharacters()
                .stream()
                .map(CharacterDTO::preview));

    }

    @Override
    public Mono<Comics> add(Comics comics, Mono<FilePart> file) {
        if (comics == null) return Mono.empty();
        comics.setId(null);
        Comics comicsFromDB = comicsRepo.save(comics); //получаем id
        if (file != null) addFile(comicsFromDB, file);
        return Mono.just(comicsRepo.save(comicsFromDB));
    }

    @Override
    public Mono<Comics> update(Comics comics, Mono<FilePart> file) {
        if (comics == null) return Mono.empty();
        if (!comicsRepo.existsById(comics.getId())) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("comics c id = %d удален или никогда не существовал", comics.getId())));
        }
        if (file != null) addFile(comics, file);
        return Mono.just(comicsRepo.save(comics));
    }

    @Override
    public Mono<Void> delete(long id) {
        if (comicsRepo.existsById(id)) {
            FileService.delete(Const.COMICS_DIR, id);
            comicsRepo.deleteById(id);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> addCharacter(long id, long characterId) {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
    }

    @Override
    public Mono<Void> deleteCharacter(long id, long characterId) {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
    }

    @Override
    public Mono<Void> addPage(long id, long order, Flux<FilePart> files) {
        if (!comicsRepo.existsById(id)) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("по id = %d ничего не найдено", id)));
        }
        Comics comics = comicsRepo.findById(id).get();
        files.index()
                .flatMap(f -> {
                    pageRepo.save(ComicsPage.builder()
                            .comics(comics)
                            .order(f.getT1() + order)
                            .pathFile(FileService.upload()
                                    .createPath(Const.COMICS_DIR, id, f.getT2().filename())
                                    .transfer(f.getT2())
                                    .getFilename())
                            .build());
                    return Mono.empty();
                }).subscribe();
        return Mono.empty();
    }

    @Override
    public Mono<Void> deletePage(long id, String fileName) {
        ComicsPage page = pageRepo.findComicsPageByPathFile(fileName);
        if (page != null) {
            FileService.delete(page.getPathFile());
            pageRepo.delete(page);
        }
        return Mono.empty();
    }

    /**
     * Добавление изображения в комикс.
     * Загрузка файла
     *
     * @param comics
     * @param file
     */
    private void addFile(Comics comics, Mono<FilePart> file) {
        file.flatMap(f -> {
            comics.setCover(FileService.upload()
                    .createPath(Const.COMICS_DIR, comics.getId(), f.filename())
                    .transfer(f)
                    .getFilename());
            return Mono.empty();
        }).subscribe();
    }

    /**
     * Возвращает страницу комикса
     *
     * @param comics
     * @param order  - номер страницы
     * @return объект {@link PageDTO#view(Comics, ComicsPage)}
     */
    private Mono<Object> getPage(Comics comics, int order) {
        return Mono.just(pageRepo.findAllByComics(comics)
                .stream()
                .sorted(Comparator.comparing(ComicsPage::getOrder))
                .skip(order)
                .limit(1)
                .map(page -> PageDTO.view(comics, page)))
                .cast(Object.class)
                .defaultIfEmpty(comics);
    }
}
