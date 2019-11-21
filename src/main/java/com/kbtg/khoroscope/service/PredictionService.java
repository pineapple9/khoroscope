package com.kbtg.khoroscope.service;

import com.kbtg.khoroscope.model.Prediction;

import java.util.Collection;

public interface PredictionService {

    public Collection<Prediction> findPredictionByPredictionTypeCode(String predictionTypeCode) throws Exception;

}
