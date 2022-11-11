package org.dew.webfolder;

import java.io.Serializable;

import java.security.Principal;

/**
 * 
 * User bean.
 *
 */
public 
class User implements Principal, Serializable
{
  private static final long serialVersionUID = -6730119599854906600L;

  protected String name;
  protected String role;

  public User()
  {
  }

  public User(String name)
  {
    this.name = name;
  }

  public User(String name, String role)
  {
    this.name = name;
    this.role = role;
  }

  protected 
  void setName(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  protected String getFolder() {
    if(name == null || name.length() == 0) {
      return "guest";
    }
    int sep = name.indexOf('@');
    if(sep > 0) {
      return name.substring(sep + 1);
    }
    return name;
  }

  @Override
  public boolean equals(Object object) {
    if(object instanceof User) {
      return this.hashCode() == object.hashCode();
    }
    return false;
  }

  @Override
  public int hashCode() {
    if(name == null) return 0;
    return name.hashCode();
  }

  @Override
  public String toString() {
    return "User(" + name + "," + role + ")";
  }
}
