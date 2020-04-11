package com.offcn.solrutil;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    //导入商品数据
    public void importItemDate() {
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(example);
        System.out.println("商品列表");
        for (TbItem item : itemList) {
            System.out.println(item.getTitle());
            Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
            //创建一个新 map 集合存储拼音
            Map<String,String> mapPinyin=new HashMap<String,String>();
            // 遍历 map，替换 key 从汉字变为拼音
            for(String key :specMap.keySet()){
                mapPinyin.put(Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
}
            item.setSpecMap(mapPinyin);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("结束");
    }


}
