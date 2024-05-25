package com.bill_management.verticles.admin.auth;

import com.bill_management.models.User;
import com.bill_management.repositories.AuthenticationRepository;
import com.bill_management.utils.GeneralVerticle;
import com.bill_management.utils.Helpers;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bill_management.utils.Constants.EventAddress.LOGIN_ADDRESS;
import static com.bill_management.utils.Constants.Keys.MESSAGE;
import static com.bill_management.utils.Constants.Keys.STATUS_CODE;

public class LoginVerticle extends GeneralVerticle {

  @Override
  public void process(JsonObject body, String requestId) {
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

    if (result.getInteger(STATUS_CODE) == HttpResponseStatus.OK.code() && Helpers.validatePassword(password, hashedPassword)) {
      response.put(MESSAGE, "Login Success")
        .put("token", jwt.generateToken(new JsonObject().put("sub", user.email())))
        .put("user", result.getJsonObject("user"));
      returnResponse(response, requestId, HttpResponseStatus.OK.code());
    } else {
      response.put(MESSAGE, "Login failed, invalid credintials")
        .put("token", null);
      returnResponse(response, requestId, HttpResponseStatus.BAD_REQUEST.code());
    }
  }

  @Override
  protected String getRequestAddress() {
    return LOGIN_ADDRESS;
  }

  @Override
  protected Logger getLogger() {
    return LoggerFactory.getLogger(LoginVerticle.class.getName());
  }
}
