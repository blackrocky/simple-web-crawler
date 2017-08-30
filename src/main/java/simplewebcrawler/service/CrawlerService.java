package simplewebcrawler.service;

import simplewebcrawler.Crawler;

import java.io.IOException;

public interface CrawlerService {
    Crawler crawlURL(String url) throws IOException;
}
