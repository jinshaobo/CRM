package com.jinshaobo.crm.workbench.service;

import com.jinshaobo.crm.workbench.domain.Tran;
import com.jinshaobo.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    boolean save(Tran tran, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistoryListByTranId(String tranId);

    boolean changeStage(Tran tran);

    Map<String, Object> getCharts();

}
