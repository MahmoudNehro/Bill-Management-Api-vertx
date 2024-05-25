package com.bill_management.mainentry;

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

import static com.bill_management.utils.Constants.EventAddress.*;
import static com.bill_management.utils.Constants.Keys.*;

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
    //admin auth
    this.router.post("/login").handler(routingContext -> requestHandler(LOGIN_ADDRESS, vertx, routingContext));
    // categories
    this.router.post("/protected/categories/create").handler(routingContext -> requestHandler(CREATE_CATEGORY_ADDRESS, vertx, routingContext));
    this.router.put("/protected/categories/:categoryId/update").handler(routingContext -> requestHandler(UPDATE_CATEGORY_ADDRESS, vertx, routingContext));
    this.router.get("/protected/categories/:categoryID").handler(routingContext -> requestHandler(GET_CATEGORY_ADDRESS, vertx, routingContext));
    this.router.get("/protected/categories").handler(routingContext -> requestHandler(LIST_CATEGORY_ADDRESS, vertx, routingContext));
    this.router.delete("/protected/categories/:categoryID").handler(routingContext -> requestHandler(DELETE_CATEGORY_ADDRESS, vertx, routingContext));
    // users
    this.router.post("/protected/users/create").handler(routingContext -> requestHandler(CREATE_USER_ADDRESS, vertx, routingContext));
    this.router.put("/protected/users/:userID/update").handler(routingContext -> requestHandler(UPDATE_USER_ADDRESS, vertx, routingContext));
    this.router.get("/protected/users/:userID").handler(routingContext -> requestHandler(GET_USER_ADDRESS, vertx, routingContext));
    this.router.get("/protected/users").handler(routingContext -> requestHandler(LIST_USER_ADDRESS, vertx, routingContext));
    this.router.delete("/protected/users/:userID").handler(routingContext -> requestHandler(DELETE_USER_ADDRESS, vertx, routingContext));
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
    JsonObject data = new JsonObject();
    data.put(REQUEST_ID, requestId);
    if (routingContext.body().asJsonObject() != null) {
      data.put(DATA, routingContext.body().asJsonObject().put(PARAMS, routingContext.pathParams()));
    } else {
      data.put(DATA, new JsonObject().put(PARAMS, routingContext.pathParams()));
    }
    vertx.eventBus().send(address, data);
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
          .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
          .setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
          .end(
            new JsonObject().put(MESSAGE, "Please login first").toString()
          );
      } else {
        errContext.response()
          .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end(
            new JsonObject().put(MESSAGE, "Something went wrong, try again later").toString()
          );
      }

    };
  }

}
