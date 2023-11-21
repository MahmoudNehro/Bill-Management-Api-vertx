package com.bill_management.admin.utils;
import com.bill_management.admin.models.Model;
import com.bill_management.admin.repositories.BaseRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public abstract class GeneralVerticle<T extends Model> extends AbstractVerticle {
  public abstract void process(BaseRepository<T> repository, JsonObject body, String requestId);

  public void returnResponse(JsonObject data, String requestId, int statusCode) {

    vertx.eventBus().send(EventAddress.RESPONSE, new JsonObject()
      .put("data", data)
      .put("statusCode", statusCode)
      .put("requestId", requestId)
    );
  }
}
