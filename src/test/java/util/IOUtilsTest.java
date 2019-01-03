package util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class IOUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

    @Test
    public void readData() throws Exception {
        String data = "abcd123";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);

        logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
    }

    @Test
    public void urlDecode() throws UnsupportedEncodingException {
        String str = "wooyoung%40test.com";
        assertEquals("wooyoung@test.com",IOUtils.urlDecode(str));
    }
}
