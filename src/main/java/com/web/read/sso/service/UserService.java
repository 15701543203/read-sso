package com.web.read.sso.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.web.read.bean.TbUser;
import com.web.read.common.bean.ReadResult;

public interface UserService {
	/**
	 * 数据监测 Description: 主要用于校验是否违反了唯一性约束
	 * 
	 * @param content
	 *            用户名、邮箱、电话号码
	 * @param type
	 *            1(用户名)2(邮箱)3(电话号码)
	 * @return
	 */
	ReadResult check(String content, int type);

	/**
	 * 用户注册 Description:
	 * 
	 * @param user
	 *            用户实体bean(需要不全createDate和updateDate)
	 * @return
	 */
	ReadResult createUser(TbUser user);

	/**
	 * 登录 Description:
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return 包含token的readresult
	 */
	ReadResult userLogin(String username, String password, HttpServletRequest request, HttpServletResponse response);

	/**
	 * 查询用户token Description: 根据token查询用户信息，看用户登录是否过期
	 * 
	 * @param token
	 * @return
	 */
	ReadResult getUserByToken(String token);
}
