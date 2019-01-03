package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private String id;
    private Map<String, Object> attributes = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, Object value){
        attributes.put(name, value);
    }

    public void removeAttribute(String name){
        attributes.remove(name);
    }

    public void invalidate(){
        attributes.clear();
    }
}
