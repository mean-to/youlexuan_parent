package com.offcn.pro.service;

import com.github.pagehelper.PageInfo;
import com.offcn.pro.bean.Analysis;
import com.offcn.pro.bean.Project;
import com.offcn.utils.QueryObj;

import java.util.List;

public interface ProService {
    //项目
    public PageInfo<Project> getBypage(int pageNO, QueryObj qo);
    public int savePro(Project pro);
    public Project getProById(int pid);
    public int updatePro(Project pro);

    //需求
    public List<Project> getProNoHasNeed();//获取没有需求的项目
    public int saveAna(Analysis ana);
    public List<Analysis> getProsHasNeed();//获取有需求的项目
    public List<Project> getAllpro();//获取所有项目


}
