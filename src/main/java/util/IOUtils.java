package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class IOUtils {
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return urlDecode(String.copyValueOf(body));
    }

    public static String urlDecode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "UTF-8");
    }
}
