package com.bill_management.admin.models;

import io.vertx.core.json.JsonObject;

public record User(Integer id, String name, String email, String phone, String password) implements Model {
  public User(String email, String password) {
    this(null, null, email, null, password);
  }

  @Override
  public JsonObject toJson() {
    return new JsonObject()
      .put("id", this.id)
      .put("name", this.name)
      .put("email", this.email)
      .put("phone", this.phone);
  }
}
