package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 给前端页面的所有分类
 */
public interface IndexService {
    /**
     * 查询一级二级三类
     * @return
     */
    List<JSONObject> getCategoryAll();
}
