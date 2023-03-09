package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * es搜索接口
 */
public interface ListService {
    /**
     * 搜索条件
     * @param keyword
     * @return
     */
    Map<String, Object> search(Map<String,String> searchDate);
}
