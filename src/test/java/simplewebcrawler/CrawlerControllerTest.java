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
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CrawlerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Gson gson = new Gson();

    @Test
    public void shouldReturnCorrectResultGivenCorrectUrl() throws Exception {
        String url = "http://www.bigmusicshop.com.au/";
        String title = "Big Music Shop - Australia\u0027s #1 Music Store - Big Music Australia";

        List<Crawler> expectedResult = new ArrayList<>();
        expectedResult.add(new Crawler(url, title, null));

        String jsonContent = gson.toJson(expectedResult, TypeToken.getParameterized(ArrayList.class, Crawler.class).getType());
        System.out.println(jsonContent);

        mockMvc.perform(get(String.format("/crawl?url=%s", url)))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonContent));
    }

    @Test
    public void shouldReturn4xxWhenUrlIsNotSupplied() throws Exception {
        mockMvc.perform(get("/crawl"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturn5xxWhenSuppliedUrldIsInvalid() throws Exception {
    }

    @Test
    public void shouldEncodeUrl() throws Exception {
    }

}