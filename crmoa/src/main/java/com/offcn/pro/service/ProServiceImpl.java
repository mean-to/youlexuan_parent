package com.offcn.pro.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.offcn.pro.bean.Analysis;
import com.offcn.pro.bean.Project;
import com.offcn.pro.dao.AnalysisMapper;
import com.offcn.pro.dao.ProjectMapper;
import com.offcn.utils.QueryObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProServiceImpl implements ProService {

    @Autowired
    ProjectMapper pm;
    @Autowired
    AnalysisMapper am;

    @Override
    public PageInfo<Project> getBypage(int pageNO, QueryObj qo) {
        PageHelper.startPage(pageNO,2);
        List<Project> plist = pm.getplist(qo);
        PageInfo<Project> info=new PageInfo<>(plist);
        return info;
    }

    @Override
    public int savePro(Project pro) {
        return pm.insertSelective(pro);
    }

    @Override
    public Project getProById(int pid) {
        return pm.selectByPrimaryKey(pid);
    }

    @Override
    public int updatePro(Project pro) {
        return pm.updateByPrimaryKeySelective(pro);
    }

    //没有需求的项目
    @Override
    public List<Project> getProNoHasNeed() {
        return pm.getProNoHasNeed();
    }

    @Override
    public int saveAna(Analysis ana) {
        return am.insertSelective(ana);
    }

    /*
       查询需求表获取所有有需求的项目
     */
    @Override
    public List<Analysis> getProsHasNeed() {
        return am.selectByExample(null);
    }

    @Override
    public List<Project> getAllpro() {
        return pm.selectByExample(null);
    }
}
