package com.example.naming.repository;

import com.example.naming.entity.NameVoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NameVoteItemRepository extends JpaRepository<NameVoteItem, Long> {
    List<NameVoteItem> findByVoteIdOrderByIdAsc(Long voteId);

    // flushAutomatically：先把挂起的变更落库；clearAutomatically：清空一级缓存，
    // 保证同一事务内后续的查询（如 castVote 里返回最新结果）读到真实票数而不是旧快照
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update NameVoteItem i set i.votes = i.votes + 1 where i.id = :id")
    void incrementVotes(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update NameVoteItem i set i.votes = i.votes - 1 where i.id = :id and i.votes > 0")
    void decrementVotes(@Param("id") Long id);
}
