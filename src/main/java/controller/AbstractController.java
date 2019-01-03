package controller;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

public class AbstractController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod requestMethod = request.getMethod();

        if(requestMethod.isGet()){
            doGet(request, response);
        }
        else if(requestMethod.isPost()){
            doPost(request, response);
        }

    }

    public void doPost(HttpRequest request, HttpResponse response) {

    }

    public void doGet(HttpRequest request, HttpResponse response) {

    }
}
