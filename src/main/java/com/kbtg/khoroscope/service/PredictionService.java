package com.kbtg.khoroscope.service;

import com.kbtg.khoroscope.model.Prediction;

import java.util.Collection;

public interface PredictionService {

    public Collection<Prediction> findPredictionByParameter(String predictionTypeCode,String predictionCode) throws Exception;

}
