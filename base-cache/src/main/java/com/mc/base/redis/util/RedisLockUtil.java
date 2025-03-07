package com.mc.base.redis.util;

import com.mc.base.redis.lua.LuaScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author huangying
 * @date 2024/8/26
 */
public class RedisLockUtil {
    private static final Long SUCCESS = 1L;
    private static final Long RELEASE_SUCCESS = 1L;
    private static final Logger log = LoggerFactory.getLogger(RedisLockUtil.class);

    /**
     * 尝试获取分布式锁
     *
     * @param redisTemplate Redis客户端
     * @param lockKey       lockKey 锁
     * @param requestId     requestId 请求标识
     * @param expireTime    超期时间 s
     * @return 是否获取成功
     */
    public static boolean tryLock(RedisTemplate redisTemplate, String lockKey, String requestId, int expireTime) {
        try {
            List<String> args = Arrays.asList(requestId, String.valueOf(expireTime));
            Long result = (Long) redisTemplate.execute((RedisCallback<Long>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                if (nativeConnection instanceof JedisCluster) {
                    // jedis 集群客户端
                    return (Long) ((JedisCluster) nativeConnection).eval(LuaScript.lockScript, Collections.singletonList(lockKey), args);
                } else if (nativeConnection instanceof Jedis) {
                    // jedis 单机客户端
                    return (Long) ((Jedis) nativeConnection).eval(LuaScript.lockScript, Collections.singletonList(lockKey), args);
                } else {
                    // lettuce客户端、其他客户端
                    RedisScript<Long> acquireLockScript = new DefaultRedisScript<>(LuaScript.LETTUCE_LOCK_SCRIPT, Long.class);
                    return (Long) redisTemplate.execute(acquireLockScript, Collections.singletonList(lockKey), requestId, expireTime);
                }
            });
            if (SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            log.error("tryLock error!", e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 释放分布式锁
     *
     * @param redisTemplate Redis客户端
     * @param lockKey       锁
     * @param requestId     请求标识
     * @return 是否释放成功
     */
    public static boolean releaseLock(RedisTemplate redisTemplate, String lockKey, String requestId) {
        try {
            List<String> args = Arrays.asList(requestId);
            Long result = (Long) redisTemplate.execute((RedisCallback<Long>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(LuaScript.releaseLockScript, Collections.singletonList(lockKey), args);
                } else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(LuaScript.releaseLockScript, Collections.singletonList(lockKey), args);
                } else {
                    // lettuce客户端
                    RedisScript<Long> acquireLockScript = new DefaultRedisScript<>(LuaScript.LETTUCE_RELEASE_LOCK_SCRIPT, Long.class);
                    return (Long) redisTemplate.execute(acquireLockScript, Collections.singletonList(lockKey), requestId);
                }
            });
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            log.error("releaseLock error!", e);
        }
        return false;
    }
}
