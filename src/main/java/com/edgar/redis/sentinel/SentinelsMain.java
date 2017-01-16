package com.edgar.redis.sentinel;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Edgar on 2016/6/24.
 *
 * @author Edgar  Date 2016/6/24
 */
public class SentinelsMain {

  public static void main(String[] args) {
    Set<String> sentinels = new HashSet<String>();
    sentinels.add("10.4.7.220:26379");
    sentinels.add("10.4.7.221:26379");
    sentinels.add("10.4.7.222:26379");

    JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels, "123");
    Jedis jedis = jedisSentinelPool.getResource();
    String replication = jedis.info("replication");
    System.out.println(replication);

    jedis.close();
    jedisSentinelPool.close();;
  }
}
