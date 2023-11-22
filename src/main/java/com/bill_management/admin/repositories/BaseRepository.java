package com.bill_management.admin.repositories;

import com.bill_management.admin.models.Model;
import com.bill_management.admin.utils.Database;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public interface BaseRepository<T extends Model> {
  Connection connection = Database.getConnection();
  Logger LOG = LoggerFactory.getLogger(BaseRepository.class.getName());


  default JsonObject handle(T data) {
    if (connection != null) {
      try {
        return executeQuery(data);
      } catch (SQLException e) {
        e.printStackTrace();
        LOG.error("Error when executing procedure");
      }
    }
    return new JsonObject().put("message" , "Something went wrong").put("status_code", 500 );
  }

  default String buildCall(String procedureName, int noOfParams) {
    StringBuilder result = new StringBuilder("call " + procedureName + "(");
    for (int i = 0; i < noOfParams; i++) {
      result.append("?");
      if (i == noOfParams - 1) {
        result.append(")");
      } else {
        result.append(",");
      }
    }
    return result.toString();
  }

  JsonObject executeQuery(T data) throws SQLException;


}
