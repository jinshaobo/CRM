package com.jinshaobo.crm.settings.service.impl;

import com.jinshaobo.crm.settings.dao.DicTypeDao;
import com.jinshaobo.crm.settings.dao.DicValueDao;
import com.jinshaobo.crm.settings.domain.DicType;
import com.jinshaobo.crm.settings.domain.DicValue;
import com.jinshaobo.crm.settings.service.DicService;
import com.jinshaobo.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    public Map<String, List<DicValue>> getAll() {
        Map<String,List<DicValue>> map = new HashMap<String, List<DicValue>>();
        //先获取所有的字典类型
        List<DicType> dtList = dicTypeDao.getTypeList();
        for (DicType dt:dtList) {
            String code = dt.getCode();
            List<DicValue> dvList = dicValueDao.getListByCode(code);
            map.put(code+"List",dvList);
        }

        return map;
    }
}
