package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台属性名称表的控制层
 */
@RestController
@RequestMapping("/api/info")
public class BaseAttrInfoController  {
    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * 查询所有数据
     * @return
     */
    @GetMapping("findAll")
    public Result findAll(){
        List<BaseAttrInfo> findAll = baseAttrInfoService.findAll();
        return Result.ok(findAll);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */

    @GetMapping("findById/{id}")
    public Result findById(@PathVariable Long id){
        BaseAttrInfo baseAttrInfo = baseAttrInfoService.findById(id);
        return Result.ok(baseAttrInfo);

    }

    /**
     * 插入数据
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("insert")
    public Result insert(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.insert(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 修改数据
     * @param baseAttrInfo
     * @return
     */

    @PutMapping("update")
    public Result update(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.update(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 删除一条数据
     * @param id
     * @return
     */

    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Long id){
        baseAttrInfoService.delete(id);
        return Result.ok();

    }

    /**
     * 分页展示
     * @param page
     * @param size
     * @return
     */

    @GetMapping("pages/{page}/{size}")
    public Result pages(@PathVariable Integer page,
                        @PathVariable Integer size){

        IPage<BaseAttrInfo> iPage = baseAttrInfoService.pageBy(page, size);
        return Result.ok(iPage);


    }

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    @GetMapping("select")
    public Result select(@RequestBody BaseAttrInfo baseAttrInfo){
        List<BaseAttrInfo> select = baseAttrInfoService.select(baseAttrInfo);
        return Result.ok(select);
    }

    /**
     * 分页条件查询
     * @param baseAttrInfo
     * @param page
     * @param size
     * @return
     */
    @GetMapping("selectByPage/{page}/{size}")
    public Result selectByPage(@RequestBody BaseAttrInfo baseAttrInfo,
                               @PathVariable Integer page,
                               @PathVariable Integer size){

        IPage<BaseAttrInfo> baseAttrInfos = baseAttrInfoService.selectByPage(baseAttrInfo, page, size);
        return Result.ok(baseAttrInfos);

    }

}
