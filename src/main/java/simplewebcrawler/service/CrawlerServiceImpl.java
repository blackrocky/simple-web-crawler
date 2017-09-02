package simplewebcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import simplewebcrawler.service.impl.Crawler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CrawlerServiceImpl implements CrawlerService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);
    private int timeoutInMillis;
    private Set<URL> urlSet = ConcurrentHashMap.newKeySet();

    @Override
    public Crawler crawlURL(URL url) throws IOException {
        if (url == null) {
            LOGGER.error("url must not be null");
            throw new IllegalStateException("url must not be null");
        }
        LOGGER.info("crawling url {} with timeout {} milliseconds", url.toString(), timeoutInMillis);

        urlSet.add(url);

        Document document = null;
        try {
            document = Jsoup.parse(url, timeoutInMillis);
        } catch (IOException e) {
            LOGGER.error("Problem accessing url {}", url.toString());
            throw e;
        }
        Elements titles = document.select("title");
        Elements links = document.select("a[href]");

        List<Crawler> childNodes = new ArrayList<>();
        links.forEach(link -> {
            try {
                URL hrefUrl = new URL(link.attr("href"));
                if (!urlSet.contains(hrefUrl)) {
                    childNodes.add(crawlURL(hrefUrl));
                }
            } catch (IOException e) {
                LOGGER.error("Problem accessing url {}, moving on to the next one", link.attr("href"));
            }
        });

        return new Crawler(url.toString(), titles == null || titles.size() == 0 ? "" : titles.get(0).text(), childNodes);
    }

    @Value("${simplewebcrawler.timeout.millis:10000}")
    public void setTimeoutInMillis(int timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }
}
