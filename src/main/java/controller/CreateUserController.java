package controller;

import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CreateUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        // User정보 저장
        if (request.getQueryString() != null) {
            createUser(request.getQueryString());
        }

        response.sendRedirect("/index.html");
    }

    private User createUser(Map<String, String> queryStrings) {
        String userId, password, name, email;
        User user;

        userId = queryStrings.get("userId");
        password = queryStrings.get("password");
        name = queryStrings.get("name");
        email = queryStrings.get("email");
        user = new User(userId, password, name, email);
        User.userList.add(new User(userId, password, name, email));
        log.info(user.toString());
        return user;
    }
}
