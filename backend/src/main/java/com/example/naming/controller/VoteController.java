package com.example.naming.controller;

import com.example.naming.dto.VoteCreateRequest;
import com.example.naming.dto.VoteDetail;
import com.example.naming.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vote")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody VoteCreateRequest req) {
        try {
            Long id = voteService.createVote(req);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id,
                                    @RequestParam(required = false) String sessionId) {
        try {
            return ResponseEntity.ok(voteService.getVote(id, sessionId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/cast")
    public ResponseEntity<?> cast(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String sessionId = body.get("sessionId") == null ? null : body.get("sessionId").toString();
            Object itemIdRaw = body.get("itemId");
            if (itemIdRaw == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "缺少候选名字"));
            }
            Long itemId;
            try {
                itemId = Long.valueOf(itemIdRaw.toString());
            } catch (NumberFormatException nfe) {
                return ResponseEntity.badRequest().body(Map.of("message", "参数格式错误"));
            }
            return ResponseEntity.ok(voteService.castVote(id, sessionId, itemId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
