package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.MangerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类service接口
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MangerServiceImpl implements MangerService {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;

    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;

    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;

    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<BaseCategory1> findAll() {
        List<BaseCategory1> category1List = baseCategory1Mapper.selectList(null);
        return category1List;
    }

    /**
     * 查询对应的BaseCategory2；
     *
     * @param id1
     * @return
     */
    @Override
    public List<BaseCategory2> findAll2(Long id1) {
        //参数验证

        if (id1 == null) {
            throw new RuntimeException("参数错误");
        }
        //查询数据
        LambdaQueryWrapper<BaseCategory2> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCategory2::getCategory1Id, id1);
        List<BaseCategory2> category2List = baseCategory2Mapper.selectList(queryWrapper);

        return category2List;
    }

    /**
     * 查询分类3中的所有数据
     *
     * @param id2
     * @return
     */
    @Override
    public List<BaseCategory3> findAll3(Long id2) {

        if (id2 == null) {
            throw new RuntimeException("参数错误");
        }
        LambdaQueryWrapper<BaseCategory3> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCategory3::getCategory2Id, id2);
        List<BaseCategory3> category3List = baseCategory3Mapper.selectList(queryWrapper);
        return category3List;
    }

    /**
     * 添加平台数据
     *
     * @param baseAttrInfo
     */
    @Override
    public void insertValue(BaseAttrInfo baseAttrInfo) {
        //数据校验
        if (baseAttrInfo == null && StringUtils.isEmpty(baseAttrInfo.getAttrName())) {
            throw new RuntimeException("参数错误");
        }
        //修改数据
        if (baseAttrInfo.getId() != null) {
            int update = baseAttrInfoMapper.updateById(baseAttrInfo);
            if (update < 0) {
                throw new RuntimeException("修改数据失败");
            }
            int delete = baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                    .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
            if (delete < 0) {
                throw new RuntimeException("删除失败");
            }

        } else {
            //保存平台数据名称
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if (insert <= 0) {
                throw new RuntimeException("插入数据错误");
            }
        }
        //获取Id
        Long id = baseAttrInfo.getId();
        //在每个平台属性值对象中补充平台属性的id
        baseAttrInfo.getAttrValueList().stream().forEach(attrValueList -> {
            if (StringUtils.isEmpty(attrValueList)) {
                return;
            }

            attrValueList.setAttrId(id);
            int insert1 = baseAttrValueMapper.insert(attrValueList);
            if (insert1 <= 0) {
                throw new RuntimeException("值数据插入失败");
            }
        });


    }

    /**
     * 获取平台数据
     *
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfo(Long id) {

        List<BaseAttrInfo> selectBaseAttrInfoByCategory3Id = baseAttrInfoMapper
                .selectBaseAttrInfoByCategory3Id(id);

        return selectBaseAttrInfoByCategory3Id;
    }

    /**
     * 查询所有的销售属性
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> findAllBySale() {
        List<BaseSaleAttr> baseSaleAttrs = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrs;
    }

    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    /**
     * spu的保存接口
     *
     * @param spuInfo
     */
    @Override
    public void saveSpu(SpuInfo spuInfo) {
        //数据校验

        if (spuInfo == null) {
            throw new RuntimeException("参数错误");
        }
        //判断是查询还是新增

        if (spuInfo.getId() != null) {
            int update = spuInfoMapper.updateById(spuInfo);
            if (update < 0) {
                throw new RuntimeException("修改错误");
            }
            //删除所有的属性表与图片表
            int delete = spuImageMapper.delete(new LambdaQueryWrapper<SpuImage>()
                    .eq(SpuImage::getSpuId, spuInfo.getId()));
            if (delete < 0) {
                throw new RuntimeException("图片删除失败");
            }
            int delete1 = spuSaleAttrMapper.delete(new LambdaQueryWrapper<SpuSaleAttr>()
                    .eq(SpuSaleAttr::getSpuId, spuInfo.getId()));
            if (delete1 < 0) {
                throw new RuntimeException("属性删除失败");
            }
            int delete2 = spuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SpuSaleAttrValue>()
                    .eq(SpuSaleAttrValue::getSpuId, spuInfo.getId()));
            if (delete2 < 0) {
                throw new RuntimeException("销售属性值删除失败");
            }
        } else {
            //新增数据
            int insert = spuInfoMapper.insert(spuInfo);
            if (insert <= 0) {
                throw new RuntimeException("新增失败");
            }
        }
        //获取id
        Long infoId = spuInfo.getId();
        //根据id添加图片属性

        spuInfo.getSpuImageList().stream().forEach(spuImageList -> {
            if (StringUtils.isEmpty(spuImageList)) {
                return;
            }

            spuImageList.setSpuId(infoId);
            int insert = spuImageMapper.insert(spuImageList);
            if (insert <= 0) {
                throw new RuntimeException("新增图片失败");
            }
        });

        //根据id添加spu的属性名与值
        spuInfo.getSpuSaleAttrList().stream().forEach(spuSaleAttrList -> {
            if (StringUtils.isEmpty(spuSaleAttrList)) {
                return;
            }
            spuSaleAttrList.setSpuId(infoId);
            int insert = spuSaleAttrMapper.insert(spuSaleAttrList);
            if (insert <= 0) {
                throw new RuntimeException("新增销售属性失败");
            }

            spuSaleAttrList.getSpuSaleAttrValueList().stream().forEach(spuSaleAttrValueList -> {
                if (StringUtils.isEmpty(spuSaleAttrValueList)) {
                    return;
                }
                spuSaleAttrValueList.setBaseSaleAttrId(spuSaleAttrList.getBaseSaleAttrId());
                spuSaleAttrValueList.setSpuId(infoId);
                spuSaleAttrValueList.setSaleAttrName(spuSaleAttrList.getSaleAttrName());
                int insert1 = spuSaleAttrValueMapper.insert(spuSaleAttrValueList);
                if (insert1 <= 0) {
                    throw new RuntimeException("销售属性值插入失败");
                }

            });


        });

    }

    /**
     * spu分页查询
     *
     * @param page
     * @param size
     * @param id3
     * @return
     */
    @Override
    public IPage<SpuInfo> selectSpuByPage(Integer page, Integer size, Long id3) {
        IPage<SpuInfo> iPage = spuInfoMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getCategory3Id, id3));

        return iPage;

    }

    /**
     * 销售属性展示接口
     *
     * @param id
     * @return
     */
    @Override
    public List<SpuSaleAttr> selectSpuBySaleValue(Long id) {

        List<SpuSaleAttr> list = spuSaleAttrMapper.getSpuBySaleById(id);
        return list;
    }

    /**
     * 图片展示
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> selectByImage(Long spuId) {
        List<SpuImage> spuImages = spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>()
                .eq(SpuImage::getSpuId, spuId));

        return spuImages;
    }

    /**
     * sku的属性保存接口
     *
     * @param skuInfo
     */
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;

    @Override
    public void saveSku(SkuInfo skuInfo) {
        //数据校验
        if (skuInfo == null) {
            throw new RuntimeException("参数错误");
        }

        //添加Info属性数据

        if (skuInfo.getId() != null) {
            int update = skuInfoMapper.updateById(skuInfo);
            if (update < 0) {
                throw new RuntimeException("修改失败");
            }
            int delete = skuImageMapper.delete(new LambdaQueryWrapper<SkuImage>()
                    .eq(SkuImage::getSkuId, skuInfo.getId()));

            int delete1 = skuAttrValueMapper.delete(new LambdaQueryWrapper<SkuAttrValue>()
                    .eq(SkuAttrValue::getSkuId, skuInfo.getId()));
            int delete2 = skuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SkuSaleAttrValue>()
                    .eq(SkuSaleAttrValue::getSkuId, skuInfo.getId()));

            if (delete < 0 || delete1 < 0 || delete2 < 0) {
                throw new RuntimeException("更新错误");
            }
        } else {

            int insert = skuInfoMapper.insert(skuInfo);
            if (insert <= 0) {
                throw new RuntimeException("添加数据失败");
            }

        }
        //获取skuId
        Long skuInfoId = skuInfo.getId();

        //添加平台属性
        skuInfo.getSkuAttrValueList().stream().forEach(skuAttrValueList -> {
            skuAttrValueList.setSkuId(skuInfoId);
            int insert = skuAttrValueMapper.insert(skuAttrValueList);

            if (insert <= 0) {
                throw new RuntimeException("属性插入失败");
            }
        });
        //获取spuId
        Long spuId = skuInfo.getSpuId();
        //添加销售属性
        skuInfo.getSkuSaleAttrValueList().stream().forEach(skuSaleAttrValueList -> {
            skuSaleAttrValueList.setSkuId(skuInfoId);

            skuSaleAttrValueList.setSpuId(spuId);
            int insert = skuSaleAttrValueMapper.insert(skuSaleAttrValueList);
            if (insert <= 0) {
                throw new RuntimeException("销售属性值添加失败");
            }
        });
        //添加图片
        skuInfo.getSkuImageList().stream().forEach(skuImageList -> {
            skuImageList.setSkuId(skuInfoId);
            int insert = skuImageMapper.insert(skuImageList);
            if (insert <= 0) {
                throw new RuntimeException("图片保存失败");
            }
        });

    }

    /**
     * 获取sku的分页
     *
     * @param page
     * @param size
     * @return
     */

    @Override
    public IPage<SkuInfo> selectBySkuByPage(Integer page, Integer size) {
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(new Page<>(page, size), null);
        return skuInfoIPage;
    }

    /**
     * 上线与下线
     *
     * @param skuId
     * @param status
     */
    @Override
    public void updateSaleStatus(Long skuId, Short status) {
        if (skuId == null) {
            throw new RuntimeException("参数错误");
        }

        int i = skuInfoMapper.updateSaleStatus(skuId, status);

        if (i < 0) {
            throw new RuntimeException("修改错误");
        }
    }

}
