import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import java.util.HashMap;
import java.util.List;


public class freemarker {
    public static <Map> void main(String[] args) {
        Configuration configuration = new Configuration(Configuration.getVersion());
        try {
            configuration.setDirectoryForTemplateLoading(new File("D:\\DaiMa\\freemarker_01\\src\\main\\resources"));
            
            configuration.setDefaultEncoding("utf-8");

            Template template = configuration.getTemplate("demo.ftl");
            HashMap modelmap = new HashMap();

            modelmap.put("name","小");
            List list=new ArrayList();

            HashMap  m1=new HashMap();
            m1.put("name","苹果");
            m1.put("price",5.88);

            HashMap m2=new HashMap();
            m2.put("name","香蕉");
            m2.put("price",6.79);

            HashMap m3=new HashMap();
            m3.put("name","葡萄");
            m3.put("price",4.38);
            m3.put("price",4.38);

            list.add(m1);
            list.add(m2);
            list.add(m3);
modelmap.put("list",list);
            FileWriter out = new FileWriter(new File("D:\\DaiMa\\freemarker_01\\src\\main\\webapp\\demo1.html"));
            template.process(modelmap,out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
