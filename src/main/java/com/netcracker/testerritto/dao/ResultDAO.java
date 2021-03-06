package com.netcracker.testerritto.dao;


import com.netcracker.testerritto.mappers.ResultRowMapper;
import com.netcracker.testerritto.models.Question;
import com.netcracker.testerritto.models.Reply;
import com.netcracker.testerritto.models.Result;
import com.netcracker.testerritto.properties.AttrtypeProperties;
import com.netcracker.testerritto.properties.ObjtypeProperties;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ResultDAO {

    private static final String GET_RESULT =
              "select  results.object_id id, result2test.reference test_id,  "+
              "    result_date.date_value result_date, result_score.value result_score ,  "+
              "    result_status.list_value_id result_status, result2user.reference user_id, "+
              "    result2category.reference category_id "+
              "from  objects results,  "+
              "    attributes result_date,  "+
              "    attributes result_score,  "+
              "    attributes result_status,  "+
              "    objreference result2test,  "+
              "    objreference result2user, "+
              "    objreference result2category "+
              "where results.object_id = ?  "+
              "    and result2test.attr_id = 30 /*results_belongs*/  "+
              "    and result2test.object_id = results.object_id  "+
              "    and result_date.attr_id = 10 /*date*/  "+
              "    and result_date.object_id = results.object_id  "+
              "    and result_score.attr_id = 11 /*score*/  "+
              "    and result_score.object_id = results.object_id  "+
              "    and result_status.attr_id = 12 /*status*/  "+
              "    and result_status.object_id = results.object_id  "+
              "    and result2user.attr_id = 29 /*look by*/  "+
              "    and result2user.object_id = results.object_id "+
              "    and result2category.attr_id = 38 /*result_category*/ "+
              "    and result2category.object_id = results.object_id";

    private static final String GET_RESULTS_BY_USER =
        "select  results.object_id id, result2test.reference test_id,  " +
            "    result_date.date_value result_date, result_score.value result_score ,  " +
            "    result_status.list_value_id result_status, result2user.reference user_id,"+
            "result2category.reference category_id  " +
            "from  objects results,  " +
            "    attributes result_date,  " +
            "    attributes result_score,  " +
            "    attributes result_status,  " +
            "    objreference result2test,  " +
            "    objreference result2user,"+
            "    objreference result2category  " +
            "where result2user.reference = ? " +
            "    and result2test.attr_id = 30 /*results_belongs*/  " +
            "    and result2test.object_id = results.object_id  " +
            "    and result_date.attr_id = 10 /*date*/  " +
            "    and result_date.object_id = results.object_id  " +
            "    and result_score.attr_id = 11 /*score*/  " +
            "    and result_score.object_id = results.object_id  " +
            "    and result_status.attr_id = 12 /*status*/  " +
            "    and result_status.object_id = results.object_id  " +
            "    and result2user.attr_id = 29 /*look by*/  " +
            "    and result2user.object_id = results.object_id"+
            "    and result2category.attr_id = 38 /*result_category*/ "+
            "    and result2category.object_id = results.object_id";

    private static final String GET_RESULTS_BY_TEST =
            "select  results.object_id id, result2test.reference test_id,   "+
             "   result_date.date_value result_date, result_score.value result_score ,   "+
             "   result_status.list_value_id result_status, result2user.reference user_id,   "+
             "   result2category.reference category_id  " +
             "from  objects results,   "+
             "   attributes result_date, "+
             "   attributes result_score, "+
             "   attributes result_status, "+
             "   objreference result2test, "+
             "   objreference result2user,  "+
             "   objreference result2category " +
             "where result2test.reference = ? "+
             "   and result2test.attr_id = 30 /*results_belongs*/   "+
             "   and result2test.object_id = results.object_id   "+
             "   and result_date.attr_id = 10 /*date*/   "+
             "   and result_date.object_id = results.object_id   "+
             "   and result_score.attr_id = 11 /*score*/   "+
             "   and result_score.object_id = results.object_id   "+
             "   and result_status.attr_id = 12 /*status*/   "+
             "   and result_status.object_id = results.object_id   "+
             "   and result2user.attr_id = 29 /*look by*/   "+
             "   and result2user.object_id = results.object_id"+
             "   and result2category.attr_id = 38 /*result_category*/ "+
             "   and result2category.object_id = results.object_id";

    private static final String GET_REPLIES =
        "select  results.object_id res_id, reply.object_id rep_id, questions.object_id ques_id  " +
            "from objects results, " +
            "    objreference answer2reply, " +
            "    objects reply, " +
            "    objreference replys2results, " +
            "    objects questions, " +
            "    objects tests, " +
            "    objects answer " +
            "where replys2results.attr_id = 31 /*reply_belongs*/ " +
            "    and replys2results.object_id = reply.object_id " +
            "    and replys2results.reference = results.object_id " +
            "    and answer2reply.attr_id = 32 /*answer_belongs*/ " +
            "    and answer2reply.reference = reply.object_id " +
            "    and answer2reply.object_id = answer.object_id " +
            "    and questions.object_id = answer.parent_id " +
            "    and tests.object_id = questions.parent_id " +
            "    and results.object_id = ?";


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ReplyDAO replyDAO;
    @Autowired
    private QuestionDAO questionDAO;


    public Result getResult(BigInteger resultId) {

        Result result = jdbcTemplate.queryForObject(GET_RESULT, new Object[]{resultId.toString()}, new ResultRowMapper());

        result.setReplies(getReplies(resultId));

        return result;
    }

    private Map<Question, Reply> getReplies(BigInteger resultId) {
        Map<BigInteger, BigInteger> resultHashMapId = new HashMap<>();
        Map<Question, Reply> resultHashMap = new HashMap<>();

        jdbcTemplate.query(GET_REPLIES, new Object[]{resultId.toString()},
            new RowMapper<Map<BigInteger, BigInteger>>() {
                @Override
                public Map<BigInteger, BigInteger> mapRow(ResultSet resultSet, int i) throws SQLException {
                    resultHashMapId.put(
                        BigInteger.valueOf(resultSet.getInt("ques_id")),
                        BigInteger.valueOf(resultSet.getInt("rep_id")));
                    return resultHashMapId;
                }
            }
        );

        for (Map.Entry<BigInteger, BigInteger> entry : resultHashMapId.entrySet()) {
            resultHashMap.put(
                questionDAO.getQuestionById(entry.getKey()),
                replyDAO.getReply(entry.getValue())
            );
        }
        return resultHashMap;

    }

    public List<Result> getResultsByUser(BigInteger userId) {
        List<Result> results = jdbcTemplate.query(GET_RESULTS_BY_USER, new Object[]{userId.toString()}, new ResultRowMapper());
        results.forEach(result -> result.setReplies(getReplies(result.getId())));
        return results;
    }

    public List<Result> getResultsByTest(BigInteger userId) {
        List<Result> results = jdbcTemplate.query(GET_RESULTS_BY_TEST, new Object[]{userId.toString()}, new ResultRowMapper());
        results.forEach(result -> result.setReplies(getReplies(result.getId())));
        return results;
    }



    public void deleteResult(BigInteger resultId) {
        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(resultId)
            .delete();
    }

    public BigInteger createResult(Result result) {

        return new ObjectEavBuilder.Builder(jdbcTemplate)
            .setName("Result " + result.getId())
            .setObjectTypeId(ObjtypeProperties.RESULT)
            .setDateAttribute(AttrtypeProperties.DATE, result.getDate())
            .setStringAttribute(AttrtypeProperties.SCORE_RESULT, String.valueOf(result.getScore()))
            .setListAttribute(AttrtypeProperties.STATUS, result.getStatus().getId())
            .setReference(AttrtypeProperties.LOOK_BY, result.getUserId())
            .setReference(AttrtypeProperties.RESULT_BELONGS, result.getTestId())
            .setReference(AttrtypeProperties.RESULT_CATEGORY,result.getCategoryId())
            .create();
    }

    public Result updateResult(Result result) {

        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(result.getId())
            .setObjectTypeId(ObjtypeProperties.RESULT)
            .setDateAttribute(AttrtypeProperties.DATE, result.getDate())
            .setStringAttribute(AttrtypeProperties.SCORE_RESULT, String.valueOf(result.getScore()))
            .setListAttribute(AttrtypeProperties.STATUS, result.getStatus().getId())
            .setReference(AttrtypeProperties.LOOK_BY, result.getUserId())
            .setReference(AttrtypeProperties.RESULT_BELONGS, result.getTestId())
            .setReference(AttrtypeProperties.RESULT_CATEGORY, result.getCategoryId())
            .update();

        return getResult(result.getId());

    }


}