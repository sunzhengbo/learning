import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

import java.math.BigDecimal;

/**
 * 洛阳华尊b8ebb679-5e24-4bd9-8b2e-75bb33fe7f3a
 * 冀东水泥4441eb5c-8278-41ad-a25a-7dca1d7ec480
 * 富川协和ce4f0bef-68b5-42c8-9e71-1e30e8dc94da
 * 读取OSS中指定folder的大小
 */
public class LimitPageReadBucket {
    public static void main(String[] args) {
        String bucketName = "mdp1791";
        String nextMartker = null;//标记
        String prefixName = "ce4f0bef-68b5-42c8-9e71-1e30e8dc94da";// 指定前缀
        long longSize = 0;//文件大小
        int count = 0;//文件总记录数
        ObjectListing listing = null;

        String endpoint = "oss-cn-beijing.aliyuncs.com";
        String accessID = "AccessKeyID";
        String accessKey = "AccessKeySecret";

        // 获取OSSClient对象
        OSSClient ossClient = new OSSClient(endpoint, accessID, accessKey);
        // 构造ListObjectsRequest请求，来扩展参数
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        //指定每页显示1000条，指定前缀
        listObjectsRequest.withMaxKeys(1000).withPrefix(prefixName.toLowerCase());
        do {
            // 从指定的nextMartker处接着遍历
            listObjectsRequest.withMarker(nextMartker);

            listing = ossClient.listObjects(listObjectsRequest);

            for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
                //System.out.println(objectSummary.getKey());//获取文件名称
                // System.out.println(objectSummary.getSize());//
                // 输出每个object文件的大小
                longSize += objectSummary.getSize();
                count++;
            }
            nextMartker = listing.getNextMarker();
        } while (listing.isTruncated());

        double dls = longSize;
        BigDecimal bd = new BigDecimal(dls/ 1024 / 1024 / 1024);//构造一个BigDecimal，用于处理小数
        //ROUND_HALF_UP：四舍五入模式，BigDecimal一共有八种舍入模式，详情：http://www.bdqn.cn/news/201311/11834.shtml
        double totalSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        System.out.println("总大小是：" + totalSize + "G");
        System.out.println();
        System.out.println("一共有" + count + "个文件");

        if(ossClient != null){
            //关闭OSSClient
            ossClient.shutdown();
        }
    }
}
