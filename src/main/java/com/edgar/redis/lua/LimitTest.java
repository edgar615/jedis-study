package com.edgar.redis.lua;

import com.google.common.io.CharStreams;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2015/12/14.
 *
 * @author Edgar  Date 2015/12/14
 */
public class LimitTest {
  public static void main(String[] args) throws InterruptedException, IOException {
    Jedis jedis = new Jedis("10.11.0.31");
    System.out.println(new LimitTest().accessLimit("192.168.1.100", 2, 3, jedis));
    System.out.println(new LimitTest().accessLimit("192.168.1.100", 2, 3, jedis));
    System.out.println(new LimitTest().accessLimit("192.168.1.100", 2, 3, jedis));
    TimeUnit.SECONDS.sleep(3);
    System.out.println(new LimitTest().accessLimit("192.168.1.100", 2, 3, jedis));
    System.out.println(new LimitTest().accessLimit("192.168.1.100", 2, 3, jedis));
    TimeUnit.SECONDS.sleep(2);
    System.out.println(new LimitTest().accessLimit("192.168.1.100", 2, 3, jedis));
  }

  private boolean accessLimit(String ip, int limit, int timeout,
                              Jedis connection) throws IOException {
    List<String> keys = Collections.singletonList(ip);
    List<String> argv = Arrays.asList(String.valueOf(limit), String.valueOf(timeout));

    return 1 == (Long) connection.eval(loadScriptString("limit.lua"), keys, argv);
  }

  // 加载Lua代码
  private String loadScriptString(String fileName) throws IOException {
    Reader reader =
            new InputStreamReader(LimitTest.class.getClassLoader().getResourceAsStream(fileName));
    return CharStreams.toString(reader);
  }
}
