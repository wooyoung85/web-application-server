package controller;

import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import model.User;

public class LoginController extends AbstractController {
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        if (request.getQueryString() != null) {
            String userId = request.getQueryString().get("userId");
            String password = request.getQueryString().get("password");
            User loginUser = findUser(userId, password);

            if(loginUser != null){
                HttpSession session = request.getSession();
                session.setAttribute("user", loginUser);
                response.sendRedirect("/index.html");
            }
            else{
                response.sendRedirect("/user/login_failed.html");
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
