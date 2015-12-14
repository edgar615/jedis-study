package com.edgar.redis;

import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Created by Edgar on 2015/12/14.
 *
 * @author Edgar  Date 2015/12/14
 */
public class ZSet {
  public static void main(String[] args) {
    Jedis jedis = new Jedis("10.4.14.60");
    jedis.zadd("zyz-test", 1, "foo");
    jedis.zadd("zyz-test", 0, "bar");
    jedis.zadd("zyz-test", 2, "hoho");
    Set<String> value = jedis.zrangeByScore("zyz-test", 0d, 1d);
    System.out.println(value);
  }
}
