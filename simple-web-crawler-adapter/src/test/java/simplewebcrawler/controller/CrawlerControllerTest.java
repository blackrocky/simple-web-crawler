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


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import simplewebcrawler.CrawlerPort;
import simplewebcrawler.validator.URLStringValidator;

import java.net.URL;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class CrawlerControllerTest {

    private static final String TEST_URL = "http://mysite.com/";

    @Mock
    private CrawlerPort crawlerPort;

    @Mock
    private URLStringValidator urlStringValidator;

    @InjectMocks
    private CrawlerController crawlerController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(crawlerController).build();
    }

    @Test
    public void shouldCallCrawlerPort_GivenCorrectParameter() throws Exception {
        when(urlStringValidator.isValid(TEST_URL)).thenReturn(true);
        mockMvc.perform(get(String.format("/crawl?url=%s", TEST_URL)))
                .andExpect(status().isOk());

        verify(urlStringValidator).isValid(TEST_URL);
        verify(crawlerPort).crawlURL(new URL(TEST_URL));
    }

    @Test
    public void shouldReturnNotFound_AndNotCallPort_GivenIncorrectUrl() throws Exception {
        mockMvc.perform(get(String.format("/crrawl?url=%s", TEST_URL)))
                .andExpect(status().isNotFound());
        verifyZeroInteractions(urlStringValidator);
        verifyZeroInteractions(crawlerPort);
    }

    @Test
    public void shouldReturnBadRequest_AndNotCallPort_GivenIncorrectParameter() throws Exception {
        mockMvc.perform(get(String.format("/crawl?urrl=%s", TEST_URL)))
                .andExpect(status().isBadRequest());
        verifyZeroInteractions(urlStringValidator);
        verifyZeroInteractions(crawlerPort);
    }
}