package simplewebcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class SingleCrawlerCallable implements Callable<SingleCrawler> {
    private final static Logger LOGGER = LoggerFactory.getLogger(SingleCrawlerCallable.class);

    private int timeoutInMillis;
    private URL url;

    public SingleCrawlerCallable(URL url, int timeoutInMillis) {
        this.url = url;
        this.timeoutInMillis = timeoutInMillis;
    }

    @Override
    public SingleCrawler call() throws Exception {
        if (url == null) {
            LOGGER.error("url must not be null");
            throw new IllegalStateException("url must not be null");
        }
        if (!url.getProtocol().startsWith("http")) {
            LOGGER.error("url is not http or https {}", url);
            System.out.println("url is not http or https " + url);
            throw new IllegalStateException("url is not http or https " + url);
        }
        LOGGER.info("Crawling url {} with timeout {} milliseconds", url.toString(), timeoutInMillis);
        System.out.println(String.format("Crawling url %s with timeout %d milliseconds", url.toString(), timeoutInMillis));

        Document document = null;
        try {
            document = Jsoup.parse(url, timeoutInMillis);
        } catch (IOException e) {
            throw e;
        }

        Elements titles = document.select("title");
        Elements links = document.select("a[href]");
        List<URL> linkList = new ArrayList<>();
        for (Element link : links) {
            String linkString = link.attr("abs:href");
            try {
                linkList.add(new URL(linkString));
            } catch (MalformedURLException e) {
                LOGGER.info("skipping url: {}", linkString);
                continue;
            }
        }

        return new SingleCrawler(url, titles == null || titles.size() == 0 ? "" : titles.get(0).text(), linkList);
    }
}
