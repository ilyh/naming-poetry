package com.example.naming.controller;

import com.example.naming.dto.BookStat;
import com.example.naming.dto.GenerateRequest;
import com.example.naming.dto.GenerateResponse;
import com.example.naming.entity.NameRecord;
import com.example.naming.service.DataImportService;
import com.example.naming.service.NameService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/name")
public class NameController {

    private final NameService nameService;
    private final DataImportService dataImportService;

    public NameController(NameService nameService, DataImportService dataImportService) {
        this.nameService = nameService;
        this.dataImportService = dataImportService;
    }

    @PostMapping("/random")
    public ResponseEntity<GenerateResponse> random(@RequestBody GenerateRequest req) {
        return ResponseEntity.ok(nameService.generateRandom(req));
    }

    @PostMapping("/keyword")
    public ResponseEntity<GenerateResponse> keyword(@RequestBody GenerateRequest req) {
        return ResponseEntity.ok(nameService.generateByKeyword(req));
    }

    @PostMapping("/theme")
    public ResponseEntity<GenerateResponse> theme(@RequestBody GenerateRequest req) {
        return ResponseEntity.ok(nameService.generateByTheme(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NameRecord> detail(@PathVariable Long id) {
        return nameService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<Page<NameRecord>> history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sessionId) {
        return ResponseEntity.ok(nameService.getHistory(sessionId, page, size));
    }

    @PostMapping("/admin/import")
    public ResponseEntity<String> importPoems() {
        try {
            return ResponseEntity.ok(dataImportService.importData());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Data import failed: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<List<BookStat>> stats() {
        return ResponseEntity.ok(nameService.getBookStats());
    }
}
