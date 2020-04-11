package com.offcn;

import com.offcn.solrutil.SolrUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppStart {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil)context.getBean("solrUtil");
        solrUtil.importItemDate();
    }
}
