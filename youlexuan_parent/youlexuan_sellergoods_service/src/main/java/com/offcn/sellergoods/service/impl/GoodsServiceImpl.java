package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Goods;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService
{

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //首先保存商品基本信息到数据库
        //设置商品状态为待审核
        goods.getTbGoods().setAuditStatus("0");
        goodsMapper.insert(goods.getTbGoods());
        //保存商品扩展信息
        //关联商品扩展信息和商品基本信息
        goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());
        tbGoodsDescMapper.insert(goods.getTbGoodsDesc());
      saveItemList(goods);
    }

    //通用设置sku属性
    private void setItemValues(Goods goods,TbItem item){
        //商品图片
        //获取商品扩展信息表配图 json
        List<Map> listPic = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
        //判断配图不为空，大于0
        if (listPic != null && listPic.size() > 0) {
            //获取第一张图
            item.setImage((String) listPic.get(0).get("url"));
        }
        //所属三级分类id
        item.setCategoryid(goods.getTbGoods().getCategory3Id());
        //分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
        if (itemCat != null) {
            item.setCategory(itemCat.getName());
        }
        //创建时间
        item.setCreateTime(new Date());
        //更新时间
        item.setUpdateTime(new Date());
        //商品id
        item.setGoodsId(goods.getTbGoods().getId());
        //卖家id
        item.setSellerId(goods.getTbGoods().getSellerId());
        //根据sellerid获取商家对象
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
        if (seller != null) {
            item.setSeller(seller.getName());
        }
        //设置品牌
        Long brandId = goods.getTbGoods().getBrandId();
        //根据品牌id获取对应品牌对象
        TbBrand brand = brandMapper.selectByPrimaryKey(brandId);
        if (brand != null) {
            item.setBrand(brand.getName());
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        //重新设置商品状态
        goods.getTbGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(goods.getTbGoods());
        tbGoodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
        //删除原有的sku
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
        itemMapper.deleteByExample(example);
        //添加新的sku
        saveItemList(goods);
    }

    //将sku列表插入的代码提取出来，封装到私有方法中
    private void saveItemList(Goods goods) {
        if ("1".equals(goods.getTbGoods().getIsEnableSpec())) {
            //保存sku数据
            List<TbItem> itemList = goods.getItemList();
            if (itemList != null && itemList.size() > 0) {
                //遍历sku集合
                for (TbItem item : itemList) {
                    //获取商品标题
                    String title = goods.getTbGoods().getGoodsName();
                    //获取对应规格 {"网络":"移动3G","机身内存":"16G"}
                    Map specMap = JSON.parseObject(item.getSpec(), Map.class);
                    //遍历map
                    for (Object key : specMap.keySet()) {
                        title += " " + specMap.get(key);
                    }
                    System.out.println("title:" + title);
                    item.setTitle(title);

                    //设置属性值
                    setItemValues(goods, item);

                    //保存sku数据到数据库

                    itemMapper.insert(item);
                }
            }
        } else {
            //不启用规格
            TbItem item = new TbItem();
            //设置标题
            item.setTitle(goods.getTbGoods().getGoodsName());
            //设置价格
            item.setPrice(goods.getTbGoods().getPrice());
            //设置状态
            item.setStatus("1");
            //设置默认库存
            item.setNum(9999);
            //是否默认
            item.setIsDefault("1");
            //设置规格
            item.setSpec("{}");
            //设置属性
            setItemValues(goods, item);
            itemMapper.insert(item);
        }
    }

            /**
             * 根据ID获取实体
             * @param id
             * @return
             */
            @Override
            public Goods findOne (Long id){
                Goods goods = new Goods();
                TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
                goods.setTbGoods(tbGoods);
                TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
                goods.setTbGoodsDesc(tbGoodsDesc);
//查询sku
                TbItemExample example = new TbItemExample();
                TbItemExample.Criteria criteria = example.createCriteria();
                criteria.andGoodsIdEqualTo(id);
                List<TbItem> itemList = itemMapper.selectByExample(example);
                goods.setItemList(itemList);
                return goods;
            }

            /**
             * 批量删除
             */
            @Override
            public void delete (Long[]ids){
                for (Long id : ids) {
                    TbGoods tbGoods=goodsMapper.selectByPrimaryKey(id);
                    tbGoods.setIsDelete("1");
                    goodsMapper.updateByPrimaryKey(tbGoods);
                }
                //修改sku为删除状态
                List<TbItem> listitem = findItemListByGoodsIdandStatus(ids, "1");
                for (TbItem tbItem : listitem) {
                    tbItem.setStatus("3");
                    itemMapper.updateByPrimaryKey(tbItem);
                }

            }


            @Override
            public PageResult findPage (TbGoods goods,int pageNum, int pageSize){
                PageHelper.startPage(pageNum, pageSize);

                TbGoodsExample example = new TbGoodsExample();
                Criteria criteria = example.createCriteria();

                if (goods != null) {
                    if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                        criteria.andSellerIdEqualTo(goods.getSellerId());
                    }
                    if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                        criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
                    }
                    if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                        criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
                    }
                    if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                        criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
                    }
                    if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                        criteria.andCaptionLike("%" + goods.getCaption() + "%");
                    }
                    if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                        criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
                    }
                    if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                        criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
                    }
                    if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                        criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");

                    }
                }
                criteria.andIsDeleteIsNull();
                Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
                return new PageResult(page.getTotal(), page.getResult());
            }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
            //修改sku状态
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<TbItem> itemList = itemMapper.selectByExample(example);
            for (TbItem item : itemList) {
                item.setStatus(status);
                itemMapper.updateByPrimaryKey(item);
            }

        }
    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        criteria.andStatusEqualTo(status);
        return itemMapper.selectByExample(tbItemExample);
    }




}
