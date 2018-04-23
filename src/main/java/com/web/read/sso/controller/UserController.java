package com.web.read.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.web.read.bean.TbUser;
import com.web.read.common.bean.ReadResult;
import com.web.read.common.utils.ExceptionUtil;
import com.web.read.sso.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/check/{param}/{type}")
	@ResponseBody
	public Object check(@PathVariable String param, @PathVariable int type, String callback) {

		System.out.println("sso-8084用户登录校验[" + param + "]" + "[" + type + "]");

		ReadResult result = null;
		if (type != 1 && type != 2 && type != 3) {
			result = ReadResult.build(400, "校验内容错误，不知道是什么方式登录的");
		}

		// 校验出错
		if (result != null) {
			// 表示该请求是以ajax跨域请求
			if (callback != null) {
				MappingJacksonValue mjv = new MappingJacksonValue(callback);
				mjv.setJsonpFunction(callback);
				return mjv;
			} else {
				return result;
			}
		}

		try {
			// 调用服务
			result = userService.check(param, type);
		} catch (Exception e) {
			e.printStackTrace();
			result.build(400, ExceptionUtil.getStackTrace(e));
		}

		if (callback != null) {
			MappingJacksonValue mjv = new MappingJacksonValue(callback);
			mjv.setJsonpFunction(callback);
			return mjv;
		} else {
			return result;
		}

	}

	/**
	 * 注册 Description:
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/register")
	@ResponseBody
	public ReadResult createUser(TbUser user) {
		System.out.println("sso-8084用户注册[" + user + "]");

		ReadResult result = null;
		try {
			result = userService.createUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			return ReadResult.build(400, "注册失败. 请校验数据后请再提交数据.");
		}

		return result;
	}

	/**
	 * 用户登录 Description:
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE
			+ ";charset=utf-8")
	@ResponseBody
	public ReadResult userLogin(String username, String password, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("sso-8084用户登录 用户名[" + username + "],密码[" + password + "]");

		ReadResult result = null;
		try {
			result = userService.userLogin(username, password, request, response);
		} catch (Exception e) {
			e.printStackTrace();
			return ReadResult.build(401, ExceptionUtil.getStackTrace(e));
		}
		return result;
	}

	@RequestMapping(value = "/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token, String callback) {
		ReadResult result = null;

		System.out.println("sso-8084 token中获取用户信息 [" + token + "]");

		try {
			result = userService.getUserByToken(token);

		} catch (Exception e) {
			e.printStackTrace();
			result.build(500, ExceptionUtil.getStackTrace(e));
		}

		if (!StringUtils.isEmpty(callback)) {
			MappingJacksonValue value = new MappingJacksonValue(result);
			value.setJsonpFunction(callback);
			return value;
		} else {
			return result;
		}
	}

}
