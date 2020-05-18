package com.offcn.pro.service;

import com.offcn.pro.bean.Employee;
import com.offcn.pro.bean.EmployeeExample;
import com.offcn.pro.dao.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    EmployeeMapper em;

    /*
      根据职位获取职工
     */
    @Override
    public List<Employee> getEmpsByPos(int pid) {
        EmployeeExample ee=new EmployeeExample();
        EmployeeExample.Criteria cc = ee.createCriteria();
        cc.andPFkEqualTo(pid);
        return em.selectByExample(ee);
    }

    /*
       获取项目经理级别以下员工
     */
    @Override
    public List<Employee> getLessManger() {
        EmployeeExample ee=new EmployeeExample();
        EmployeeExample.Criteria cc = ee.createCriteria();
        cc.andPFkLessThan(4);
        return em.selectByExample(ee);
    }

    @Override
    public Employee login(String username, String password) {
        EmployeeExample ee=new EmployeeExample();
        EmployeeExample.Criteria cc = ee.createCriteria();
        cc.andUsernameEqualTo(username);
        cc.andPasswordEqualTo(password);
        List<Employee> elist = em.selectByExample(ee);
        if(elist!=null && elist.size()>0){
            return elist.get(0);
        }
        return null;
    }

    /*
      非当前登录职工
     */
    @Override
    public List<Employee> getUnselfEmps(int eid) {
        EmployeeExample ee=new EmployeeExample();
        EmployeeExample.Criteria cc = ee.createCriteria();
        cc.andEidNotEqualTo(eid);
        return em.selectByExample(ee);
    }
}
