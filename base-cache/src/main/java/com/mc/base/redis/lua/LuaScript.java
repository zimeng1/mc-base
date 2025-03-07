package com.mc.base.redis.lua;

public interface LuaScript {

	/**
	 *  释放锁使用lua脚本
	 *  适用于jedis客户端
	 */
	String releaseLockScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
	/**
	 *  获取锁使用lua脚本
	 *  适用于jedis客户端
	 */
	String lockScript = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";

	/**
	 * 获取锁使用lua脚本
	 * 适用于lettuce客户端
	 */
	String LETTUCE_LOCK_SCRIPT = "if redis.call('set', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2]) then return 1 else return 0 end";

	/**
	 *  释放锁使用lua脚本
	 *  适用于lettuce客户端
	 */
	String LETTUCE_RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
}
