package com.bill_management.utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bill_management.utils.Constants.EventAddress.RESPONSE;
import static com.bill_management.utils.Constants.Keys.*;

public abstract class GeneralVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    var eventBus = vertx.eventBus();
    eventBus.<JsonObject>consumer(getRequestAddress(), message -> {
      getLogger().debug("Receiving body {} ", message.body());

      JsonObject body = message.body().copy();
      JsonObject data = body.getJsonObject("data");
      String requestId = body.getString("requestId");
      vertx.executeBlocking(context -> {
        process(data, requestId);
      });
    });
    startPromise.complete();
  }

  public void returnResponse(JsonObject data, String requestId, int statusCode) {

    vertx.eventBus().send(RESPONSE, new JsonObject()
      .put(DATA, data)
      .put(STATUS_CODE, statusCode)
      .put(REQUEST_ID, requestId)
    );
  }

  protected abstract void process(JsonObject body, String requestId);

  protected abstract String getRequestAddress();

  protected abstract Logger getLogger();
}
