package com.kbtg.khoroscope.dao;

import com.kbtg.khoroscope.model.Prediction;
import com.kbtg.khoroscope.model.PredictionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PredictionDAOImpl implements PredictionDAO{


    private DataSource dataSource;

//    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//
//    private JdbcTemplate jdbcTemplate;
//
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Collection<Prediction> findPredictionByParameter(String predictionTypeCode,String predictionCode) throws SQLException {

        System.out.println("datasource ==> "+dataSource);

        List<Prediction> predictionList = null;

        String query = "select predict.id as predictionId" +
                ", predict.prediction_code as predictionCode" +
                ", predict.prediction_title_1 as predictionTitle1 " +
                ", predict.prediction_title_2 as predictionTitle2 " +
                ", predict.prediction_title_3 as predictionTitle3 " +
                ", predict.prediction_title_4 as predictionTitle4 " +
                ", predict.prediction_title_5 as predictionTitle5 " +

                ", predictType.id as predictionTypeId" +
                ", predictType.prediction_type_code as predictionTypeCode" +
                ", predictType.prediction_type_description as predictionTypeDescription" +

                " from horoscope.prediction predict " +
                " join horoscope.prediction_type predictType on predict.prediction_type_fk = predictType.id" +
                " where predictType.prediction_type_code = ? ";

        System.out.println("predictionTypeCode ==> "+predictionTypeCode);
        System.out.println("predictionCode ==> "+predictionCode);
        if(null != predictionCode){
            query += " and predict.prediction_code = ? ";
        }
        System.out.println("query ==> "+query);
        try (Connection conn = dataSource.getConnection();) {

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            int i =1;
            preparedStatement.setString(i++, predictionTypeCode);

            if(null != predictionCode){
                preparedStatement.setString(i++, predictionCode);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            predictionList = new ArrayList<Prediction>();
            Prediction obj = null;
            PredictionType objType = null;
            while (resultSet.next()) {

                System.out.println("predictionId : " + resultSet.getInt("predictionId"));

                obj = new Prediction();
                obj.setPredictionId( resultSet.getInt("predictionId"));
                obj.setPredictionCode(resultSet.getString("predictionCode"));
                obj.setPredictionTitle1(resultSet.getString("predictionTitle1"));
                obj.setPredictionTitle2(resultSet.getString("predictionTitle2"));
                obj.setPredictionTitle3(resultSet.getString("predictionTitle3"));
                obj.setPredictionTitle4(resultSet.getString("predictionTitle4"));
                obj.setPredictionTitle5(resultSet.getString("predictionTitle5"));

                objType = new PredictionType();
                objType.setPredictionTypeId(resultSet.getInt("predictionTypeId"));
                objType.setPredictionTypeCode(resultSet.getString("predictionTypeCode"));
                objType.setPredictionTypeDescription(resultSet.getString("predictionTypeDescription"));

                predictionList.add(obj);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            System.out.println("Final");
        }
        return predictionList;
    }
//
//        return jdbcTemplate.execute(query,new PreparedStatementCallback<List<Prediction>>(){
//            @Override
//            public List<Prediction> doInPreparedStatement(PreparedStatement ps)
//                    throws SQLException, DataAccessException {
//
//                ps.setString(1,predictionType);
//
//                return ps.execute();
//
//            }
//        });

//        return jdbcTemplate.query(query,new ResultSetExtractor<List<Prediction>>(){
//            @Override
//            public List<Prediction> extractData(ResultSet rs) throws SQLException,
//                    DataAccessException {
//
//                List<Prediction> list=new ArrayList<Prediction>();
//                while(rs.next()){
//                    Prediction prediction=new Prediction();
//                    prediction.setPredictionId((rs.getInt("predictionId")));
//                    prediction.s(rs.getString(2));
//                    e.setSalary(rs.getInt(3));
//                    list.add(e);
//                }
//                return list;
//            }
//        });
//    }



//    String query = "select predict.id as predictionId" +
//                ", predict.prediction_type_fk as predictionType"+
//                ", predict.prediction_code as predictionCode" +
//                ", predict.prediction_title_1 as predictionTitle1 "+
//                ", predict.prediction_title_2 as predictionTitle2 "+
//                ", predict.prediction_title_3 as predictionTitle3 "+
//                ", predict.prediction_title_4 as predictionTitle4 "+
//                ", predict.prediction_title_5 as predictionTitle5 "+
//                " from horoscope.prediction predict " +
//                " join horoscope.prediction_type predictType on predict.prediction_id = predictType.prediction_type_id"
//                        "where predict.";
//
//        List<Prediction> orderList = jdbcTemplate.query(
//                query,
//                ParameterizedBeanPropertyRowMapper.newInstance(Prediction.class));
//
//        return orderList;
//
//    }

}
