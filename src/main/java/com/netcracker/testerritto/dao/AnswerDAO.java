package com.netcracker.testerritto.dao;

import com.netcracker.testerritto.mappers.AnswerRowMapper;
import com.netcracker.testerritto.models.Answer;
import com.netcracker.testerritto.properties.AttrtypeProperties;
import com.netcracker.testerritto.properties.ListsAttr;
import com.netcracker.testerritto.properties.ObjtypeProperties;
import java.math.BigInteger;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class AnswerDAO {
  @Autowired
  public JdbcTemplate jdbcTemplate;
  @Autowired
  private AnswerRowMapper answerRowMapper;

  private String QUERY_SELECT_BY_ID =
      "select\n" +
          "answer.object_id id,\n" +
          "answer.parent_id question_id,\n" +
          "answer_text.list_value_id text,\n" +
          "answer_score.value score\n" +
          "from\n" +
          "objects answer,\n" +
          "attributes answer_text,\n" +
          "attributes answer_score\n" +
          "where\n" +
          "answer.object_id = ?\n" +
          "and answer.object_type_id = 11 \n" +
          "and answer_text.attr_id = 20\n" +
          "and answer_text.object_id = answer.object_id\n" +
          "and answer_score.attr_id = 21\n" +
          "and answer_score.object_id = answer.object_id";

  private String QUERY_SELECT_ALL_ANSWERS_IN_QUESTION =
      "select" +
          "answer.object_id id,"+
          "answer.parent_id question_id," +
          "answer_text.list_value_id text," +
          "answer_score.value score," +
          "answer2reply.reference reply_id, " +
          "answer2remark.reference remark_id," +
          "next_question. reference next_question" +
    "from " +
          "objects answer," +
          "attributes answer_text," +
          "attributes answer_score," +
          "objreference answer2reply," +
          "objreference answer2remark," +
          "objreference next_question" +
    "where" +
          "answer.parent_id = ?" +
          "and answer.object_type_id = 11" +
          "and answer_text.attr_id = 20"  +
          "and answer_text.object_id = answer.object_id" +
          "and answer_score.attr_id = 21" +
          "and answer_score.object_id = answer.object_id" +
          "and answer2reply.object_id = answer.object_id" +
          "and answer2reply.attr_id = 32" +
          "and answer2remark.object_id = answer.object_id" +
          "and answer2remark.attr_id = 28"  +
          "and next_question.object_id = answer.object_id" +
          "and next_question.attr_id = 37" ;

  private String QUERY_SELECT_ANSWERS_FOR_REPLY =
      "select " +
          "answer.object_id id," +
          "answer.parent_id question_id," +
          "answer_text.list_value_id text," +
          "answer_score.value score," +
          "answer2reply.reference" +
    "from" +
          "objects answer," +
          "attributes answer_text," +
          "attributes answer_score," +
          "objreference answer2reply" +
    "where " +
          "answer2reply.reference = ?" +
          "and answer2reply.attr_id = 32" +
          "and answer2reply.object_id = answer.object_id" +
          "and answer.object_type_id = 11" +
          "and answer_text.attr_id = 20" +
          "and answer_text.object_id = answer.object_id" +
          "and answer_score.attr_id = 21" +
          "and answer_score.object_id = answer.object_id";

  public AnswerDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Answer getAnswerById(BigInteger answerId){
    return jdbcTemplate.queryForObject(QUERY_SELECT_BY_ID, new Object[]{answerId.toString()}, answerRowMapper);
  }

  public List<Answer> getAllAnswerInQuestion(BigInteger questionId){
    return jdbcTemplate.query(QUERY_SELECT_ALL_ANSWERS_IN_QUESTION, new Object[]{questionId.toString()}, answerRowMapper);
  }

  public Answer getAnswerForReply(BigInteger questionId) {
    return jdbcTemplate.queryForObject(QUERY_SELECT_ANSWERS_FOR_REPLY, new Object[]{questionId.toString()}, answerRowMapper);
  }

  public Answer updateAnswer(Answer answer){
    new ObjectEavBuilder.Builder(jdbcTemplate)
        .setObjectId(answer.getId())
        .setListAttribute(AttrtypeProperties.TEXT_ANSWER, answer.getTextAnswer().getId())
        .setStringAttribute(AttrtypeProperties.SCORE_ANSWER, String.valueOf(answer.getScore()))
        .update();
    return getAnswerById(answer.getId());
  }

  public BigInteger createAnswer(Answer newAnswer){
    return new ObjectEavBuilder.Builder(jdbcTemplate)
        .setName("Answer " + newAnswer.getId())
        .setObjectTypeId(ObjtypeProperties.ANSWER)
        .setParentId(newAnswer.getQuestionId())
        .setListAttribute(AttrtypeProperties.TEXT_ANSWER, newAnswer.getTextAnswer().getId())
        .setStringAttribute(new BigInteger(String.valueOf(AttrtypeProperties.SCORE_ANSWER)), String.valueOf(newAnswer.getScore()))
        .create();
  }

  public void deleteAnswer(BigInteger answerId){
    new ObjectEavBuilder.Builder(jdbcTemplate)
        .setObjectId(answerId)
        .delete();
  }

  public void deleteAllAnswerInQuestion(BigInteger questionId){
    new ObjectEavBuilder.Builder(jdbcTemplate)
        .setParentId(questionId)
        .setObjectTypeId(new BigInteger(String.valueOf(ObjtypeProperties.ANSWER)))
        .delete();
  }
}
