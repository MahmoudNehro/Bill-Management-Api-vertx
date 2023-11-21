package com.bill_management.admin.repositories;

import com.bill_management.admin.models.User;

public class AuthenticationRepository implements BaseRepository<User>{

  public boolean login(User data) {
    if (connection != null) {
      System.out.println("Logged in");
      // email , password
      return true;
    }
    return false;
  }

  @Override
  public boolean executeQuery(User data) {
    return login(data);
  }
}
