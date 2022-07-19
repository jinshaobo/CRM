package com.jinshaobo.crm.workbench.web.controller;

import com.jinshaobo.crm.settings.domain.User;
import com.jinshaobo.crm.settings.service.UserService;
import com.jinshaobo.crm.settings.service.impl.UserServiceImpl;
import com.jinshaobo.crm.utils.DateTimeUtil;
import com.jinshaobo.crm.utils.PrintJson;
import com.jinshaobo.crm.utils.ServiceFactory;
import com.jinshaobo.crm.utils.UUIDUtil;
import com.jinshaobo.crm.workbench.domain.Activity;
import com.jinshaobo.crm.workbench.domain.Clue;
import com.jinshaobo.crm.workbench.domain.Tran;
import com.jinshaobo.crm.workbench.service.ActivityService;
import com.jinshaobo.crm.workbench.service.ClueService;
import com.jinshaobo.crm.workbench.service.impl.ActivityServiceImpl;
import com.jinshaobo.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到线索控制器");
        String path = request.getServletPath();

        if("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/clue/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);
        }else if("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);
        }else if("/workbench/clue/getActivityListByNameNotByClueId.do".equals(path)){
            getActivityListByNameNotByClueId(request,response);
        }else if("/workbench/clue/bund.do".equals(path)){
            bund(request,response);
        }else if("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);
        }else if("/workbench/clue/convert.do".equals(path)){
            convert(request,response);
        }
    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String clueId = request.getParameter("clueId");
        //判断是否创建交易
        String flag = request.getParameter("flag");
        Tran tran = null;
        String createBy = "";
        if("a".equals(flag)){
            //需要创建交易,接收交易表单中的参数
            tran = new Tran();
            tran.setId(UUIDUtil.getUUID());
            tran.setMoney(request.getParameter("money"));
            tran.setName(request.getParameter("name"));
            tran.setExpectedDate(request.getParameter("expectedDate"));
            tran.setStage(request.getParameter("stage"));
            tran.setActivityId(request.getParameter("activityId"));
            createBy = ((User) request.getSession().getAttribute("user")).getName();
            tran.setCreateBy(createBy);
            tran.setCreateTime(DateTimeUtil.getSysTime());
        }

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        //传递的参数，clueId和tran，tran可能为null，
        // 除了这两个参数还得为转换为客户和联系人的两张表传递id，createBy和createTime
        //id和createTime在这里可以在业务层自己生成，createBy必须从controller传递过去
        boolean flag1 = clueService.convert(clueId,tran,createBy);
        if(flag1){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }


    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {

        String aName = request.getParameter("aName");
        ActivityService activityService = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByName(aName);
        PrintJson.printJsonObj(response,activityList);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {
        String cid = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.bund(cid,aids);
        PrintJson.printJsonFlag(response,flag);

    }

    private void getActivityListByNameNotByClueId(HttpServletRequest request, HttpServletResponse response) {
        String aName = request.getParameter("aName");
        String clueId = request.getParameter("clueId");
        Map<String,String> map = new HashMap<String,String>();
        map.put("aName",aName);
        map.put("clueId",clueId);
        ActivityService activityService = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByNameNotByClueId(map);
        PrintJson.printJsonObj(response,activityList);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.unbund(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据线索ID查询市场活动列表");
        String clueId = request.getParameter("clueId");
        System.out.println(clueId);
        ActivityService activityService = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response,activityList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = clueService.detail(id);
        request.setAttribute("clue",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);//转发有异常
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        Clue clue = new Clue();
        clue.setId(UUIDUtil.getUUID());
        clue.setFullname(request.getParameter("fullname"));
        clue.setAppellation(request.getParameter("appellation"));
        clue.setOwner(request.getParameter("owner"));
        clue.setCompany(request.getParameter("company"));
        clue.setJob(request.getParameter("job"));
        clue.setEmail(request.getParameter("email"));
        clue.setPhone(request.getParameter("phone"));
        clue.setWebsite(request.getParameter("website"));
        clue.setMphone(request.getParameter("mphone"));
        clue.setState(request.getParameter("state"));
        clue.setSource(request.getParameter("source"));
        User user = (User) request.getSession().getAttribute("user");
        clue.setCreateBy(user.getName());
        clue.setCreateTime(DateTimeUtil.getSysTime());
        clue.setDescription(request.getParameter("description"));
        clue.setContactSummary(request.getParameter("contactSummary"));
        clue.setNextContactTime(request.getParameter("nextContactTime"));
        clue.setAddress(request.getParameter("address"));

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.save(clue);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);
    }
}
