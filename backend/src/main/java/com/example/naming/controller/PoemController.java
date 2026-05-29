package com.example.naming.controller;

import com.example.naming.dto.PoemResponse;
import com.example.naming.entity.Poem;
import com.example.naming.repository.PoemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poem")
public class PoemController {

    private final PoemRepository poemRepository;

    public PoemController(PoemRepository poemRepository) {
        this.poemRepository = poemRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoemResponse> getPoem(@PathVariable Long id) {
        return poemRepository.findById(id)
            .map(poem -> ResponseEntity.ok(new PoemResponse(
                poem.getId(),
                poem.getTitle(),
                poem.getAuthor(),
                poem.getDynasty(),
                poem.getContent(),
                poem.getSource()
            )))
            .orElse(ResponseEntity.notFound().build());
    }
}
