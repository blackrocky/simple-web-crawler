package simplewebcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplewebcrawler.validator.URLValidator;

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
        if (!URLValidator.isValid(url)) {
            LOGGER.error("url is not valid");
            throw new IllegalStateException("url not valid");
        }
        LOGGER.info("Crawling url {}", url.toString());

        Document document = null;
        try {
            document = Jsoup.parse(url, timeoutInMillis);
        } catch (IOException e) {
            LOGGER.error("Problem accessing url {}", String.valueOf(url));
            throw new IOException("Problem accessing url " + String.valueOf(url));
        }

        Elements titles = document.select("title");
        Elements links = document.select("a[href]");
        List<URL> linkList = new ArrayList<>();
        for (Element link : links) {
            String linkString = link.attr("abs:href");
            try {
                URL linkURL = new URL(linkString);
                linkList.add(linkURL);
            } catch (MalformedURLException e) {
                LOGGER.info("skipping url: {}", linkString);
                continue;
            }
        }

        return new SingleCrawler(url, titles == null || titles.size() == 0 ? "" : titles.get(0).text(), linkList);
    }
}
