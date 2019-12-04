package ru.stm.marvelcomics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.stm.marvelcomics.domain.Char;

/**
 * <h2>Репозитории для работы с персонажами комиксов</h2>
 *
 * @see Char#Char()
 */
public interface CharacterRepository extends JpaRepository<Char, Long> {

    /**
     * Добавление связи персонажа с комиксом
     * @param id
     * @param comicsId
     */
    @Modifying
    @Query(
            value = "INSERT INTO public.character_has_comics (character_id, comics_id) VALUES (:character_id, :comics_id)",
            nativeQuery = true)
    void saveCharacterHasComics(@Param("character_id") Long id, @Param("comics_id") Long comicsId);

    /**
     * Удаление связи персонажа и комикса
     * @param id
     * @param comicsId
     * @return
     */
    @Modifying
    @Query(
            value = "DELETE FROM public.character_has_comics WHERE character_id = :character_id AND comics_id = :comics_id",
            nativeQuery = true)
    int deleteCharacterHasComics(@Param("character_id") Long id, @Param("comics_id") Long comicsId);
}
