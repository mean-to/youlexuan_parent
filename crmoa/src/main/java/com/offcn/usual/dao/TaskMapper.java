package com.offcn.usual.dao;

import com.offcn.usual.bean.Task;
import com.offcn.usual.bean.TaskExample;
import com.offcn.usual.bean.TaskView;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskMapper {
    long countByExample(TaskExample example);

    int deleteByExample(TaskExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Task record);

    int insertSelective(Task record);

    List<Task> selectByExample(TaskExample example);

    Task selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByExample(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByPrimaryKeySelective(Task record);

    int updateByPrimaryKey(Task record);

    List<TaskView> getTasks(Map map);//获取某个用户下发的所有任务
    List<TaskView> getMytlist(Map map);//获取某个用户需要执行的任务

}