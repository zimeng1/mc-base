package com.mc.base.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisManager {

	@Resource
	private RedisTemplate<String, Object> redisTemplate;


	public final <K> String concatKey(List<K> keys) {
		return this.concatKey(keys, ":");
	}

	public final <K> String concatKey(List<K> keys, String delimiter) {
		return (String) keys.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
	}

	public final <K> String concatKeyWithPrefix(String prefix, List<K> keys) {
		return this.concatKeyWithPrefix(prefix, keys, ":");
	}

	public final <K> String concatKeyWithPrefix(String prefix, List<K> keys, String delimiter) {
		return prefix + (String) keys.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
	}

	public <K, V> void set(String prefix, K key, V value) {
		this.redisTemplate.opsForValue().set(prefix + key.toString(), value);
	}

	public <K, V> void set(String prefix, K key, V value, Long expireTime, TimeUnit timeUnit) {
		this.redisTemplate.opsForValue().set(prefix + key.toString(), value, expireTime, timeUnit);
	}

	public <K, V> void multiSet(String prefix, Map<K, V> map) {
		Map<String, V> kAndV = (Map) map.entrySet().stream().collect(Collectors.toMap((e) -> {
			return prefix + e.getKey().toString();
		}, Map.Entry::getValue));
		this.redisTemplate.opsForValue().multiSet(kAndV);
	}

	public <K, V> Boolean setIfAbsent(String prefix, K key, V value) {
		return this.redisTemplate.opsForValue().setIfAbsent(prefix + key.toString(), value);
	}

	public <K, V> Boolean setIfAbsent(String prefix, K key, V value, Long expireTime, TimeUnit timeUnit) {
		return this.redisTemplate.opsForValue().setIfAbsent(prefix + key.toString(), value, expireTime, timeUnit);
	}

	public <K, V> V get(String prefix, K key, Class<V> clazz) {
		if (key == null) {
			return null;
		}
		String redisKey = prefix + key.toString();
		Object rawValue = this.redisTemplate.opsForValue().get(redisKey);
		if (rawValue == null) {
			// Or throw an exception or provide a default value, depending on your requirements.
			return null;
		}
		return this.convertType(clazz, rawValue);
	}

	public <K, V> Map<K, V> multiGet(String prefix, List<K> keys, Class<V> clazz) {
		List<String> keyList = (List) keys.stream().map((key) -> {
			return prefix + key.toString();
		}).collect(Collectors.toList());
		List<Object> objects = this.redisTemplate.opsForValue().multiGet(keyList);
		return this.getKvMap(keys, objects, clazz);
	}

	private <K, V> Map<K, V> getKvMap(List<K> keys, List<Object> objects, Class<V> clazz) {
		if (objects != null && !objects.isEmpty()) {
			Map<K, V> map = new HashMap(keys.size());

			for (int i = 0; i < keys.size(); ++i) {
				map.put(keys.get(i), this.convertType(clazz, objects.get(i)));
			}

			return map;
		} else {
			return Collections.emptyMap();
		}
	}

	public <K> Boolean isExist(String prefix, K key) {
		return this.redisTemplate.hasKey(prefix + key.toString());
	}

	public <K> void setKeyExpireTime(String prefix, K key, long expireTime, TimeUnit timeUnit) {
		this.redisTemplate.expire(prefix + key.toString(), expireTime, timeUnit);
	}

	public <K> void delete(String prefix, K key) {
		this.redisTemplate.delete(prefix + key.toString());
	}

	public <K> Long increment(String prefix, K key, int delta) {
		return this.redisTemplate.opsForValue().increment(prefix + key.toString(), (long) delta);
	}

	public <K> Long decrement(String prefix, K key, int delta) {
		return this.redisTemplate.opsForValue().decrement(prefix + key.toString(), (long) delta);
	}

	public <K, HK, V> void hashSet(String prefix, K key, HK hashKey, V value) {
		this.redisTemplate.opsForHash().put(prefix + key.toString(), hashKey, value);
	}

	public <K, HK, V> void hashMultiSet(String prefix, K key, Map<HK, V> map) {
		this.redisTemplate.opsForHash().putAll(prefix + key.toString(), map);
	}

	public <K, HK, V> V hashGet(String prefix, K key, HK hashKey, Class<V> clazz) {
		return this.convertType(clazz, this.redisTemplate.opsForHash().get(prefix + key.toString(), hashKey));
	}

	public <K, HK, V> Map<HK, V> hashMultiGet(String prefix, K key, List<HK> hashKeys, Class<V> clazz) {
		List<Object> keyList = (List) hashKeys.stream().map((h) -> {
			return h;
		}).collect(Collectors.toList());
		List<Object> objects = this.redisTemplate.opsForHash().multiGet(prefix + key.toString(), keyList);
		return this.getKvMap(hashKeys, objects, clazz);
	}

	public <K, HK> Long hashDelete(String prefix, K key, List<HK> hashKeys) {
		return this.redisTemplate.opsForHash().delete(prefix + key.toString(), hashKeys.toArray());
	}

	public <K> Long getHashSize(String prefix, K key) {
		return this.redisTemplate.opsForHash().size(prefix + key.toString());
	}

	public <K, HK> Boolean isHashExist(String prefix, K key, HK hashKey) {
		return this.redisTemplate.opsForHash().hasKey(prefix + key.toString(), hashKey);
	}

	public <K, V> Long addToSet(String prefix, K key, V value) {
		return this.redisTemplate.opsForSet().add(prefix + key.toString(), new Object[]{value});
	}

	public <K, V> Long removeFromSet(String prefix, K key, V value) {
		return this.redisTemplate.opsForSet().remove(prefix + key.toString(), new Object[]{value});
	}

	public <K, V> Long batchRemoveFromSet(String prefix, K key, Set<V> values) {
		return this.redisTemplate.opsForSet().remove(prefix + key.toString(), values.toArray());
	}

	public <K, V> Long batchAddToSet(String prefix, K key, Set<V> values) {
		return this.redisTemplate.opsForSet().add(prefix + key.toString(), values.toArray());
	}

	public <K, V> Boolean isMember(String prefix, K key, V value) {
		return this.redisTemplate.opsForSet().isMember(prefix + key.toString(), value);
	}

	public <K, V> Set<V> members(String prefix, K key) {
		return (Set<V>) this.redisTemplate.opsForSet().members(prefix + key.toString());
	}

	public <K> Long getSetSize(String prefix, K key) {
		return this.redisTemplate.opsForSet().size(prefix + key.toString());
	}

	public <K, V> List<V> popFromSet(String prefix, K key, int count, Class<V> clazz) {
		return (List) Optional.ofNullable(this.redisTemplate.opsForSet().pop(prefix + key.toString(), (long) count)).map((list) -> {
			return (List) list.stream().map((e) -> {
				return this.convertType(clazz, e);
			}).collect(Collectors.toList());
		}).orElse(Collections.emptyList());
	}

	private <V> V convertType(Class<V> clazz, Object e) {
		if (e != null && clazz.isInstance(e)) {
			return clazz.cast(e);
		} else {
			return clazz == Long.class && e instanceof Number ? clazz.cast(Long.valueOf(e.toString())) : null;
		}
	}

	public <K, V> Long batchAddToList(String prefix, K key, List<V> values) {
		return this.redisTemplate.opsForList().leftPushAll(prefix + key.toString(), values.toArray());
	}

	public <K, V> Long addToList(String prefix, K key, V value) {
		return this.redisTemplate.opsForList().leftPush(prefix + key.toString(), value);
	}

	public <K, V> V popFromList(String prefix, K key, Class<V> clazz) {
		return clazz.cast(this.redisTemplate.opsForList().rightPop(prefix + key.toString()));
	}

	public <K, V> List<V> queryAllFromList(String prefix, K key, Class<V> clazz) {
		return (List) ((List) Optional.ofNullable(this.redisTemplate.opsForList().range(prefix + key.toString(), 0L, -1L)).orElse(Collections.emptyList())).stream().map((val) -> {
			return this.convertType(clazz, val);
		}).collect(Collectors.toList());
	}

	public <K> Long getListSize(String prefix, K key) {
		return this.redisTemplate.opsForList().size(prefix + key.toString());
	}

	public <T> Boolean addToZSet(String redisKey, T obj, double score) {
		return this.redisTemplate.opsForZSet().add(redisKey, obj, score);
	}

	public <T> Double scoreZSet(String redisKey, T obj) {
		return this.redisTemplate.opsForZSet().score(redisKey, obj);
	}

	/**
	 * 移除key相关的成员
	 */
	public Long removeRangeZSet(String redisKey, long start, long end) {
		return this.redisTemplate.opsForZSet().removeRange(redisKey, start, end);
	}

	public Long removeZSet(String redisKey, Object... values) {
		return this.redisTemplate.opsForZSet().remove(redisKey, values);
	}

	/**
	 * 移除key相关的成员
	 */
	public Long removeRangeByScoreZSet(String redisKey, double min, double max) {
		return this.redisTemplate.opsForZSet().removeRangeByScore(redisKey, min, max);
	}

	public Set rangeZSet(String redisKey, long start, long end) {
		return this.redisTemplate.opsForZSet().range(redisKey, start, end);
	}

	/**
	 * 按照分数对指定区间取值
	 */
	public Set rangeByScoreWithScoresZSet(String redisKey, long min, long max) {
		return this.redisTemplate.opsForZSet().rangeByScoreWithScores(redisKey, min, max);
	}

	/**
	 * 按照分数对指定区间取值
	 */
	public Set rangeByScoreZSet(String redisKey, long min, long max) {
		return this.redisTemplate.opsForZSet().rangeByScore(redisKey, min, max);
	}

	public <V> V executeScript(String script, List<String> keys, List<Object> args, Class<V> returnType) {
		DefaultRedisScript<V> redisScript = new DefaultRedisScript(script, returnType);
		return this.redisTemplate.execute(redisScript, keys, args.toArray());
	}

	/**
	 * 生成key
	 */
	public String generateKey(String prefix, String... args) {
		String key = prefix;
		for (String item : args) {
			if (item != null) {
				key += ":" + item;
			}
		}
		return key;
	}
}
