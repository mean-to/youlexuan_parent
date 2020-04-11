package com.offcn.cart.service;

import com.offcn.entity.Cart;

import java.util.List;

public interface CartService {

    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);
    List<Cart> findCartListFromRedis(String name);
    public void saveCartListToRedis(String username,List<Cart> cartList);
    //合并购物车
    List<Cart> mergeCartList(List<Cart> cartList_redis,List<Cart> cartList_cookie);


}
