package com.jinshaobo.crm.workbench.web.controller;

import com.jinshaobo.crm.settings.domain.User;
import com.jinshaobo.crm.settings.service.UserService;
import com.jinshaobo.crm.settings.service.impl.UserServiceImpl;
import com.jinshaobo.crm.utils.DateTimeUtil;
import com.jinshaobo.crm.utils.PrintJson;
import com.jinshaobo.crm.utils.ServiceFactory;
import com.jinshaobo.crm.utils.UUIDUtil;
import com.jinshaobo.crm.workbench.domain.Tran;
import com.jinshaobo.crm.workbench.domain.TranHistory;
import com.jinshaobo.crm.workbench.service.CustomerService;
import com.jinshaobo.crm.workbench.service.TranService;
import com.jinshaobo.crm.workbench.service.impl.CustomerServiceImpl;
import com.jinshaobo.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        }else if("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }else if("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/transaction/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/transaction/getHistoryListByTranId.do".equals(path)){
            getHistoryListByTranId(request,response);
        }else if("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }else if("/workbench/transaction/getCharts.do".equals(path)){
            getCharts(request,response);
        }

    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入交易图标控制器");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Map<String,Object> map = tranService.getCharts();
        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        Tran tran = new Tran();
        tran.setId(request.getParameter("id"));
        tran.setStage(request.getParameter("stage"));
        System.out.println(request.getParameter("stage"));
        tran.setMoney(request.getParameter("money"));
        tran.setExpectedDate(request.getParameter("expectedDate"));
        tran.setEditTime(DateTimeUtil.getSysTime());
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        tran.setEditBy(editBy);

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.changeStage(tran);

        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(request.getParameter("stage"));
        tran.setPossibility(possibility);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("tran",tran);
        PrintJson.printJsonObj(response,map);


    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取交易列表");
        String tranId = request.getParameter("tranId");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> tranHistoryList = tranService.getHistoryListByTranId(tranId);

        //处理可能性
        ServletContext application = request.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        for (TranHistory tranHistory:tranHistoryList) {
            String stage = tranHistory.getStage();
            String possibility = pMap.get(stage);
            tranHistory.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response,tranHistoryList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran = tranService.detail(id);

        //处理可能性
        //stage和对应关系pMap有了
        String stage = tran.getStage();
        ServletContext application = request.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibility = pMap.get(stage);
        tran.setPossibility(possibility);
        request.setAttribute("tran",tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Tran tran = new Tran();
        tran.setId(UUIDUtil.getUUID());
        tran.setOwner(request.getParameter("owner"));
        tran.setMoney(request.getParameter("money"));
        tran.setName(request.getParameter("name"));
        tran.setExpectedDate(request.getParameter("expectedDate"));
        String customerName = request.getParameter("customerName");
        //tran.setCustomerId();前端没有传id，只有名字
        tran.setStage(request.getParameter("stage"));
        tran.setType(request.getParameter("type"));
        tran.setSource(request.getParameter("source"));
        tran.setActivityId(request.getParameter("activityId"));
        tran.setContactsId(request.getParameter("contactsId"));
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        tran.setCreateBy(createBy);
        tran.setCreateTime(DateTimeUtil.getSysTime());
        tran.setDescription(request.getParameter("description"));
        tran.setContactSummary(request.getParameter("contactSummary"));
        tran.setNextContactTime(request.getParameter("nextContactTime"));

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.save(tran,customerName);
        if(flag){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }

    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> sList = customerService.getCustomerName(name);
        PrintJson.printJsonObj(response,sList);

    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        request.setAttribute("userList",userList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
