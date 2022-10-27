package com.zjl.context;

import com.zjl.domain.dbentity.User;

import java.util.HashMap;
import java.util.Map;

public class UserContext {
    private ThreadLocal<User> user = new ThreadLocal<>();
    public void setUser(User user){
        this.user .set(user);
    }
    public User getUser(){
        return user.get();
    }
}
