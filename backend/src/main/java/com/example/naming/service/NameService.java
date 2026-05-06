package com.example.naming.service;

import com.example.naming.dto.GenerateRequest;
import com.example.naming.dto.GenerateResponse;
import com.example.naming.entity.NameRecord;
import com.example.naming.entity.PoemWord;
import com.example.naming.repository.NameRecordRepository;
import com.example.naming.repository.PoemWordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NameService {

    private final PoemWordRepository poemWordRepository;
    private final NameRecordRepository nameRecordRepository;
    private final Random random = new Random();

    public NameService(PoemWordRepository poemWordRepository, NameRecordRepository nameRecordRepository) {
        this.poemWordRepository = poemWordRepository;
        this.nameRecordRepository = nameRecordRepository;
    }

    public GenerateResponse generateRandom(GenerateRequest req) {
        List<PoemWord> candidates = getCandidates(req.getSources());
        return buildResponse(req, candidates);
    }

    public GenerateResponse generateByKeyword(GenerateRequest req) {
        String keyword = req.getKeyword();
        List<PoemWord> candidates;
        if (req.getSources() != null && !req.getSources().isEmpty()) {
            candidates = poemWordRepository.findByWordAndSources(keyword, req.getSources());
        } else {
            candidates = poemWordRepository.findByWord(keyword);
        }
        return buildResponse(req, candidates);
    }

    public GenerateResponse generateByTheme(GenerateRequest req) {
        List<String> themes = req.getThemes();
        List<PoemWord> candidates;
        if (req.getSources() != null && !req.getSources().isEmpty()) {
            candidates = poemWordRepository.findByMeaningTagsAndSources(themes, req.getSources());
        } else {
            candidates = poemWordRepository.findByMeaningTags(themes);
        }
        return buildResponse(req, candidates);
    }

    private List<PoemWord> getCandidates(List<String> sources) {
        if (sources != null && !sources.isEmpty()) {
            return poemWordRepository.findBySources(sources);
        }
        return poemWordRepository.findAll();
    }

    private GenerateResponse buildResponse(GenerateRequest req, List<PoemWord> candidates) {
        if (candidates.isEmpty()) {
            return new GenerateResponse(Collections.emptyList());
        }

        List<GenerateResponse.NameItem> names = new ArrayList<>();
        int maxAttempts = req.getCount() * 10;
        int attempts = 0;

        while (names.size() < req.getCount() && attempts < maxAttempts) {
            attempts++;
            StringBuilder givenName = new StringBuilder();
            List<String> sources = new ArrayList<>();
            Set<Long> usedPoemIds = new HashSet<>();

            for (int i = 0; i < req.getLength(); i++) {
                PoemWord pw = candidates.get(random.nextInt(candidates.size()));
                if (usedPoemIds.contains(pw.getPoem().getId())) continue;
                usedPoemIds.add(pw.getPoem().getId());
                givenName.append(pw.getWord());
                if (pw.getPoem() != null && pw.getPoem().getContent() != null) {
                    sources.add(pw.getPoem().getContent());
                }
            }

            if (givenName.length() != req.getLength()) continue;

            String surname = req.getSurname() != null ? req.getSurname() : "";
            String fullName = surname + givenName.toString();
            String sourceStr = sources.isEmpty() ? "" : sources.get(0);

            GenerateResponse.NameItem item = new GenerateResponse.NameItem(
                fullName, surname, givenName.toString(), sourceStr, null
            );
            names.add(item);

            NameRecord record = new NameRecord();
            record.setSurname(surname);
            record.setGivenName(givenName.toString());
            record.setFullName(fullName);
            record.setMode("random");
            nameRecordRepository.save(record);
        }

        return new GenerateResponse(names);
    }

    public Page<NameRecord> getHistory(int page, int size) {
        return nameRecordRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }
}
