package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsDao goodsDao;
    @Autowired
    private ProductFeignClient productFeignClient;
    /**
     * 数据查询
     *
     * @param skuId
     */
    @Override
    public void addGoodsByEs(Long skuId) {
        //校验数据
        if (skuId == null){
            return;
        }
        //查询sku信息
        SkuInfo skuInfo = productFeignClient.selectBySkuId(skuId);
        //判断sku是否存在
        if (skuInfo==null || skuInfo.getId()==null){
            return;
        }
        //将skuInfo对象转换为Goods对象
        Goods goods = new Goods();
        goods.setId(skuInfo.getId());
        goods.setTitle(skuInfo.getSkuName());
        BigDecimal price = productFeignClient.getPrice(skuId);
        goods.setPrice(price.doubleValue());
        goods.setCreateTime(new Date());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        //品牌查询
        BaseTrademark tradeMake = productFeignClient.getTradeMake(skuInfo.getTmId());
        goods.setTmId(tradeMake.getId());
        goods.setTmName(tradeMake.getTmName());
        goods.setTmLogoUrl(tradeMake.getLogoUrl());
        BaseCategoryView category = productFeignClient.getCategory(skuInfo.getCategory3Id());
        goods.setCategory1Id(category.getCategory1Id());
        goods.setCategory1Name(category.getCategory1Name());
        goods.setCategory2Id(category.getCategory2Id());
        goods.setCategory2Name(category.getCategory2Name());
        goods.setCategory3Id(category.getCategory3Id());
        goods.setCategory3Name(category.getCategory3Name());

        //补全属性与品牌
        List<BaseAttrInfo> attrInfo = productFeignClient.getAttrInfo(skuId);
        List<SearchAttr> attrList = attrInfo.stream().map(getAttrInfo -> {
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(getAttrInfo.getId());
            searchAttr.setAttrName(getAttrInfo.getAttrName());
            searchAttr.setAttrValue(getAttrInfo.getAttrValueList().get(0).getValueName());
            return searchAttr;

        }).collect(Collectors.toList());
        goods.setAttrs(attrList);

        //存储es中
        goodsDao.save(goods);

    }

    /**
     * 删除数据
     * @param goodsId
     */

    @Override
    public void removeGoodsByEs(Long goodsId) {
        if (goodsId == null){
            return;

        }
        goodsDao.deleteById(goodsId);

    }

    /**
     * 添加热度值
     *
     * @param id
     */
@Resource
private RedisTemplate redisTemplate;
    @Override
    public void addHotScore(Long id) {
        //参数校验
        if (id==null){
            return;
        }
        //从数据库中取出数据
        Optional<Goods> optionalGoods = goodsDao.findById(id);

        //如果商品存在热度值+1，直到加10，就同步es一次
        Double score = redisTemplate.opsForZSet().incrementScore("Goods_Hot_Score", id, 1);

        //将计算值放回到es
        if (score%10==0){
            Goods goods = optionalGoods.get();
            goods.setHotScore(score.longValue());
            goodsDao.save(goods);
        }
    }
}
