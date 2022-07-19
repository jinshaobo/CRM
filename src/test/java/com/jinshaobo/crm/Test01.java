package com.jinshaobo.crm;

import com.jinshaobo.crm.utils.DateTimeUtil;
import com.jinshaobo.crm.utils.MD5Util;
import org.junit.Test;

public class Test01 {

    @Test
    public void test01(){
        //验证失效时间
        String expireTime = "2022-07-31 10-10-10";
        String currentTime = DateTimeUtil.getSysTime();

        int count = expireTime.compareTo(currentTime);

        if(count > 0){
            System.out.println("还没失效");
        }else {
            System.out.println("已失效");
        }
    }

    @Test
    public void test02(){
        //验证锁定状态时间
        String lockState = "0";
        if("0".equals(lockState)){
            System.out.println("当前账号已锁定");
        }else if("1".equals(lockState)){
            System.out.println("当前账号没被锁定");
        }
    }

    @Test
    public void test03(){
        //验证可以访问的IP地址
        //用户的IP
        String ip = "192.168.1.1";
        //允许访问的IP
        String allowIPs = "192.168.1.1,192.168.1.2,192.168.1.3";

        if(allowIPs.contains(ip)){
            System.out.println("允许该用户访问");
        }else {
            System.out.println("不允许该用户访问");
        }
    }

    @Test
    public void test04(){
        //MD5加密
        String pwd = "123";
        String pwd1 = MD5Util.getMD5(pwd);
        System.out.println(pwd1);
    }


}
