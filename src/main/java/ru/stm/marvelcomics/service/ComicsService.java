package ru.stm.marvelcomics.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.stm.marvelcomics.domain.Comics;

public interface ComicsService {
    Flux<Object> get(String sort, int limit, int offset);

    Mono<Object> getById(long id, int order);

    Flux<Object> getCharacters(long id);

    Mono<Object> add(Comics comics, Mono<FilePart> file);

    Mono<Object> update(Comics comics, Mono<FilePart> file);

    Mono<Void> delete(long id);

    Mono<Void> addCharacter(long id, long characterId);

    Mono<Void> deleteCharacter(long id, long characterId);

    Mono<Object> addPage(long id, long order, Flux<FilePart> files);

    Mono<Void> deletePage(long id, String fileName);

}
