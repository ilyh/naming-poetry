package com.example.naming.repository;

import com.example.naming.entity.NameVoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NameVoteRecordRepository extends JpaRepository<NameVoteRecord, Long> {
    Optional<NameVoteRecord> findByVoteIdAndSessionId(Long voteId, String sessionId);
}
