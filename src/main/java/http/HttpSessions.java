package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions
{
    private static Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getHttpSession(String id) {
        HttpSession session = sessions.get(id);
        if(session == null){
            session = new HttpSession(id);
            sessions.put(id, session);
        }
        return session;
    }

    public void setHttpSessions(String id, HttpSession httpSession) {
        sessions.put(id, httpSession);
    }

    public void remove(String id){
        sessions.remove(id);
    }
}
