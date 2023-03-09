package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 品牌service实现
 */
@Service
public class BaseTrademarkServiceImpl implements BaseTrademarkService {

@Resource
private BaseTrademarkMapper baseTrademarkMapper;
    /**
     * 查询所有数据
     *
     * @return
     */
    @Override
    public List<BaseTrademark> findAll() {
        List<BaseTrademark> baseTrademarks = baseTrademarkMapper.selectList(null);
        return baseTrademarks;
    }

    /**
     * 分页查询所有的品牌数据
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseTrademark> findAllByPage(Integer page, Integer size) {

        IPage<BaseTrademark> iPage = baseTrademarkMapper.selectPage(new Page<>(page, size), null);

        return iPage;
    }

    /**
     * 添加品牌
     * @param baseTrademark
     */
    @Override
    public void saveTrademark(BaseTrademark baseTrademark) {
        if (baseTrademark == null && StringUtils.isEmpty(baseTrademark.getTmName())){
            throw new RuntimeException("参数错误");
        }

        int insert = baseTrademarkMapper.insert(baseTrademark);
        if (insert <=0){
            throw new RuntimeException("添加错误");
        }
    }
}
