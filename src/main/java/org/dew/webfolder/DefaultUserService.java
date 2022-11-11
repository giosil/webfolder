package org.dew.webfolder;

public 
class DefaultUserService implements IUserService
{
  @Override
  public 
  String login(String username, String password) 
      throws Exception 
  {
    if(username == null || username.length() == 0) {
      return null;
    }
    if(password == null || password.length() == 0) {
      return null;
    }
    
    if(!username.equals(password)) {
      return null;
    }
    
    return "user";
  }

  @Override
  public 
  boolean logout(String username) 
      throws Exception 
  {
    return true;
  }
}
