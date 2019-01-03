package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private DataOutputStream dos = null;
    private String statusCode;
    private Map<String, String> header = new HashMap<>();
    private byte[] body;
    private String redirectUrl;
    private String host;
    private String cookieValue = "";
    private Boolean cssYN = false;

    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue += cookieValue + ";";
    }

    public void addHeader(String key, String value){
        header.put(key, value);
    }

    public void forward(String url) {
        //body 구성하기
        getResponseBodyContents(url);

        // header 구성하기
        response200Header();

        // response flush
        responseBody();
    }

    public void sendRedirect(String url) {
        //body 구성하기
        getResponseBodyContents(url);
        this.redirectUrl = url;

        // header 구성하기
        response302Header();

        // response flush
        responseBody();
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    private void getResponseBodyContents(String requestPage) {
        if(requestPage.endsWith(".css")){
            this.cssYN = true;
        }

        if(body == null){
            try {
                if ("".equals(requestPage) ||  "/".equals(requestPage)) {
                    this.body = "Hello World 자바 프로그래밍!".getBytes();
                } else {
                    this.body = Files.readAllBytes(new File("./webapp" + requestPage).toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");

            if (this.cssYN) {
                header.put("Content-Type", "text/css;charset=utf-8");
            } else {
                header.put("Content-Type", "text/html;charset=utf-8");
            }
            header.put("Content-Length", String.valueOf(body.length));
            processHeader();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header() {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            header.put("Location", "http://" + host +redirectUrl);
            if(!cookieValue.equals("")){
                header.put("Set-Cookie", cookieValue);
            }
            processHeader();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody() {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processHeader() throws IOException {
        for (String key: header.keySet()) {
            dos.writeBytes(key + ": " + header.get(key) + "\r\n");
        }
        dos.writeBytes("\r\n");
    }
}
