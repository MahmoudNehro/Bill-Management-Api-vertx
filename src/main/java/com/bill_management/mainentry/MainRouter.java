package com.bill_management.mainentry;

import com.bill_management.utils.EventAddress;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class MainRouter {
  private static final Logger LOG = LoggerFactory.getLogger(MainRouter.class.getName());
  private Router router;
  public static final HashMap<String, RoutingContext> routingMap = new HashMap<>();

  public MainRouter() {
    LOG.debug("Object created");
  }

  public Router makeRoutes(Vertx vertx) {
    this.router = Router.router(vertx);
    this.router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

    JWTAuthOptions authConfig = new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
        .setType("jceks")
        .setPath("keystore.jceks")
        .setPassword("secret"));

    JWTAuth authProvider = JWTAuth.create(vertx, authConfig);
    router.route("/protected/*").handler(JWTAuthHandler.create(authProvider));
    router.route().handler(BodyHandler.create());



    this.router.route().failureHandler(handleFailure());
    this.router.post("/login").handler(routingContext -> requestHandler(EventAddress.LOGIN_ADDRESS, vertx, routingContext));
    this.router.post("/protected/categories/create").handler(routingContext -> requestHandler(EventAddress.CREATE_CATEGORY_ADDRESS, vertx, routingContext));
    this.router.put("/protected/categories/:categoryId/update").handler(routingContext -> requestHandler(EventAddress.UPDATE_CATEGORY_ADDRESS, vertx, routingContext));
    this.router.get("/protected/categories/:categoryID").handler(routingContext -> requestHandler(EventAddress.GET_CATEGORY_ADDRESS, vertx, routingContext));
    this.router.get("/protected/categories").handler(routingContext -> requestHandler(EventAddress.LIST_CATEGORY_ADDRESS, vertx, routingContext));
    this.router.delete("/protected/categories/:categoryID").handler(routingContext -> requestHandler(EventAddress.DELETE_CATEGORY_ADDRESS, vertx, routingContext));

    return this.router;
  }

  private void requestHandler(String address, Vertx vertx, RoutingContext routingContext) {
    LOG.info("Sending message to address: " + address);
    if (routingContext.user() != null) {
      LOG.info("User {}", routingContext.user().attributes().getString("sub"));
    }
    String requestId = UUID.randomUUID().toString();
    routingMap.put(requestId, routingContext);

    LOG.info("Params {}", routingContext.pathParams());
    vertx.eventBus().send(address, new JsonObject()
      .put("data", routingContext.body().asJsonObject().put("params" , routingContext.pathParams()))
      .put("requestId", requestId)
    );
  }

  protected static Handler<RoutingContext> handleFailure() {
    return errContext -> {
      if (errContext.response().ended()) {
        return;
      }
      LOG.error("Routing error : ", errContext.failure());
      LOG.error("Error code : {} ", errContext.statusCode());
      if (errContext.statusCode() == HttpResponseStatus.UNAUTHORIZED.code()) {
        errContext.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
          .end(
            new JsonObject().put("message", "Please login first").toString()
          );
      } else {
        errContext.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end(
            new JsonObject().put("message", "Something went wrong, try again later").toString()
          );
      }

    };
  }

}
