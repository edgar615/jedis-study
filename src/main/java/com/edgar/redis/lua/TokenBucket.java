package com.edgar.redis.lua;

import com.google.common.io.CharStreams;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Edgar on 2017/1/16.
 *
 * @author Edgar  Date 2017/1/16
 */
public class TokenBucket {
  private final Jedis jedis;

  // 向令牌桶中添加令牌的速率
  private final double rate;

  // 令牌桶的最大容量
  private final int size;

  private final String key;

  public TokenBucket(Jedis jedis, double rate, int size, String key) {
    this.jedis = jedis;
    this.rate = rate;
    this.size = size;
    this.key = key;
  }

  public Object acquire(int permits) throws IOException {
    List<String> keys = Collections.singletonList(key);
    List<String> argv =
            Arrays.asList(String.valueOf(System.currentTimeMillis()), String.valueOf(rate)
                    , String.valueOf(size), String.valueOf(permits));

    return jedis.eval(loadScriptString("token_bucket.lua"), keys, argv);
  }

  // 加载Lua代码
  private String loadScriptString(String fileName) throws IOException {
    Reader reader =
            new InputStreamReader(
                    TokenBucketTest.class.getClassLoader().getResourceAsStream(fileName));
    return CharStreams.toString(reader);
  }
}
