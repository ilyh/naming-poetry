package com.example.naming.repository;

import com.example.naming.entity.NameRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NameRecordRepository extends JpaRepository<NameRecord, Long> {
    Page<NameRecord> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
