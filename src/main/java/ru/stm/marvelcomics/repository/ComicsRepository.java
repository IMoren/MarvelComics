package ru.stm.marvelcomics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.stm.marvelcomics.domain.Comics;

/**
 * <h2>Репозиторий для работы с комиксами</h2>
 *
 * @see Comics#Comics()
 */
public interface ComicsRepository extends JpaRepository<Comics, Long> {
}
