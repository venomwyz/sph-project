package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.all.util.Page;
import com.atguigu.gmall.list.client.ListFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("page/list")
public class ListController {

    @Value("${item.url}")
    String itemUrl;

    @Autowired
    private ListFeign listFeign;
    @GetMapping("getSearch")
    public String list(@RequestParam Map<String,String> searchDate,
                       Model model){
        Map<String, Object> searchDate1 = listFeign.getSearch(searchDate);
        //测试
        model.addAllAttributes(searchDate1);
        //搜索框的数据回显
        model.addAttribute("searchData",searchDate);
        //后面拼接url
        String url1 = getUrl1(searchDate);
        model.addAttribute("url", getUrl1(searchDate));
        //获取排序的url
        model.addAttribute("sortUrl", getSortUrl(searchDate));
        //获取总数据
        Object totalHits = searchDate1.get("totalHits");
        //计算页数
        //初始化分页对象
        Page pageInfo = new Page<>(Long.valueOf(totalHits.toString()),
                getPageNum(searchDate.get("pageNum")),
                50);
        model.addAttribute("pageInfo", pageInfo);
        //保存商品详情页的前缀
        model.addAttribute("itemUrl", itemUrl);


        return "list";

    }

    private String getUrl1(Map<String, String> searchDate) {

        //初始化
        String url = "/page/list/getSearch?";
        //拼接
        for (Map.Entry<String, String> entry : searchDate.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("pageNum")) {
                url += key + "=" + entry.getValue() + "&";
            }

        }
        //返回
        return url.substring(0, url.length() - 1);


    }

    /**
     * 获取排序的url
     * @param searchData
     * @return
     */
    private String getSortUrl(Map<String, String> searchData) {
        //初始化
        String url = "/page/list/getSearch?";
        //拼接
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            String key = entry.getKey();
            //不保留排序字段
            if(!key.equals("sortField") &&
                    !key.equals("sortRule") &&
                    !key.equals("pageNum")){
                url += key + "=" + entry.getValue() + "&";
            }
        }
        //返回
        return url.substring(0, url.length() - 1);
    }

    /**
     * 计算页码
     * @param pageNum
     */
    private static int getPageNum(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            //防止负数
            i = i>0?i:1;
            //防止超出限制
            return i>=200?199:i;
        }catch (Exception e){
            return 1;
        }
    }


}
