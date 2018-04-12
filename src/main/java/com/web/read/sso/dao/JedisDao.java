package com.web.read.sso.dao;

public interface JedisDao {
	
	/**
	 * 获取string类型的值
	 * @param key
	 * @return
	 */
	String get(String key);
	
	/**
	 * 设置一个string类型的键值对
	 * @param key
	 * @param value
	 * @return
	 */
	String set(String key, String value);
	
	/**
	 * 获取hash类型的值的字段值
	 * @param hkey 键值对的key
	 * @param key 键值对的值的字段名称
	 * @return
	 */
	String hget(String hkey, String key);
	
	/**
	 * 设置一个hash类型的值
	 * @param hkey 键值对的key
	 * @param key 键值对的value的字段名称
	 * @param value 键值对的value的字段值
	 * @return
	 */
	long hset(String hkey, String key, String value);
	
	/**
	 * 原子操作，给string类型的值加一
	 * @param key
	 * @return
	 */
	long incr(String key);

	/**
	 * 给string类型的键值对设置过期时间
	 * @param key 键值对的key
	 * @param second 秒为单位的时间
	 * @return
	 */
	long expire(String key, int second);
	
	/**
	 * 键值对剩余的生存时间
	 * @param key
	 * @return
	 */
	long ttl(String key);
	
	long hdel(String hashKey, String field);

}
