package com.bill_management.admin.models;

import io.vertx.core.json.JsonObject;

public interface Model {
  public JsonObject toJson();
}
