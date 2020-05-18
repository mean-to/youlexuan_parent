package com.offcn.pro.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.offcn.pro.bean.Module;
import com.offcn.pro.bean.ModuleExample;
import com.offcn.pro.dao.ModuleMapper;
import com.offcn.utils.QueryObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModServiceImpl implements ModService {

    @Autowired
    ModuleMapper mm;

    @Override
    public PageInfo<Module> getBypage(int pageNO, QueryObj qo) {
        PageHelper.startPage(pageNO,2);
        List<Module> mlist = mm.getmlist(qo);
        PageInfo<Module> ifo=new PageInfo<>(mlist);
        return ifo;
    }

    //保存模块
    @Override
    public int saveMod(Module mod) {
        return mm.insertSelective(mod);
    }

    //根据组件获取模块
    @Override
    public Module getModByMid(int mid) {
        return mm.selectByPrimaryKey(mid);
    }

    @Override
    public int updateMod(Module mod) {
        return mm.updateByPrimaryKeySelective(mod);
    }

    @Override
    public List<Module> getmlist(int aid) {
        ModuleExample me=new ModuleExample();
        ModuleExample.Criteria cc = me.createCriteria();
        cc.andAnalysisFkEqualTo(aid);
        return mm.selectByExample(me);
    }










}
