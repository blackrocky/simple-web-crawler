package simplewebcrawler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import simplewebcrawler.CrawlerPort;
import simplewebcrawler.provides.Crawler;
import simplewebcrawler.validator.URLValidator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrawlerServiceImpl implements CrawlerPort {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);
    private Map<URL, List<Crawler>> masterMap = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();

    private int timeoutInMillis;
    private int maxDepth;

    @Override
    public Crawler crawlURL(URL rootUrl) throws IOException {
        if (!URLValidator.isValid(rootUrl)) {
            LOGGER.warn("rootUrl must be valid");
            throw new IllegalStateException();
        }

        try {
            buildMap(rootUrl, maxDepth);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Crawler rootCrawler = constructRootCrawler(rootUrl);

        return rootCrawler;
    }

    private void buildMap(URL url, int depth) throws MalformedURLException, ExecutionException, InterruptedException {
        if (depth <= 0) {
            return;
        }

        List<Crawler> children = buildChildren(url);
        masterMap.putIfAbsent(url, children);

        for (Crawler child : children) {
            buildMap(new URL(child.getUrl()), depth - 1);
        }
    }

    private List<Crawler> buildChildren(URL url) throws ExecutionException, InterruptedException {
        SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(url, timeoutInMillis);
        SingleCrawler singleCrawler = singleExecutorService.submit(singleCrawlerCallable).get();

        List<Crawler> crawlers = new ArrayList<>();
        for (URL link : singleCrawler.getLinks()) {
            SingleCrawlerCallable titleCallable = new SingleCrawlerCallable(link, timeoutInMillis);
            String title = singleExecutorService.submit(titleCallable).get().getTitle();

            crawlers.add(new Crawler(String.valueOf(link), title, new ArrayList<>()));
        }
        return crawlers;
    }

    private Crawler constructRootCrawler(URL url) throws MalformedURLException {
        Crawler rootCrawler = new Crawler(String.valueOf(url), "My root title", new ArrayList()); // TODO remove hardcoded value

        for (URL keyUrl : masterMap.keySet()) {
            List<Crawler> crawlers = masterMap.get(keyUrl);
            for (Crawler crawler : crawlers) {
                List<Crawler> cs = masterMap.get(new URL(crawler.getUrl()));
                crawler.getNodes().addAll(cs);
            }
            rootCrawler.getNodes().addAll(crawlers);

        }

        return rootCrawler;
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
