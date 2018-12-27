package com.imooc.hadoop.logparse;

import com.kumkee.userAgent.UserAgent;
import com.kumkee.userAgent.UserAgentParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * MapReduce统计网站浏览器
 */
public class LogParseApp {

    /**
     * 读取文件
     */
    public static class MyMapper extends Mapper<LongWritable,Text,Text,LongWritable>{

        UserAgentParser userAgentParser = null;
        UserAgent agent = null;

        @Override
        protected void setup(Context context) {
            userAgentParser = new UserAgentParser();
            agent = new UserAgent();
        }

        @Override
        protected void cleanup(Context context) {
            userAgentParser = null;
            agent = null;
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            //将Text的value转成成java字符串
            String line = value.toString();

            //遍历单词并输出
            String source = subInfo(line);

            agent = userAgentParser.parse(source);

            context.write(new Text(agent.getBrowser()),new LongWritable(1));
        }
    }

    /**
     * 截取自己需要的信息
     */
    private static String subInfo(String line) {
        String []browses = line.split("\"");
        String browse = null;

        int i = 0;
        while (i < browses.length) {
            browse = browses[5];
            if (browse != null) {
                break;
            }
            i++;
        }

        return browse;
    }


    /**
     * 归并操作，词频计算
     */
    public static class MyReducer extends Reducer<Text,LongWritable,Text,LongWritable>{
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {

            //定义sum
            long sum = 0;

            //求词频之和
            for (LongWritable value:values){
                sum += value.get();
            }

            context.write(key,new LongWritable(sum));
        }
    }

    /**
     * Driver
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        //自动删除已经存在的输出目录
        FileSystem fileSystem = FileSystem.get(new Configuration());
        if (fileSystem.exists(new Path(args[1]))) {
            try {
                deleteOutputDirector(fileSystem,args[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //create Job
        Job job = Job.getInstance(new Configuration(),"logparse");

        //set handle class
        job.setJarByClass(LogParseApp.class);

        //set job handle path
        FileInputFormat.setInputPaths(job,new Path(args[0]));

        //set map argument
        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        //start Combiner
        job.setCombinerClass(MyReducer.class);

        //set reduce argument
        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        //set job write path
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //commit job
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    /**
     * 自动删除已经存在的输出目录
     */
    private static void deleteOutputDirector(FileSystem fileSystem, String path) throws Exception {
        boolean flag = fileSystem.delete(new Path(path),true);
        System.out.println(flag?path+"删除成功":path+"删除失败");
    }
}
