package com.zc.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Description: TODO
 * @author: zhangcheng
 * @date: 2019年3月18日
 */
@Component
public class RedisLockUtil {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 加锁
	 * 
	 * @param key
	 * @param value
	 *            当前时间+超时时间
	 * @return
	 */
	public boolean lock(String key, String value) {
		System.out.println("进行获取锁。。");
		if (stringRedisTemplate.opsForValue().setIfAbsent(key, value)) {
			System.out.println("获取锁成功");
			return true;
		}
		// currentValue=A 这两个线程的value都是B 其中一个线程拿到锁
		String currentValue = stringRedisTemplate.opsForValue().get(key);
		// 如果锁过期
		if (!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()) {
			// 获取上一个锁的时间
			String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);
			if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
				return true;
			}
		}
		System.out.println("获取锁失败");
		return false;
	}

	/**
	 * 解锁
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void unlock(String key, String value) {
		String currentVaule = stringRedisTemplate.opsForValue().get(key);
		try {
			if (!StringUtils.isEmpty(currentVaule) && currentVaule.equals(value)) {
				stringRedisTemplate.opsForValue().getOperations().delete(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
