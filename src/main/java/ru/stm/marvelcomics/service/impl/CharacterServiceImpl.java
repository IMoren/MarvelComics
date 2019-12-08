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
import ru.stm.marvelcomics.domain.Comics;
import ru.stm.marvelcomics.domain.dto.CharacterDTO;
import ru.stm.marvelcomics.domain.dto.ComicsDTO;
import ru.stm.marvelcomics.repository.CharacterRepository;
import ru.stm.marvelcomics.repository.ComicsRepository;
import ru.stm.marvelcomics.service.CharacterService;
import ru.stm.marvelcomics.service.FileService;
import ru.stm.marvelcomics.util.Const;

import java.util.Optional;

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
    public Flux<CharacterDTO> get(String sort, int limit, int offset) {
        return Flux.fromStream(characterRepo
                .findAll(Sort.by(sort))
                .stream()
                .skip(offset)
                .limit(limit)
                .map(CharacterDTO::preview));
    }

    @Override
    public Char getCharacterById(Long id) {
        return Optional.of(characterRepo.findById(id).get())
                .orElseGet(null);
    }

    @Override
    public Mono<Char> getById(long id) {
        return characterRepo.findById(id)
                .map(Mono::just)
                .orElseGet(() ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format("по id = %d ничего не найдено", id))));
    }

    @Override
    public Flux<ComicsDTO> getComics(long id) {
        return characterRepo.findById(id)
                .map(character ->
                        Flux.fromStream(character.getComicsList()
                                .stream()
                                .map(ComicsDTO::preview)))
                .orElseGet(() -> Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("по id = %d ничего не найдено", id))));
    }

    @Override
    public Mono<Char> add(Char character, Mono<FilePart> file) {
        if (character == null) return Mono.empty();
        character.setId(null);
        Char charFromDB = characterRepo.save(character);
        if (file != null) addFile(charFromDB, file);
        return Mono.just(characterRepo.save(charFromDB));
    }

    @Override
    public Mono<Char> update(Char character, Mono<FilePart> file) {
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
    public Mono<Object> addComics(long id, Comics comics) {
        Char character = getCharacterById(id);
        if (character != null && comics != null) {
            character.addComics(comics);
            return Mono.just(characterRepo.save(character));
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
        Char character = getCharacterById(id);
        if (character!=null) {
            character.getComicsList().removeIf(comics -> comics.getId().equals(comicsId));
            characterRepo.save(character);
            return Mono.empty();
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
