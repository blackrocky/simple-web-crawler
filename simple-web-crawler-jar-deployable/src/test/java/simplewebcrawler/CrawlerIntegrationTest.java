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

package simplewebcrawler;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import simplewebcrawler.config.AdapterConfig;
import simplewebcrawler.config.CrawlerConfig;

import java.net.URL;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={CrawlerIntegrationTest.TestConfig.class, AdapterConfig.class, CrawlerConfig.class})
@SpringBootTest(classes = MainApplication.class)
@AutoConfigureMockMvc
public class CrawlerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @Autowired
    private CrawlerPort crawlerPort;

    @Test
    public void shouldReturnCorrectResultGivenCorrectUrl() throws Exception {
        String url = "http://www.bigmusicshop.com.au/";

        mockMvc.perform(get(String.format("/crawl?url=%s", url)))
                .andExpect(status().isOk());

        verify(crawlerPort).crawlURL(new URL(url));
    }

    public static class TestConfig {
        @Bean
        @Primary
        public CrawlerPort crawlerPort() {
            return mock(CrawlerPort.class);
        }
    }

}