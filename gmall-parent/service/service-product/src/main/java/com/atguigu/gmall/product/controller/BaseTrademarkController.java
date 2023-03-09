package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌控制层
 */
@RestController
@RequestMapping("admin/product/baseTrademark")
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    /**
     * 查询所有的品牌数据
     * @return
     */
    @GetMapping("getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> all = baseTrademarkService.findAll();

        return Result.ok(all);
    }

    /**
     * 品牌分页展示
     * @param page
     * @param limit
     * @return
     */

    @GetMapping("{page}/{limit}")
    public Result page(@PathVariable Integer page,
                       @PathVariable Integer limit){
        IPage<BaseTrademark> allByPage = baseTrademarkService.findAllByPage(page, limit);
        return Result.ok(allByPage);

    }

    @PostMapping("save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.saveTrademark(baseTrademark);
        return Result.ok();

    }


}
