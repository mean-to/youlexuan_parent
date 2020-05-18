package com.offcn.pro.service;

import com.offcn.pro.bean.Employee;

import java.util.List;

public interface EmpService {
    public List<Employee> getEmpsByPos(int pid);
    public List<Employee> getLessManger();//项目经理级别下的所有员工
    public Employee login(String username,String password);//登录
    public List<Employee> getUnselfEmps(int eid);//
}
