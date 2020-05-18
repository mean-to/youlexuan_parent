package com.offcn.pro.service;

import com.offcn.pro.bean.Attachment;
import com.offcn.utils.QueryObj;

import java.util.List;

public interface AttService {
    public List<Attachment> getAtts(QueryObj qo);
    public int saveAtt(Attachment att);
    public Attachment getAttById(int id);//根据主键查询附件
    public  int updateAtt(Attachment att);//更新附件
    public int delmulti(List<Integer> ids);//批量删除附件
}
