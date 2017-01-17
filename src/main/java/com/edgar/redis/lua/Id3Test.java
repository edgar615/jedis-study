package com.edgar.redis.lua;

import com.google.common.io.CharStreams;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Edgar on 2015/12/14.
 *
 * @author Edgar  Date 2015/12/14
 */
public class Id3Test {
  public static void main(String[] args) throws InterruptedException, IOException {

    Jedis jedis = new Jedis("10.11.0.31");
    List<String> keys = new ArrayList<String>();
    List<String> argv = new ArrayList<String>();
    for (int i = 0; i < 5000; i++) {
      List<Long> result =
              new ArrayList<Long>(
                      (Collection<Long>) jedis.eval(loadScriptString("id3.lua"), keys, argv));
      long seq = result.get(0);
      long shardId = result.get(1);
      long time = result.get(2);
      long id = (time << 22)
                | (shardId << 12)
                | seq;
      System.out.println(result);
    }
  }

  // 加载Lua代码
  private static String loadScriptString(String fileName) throws IOException {
    Reader reader =
            new InputStreamReader(Id3Test.class.getClassLoader().getResourceAsStream(fileName));
    return CharStreams.toString(reader);
  }
}
