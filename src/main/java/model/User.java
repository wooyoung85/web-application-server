package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;
    public static List<User> userList = new ArrayList<>();

    static {
        userList.add(new User("test1","1","테스트1","test1@test.com"));
        userList.add(new User("test2","2","테스트2","test2@test.com"));
        userList.add(new User("test3","3","테스트3","test3@test.com"));
    }

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
    
    public static boolean equal(User user1, User user2) {
    	if(user1.userId == user2.userId && user1.password == user2.password && user1.name == user2.name && user1.email == user2.email) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
}
