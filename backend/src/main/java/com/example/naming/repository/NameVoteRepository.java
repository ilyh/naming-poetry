package com.example.naming.repository;

import com.example.naming.entity.NameVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NameVoteRepository extends JpaRepository<NameVote, Long> {
}
