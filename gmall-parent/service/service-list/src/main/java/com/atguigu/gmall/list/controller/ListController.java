package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("api/list")
public class ListController {
    @Resource
    private ListService listService;

    /**
     * 搜索查询
     * @param
     * @return
     */
    @GetMapping("/getSearch")
    public Map<String, Object> getSearch(@RequestParam Map<String,String> searchDate){
        Map<String, Object> search = listService.search(searchDate);
        return search;
    }
}
