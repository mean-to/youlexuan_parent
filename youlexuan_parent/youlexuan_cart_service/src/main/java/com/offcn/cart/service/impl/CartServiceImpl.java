package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //根据商品sku iD查询Sku商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品状态无效");
        }
        String sellerId = item.getSellerId();
        //查询是否存在这个商家的购物车
        Cart cart = searchCartBySellerTd(cartList, sellerId);
        //判断如果不存在
        if (cart == null) {
            //新建这个商家的购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //该商家购物车商品清单
            TbOrderItem orderItem = createItem(item, num);
            List orderItemList = new ArrayList();
            //某个商品明细进商家商品明细
            orderItemList.add(orderItem);
            //商家进购物车
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }
        //购物车已经存在
        else {
            //判断购物车明细列表是否存在该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem==null){
                //没有这个商品
                orderItem = createItem(item, num);
                cart.getOrderItemList().add(orderItem);
            }else {
                //如果已经存在该商品，添加数量，更改钱数
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                //如果数量操作小于0，则移除
                if(orderItem.getNum()<=0){
                    //移除这个商品的明细
                    cart.getOrderItemList().remove(orderItem);
                }

                //如果移除后cart的这个商家商品明细数量为0
                if(cart.getOrderItemList().size()==0){
                    //移除这个商家
                    cartList.remove(cart);
                }

            }

        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        //从redis取出数据
        List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            cartList=new ArrayList<Cart>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_redis, List<Cart> cartList_cookie) {
        //合并购物车，把cookie中的所有商品信息读取出来存入cartlist_redis
        for (Cart cart : cartList_cookie) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                addGoodsToCartList(cartList_redis,orderItem.getItemId(),orderItem.getNum());
            }
        }

        return cartList_redis;
    }

    //查询是否有这个商家id
    private Cart searchCartBySellerTd(List<Cart> carList, String sellerId) {
        for (Cart cart : carList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    //商品明细
    TbOrderItem createItem(TbItem item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        //总价格
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return  orderItem;


    }
    //判断商品明细列表是否存在该商品
    TbOrderItem searchOrderItemByItemId(List<TbOrderItem> OrderItemList,Long itemId){
        for (TbOrderItem orderItem : OrderItemList) {
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }


}
