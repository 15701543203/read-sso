package com.web.read.sso.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.web.read.sso.dao.JedisDao;

import redis.clients.jedis.JedisCluster;


@Component
public class JedisClientCluster implements JedisDao {

	@Autowired
	private JedisCluster jedisCluster;
	
	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}

	@Override
	public String set(String key, String value) {
		return jedisCluster.set(key, value);
	}

	@Override
	public String hget(String hkey, String key) {
		return jedisCluster.hget(hkey, key);
	}

	@Override
	public long hset(String hkey, String key, String value) {
		return jedisCluster.hset(hkey, key, value);
	}

	@Override
	public long incr(String key) {
		return jedisCluster.incr(key);
	}

	@Override
	public long expire(String key, int second) {
		return jedisCluster.expire(key, second);
	}

	@Override
	public long ttl(String key) {
		return jedisCluster.ttl(key);
	}

	@Override
	public long hdel(String hashKey, String field) {
		return jedisCluster.hdel(hashKey, field);
	}

}
