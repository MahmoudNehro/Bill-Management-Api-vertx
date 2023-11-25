package com.bill_management.repositories;

import com.bill_management.models.User;
import io.vertx.core.json.JsonObject;

import java.sql.*;

public class AuthenticationRepository implements BaseRepository<User> {

  public JsonObject login(User model)  {
    String sqlStmt = buildCall("login", 3);
    return handle(model, sqlStmt,"returnLoginData", 2, 3);
  }


  @Override
  public void bindParams(CallableStatement callableStatement, User user) throws SQLException {
    callableStatement.setString(1, user.email());
    callableStatement.registerOutParameter(2, Types.INTEGER);
    callableStatement.registerOutParameter(3, Types.REF_CURSOR);
  }

  public void returnLoginData(CallableStatement callableStatement, User user, JsonObject result) throws SQLException {
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
    User loggedInUser = new User(id, name, email, phone);
    result.put("user", loggedInUser.toJson());
    result.put("password", password);
  }
}
