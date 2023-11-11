package com.bill_management.admin.utils;

import com.bill_management.admin.auth.LoginVerticle;
import com.bill_management.admin.mainentry.MainRouter;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseVerticle extends AbstractVerticle {
  public static final Logger LOG = LoggerFactory.getLogger(ResponseVerticle.class.getName());

  @Override
  public void start(Promise<Void> startPromise) {
    var eventBus = vertx.eventBus();
    eventBus.<JsonObject>consumer(EventAddress.RESPONSE, message -> {
      LOG.debug("Receiving body {} ", message.body());
      JsonObject body = message.body().copy();
      
      RoutingContext ctx = MainRouter.routingMap.get(body.getString("requestId"));
      ctx.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(body.getInteger("statusCode"))
        .end(
          body.getJsonObject("data").toString()
        );

    });
    startPromise.complete();
  }
}

