package com.bill_management.repositories;

import com.bill_management.models.Category;
import io.vertx.core.json.JsonObject;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CategoryRepository implements BaseRepository<Category> {

  public JsonObject createCategory(Category category) {
    String sqlStmt = buildCall("create_category", 7);
    return handle(category, sqlStmt, "returnCreateData", this::bindCreateParams, 5, 6, 7);
  }

  public JsonObject updateCategory(Category category) {
    String sqlStmt = buildCall("update_cateogry", 7);
    return handle(category, sqlStmt, null, this::bindUpdateParams, 6, 7);
  }

  public JsonObject deleteCategory(Category category) {
    String sqlStmt = buildCall("delete_category", 3);
    return handle(category, sqlStmt, null, this::bindDeleteParams, 2, 3);
  }

  public void bindCreateParams(CallableStatement callableStatement, Category category) throws SQLException {
    callableStatement.setString(1, category.nameEn());
    callableStatement.setString(2, category.nameAr());
    callableStatement.setString(3, category.descEn());
    callableStatement.setString(4, category.descAr());
    callableStatement.registerOutParameter(5, Types.INTEGER);
    callableStatement.registerOutParameter(6, Types.VARCHAR);
    callableStatement.registerOutParameter(7, Types.INTEGER);
  }

  public void bindUpdateParams(CallableStatement callableStatement, Category category) throws SQLException {
    callableStatement.setInt(1, category.id());
    callableStatement.setString(2, category.nameEn());
    callableStatement.setString(3, category.nameAr());
    callableStatement.setString(4, category.descEn());
    callableStatement.setString(5, category.descAr());
    callableStatement.registerOutParameter(6, Types.INTEGER);
    callableStatement.registerOutParameter(7, Types.VARCHAR);
  }

  public void bindDeleteParams(CallableStatement callableStatement, Category category) throws SQLException {
    callableStatement.setInt(1, category.id());
    callableStatement.registerOutParameter(2, Types.INTEGER);
    callableStatement.registerOutParameter(3, Types.VARCHAR);
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
