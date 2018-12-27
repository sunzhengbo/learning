package com.imooc.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * hadoop HDFS java api operation
 */
public class HDFSAppTest {

    public static void main(String[] args) {
        final String HDFS_PATH = "hdfs://118.25.7.22:8020";

        Configuration config = new Configuration();
        config.set("dfs.client.use.datanode.hostname", "true");

        FileSystem fileSystem = null;
        try {
            //获取文件系统对象
            fileSystem = FileSystem.get(new URI(HDFS_PATH), config, "ubuntu" );
            //上传的文件
            String localPath = "E:/Test/hello.txt";
            //获取上传文件的名字
            String[] pathArray = localPath.split("/");
            List<String> pathList = Arrays.asList(pathArray);
            String fileName = pathList.get(pathList.size() - 1);
            //上传至hdfs服务地址
            String serverPath = HDFS_PATH+"/test";
            //上传至hdfs服务后的文件
            Path path = new Path(serverPath + "/" + fileName);
            System.out.println(path);
            //判断上传的文件是否存在，存在就删除
            if (fileSystem.exists(path)) {
                Boolean flag = fileSystem.delete(path, true);
                System.out.println(flag);
            }
            //上传文件
            fileSystem.copyFromLocalFile(new Path(localPath), path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
