package com.bill_management.utils;

import com.bill_management.mainentry.MainRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bill_management.utils.Constants.EventAddress.RESPONSE;
import static com.bill_management.utils.Constants.Keys.*;

public class ResponseVerticle extends AbstractVerticle {
  public static final Logger LOG = LoggerFactory.getLogger(ResponseVerticle.class.getName());

  @Override
  public void start(Promise<Void> startPromise) {
    var eventBus = vertx.eventBus();
    eventBus.<JsonObject>consumer(RESPONSE, message -> {
      LOG.debug("Receiving body {} ", message.body());
      JsonObject body = message.body().copy();

      RoutingContext ctx = MainRouter.routingMap.get(body.getString(REQUEST_ID));
      ctx.response()
        .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
        .setStatusCode(body.getInteger(STATUS_CODE))
        .end(
          body.getJsonObject(DATA).toString()
        );

    });
    startPromise.complete();
  }
}

