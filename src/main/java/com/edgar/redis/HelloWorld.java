package com.edgar.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by Edgar on 2015/12/14.
 *
 * @author Edgar  Date 2015/12/14
 */
public class HelloWorld {
  public static void main(String[] args) {
    Jedis jedis = new Jedis("10.4.14.60");
    jedis.set("foo", "bar");
    String value = jedis.get("foo");
    System.out.println(value);
  }
}
