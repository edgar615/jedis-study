package com.edgar.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2015/12/14.
 *
 * @author Edgar  Date 2015/12/14
 */
public class DelayQueue {
  public static void main(String[] args) throws InterruptedException {
    Jedis jedis = new Jedis("10.4.14.60");
    long delay1 = Instant.now().getEpochSecond() + 5;
    long delay2 = Instant.now().getEpochSecond() + 10;
    long delay3 = Instant.now().getEpochSecond() + 15;
    System.out.println(delay1);
    System.out.println(delay2);
    System.out.println(delay3);
    jedis.zadd("zyz-test", delay1, "foo");
    jedis.zadd("zyz-test", delay2, "bar");
    jedis.zadd("zyz-test", delay3, "hoho");
    TimeUnit.SECONDS.sleep(6);
    long delay4 = Instant.now().getEpochSecond();
    System.out.println(delay4);
    Transaction t =  jedis.multi();
    Response<Set<String>> response = t.zrangeByScore("zyz-test", 0d, delay4);
    t.zremrangeByScore("zyz-test", 0d, delay4);
    t.exec();
    Set<String> tuples = response.get();
    for (String tuple : tuples) {
      System.out.println(tuple);
    }

    TimeUnit.SECONDS.sleep(6);

    delay4 = Instant.now().getEpochSecond();
    System.out.println(delay4);
    Set<String> value2 = jedis.zrangeByScore("zyz-test", 0d, delay4);
    System.out.println(value2);
  }
}
