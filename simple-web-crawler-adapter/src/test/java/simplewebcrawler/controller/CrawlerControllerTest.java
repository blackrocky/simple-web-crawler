/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simplewebcrawler.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import simplewebcrawler.CrawlerPort;
import simplewebcrawler.provides.Crawler;

import java.net.URL;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CrawlerControllerTest {

    private static final String TEST_URL = "http://mysite.com/";

    @Mock
    private CrawlerPort crawlerPort;

    @InjectMocks
    private CrawlerController crawlerController;

    @Test
    public void shouldCrawlUrlWithNoLinks() throws Exception {
        when(crawlerPort.crawlURL(new URL(TEST_URL))).thenReturn(new Crawler("http://mysite.com/", "my site", null));
        crawlerController.crawl(TEST_URL);

        verify(crawlerPort).crawlURL(new URL(TEST_URL));
    }
}