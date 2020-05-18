package com.offcn.pro.dao;

import com.offcn.pro.bean.Module;
import com.offcn.pro.bean.ModuleExample;
import com.offcn.utils.QueryObj;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper {
    long countByExample(ModuleExample example);

    int deleteByExample(ModuleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Module record);

    int insertSelective(Module record);

    List<Module> selectByExample(ModuleExample example);

    Module selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Module record, @Param("example") ModuleExample example);

    int updateByExample(@Param("record") Module record, @Param("example") ModuleExample example);

    int updateByPrimaryKeySelective(Module record);

    int updateByPrimaryKey(Module record);

    List<Module> getmlist(QueryObj qo);

}