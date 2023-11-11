package com.bill_management.admin.utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public abstract class GeneralVerticle extends AbstractVerticle {
  public abstract void process(JsonObject body, String requestId);

  public void returnResponse(JsonObject data, String requestId, int statusCode) {

    vertx.eventBus().send(EventAddress.RESPONSE, new JsonObject()
      .put("data", data)
      .put("statusCode", statusCode)
      .put("requestId", requestId)
    );
  }
}
