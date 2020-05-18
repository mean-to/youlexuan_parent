package com.offcn.usual.service;

import com.offcn.pro.bean.Employee;
import com.offcn.pro.dao.EmployeeMapper;
import com.offcn.usual.bean.Archives;
import com.offcn.usual.dao.ArchivesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ArcServiceImpl implements ArcService {

    @Autowired
    EmployeeMapper em;
    @Autowired
    ArchivesMapper am;

    @Override
    public Employee getEmpInfo(int eid) {
        return em.getEmpInfo(eid);
    }

    /*
     保存或者更新职工信息以及档案信息
     */
    @Override
    public int saveOrUpdate(Employee emp) {
        int i=0;
        //职工表：更新操作，
        em.updateByPrimaryKeySelective(emp);
        //档案表：添加或者更新操作
        String dnum = emp.getArc().getDnum();
        if(dnum!=null && dnum!=""){
          //更新操作
          i=am.updateByPrimaryKeySelective(emp.getArc());
        }else{
            Archives arc = emp.getArc();
            arc.setDnum(UUID.randomUUID().toString().substring(28));
            arc.setEmpFk(emp.getEid());
            i=am.insertSelective(emp.getArc());
        }
        return i;
    }

    //批量插入
    @Override
    public int saveMulti(List<Archives> alist) {
        //insert into 表名（列名。。。） values(1,'',''),(1,'',''),(1,'',''),(1,'','')
        return am.saveArcs(alist);
    }
}
