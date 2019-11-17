package com.kbtg.khoroscope.controller;

import com.google.gson.Gson;
import com.kbtg.khoroscope.Model.GypsyModel;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GypsyController {

    @RequestMapping("/getPrediction")
    public String getPrediction(){

        GypsyModel obj = new GypsyModel();
        obj.setGypsyContent("test GypsyContent");
        obj.setGypsyRemark("test Remark");

        System.out.println("obj.getGypsyContent() ==> "+obj.getGypsyContent());
        System.out.println("obj.getGypsyRemark() ==> "+obj.getGypsyRemark());

        return new Gson().toJson(obj);
    }

    @RequestMapping("/")
    public String hello(){

        return "Hello K Horoscope!!!";
    }

}
