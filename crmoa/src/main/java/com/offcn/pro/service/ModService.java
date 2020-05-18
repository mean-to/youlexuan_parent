package com.offcn.pro.service;

import com.github.pagehelper.PageInfo;
import com.offcn.pro.bean.Module;
import com.offcn.utils.QueryObj;

import java.util.List;

public interface ModService {
    public PageInfo<Module> getBypage(int pageNO, QueryObj qo);//获取模块的列表
    public int saveMod(Module mod);
    public Module getModByMid(int mid);
    public int updateMod(Module mod);
    public List<Module> getmlist(int aid);

}
