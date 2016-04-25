
package com.bunjlabs.classificator.tools;

import java.util.HashMap;


public class Loginer {
    
    public enum AccessLevel {
        ADMIN,
        USER,
        NOBODY
    }
    
    private static class LoginInfo {
        public String password;
        public AccessLevel level;
        
        public LoginInfo(String password, AccessLevel level) {
            this.password = password;
            this.level = level;
        }
    }
    
    private HashMap<String, LoginInfo> accounts = new HashMap<>();
    
    private Loginer() {
        accounts.put("admin", new LoginInfo("admin", AccessLevel.ADMIN));
        accounts.put("user", new LoginInfo("user", AccessLevel.USER));
    }
    
    public AccessLevel whoIs(String login, String password) {
        LoginInfo info = accounts.get(login);
        if (info == null) {
            return AccessLevel.NOBODY;
        }
        if (!info.password.equals(password)) {
            return null;
        }
        return info.level;
    }
    
    
    private static Loginer instance;
    
    public static Loginer getInstance() {
        return (instance == null) ? instance = new Loginer() : instance;
    }
    
}
