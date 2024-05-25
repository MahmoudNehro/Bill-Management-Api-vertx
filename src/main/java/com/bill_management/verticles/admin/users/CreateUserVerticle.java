package com.bill_management.verticles.admin.users;

import com.bill_management.models.User;
import com.bill_management.repositories.UserRepository;
import com.bill_management.utils.GeneralVerticle;
import com.bill_management.utils.Helpers;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bill_management.utils.Constants.EventAddress.CREATE_USER_ADDRESS;
import static com.bill_management.utils.Constants.Keys.MESSAGE;
import static com.bill_management.utils.Constants.Keys.STATUS_CODE;

public class CreateUserVerticle extends GeneralVerticle {

  @Override
  public void process(JsonObject body, String requestId) {
    String name = body.getString("name");
    String email = body.getString("email");
    String phone = body.getString("phone");
    String password = body.getString("password");
    String hashedPassword = Helpers.hashPassword(password);
    User user = new User(name, email, phone, hashedPassword);

    JsonObject result = new UserRepository().createUser(user);
    JsonObject response = new JsonObject();

    response.put(MESSAGE, result.getString(MESSAGE))
      .put("user", result.getJsonObject("user"));

    if (result.getInteger(STATUS_CODE) == HttpResponseStatus.OK.code()) {
      returnResponse(response, requestId, HttpResponseStatus.OK.code());
    } else {
      returnResponse(response, requestId, HttpResponseStatus.BAD_REQUEST.code());
    }
  }

  @Override
  protected String getRequestAddress() {
    return CREATE_USER_ADDRESS;
  }

  @Override
  protected Logger getLogger() {
    return LoggerFactory.getLogger(CreateUserVerticle.class.getName());
  }
}
