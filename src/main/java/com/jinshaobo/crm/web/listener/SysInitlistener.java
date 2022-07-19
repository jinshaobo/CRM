package com.jinshaobo.crm.web.listener;

import com.jinshaobo.crm.settings.dao.DicValueDao;
import com.jinshaobo.crm.settings.domain.DicValue;
import com.jinshaobo.crm.settings.service.DicService;
import com.jinshaobo.crm.settings.service.impl.DicServiceImpl;
import com.jinshaobo.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitlistener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        //上下文域对象创建了
        System.out.println("上下文域对象创建了");

        //通过方法中的参数可以获得监听的域对象
        ServletContext application = sce.getServletContext();
        //调用service去数据库取数据字典
        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());
        //将47条数据字典，根据类型分成7类，获得7个list，再将7个list打包成map返回
        Map<String, List<DicValue>> map = dicService.getAll();
        //将map集合中的数据拆开放到application中
        Set<String> set = map.keySet();
        for (String key : set) {
            application.setAttribute(key,map.get(key));

        }

        //解析Stage2Possibility.properties配置文件
        Map<String,String> pMap = new HashMap<String, String>();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> e = resourceBundle.getKeys();
        while (e.hasMoreElements()){
            String key = e.nextElement();
            String value = resourceBundle.getString(key);
            pMap.put(key,value);
        }
        application.setAttribute("pMap",pMap );
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }
}
