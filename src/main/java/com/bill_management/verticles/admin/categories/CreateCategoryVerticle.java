package com.bill_management.verticles.admin.categories;

import com.bill_management.models.Category;
import com.bill_management.repositories.BaseRepository;
import com.bill_management.repositories.CategoryRepository;
import com.bill_management.utils.EventAddress;
import com.bill_management.utils.GeneralVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCategoryVerticle extends GeneralVerticle {
  public static final Logger LOG = LoggerFactory.getLogger(CreateCategoryVerticle.class.getName());

  @Override
  public void start(Promise<Void> startPromise) {
    var eventBus = vertx.eventBus();
    eventBus.<JsonObject>consumer(EventAddress.CREATE_CATEGORY_ADDRESS, message -> {
      LOG.debug("Receiving body {} ", message.body());
      JsonObject body = message.body().copy();
      JsonObject data = body.getJsonObject("data");
      String requestId = body.getString("requestId");

      process(data, requestId);
    });
    startPromise.complete();
  }

  @Override
  public void process(JsonObject body, String requestId) {
    String nameEn = body.getString("name_en");
    String nameAr = body.getString("name_ar");
    String descEn = body.getString("desc_en");
    String descAr = body.getString("desc_ar");
    Category category = new Category(nameEn, nameAr, descEn, descAr);

    JsonObject result = new CategoryRepository().createCategory(category);
    JsonObject response = new JsonObject();

    response.put("message", result.getString("message"))
      .put("category", result.getJsonObject("category"));

    if (result.getInteger("status_code") == 200) {
      returnResponse(response, requestId, HttpResponseStatus.OK.code());
    } else {
      returnResponse(response, requestId, HttpResponseStatus.BAD_REQUEST.code());
    }
  }
}
