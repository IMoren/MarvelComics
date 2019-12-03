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

@RequiredArgsConstructor
@Service
public class ComicsServiceImpl implements ComicsService {
    private final ComicsRepository comicsRepo;
    private final ComicsPageRepository pageRepo;

    @Override
    public Flux<Object> get(String sort, int limit, int offset) {
        return Flux.just(comicsRepo
                .findAll(Sort.by(sort))
                .stream()
                .skip(offset)
                .limit(limit)
                .map(ComicsDTO::preview))
                .cast(Object.class)
                .defaultIfEmpty(Flux.empty());
    }

    @Override
    public Mono<Object> getById(long id, int order) {
        Optional<Comics> optionalComics = comicsRepo.findById(id);
        if (id < 0 || !optionalComics.isPresent()) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("по id = %d ничего не найдено", id)));
        }
        Comics comics = optionalComics.get();
        if (order <= 0) {
            return Mono.just(comics).cast(Object.class)
                    .doOnError(e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("по id = %d ничего не найдено", id))));
        }
        return getPage(comics, order);
    }

    @Override
    public Flux<Object> getCharacters(long id) {
        return Flux.just(comicsRepo.findById(id).get()
                .getCharacters()
                .stream()
                .map(CharacterDTO::preview))
                .cast(Object.class)
                .defaultIfEmpty(Flux.empty());
    }

    @Override
    public Mono<Object> add(Comics comics, Mono<FilePart> file) {
        if (comics == null) return Mono.empty();
        comics.setId(null);
        Comics comicsFromDB = comicsRepo.save(comics); //получаем id
        if (file != null) addFile(comicsFromDB, file);
        return Mono.just(comicsRepo.save(comicsFromDB));
    }

    @Override
    public Mono<Object> update(Comics comics, Mono<FilePart> file) {
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
        return null;
    }

    @Override
    public Mono<Void> deleteCharacter(long id, long characterId) {
        return null;
    }

    @Override
    public Mono<Object> addPage(long id, long order, Flux<FilePart> files) {
        if (!comicsRepo.existsById(id)) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("по id = %d ничего не найдено", id)));
        }
        Comics comics = comicsRepo.findById(id).get();
        files.flatMap(f -> {
            pageRepo.save(ComicsPage.builder()
                    .comics(comics)
                    .order(order)
                    .pathFile(FileService.upload()
                            .createPath(Const.COMICS_DIR, id, f.filename())
                            .transfer(f)
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

    private void addFile(Comics comics, Mono<FilePart> file) {
        file.flatMap(f -> {
            comics.setCover(FileService.upload()
                    .createPath(Const.COMICS_DIR, comics.getId(), f.filename())
                    .transfer(f)
                    .getFilename());
            return Mono.empty();
        }).subscribe();
    }

    private Mono<Object> getPage(Comics comics, int order) {
        return Mono.just(pageRepo.findAllByComics(comics)
                .stream()
                .sorted(Comparator.comparing(ComicsPage::getOrder))
                .skip(order - 1)
                .limit(1)
                .map(page -> PageDTO.view(comics, page)))
                .cast(Object.class)
                .defaultIfEmpty(comics);
    }
}
