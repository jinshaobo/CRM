package com.jinshaobo.crm.settings.dao;

import com.jinshaobo.crm.settings.domain.DicType;
import com.jinshaobo.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getListByCode(String code);
}
