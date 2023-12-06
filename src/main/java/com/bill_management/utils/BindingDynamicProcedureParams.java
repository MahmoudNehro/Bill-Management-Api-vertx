package com.bill_management.utils;

import com.bill_management.models.Model;

import java.sql.CallableStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface BindingDynamicProcedureParams<T extends Model> {
  void bind(CallableStatement callableStatement, T model) throws SQLException;
}
