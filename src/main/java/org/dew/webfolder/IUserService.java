package org.dew.webfolder;

/**
 * 
 * User service interface.
 *
 */
public 
interface IUserService 
{
  public String login(String username, String password) throws Exception;
  
  public boolean logout(String username) throws Exception;
}
