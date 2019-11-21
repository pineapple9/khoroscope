package com.kbtg.khoroscope.controller;

import com.google.gson.Gson;
import com.kbtg.khoroscope.model.Prediction;
import com.kbtg.khoroscope.service.PredictionService;
import com.kbtg.khoroscope.service.PredictionServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PredictionController {

    @RequestMapping("/getPrediction")
    public String getPrediction(){

        Prediction obj = new Prediction();
        obj.setPredictionId(1);
        obj.setPredictionCode("PredictionCode");

        System.out.println("obj.getGypsyContent() ==> "+obj.getPredictionId());
        System.out.println("obj.getGypsyRemark() ==> "+obj.getPredictionCode());

        return new Gson().toJson(obj);
    }

    @RequestMapping("/")
    public String hello() throws Exception{

        PredictionService gypsyService = new PredictionServiceImpl();
        return new Gson().toJson(gypsyService.findPredictionByPredictionTypeCode("GYPSY"));

        //return "Hello K Horoscope!!!!";
    }

}
