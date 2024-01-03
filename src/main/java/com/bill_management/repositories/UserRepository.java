package com.bill_management.repositories;

import com.bill_management.models.User;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class UserRepository implements BaseRepository<User> {
  public JsonObject createUser(User user) {
    String sqlStmt = buildCall("create_user", 7);
    return handle(user, sqlStmt, "returnCreateData", this::bindCreateUserParams, 5, 6, 7);
  }

  public JsonObject updateUser(User user) {
    String sqlStmt = buildCall("update_user", 7);
    return handle(user, sqlStmt, null, this::bindUpdateUserParams, 6, 7);
  }

  public JsonObject deleteUser(User user) {
    String sqlStmt = buildCall("delete_user", 3);
    return handle(user, sqlStmt, null, this::bindDeleteUserParams, 2, 3);
  }

  public JsonObject getUser(User user) {
    String sqlStmt = buildCall("get_user", 4);
    return handle(user, sqlStmt, "returnGetData", this::bindGetUserParams, 2, 3, 4);
  }
  public JsonObject listUsers(User user) {
    String sqlStmt = buildCall("list_users", 3);
    return handle(user, sqlStmt, "returnListData", this::bindListUserParams, 2, 3);
  }

  public void bindCreateUserParams(CallableStatement callableStatement, User user) throws SQLException {
    callableStatement.setString(1, user.name());
    callableStatement.setString(2, user.email());
    callableStatement.setString(3, user.phone());
    callableStatement.setString(4, user.password());
    callableStatement.registerOutParameter(5, Types.INTEGER);
    callableStatement.registerOutParameter(6, Types.VARCHAR);
    callableStatement.registerOutParameter(7, Types.INTEGER);
  }

  public void bindUpdateUserParams(CallableStatement callableStatement, User user) throws SQLException {
    callableStatement.setInt(1, user.id());
    callableStatement.setString(2, user.name());
    callableStatement.setString(3, user.email());
    callableStatement.setString(4, user.phone());
    callableStatement.setString(5, user.password());
    callableStatement.registerOutParameter(6, Types.INTEGER);
    callableStatement.registerOutParameter(7, Types.VARCHAR);
  }

  public void bindDeleteUserParams(CallableStatement callableStatement, User user) throws SQLException {
    callableStatement.setInt(1, user.id());
    callableStatement.registerOutParameter(2, Types.INTEGER);
    callableStatement.registerOutParameter(3, Types.VARCHAR);
  }

  public void bindGetUserParams(CallableStatement callableStatement, User user) throws SQLException {
    callableStatement.setInt(1, user.id());
    callableStatement.registerOutParameter(2, Types.INTEGER);
    callableStatement.registerOutParameter(3, Types.VARCHAR);
    callableStatement.registerOutParameter(4, Types.REF_CURSOR);
  }

  public void bindListUserParams(CallableStatement callableStatement, User user) throws SQLException {
    callableStatement.setInt(1, user.id());
    callableStatement.registerOutParameter(2, Types.INTEGER);
    callableStatement.registerOutParameter(3, Types.VARCHAR);
    callableStatement.registerOutParameter(4, Types.REF_CURSOR);
  }


  public void returnCreateData(CallableStatement callableStatement, User user, JsonObject result) throws SQLException {
    Integer id = callableStatement.getInt(7);
    if (result.getInteger("status_code") == 200) {
      result.put("user", user.toJson().put("id", id));
    } else {
      result.put("user", null);
    }
  }
  public void returnGetData(CallableStatement callableStatement, User user, JsonObject result) throws SQLException {
    ResultSet resultSet = (ResultSet) callableStatement.getObject(4);
    Integer id = null;
    String name = null;
    String email = null;
    String phone = null;
    if (resultSet != null) {
      while (resultSet.next()) {
        id = resultSet.getInt("id");
        name = resultSet.getString("name");
        email = resultSet.getString("email");
        phone = resultSet.getString("phone");
      }
      User user1 = new User(id, name, email, phone);
      result.put("user", user1.toJson());
    } else {
      result.put("user", null);
    }
  }
  public void returnListData(CallableStatement callableStatement, User user, JsonObject result) throws SQLException {
    ResultSet resultSet = (ResultSet) callableStatement.getObject(3);
    Integer id = null;
    String name = null;
    String email = null;
    String phone = null;
    JsonArray users = new JsonArray();
    if (resultSet != null) {

      while (resultSet.next()) {
        id = resultSet.getInt("id");
        name = resultSet.getString("name");
        email = resultSet.getString("email");
        phone = resultSet.getString("phone");
        User user1 = new User(id, name, email, phone);
        users.add(user1.toJson());
      }
      result.put("users", users);
    } else {
      result.put("users", null);
    }
  }
}
