package ru.stm.marvelcomics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.stm.marvelcomics.domain.Comics;

/**
 * Репозиторий для работы с комиксами
 */
public interface ComicsRepository extends JpaRepository<Comics, Long> {
    @Modifying
    @Query(
            value = "INSERT INTO character_has_comics (character_id, comics_id) VALUES (:character_id, :comics_id)",
            nativeQuery = true)
    void saveCharacterHasComics(@Param("character_id") Long id, @Param("comics_id") Long comicsId);

}
