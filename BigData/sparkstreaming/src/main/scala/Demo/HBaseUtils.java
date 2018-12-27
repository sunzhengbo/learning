package Demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * HBase操作类
 */
public class HBaseUtils {

    private static final Logger logger = Logger.getLogger(HBaseUtils.class);
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
                if (addr.contains("hdfs")){
                    configuration.set("hbase.rootdir", addr);
                }else {
                    configuration.set("hbase.zookeeper.quorum", addr);
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


    /**
     * list all tables
     */
    public void listTables(){
        try {
            //获取所有的表名
            TableName [] tableNames = admin.listTableNames();

            if (tableNames.length == 0){
                logger.info("HBase has no table");
            }else {
                for (TableName tableName:tableNames){
                    System.out.println(tableName.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read table
     * tname:table name
     * return:table context
     */
    public void readTable(String tname){
        //To String convert to TableName
        TableName tableName = TableName.valueOf(tname);
        try {
            //判断tname是否存在,存在就返回true,否则返回false
            Boolean flag = admin.tableExists(tableName);
            if (flag){
                //判断当前的表是否被禁用了,是就开启
                if (admin.isTableDisabled(tableName)){
                    admin.enableTable(tableName);
                }
                Table table = connection.getTable(tableName);

                ResultScanner resultScanner = table.getScanner(new Scan());

                for (Result result:resultScanner){
                    for (Cell cell:result.listCells()){
                        //取行健
                        String rowKey=Bytes.toString(CellUtil.cloneRow(cell));
                        //取到时间戳
                        long timestamp = cell.getTimestamp();
                        //取到族列
                        String family = Bytes.toString(CellUtil.cloneFamily(cell));
                        //取到修饰名
                        String qualifier  = Bytes.toString(CellUtil.cloneQualifier(cell));
                        //取到值
                        String value = Bytes.toString(CellUtil.cloneValue(cell));

                        System.out.println(" ====> RowKey : " + rowKey + ",  Timestamp : " +
                                timestamp + ", ColumnFamily : " + family + ", Key : " + qualifier
                                + ", Value : " + value);
                    }
                }
                resultScanner.close();
            }else {
                logger.error("Table is not exist");
                System.exit(1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * read key value
     * tname:table name
     * return:T
     */
    public byte [] readKeyValue (String tname,String rKey,String cFamily, String qualifier){
            //To String convert to TableName type
            TableName tableName = TableName.valueOf(tname);
            boolean flag_1 = false;
            boolean flag_2 = true;
            boolean flag_3 = false;
            try {
                //判断tname是否存在,存在就返回true,否则返回false
                flag_1 = admin.tableExists(tableName);
                if (flag_1) {
                    //判断当前的表是否被禁用了,是就开启
                    if (admin.isTableDisabled(tableName)){
                        admin.enableTable(tableName);
                    }
                    //get table object
                    Table table = connection.getTable(tableName);

                    ResultScanner resultScanner = table.getScanner(new Scan());
                    //判断当前输入的RowKey是否存在
                    if ("".equals(rKey) || " ".equals(rKey) || rKey == null){
                        logger.error("RowKey is null");
                        System.exit(1);
                    }else {
                        //判断列簇是否存在
                        for (Result result:resultScanner){
                            for (Cell cell:result.listCells()){
                                if (rKey.equals(Bytes.toString(CellUtil.cloneRow(cell)))){
                                    flag_1 = false;
                                    if (cFamily.equals(Bytes.toString(CellUtil.cloneFamily(cell)))){
                                        flag_2 = false;
                                        flag_3 = qualifier.equals(Bytes.toString(CellUtil.cloneQualifier(cell)));
                                        if (flag_3) {
                                            return CellUtil.cloneValue(cell);
                                        }
                                    }
                                }
                            }
                        }
                        if (flag_1){
                            logger.error("Table has no specific RowKey");
                            System.exit(1);
                        }

                        if (flag_2) {
                            logger.error("Table has no specific column family");
                            System.exit(1);
                        }

                        if (!flag_3) {
                            logger.error("Table has no specific column");
                            System.exit(1);
                        }
                    }
                }else {
                    logger.error("Table is not exist");
                    System.exit(1);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }


    /**
     * create table
     * tname:table name
     * return:Boolean
     */
    public Boolean createTable(String tname,String... cfamily){

        //To String convert to TableName
        TableName tableName = TableName.valueOf(tname);
        Boolean flag = false;
        try {
            //判断tname是否存在,存在就返回true,否则返回false
            flag = admin.tableExists(tableName);
            if (flag) {
                logger.error("Table has existed");
                return !flag;
            }else {
                //判断当前的表是否被禁用了,是就开启
                if (admin.isTableDisabled(tableName)){
                    admin.enableTable(tableName);
                }
                HTableDescriptor hbaseTable = new HTableDescriptor(tableName);

                //通过可变参数来传递不定量的列簇
                for (String cf:cfamily){
                    hbaseTable.addFamily(new HColumnDescriptor(cf));
                }
                admin.createTable(hbaseTable);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return !flag;
    }

    /**
     * 写入记录,不存在就是写入,已存在就更新
     * tname:table name
     * return:Boolean
     */
    public boolean writeSingleRecord(String tname,String rKey,String cFamily, String qualifier, Object value){

        //To String convert to TableName
        TableName tableName = TableName.valueOf(tname);
        boolean flag = false;
        try {
            //判断tname是否存在,存在就返回true,否则返回false
            flag = admin.tableExists(tableName);
            if (flag) {
                //判断当前的表是否被禁用了,是就开启
                if (admin.isTableDisabled(tableName)){
                    admin.enableTable(tableName);
                }
                Table table = connection.getTable(tableName);

                ResultScanner resultScanner = table.getScanner(new Scan());
                //判断当前输入的Column Family是否存在
                for (Result result:resultScanner){
                    for (Cell cell:result.listCells()){
                        flag = cFamily.equals(Bytes.toString(CellUtil.cloneFamily(cell)));
                        if (flag) break;
                    }
                }
                if (flag){
                    Put put = new Put(rKey.getBytes());
                    put.addColumn(Bytes.toBytes(cFamily), Bytes.toBytes(qualifier),Bytes.toBytes(value.toString()));
                    table.put(put);
                }else {
                    logger.error("Column Family is not exist");
                    return false;
                }
            }else {
                logger.error("Table is not exist");
                return false;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 写入记录,不存在就是写入,已存在就更新
     * tname:table name
     * return:Boolean
     */
    public boolean writeMultipleRecord(String tname, String rKey, String cFamily, Map<String,Object> map){
        //To String convert to TableName
        TableName tableName = TableName.valueOf(tname);
        boolean flag = false;
        try {
            //判断tname是否存在,存在就返回true,否则返回false
            flag = admin.tableExists(tableName);
            if (flag) {
                //判断当前的表是否被禁用了,是就开启
                if (admin.isTableDisabled(tableName)){
                    admin.enableTable(tableName);
                }
                Table table = connection.getTable(tableName);

                ResultScanner resultScanner = table.getScanner(new Scan());
                //判断当前输入的Column Family是否存在
                for (Result result:resultScanner){
                    for (Cell cell:result.listCells()){
                        flag = cFamily.equals(Bytes.toString(CellUtil.cloneFamily(cell)));
                        if (flag) break;
                    }
                }
                if (flag){
                    Put putValue = new Put(rKey.getBytes());
                    //put multiple
                    Set<String> keySet = map.keySet();
                    for (String key:keySet) {
                        putValue.addColumn(Bytes.toBytes(cFamily),Bytes.toBytes(key),Bytes.toBytes(map.get(key).toString()));
                    }

                    table.put(putValue);
                }else {
                    logger.error("Column Family is not exist");
                    return false;
                }
            }else {
                logger.error("Table is not exist");
                return false;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * drop table
     */
    public boolean deleteTable(String tname){

        //To String convert to TableName
        TableName tableName = TableName.valueOf(tname);
        boolean flag;
        try {
            //判断tname是否存在,存在就返回true,否则返回false
            flag = admin.tableExists(tableName);
            if (flag) {
                //禁用表
                admin.disableTable(tableName);
                //删除表
                admin.deleteTable(tableName);
            }else {
                logger.error("Table is not exist");
                return flag;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * delete row key
     */
    public boolean deleteRows(String tname,String... rKey){

        //To String convert to TableName
        TableName tableName = TableName.valueOf(tname);
        boolean flag = false;
        try {
            //判断tname是否存在,存在就返回true,否则返回false
            flag = admin.tableExists(tableName);
            if (flag) {
                //判断当前的表是否被禁用了,是就开启
                if (admin.isTableDisabled(tableName)){
                    admin.enableTable(tableName);
                }
                //get table object
                Table table = connection.getTable(tableName);

                ResultScanner resultScanner = table.getScanner(new Scan());
                //判断当前输入的RowKey是否存在
                for (Result result:resultScanner){
                    for (Cell cell:result.listCells()){
                        flag = Arrays.asList(rKey).contains(Bytes.toString(CellUtil.cloneRow(cell)));
                        if (flag) break;
                    }
                }
                if (flag){
                    //存放要被删除的RowKey的对象
                    List<Delete> list = new ArrayList<Delete>();
                    //逐条添加RowKey的对象
                    for (String rk:rKey){
                        list.add(new Delete(Bytes.toBytes(rk)));
                    }
                    //批量删除
                    table.delete(list);
                }else {
                    logger.error("Table has no specific RowKey");
                    return false;
                }
            }else {
                logger.error("Table is not exist");
                return false;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * delete columns family
     */
    public boolean deleteColumnFamilys(String tname,String rKey,String... cFamily){

        //To String convert to TableName
        TableName tableName = TableName.valueOf(tname);
        boolean flag_1 = false;
        boolean flag_2 = false;
        try {
            //判断tname是否存在,存在就返回true,否则返回false
            flag_1 = admin.tableExists(tableName);
            if (flag_1) {
                //判断当前的表是否被禁用了,是就开启
                if (admin.isTableDisabled(tableName)){
                    admin.enableTable(tableName);
                }
                //get table object
                Table table = connection.getTable(tableName);

                ResultScanner resultScanner = table.getScanner(new Scan());
                //判断当前输入的RowKey是否存在
                if ("".equals(rKey) || " ".equals(rKey) || rKey == null){
                    logger.error("RowKey is null");
                    return false;
                }else {
                    //判断列簇是否存在
                    for (Result result:resultScanner){
                        for (Cell cell:result.listCells()){
                            if (rKey.equals(Bytes.toString(CellUtil.cloneRow(cell)))){
                                flag_1 = false;
                                flag_2 = Arrays.asList(cFamily).contains(Bytes.toString(CellUtil.cloneFamily(cell)));
                                if (flag_2) break;
                            }
                        }
                    }
                    if (flag_1){
                        logger.error("Table has no specific RowKey");
                        return false;
                    }

                    if (!flag_2) {
                        logger.error("Table has no specific column family");
                        return false;
                    }

                    if(!flag_1 && flag_2){
                        //存放要被删除的RowKey的对象
                        List<Delete> list = new ArrayList<Delete>();
                        //获取RowKey的删除对象
                        Delete delete = new Delete(Bytes.toBytes(rKey));
                        //逐条添加cFamily的对象
                        for (String cf:cFamily){
                            list.add(delete.addFamily(Bytes.toBytes(cf)));
                        }
                        //批量删除
                        table.delete(list);
                    }
                }
            }else {
                logger.error("Table is not exist");
                return false;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return !flag_1 && flag_2;
    }

    /**
     * delete columns
     */
    public boolean deleteColumns(String tname,String rKey,String cFamily,String... columns){

        //To String convert to TableName type
        TableName tableName = TableName.valueOf(tname);
        boolean flag_1 = false;
        boolean flag_2 = true;
        boolean flag_3 = false;
        try {
            //判断tname是否存在,存在就返回true,否则返回false
            flag_1 = admin.tableExists(tableName);
            if (flag_1) {
                //判断当前的表是否被禁用了,是就开启
                if (admin.isTableDisabled(tableName)){
                    admin.enableTable(tableName);
                }
                //get table object
                Table table = connection.getTable(tableName);

                ResultScanner resultScanner = table.getScanner(new Scan());
                //判断当前输入的RowKey是否存在
                if ("".equals(rKey) || " ".equals(rKey) || rKey == null){
                    logger.error("RowKey is null");
                    return false;
                }else {
                    //判断列簇是否存在
                    for (Result result:resultScanner){
                        for (Cell cell:result.listCells()){
                            if (rKey.equals(Bytes.toString(CellUtil.cloneRow(cell)))){
                                flag_1 = false;
                                if (cFamily.equals(Bytes.toString(CellUtil.cloneFamily(cell)))){
                                    flag_2 = false;
                                    flag_3 = Arrays.asList(columns).contains(Bytes.toString(CellUtil.cloneQualifier(cell)));
                                    if (flag_3) break;
                                }
                            }
                        }
                    }
                    if (flag_1){
                        logger.error("Table has no specific RowKey");
                        return false;
                    }

                    if (flag_2) {
                        logger.error("Table has no specific column family");
                        return false;
                    }

                    if (!flag_3) {
                        logger.error("Table has no specific column");
                        return false;
                    }

                    if(!flag_1 && !flag_2 && flag_3){
                        //存放要被删除的RowKey的对象
                        List<Delete> list = new ArrayList<Delete>();
                        //获取RowKey的删除对象
                        Delete delete = new Delete(Bytes.toBytes(rKey));
                        //逐条添加cFamily的对象

                        for (String c:columns){
                            list.add(delete.addColumn(Bytes.toBytes(cFamily),Bytes.toBytes(c)));
                        }
                        //批量删除
                        table.delete(list);
                    }
                }
            }else {
                logger.error("Table is not exist");
                return false;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return !flag_1 && !flag_2 && flag_3;
    }


    /**
     * Amount method
     * The {@link Durability} is defaulted to {@link Durability#SYNC_WAL}.
     * @param tname table name
     * @param rKey The row that contains the cell to increment.
     * @param cFamily The column family of the cell to increment.
     * @param qualifier The column qualifier of the cell to increment.
     * @param amount The amount to increment the cell with (or decrement, if the amount is negative).
     * @return The new value, post increment.
     */
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
