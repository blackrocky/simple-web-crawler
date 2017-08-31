package simplewebcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import simplewebcrawler.Crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlerServiceImpl implements CrawlerService {
    private int depth;

    @Override
    public Crawler crawlURL(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Element title = document.select("title").get(0);
        Elements links = document.select("a[href]");

        List<Crawler> nodes = new ArrayList<>();
        links.forEach(link -> nodes.add(new Crawler(link.attr("abs:href"), "test title", null)));
        System.out.println("depth = " + depth);
        return new Crawler(url, title.text(), nodes);
    }

    @Value("${simplewebcrawler.depth:1}")
    public void setDepth(int depth) {
        this.depth = depth;
    }
}
