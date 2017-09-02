package simplewebcrawler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import simplewebcrawler.CrawlerPort;
import simplewebcrawler.provides.Crawler;
import simplewebcrawler.validator.URLValidator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Collections.singletonList;

@Service
public class OldCrawlerServiceImpl implements CrawlerPort {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);
    private Set<URL> urlSet = new HashSet<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private int timeoutInMillis;
    private int maxDepth;

    @Override
    public Crawler crawlURL(URL url) throws IOException {
        if (!URLValidator.isValid(url)) {
            LOGGER.error("url must be valid");
            throw new IllegalStateException();
        }
        Crawler crawler = crawlURL(singletonList(url), maxDepth);
        LOGGER.info("FINISHED!!! {}", crawler);
        System.out.println("FINISHED!!! " + crawler);
        return crawler;
    }

    private Crawler crawlURL(List<URL> urls, int depth) throws IOException {
        if (depth <= 0) {
            return null;
        }
        List<Future<SingleCrawler>> singleCrawlerFutures = new ArrayList<>();
        for (URL url : urls) {
            if (!URLValidator.isValid(url) || urlSet.contains(url)) {
                LOGGER.info("ignoring url {} because it is either invalid or exists in set", String.valueOf(url));
                continue;
            }
            urlSet.add(url);
            LOGGER.info("Crawling url {} with depth {}", url, depth);
            System.out.println("Crawling url " + url + " with depth " + depth);

            SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(url, timeoutInMillis);
            Future<SingleCrawler> singleCrawlerFuture = executorService.submit(singleCrawlerCallable);
            singleCrawlerFutures.add(singleCrawlerFuture);
        }

        for (Future<SingleCrawler> singleCrawlerFuture : singleCrawlerFutures) {
            SingleCrawler singleCrawler = null;
            try {
                singleCrawler = singleCrawlerFuture.get();
            } catch (ExecutionException e) {
                LOGGER.error("Problem accessing url {}", String.valueOf(singleCrawler.getUrl()));
                System.out.println("Problem accessing url " + String.valueOf(singleCrawler.getUrl()));
            } catch (InterruptedException e) {
                LOGGER.error("Problem accessing url {}", String.valueOf(singleCrawler.getUrl()));
                System.out.println("Problem accessing url " + String.valueOf(singleCrawler.getUrl()));
            }

            Crawler crawler = new Crawler(singleCrawler.getUrl().toString(), singleCrawler.getTitle(), new ArrayList<>());
            return crawlChildURLs(crawler, singleCrawler.getLinks(), depth - 1);
        }

        return null;
    }

    private Crawler crawlChildURLs(Crawler crawler, List<URL> linkUrls, int depth) throws IOException {
        for (URL linkUrl : linkUrls) {
            if (!URLValidator.isValid(linkUrl) || urlSet.contains(linkUrl)) {
                LOGGER.info("Skipping url {}", String.valueOf(linkUrl));
                System.out.println("Skipping url " + String.valueOf(linkUrl));
                continue;
            }
            urlSet.add(linkUrl);
            SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(linkUrl, timeoutInMillis);
            Future<SingleCrawler> singleCrawlerFuture = executorService.submit(singleCrawlerCallable);
            try {
                SingleCrawler singleCrawler = singleCrawlerFuture.get();
                Crawler crawlerChild = new Crawler(singleCrawler.getUrl().toString(), singleCrawler.getTitle(), new ArrayList<>());
                Crawler c = crawlURL(singleCrawler.getLinks(), depth);
                if (c != null) {
                    crawlerChild.getNodes().add(c);
                }

                crawler.getNodes().add(crawlerChild);
            } catch (ExecutionException e) {
                LOGGER.error("Problem accessing url {}, moving on to the next one", String.valueOf(linkUrl));
                continue;
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
        }

        return crawler;
    }

    @Value("${simplewebcrawler.timeout.millis:10000}")
    public void setTimeoutInMillis(int timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    @Value("${simplewebcrawler.max.depth:2}")
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
