package com.jinshaobo.crm.workbench.dao;

import com.jinshaobo.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {

    int getCountByAids(String[] ids);

    int delete(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String activityId);

    int deleteRemark(String id);

    int addRemark(ActivityRemark activityRemark);

    ActivityRemark getRemarkById(String id);

    int updateRemark(ActivityRemark activityRemark);
}
