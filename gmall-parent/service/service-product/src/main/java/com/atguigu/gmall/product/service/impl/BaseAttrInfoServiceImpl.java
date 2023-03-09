package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
@Service
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<BaseAttrInfo> findAll() {
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectList(null);
        return baseAttrInfos;
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseAttrInfo findById(Long id) {
        BaseAttrInfo selectById = baseAttrInfoMapper.selectById(id);
        return selectById;
    }

    /**
     * 插入数据
     *
     * @param baseAttrInfo
     */
    @Override
    public void insert(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo == null && StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            throw new RuntimeException("参数错误");
        }
        int insert = baseAttrInfoMapper.insert(baseAttrInfo);

        if (insert <= 0){
            throw new RuntimeException("未插入请重试");
        }

    }

    /**
     * 修改数据
     *
     * @param baseAttrInfo
     */
    @Override
    public void update(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo == null && StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            throw new RuntimeException("参数错误");
        }
        int update = baseAttrInfoMapper.updateById(baseAttrInfo);
        if (update < 0){
            throw new RuntimeException("修改失败请重试");
        }

    }

    /**
     * 删除数据
     * @param id
     */

    @Override
    public void delete(Long id) {
        if (id ==null){
            return;
        }
        int i = baseAttrInfoMapper.deleteById(id);
        if (i< 0){
            throw new RuntimeException("删除错误");
        }

    }

    /**
     * 分页展示
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> pageBy(Integer page, Integer size) {

        IPage<BaseAttrInfo> iPage = baseAttrInfoMapper.selectPage(new Page<>(page, size), null);
        return iPage;
    }

    /**
     * 条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public List<BaseAttrInfo> select(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo == null){
           return baseAttrInfoMapper.selectList(null);

        }

        LambdaQueryWrapper<BaseAttrInfo> queryWrapper = getQueryWrapper(baseAttrInfo);

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectList(queryWrapper);
        return baseAttrInfos;

    }

    /**
     * 条件分页查询
     *  @param baseAttrInfo
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> selectByPage(BaseAttrInfo baseAttrInfo, Integer page, Integer size) {

        if (baseAttrInfo == null){
            return baseAttrInfoMapper.selectPage(new Page<>(page,size),null);
        }
        LambdaQueryWrapper<BaseAttrInfo> queryWrapper = getQueryWrapper(baseAttrInfo);

       return baseAttrInfoMapper.selectPage(new Page<>(page, size), queryWrapper);

    }

    /**
     * 提取条件查询的方法
     * @param baseAttrInfo
     * @return
     */

    private LambdaQueryWrapper<BaseAttrInfo> getQueryWrapper(BaseAttrInfo baseAttrInfo) {
        LambdaQueryWrapper<BaseAttrInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (baseAttrInfo.getId() != null){
            queryWrapper.eq(BaseAttrInfo::getId, baseAttrInfo.getId());
        }
        if (baseAttrInfo.getAttrName() != null){
            queryWrapper.like(BaseAttrInfo::getAttrName, baseAttrInfo.getAttrName());
        }
        if (baseAttrInfo.getCategoryId() != null){
            queryWrapper.eq(BaseAttrInfo::getCategoryId, baseAttrInfo.getCategoryId());
        }
        return queryWrapper;
    }


}
