package simplewebcrawler;

public class Crawler {
    private String url;
    private String title;
    private Crawler crawler;

//    @JsonCreator
//    public Crawler(@JsonProperty("url") String url, @JsonProperty("title") String title, @JsonProperty("crawler") Crawler crawler) {
//        this.url = url;
//        this.title = title;
//        this.crawler = crawler;
//    }

    public Crawler(String url, String title, Crawler crawler) {
        this.url = url;
        this.title = title;
        this.crawler = crawler;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public Crawler getCrawler() {
        return crawler;
    }
}
