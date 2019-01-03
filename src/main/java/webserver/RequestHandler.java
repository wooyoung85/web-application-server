package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

import controller.Controller;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.HttpRequest;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            if(request.getCookies().getCookie("JSESSIONID") == null){
                response.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
            }

            response.setHost(request.getHost());

            if(RequestMapper.getControllers().containsKey(request.getUrl())){
                Controller controller = RequestMapper.getControllers().get(request.getUrl());
                controller.service(request, response);
            }
            else{
                response.forward(request.getUrl());
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getSessionID(String cookieValue){
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        return cookies.get("JSESSIONID");
    }
}
