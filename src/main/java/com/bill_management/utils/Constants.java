package com.bill_management.utils;

public class Constants {

  public static class EventAddress {
    // general
    public static final String RESPONSE = "RESPONSE";
    // admin auth
    public static final String LOGIN_ADDRESS = "LOGIN";
    // categories
    public static final String CREATE_CATEGORY_ADDRESS = "CREATE_CATEGORY";
    public static final String UPDATE_CATEGORY_ADDRESS = "UPDATE_CATEGORY";
    public static final String GET_CATEGORY_ADDRESS = "GET_CATEGORY";
    public static final String LIST_CATEGORY_ADDRESS = "LIST_CATEGORY";
    public static final String DELETE_CATEGORY_ADDRESS = "DELETE_CATEGORY";
    // users
    public static final String CREATE_USER_ADDRESS = "CREATE_USER";
    public static final String UPDATE_USER_ADDRESS = "UPDATE_USER";
    public static final String GET_USER_ADDRESS = "GET_USER";
    public static final String LIST_USER_ADDRESS = "LIST_USER";
    public static final String DELETE_USER_ADDRESS = "DELETE_USER";
  }
  public static class Keys {
    public static final String REQUEST_ID = "requestId";
    public static final String STATUS_CODE = "status_code";
    public static final String DATA = "data";
    public static final String MESSAGE = "message";
    public static final String PARAMS = "params";
    public static final String CONTENT_TYPE = "content-type";
    public static final String CONTENT_TYPE_JSON = "application/json";

  }
}
