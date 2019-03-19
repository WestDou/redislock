package com.zc.redislock;

import java.util.Collections;
import java.util.UUID;

import redis.clients.jedis.Jedis;

/**
 * @Description: TODO
 * @author: zhangcheng
 * @date: 2019年3月14日
 */
public class RedisLock {

	private static final String LOCK_SUCCESS = "OK";
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_WITH_EXPIRE_TIME = "PX";
	private static final Long RELEASE_SUCCESS = 1L;

	public static int k = 0;

	public static void main(String[] args) {

		// setLock(jedis, lockKey, requestId, expireTime);
		for (int i = 0; i < 2; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Jedis jedis = new Jedis("47.100.52.42", 6379);
					jedis.auth("zcmyvideo");
					System.out.println(Thread.currentThread().getName() + "已经开启");
					String requiredId = UUID.randomUUID().toString();
					//如果没有取到锁就进行阻塞等待
					while (!setLock(jedis, k + "", requiredId, 10)) {
					}
					for (int j = 0; j < 5; j++) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println(j + Thread.currentThread().getName());
					}
					unLock(jedis, k+"", requiredId);
				}
			}).start();
		}
	}
	public static boolean setLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
		String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

		if (LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;

	}

	public static boolean unLock(Jedis jedis, String lockKey, String requestId) {

		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

		if (RELEASE_SUCCESS.equals(result)) {
			return true;
		}
		return false;

	}

}
