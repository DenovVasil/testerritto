package com.netcracker.testerritto.dao;

import com.netcracker.testerritto.mappers.QuestionRowMapper;
import com.netcracker.testerritto.models.Question;
import com.netcracker.testerritto.properties.AttrtypeProperties;
import com.netcracker.testerritto.properties.ObjtypeProperties;
import java.math.BigInteger;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class QuestionDAO {
  @Autowired
  public JdbcTemplate jdbcTemplate;
  @Autowired
  private QuestionRowMapper questionRowMapper;

  private String QUERY_FOR_SELECT_BY_ID =
    "select \n" +
      "question.object_id as id,\n" +
      "question_text.value as text,\n" +
      "question_type.list_value_id as question_type,\n" +
      "question.parent_id as test_id\n" +
    "from\n" +
      "objects question,\n" +
      "attributes question_type,\n" +
      "attributes question_text\n" +
    "where\n" +
      "question.object_id = ?\n" +
      "and question.object_type_id = 10\n" +
      "and question.object_id = question_text.object_id\n" +
      "and question_text.attr_id = 18\n" +
      "and question.object_id = question_text.object_id\n" +
      "and question.object_id = question_type.object_id\n" +
      "and question_type.attr_id = 19";

  private String QUERY_FOR_SELECT_ALL_QUESTIONS_IN_TEST =
    "select " +
      "question.object_id as id,\n" +
      "question_text.value as text,\n" +
      "question_type.list_value_id as question_type,\n" +
      "question.parent_id as test_id\n" +
    "from \n" +
      "objects question,\n" +
      "attributes question_type,\n"+
      "attributes question_text\n" +
    "where \n" +
      "question.parent_id = ?\n" +
      "and question.object_type_id = 10\n" +
      "and question.object_id = question_text.object_id \n" +
      "and question_text.attr_id = 18 \n" +
      "and question.object_id = question_type.object_id\n" +
      "and question_type.attr_id = 19 \n";

  public QuestionDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Question getQuestionById(BigInteger id) {
    return jdbcTemplate.queryForObject(QUERY_FOR_SELECT_BY_ID, new Object[]{id.toString()}, questionRowMapper);
  }

  public List<Question> getAllQuestionInTest(BigInteger test_id){
    return jdbcTemplate.query(QUERY_FOR_SELECT_ALL_QUESTIONS_IN_TEST, new Object[]{test_id.toString()}, questionRowMapper);
  }

  public Question updateQuestion(Question question){
    new ObjectEavBuilder.Builder(jdbcTemplate)
        .setObjectId(question.getId())
        .setObjectTypeId(ObjtypeProperties.QUESTION)
        .setStringAttribute(AttrtypeProperties.TEXT_QUESTION, question.getTextQuestion())
        .setListAttribute(AttrtypeProperties.TYPE_QUESTION,question.getTypeQuestion().getId())
        .update();
    return getQuestionById(question.getId());
  }

  public BigInteger createQuestion(Question newQuestion){
    return new ObjectEavBuilder.Builder(jdbcTemplate)
      .setName(newQuestion.getTextQuestion())
      .setObjectTypeId(ObjtypeProperties.QUESTION)
      .setParentId(newQuestion.getTestId())
      .setStringAttribute(AttrtypeProperties.TEXT_QUESTION, newQuestion.getTextQuestion())
      .setListAttribute(AttrtypeProperties.TYPE_QUESTION,newQuestion.getTypeQuestion().getId())
      .create();
  }

  public void deleteQuestionById(BigInteger id){
    new ObjectEavBuilder.Builder(jdbcTemplate)
        .setObjectId(id)
        .delete();
  }
}
