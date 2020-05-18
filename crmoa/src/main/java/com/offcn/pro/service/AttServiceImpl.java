package com.offcn.pro.service;

import com.offcn.pro.bean.Attachment;
import com.offcn.pro.bean.AttachmentExample;
import com.offcn.pro.dao.AttachmentMapper;
import com.offcn.utils.QueryObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
@Service
public class AttServiceImpl implements AttService {

    @Autowired
    AttachmentMapper am;

    @Override
    public List<Attachment> getAtts(QueryObj qo) {
        return am.getAttlist(qo);
    }

    @Override
    public int saveAtt(Attachment att) {
        return am.insertSelective(att);
    }

    @Override
    public Attachment getAttById(int id) {
        return am.selectByPrimaryKey(id);
    }

    @Override
    public int updateAtt(Attachment att) {
        return am.updateByPrimaryKeySelective(att);
    }

    /*
      附件删除
     */
    @Override
    public int delmulti(List<Integer> ids) {
        AttachmentExample ae=new AttachmentExample();
        AttachmentExample.Criteria cc = ae.createCriteria();
        cc.andIdIn(ids);
        //从服务器删除附件文件
        List<Attachment> alist = am.selectByExample(ae);
        for(Attachment att:alist){
            File file=new File("E:\\crmoa\\att",att.getPath());
            if(file.exists()){
                file.delete();
            }
        }
        //删除数据库中附件的数据
        int i=am.deleteByExample(ae);
        return i;
    }

}
