package com.offcn.content.controller;
import java.util.List;

import com.offcn.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbContent;
import com.offcn.content.service.ContentService;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/content")
public class ContentController {

	@Reference
	private ContentService contentService;
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbContent> findAll(){			
		return contentService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return contentService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param content
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbContent content){
		try {
			contentService.add(content);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param content
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbContent content){
		try {
			contentService.update(content);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbContent findOne(Long id){
		return contentService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			contentService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbContent content, int page, int rows  ){
		return contentService.findPage(content, page, rows);		
	}
	@RequestMapping("/upload")
	public Result upload(MultipartFile file)  {
		//1,取文件的扩展名
		String originalFilename = file.getOriginalFilename();
		String exName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		//2.创建一个FastDFS的客户端
		FastDFSClient fastDFSClient = null;
		try {
			fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			//3,执行上传处理
			String fileId = fastDFSClient.uploadFile(file.getBytes(), exName);
			//4,拼接返回的url和ip地址，
			String url = FILE_SERVER_URL+fileId;
			return new Result(true,url);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"上传失败");
		}
	}
	
}
