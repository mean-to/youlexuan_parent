package com.offcn.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import javafx.scene.text.TextBoundsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map dataModel = new HashMap();
            //加载商品表数据
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",tbGoods);
            TbItemCat tbItemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat tbItemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat tbItemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            dataModel.put("itemCat1",tbItemCat1);
            dataModel.put("itemCat2",tbItemCat2);
            dataModel.put("itemCat3",tbItemCat3);

            //加载商品扩展表数据
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc",tbGoodsDesc);
            //生成sku
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");//降序，保证第一个是默认
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList",itemList);


            Writer out = new FileWriter(pagedir + goodsId + ".html");
            template.process(dataModel,out);
            out.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
