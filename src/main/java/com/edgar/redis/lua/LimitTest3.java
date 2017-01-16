package com.edgar.redis.lua;

import com.google.common.io.CharStreams;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2015/12/14.
 *
 * @author Edgar  Date 2015/12/14
 */
public class LimitTest3 {
  public static void main(String[] args) throws InterruptedException, IOException {
    Jedis jedis = new Jedis("10.11.0.31");
    System.out.println(new LimitTest3().accessLimit("192.168.1.100", jedis));
    System.out.println(new LimitTest3().accessLimit("192.168.1.100", jedis));
    TimeUnit.SECONDS.sleep(1);
    System.out.println(new LimitTest3().accessLimit("192.168.1.100", jedis));
    TimeUnit.SECONDS.sleep(1);
    System.out.println(new LimitTest3().accessLimit("192.168.1.100", jedis));
    TimeUnit.SECONDS.sleep(1);
    System.out.println(new LimitTest3().accessLimit("192.168.1.100", jedis));
    TimeUnit.SECONDS.sleep(1);
    System.out.println(new LimitTest3().accessLimit("192.168.1.100", jedis));
    TimeUnit.SECONDS.sleep(1);
    System.out.println(new LimitTest3().accessLimit("192.168.1.100", jedis));
    TimeUnit.SECONDS.sleep(61);
    System.out.println(new LimitTest3().accessLimit("192.168.1.100", jedis));
  }

  private Object accessLimit(String ip,
                              Jedis connection) throws IOException {
    List<String> keys = Collections.singletonList(ip);
    List<String> argv = Arrays.asList(String.valueOf(Instant.now().getEpochSecond()));

    return connection.eval(loadScriptString("rate_limiter.lua"), keys, argv);
  }

  // 加载Lua代码
  private String loadScriptString(String fileName) throws IOException {
    Reader reader =
            new InputStreamReader(LimitTest3.class.getClassLoader().getResourceAsStream(fileName));
    return CharStreams.toString(reader);
  }
}
