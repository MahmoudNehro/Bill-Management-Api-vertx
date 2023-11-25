package com.bill_management.repositories;

import com.bill_management.models.Category;
import io.vertx.core.json.JsonObject;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CategoryRepository implements BaseRepository<Category> {

  public JsonObject createCategory(Category category) {
    String sqlStmt = buildCall("create_category", 7);
    return handle(category, sqlStmt, "returnCreateData", 5, 6, 7);
  }

  @Override
  public void bindParams(CallableStatement callableStatement, Category category) throws SQLException {
    callableStatement.setString(1, category.nameEn());
    callableStatement.setString(2, category.nameAr());
    callableStatement.setString(3, category.descEn());
    callableStatement.setString(4, category.descAr());
    callableStatement.registerOutParameter(5, Types.INTEGER);
    callableStatement.registerOutParameter(6, Types.VARCHAR);
    callableStatement.registerOutParameter(7, Types.INTEGER);
  }

  public void returnCreateData(CallableStatement callableStatement, Category category, JsonObject result) throws SQLException {
    Integer id = callableStatement.getInt(7);
    if (result.getInteger("status_code") == 200) {
      result.put("category", category.toJson().put("id", id));
    } else {
      result.put("category", null);
    }
  }

}
