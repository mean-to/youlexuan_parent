package com.offcn.cus.dao;

import com.offcn.cus.bean.Customer;
import com.offcn.cus.bean.CustomerExample;
import com.offcn.utils.QueryObj;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerMapper {
    long countByExample(CustomerExample example);

    int deleteByExample(CustomerExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Customer record);

    int insertSelective(Customer record);

    List<Customer> selectByExample(CustomerExample example);

    Customer selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Customer record, @Param("example") CustomerExample example);

    int updateByExample(@Param("record") Customer record, @Param("example") CustomerExample example);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer record);

    List<Customer> getclist(QueryObj qo);
}