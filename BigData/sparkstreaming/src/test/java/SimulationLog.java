import org.apache.log4j.Logger;

/**
 * Streaming 整合 kafka & flume
 * 1.模拟生成日志信息
 * 2.flume接收日志
 * 3.输入kafka
 * 4.Spark Streaming 整合 Kafka
 */
public class SimulationLog {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(SimulationLog.class);

        int index = 1;

        while(true){
            logger.info("value:" + index++);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
