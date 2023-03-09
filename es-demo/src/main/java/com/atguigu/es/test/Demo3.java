package com.atguigu.es.test;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.es.test.pojo.Article;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import javax.swing.*;
import javax.swing.text.Highlighter;
import java.net.InetAddress;
import java.util.Iterator;

public class Demo3 {

    public static void main(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //声明高亮的条件
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("content");
        highlightBuilder.preTags("<font style=color:red>");
        highlightBuilder.postTags("</font>");
        SearchResponse searchResponse = client.prepareSearch("java_0828_new")
                .setTypes("article")
                .setQuery(QueryBuilders.termQuery("content", "搜索"))
                .addSort("id", SortOrder.DESC)
                .setFrom(0)
                .setSize(100)
                .highlighter(highlightBuilder)
                .get();
        SearchHits hits = searchResponse.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit next = iterator.next();
            //原始数据
            String sourceAsString = next.getSourceAsString();
            Article article = JSONObject.parseObject(sourceAsString, Article.class);
            HighlightField content = next.getHighlightFields().get("content");
            if (content!= null){
                Text[] fragments = content.getFragments();
                if (fragments != null && fragments.length>0){
                    String content1="";
                    for (Text fragment : fragments) {
                        content1 = content1+ fragment;
                    }
                    article.setContent(content1);

                }
            }
            System.out.println(article);
        }


        client.close();


    }


    /**
     * 分页查询
     * @param args
     * @throws Exception
     */
    public static void main9(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.prepareSearch("java_0828_new")
                .setTypes("article")
                .setQuery(QueryBuilders.rangeQuery("id").lte(55).gte(45))//相似度查询
                .setFrom(0)
                .setSize(4)
                .get();
        //多条数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            //获取每条数据
            SearchHit next = iterator.next();
            //获取数据
            System.out.println(next.getSourceAsString());
        }


        client.close();


    }
    /**
     * 范围查询
     * @param args
     * @throws Exception
     */
    public static void main8(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.prepareSearch("java_0828_new")
                .setTypes("article")
                .setQuery(QueryBuilders.rangeQuery("id").lte(55).gte(45))//相似度查询
                .get();
        //多条数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            //获取每条数据
            SearchHit next = iterator.next();
            //获取数据
            System.out.println(next.getSourceAsString());
        }


        client.close();


    }
    /**
     * 相似度查询: 查询2次,对于输入的条件不分词
     * @param args
     * @throws Exception
     */

    public static void main7(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.prepareSearch("java_0828_new")
                .setTypes("article")
                .setQuery(QueryBuilders.fuzzyQuery("content", "ELASTICSEARCH"))//相似度查询
                .get();
        //多条数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            //获取每条数据
            SearchHit next = iterator.next();
            //获取数据
            System.out.println(next.getSourceAsString());
        }


        client.close();


    }
    /**
     * 模糊查询，查询两次次，不分词
     * @param args
     * @throws Exception
     */

    public static void main6(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.prepareSearch("java_0828_new")
                .setTypes("article")
                .setQuery(QueryBuilders.wildcardQuery("content", "ELASTICSE*"))//模糊查询
                .get();
        //多条数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            //获取每条数据
            SearchHit next = iterator.next();
            //获取数据
            System.out.println(next.getSourceAsString());
        }


        client.close();


    }

    /**
     * 词条查询: 查询2次,对于输入的条件不分词
     * @param args
     * @throws Exception
     */

    public static void main5(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.prepareSearch("java_0828_new")
                .setTypes("article")
                .setQuery(QueryBuilders.termQuery("content", "ELASTICSEARCH"))//匹配查询
                .get();
        //多条数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            //获取每条数据
            SearchHit next = iterator.next();
            //获取数据
            System.out.println(next.getSourceAsString());
        }


        client.close();


    }
    /**
     * 匹配查询: 查询2次,对于输入的条件分词
     * @param args
     * @throws Exception
     */

    public static void main4(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.prepareSearch("java_0828_new")
                .setTypes("article")
                .setQuery(QueryBuilders.matchQuery("content", "ELASTICSEARCH"))//匹配查询
                .get();
        //多条数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            //获取每条数据
            SearchHit next = iterator.next();
            //获取数据
            System.out.println(next.getSourceAsString());
        }


        client.close();


    }
    /**
     * 字符串查询: 查询两次,对于输入的条件分词
     * @param args
     * @throws Exception
     */
    public static void main3(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.prepareSearch("java_0828_new")
                .setTypes("article")
                .setQuery(QueryBuilders.queryStringQuery("ELASTICSEARCH").field("content"))//字符串查询
                .get();
        //多条数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            //获取每条数据
            SearchHit next = iterator.next();
            //获取数据
            System.out.println(next.getSourceAsString());
        }


        client.close();


    }


    /**
     *查询全部: 查询一次(文档域)
     * @param args
     * @throws Exception
     */

    public static void main2(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse getResponse = client.prepareSearch("java_0828_new")
                .setTypes("article")
                .setQuery(QueryBuilders.matchAllQuery())
                .get();
        SearchHits hits = getResponse.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit next = iterator.next();
            String sourceAsString = next.getSourceAsString();
            System.out.println(sourceAsString);
        }
        client.close();


    }


    /**
     * 主键查询
     * @param args
     * @throws Exception
     */
    public static void main1(String[] args)throws Exception {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        GetResponse getResponse = client.prepareGet("java_0828_new", "article", "55").get();

        String sourceAsString = getResponse.getSourceAsString();
        System.out.println(sourceAsString);

        Article article = JSONObject.parseObject(sourceAsString, Article.class);
        System.out.println(article);


    }
}
