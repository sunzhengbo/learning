package com.imooc.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
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
 * MapReduce统计词频
 */
public class WordCountApp {

    /**
     * 读取文件
     * Mapper <KEYIN, VALUEIN, KEYOUT, VALUEOUT>
     *     KEYIN<LongWritable>:文档中的行数
     *     VALUEIN<Text>:每行的值
     *     KEYOUT<Text>:拆分后的单词
     *     VALUEOUT<LongWritable>:词频出现的次数
     */
    public static class MyMapper extends Mapper<LongWritable,Text,Text,LongWritable>{
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            //将Text的value转成成java字符串
            String line = value.toString();

            //拆分
            String []words = line.split(" ");

            //遍历单词并输出
            for (String word:words){
                //通过上下文把结果输出
                context.write(new Text(word),new LongWritable(1));
            }
        }
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
        //create Job
        Job job = Job.getInstance(new Configuration(),"wordcount");

        //set handle class
        job.setJarByClass(WordCountApp.class);

        //set job handle path
        FileInputFormat.setInputPaths(job,new Path(args[0]));

        //set map argument
        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        //set reduce argument
        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        //set job write path
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //commit job
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
