package com.offcn.pro.service;

import com.offcn.pro.bean.Function;

import java.util.List;

public interface FunService {
    public int saveFun(Function fun);
    public Function getFunById(int fid);
    public int updateFun(Function fun);
    public List<Function> getFunBYMid(int mid);//根据模块id获取功能
}
