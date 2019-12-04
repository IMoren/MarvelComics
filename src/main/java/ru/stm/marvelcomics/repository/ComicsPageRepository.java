package ru.stm.marvelcomics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.stm.marvelcomics.domain.Comics;
import ru.stm.marvelcomics.domain.ComicsPage;

import java.util.Collection;

/**
 * <h2>Репозиторий для работы с страницами комикса</h2>
 *
 * @see ComicsPage#ComicsPage()
 */
public interface ComicsPageRepository extends JpaRepository<ComicsPage, Long> {
    Collection<ComicsPage> findAllByComics(Comics comics);
    ComicsPage findComicsPageByPathFile(String pathFile);
}
