package com.webcrawler.webcrawlerapi.service;

import com.webcrawler.webcrawlerapi.crawler.ContentCleaner;
import com.webcrawler.webcrawlerapi.crawler.Fetcher;
import com.webcrawler.webcrawlerapi.crawler.Normalizer;
import com.webcrawler.webcrawlerapi.model.PageContent;
import com.webcrawler.webcrawlerapi.storage.JsonStorage;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class CrawlPipeline {

    Fetcher fetcher = new Fetcher();
    ContentCleaner clean = new ContentCleaner();
    Normalizer normalizer = new Normalizer();
    JsonStorage jsonStorage = new JsonStorage();

    public PageContent execute(String url) {
        Document document = fetcher.fetch(url);
        if (document == null) {
            throw new IllegalArgumentException("Não foi possível acessar a URL: " + url);
        }
        clean.clean(document);
        PageContent pageContent = normalizer.transformInPage(document);
        jsonStorage.save(pageContent);
        return pageContent;
    }
}