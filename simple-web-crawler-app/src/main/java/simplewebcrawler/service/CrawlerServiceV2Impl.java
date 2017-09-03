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

public class CrawlerServiceV2Impl implements CrawlerPort {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceV2Impl.class);
    private Map<SingleCrawler, List<SingleCrawler>> masterMap = new ConcurrentHashMap<>();
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
            SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(rootUrl, timeoutInMillis);
            SingleCrawler singleCrawler = singleExecutorService.submit(singleCrawlerCallable).get();
            buildMap(singleCrawler, maxDepth);

            Crawler rootCrawler = constructRootCrawler(singleCrawler);
            masterMap.clear();
            return rootCrawler;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void buildMap(SingleCrawler singleCrawler, int depth) throws MalformedURLException, ExecutionException, InterruptedException {
        if (depth <= 0) {
            return;
        }

        List<SingleCrawler> children = buildChildren(singleCrawler);
        LOGGER.info("****** Added url {}", singleCrawler);
        masterMap.putIfAbsent(singleCrawler, children);

        for (SingleCrawler child : children) {
            buildMap(child, depth - 1);
        }
    }

    private List<SingleCrawler> buildChildren(SingleCrawler singleCrawler) throws ExecutionException, InterruptedException {
        List<SingleCrawler> crawlers = new ArrayList<>();
        crawlers.add(singleCrawler);

        for (URL link : singleCrawler.getLinks()) {
            crawlers.add(new SingleCrawler(link, "My ONE ONE title", new ArrayList<>())); // TODO remove hardcoded value
        }

        return crawlers;
    }

    private Crawler constructRootCrawler(SingleCrawler rootSingleCrawler) throws MalformedURLException {
        LOGGER.info("number of keys: {}", masterMap.keySet().size());

        Crawler rootCrawler = mapSingleCrawlerToCrawler(rootSingleCrawler);
        List<Crawler> crawlers = new ArrayList<>();

        for (SingleCrawler singleCrawlerKey : masterMap.keySet()) {
            List<SingleCrawler> singleCrawlers = masterMap.get(singleCrawlerKey);
            Crawler crawler = new Crawler(String.valueOf(singleCrawlerKey.getUrl()), singleCrawlerKey.getTitle(), new ArrayList<>());
            List<Crawler> childCrawlers = new ArrayList<>();
            for (SingleCrawler singleCrawler : singleCrawlers) {
                Crawler childCrawler = new Crawler(String.valueOf(singleCrawler.getUrl()), singleCrawler.getTitle(), new ArrayList());

                List<SingleCrawler> cs = masterMap.get(singleCrawler);
                if (cs != null) {
                    childCrawler.getNodes()
                            .addAll(cs.stream()
                                    .filter(c -> URLValidator.isValid(c.getUrl()))
                                    .map(c -> mapSingleCrawlerToCrawler(c)).collect(Collectors.toList())
                            );
                }

                if (!childCrawler.getUrl().equalsIgnoreCase(String.valueOf(crawler.getUrl()))) {
                    childCrawlers.add(childCrawler);
                }
            }
            LOGGER.info("childCrawler size {}", childCrawlers.size());

            if (singleCrawlers != null) {
                crawler.getNodes()
                        .addAll(singleCrawlers.stream()
                                .filter(c -> URLValidator.isValid(c.getUrl()))
                                .map(c -> mapSingleCrawlerToCrawler(c)).collect(Collectors.toList()));
            }

            if (!crawler.getUrl().equalsIgnoreCase(String.valueOf(rootSingleCrawler.getUrl()))) {
                crawlers.add(crawler);
            }
        }


        LOGGER.info("crawlers size {}", crawlers.size());

        rootCrawler.getNodes().addAll(crawlers);
        LOGGER.info("root crawler has {} children", rootCrawler.getNodes().size());

        return rootCrawler;
    }

    private Crawler mapSingleCrawlerToCrawler(SingleCrawler singleCrawler) {
        return new Crawler(String.valueOf(singleCrawler.getUrl()), singleCrawler.getTitle(), new ArrayList());
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
