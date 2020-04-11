package com.offcn.service;

import com.offcn.pojo.TbSeller;
import com.offcn.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

public class UserDetailServiceImpl implements UserDetailsService {

private SellerService sellerService;
 public  void setSellerService(SellerService sellerService){
     this.sellerService=sellerService;
 }
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过了UserDetailServiceImpl");
        ArrayList<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //得到商家对象
        TbSeller seller = sellerService.findOne(username);
        System.out.println(seller.getPassword());
        if(seller!=null){
            if(seller.getStatus().equals("1")){
                return new User(username,seller.getPassword(),grantedAuths);
            }else {
                return null;
            }
        }else {
            return null;
        }

    }

}
