package http;

import http.HttpMethod;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private HttpMethod method;
    private String path;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        this.method = HttpMethod.valueOf(tokens[0]);
        this.path = tokens[1];

        if(tokens.length != 3){
            throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
        }

        if (method.isGet() && this.path.contains("?")) {
            String requestUrl = this.path;
            int questionIndex = requestUrl.indexOf("?");
            this.path = requestUrl.substring(0, questionIndex);
            this.params = HttpRequestUtils.parseQueryString(requestUrl.substring(questionIndex + 1));
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
