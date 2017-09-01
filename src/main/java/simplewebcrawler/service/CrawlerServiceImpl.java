package simplewebcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import simplewebcrawler.Crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlerServiceImpl implements CrawlerService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);
    private int depth;

    @Override
    public Crawler crawlURL(String url) throws IOException {
        LOGGER.info("crawling url {}", url);

        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            LOGGER.error("Problem accessing url {}", url);
            throw e;
        }
        Element title = document.select("title").get(0);
        Elements links = document.select("a[href]");

        List<Crawler> childNodes = new ArrayList<>();
        links.forEach(link -> {
            try {
                childNodes.add(crawlURL(link.attr("href")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return new Crawler(url, title.text(), childNodes);
    }

    // TODO remove
    @Value("${simplewebcrawler.depth:1}")
    public void setDepth(int depth) {
        this.depth = depth;
    }
}
