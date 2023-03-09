package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * skuMapper映射
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {


    @Update("UPDATE sku_info set is_sale=#{status} WHERE id=#{skuId}")
    int updateSaleStatus(Long skuId, Short status);

    /**
     * 扣减库存
     * @param skuId
     * @param num
     * @return
     */
    @Update("update sku_info set stock = stock - #{num} where id = #{skuId} and stock >= #{num}")
    public int decount(Long skuId, Integer num);

    /**
     * 事务的特性:
     * 1.原子性
     * 2.隔离性: 读未提交 读已提交 可重复读 串行化
     * 3.持久性
     * 4.破坏隔离产生一致性
     */

    /**
     * 数据库的隔离级别:
     * a.读未提交: A事务能读取到B事务未提交事务的修改数据--->问题: 脏读 不可重复读 幻读
     * b.读已提交: A事务只能读取到B事务已经提交的事务数据--->问题: 不可重复读 幻读
     * c.可重复读(默认): 同一个事务对同一份数据,多次读取到的结果是一致的--->问题: 幻读
     * d.串行化: 排队一个个来-->没有问题
     */

    /**
     * 数据库隔离级别产生的问题:
     *  脏读: A事务能读取到B事务未提交事务的修改数据,B事务提交失败,A读取到的就是脏数据
     *  不可重复读: 同一个事务对同一份数据,多次读取到的结果是不一致的(存在的---修改)
     *  幻读: 同一个事务对同一个查询,多次读取到的结果不一致(不确定存在或不存在---新增)
     */

    /**
     * 乐观锁: 乐观的认为我修改的数据,别人没有修改
     *      实现: 版本号
     *  悲观锁: 悲观的认为我修改的数据已经被人修改了
     *      实现:
     *      1.开启事务
     *      2.执行sql: select * from sku_info where id = 1 for update;(id=1的数据将被加上排他锁)
     *      3.执行逻辑---->失败--->死锁(严重)
     *      4.提交事务(释放锁)--->失败--->死锁(严重)
     */
    /**
     * 回退库从
     * @param skuId
     * @param num
     * @return
     */
    @Update("update sku_info set stock = stock + #{num} where id = #{skuId}")
    public int rollbackCart(Long skuId, Integer num);
}
