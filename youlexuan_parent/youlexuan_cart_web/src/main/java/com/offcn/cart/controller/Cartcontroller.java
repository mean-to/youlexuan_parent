package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.entity.Result;
import com.offcn.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class Cartcontroller {
    @Reference(timeout = 300000)
    private CartService cartService;
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString==null||cartListString.equals("")){
            cartListString="[]";
        }
        List<Cart> cartList_Cookie = JSON.parseArray(cartListString, Cart.class);
        if(name.equals("anonymousUser")){
            //未登录购物车列表
            return cartList_Cookie;
        }else {
            //已经登录
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
            if(cartList_Cookie.size()>0){
                //合并
                List<Cart> cartList = cartService.mergeCartList(cartListFromRedis, cartList_Cookie);
                //清空cookie
                CookieUtil.deleteCookie(request,response,"cartList");
                //将合并后的数据存入redis
                cartService.saveCartListToRedis(name,cartListFromRedis);
            }
            return  cartListFromRedis;
        }
    }
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num,HttpServletRequest request,HttpServletResponse response){
        //判断是否登录，未登录时anonymousUser
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
           List<Cart> cartList = findCartList(request, response);//获取购物车列表
           cartList = cartService.addGoodsToCartList(cartList, itemId, num);
           if(name.equals("anonymousUser")){
               CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
           }else {
               cartService.saveCartListToRedis(name,cartList);
           }
            return new Result(true, "添加成功");

       }catch (Exception e){
           e.printStackTrace();
           return new Result(false,"添加失败");
       }
    }
}
