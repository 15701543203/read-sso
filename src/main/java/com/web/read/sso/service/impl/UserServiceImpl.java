package com.web.read.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.alibaba.druid.util.StringUtils;
import com.web.read.bean.TbUser;
import com.web.read.bean.TbUserExample;
import com.web.read.bean.TbUserExample.Criteria;
import com.web.read.common.bean.ReadResult;
import com.web.read.common.utils.CookieUtils;
import com.web.read.common.utils.JsonUtils;
import com.web.read.mapper.TbUserMapper;
import com.web.read.sso.dao.JedisDao;
import com.web.read.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired
	private JedisDao jedisClient;

	@Value("${REDIS_USER_TOKEN}")
	private String REDIS_USER_TOKEN;

	@Value("${REDIS_USER_TOKEN_EXPIRE}")
	private int REDIS_USER_TOKEN_EXPIRE;

	@Override
	public ReadResult check(String content, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();

		//用户名校验，判断用户是以那种方式登录
		if (type == 1) {
			criteria.andUsernameEqualTo(content);
		} else if (type == 2) {
			criteria.andPhoneEqualTo(content);
		} else {
			criteria.andEmailEqualTo(content);
		}
		
		//查询
		List<TbUser> userList = userMapper.selectByExample(example);
		if (userList == null || userList.size() <= 0) {
			return ReadResult.ok(true);
		} else {
			return ReadResult.ok(false);
		}
	}

	@Override
	public ReadResult createUser(TbUser user) {
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//使用MD5加密
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		userMapper.insert(user);
		return ReadResult.ok();
	}

	@Override
	public ReadResult userLogin(String username, String password, HttpServletRequest request, HttpServletResponse response) {

		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> userList = userMapper.selectByExample(example);
		
		//如果没有这个用户直接返回不存在
		if (userList == null || userList.size() <= 0) {
			return ReadResult.build(401, "用户不存在或账号密码错误");
		}

		TbUser user = userList.get(0);
		if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
			return ReadResult.build(401, "账号或密码错误");
		}

		//生成token
		String token = UUID.randomUUID().toString();
		//将密码设置为空
		user.setPassword(null);
		//将登录成功的用户信息写到缓存当中，并用token作为key
		jedisClient.set(REDIS_USER_TOKEN + ":" + token, JsonUtils.objectToJson(user));
		//设置过期时间
		jedisClient.expire(REDIS_USER_TOKEN_EXPIRE + ":" + token, REDIS_USER_TOKEN_EXPIRE);
		
		//将token写到cookie中
		CookieUtils.setCookie(request, response, "EGO_TOKEN", token);
		
		return ReadResult.ok(token);
	}

	@Override
	public ReadResult getUserByToken(String token) {
		//获取缓存中的值
		String json = jedisClient.get(REDIS_USER_TOKEN+":"+token);
		//查看是否过期
		if (StringUtils.isEmpty(json)) {
			return ReadResult.build(400, "密码失效，需要重新登录");
		}
		jedisClient.expire(REDIS_USER_TOKEN+":"+token, REDIS_USER_TOKEN_EXPIRE);
		return ReadResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
	}

}
