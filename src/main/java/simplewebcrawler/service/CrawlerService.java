package simplewebcrawler.service;

import simplewebcrawler.service.impl.Crawler;

import java.io.IOException;
import java.net.URL;

public interface CrawlerService {
    Crawler crawlURL(URL url) throws IOException;
}
