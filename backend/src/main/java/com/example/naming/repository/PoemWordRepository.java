package com.example.naming.repository;

import com.example.naming.entity.PoemWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PoemWordRepository extends JpaRepository<PoemWord, Long> {

    List<PoemWord> findByWord(String word);

    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE pw.meaningTag IN :tags")
    List<PoemWord> findByMeaningTags(@Param("tags") List<String> tags);

    @Query(value = "SELECT * FROM poem_word ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<PoemWord> findRandom(@Param("limit") int limit);

    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE p.source IN :sources")
    List<PoemWord> findBySources(@Param("sources") List<String> sources);

    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE pw.word = :word AND p.source IN :sources")
    List<PoemWord> findByWordAndSources(@Param("word") String word, @Param("sources") List<String> sources);

    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE pw.meaningTag IN :tags AND p.source IN :sources")
    List<PoemWord> findByMeaningTagsAndSources(@Param("tags") List<String> tags, @Param("sources") List<String> sources);
}
