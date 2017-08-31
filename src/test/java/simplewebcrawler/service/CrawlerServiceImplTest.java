package simplewebcrawler.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import simplewebcrawler.Crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Jsoup.class})
public class CrawlerServiceImplTest {
    private static final String TEST_URL = "http://www.mysite.com/";
    private static final String TEST_TITLE = "My title";
    private static final List<String> TEST_LINKS = Arrays.asList("http://www.link1.com/", "http://www.link2.com/");

    @Mock
    private Connection mockConnection;

    @Mock
    private Document mockDocument;

    @InjectMocks
    private CrawlerServiceImpl crawlService;

    private Elements titles;

    private Elements links;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        // TODO improve list insertions
        List<Element> titleList = new ArrayList<>();
        Element title = new Element("title");
        title.appendText(TEST_TITLE);
        titleList.add(title);
        titles = new Elements(titleList);

        List<Element> linkList = new ArrayList<>();
        TEST_LINKS.forEach(testLink -> {
            Attributes attributes = new Attributes();
            attributes.put(new Attribute("href", testLink));
            Element link = new Element(Tag.valueOf("a"), "", attributes);
            linkList.add(link);
        });
        links = new Elements(linkList);

        when(mockDocument.select("title")).thenReturn(titles);
        when(mockDocument.select("a[href]")).thenReturn(links);
        when(mockConnection.get()).thenReturn(mockDocument);
        mockStatic(Jsoup.class);
        PowerMockito.when(Jsoup.connect(TEST_URL)).thenReturn(mockConnection);
    }

    @Test
    public void shouldCrawlUrl() throws Exception {
        Crawler crawler = crawlService.crawlURL(TEST_URL);
        System.out.println(crawler);

        assertThat(crawler.getUrl(), is(TEST_URL));
        assertThat(crawler.getTitle(), is(TEST_TITLE));
        assertThat(crawler.getNodes().size(), is(2));
    }
}