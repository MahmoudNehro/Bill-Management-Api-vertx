package com.bill_management.repositories;

import com.bill_management.models.Category;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.sql.CallableStatement;
import java.sql.ResultSet;
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

  public JsonObject getCategory(Category category) {
    String sqlStmt = buildCall("get_category", 4);
    return handle(category, sqlStmt, "returnGetData", this::bindGetParams, 2, 3, 4);
  }

  public JsonObject listCategories() {
    String sqlStmt = buildCall("list_categories", 3);
    return handle(new Category(), sqlStmt, "returnListData", this::bindListParams, 1, 2, 3);
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

  public void bindGetParams(CallableStatement callableStatement, Category category) throws SQLException {
    callableStatement.setInt(1, category.id());
    callableStatement.registerOutParameter(2, Types.INTEGER);
    callableStatement.registerOutParameter(3, Types.VARCHAR);
    callableStatement.registerOutParameter(4, Types.REF_CURSOR);

  }

  private void bindListParams(CallableStatement callableStatement, Category category) throws SQLException {
    callableStatement.registerOutParameter(1, Types.INTEGER);
    callableStatement.registerOutParameter(2, Types.VARCHAR);
    callableStatement.registerOutParameter(3, Types.REF_CURSOR);

  }

  public void returnCreateData(CallableStatement callableStatement, Category category, JsonObject result) throws SQLException {
    Integer id = callableStatement.getInt(7);
    if (result.getInteger("status_code") == 200) {
      result.put("category", category.toJson().put("id", id));
    } else {
      result.put("category", null);
    }
  }

  public void returnGetData(CallableStatement callableStatement, Category category, JsonObject result) throws SQLException {
    ResultSet resultSet = (ResultSet) callableStatement.getObject(4);
    Integer id = null;
    String nameEn = null;
    String nameAr = null;
    String descEn = null;
    String descAr = null;
    if (resultSet != null) {

      while (resultSet.next()) {
        id = resultSet.getInt("id");
        nameEn = resultSet.getString("name_en");
        nameAr = resultSet.getString("name_ar");
        descEn = resultSet.getString("desc_en");
        descAr = resultSet.getString("desc_ar");
      }
      Category category1 = new Category(id, nameEn, nameAr, descEn, descAr);
      result.put("category", category1.toJson());
    } else {
      result.put("category", null);
    }
  }

  public void returnListData(CallableStatement callableStatement, Category category, JsonObject result) throws SQLException {
    ResultSet resultSet = (ResultSet) callableStatement.getObject(3);
    Integer id = null;
    String nameEn = null;
    String nameAr = null;
    String descEn = null;
    String descAr = null;
    JsonArray categories = new JsonArray();
    if (resultSet != null) {

      while (resultSet.next()) {
        id = resultSet.getInt("id");
        nameEn = resultSet.getString("name_en");
        nameAr = resultSet.getString("name_ar");
        descEn = resultSet.getString("desc_en");
        descAr = resultSet.getString("desc_ar");
        Category category1 = new Category(id, nameEn, nameAr, descEn, descAr);
        categories.add(category1.toJson());

      }
      result.put("categories", categories);
    } else {
      result.put("categories", null);
    }
  }


}
