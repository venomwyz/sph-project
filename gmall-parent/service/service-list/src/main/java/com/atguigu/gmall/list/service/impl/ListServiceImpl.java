package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.fetch.subphase.highlight.Highlighter;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索
 */
@Service
public class ListServiceImpl implements ListService {
    /**
     * 搜索条件查询
     * @param keyWord
     */
    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Override
    public Map<String, Object> search(Map<String,String> searchDate) {
        try {
            //拼接查询
            SearchRequest buildSearch = getBuildSearch(searchDate);

            //执行查询
            SearchResponse searchResponse = restHighLevelClient.search(buildSearch, RequestOptions.DEFAULT);

            //解析并返回结果
            Map<String, Object> search = getSearch(searchResponse);
            return search;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 拼接查询
     * @param searchDate
     * @return
     */
    private SearchRequest getBuildSearch(Map<String,String> searchDate) {
        //索引
        SearchRequest searchRequest = new SearchRequest("goods_java0107");
        //查询条件，用什么查询
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建组合查询
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        String keyword = searchDate.get("keyword");
        if (!StringUtils.isEmpty(keyword)){

            boolQueryBuilder.must(QueryBuilders.matchQuery("title",keyword));

        }
        //分类属性查询
        String category = searchDate.get("category");
        if (!StringUtils.isEmpty(category)){
            String[] split = category.split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("category",split[0]));

        }
        //品牌属性查询 1:华为
        String tradeMark = searchDate.get("tradeMark");
        if(!StringUtils.isEmpty(tradeMark)){
            String[] split = tradeMark.split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId", split[0]));
        }
        //平台属性的查询 attr_?=1:value
        searchDate.entrySet().stream().forEach(entry->{
            //获取等号左边的数据
            String key = entry.getKey();
            if (key.startsWith("attr_")){
                //获取参数内容
                String value = entry.getValue();

                //切分
                String[] split = value.split(":");
                //拼接条件
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("attrs.attrId", split[0]))
                        .must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                boolQueryBuilder
                        .must(QueryBuilders.nestedQuery("attrs",queryBuilder, ScoreMode.None));
            }

        });
        //价格查询：判断价格1000-2000,或者10000以上
        String price = searchDate.get("price");
        //判断
        if (!StringUtils.isEmpty(price)){
            String replace = price.replace("元", "").replace("以上", "");
            //将拿到的数据切割
            String[] split = replace.split("-");
            //确定是在那个区间
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));

            //如果split的值存在两个就将第二个值也判断
            if (split.length>1){
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }

        }



        //添加查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        //排序
        String sortField = searchDate.get("sortField");
        String sortRule = searchDate.get("sortRule");
        //判断是否为空
        if (!StringUtils.isEmpty(sortField)&&
        !StringUtils.isEmpty(sortRule)){
            //如果不为空就执行查询
            searchSourceBuilder.sort(sortField, SortOrder.valueOf(sortRule));
        }else {
            //如果为空就执行默认
            searchSourceBuilder.sort("id",SortOrder.ASC);
        }
        //分页查询
        String pageNum = searchDate.get("pageNum");


        //设置每页显示的数（模数值）
        searchSourceBuilder.size(50);
        //计算当前在多少页
        /**
         *  1------0-49
         *  2------50-99
         *  (（页数-）*50)计算是在多少页
         */
        int pageNum1 = getPageNum(searchDate.get("pageNum"));
        searchSourceBuilder.from((pageNum1-1)*50);

        //高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style=color:red>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);

        //将品牌进行聚合
        searchSourceBuilder.aggregation(AggregationBuilders
                .terms("aggTmId").field("tmId")
                .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                .size(100)
        );
        //属性聚合搜索
        searchSourceBuilder.aggregation(
                AggregationBuilders.nested("aggAttrs","attrs")
                        .subAggregation(AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                                .size(100)
                        )
        );
        //根据条件进行搜索
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    /**
     * 计算根据用户传过来是在多少页
     * @param pageNum
     * @return
     */
    private int getPageNum(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            //防止负数
            i=i>0?i:1;
            //防止超出es的最大限制
            i=i>=200?199:i;
            return i;
        } catch (NumberFormatException e) {
            return 1;
        }
    }


    /**
     * 用私有方法做查询
     * @param
     * @return
     */
    private Map<String, Object> getSearch(SearchResponse searchResponse) {
        //返回整体数据
        Map<String,Object> map=new HashMap<>();

        //返回结果初始化
        List<Goods> arrayList = new ArrayList<>();
        //获取命中的数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //获取总命中的数据
        long totalHits = hits.totalHits;
        map.put("totalHits",totalHits);
        //遍历解析
        while (iterator.hasNext()){
            //获取每条数据
            SearchHit next = iterator.next();
            //获取每条数据的字符串
            String sourceAsString = next.getSourceAsString();
            //反序列化
            Goods jsonGoods = JSONObject.parseObject(sourceAsString,Goods.class);
            //将之前商品中的数据用高亮代替
            HighlightField title = next.getHighlightFields().get("title");
            //判断
            if (title != null){
                //获取的是全部的高亮数据
                Text[] fragments = title.getFragments();
                if (fragments!= null && fragments.length>0){
                    String title1="";
                //然后遍历
                for (Text fragment : fragments) {
                title1+=fragment;
                }
                jsonGoods.setTitle(title1);
                }
            }

            //保存商品
            arrayList.add(jsonGoods);
        }
        //解析品牌数据
        Aggregations aggregations = searchResponse.getAggregations();

        List<SearchResponseTmVo> tmake = getTmake(aggregations);
        //解析平台属性
        List<SearchResponseAttrVo> attrInfo = getAttrInfo(aggregations);

        map.put("goodsList",arrayList);
        map.put("tmake",tmake);
        map.put("attrInfo",attrInfo);
        //返回数据
        return map;
    }

    /**
     * 解析平台属性
     * @param aggregations
     * @return
     */
    private List<SearchResponseAttrVo> getAttrInfo(Aggregations aggregations) {
        //获取nested的聚合结果
        ParsedNested aggAttrs = aggregations.get("aggAttrs");
        //获取属性id的聚合结果
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        //遍历
        return aggAttrId.getBuckets().stream().map(getAttrValue->{
            //获取对象,要将次对象返回给前端
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取id
            long attrId = getAttrValue.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取name
            ParsedStringTerms aggAttrName = getAttrValue.getAggregations().get("aggAttrName");
            //判断
            if (!aggAttrName.getBuckets().isEmpty()){
                //获取名字
                String keyAsString = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(keyAsString);
            }
            //获取值
            ParsedStringTerms aggAttrValue = getAttrValue.getAggregations().get("aggAttrValue");
            //判断
            if (!aggAttrValue.getBuckets().isEmpty()){
                //因为值存在多个所以要遍历
                List<String> stringList = aggAttrValue.getBuckets().stream().map(attrValue -> {
                    String keyAsString = attrValue.getKeyAsString();

                    return keyAsString;
                }).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValueList(stringList);
            }
            return searchResponseAttrVo;

        }).collect(Collectors.toList());

    }

    /**
     * 解析品牌数据
     * @param aggregations
     * @return
     */
    private List<SearchResponseTmVo> getTmake(Aggregations aggregations) {
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");

        //获取id
        return aggTmId.getBuckets().stream().map(tMake -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            long tmId = ((Terms.Bucket) tMake).getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);

            //获取聚合的名字
            ParsedStringTerms aggTmName = ((Terms.Bucket) tMake).getAggregations().get("aggTmName");
            List<? extends Terms.Bucket> buckets = aggTmName.getBuckets();
            //判断是否为空
            if (buckets != null && buckets.size() > 0) {
                String keyAsString = buckets.get(0).getKeyAsString();
                searchResponseTmVo.setTmName(keyAsString);
            }
            //获取聚合的logo
            ParsedStringTerms aggTmLogoUrl = ((Terms.Bucket) tMake).getAggregations().get("aggTmLogoUrl");
            List<? extends Terms.Bucket> buckets1 = aggTmLogoUrl.getBuckets();
            //判断是否为空
            if (buckets1 != null && buckets1.size() > 0) {
                String keyAsString = buckets1.get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(keyAsString);

            }
            return searchResponseTmVo;

        }).collect(Collectors.toList());


    }
}
