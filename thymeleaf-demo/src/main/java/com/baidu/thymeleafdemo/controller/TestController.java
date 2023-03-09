package com.baidu.thymeleafdemo.controller;

import com.baidu.thymeleafdemo.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.xml.crypto.Data;
import java.util.*;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String m1(Model model){
        model.addAttribute("to","0123456789");
        return "torrey.html";
    }

    @GetMapping("user")
    public String user(Model model){
        List<User> arrayList = new ArrayList<>();
        for (int i = 1; i <=10 ; i++) {
            User user = new User();
            user.setId(1);
            user.setName("torey");
            user.setAge(20);
            arrayList.add(user);

        }
        model.addAttribute("userList",arrayList);
        model.addAttribute("date", new Date());

        model.addAttribute("if1",18);

        Map<String, String> hashMap = new HashMap<>();

        hashMap.put("chg","chg");
        hashMap.put("mf","mf");
        hashMap.put("mche","mche");
        model.addAttribute("hashMap",hashMap);

        //图片
        model.addAttribute("image","https://img0.baidu.com/it/u=1472391233,99561733&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500");

        model.addAttribute("to2","12345678");
        model.addAttribute("to3","12345678");

        model.addAttribute("url","/test/url");

        return "torrey";

    }

    @GetMapping("url")
    public String m2(String name,Integer age){
        System.out.println(name);
        System.out.println(age);
        return "redirect:http://www.baidu.com";
    }
}
