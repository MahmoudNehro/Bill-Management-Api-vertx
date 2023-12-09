package com.bill_management.verticles.admin.categories;

import com.bill_management.repositories.CategoryRepository;
import com.bill_management.utils.GeneralVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bill_management.utils.EventAddress.LIST_CATEGORY_ADDRESS;

public class ListCategoryVerticle extends GeneralVerticle {
  public static final Logger LOG = LoggerFactory.getLogger(ListCategoryVerticle.class.getName());
  @Override
  public void start(Promise<Void> startPromise)  {
    var eventBus = vertx.eventBus();
    eventBus.<JsonObject>consumer(LIST_CATEGORY_ADDRESS, message -> {
      LOG.info("Receiving body {}" , message.body());
      JsonObject body = message.body().copy();
      JsonObject data = body.getJsonObject("data");
      String requestId = body.getString("requestId");
      process(data,requestId);
    });
    startPromise.complete();
  }

  @Override
  public void process(JsonObject body, String requestId) {
    JsonObject result = new CategoryRepository().listCategories();
    JsonObject response = new JsonObject();

    response.put("message", result.getString("message"))
      .put("categories", result.getJsonArray("categories"));

    if (result.getInteger("status_code") == 200) {
      returnResponse(response, requestId, HttpResponseStatus.OK.code());
    } else {
      returnResponse(response, requestId, HttpResponseStatus.BAD_REQUEST.code());
    }
  }
}
