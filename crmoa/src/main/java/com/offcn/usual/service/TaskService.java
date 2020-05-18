package com.offcn.usual.service;

import com.offcn.usual.bean.Task;
import com.offcn.usual.bean.TaskView;

import java.util.List;
import java.util.Map;

public interface TaskService {
    public int saveTask(Task task);
    public List<TaskView> getTlist(Map map);//任务信息
    public Task getByTid(int tid);
    public List<TaskView> getMyTlist(Map map);//我的任务
    public int updateStatus(int tid,int st);//更新状态
    public int getUnTaskCount(int eid);//未完成任务
}
