package com.example.naming.repository;

import com.example.naming.entity.Poem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

public interface PoemRepository extends JpaRepository<Poem, Long> {
    List<Poem> findBySource(String source);
    boolean existsByTitleAndAuthorAndSource(String title, String author, String source);

    @Query("SELECT p.source, COUNT(p) FROM Poem p GROUP BY p.source")
    List<Object[]> countBySource();

    @Query("SELECT CONCAT(p.title, '|', p.author, '|', p.source) FROM Poem p")
    Set<String> findAllKeys();

    List<Poem> findAllByIdIn(List<Long> ids);

    Long countBySourceIn(List<String> sources);
}
