package utils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 使用数据库连接池的方式插入数据
 */
public class JdbcConnectionsPool implements DataSource ,Serializable {

    /**
     * 使用静态块代码，初始化连接池，创建连接池的中最小链接数量连接，
     * 创建linkedlist集合，将这些连接放入集合中
     */
    //创建linkedlist集合
    private static LinkedList<Connection> linkedlist = new LinkedList<Connection>();
    private static String driver;//
    private static String url;//
    private static String username;//数据库登陆名
    private static String password;//数据库的登陆密码
    private static int jdbcConnectionInitSize;//最小连接数量
    private static int max = 1; //当前最大连接数量=max*jdbcConnectionInitSize
    private static Connection connection = null;

    //初始化数据库连接池
    static {
        //通过反射机制获取访问db.properties文件
        InputStream is = JdbcConnectionsPool.class.getResourceAsStream("/utils/db.properties");
        Properties prop = new Properties();

        try {
            //加载db.properties文件
            prop.load(is);
            //获取db.properties文件中的数据库连接信息
            driver = prop.getProperty("driver");
            url = prop.getProperty("url");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
            jdbcConnectionInitSize = Integer.parseInt(prop.getProperty("jdbcConnectionInitSize"));


            //创建最小连接数个数据库连接对象以备使用
            Class.forName("com.mysql.jdbc.Driver");

            for (int i=0;i<jdbcConnectionInitSize;i++){
                connection = DriverManager.getConnection(url,username,password);
                linkedlist.add(connection);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() throws SQLException {
        if (linkedlist.size() > jdbcConnectionInitSize*5){
            System.out.println("数据库的连接数量已达到极限");

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (linkedlist.size()==0 && max<5){
            try {
                Class.forName("com.mysql.jdbc.Driver");
                for (int i=0;i<jdbcConnectionInitSize;i++){
                    connection = DriverManager.getConnection(url,username,password);
                    linkedlist.add(connection);
                }
                max ++;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(linkedlist.size()>0){
            connection = linkedlist.getFirst();
            linkedlist.removeFirst();
            max = getInt(linkedlist.size(),jdbcConnectionInitSize);
        }
        return connection;
    }

    /**
     * 取整数
     */
    public Integer getInt(int val1,int val2) {
        int val = val1/val2;
        if (val1%val2==0){
            return val;
        }else {
            return val+1;
        }
    }

    //收回连接
    public static void returnConnection(Connection connection){
        linkedlist.add(connection);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
