package ru.stm.marvelcomics.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.stm.marvelcomics.domain.Char;

public interface CharacterService {
    Flux<Object> get(String sort, int limit, int offset);

    Mono<Object> getById(long id);

    Flux<Object> getComics(long id);

    Mono<Object> add(Char character, Mono<FilePart> file);

    Mono<Object> update(Char comics, Mono<FilePart> file);

    Mono<Object> addComics(long id, long comicsId);

    Mono<Void> delete(long id);

    Mono<Void> deleteComics(Long id, Long comicsId);

}
