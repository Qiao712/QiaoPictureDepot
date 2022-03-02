package com.qiao.picturedepot.util;

import redis.clients.jedis.Jedis;

public class RedisUtil {
    private static Jedis jedis = new Jedis("8.141.151.176", 6379);
    static{
        jedis.auth("lty0712");
    }

    public static void set(String key, String value){
        jedis.set(key, value);
    }

    public static String get(String key){
        return jedis.get(key);
    }

    public static void addToSet(String set, String value){
        jedis.sadd(set, value);
    }

    //刷新过期时间
    public static void addToSet(String set, String value, long second){
        jedis.sadd(set, value);
        jedis.expire(set, second);
    }

    public static boolean contains(String set, String value){
        return jedis.sismember(set, value);
    }
}
