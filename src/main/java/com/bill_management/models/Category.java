package com.bill_management.models;

import io.vertx.core.json.JsonObject;

public record Category(Integer id, String nameEn, String nameAr, String descEn, String descAr) implements Model {

  public Category(String nameEn, String nameAr, String descEn, String descAr) {
    this(null, nameEn, nameAr, descEn, descAr);
  }

  public Category(Integer id) {
    this(id, null, null, null, null);

  }

  @Override
  public JsonObject toJson() {
    return new JsonObject()
      .put("id", this.id)
      .put("name_en", this.nameEn)
      .put("name_ar", this.nameAr)
      .put("desc_en", this.descEn)
      .put("desc_ar", this.descAr);
  }

}
