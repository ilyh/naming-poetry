package com.example.naming.controller;

import com.example.naming.dto.GenerateRequest;
import com.example.naming.dto.GenerateResponse;
import com.example.naming.entity.NameRecord;
import com.example.naming.service.DataImportService;
import com.example.naming.service.NameService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return nameService.getHistory(0, 1).getContent().stream()
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<Page<NameRecord>> history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(nameService.getHistory(page, size));
    }

    @PostMapping("/admin/import")
    public ResponseEntity<String> importPoems() {
        return ResponseEntity.ok(dataImportService.importData());
    }
}
