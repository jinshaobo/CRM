package com.jinshaobo.crm.settings.service.impl;

import com.jinshaobo.crm.exception.LoginException;
import com.jinshaobo.crm.settings.dao.UserDao;
import com.jinshaobo.crm.settings.domain.User;
import com.jinshaobo.crm.settings.service.UserService;
import com.jinshaobo.crm.utils.DateTimeUtil;
import com.jinshaobo.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private UserDao userDao;

    //用户登录业务类
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
        Map<String,String> map = new HashMap<String, String>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userDao.login(map);

        if(user == null){
            throw new LoginException("账号或密码错误！");
        }
        //执行到此处说明账号密码正确，再验证其他信息
        //验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if(expireTime.compareTo(currentTime) < 0){
            throw new LoginException("该账号已失效！");
        }
        //验证锁定状态
        String lockState = user.getLockState();
        if("0".equals(lockState)){
            throw new LoginException("该账号已被锁定！");
        }

        //判断IP地址
        String ips = user.getAllowIps();
        if(!ips.contains(ip)){
            throw new LoginException("对不起，您无权访问！");
        }

        return user;
    }

    //市场活动查询用户信息业务类
    public List<User> getUserList() {
        userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
        List<User> userList = userDao.getUserList();
        return userList;
    }
}
