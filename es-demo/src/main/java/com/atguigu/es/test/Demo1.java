package com.atguigu.es.test;

import jdk.nashorn.internal.objects.annotations.Setter;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 创建索引
 */
public class Demo1 {


    public static void main(String[] args)throws Exception {
        //连接初始化: Settings.EMPTYes单点不是集群
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置es的ip和端口--->TCP端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //创建索引
        client.admin().indices().prepareCreate("java_0828_new").get();
        //构建映射
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder
                .startObject()
                .startObject("article")
                .startObject("properties")
                .startObject("id")
                .field("type", "long")
                .endObject()
                .startObject("title")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("content")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        //创建映射
        PutMappingRequest putMappingRequest =
                Requests.putMappingRequest("java_0828_new").type("article").source(builder);
        client.admin().indices().putMapping(putMappingRequest);
        //关闭练习
        client.close();
    }




}
