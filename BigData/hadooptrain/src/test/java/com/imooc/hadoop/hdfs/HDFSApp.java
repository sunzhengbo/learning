package com.imooc.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * hadoop HDFS java api operation
 */
public class HDFSApp {

    //hdfs path
    public static final String HDFS_PATH = "hdfs://118.25.7.22:9000";

    //define fileSystem class
    FileSystem fileSystem = null;

    /**
     * init resources
     */
    @Before
    public void initResources() throws Exception{
        Configuration config = new Configuration();
        config.set("dfs.client.use.datanode.hostname", "true");
        //获取文件系统文件
        fileSystem = FileSystem.get(new URI(HDFS_PATH),config,"ubuntu");
        //若不是在本机上运行就产生用户名不一致，权限不够无法运行成功，则需要在构建fileSystem对象是指定用户名
        //fileSystem = FileSystem.get(new URI(HDFS_PATH),configuration,"sunzb");
    }

    /**
     * release resources
     */
    @After
    public void releaseResources() throws Exception{
        fileSystem = null;
    }


    /**
     * create directory
     */
    @Test
    public void mkdir() throws Exception{
        fileSystem.mkdirs(new Path("/test"));
    }

    /**
     * create file
     */
    @Test
    public void createFile() throws Exception {
        fileSystem.create(new Path("/test/a.txt"));
    }

    /**
     * upload
     */
    @Test
    public void upload() throws Exception{
        String localPath = "E:/Test/hello.txt";
        String [] pathArray = localPath.split("/");
        List<String> pathList = Arrays.asList(pathArray);
        String fileName = pathList.get(pathList.size()-1);
        String serverPath = "/test";
        Path path = new Path(serverPath+"/"+fileName);
        if (fileSystem.exists(path)){
            fileSystem.delete(path,true);
        }
        fileSystem.copyFromLocalFile(new Path(localPath),path);
    }

    /**
     * download
     */
    @Test
    public void download() throws Exception{
        String localPath = "/home/sunzb/Documents/";
        String serverPath = "/test/a.txt";
        fileSystem.copyToLocalFile(new Path(serverPath),new Path(localPath));
    }

    /**
     * cat file
     */
    @Test
    public void catFile() throws Exception{
        FSDataInputStream inputStream = fileSystem.open(new Path("/test/a.txt"));
        IOUtils.copyBytes(inputStream,System.out,1024);
    }
}
