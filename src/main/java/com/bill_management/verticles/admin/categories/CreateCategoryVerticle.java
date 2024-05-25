package com.bill_management.verticles.admin.categories;

import com.bill_management.models.Category;
import com.bill_management.repositories.CategoryRepository;
import com.bill_management.utils.GeneralVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bill_management.utils.Constants.EventAddress.CREATE_CATEGORY_ADDRESS;
import static com.bill_management.utils.Constants.Keys.MESSAGE;
import static com.bill_management.utils.Constants.Keys.STATUS_CODE;

public class CreateCategoryVerticle extends GeneralVerticle {
  @Override
  public void process(JsonObject body, String requestId) {
    String nameEn = body.getString("name_en");
    String nameAr = body.getString("name_ar");
    String descEn = body.getString("desc_en");
    String descAr = body.getString("desc_ar");
    Category category = new Category(nameEn, nameAr, descEn, descAr);

    JsonObject result = new CategoryRepository().createCategory(category);
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
    return CREATE_CATEGORY_ADDRESS;
  }

  @Override
  protected Logger getLogger() {
    return LoggerFactory.getLogger(CreateCategoryVerticle.class.getName());
  }
}
