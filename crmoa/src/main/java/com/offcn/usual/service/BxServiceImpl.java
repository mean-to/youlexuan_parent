package com.offcn.usual.service;

import com.offcn.usual.bean.Baoxiao;
import com.offcn.usual.bean.BaoxiaoExample;
import com.offcn.usual.dao.BaoxiaoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BxServiceImpl implements BxService {

    @Autowired
    BaoxiaoMapper bxm;

    @Override
    public List<Baoxiao> getBxTasks(int flag) {
        return bxm.getBxTasks(flag);
    }

    @Override
    public Baoxiao getBxById(String bxid) {
        return bxm.selectByPrimaryKey(bxid);
    }

    @Override
    public int updateSts(Baoxiao bx) {
        return bxm.updateByPrimaryKeySelective(bx);
    }

    @Override
    public List<Baoxiao> getMyBxs(int eid) {
        BaoxiaoExample bm=new BaoxiaoExample();
        BaoxiaoExample.Criteria cc = bm.createCriteria();
        cc.andEmpFkEqualTo(eid);
        return bxm.selectByExample(bm);
    }
}
