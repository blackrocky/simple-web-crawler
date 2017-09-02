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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplewebcrawler.Crawler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Jsoup.class, LoggerFactory.class})
public class CrawlerServiceImplTest {
    private static final String ROOT_URL = "http://www.mysite.com/";
    private static final String ROOT_TITLE = "My root title";
    private static final String LINK_1_1_URL = "http://www.mysite.com/link11";
    private static final String LINK_1_2_URL = "http://www.mysite.com/link12";
    private static final String LINK_1_1_TITLE = "My ONE ONE title";
    private static final String LINK_1_2_TITLE = "My ONE TWO title";
    private static final List<String> ROOT_LINKS = asList(LINK_1_1_URL, LINK_1_2_URL);
    private static final String LINK_1_1_1_URL = "http://www.mysite.com/link11/link111";
    private static final String LINK_1_1_1_TITLE = "My ONE ONE ONE title";
    private static final List<String> LINK_1_1_LINKS = singletonList(LINK_1_1_1_URL);
    private static final int TIMEOUT = 20;

    @Mock
    private Connection mockRootConnection;

    @Mock
    private Document mockRootDocument;

    @Mock
    private Connection mock_1_1_Connection;

    @Mock
    private Document mock_1_1_Document;

    @Mock
    private Connection mock_1_2_Connection;

    @Mock
    private Connection mock_1_1_1_Connection;

    @Mock
    private Document mock_1_1_1_Document;

    @Mock
    private Document mock_1_2_Document;

    private static Logger mockLogger;

    @InjectMocks
    private CrawlerServiceImpl crawlService;

    @BeforeClass
    public static void setUpLogger() {
        mockStatic(LoggerFactory.class);
        mockLogger = PowerMockito.mock(Logger.class);
        PowerMockito.when(LoggerFactory.getLogger(any(Class.class))).thenReturn(mockLogger);
    }

    @Before
    public void setUp() throws IOException {
        initMocks(this);
        crawlService.setTimeout(TIMEOUT);
    }

    @Test
    public void shouldCrawlUrlWithDepthOf1() throws Exception {
        when(mockRootDocument.select("title")).thenReturn(setUpTitle(ROOT_TITLE));
        when(mockRootDocument.select("a[href]")).thenReturn(setUpLinks(ROOT_LINKS));

        when(mock_1_1_Document.select("title")).thenReturn(setUpTitle(LINK_1_1_TITLE));
        when(mock_1_1_Document.select("a[href]")).thenReturn(new Elements());

        when(mock_1_2_Document.select("title")).thenReturn(setUpTitle(LINK_1_2_TITLE));
        when(mock_1_2_Document.select("a[href]")).thenReturn(new Elements());

        mockStatic(Jsoup.class);
        PowerMockito.when(Jsoup.parse(new URL(ROOT_URL), TIMEOUT)).thenReturn(mockRootDocument);
        PowerMockito.when(Jsoup.parse(new URL(LINK_1_1_URL), TIMEOUT)).thenReturn(mock_1_1_Document);
        PowerMockito.when(Jsoup.parse(new URL(LINK_1_2_URL), TIMEOUT)).thenReturn(mock_1_2_Document);

        Crawler crawler = crawlService.crawlURL(new URL(ROOT_URL));

        assertThat(crawler.getUrl(), is(ROOT_URL));
        assertThat(crawler.getTitle(), is(ROOT_TITLE));
        assertThat(crawler.getNodes().size(), is(2));

        assertThat(crawler.getNodes().get(0).getUrl(), is(LINK_1_1_URL));
        assertThat(crawler.getNodes().get(0).getTitle(), is(LINK_1_1_TITLE));
        assertThat(crawler.getNodes().get(0).getNodes().size(), is(0));

        assertThat(crawler.getNodes().get(1).getUrl(), is(LINK_1_2_URL));
        assertThat(crawler.getNodes().get(1).getTitle(), is(LINK_1_2_TITLE));
        assertThat(crawler.getNodes().get(1).getNodes().size(), is(0));
    }

    @Test
    public void shouldCrawlUrlWithDepthOf2() throws Exception {
        when(mockRootDocument.select("title")).thenReturn(setUpTitle(ROOT_TITLE));
        when(mockRootDocument.select("a[href]")).thenReturn(setUpLinks(ROOT_LINKS));

        when(mock_1_1_Document.select("title")).thenReturn(setUpTitle(LINK_1_1_TITLE));
        when(mock_1_1_Document.select("a[href]")).thenReturn(setUpLinks(LINK_1_1_LINKS));

        when(mock_1_1_1_Document.select("title")).thenReturn(setUpTitle(LINK_1_1_1_TITLE));
        when(mock_1_1_1_Document.select("a[href]")).thenReturn(new Elements());

        when(mock_1_2_Document.select("title")).thenReturn(setUpTitle(LINK_1_2_TITLE));
        when(mock_1_2_Document.select("a[href]")).thenReturn(new Elements());

        mockStatic(Jsoup.class);
        PowerMockito.when(Jsoup.parse(new URL(ROOT_URL), TIMEOUT)).thenReturn(mockRootDocument);
        PowerMockito.when(Jsoup.parse(new URL(LINK_1_1_URL), TIMEOUT)).thenReturn(mock_1_1_Document);
        PowerMockito.when(Jsoup.parse(new URL(LINK_1_1_1_URL), TIMEOUT)).thenReturn(mock_1_1_1_Document);
        PowerMockito.when(Jsoup.parse(new URL(LINK_1_2_URL), TIMEOUT)).thenReturn(mock_1_2_Document);

        Crawler crawler = crawlService.crawlURL(new URL(ROOT_URL));

        assertThat(crawler.getUrl(), is(ROOT_URL));
        assertThat(crawler.getTitle(), is(ROOT_TITLE));
        assertThat(crawler.getNodes().size(), is(2));

        assertThat(crawler.getNodes().get(0).getUrl(), is(LINK_1_1_URL));
        assertThat(crawler.getNodes().get(0).getTitle(), is(LINK_1_1_TITLE));
        assertThat(crawler.getNodes().get(0).getNodes().size(), is(1));

        assertThat(crawler.getNodes().get(0).getNodes().get(0).getUrl(), is(LINK_1_1_1_URL));
        assertThat(crawler.getNodes().get(0).getNodes().get(0).getTitle(), is(LINK_1_1_1_TITLE));
        assertThat(crawler.getNodes().get(0).getNodes().get(0).getNodes().size(), is(0));

        assertThat(crawler.getNodes().get(1).getUrl(), is(LINK_1_2_URL));
        assertThat(crawler.getNodes().get(1).getTitle(), is(LINK_1_2_TITLE));
        assertThat(crawler.getNodes().get(1).getNodes().size(), is(0));
    }

    @Test
    public void shouldStopCrawlingWhenRootUrlIsNotAccessible() throws Exception {
        mockStatic(Jsoup.class);
        PowerMockito.when(Jsoup.parse(new URL(ROOT_URL), TIMEOUT)).thenThrow(new IOException());

        try {
            Crawler crawler = crawlService.crawlURL(new URL(ROOT_URL));
            fail("expected to throw IOException");
        } catch (IOException expected) {
            verify(mockLogger).error("Problem accessing url {}", ROOT_URL);
        }
    }

    @Test
    public void shouldContinueCrawlingWhenOneOfTheLinksIsNotAccessible() throws Exception {
        when(mockRootDocument.select("title")).thenReturn(setUpTitle(ROOT_TITLE));
        when(mockRootDocument.select("a[href]")).thenReturn(setUpLinks(ROOT_LINKS));

        when(mock_1_2_Document.select("title")).thenReturn(setUpTitle(LINK_1_2_TITLE));
        when(mock_1_2_Document.select("a[href]")).thenReturn(new Elements());
        when(mock_1_2_Connection.get()).thenReturn(mock_1_2_Document);

        mockStatic(Jsoup.class);
        PowerMockito.when(Jsoup.parse(new URL(ROOT_URL), TIMEOUT)).thenReturn(mockRootDocument);
        PowerMockito.when(Jsoup.parse(new URL(LINK_1_1_URL), TIMEOUT)).thenThrow(new IOException());
        PowerMockito.when(Jsoup.parse(new URL(LINK_1_2_URL), TIMEOUT)).thenReturn(mock_1_2_Document);

        Crawler crawler = crawlService.crawlURL(new URL(ROOT_URL));

        assertThat(crawler.getUrl(), is(ROOT_URL));
        assertThat(crawler.getTitle(), is(ROOT_TITLE));
        assertThat(crawler.getNodes().size(), is(1));

        assertThat(crawler.getNodes().get(0).getUrl(), is(LINK_1_2_URL));
        assertThat(crawler.getNodes().get(0).getTitle(), is(LINK_1_2_TITLE));
        assertThat(crawler.getNodes().get(0).getNodes().size(), is(0));

        verify(mockLogger).error("Problem accessing url {}, moving on to the next one", LINK_1_1_URL);
    }

    @Test
    public void shouldHandleBlankTitle() throws Exception {
        when(mockRootDocument.select("title")).thenReturn(new Elements());
        when(mockRootDocument.select("a[href]")).thenReturn(setUpLinks(ROOT_LINKS));

        when(mock_1_1_Document.select("title")).thenReturn(setUpTitle(LINK_1_1_TITLE));
        when(mock_1_1_Document.select("a[href]")).thenReturn(new Elements());

        when(mock_1_2_Document.select("title")).thenReturn(setUpTitle(LINK_1_2_TITLE));
        when(mock_1_2_Document.select("a[href]")).thenReturn(new Elements());

        mockStatic(Jsoup.class);
        PowerMockito.when(Jsoup.parse(new URL(ROOT_URL), TIMEOUT)).thenReturn(mockRootDocument);
        PowerMockito.when(Jsoup.parse(new URL(LINK_1_1_URL), TIMEOUT)).thenReturn(mock_1_1_Document);
        PowerMockito.when(Jsoup.parse(new URL(LINK_1_2_URL), TIMEOUT)).thenReturn(mock_1_2_Document);

        Crawler crawler = crawlService.crawlURL(new URL(ROOT_URL));

        assertThat(crawler.getUrl(), is(ROOT_URL));
        assertThat(crawler.getTitle(), is(""));
        assertThat(crawler.getNodes().size(), is(2));

        assertThat(crawler.getNodes().get(0).getUrl(), is(LINK_1_1_URL));
        assertThat(crawler.getNodes().get(0).getTitle(), is(LINK_1_1_TITLE));
        assertThat(crawler.getNodes().get(0).getNodes().size(), is(0));

        assertThat(crawler.getNodes().get(1).getUrl(), is(LINK_1_2_URL));
        assertThat(crawler.getNodes().get(1).getTitle(), is(LINK_1_2_TITLE));
        assertThat(crawler.getNodes().get(1).getNodes().size(), is(0));
    }

    private Elements setUpLinks(List<String> links) {
        List<Element> linkList = new ArrayList<>();
        links.forEach(testLink -> {
            Attributes attributes = new Attributes();
            attributes.put(new Attribute("href", testLink));
            Element link = new Element(Tag.valueOf("a"), "", attributes);
            linkList.add(link);
        });
        return new Elements(linkList);
    }

    private Elements setUpTitle(String titleText) {
        List<Element> titleElementList = new ArrayList<>();
        Element titleElement = new Element("title");
        titleElement.appendText(titleText);
        titleElementList.add(titleElement);
        return new Elements(titleElementList);
    }
}