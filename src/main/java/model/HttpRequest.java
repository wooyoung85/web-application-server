package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String url;
    private Map<String, String> header;
    private String host;
    private int contentLength;
    private String body;
    private Map<String, String> queryString;
    private Map<String, String> cookieValue = new HashMap<>();

    public HttpRequest(InputStream is) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            // method, url 추출
            String[] requestLine = line.split(" ");
            String querystrings = "";
            this.method = requestLine[0].toString();
            this.url = requestLine[1].toString();

            if (method.equals("GET") && this.url.contains("?")) {
                String requestUrl = this.url;
                int questionIndex = requestUrl.indexOf("?");
                this.url = requestUrl.substring(0, questionIndex);
                querystrings = requestUrl.substring(questionIndex + 1);
            }

            // header 추출
            header = new HashMap<>();
            while (!line.equals("")) {
                line = br.readLine();
                int seperatorIdx = line.indexOf(":");
                if (seperatorIdx != -1) {
                    this.header.put(line.substring(0, seperatorIdx).trim(), line.substring(seperatorIdx + 1).trim());
                }
            }

            // cookie 추출
            String cookies = getHeaderField("Cookie");

            if (cookies != null && !cookies.equals("")) {
                cookieValue = HttpRequestUtils.parseCookies(cookies);
            }

            this.host = getHeaderField("Host");

            // contentLength, body 추출
            if (this.method.equals("POST")) {
                this.contentLength = Integer.parseInt(getHeaderField("Content-Length"));
                this.body = IOUtils.readData(br, contentLength);
            }

            // queryString 추출
            queryString = new HashMap<>();
            if (method.equals("GET")) {
                this.queryString = HttpRequestUtils.parseQueryString(querystrings);
            } else if (method.equals("POST")) {
                this.queryString = HttpRequestUtils.parseQueryString(body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public String getMethod() {
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
