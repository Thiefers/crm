package com.roc.crm.settings.service;

import com.roc.crm.exception.LoginException;
import com.roc.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
