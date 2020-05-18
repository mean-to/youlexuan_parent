package com.offcn.auth.controller;

import com.offcn.pro.bean.Employee;
import com.offcn.pro.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("emp")
public class EmpController {

    @Autowired
    EmpService es;

    @RequestMapping("login")
    public String login(Model m, String username, String password, HttpSession session){
        Employee emp = es.login(username, password);
        if(emp!=null){
            //成功
            session.setAttribute("emp",emp);
            return "main/index";
        }else{
            m.addAttribute("msg","请检查账号或密码！");
            return "main/login";
        }
    }

}
