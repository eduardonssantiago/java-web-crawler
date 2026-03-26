package com.webcrawler.webcrawlerapi.controller;

import com.webcrawler.webcrawlerapi.model.CrawlRequest;
import com.webcrawler.webcrawlerapi.model.PageContent;
import com.webcrawler.webcrawlerapi.service.CrawlPipeline;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class CrawlerController {

    private final CrawlPipeline crawlPipeline;

    public CrawlerController(CrawlPipeline crawlPipeline) {
        this.crawlPipeline = crawlPipeline;
    }

    @PostMapping("/crawls")
    public ResponseEntity<?> crawl(@RequestBody CrawlRequest request) {
        try {
            PageContent result = crawlPipeline.execute(request.url());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/crawls/{uuid}")
    public ResponseEntity<?> crawl(@PathVariable("uuid") String uuid) {

        try {
            UUID uuidObj = UUID.fromString(uuid);
            PageContent result = crawlPipeline.get(uuidObj);

            if (result == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(result);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "UUID invalido"));
        }

    }

    @GetMapping("/crawls")
    public ResponseEntity<?> getAll() {
        try {
            List<PageContent> results = crawlPipeline.getAll();
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/crawls/{uuid}")
    public ResponseEntity<?> delete(@PathVariable("uuid") String uuid) {

        try {
            UUID uuidObj = UUID.fromString(uuid);
            PageContent result = crawlPipeline.delete(uuidObj);

            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "UUID invalido"));
        }
    }

    @DeleteMapping("/crawls")
    public ResponseEntity<?> deleteAll() {
        try {
            crawlPipeline.deleteAll();
            return  ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}