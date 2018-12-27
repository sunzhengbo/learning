package com.imooc.spark.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;

/**
 * HBase工具类
 */
public class HBaseUtils implements Serializable {

    private static final Logger logger = Logger.getLogger(Demo.HBaseUtils.class);
    private static HBaseUtils hBaseUtils = null;
    private static Connection connection = null;
    private static Admin admin = null;

    /**
     * 单例模式之懒汉模式
     * @param address Address of zookeeper or hdfs;
     * @return HBaseUtilsTest object
     */
    public static HBaseUtils create(String... address) {
        if (hBaseUtils == null) {
            synchronized (HBaseUtils.class) {
                if (hBaseUtils == null) {
                    hBaseUtils = new HBaseUtils(address);
                }
            }
        }
        return hBaseUtils;
    }

    /**
     * @param address Address of zookeeper or hdfs;
     *                The length of the parameters are less than or equal to 2
     */
    private HBaseUtils(String... address){
        Configuration configuration = HBaseConfiguration.create();

        if (address.length > 2){
            logger.error("The length of the parameters are less than or equal to 2");
            System.exit(1);
        }

        if (address.length!=0){
            for (String addr:address){
                if (!"".equals(addr) && addr != null){
                    if (addr.contains("hdfs")){
                        configuration.set("hbase.rootdir", addr);
                    }else {
                        configuration.set("hbase.zookeeper.quorum", addr);
                    }
                }
            }
        }
        //init data
        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long incrementColumnValue (String tname,String rKey,String cFamily,String qualifier,long amount){
        long finalAmount = 0l;
        try {
            Table table = connection.getTable(TableName.valueOf(tname));

            finalAmount = table.incrementColumnValue(
                    Bytes.toBytes(rKey),
                    Bytes.toBytes(cFamily),
                    Bytes.toBytes(qualifier),
                    amount
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalAmount;
    }
}
