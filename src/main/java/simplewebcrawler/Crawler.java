package simplewebcrawler;

import java.util.List;

public class Crawler {
    private String url;
    private String title;
    private List<Crawler> nodes;

    public Crawler(String url, String title, List<Crawler> nodes) {
        this.url = url;
        this.title = title;
        this.nodes = nodes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Crawler> getNodes() {
        return nodes;
    }

    public void setNodes(List<Crawler> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "Crawler{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
