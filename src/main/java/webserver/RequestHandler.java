package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.HttpRequest;
import model.User;

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

            response.setHost(request.getHost());

            if (request.getUrl().equals("/user/login")) {
                // User 로그인 처리
                if (request.getQueryString() != null) {
                    String userId = request.getQueryString().get("userId");
                    String password = request.getQueryString().get("password");

                    User loginUser = findUser(userId, password);

                    // 기존 로그인 정보 가져오기
                    boolean requestCookieLogined = Boolean.parseBoolean(request.getCookieValue("login"));

                    // 기존 로그인 정보가 없는 경우 로그인 프로세스 진행
                    if (!requestCookieLogined) {
                        if (loginUser != null) {
                            response.setCookieValue("logined=true");
                            response.sendRedirect("/index.html");
                        } else {
                            response.setCookieValue("logined=false");
                            response.sendRedirect("/user/login_failed.html");
                        }
                    }
                }
            } else if (request.getUrl().equals("/user/create")) {
                // User정보 저장
                if (request.getQueryString() != null) {
                    setUser(request.getQueryString());
                    response.sendRedirect("/index.html");
                }
            } else {
                response.forward(request.getUrl());
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private User setUser(Map<String, String> queryStrings) {
        String userId, password, name, email;
        User user = new User("", "", "", "");

        userId = queryStrings.get("userId").toString();
        password = queryStrings.get("password").toString();
        name = queryStrings.get("name").toString();
        email = queryStrings.get("email").toString();
        user = new User(userId, password, name, email);
        log.info(user.toString());
        return user;
    }

    private User findUser(String userId, String password) {
        List<User> userList = User.getUserList();
        User findedUser = null;
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(userId) && userList.get(i).getPassword().equals(password)) {
                findedUser = userList.get(i);
            }
        }
        return findedUser;
    }


}
