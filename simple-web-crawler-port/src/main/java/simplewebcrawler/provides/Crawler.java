package simplewebcrawler.provides;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class Crawler {
    private String url;
    private String title;
    private List<Crawler> nodes;

    public Crawler() {
    }

    public Crawler(String url, String title, List<Crawler> nodes) {
        this.url = url;
        this.title = title;
        this.nodes = nodes;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public List<Crawler> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
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
