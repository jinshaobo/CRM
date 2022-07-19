package com.jinshaobo.crm.workbench.service.impl;

import com.jinshaobo.crm.utils.DateTimeUtil;
import com.jinshaobo.crm.utils.SqlSessionUtil;
import com.jinshaobo.crm.utils.UUIDUtil;
import com.jinshaobo.crm.workbench.dao.*;

import com.jinshaobo.crm.workbench.domain.*;
import com.jinshaobo.crm.workbench.service.ClueService;

import java.util.List;


public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);



    public boolean save(Clue clue) {
        boolean flag = true;
        int count = clueDao.save(clue);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public Clue detail(String id) {
        Clue clue = clueDao.detail(id);
        return clue;
    }

    public boolean unbund(String id) {
        boolean flag = true;
        int count = clueActivityRelationDao.unbund(id);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public boolean bund(String cid, String[] aids) {
        boolean flag = true;
        for (String aid : aids) {
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(cid);
            car.setActivityId(aid);
            int count = clueActivityRelationDao.bund(car);
            if (count != 1) {
                flag = false;
            }

        }
        return flag;
    }

    public boolean convert(String clueId, Tran tran, String createBy) {
        boolean flag = true;
        String createTime = DateTimeUtil.getSysTime();
        //1.通过线索id获取线索对象
        Clue clue = clueDao.getById(clueId);
        //2.通过线索对象获取客户(公司)信息，去客户表中查查，当客户不存在，新建客户
        String company = clue.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        if(customer == null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setOwner(clue.getOwner());
            customer.setName(company);
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setContactSummary(clue.getContactSummary());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setDescription(clue.getDescription());
            customer.setAddress(clue.getAddress());
            int count1 = customerDao.save(customer);
            if(count1 != 1){
                flag = false;
            }
        }

        //3.通过线索对象获取联系人信息，保存联系人信息，联系人可以重复不需要判断
        Contacts contacts = new Contacts();
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setId(UUIDUtil.getUUID());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());

        int count2 = contactsDao.save(contacts);
        if(count2 != 1){
            flag = false;
        }

        //4.线索备注转换成客户备注和联系人备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        for (ClueRemark clueRemark:clueRemarkList) {
            //取出线索备注信息，主要转换到客户和联系人的就是备注信息
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注对象
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setNoteContent(noteContent);
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateTime(createTime);
            customerRemark.setCreateBy(createBy);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3 != 1){
                flag = false;
            }

            //创建联系人备注对象
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setEditFlag("0");
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4 != 1){
                flag = false;
            }
        }

        //5.线索与市场活动关系转换为联系人与市场活动关系
        //查询出线索与市场活动关联关系列表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList) {
            String activityId = clueActivityRelation.getActivityId();
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5 != 1){
                flag = false;
            }
        }

        //6.如果有创建交易需求，创建一条交易
        if(tran != null){
            //tran在controller中已经封装好的信息
            //id、money、name、expectedDate、stage、activityId、createBy、createTime
            //继续封装交易表中其他信息，这些信息是从clue表中转换过来的
            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(customer.getId());
            tran.setContactSummary(contacts.getContactSummary());
            tran.setContactsId(contacts.getId());

            int count6 = tranDao.save(tran);
            if(count6 != 1){
                flag = false;
            }

            //7.创建一条交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setTranId(tran.getId());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setStage(tran.getStage());

            int count7 = tranHistoryDao.save(tranHistory);
            if(count7 != 1){
                flag = false;
            }
        }

        //8.删除线索备注
        for (ClueRemark clueRemark:clueRemarkList) {
            int count8 = clueRemarkDao.delete(clueRemark);
            if(count8 != 1){
                flag = false;
            }
        }

        //9.删除线索市场活动的关系
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList) {
            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if(count9 != 1){
                flag = false;
            }
        }

        //10.删除线索
        int count10 = clueDao.delete(clueId);
        if(count10 != 1){
            flag = false;
        }
        return flag;
    }


}
