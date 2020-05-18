package com.offcn.cus.service;

import com.github.pagehelper.PageInfo;
import com.offcn.cus.bean.Customer;
import com.offcn.utils.QueryObj;

import java.util.List;

public interface CusService {
    public PageInfo<Customer> getByPage(int pageNO, QueryObj qo);
    public int saveCus(Customer cus);
    public int updateCus(Customer cus);
    public Customer getCusById(int cid);
    public int delCus(List<Integer> ids);
    public List<Customer> getclist();
}
