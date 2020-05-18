package com.offcn.cus.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.offcn.cus.bean.Customer;
import com.offcn.cus.bean.CustomerExample;
import com.offcn.cus.dao.CustomerMapper;
import com.offcn.utils.QueryObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CusServiceImpl implements CusService {

    @Autowired
    CustomerMapper cm;

    @Override
    public PageInfo<Customer> getByPage(int pageNO, QueryObj qo) {
        PageHelper.startPage(pageNO,4);
        List<Customer> clist = cm.getclist(qo);
        PageInfo<Customer> info=new PageInfo<Customer>(clist);
        return info;
    }

    @Override
    public int saveCus(Customer cus) {
        return cm.insertSelective(cus);
    }

    @Override
    public int updateCus(Customer cus) {
        return cm.updateByPrimaryKeySelective(cus);
    }

    @Override
    public Customer getCusById(int cid) {
        return cm.selectByPrimaryKey(cid);
    }

    @Override
    public int delCus(List<Integer> ids) {
        CustomerExample ce=new CustomerExample();
        CustomerExample.Criteria cc = ce.createCriteria();
        cc.andIdIn(ids);
        return cm.deleteByExample(ce);
    }

    @Override
    public List<Customer> getclist() {
        return cm.selectByExample(null);
    }




}
