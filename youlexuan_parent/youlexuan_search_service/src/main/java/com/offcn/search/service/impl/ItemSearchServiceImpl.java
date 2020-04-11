package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.junit.rules.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class ItemSearchServiceImpl implements ItemSearchService
{
    @Autowired
    SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        //处理空格
        String keywords =(String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));
        HashMap<String, Object> map= new HashMap<String, Object>();
        /*SimpleQuery query = new SimpleQuery();
        // is：基于分词后的结果 和 传入的参数匹配
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //添加查询条件
        query.addCriteria(criteria);
        query.setOffset(0);
        query.setRows(100);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        map.put("rows",page.getContent());*/
        map.putAll(searchList(searchMap));

        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //根据商品类目查询对应品牌规格
        String categoryName =(String) searchMap.get("category");
        if(!"".equals(categoryName)){
            map.putAll(searchBrandAndSpecList(categoryName));
        }else {
            if (categoryList.size() > 0) {
                Map mapBrandAndSpec = searchBrandAndSpecList((String) categoryList.get(0));
                map.putAll(mapBrandAndSpec);
            }
        }
        return map;
    }

    @Override
    public void importList(List<TbItem> itemList) {
        for (TbItem item : itemList) {
            System.out.println(item.getTitle());
            Map<String,String> specMap = JSON.parseObject(item.getSpec(), Map.class);
           Map<String,String> map = new HashMap();
            for  (String key : specMap.keySet()) {
                map.put("item_spec_"+Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
            }
            item.setSpecMap(map);
        }
        solrTemplate.saveBean(itemList);
        solrTemplate.commit();
        ;

    }

    @Override
    public void deleByGoodsIds(List<Long> goodsList) {
        System.out.println("删除商品ID"+goodsList);
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goods_id").in(goodsList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private Map<String,Object> searchList(Map searchMap){
        Map map=new HashMap();
        //创建高亮查询
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //创建高亮选项对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置需要高亮处理字段
        highlightOptions.addField("item_title");
        //设置高亮前缀，与后缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        //关联高亮选项到高亮查询器对象
        query.setHighlightOptions(highlightOptions);
        //设定查询条件 根据关键字查询
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //关联查询条件到查询器对象
        query.addCriteria(criteria);
        //筛选过滤
        if (searchMap != null && searchMap.get("category").equals("")) {
            //查询对象
            Criteria criteriaCategory = new Criteria("item_category").is(searchMap.get("category"));
            //过滤对象
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteriaCategory);
            query.addFilterQuery(simpleFilterQuery);
        }
        //二、增加按照商品品牌 进行筛选
        if(searchMap!=null&&!searchMap.get("brand").equals("")){
            //创建查询条件
            Criteria criteriaBrand = new Criteria("item_brand").is(searchMap.get("brand"));
            //不要直接关联到查询器对象
            //创建过滤器对象
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteriaBrand);
            //关联过滤器对象到查询器对象
            query.addFilterQuery(simpleFilterQuery);
        }


//------------------------------用不了无拼音包
        if (searchMap!=null&&searchMap.get("spec")!=null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            if (searchMap != null) {
                for (String key : specMap.keySet()) {
                    Criteria criteriaSpec = new Criteria("item_spec_" + Pinyin.toPinyin(key, "").toLowerCase()).is(specMap.get(key));
                    SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteriaSpec);
                    query.addFilterQuery(simpleFilterQuery);
                }
            }
        }
       //价格区间搜索
       if(!"".equals(searchMap.get("price"))){
           String[] prices = ((String) searchMap.get("price")).split("-");
           if(!prices[0].equals("0")){//如果区间起点不是0
               Criteria firstCriteria = new Criteria("item_price").greaterThan(prices[0]);
               SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(firstCriteria);
               query.addFilterQuery(simpleFilterQuery);
           }
           if(!prices[1].equals("*")){
               Criteria filterCriteria=new Criteria("item_price").lessThanEqual(prices[1]);
               FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
               query.addFilterQuery(filterQuery);
           }

       }
    //价格升序，降序
        String sortValue = (String) searchMap.get("sort");//(ASC DEsc)
        String sortField = (String)searchMap.get("sortField");//排序字段
        if( sortValue!=null && !sortValue.equals("")){
           if(sortValue.equals("ASC")){
               Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
               query.addSort(sort);
           }
           if(sortValue.equals("DESC")){
               Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
               query.addSort(sort);
           }
        }


        //发出高亮查询
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获取查询结果记录集合
        List<TbItem> list = highlightPage.getContent();
        //循环集合对象
        for (TbItem item : list) {
            //获取到针对对象的高亮集合
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(item);
            if(highlights!=null&&highlights.size()>0){
                //获取第一个字段高亮对象
                List<String> highlightSnipplets = highlights.get(0).getSnipplets();
                System.out.println("高亮："+highlightSnipplets.get(0));
                //使用高亮结果替换商品标题
                item.setTitle(highlightSnipplets.get(0));
            }
        }
        map.put("rows",list);
        return map;
    }
    private List searchCategoryList(Map searchMap){
        ArrayList list = new ArrayList();
        SimpleQuery query = new SimpleQuery();
        //按关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项 注意商品分类不能设置分词，要不然分组结果会失败
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //根据分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());////将分组结果的名称封装到返回值中
        }
    return list;
    }

    private Map searchBrandAndSpecList(String category){
        HashMap map = new HashMap();
        Long typeId= (Long)redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId!=null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            //返回值添加品牌列表
            // 根据模板 ID 查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId); map.put("specList", specList);
        }
        return map;

    }
}
