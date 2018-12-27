package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 直接使用数据库连接的方式插入数据
 */
public class JdbcConnections implements Serializable {

    /**
     * 使用静态块代码，初始化连接池，创建连接池的中最小链接数量连接，
     * 创建linkedlist集合，将这些连接放入集合中
     */
    private static String driver;//
    private static String url;//
    private static String username;//数据库登陆名
    private static String password;//数据库的登陆密码
    private static Connection connection = null;

   public static Connection getConnection(){
       //通过反射机制获取访问db.properties文件
       InputStream is = JdbcConnections.class.getResourceAsStream("/utils/db.properties");
       Properties prop = new Properties();

       try {
           //加载db.properties文件
           prop.load(is);
           //获取db.properties文件中的数据库连接信息
           driver = prop.getProperty("driver");
           url = prop.getProperty("url");
           username = prop.getProperty("username");
           password = prop.getProperty("password");

           Class.forName("com.mysql.jdbc.Driver");
           connection = DriverManager.getConnection(url,username,password);
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       } catch (SQLException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return connection;
   }

}
