package com.kbtg.khoroscope.service;

import com.kbtg.khoroscope.model.Prediction;
import com.kbtg.khoroscope.dao.PredictionDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;

public class PredictionServiceImpl implements PredictionService {

    ApplicationContext context = new ClassPathXmlApplicationContext(
            "spring-beans.xml");

    PredictionDAO predictionDAO = (PredictionDAO) context.getBean("predictionDAO");

    public Collection<Prediction> findPredictionByPredictionTypeCode(String predictionTypeCode) throws Exception{
        return predictionDAO.findPredictionByPredictionTypeCode(predictionTypeCode);
    }


}
