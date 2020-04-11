package com.offcn.user;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.mail.MailParseException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class loginController {
    @RequestMapping("/showLoginName")
    public Map name(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map=new HashMap();
        map.put("loginName",name);
        return map;
    }
}
