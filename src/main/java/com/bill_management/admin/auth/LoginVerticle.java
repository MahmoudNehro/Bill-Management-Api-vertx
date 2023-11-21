package com.bill_management.admin.auth;

import com.bill_management.admin.models.User;
import com.bill_management.admin.repositories.AuthenticationRepository;
import com.bill_management.admin.repositories.BaseRepository;
import com.bill_management.admin.utils.EventAddress;
import com.bill_management.admin.utils.GeneralVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginVerticle extends GeneralVerticle<User> {
  public static final Logger LOG = LoggerFactory.getLogger(LoginVerticle.class.getName());

  @Override
  public void start(Promise<Void> startPromise) {
    var eventBus = vertx.eventBus();
    eventBus.<JsonObject>consumer(EventAddress.LOGIN_ADDRESS, message -> {
      LOG.debug("Receiving body {} ", message.body());
      JsonObject body = message.body().copy();
      JsonObject data = body.getJsonObject("data");
      String requestId = body.getString("requestId");

      process(new AuthenticationRepository(),data, requestId);
    });
    startPromise.complete();
  }

  @Override
  public void process(BaseRepository<User> userAuthenticationRepository , JsonObject body, String requestId) {
    JWTAuthOptions authConfig = new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
        .setType("jceks")
        .setPath("keystore.jceks")
        .setPassword("secret"));
    JWTAuth jwt = JWTAuth.create(vertx, authConfig);
    JsonObject response = new JsonObject();
    User user = new User(body.getString("email"), body.getString("password"));
    boolean loggedIn = userAuthenticationRepository.executeQuery(user);
    if (loggedIn) {
      response.put("message", "Login Success")
        .put("token", jwt.generateToken(new JsonObject().put("sub", user.email())))
        .put("user", user.toJson());
      returnResponse(response, requestId, HttpResponseStatus.OK.code());
    } else {
      response.put("message", "Login failed, invalid credintials")
        .put("token", null);
      returnResponse(response, requestId, HttpResponseStatus.BAD_REQUEST.code());
    }
  }
}
