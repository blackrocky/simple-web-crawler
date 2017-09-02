package simplewebcrawler.validator;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class URLStringValidatorTest {
    private URLStringValidator urlStringValidator;

    @Before
    public void setUp() {
        urlStringValidator = new URLStringValidator();
    }

    @Test
    public void shouldReturnFalseWhenURLIsNullOrBlank() throws Exception {
        assertThat(urlStringValidator.isValid(null), is(false));
        assertThat(urlStringValidator.isValid(""), is(false));
    }

    @Test
    public void shouldReturnFalseWhenURLEndsWithHash() throws Exception {
        assertThat(urlStringValidator.isValid("http://www.mywebsite.com/browse#"), is(false));
        assertThat(urlStringValidator.isValid("http://mysite.com/"), is(true));
    }

    @Test
    public void shouldReturnFalseWhenURLHasBadExtension() throws Exception {
        assertThat(urlStringValidator.isValid("http://www.mywebsite.com/evil.js"), is(false));
        assertThat(urlStringValidator.isValid("http://www.mywebsite.com/evil.php"), is(false));
        assertThat(urlStringValidator.isValid("http://www.mywebsite.com/evil.exe"), is(false));
        assertThat(urlStringValidator.isValid("http://www.mywebsite.com/evil.bat"), is(false));
        assertThat(urlStringValidator.isValid("http://www.mywebsite.com/evil.sh"), is(false));
        assertThat(urlStringValidator.isValid("http://www.mywebsite.com/evil.ksh"), is(false));
        assertThat(urlStringValidator.isValid("http://www.mywebsite.com/evil.php3"), is(false));
        assertThat(urlStringValidator.isValid("http://www.mywebsite.com/evil.dll"), is(false));
    }
}