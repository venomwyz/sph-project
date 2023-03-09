package com.atguigu.es.test;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.es.test.pojo.Article;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class Demo2 {

    /**
     * 单条新增/批量
     * @param args
     */
    public static void main(String[] args) throws Exception {
        //连接初始化: Settings.EMPTYes单点不是集群
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置es的ip和端口--->TCP端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //构建一个批量对象
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        //构建数据
        for (long i = 1; i <= 100; i++) {
            Article article = new Article();
            article.setId(i);
            article.setTitle(i + ",Elasticsearch 是位于 Elastic Stack 核心的分布式搜索和分析引擎");
            article.setContent(i + ",Elasticsearch 为所有类型的数据提供近乎实时的搜索和分析。" +
                    "无论您拥有结构化或非结构化文本、数字数据还是地理空间数据，" +
                    "Elasticsearch 都能以支持快速搜索的方式高效地存储和索引它。" +
                    "您可以超越简单的数据检索和聚合信息来发现数据中的趋势和模式。" +
                    "随着您的数据和查询量的增长，Elasticsearch 的分布式特性使您的部署能够随之无缝增长。");
            //存储数据
            bulkRequestBuilder.add(client.prepareIndex("java_0828_new", "article", i + "")
                    .setSource(JSONObject.toJSONString(article), XContentType.JSON));
        }
        //一次提交
        bulkRequestBuilder.execute().actionGet();
        //关闭连接
        client.close();
    }

}
