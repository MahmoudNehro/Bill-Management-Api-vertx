package com.bill_management.models;

import io.vertx.core.json.JsonObject;

public record User(Integer id, String name, String email, String phone, String password) implements Model {

  public User(Integer id, String name, String email, String phone) {
    this(id, name, email, phone, null);
  }

  public User(String name, String email, String phone, String password) {
    this(null, name, email, phone, password);
  }

  public User(String email, String password) {
    this(null, null, email, null, password);
  }

  public User(String email) {
    this(null, null, email, null, null);
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
