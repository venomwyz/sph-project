package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品价格的service层
 */
@Service
public class BaseAttrValueServiceImpl implements BaseAttrValueService {
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;

    /**
     * 查询全部数据
     *
     * @return
     */
    @Override
    public List<BaseAttrValue> findAll() {

        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(null);

        return baseAttrValues;
    }

    /**
     * 删除数据
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        if (id == null){
            return;
        }
        int i = baseAttrValueMapper.deleteById(id);
        if (i<0){
            throw new RuntimeException("删除数据失败");
        }
    }

    /**
     * 新增数据
     *
     * @param baseAttrValue
     */
    @Override
    public void insert(BaseAttrValue baseAttrValue) {
        if (baseAttrValue == null){
            throw new RuntimeException("参数错误");
        }
        int insert = baseAttrValueMapper.insert(baseAttrValue);
        if (insert <= 0){
            throw new RuntimeException("数据插入失败");
        }

    }

    /**
     * 修改数据
     *
     * @param baseAttrValue
     */
    @Override
    public void update(BaseAttrValue baseAttrValue) {

        if (baseAttrValue == null){
         throw new RuntimeException("参数错误");
        }
        int update = baseAttrValueMapper.updateById(baseAttrValue);
        if (update <0){
            throw new RuntimeException("数据修改失败");
        }

    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrValue> page(Integer page, Integer size) {
        if (page ==null&& size==null){
            throw new RuntimeException("页数不能为空");
        }
        IPage<BaseAttrValue> valueIPage = baseAttrValueMapper.selectPage(new Page<>(page, size), null);

        return valueIPage;
    }

    /**
     * 条件查询
     *
     * @param baseAttrValue
     * @return
     */
    @Override
    public List<BaseAttrValue> select(BaseAttrValue baseAttrValue) {
        if (baseAttrValue == null){
          return   baseAttrValueMapper.selectList(null);
        }

        LambdaQueryWrapper<BaseAttrValue> queryWrapper = getLambdaQueryWrapper(baseAttrValue);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(queryWrapper);
        return baseAttrValues;
    }

    /**
     * 抽出查询方法
     * @param baseAttrValue
     * @return
     */

    private LambdaQueryWrapper<BaseAttrValue> getLambdaQueryWrapper(BaseAttrValue baseAttrValue) {
        LambdaQueryWrapper<BaseAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        if (baseAttrValue.getId() != null){
            queryWrapper.eq(BaseAttrValue::getId, baseAttrValue.getId());
        }
        if (baseAttrValue.getValueName() != null){
            queryWrapper.like(BaseAttrValue::getValueName, baseAttrValue.getValueName());
        }
        if (baseAttrValue.getAttrId() != null){
            queryWrapper.eq(BaseAttrValue::getAttrId, baseAttrValue.getAttrId());
        }
        return queryWrapper;
    }

    /**
     * 分页条件查询
     *
     * @param baseAttrValue
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrValue> selectByPage(BaseAttrValue baseAttrValue, Integer page, Integer size) {

        if (baseAttrValue == null){
            baseAttrValueMapper.selectPage(new Page<>(page,size),null);
        }
        LambdaQueryWrapper<BaseAttrValue> lambdaQueryWrapper = getLambdaQueryWrapper(baseAttrValue);
        IPage<BaseAttrValue> baseAttrValueIPage = baseAttrValueMapper.selectPage(new Page<>(page, size)
                , lambdaQueryWrapper);
        return baseAttrValueIPage;
    }
}
