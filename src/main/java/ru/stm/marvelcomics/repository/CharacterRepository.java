package ru.stm.marvelcomics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.stm.marvelcomics.domain.Char;

/**
 * <h2>Репозитории для работы с персонажами комиксов</h2>
 *
 * @see Char#Char()
 */
public interface CharacterRepository extends JpaRepository<Char, Long> {
}
