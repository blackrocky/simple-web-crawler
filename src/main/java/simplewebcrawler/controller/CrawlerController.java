package simplewebcrawler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import simplewebcrawler.Crawler;
import simplewebcrawler.service.CrawlerService;

import java.io.IOException;

@RestController
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @RequestMapping("/crawl")
    @ResponseBody
    public HttpEntity<Crawler> crawl(@RequestParam(value = "url") String url) throws IOException {
        Crawler crawler = crawlerService.crawlURL(url);

        return new ResponseEntity<>(crawler, HttpStatus.OK);
    }
}