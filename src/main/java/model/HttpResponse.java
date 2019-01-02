package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private DataOutputStream dos = null;
    private String statusCode;
    private Map<String, String> header;
    private byte[] body;
    private String redirectUrl;
    private String host;

    private Boolean cookieWithYN;
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

    public void setCookieWithYN(Boolean cookieWithYN) {
        this.cookieWithYN = cookieWithYN;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }

    public void setCssYN(Boolean cssYN) {
        this.cssYN = cssYN;
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

    private void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");

            if (this.cssYN) {
                dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            } else {
                dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            }
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
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

    private void getResponseBodyContents(String requestPage) {
        try {
            if ("".equals(requestPage) ||  "/".equals(requestPage)) {
                this.body = "Hello World 자바 프로그래밍!".getBytes();
            } else {
                if (requestPage.equals("/user/list")) {
                    List<String> templates = null;

                    templates = Files.readAllLines((new File("./webapp" + requestPage + ".html").toPath()));

                    String template = String.join("", templates);

                    StringBuilder sb = new StringBuilder();
                    List<User> userList = User.getUserList();

                    for (int i = 0; i < userList.size(); i++) {
                        sb.append("<tr>");
                        sb.append("<td> </td>");
                        sb.append("<td>" + userList.get(i).getUserId() + "</td>");
                        sb.append("<td>" + userList.get(i).getName() + "</td>");
                        sb.append("<td>" + userList.get(i).getEmail() + "</td>");
                        sb.append("</tr>");
                    }

                    template = template.replace("##tbodyContents##", sb.toString());
                    this.body = template.getBytes();
                } else {
                    this.body = Files.readAllBytes(new File("./webapp" + requestPage).toPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void response302Header() {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: http://" + host + redirectUrl + "\r\n");
            if(!cookieValue.equals("")){
                dos.writeBytes("Set-Cookie: " + cookieValue + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
