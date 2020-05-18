package com.offcn.usual.service;

import com.offcn.pro.bean.Employee;
import com.offcn.usual.bean.Archives;

import java.util.List;

public interface ArcService {
    Employee getEmpInfo(int eid);//我的档案
    int saveOrUpdate(Employee emp);//保存更新档案
    int saveMulti(List<Archives> alist);//批量插入
}
