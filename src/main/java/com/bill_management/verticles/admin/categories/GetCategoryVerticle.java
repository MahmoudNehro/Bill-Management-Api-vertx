package com.bill_management.verticles.admin.categories;

import com.bill_management.models.Category;
import com.bill_management.repositories.CategoryRepository;
import com.bill_management.utils.GeneralVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bill_management.utils.Constants.EventAddress.GET_CATEGORY_ADDRESS;
import static com.bill_management.utils.Constants.Keys.*;

public class GetCategoryVerticle extends GeneralVerticle {

  @Override
  public void process(JsonObject body, String requestId) {
    int id = Integer.parseInt(body.getJsonObject(PARAMS).getString("categoryID"));

    Category category = new Category(id);
    JsonObject result = new CategoryRepository().getCategory(category);
    JsonObject response = new JsonObject();

    response.put(MESSAGE, result.getString(MESSAGE))
      .put("category", result.getJsonObject("category"));


    if (result.getInteger(STATUS_CODE) == HttpResponseStatus.OK.code()) {
      returnResponse(response, requestId, HttpResponseStatus.OK.code());
    } else {
      returnResponse(response, requestId, HttpResponseStatus.BAD_REQUEST.code());
    }

  }

  @Override
  protected String getRequestAddress() {
    return GET_CATEGORY_ADDRESS;
  }

  @Override
  protected Logger getLogger() {
    return LoggerFactory.getLogger(GetCategoryVerticle.class.getName());
  }
}
