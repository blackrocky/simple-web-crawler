package simplewebcrawler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlerController {

    @RequestMapping("/crawl")
    public HttpEntity<Crawler> crawl(
            @RequestParam(value = "url") String url) {

        Crawler crawler = new Crawler(url, "there", null);

        return new ResponseEntity<>(crawler, HttpStatus.OK);
    }
}
