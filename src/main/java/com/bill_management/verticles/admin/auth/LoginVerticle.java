package com.bill_management.verticles.admin.auth;

import com.bill_management.models.User;
import com.bill_management.repositories.AuthenticationRepository;
import com.bill_management.repositories.BaseRepository;
import com.bill_management.utils.EventAddress;
import com.bill_management.utils.GeneralVerticle;
import com.bill_management.utils.Helpers;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginVerticle extends GeneralVerticle {

  public static final Logger LOG = LoggerFactory.getLogger(LoginVerticle.class.getName());

  @Override
  public void start(Promise<Void> startPromise) {
    var eventBus = vertx.eventBus();
    eventBus.<JsonObject>consumer(EventAddress.LOGIN_ADDRESS, message -> {
      LOG.debug("Receiving body {} ", message.body());
      JsonObject body = message.body().copy();
      JsonObject data = body.getJsonObject("data");
      String requestId = body.getString("requestId");

      process(data, requestId);
    });
    startPromise.complete();
  }

  @Override
  public void process( JsonObject body, String requestId) {
    JWTAuthOptions authConfig = new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
        .setType("jceks")
        .setPath("keystore.jceks")
        .setPassword("secret"));
    JWTAuth jwt = JWTAuth.create(vertx, authConfig);
    JsonObject response = new JsonObject();
    String email = body.getString("email");
    String password = body.getString("password");
    User user = new User(email);
    JsonObject result = new AuthenticationRepository().login(user);
    String hashedPassword = result.getString("password");

    if (result.getInteger("status_code") == 200 && Helpers.validatePassword(password, hashedPassword)) {
      response.put("message", "Login Success")
        .put("token", jwt.generateToken(new JsonObject().put("sub", user.email())))
        .put("user", result.getJsonObject("user"));
      returnResponse(response, requestId, HttpResponseStatus.OK.code());
    } else {
      response.put("message", "Login failed, invalid credintials")
        .put("token", null);
      returnResponse(response, requestId, HttpResponseStatus.BAD_REQUEST.code());
    }
  }
}
