package com.edgar.redis.lua;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2015/12/14.
 *
 * @author Edgar  Date 2015/12/14
 */
public class TokenBucket2Test {
  public static void main(String[] args) throws InterruptedException, IOException {
    Jedis jedis = new Jedis("10.11.0.31");
    System.out.println(new TokenBucket(jedis, 1, 10,  "192.168.1.100").acquire(8));
    System.out.println(new TokenBucket(jedis, 1, 10,  "192.168.1.100").acquire(4));
    TimeUnit.SECONDS.sleep(3);
    System.out.println(new TokenBucket(jedis, 1, 10, "192.168.1.100").acquire(4));
    TimeUnit.SECONDS.sleep(3);
    System.out.println(new TokenBucket(jedis, 1, 10, "192.168.1.100").acquire(4));
    System.out.println(new TokenBucket(jedis, 1, 10, "192.168.1.100").acquire(1));

    TimeUnit.SECONDS.sleep(15);
    System.out.println(new TokenBucket(jedis, 1, 10, "192.168.1.100").acquire(1));
  }
}
