package com.example.naming.service;

import com.example.naming.dto.VoteCreateRequest;
import com.example.naming.dto.VoteDetail;
import com.example.naming.entity.NameVote;
import com.example.naming.entity.NameVoteItem;
import com.example.naming.entity.NameVoteRecord;
import com.example.naming.repository.NameVoteItemRepository;
import com.example.naming.repository.NameVoteRecordRepository;
import com.example.naming.repository.NameVoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoteService {

    private static final int MAX_ITEMS = 8;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final NameVoteRepository voteRepository;
    private final NameVoteItemRepository itemRepository;
    private final NameVoteRecordRepository recordRepository;

    public VoteService(NameVoteRepository voteRepository,
                       NameVoteItemRepository itemRepository,
                       NameVoteRecordRepository recordRepository) {
        this.voteRepository = voteRepository;
        this.itemRepository = itemRepository;
        this.recordRepository = recordRepository;
    }

    @Transactional
    public Long createVote(VoteCreateRequest req) {
        if (req.getItems() == null || req.getItems().size() < 2) {
            throw new IllegalArgumentException("至少需要 2 个候选名字");
        }
        if (req.getItems().size() > MAX_ITEMS) {
            throw new IllegalArgumentException("最多支持 " + MAX_ITEMS + " 个候选名字");
        }

        NameVote vote = new NameVote();
        vote.setCreatorSessionId(req.getSessionId() == null ? "" : req.getSessionId());
        vote.setSurname(req.getSurname() == null ? "" : req.getSurname());
        vote = voteRepository.save(vote);

        for (VoteCreateRequest.ItemInput input : req.getItems()) {
            String given = input.getGivenName() == null ? "" : input.getGivenName().trim();
            String surname = input.getSurname() == null ? "" : input.getSurname().trim();
            if (given.isEmpty()) {
                continue;
            }
            NameVoteItem item = new NameVoteItem();
            item.setVoteId(vote.getId());
            item.setGivenName(given);
            item.setFullName(surname + given);
            item.setSourceNote(truncate(input.getSourceNote(), 255));
            String sentence = input.getSources() != null && !input.getSources().isEmpty()
                    ? input.getSources().get(0) : null;
            item.setSourceSentence(truncate(sentence, 255));
            item.setPoemId(input.getPoemId());
            item.setVotes(0);
            itemRepository.save(item);
        }
        return vote.getId();
    }

    @Transactional(readOnly = true)
    public VoteDetail getVote(Long id, String sessionId) {
        NameVote vote = voteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("投票不存在"));
        List<VoteDetail.Item> items = new ArrayList<>();
        for (NameVoteItem item : itemRepository.findByVoteIdOrderByIdAsc(id)) {
            items.add(new VoteDetail.Item(item.getId(), item.getFullName(), item.getGivenName(),
                    item.getSourceNote(), item.getSourceSentence(), item.getPoemId(), item.getVotes()));
        }
        Long myVoteItemId = recordRepository.findByVoteIdAndSessionId(id, sessionId == null ? "" : sessionId)
                .map(NameVoteRecord::getItemId)
                .orElse(null);
        return new VoteDetail(vote.getId(), vote.getSurname(),
                vote.getCreatedAt() == null ? null : vote.getCreatedAt().format(FMT), myVoteItemId, items);
    }

    @Transactional
    public VoteDetail castVote(Long id, String sessionId, Long itemId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("缺少投票者身份");
        }
        NameVote vote = voteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("投票不存在"));
        NameVoteItem target = itemRepository.findById(itemId)
                .filter(it -> it.getVoteId().equals(id))
                .orElseThrow(() -> new IllegalArgumentException("候选名字不存在"));

        NameVoteRecord record = recordRepository.findByVoteIdAndSessionId(id, sessionId).orElse(null);
        if (record == null) {
            record = new NameVoteRecord();
            record.setVoteId(id);
            record.setSessionId(sessionId);
            record.setCreatedAt(LocalDateTime.now());
            itemRepository.incrementVotes(target.getId());
        } else if (!record.getItemId().equals(target.getId())) {
            itemRepository.decrementVotes(record.getItemId());
            itemRepository.incrementVotes(target.getId());
            record.setUpdatedAt(LocalDateTime.now());
        } else {
            // 重复投同一项，直接返回当前结果
            return getVote(id, sessionId);
        }
        record.setItemId(target.getId());
        recordRepository.save(record);

        return getVote(id, sessionId);
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
