package com.kbtg.khoroscope.model;

public class PredictionType {

    Integer predictionTypeId;
    String predictionTypeCode;
    String predictionTypeDescription;

    public Integer getPredictionTypeId() {
        return predictionTypeId;
    }

    public void setPredictionTypeId(Integer predictionTypeId) {
        this.predictionTypeId = predictionTypeId;
    }

    public String getPredictionTypeCode() {
        return predictionTypeCode;
    }

    public void setPredictionTypeCode(String predictionTypeCode) {
        this.predictionTypeCode = predictionTypeCode;
    }

    public String getPredictionTypeDescription() {
        return predictionTypeDescription;
    }

    public void setPredictionTypeDescription(String predictionTypeDescription) {
        this.predictionTypeDescription = predictionTypeDescription;
    }
}
