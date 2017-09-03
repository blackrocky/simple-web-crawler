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
import java.util.stream.Collectors;

public class CrawlerServiceImpl implements CrawlerPort {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);
    private Map<URL, List<SingleCrawler>> masterMap = new ConcurrentHashMap<>();
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
        masterMap.clear();
        return rootCrawler;
    }

    private void buildMap(URL url, int depth) throws MalformedURLException, ExecutionException, InterruptedException {
        if (depth <= 0) {
            return;
        }

        List<SingleCrawler> children = buildChildren(url);
        LOGGER.info("****** Added url {}", url);
        masterMap.putIfAbsent(url, children);

        for (SingleCrawler child : children) {
            buildMap(child.getUrl(), depth - 1);
        }
    }

    private List<SingleCrawler> buildChildren(URL url) throws ExecutionException, InterruptedException {
        SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(url, timeoutInMillis);
        SingleCrawler singleCrawler = singleExecutorService.submit(singleCrawlerCallable).get();

        List<SingleCrawler> crawlers = new ArrayList<>();
        crawlers.add(singleCrawler);

        for (URL link : singleCrawler.getLinks()) {
            crawlers.add(new SingleCrawler(link, "test Title", new ArrayList<>())); // TODO remove hardcoded value
        }


        return crawlers;
    }

    private Crawler constructRootCrawler(URL url) throws MalformedURLException {
        LOGGER.info("number of keys: {}", masterMap.keySet().size());

        Crawler rootCrawler = new Crawler(String.valueOf(url), "My root title", new ArrayList<>()); // TODO remove hardcoded value
        List<Crawler> crawlers = new ArrayList<>();

        for (URL keyUrl : masterMap.keySet()) {
            List<SingleCrawler> singleCrawlers = masterMap.get(keyUrl);
            Crawler crawler = new Crawler(String.valueOf(keyUrl), "my key title", new ArrayList<>());
            List<Crawler> childCrawlers = new ArrayList<>();
            for (SingleCrawler singleCrawler : singleCrawlers) {
                Crawler childCrawler = new Crawler(String.valueOf(singleCrawler.getUrl()), singleCrawler.getTitle(), new ArrayList());

                List<SingleCrawler> cs = masterMap.get(singleCrawler.getUrl());
                if (cs != null) {
                    childCrawler.getNodes()
                            .addAll(cs.stream()
                                    .filter(c -> URLValidator.isValid(c.getUrl()))
                                    .map(c -> mapSingleCrawlerToCrawler(c)).collect(Collectors.toList())
                            );
                }

                childCrawlers.add(childCrawler);
            }
            LOGGER.info("childCrawler size {}", childCrawlers.size());

            if (singleCrawlers != null) {
                crawler.getNodes()
                        .addAll(singleCrawlers.stream()
                                .filter(c -> URLValidator.isValid(c.getUrl()))
                                .map(c -> mapSingleCrawlerToCrawler(c)).collect(Collectors.toList()));
            }

            if (!crawler.getUrl().equalsIgnoreCase(String.valueOf(url))) {
                crawlers.add(crawler);
            }
        }
        LOGGER.info("crawlers size {}", crawlers.size());

        rootCrawler.getNodes().addAll(crawlers);
//        rootCrawler.getNodes()
//                .addAll(masterMap.keySet().stream()
//                        .filter(c -> URLValidator.isValid(c))
//                        .map(c -> mapUrlToCrawler(c))
//                        .collect(Collectors.toList()));
//

        LOGGER.info("root crawler has {} children", rootCrawler.getNodes().size());
        return rootCrawler;
    }

    private Crawler mapSingleCrawlerToCrawler(SingleCrawler singleCrawler) {
        return new Crawler(String.valueOf(singleCrawler.getUrl()), singleCrawler.getTitle(), new ArrayList());
    }

    private Crawler mapUrlToCrawler(URL url) {
        return new Crawler(String.valueOf(url), "my title", new ArrayList());
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
