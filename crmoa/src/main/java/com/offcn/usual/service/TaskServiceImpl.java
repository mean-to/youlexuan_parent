package com.offcn.usual.service;

import com.offcn.usual.bean.Task;
import com.offcn.usual.bean.TaskExample;
import com.offcn.usual.bean.TaskView;
import com.offcn.usual.dao.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskMapper tm;

    @Override
    public int saveTask(Task task) {
        return tm.insertSelective(task);
    }

    /*
      获取某个用户下发的所有任务
     */
    @Override
    public List<TaskView> getTlist(Map map) {
        return tm.getTasks(map);
    }

    @Override
    public Task getByTid(int tid) {
        return tm.selectByPrimaryKey(tid);
    }

    @Override
    public List<TaskView> getMyTlist(Map map) {
        return tm.getMytlist(map);
    }

    @Override
    public int updateStatus(int tid, int st) {
        Task t=new Task();
        t.setStatus(st);
        t.setId(tid);
        return tm.updateByPrimaryKeySelective(t);
    }

    @Override
    public int getUnTaskCount(int eid) {
        TaskExample te=new TaskExample();
        TaskExample.Criteria cc = te.createCriteria();
        cc.andEmpFk2EqualTo(eid);
        cc.andStatusNotEqualTo(2);
        return (int)tm.countByExample(te);
    }
}
