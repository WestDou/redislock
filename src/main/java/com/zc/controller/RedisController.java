package com.zc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zc.redislock.RedisLock;
import com.zc.util.RedisLockUtil;

/**
 * @Description: TODO
 * @author: zhangcheng
 * @date: 2019年3月18日
 */
@Controller
public class RedisController {

	@Autowired
	private RedisLockUtil redisLockUtil;

	@ResponseBody
	@RequestMapping("/redisLock")
	public String redisLock() {
		System.out.println("请求来了");
		String value = System.currentTimeMillis() + 1000000000000000l + "";
		if (redisLockUtil.lock("1", value)) {
			System.out.println("执行中...");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("执行完成");
			redisLockUtil.unlock("1", value);
			return "执行完成";
		} else {
			return "另外一个正在执行！！！";
		}
	}

}
