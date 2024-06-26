package com.bill_management.repositories;

import com.bill_management.models.Model;
import com.bill_management.utils.BindingDynamicProcedureParams;
import com.bill_management.utils.Database;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import static com.bill_management.utils.Constants.Keys.MESSAGE;
import static com.bill_management.utils.Constants.Keys.STATUS_CODE;

public interface BaseRepository<T extends Model> {
  Connection connection = Database.getConnection();
  Logger LOG = LoggerFactory.getLogger(BaseRepository.class.getName());


  default JsonObject handle(T model, String sqlStmt, String returnDataMethod, BindingDynamicProcedureParams<T> bindingDynamicProcedureParams, int... outParams) {
    JsonObject result = new JsonObject();

    if (connection != null) {
      try {
        CallableStatement callableStatement = connection.prepareCall(sqlStmt);
        bindingDynamicProcedureParams.bind(callableStatement, model);
        callableStatement.execute();
        int statusCode = callableStatement.getInt(outParams[0]);
        String resultMsg = callableStatement.getString(outParams[1]);
        result.put(STATUS_CODE, statusCode)
          .put(MESSAGE, resultMsg);
        if (returnDataMethod != null) {
          Method method = this.getClass().getMethod(returnDataMethod, CallableStatement.class, model.getClass(), JsonObject.class);
          method.invoke(this, callableStatement, model, result);
        }
      } catch (SQLException e) {
        LOG.error("Error when executing procedure {}", e.getMessage());
        result.put("message", "Something went wrong").put(STATUS_CODE, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
        LOG.error("Error when invoking method {}", e.getMessage());
        e.printStackTrace();
        result.put(MESSAGE, "Something went wrong").put(STATUS_CODE, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      }
    }
    return result;
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

}
