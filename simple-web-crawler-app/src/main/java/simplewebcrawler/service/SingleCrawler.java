package simplewebcrawler.service;

import java.net.URL;
import java.util.List;

public class SingleCrawler {
    private URL url;
    private String title;
    private List<URL> links;

    public SingleCrawler(URL url, String title, List<URL> links) {
        this.url = url;
        this.title = title;
        this.links = links;
    }

    public URL getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public List<URL> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return "SingleCrawler{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", links=" + links +
                '}';
    }
}
