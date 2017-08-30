package simplewebcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CrawlerController {

    @RequestMapping("/crawl")
    @ResponseBody
    public HttpEntity<List<Crawler>> crawl(@RequestParam(value = "url") String url) throws IOException {
        List<Crawler> crawlerList = new ArrayList<>();

        Document document = Jsoup.connect(url).get();
        Element title = document.select("title").get(0);
        crawlerList.add(new Crawler(url, title.text(), null));

        return new ResponseEntity<>(crawlerList, HttpStatus.OK);
    }
}
