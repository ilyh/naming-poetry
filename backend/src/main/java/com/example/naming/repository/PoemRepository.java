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

    @Query(value = "SELECT * FROM poem ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Poem> findRandom(@Param("limit") int limit);

    @Query(value = "SELECT * FROM poem WHERE source IN :sources ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Poem> findRandomBySources(@Param("sources") List<String> sources, @Param("limit") int limit);
}
