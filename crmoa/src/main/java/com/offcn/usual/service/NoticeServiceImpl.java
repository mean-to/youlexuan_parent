package com.offcn.usual.service;

import com.offcn.usual.bean.Notice;
import com.offcn.usual.dao.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    NoticeMapper nm;

    //获取最新的公告
    @Override
    public List<Notice> getLast() {
        return nm.getLast();
    }

    @Override
    public Notice getNoticeByNid(int nid) {
        return nm.selectByPrimaryKey(nid);
    }
}
