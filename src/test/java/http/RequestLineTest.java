package http;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestLineTest {

    @Test
    public void create_method(){
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
        assertEquals(HttpMethod.GET, line.getMethod());
        assertEquals("/index.html", line.getPath());
    }

    @Test
    public void creat_path_and_params(){
        RequestLine line = new RequestLine("GET /user/create?userId=test&password=1 HTTP/1.1");
        assertEquals(HttpMethod.GET, line.getMethod());

        Map<String, String> params = new HashMap<>();
        params.put("userId", "test");
        params.put("password", "1");
        assertEquals(params, line.getParams());
    }
}