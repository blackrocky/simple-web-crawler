package simplewebcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import simplewebcrawler.CrawlerPort;
import simplewebcrawler.provides.Crawler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CrawlerServiceImpl implements CrawlerPort {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);
    private Set<URL> urlSet = ConcurrentHashMap.newKeySet();

    private int timeoutInMillis;
    private int maxDepth;

    @Override
    public Crawler crawlURL(URL url) throws IOException {
        Crawler crawler = crawlURL(url, maxDepth);
        LOGGER.info("FINISHED!");
        System.out.println("FINISHED!");
        return crawler;
    }

    private Crawler crawlURL(URL url, int depth) throws IOException {
        if (url == null) {
            LOGGER.error("url must not be null");
            throw new IllegalStateException("url must not be null");
        }
        LOGGER.info("crawling url {} with timeout {} milliseconds and max depth {}", url.toString(), timeoutInMillis, depth);
        System.out.println(String.format("crawling url %s with timeout %d milliseconds and max depth %d", url.toString(), timeoutInMillis, depth));

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

        List<Crawler> childNodes = crawlChildNodes(links, depth);

        return new Crawler(url.toString(), titles == null || titles.size() == 0 ? "" : titles.get(0).text(), childNodes);
    }

    private List<Crawler> crawlChildNodes(Elements links, int depth) {
        List<Crawler> childNodes = new ArrayList<>();
        links.forEach(link -> {
            try {
                URL hrefUrl = new URL(link.attr("href"));
                if (depth == 0) {
                    LOGGER.info("depth is 0, stop crawling");
                    System.out.println("depth is 0, stop crawling");
                }

                if (!urlSet.contains(hrefUrl) && depth != 0) {
                    childNodes.add(crawlURL(hrefUrl, depth-1));
                }
            } catch (IOException e) {
                LOGGER.error("Problem accessing url {}, moving on to the next one", link.attr("href"));
            }
        });

        return childNodes;
    }

    @Value("${simplewebcrawler.timeout.millis:10000}")
    public void setTimeoutInMillis(int timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    @Value("${simplewebcrawler.max.depth:10}")
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
