package com.jinshaobo.crm.settings.service;

import com.jinshaobo.crm.exception.LoginException;
import com.jinshaobo.crm.settings.domain.User;

import java.util.List;

public interface UserService {

    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
