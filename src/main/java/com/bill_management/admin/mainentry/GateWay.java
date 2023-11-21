package com.bill_management.admin.mainentry;

import com.bill_management.admin.auth.LoginVerticle;
import com.bill_management.admin.utils.BaseVerticle;
import com.bill_management.admin.utils.Database;
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
    deployVerticle(GateWay.class.getName(), 1, false, vertx);
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
          Database database = Database.getInstance("jdbc:oracle:thin:@localhost:1521:orcl", "sys as SYSDBA", "8101419");
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
