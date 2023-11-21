package com.bill_management.admin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

  public static final Logger LOG = LoggerFactory.getLogger(Database.class);
  private static volatile Database instance;
  private static Connection connection;

  private Database(String url, String userName, String password) {
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      connection = DriverManager.getConnection(url, userName, password);
    } catch (ClassNotFoundException ex) {
      LOG.error("Error loading class ", ex.getException());
    } catch (SQLException ex) {
      LOG.error("SQL Exception when connecting ", ex.getNextException());
    }
  }

  public static Database getInstance(String url, String userName, String password) {
    Database result = instance;
    if (result != null) {
      return result;
    }
    synchronized (Database.class) {
      if (instance == null) {
        instance = new Database(url, userName, password);
      }
      return instance;
    }
  }

  public static Connection getConnection() {
    return connection;
  }
}
