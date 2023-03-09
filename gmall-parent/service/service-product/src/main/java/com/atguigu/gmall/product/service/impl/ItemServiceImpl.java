package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class ItemServiceImpl implements ItemService {

    /**
     * 远程调用查询接口
     * @param skuId
     */

    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Override
    public SkuInfo getPageInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    /**
     * 从redis中获取数据
     *
     * @param skuId
     * @return
     */
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;
    @Override
    public SkuInfo getRedisPageInfo(Long skuId) {
        //从redis中获取数据
        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get("sku:"+skuId+"info");
        if (skuInfo != null){
            return skuInfo;

        }
            //redis中如果没有加锁从数据库获取数据
            RLock lock = redissonClient.getLock("sku:"+skuId+"info");
            try {
                if (lock.tryLock(100,10,TimeUnit.SECONDS)){
                    try {
                        //从redis中获取数据
                        skuInfo = (SkuInfo) redisTemplate.opsForValue().get("sku:"+skuId+"info");
                        if (skuInfo != null){
                            return skuInfo;

                        }
                        skuInfo = skuInfoMapper.selectById(skuId);
                        if (skuInfo==null || skuInfo.getId()==null){
                           skuInfo = new SkuInfo();
                           redisTemplate.opsForValue().set("sku:"+skuId+"info",skuInfo,300,TimeUnit.SECONDS);
                        }else {
                            //最后放入redis中
                            redisTemplate.opsForValue().set("sku:"+skuId+"info",skuInfo,24*60*60,TimeUnit.SECONDS);
                        }
                        return skuInfo;

                    }catch (Exception e){
                        log.error("执行失败");
                    }finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                log.error("加锁失败");
            }
            //返回
        return null;
    }

    /**
     * 查询分类信息
     *
     * @param category3Id
     * @return
     */
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Override
    public BaseCategoryView getCategory(Long category3Id) {
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectById(category3Id);
        return baseCategoryView;
    }

    /**
     * 查询图片
     *
     * @param SkuId
     * @return
     */
    @Resource
    private SkuImageMapper skuImageMapper;

    @Override
    public List<SkuImage> getSpuImage(Long skuId) {
        List<SkuImage> skuImages = skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>()
                .eq(SkuImage::getSkuId, skuId));
        return skuImages;
    }

    /**
     * 价格查询
     *
     * @param skuId
     * @return
     */

    @Override
    public BigDecimal getPrice(Long skuId) {
        BigDecimal price = skuInfoMapper.selectById(skuId).getPrice();
        return price;

    }

    /**
     * 查询属性
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long skuId, Long spuId) {
        List<SpuSaleAttr> list = spuSaleAttrMapper.selectSpuSaleAttrBySpuIdAndSkuId(skuId, spuId);

        return list;
    }

    /**
     * 跳转页面
     *
     * @param spuId
     * @return
     */
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Override
    public Map getSkuIdAndValues(Long spuId) {
        List<Map> skuSaleValue = skuSaleAttrValueMapper.selectSkuIdAndValues(spuId);

        Map map = new ConcurrentHashMap<>();
        skuSaleValue.stream().forEach(a->{
            Object skuId = a.get("sku_id");
            Object valuesId = a.get("values_id");
            map.put(skuId,valuesId);
        });

        return map;
    }

    /**
     * 品牌查询
     */
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    @Override
    public BaseTrademark getMark(Long id) {
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(id);

        return baseTrademark;
    }

    /**
     * 属性查询
     *
     * @param skuId
     * @return
     */
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Override
    public List<BaseAttrInfo> getBaseAttrInfoBySkuId(Long skuId) {
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);
        return baseAttrInfos;
    }

    /**
     * 扣减库从
     *
     * @return
     */
    @Override
    public boolean decountBase(Map<Object,Object> decountSkuMap) {

        decountSkuMap.entrySet().forEach(o->{
            //扣减库从
            int decount = skuInfoMapper.decount(Long.valueOf(o.getKey().toString()), Integer.valueOf(o.getValue().toString()));
            if (decount <=0){
                throw new RuntimeException("库从不足");
            }

        });
        return true;
    }

    /**
     * 回退库从
     *
     * @param
     * @return
     */
    @Override
    public boolean rollbackStock(Map<Object,Object> decountSkuMap) {
        decountSkuMap.entrySet().forEach(o->{
            int i = skuInfoMapper.rollbackCart(Long.valueOf(o.getKey().toString()), Integer.parseInt(o.getValue().toString()));
            if (i <=0){
                throw new RuntimeException("回滚失败");
            }
        });
        return true;
    }
}
