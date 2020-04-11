package com.offcn.user.service;
import java.util.List;
import com.offcn.pojo.TbUser;

import com.offcn.entity.PageResult;
/**
 * 用户表服务层接口
 * @author Administrator
 *
 */
public interface UserService {


	 List<TbUser> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
 void add(TbUser user);
	
	
	/**
	 * 修改
	 */
 void update(TbUser user);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
 TbUser findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	 void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	 PageResult findPage(TbUser user, int pageNum, int pageSize);
	   void CreateSmsCode(String mobile);
	   boolean checkSmsCode(String mobile,String smsCode);
	
}
