package simplewebcrawler.service;

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
import java.util.concurrent.*;

@Service
public class CrawlerServiceImpl implements CrawlerPort {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);
    private Set<URL> urlSet = ConcurrentHashMap.newKeySet();
    private ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private int timeoutInMillis;
    private int maxDepth;

    @Override
    public Crawler crawlURL(URL url) throws IOException {
        Crawler crawler = crawlURL(url, maxDepth);
        LOGGER.info("FINISHED!!! {}", crawler);
        System.out.println("FINISHED!!! " + crawler);
        return crawler;
    }

    private Crawler crawlURL(URL url, int depth) throws IOException {
        LOGGER.info("crawling url {} with depth {}", url, depth);
        SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(url, timeoutInMillis);
        Future<SingleCrawler> root = singleExecutorService.submit(singleCrawlerCallable);
        SingleCrawler singleCrawler = null;
        try {
            singleCrawler = root.get();
        } catch (ExecutionException e) {
            LOGGER.error("Problem accessing url {}", String.valueOf(url));
            System.out.println("Problem accessing url " + String.valueOf(url));
        } catch (InterruptedException e) {
            LOGGER.error("Problem accessing url {}", String.valueOf(url));
            System.out.println("Problem accessing url " + String.valueOf(url));
        }

        urlSet.add(singleCrawler.getUrl());

        Crawler crawler = new Crawler(singleCrawler.getUrl().toString(), singleCrawler.getTitle(), new ArrayList<>());
        return crawlChildURLs(crawler, singleCrawler.getLinks(), depth - 1);
    }

    private Crawler crawlChildURLs(Crawler crawler, List<URL> urls, int depth) throws IOException {
        for (URL url : urls) {
            if (url == null) {
                LOGGER.info("Skipping url {}", String.valueOf(url));
                System.out.println("Skipping url " + String.valueOf(url));
                continue;
            }
            if (!url.getProtocol().startsWith("http")) {
                LOGGER.info("Skipping url {}", String.valueOf(url));
                System.out.println("Skipping url " + String.valueOf(url));
                continue;
            }
            if (urlSet.contains(url)) {
                LOGGER.info("Skipping url {}", String.valueOf(url));
                System.out.println("Skipping url " + String.valueOf(url));
                continue;
            }
            SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(url, timeoutInMillis);
            Future<SingleCrawler> root = executorService.submit(singleCrawlerCallable);
            SingleCrawler singleCrawler = null;
            try {
                singleCrawler = root.get();
                urlSet.add(singleCrawler.getUrl());
            } catch (ExecutionException e) {
                LOGGER.error("Problem accessing url {}, moving on to the next one", String.valueOf(url));
                continue;
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            Crawler crawlerChild = new Crawler(singleCrawler.getUrl().toString(), singleCrawler.getTitle(), new ArrayList<>());
//            if (depth <= 0) {
//                continue;
//            } else {
//                crawlerChild.getNodes().add(crawlChildURLs(crawlerChild, singleCrawler.getLinks(), depth - 1));
//            }

            for (URL hrefUrl : singleCrawler.getLinks()) {
                if (depth <= 0) {
                    continue;
                }
                if (hrefUrl != null && hrefUrl.getProtocol().startsWith("http") && !urlSet.contains(hrefUrl)) {
                        crawlerChild.getNodes().add(crawlURL(hrefUrl, depth));
                }
            }

            crawler.getNodes().add(crawlerChild);
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
