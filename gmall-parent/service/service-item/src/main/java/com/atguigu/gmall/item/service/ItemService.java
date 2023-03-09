package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * 获取页面属性接口
 */
public interface ItemService {
    /**
     * 获取页面信息
     * @param skuId
     * @return
     */
    Map<String, Object> getItemPageInfo(Long skuId);

}
