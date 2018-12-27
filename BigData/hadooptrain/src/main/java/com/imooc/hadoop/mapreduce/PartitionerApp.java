package com.imooc.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * MapReduce统计词频,自动删除已经存在的输出目录
 */
public class PartitionerApp {

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

            //统计
            context.write(new Text(words[0]),new LongWritable(Long.parseLong(words[1])));
        }
    }


    /**
     * 归并操作，计算
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
     * Partitioner 操作
     */
    public static class MyPartitioner extends Partitioner<Text,LongWritable> {
        @Override
        public int getPartition(Text text, LongWritable longWritable, int i) {

            String key = text.toString();

            if (key.equals("xiaomi")) {
                return 0;
            }

            if (key.equals("huawei")) {
                return 1;
            }

            if (key.equals("apple")) {
                return 2;
            }

            return 3;
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
        Job job = Job.getInstance(new Configuration(),"wordcount");

        //set handle class
        job.setJarByClass(PartitionerApp.class);

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

        //set partitioner
        job.setPartitionerClass(MyPartitioner.class);

        //ser partitioner number
        job.setNumReduceTasks(4);

        //set job write path
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //commit job
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    /**
     * 自动删除已经存在的输出目录
     */
    public static void deleteOutputDirector(FileSystem fileSystem,String path) throws Exception {
        boolean flag = fileSystem.delete(new Path(path),true);
        System.out.println(flag?path+"删除成功":path+"删除失败");
    }
}
