package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import http.HttpMethod;
import http.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private HttpMethod method;
    private String url;
    private Map<String, String> header = new HashMap<>();
    private String host;
    private int contentLength;
    private String body;
    private Map<String, String> queryString = new HashMap<>();
    private Map<String, String> cookieValue = new HashMap<>();

    public HttpRequest(InputStream is) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            RequestLine requestLine = new RequestLine(line);
            this.method = requestLine.getMethod();
            this.url = requestLine.getPath();
            this.queryString = requestLine.getParams();

            // header 추출
            while (!line.equals("")) {
                line = br.readLine();
                int seperaterIdx = line.indexOf(":");
                if (seperaterIdx != -1) {
                    this.header.put(line.substring(0, seperaterIdx).trim(), line.substring(seperaterIdx + 1).trim());
                }
            }

            // cookie 추출
            String cookies = getHeaderField("Cookie");

            if (cookies != null && !cookies.equals("")) {
                cookieValue = HttpRequestUtils.parseCookies(cookies);
            }

            this.host = getHeaderField("Host");

            // contentLength, body 추출
            if (this.method.isPost()) {
                this.contentLength = Integer.parseInt(getHeaderField("Content-Length"));
                this.body = IOUtils.readData(br, contentLength);
                this.queryString = HttpRequestUtils.parseQueryString(body);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getUrl() {
        return this.url;
    }

    public Map<String, String> getHeader() {
        return this.header;
    }

    public String getHeaderField(String fieldName) {
        return this.header.get(fieldName);
    }

    public String getHost() {
        return this.host;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, String> getQueryString() {
        return this.queryString;
    }

    public String getParameter(String key) {
        return this.queryString.get(key);
    }

    public String getCookieValue(String key) {
        return this.cookieValue.get(key);
    }

}
