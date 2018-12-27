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
 * 单机版分析日志，分析多项
 */
public class LogParse2App {

    private UserAgentParser userAgentParser ;
    private UserAgent agent ;

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
        String os ;
        boolean flag ;
        Map<String,Integer> map = new HashMap<String, Integer>();

        int i = 0; //计数器

        Set<String> names ;//浏览器名称
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
                os = agent.getPlatform();
                flag = agent.isMobile();

                //把浏览器的信息装入Map集合中
                if (browses != null) {
                    if (map.containsKey(browses)) {
                        map.put(browses,map.get(browses)+1);
                    }else{
                        map.put(browses, 1);
                    }
                }

                //把操作系统的信息装入Map集合中
                if (os != null) {
                    if (map.containsKey(os)) {
                        map.put(os,map.get(os)+1);
                    }else{
                        map.put(os, 1);
                    }
                }

                //把是否是手机的信息装入Map集合中
                String phoneFlag = String.valueOf(flag);
                if (map.containsKey(phoneFlag)) {
                    map.put(phoneFlag,map.get(phoneFlag)+1);
                }else{
                    map.put(phoneFlag, 1);
                }

                i++;
            }

            //遍历取值
            int sum = 0;
            names = map.keySet();
            for(String name:names) {
                num = map.get(name);
                sum += num;
                System.out.println(name+","+num);
            }
            System.out.println("一共有："+i+"条记录数");
            System.out.println("合计："+sum);

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
