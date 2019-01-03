package controller;

import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ListUserController extends AbstractController {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if(isLogin(request)) {
            List<String> templates = null;

            try {
                templates = Files.readAllLines((new File("./webapp" + request.getUrl() + ".html").toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String template = String.join("", templates);

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < User.userList.size(); i++) {
                sb.append("<tr>");
                sb.append("<td> </td>");
                sb.append("<td>" + User.userList.get(i).getUserId() + "</td>");
                sb.append("<td>" + User.userList.get(i).getName() + "</td>");
                sb.append("<td>" + User.userList.get(i).getEmail() + "</td>");
                sb.append("</tr>");
            }

            template = template.replace("##tbodyContents##", sb.toString());
            response.setBody(template.getBytes());

            response.forward("/user/list");
        }
        else{
            response.sendRedirect("/index.html");
        }
    }

    private boolean isLogin(HttpRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if(user == null){
            return false;
        }
        return true;
    }
}
