package com.offcn.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.group.Goods;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbItemExample;
import com.offcn.sellergoods.service.GoodsService;
import com.offcn.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout = 3000000)
	private GoodsService goodsService;
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getTbGoods().setSellerId(name);
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		//增加验证是否是当前商家的操作
		Goods goods2 = goodsService.findOne(goods.getTbGoods().getId());
		//获取当前商家的id
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		//如果传递过来的商家ID并不是当前登录用户的ID。则属于非法操作
		if(!goods2.getTbGoods().getSellerId().equals(sellerId)
		||!goods.getTbGoods().getSellerId().equals(sellerId)){
			return new Result(false,"非法操作");
		}


		try {
			goodsService.update(goods);
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
	public Goods findOne(Long id){
		return goodsService.findOne(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);
		return goodsService.findPage(goods, page, rows);		
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
