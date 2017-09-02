package simplewebcrawler;

import simplewebcrawler.provides.Crawler;

import java.io.IOException;
import java.net.URL;

public interface CrawlerPort {
    Crawler crawlURL(URL rootUrl) throws IOException;
}
