package com.offcn.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.Map;


public class MDoc {

	//读取加载ftl文件
	private Configuration configuration = null;

	public MDoc() {
		configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
	}

	/*
	 * 参数一dataMap：将要在doc上显示的数据保存在map里
	 * 参数二fileName：指定生成文件的绝对路径
	 */
	public void createDoc(Map<String,Object> dataMap,File f) throws UnsupportedEncodingException {

		Template t=null;
		try {

			/*
			  第一步：获取并读取模板
			  1.获取模板文件所在的包路径
			  2.从模板文件所在的包路径中获取指定的模板
			 */
			configuration.setClassForTemplateLoading(this.getClass(), "/com/offcn/template");
			t = configuration.getTemplate("arc.ftl");


			/*
			  第二步：获取写文件的字节缓存流
			  1.文件输出流
			  2.文件输出流---->字符流
			  3.字符流------>缓存流
			 */
			FileOutputStream fos = new FileOutputStream(f);
			OutputStreamWriter oWriter = new OutputStreamWriter(fos,"UTF-8");//字符流转化为字节流
			Writer out = new BufferedWriter(oWriter);////为字符输出流添加缓存的功能


			/*
			  第三步：将要会改变的数据(dataMap)以输出流(out)的方式写到模板t中
			 */
			t.process(dataMap, out);
			out.close();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
