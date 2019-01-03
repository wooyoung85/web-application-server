package controller;

import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.util.List;

public class LoginController extends AbstractController {
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        if (request.getQueryString() != null) {
            String userId = request.getQueryString().get("userId");
            String password = request.getQueryString().get("password");
            User loginUser = findUser(userId, password);
            boolean requestCookieLogined = Boolean.parseBoolean(request.getCookieValue("login"));

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
    }

    private User findUser(String userId, String password) {
        User findedUser = null;
        for (int i = 0; i < User.userList.size(); i++) {
            if (User.userList.get(i).getUserId().equals(userId) && User.userList.get(i).getPassword().equals(password)) {
                findedUser = User.userList.get(i);
            }
        }
        return findedUser;
    }
}
