package com.bill_management.admin.repositories;

import com.bill_management.admin.models.Model;
import com.bill_management.admin.utils.Database;

import java.sql.Connection;

public interface BaseRepository<T extends Model> {
  Connection connection = Database.getConnection();

  boolean executeQuery(T data);
}
