package simplewebcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import simplewebcrawler.Crawler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlerServiceImpl implements CrawlerService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);
    private int timeout;

    @Override
    public Crawler crawlURL(URL url) throws IOException {
        LOGGER.info("crawling url {}", url);

        Document document = null;
        try {
            document = Jsoup.parse(url, timeout);
        } catch (IOException e) {
            LOGGER.error("Problem accessing url {}", url.toString());
            throw e;
        }
        Elements titles = document.select("title");
        Elements links = document.select("a[href]");

        List<Crawler> childNodes = new ArrayList<>();
        links.forEach(link -> {
            try {
                childNodes.add(crawlURL(new URL(link.attr("href"))));
            } catch (IOException e) {
                LOGGER.error("Problem accessing url {}, moving on to the next one", link.attr("href"));
            }
        });

        return new Crawler(url.toString(), titles == null || titles.size() == 0 ? "" : titles.get(0).text(), childNodes);
    }

    @Value("${simplewebcrawler.timeout.seconds:8}")
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
