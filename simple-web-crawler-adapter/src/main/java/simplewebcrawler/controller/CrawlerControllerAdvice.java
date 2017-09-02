package simplewebcrawler.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.MalformedURLException;

@ControllerAdvice
public class CrawlerControllerAdvice {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerControllerAdvice.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {IllegalStateException.class, MalformedURLException.class})
    protected void handleUrlError(Exception e) {
        LOGGER.warn("bad request: ", e);
    }
}
