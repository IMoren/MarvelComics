package ru.stm.marvelcomics.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.stm.marvelcomics.domain.Char;
import ru.stm.marvelcomics.domain.dto.CharacterDTO;
import ru.stm.marvelcomics.domain.dto.ComicsDTO;
import ru.stm.marvelcomics.repository.CharacterRepository;
import ru.stm.marvelcomics.repository.ComicsRepository;
import ru.stm.marvelcomics.service.CharacterService;
import ru.stm.marvelcomics.service.FileService;
import ru.stm.marvelcomics.util.Const;

/**
 * <h2>Сервис для работы с репозиторием персонажей </h2>
 *
 * @see Char#Char()
 */
@RequiredArgsConstructor
@Service
public class CharacterServiceImpl implements CharacterService {
    private final CharacterRepository characterRepo;
    private final ComicsRepository comicsRepo;

    @Override
    public Flux<Object> get(String sort, int limit, int offset) {
        return Flux.just(characterRepo
                .findAll(Sort.by(sort))
                .stream()
                .skip(offset)
                .limit(limit)
                .map(CharacterDTO::preview))
                .cast(Object.class)
                .defaultIfEmpty(Flux.empty());
    }

    @Override
    public Mono<Object> getById(long id) {
        return characterRepo.findById(id)
                .map(character ->
                        Mono.just(character).cast(Object.class))
                .orElseGet(() ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format("по id = %d ничего не найдено", id))));
    }

    @Override
    public Flux<Object> getComics(long id) {
        return characterRepo.findById(id)
                .map(character ->
                        Flux.just(character.getComicsList()
                                .stream()
                                .map(ComicsDTO::preview))
                                .cast(Object.class))
                .orElseGet(() -> Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("по id = %d ничего не найдено", id))));
    }

    @Override
    public Mono<Object> add(Char character, Mono<FilePart> file) {
        if (character == null) return Mono.empty();
        character.setId(null);
        Char charFromDB = characterRepo.save(character);
        if (file != null) addFile(charFromDB, file);
        return Mono.just(characterRepo.save(charFromDB));
    }

    @Override
    public Mono<Object> update(Char character, Mono<FilePart> file) {
        if (!characterRepo.existsById(character.getId())) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("character c id = %d удален или никогда не существовал", character.getId())));
        }
        FileService.delete(characterRepo.findById(character.getId())
                .map(Char::getPortrait)
                .orElse(""));
        if (file != null) addFile(character, file);
        return Mono.just(characterRepo.save(character));
    }

    @Override
    public Mono<Object> addComics(long id, long comicsId) {
        if (comicsRepo.existsById(comicsId) && characterRepo.existsById(id)) {
//            characterRepo.saveCharacterHasComics(id, comicsId);
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
        }
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    @Override
    public Mono<Void> delete(long id) {
        if (characterRepo.existsById(id)) {
            FileService.delete(Const.CHARACTER_DIR, id);
            characterRepo.deleteById(id);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> deleteComics(Long id, Long comicsId) {
        if (characterRepo.existsById(id)) {
//            characterRepo.deleteCharacterHasComics(id, comicsId);
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
        }
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    private void addFile(Char character, Mono<FilePart> file) {
        file.flatMap(f -> {
            character.setPortrait(FileService.upload()
                    .createPath(Const.COMICS_DIR, character.getId(), f.filename())
                    .transfer(f)
                    .getFilename());
            return Mono.empty();
        }).subscribe();
    }
}
