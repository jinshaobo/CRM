package com.jinshaobo.crm.workbench.service.impl;


import com.jinshaobo.crm.settings.dao.UserDao;
import com.jinshaobo.crm.settings.domain.User;
import com.jinshaobo.crm.utils.SqlSessionUtil;
import com.jinshaobo.crm.vo.Pagination;
import com.jinshaobo.crm.workbench.dao.ActivityDao;
import com.jinshaobo.crm.workbench.dao.ActivityRemarkDao;
import com.jinshaobo.crm.workbench.domain.Activity;
import com.jinshaobo.crm.workbench.domain.ActivityRemark;
import com.jinshaobo.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {

    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao;


    public boolean save(Activity activity) {
        boolean flag = true;
        int count = activityDao.save(activity);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public Pagination<Activity> pageList(Map<String, Object> map) {

        //取得total
        int total = activityDao.getTotalByCondition(map);

        //取得dataList
        List<Activity> activityList = activityDao.getActivityListByCondition(map);

        //封装成vo
        Pagination<Activity> vo = new Pagination<Activity>();
        vo.setTotal(total);
        vo.setDataList(activityList);

        //返回vo
        return vo;
    }

    public boolean delete(String[] ids) {
        boolean flag = true;
        //删除市场活动之前，必须删除掉它关联的市场备注表，操作哪张表就需要使用哪张表的Dao
        //先查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);

        //删除备注后返回影响记录条数count，进行对比
        int count2 = activityRemarkDao.delete(ids);

        if(count1 != count2){
            flag = false;
        }

        //删除市场活动
        int count = activityDao.delete(ids);
        if(count != ids.length){
            flag = false;
        }
        return flag;
    }

    public Map<String, Object> getUserListAndActivity(String id) {
        userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
        List<User> userList = userDao.getUserList();
        Activity activity = activityDao.getActivityById(id);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userList",userList);
        map.put("activity",activity);
        return map;
    }

    public boolean update(Activity activity) {
        boolean flag = true;
        int count = activityDao.update(activity);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public Activity detail(String id) {
        Activity activity = activityDao.detail(id);
        return activity;
    }

    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        List<ActivityRemark> remarkList = activityRemarkDao.getRemarkListByAid(activityId);
        return remarkList;
    }

    public boolean deleteRemark(String id) {
        boolean flag = true;
        int count = activityRemarkDao.deleteRemark(id);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public Map<String,Object> saveRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.addRemark(activityRemark);
        if(count != 1){
            flag =false;
        }
        ActivityRemark remark = activityRemarkDao.getRemarkById(activityRemark.getId());
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("remark",remark);
        return map;
    }

    public Map<String, Object> updateRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(activityRemark);
        if(count != 1){
            flag = false;
        }
        ActivityRemark remark = activityRemarkDao.getRemarkById(activityRemark.getId());
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("remark",remark);
        return map;
    }

    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> activityList = activityDao.getActivityListByClueId(clueId);
        return activityList;
    }

    public List<Activity> getActivityListByNameNotByClueId(Map<String, String> map) {

        List<Activity> activityList = activityDao.getActivityListByNameNotByClueId(map);
        return activityList;
    }

    public List<Activity> getActivityListByName(String aName) {
        List<Activity> activityList = activityDao.getActivityListByName(aName);
        return activityList;
    }
}

