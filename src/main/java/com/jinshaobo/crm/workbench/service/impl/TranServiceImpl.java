package com.jinshaobo.crm.workbench.service.impl;

import com.jinshaobo.crm.utils.DateTimeUtil;
import com.jinshaobo.crm.utils.SqlSessionUtil;
import com.jinshaobo.crm.utils.UUIDUtil;
import com.jinshaobo.crm.workbench.dao.CustomerDao;
import com.jinshaobo.crm.workbench.dao.TranDao;
import com.jinshaobo.crm.workbench.dao.TranHistoryDao;
import com.jinshaobo.crm.workbench.domain.Customer;
import com.jinshaobo.crm.workbench.domain.Tran;
import com.jinshaobo.crm.workbench.domain.TranHistory;
import com.jinshaobo.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    public boolean save(Tran tran, String customerName) {
        //tran里少了个customerId，先将customerName转换成customerId
        boolean flag = true;
        Customer customer = customerDao.getCustomerByName(customerName);
        if(customer == null){
            //创建一个客户
            customer = new Customer();
            customer.setCreateTime(tran.getCreateTime());
            customer.setCreateBy(tran.getCreateBy());
            customer.setId(UUIDUtil.getUUID());
            customer.setContactSummary(tran.getContactSummary());
            customer.setDescription(tran.getDescription());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setName(customerName);
            customer.setOwner(tran.getOwner());
            int count1 = customerDao.save(customer);
            if(count1 != 1){
                flag = false;
            }
        }
        //将tran补全
        tran.setCustomerId(customer.getId());
        int count2 = tranDao.save(tran);
        if(count2 != 1){
            flag = false;
        }

        //添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateTime(tran.getCreateTime());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setTranId(tran.getId());
        int count3 = tranHistoryDao.save(tranHistory);
        if(count3 != 1){
            flag = false;
        }
        return flag;
    }

    public Tran detail(String id) {
        Tran tran = tranDao.detail(id);
        return tran;
    }

    public List<TranHistory> getHistoryListByTranId(String tranId) {

        List<TranHistory> tranHistoryList = tranHistoryDao.getHistoryListByTranId(tranId);

        return tranHistoryList;
    }

    public boolean changeStage(Tran tran) {
        boolean flag = true;
        int count = tranDao.changeStage(tran);
        if(count != 1){
            flag = false;
        }

        //交易阶段改变后生成一条交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setCreateBy(tran.getEditBy());
        tranHistory.setTranId(tran.getId());

        int count2 = tranHistoryDao.save(tranHistory);
        if(count2 != 1){
            flag = false;
        }
        return flag;
    }

    public Map<String, Object> getCharts() {
        //取得total
        int total = tranDao.getTotal();
        //取得dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();
        //封装到map
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("total",total);
        map.put("dataList",dataList);
        return map;
    }
}
