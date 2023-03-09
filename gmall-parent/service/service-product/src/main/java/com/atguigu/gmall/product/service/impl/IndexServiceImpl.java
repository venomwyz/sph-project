package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndexServiceImpl implements IndexService {
    /**
     * 查询一级二级三类
     */
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Override
    public List<JSONObject> getCategoryAll() {
        //查询所有的一级二级三级分类
        List<BaseCategoryView> baseCategoryViewsAll = baseCategoryViewMapper.selectList(null);

        //将一级分类进行分桶
        Map<Long, List<BaseCategoryView>> collect1 = baseCategoryViewsAll.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

        //遍历这个桶
        return collect1.entrySet().stream().map(category1Ent->{
            JSONObject json1Object = new JSONObject();
            //获取分类一的id
            Long category1Id = category1Ent.getKey();
            json1Object.put("categoryId",category1Id);
            //获取分类的值，即所有的二级三级分类
            List<BaseCategoryView> category1EntValue = category1Ent.getValue();
            String category1Name = category1EntValue.get(0).getCategory1Name();
            json1Object.put("categoryName",category1Name);

            //获取二级分类的桶
            Map<Long, List<BaseCategoryView>> collect2 = category1EntValue.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //遍历这个二级分类
            List<JSONObject> jsonObjectList2 = collect2.entrySet().stream().map(category2Ent -> {
                JSONObject json2Object = new JSONObject();
                //获取二级分类的值
                Long category2Id = category2Ent.getKey();
                json2Object.put("categoryId", category2Id);
                List<BaseCategoryView> category2EntValue = category2Ent.getValue();
                //获取二级分类的name
                String category2Name = category2EntValue.get(0).getCategory2Name();
                json2Object.put("categoryName", category2Name);
                //遍历三级的数据
                List<JSONObject> jsonObjectList3 = category2EntValue.stream().map(category3Ent -> {
                    JSONObject json3Object = new JSONObject();
                    //三级的id
                    Long category3Id = category3Ent.getCategory3Id();
                    json3Object.put("categoryId", category3Id);
                    //三级的name
                    String category3Name = category3Ent.getCategory3Name();
                    json3Object.put("categoryName", category3Name);
                    return json3Object;
                }).collect(Collectors.toList());
                //二级分类与三级分类的关系
                json2Object.put("childCategory",jsonObjectList3);
                return json2Object;
            }).collect(Collectors.toList());
            //一级分类与二级分类的关系
            json1Object.put("childCategory", jsonObjectList2);
            return json1Object;
        }).collect(Collectors.toList());

    }
}
