package com.bill_management.admin.mainentry;

import com.bill_management.admin.auth.LoginVerticle;
import com.bill_management.admin.utils.BaseVerticle;
import com.bill_management.admin.utils.ResponseVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GateWay extends BaseVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(GateWay.class);
  private static final int port = 8080;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    deployVerticle(GateWay.class.getName(), 1, false ,vertx);
    deployVerticle(LoginVerticle.class.getName(), 1, false, vertx);
    deployVerticle(ResponseVerticle.class.getName(), 1, false, vertx);
  }

  @Override
  public void start(Promise<Void> startPromise) {

    MainRouter mainRouter = new MainRouter();
    Router router = mainRouter.makeRoutes(vertx);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port, httpServerAsyncResult -> {
        if (httpServerAsyncResult.succeeded()) {
          startPromise.complete();
          LOG.debug("Server started on port {}", port);
        } else {
          startPromise.fail(httpServerAsyncResult.cause());
          LOG.debug("Server failed because {}" , httpServerAsyncResult);
        }
      });
  }
}
