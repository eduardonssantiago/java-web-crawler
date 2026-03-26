package com.webcrawler.webcrawlerapi.controller;

import com.webcrawler.webcrawlerapi.model.CrawlRequest;
import com.webcrawler.webcrawlerapi.model.PageContent;
import com.webcrawler.webcrawlerapi.service.CrawlPipeline;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CrawlerController {

    private final CrawlPipeline crawlPipeline;

    public CrawlerController(CrawlPipeline crawlPipeline) {
        this.crawlPipeline = crawlPipeline;
    }

    @PostMapping("/crawl")
    public ResponseEntity<?> crawl(@RequestBody CrawlRequest request) {
        try {
            PageContent result = crawlPipeline.execute(request.url());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
        }
    }
}