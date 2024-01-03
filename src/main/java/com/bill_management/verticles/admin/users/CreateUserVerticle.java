package com.bill_management.verticles.admin.users;

import com.bill_management.models.User;
import com.bill_management.repositories.UserRepository;
import com.bill_management.utils.GeneralVerticle;
import com.bill_management.utils.Helpers;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bill_management.utils.EventAddress.CREATE_USER_ADDRESS;

public class CreateUserVerticle extends GeneralVerticle {
  public static final Logger LOG = LoggerFactory.getLogger(CreateUserVerticle.class.getName());

  @Override
  public void start(Promise<Void> startPromise) {
    var eventBus = vertx.eventBus();
    eventBus.<JsonObject>consumer(CREATE_USER_ADDRESS, message -> {
      LOG.info("Receiving body {}", message.body());
      JsonObject body = message.body().copy();
      JsonObject data = body.getJsonObject("data");
      String requestId = body.getString("requestId");

      process(data, requestId);
    });
    startPromise.complete();
  }

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

    response.put("message", result.getString("message"))
      .put("user", result.getJsonObject("user"));

    if (result.getInteger("status_code") == 200) {
      returnResponse(response, requestId, HttpResponseStatus.OK.code());
    } else {
      returnResponse(response, requestId, HttpResponseStatus.BAD_REQUEST.code());
    }
  }
}
