package com.offcn.usual.service;

import com.offcn.usual.bean.Baoxiao;

import java.util.List;

public interface BxService {
    List<Baoxiao> getBxTasks(int flag);//查看报销任务
    Baoxiao getBxById(String bxid);//根据查询
    int updateSts(Baoxiao bx);//报销更新接口
    List<Baoxiao> getMyBxs(int eid);//根据职工id查询某个职工的报销单
}
