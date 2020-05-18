package com.offcn.pro.service;

import com.offcn.pro.bean.Function;
import com.offcn.pro.bean.FunctionExample;
import com.offcn.pro.dao.FunctionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunServiceImpl implements FunService {

    @Autowired
    FunctionMapper fm;

    @Override
    public int saveFun(Function fun) {
        return fm.insertSelective(fun);
    }

    @Override
    public Function getFunById(int fid) {
        return fm.selectByPrimaryKey(fid);
    }

    @Override
    public int updateFun(Function fun) {
        return fm.updateByPrimaryKeySelective(fun);
    }

    @Override
    public List<Function> getFunBYMid(int mid) {
        FunctionExample fe=new FunctionExample();
        FunctionExample.Criteria cc = fe.createCriteria();
        cc.andModeleFkEqualTo(mid);
        return fm.selectByExample(fe);
    }
}
