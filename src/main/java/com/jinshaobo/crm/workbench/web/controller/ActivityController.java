package com.jinshaobo.crm.workbench.web.controller;

import com.jinshaobo.crm.settings.domain.User;
import com.jinshaobo.crm.settings.service.UserService;
import com.jinshaobo.crm.settings.service.impl.UserServiceImpl;
import com.jinshaobo.crm.utils.*;
import com.jinshaobo.crm.vo.Pagination;
import com.jinshaobo.crm.workbench.domain.Activity;
import com.jinshaobo.crm.workbench.domain.ActivityRemark;
import com.jinshaobo.crm.workbench.service.ActivityService;
import com.jinshaobo.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = request.getServletPath();

        if("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/activity/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        }else if("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        }else if("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }else if("/workbench/activity/update.do".equals(path)){
            update(request,response);
        }else if("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(request,response);
        }else if("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        }else if("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }else if("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setId(request.getParameter("id"));
        activityRemark.setNoteContent(request.getParameter("noteContent"));
        activityRemark.setEditTime(DateTimeUtil.getSysTime());
        User user = (User) request.getSession().getAttribute("user");
        activityRemark.setEditBy(user.getName());
        activityRemark.setEditFlag("1");

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map = activityService.updateRemark(activityRemark);
        PrintJson.printJsonObj(response,map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {

        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setId(UUIDUtil.getUUID());
        activityRemark.setNoteContent(request.getParameter("noteContent"));
        activityRemark.setCreateTime(DateTimeUtil.getSysTime());
        User user = (User) request.getSession().getAttribute("user");
        activityRemark.setCreateBy(user.getName());
        activityRemark.setEditFlag("0");
        activityRemark.setActivityId(request.getParameter("activityId"));

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map =  activityService.saveRemark(activityRemark);
        PrintJson.printJsonObj(response,map);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        String activityId = request.getParameter("activityId");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> remarkList = activityService.getRemarkListByAid(activityId);
        PrintJson.printJsonObj(response,remarkList);
    }

    //这里走传统请求，需要转发，转发需要抛异常
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity activity = activityService.detail(id);
        request.setAttribute("activity",activity);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        Activity activity = new Activity();
        activity.setId(request.getParameter("id"));
        activity.setOwner(request.getParameter("owner"));
        activity.setName(request.getParameter("name"));
        activity.setStartDate(request.getParameter("startDate"));
        activity.setEndDate(request.getParameter("endDate"));
        activity.setCost(request.getParameter("cost"));
        activity.setDescription(request.getParameter("description"));
        activity.setEditTime(DateTimeUtil.getSysTime());
        User user = (User)request.getSession().getAttribute("user");
        activity.setEditBy(user.getName());

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.update(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        //接收参数
        String id = request.getParameter("id");
        //调用service处理参数
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        //前端需要userList和activity单条，所以返回一个map封装
        Map<String,Object> map = activityService.getUserListAndActivity(id);
        //转换为json给前端
        PrintJson.printJsonObj(response,map);

    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {

       String[] ids = request.getParameterValues("id");

       ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
       boolean flag = activityService.delete(ids);
       PrintJson.printJsonFlag(response,flag);

    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        //前端传过来的数据含有pageNo和pageSize，domain接收不了，所以只能用map接收
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.parseInt(pageNoStr);
        int pageSize = Integer.parseInt(pageSizeStr);
        int skipCount = (pageNo-1) * pageSize;

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        map.put("name",request.getParameter("name"));
        map.put("owner",request.getParameter("owner"));
        map.put("startDate",request.getParameter("startDate"));
        map.put("endDate",request.getParameter("endDate"));

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        //返回的数据有total,List<Activity>,因为数据复用率高，还要在其他模块使用，所以用VO类返回数据
        Pagination<Activity> vo = activityService.pageList(map);
        PrintJson.printJsonObj(response,vo);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        Activity activity = new Activity();
        activity.setId(UUIDUtil.getUUID());
        activity.setOwner(request.getParameter("owner"));
        activity.setName(request.getParameter("name"));
        activity.setStartDate(request.getParameter("startDate"));
        activity.setEndDate(request.getParameter("endDate"));
        activity.setCost(request.getParameter("cost"));
        activity.setDescription(request.getParameter("description"));
        activity.setCreateTime(DateTimeUtil.getSysTime());
        User user = (User)request.getSession().getAttribute("user");
        activity.setCreateBy(user.getName());

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.save(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始执行getUserList方法");
        //虽然这是市场活动控制器，但是处理的业务是用户相关的业务，需要调用户相关的业务UserService
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);
        System.out.println("执行完getUserList方法");
    }


}
