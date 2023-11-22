package com.bill_management.admin.repositories;

import com.bill_management.admin.models.User;
import io.vertx.core.json.JsonObject;
import oracle.jdbc.OracleTypes;

import java.sql.*;

public class AuthenticationRepository implements BaseRepository<User> {

  public JsonObject login(User data) throws SQLException {
    String sqlStmt = buildCall("login", 3);

    CallableStatement callableStatement = connection.prepareCall(sqlStmt);
    callableStatement.setString(1, data.email());
    callableStatement.registerOutParameter(2, Types.INTEGER);
    callableStatement.registerOutParameter(3,  Types.REF_CURSOR);
    callableStatement.execute();

    int statusCode = callableStatement.getInt(2);
    JsonObject result = new JsonObject();
    result.put("status_code", statusCode);
    if (statusCode == 200) {
      ResultSet resultSet = (ResultSet) callableStatement.getObject(3);
      Integer id = null;
      String name = null;
      String email = null;
      String phone = null;
      String password = null;
      while (resultSet.next()) {
        id = resultSet.getInt("id");
        name = resultSet.getString("name");
        phone = resultSet.getString("phone");
        email = resultSet.getString("email");
        password = resultSet.getString("password");
      }
      User loggedInUser = new User(id,name,email,phone);
      result.put("user", loggedInUser.toJson());
      result.put("password", password);
    } else {
      result.put("user", null);
      result.put("password", null);
    }
    return result;
  }

  @Override
  public JsonObject executeQuery(User data) throws SQLException {
    return login(data);
  }
}
