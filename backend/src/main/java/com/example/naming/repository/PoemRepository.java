package com.example.naming.repository;

import com.example.naming.entity.Poem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PoemRepository extends JpaRepository<Poem, Long> {
    List<Poem> findBySource(String source);
}
