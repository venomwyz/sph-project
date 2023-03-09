package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品价格的控制层
 */
@RestController
@RequestMapping("/api/value")
public class BaseAttrValueController {

    @Autowired
    BaseAttrValueService baseAttrValueService;

    /**
     * 查询所有商品价格
     * @return
     */

    @GetMapping("findAll")
    public Result findAll(){
        List<BaseAttrValue> all = baseAttrValueService.findAll();
        return Result.ok(all);
    }

    /**
     * 删除数据
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Long id){
        baseAttrValueService.delete(id);
        return Result.ok();

    }

    /**
     * 插入数据
     * @param baseAttrValue
     * @return
     */
    @PostMapping
    public Result insert(@RequestBody BaseAttrValue baseAttrValue){
        baseAttrValueService.insert(baseAttrValue);
        return Result.ok();

    }

    /**
     * 修改数据
     * @param baseAttrValue
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseAttrValue baseAttrValue){
        baseAttrValueService.update(baseAttrValue);
        return Result.ok();
    }

    /**
     * 页面查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("pages/{page}/{size}")
    public Result pages(@PathVariable Integer page,
                        @PathVariable Integer size){

        IPage<BaseAttrValue> page1 = baseAttrValueService.page(page, size);
        return Result.ok(page1);

    }

    /**
     * 根据条件查询
     * @param baseAttrValue
     * @return
     */
    @GetMapping("select")
    public Result select(@RequestBody BaseAttrValue baseAttrValue){
        List<BaseAttrValue> select = baseAttrValueService.select(baseAttrValue);
        return Result.ok(select);
    }

    /**
     * 分页条件查询
     * @param baseAttrValue
     * @param page
     * @param size
     * @return
     */

    @GetMapping("selectByPage/{page}/{size}")
    public Result selectByPage(@RequestBody BaseAttrValue baseAttrValue,
                               @PathVariable Integer page,
                               @PathVariable Integer size){
        IPage<BaseAttrValue> baseAttrValueIPage = baseAttrValueService.selectByPage(baseAttrValue, page, size);
        return Result.ok(baseAttrValueIPage);

    }


}
