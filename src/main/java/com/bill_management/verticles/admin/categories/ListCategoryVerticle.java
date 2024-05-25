package com.bill_management.verticles.admin.categories;

import com.bill_management.repositories.CategoryRepository;
import com.bill_management.utils.GeneralVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bill_management.utils.Constants.EventAddress.LIST_CATEGORY_ADDRESS;
import static com.bill_management.utils.Constants.Keys.MESSAGE;
import static com.bill_management.utils.Constants.Keys.STATUS_CODE;

public class ListCategoryVerticle extends GeneralVerticle {
  @Override
  public void process(JsonObject body, String requestId) {
    JsonObject result = new CategoryRepository().listCategories();
    JsonObject response = new JsonObject();

    response.put(MESSAGE, result.getString(MESSAGE))
      .put("categories", result.getJsonArray("categories"));

    if (result.getInteger(STATUS_CODE) == HttpResponseStatus.OK.code()) {
      returnResponse(response, requestId, HttpResponseStatus.OK.code());
    } else {
      returnResponse(response, requestId, HttpResponseStatus.BAD_REQUEST.code());
    }
  }

  @Override
  protected String getRequestAddress() {
    return LIST_CATEGORY_ADDRESS;
  }

  @Override
  protected Logger getLogger() {
    return LoggerFactory.getLogger(ListCategoryVerticle.class.getName());
  }
}
