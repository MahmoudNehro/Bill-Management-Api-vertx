package com.bill_management.mainentry;

import com.bill_management.verticles.admin.auth.LoginVerticle;
import com.bill_management.verticles.admin.categories.CreateCategoryVerticle;
import com.bill_management.utils.BaseVerticle;
import com.bill_management.utils.Database;
import com.bill_management.utils.ResponseVerticle;
import com.bill_management.verticles.admin.categories.DeleteCategoryVerticle;
import com.bill_management.verticles.admin.categories.UpdateCategoryVerticle;
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
    deployVerticle(GateWay.class.getName(), 1, false, vertx);
    deployVerticle(ResponseVerticle.class.getName(), 1, false, vertx);

    deployVerticle(LoginVerticle.class.getName(), 1, false, vertx);
    deployVerticle(CreateCategoryVerticle.class.getName(), 1, false, vertx);
    deployVerticle(UpdateCategoryVerticle.class.getName(), 1, false, vertx);
    deployVerticle(DeleteCategoryVerticle.class.getName(), 1, false, vertx);
  }

  @Override
  public void start(Promise<Void> startPromise) {

    MainRouter mainRouter = new MainRouter();
    Router router = mainRouter.makeRoutes(vertx);


    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port, httpServerAsyncResult -> {
        if (httpServerAsyncResult.succeeded()) {
          Database.makeConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as SYSDBA", "8101419");
          if (Database.getConnection() != null) {
            startPromise.complete();
            LOG.debug("Server started on port {}", port);
            LOG.debug("Success connecting to database");
          } else {
            startPromise.fail("Error connecting to database");
          }

        } else {
          startPromise.fail(httpServerAsyncResult.cause());
          LOG.debug("Server failed because {}", httpServerAsyncResult);
        }
      });
  }
}
