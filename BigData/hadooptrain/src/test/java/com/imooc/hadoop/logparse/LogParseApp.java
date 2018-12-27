package com.imooc.hadoop.logparse;


import com.kumkee.userAgent.UserAgent;
import com.kumkee.userAgent.UserAgentParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 单机版分析日志
 */
public class LogParseApp {

    private UserAgentParser userAgentParser;
    private UserAgent agent;

    @Before
    public void setup() {
        userAgentParser  = new UserAgentParser();
    }

    @After
    public void destroy() {
        userAgentParser = null;
        agent = null;
    }

    /**
     * 读取数据
     */
    @Test
    public void parseLog() {

        //数据存放的路径
        String dataPath = "/home/sunzb/Documents/access_100.log";
        BufferedReader reader = null;

        String line ;
        String browses ;
        Map<String,Integer> map = new HashMap<String, Integer>();

        int i = 0; //计数器

        Set<String> broNames ;//浏览器名称
        int num ; //计算总条数

        try {
            //读取数据
            reader = new BufferedReader(
                        new InputStreamReader(
                            new FileInputStream(
                                new File(dataPath))));
            //循环读取数据
            while(true) {

                line = reader.readLine();
                //若line为null则跳出循环
                if (line == null) {
                    break;
                }

                String source = subInfo(line);

                agent = userAgentParser.parse(source);

                browses = agent.getBrowser();

                //把浏览器的信息装入Map集合中
                if (browses != null) {
                    if (map.containsKey(browses)) {
                        map.put(browses,map.get(browses)+1);
                    }else{
                        map.put(browses, 1);
                    }
                }
                i++;
            }

            //遍历取值
            broNames = map.keySet();
            for(String broName:broNames) {
                num = map.get(broName);
                System.out.println("browse:"+broName+",各个总数："+num);
            }
            System.out.println("总数是："+i);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    /**
     * 截取自己需要的信息
     */
    private String subInfo(String line) {
        String []browses = line.split("\"");
        String browse = null;

        int i = 0;
        while (i < browses.length) {
            browse = browses[5];
            if (browse != null) break;
            i++;
        }

        return browse;
    }
}
