package com.kbtg.khoroscope.dao;

import com.kbtg.khoroscope.model.Prediction;

import java.sql.SQLException;
import java.util.Collection;

public interface PredictionDAO {

    public Collection<Prediction> findPredictionByParameter(String predictionTypeCode,String predictionCode) throws SQLException;

}
