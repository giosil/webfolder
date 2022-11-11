package org.dew.webfolder;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

public 
class LDAPUserService implements IUserService
{
  protected static final String LDAP_PROVIDER     = "ldap://ldap.dew.org:389";
  protected static final String DOMAIN_CONTROLLER = "DC=ad,DC=dew,DC=org";
  protected static final String ADMIN_DN          = "CN=User.ldap,OU=dew," + DOMAIN_CONTROLLER;
  protected static final String ADMIN_PASS        = "password";
  protected static final int    SEARCH_SCOPE      = SearchControls.SUBTREE_SCOPE;

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
    int sep = username.indexOf('@');
    if(sep <= 0) return null;
    
    String organizationalUnit = getOrganizationalUnit(username);
    if(organizationalUnit == null || organizationalUnit.length() == 0) {
      System.err.println("[LDAPUserService] login(" + username + ",*)#getOrganizationalUnit(" + username + ") -> " + organizationalUnit);
      return null;
    }
    
    String userDN = search(username, organizationalUnit);
    if(userDN == null || userDN.length() == 0) {
      System.err.println("[LDAPUserService] login(" + username + ",*)#search(" + username + "," + organizationalUnit + ") -> " + userDN);
      return null;
    }
    else {
      System.err.println("[LDAPUserService] login(" + username + ",*)#search(" + username + "," + organizationalUnit + ") -> " + userDN);
    }
    
    boolean success = auth(userDN, password);
    if(!success) {
      System.err.println("[LDAPUserService] login(" + username + ",*)#auth(" + userDN + ",*) -> " + success);
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

  protected
  String getOrganizationalUnit(String username)
  {
    if(username == null || username.length() == 0) {
      return null;
    }
    int sep = username.indexOf('@');
    if(sep <= 0) {
      return null;
    }
    String domain = username.substring(sep + 1).toLowerCase();
    return domain;
  }

  protected 
  String search(String email, String organizationalUnit) 
  {
    String baseDN = "OU=OU_" + organizationalUnit + "," + DOMAIN_CONTROLLER;
    String filter = "(mail=" + email + ")";
    
    String result = null;
    InitialLdapContext ldapContext = null;
    try {
      Hashtable<String, Object> environment = new Hashtable<String, Object>();
      environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      environment.put(Context.PROVIDER_URL,            LDAP_PROVIDER);
      environment.put(Context.SECURITY_AUTHENTICATION, "simple");
      environment.put(Context.SECURITY_PRINCIPAL,      ADMIN_DN);
      environment.put(Context.SECURITY_CREDENTIALS,    ADMIN_PASS);
      
      ldapContext = new InitialLdapContext(environment, null);
      
      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SEARCH_SCOPE);
      searchControls.setCountLimit(10);
      searchControls.setDerefLinkFlag(false);
      
      NamingEnumeration<SearchResult> namingEnumeration = ldapContext.search(baseDN, filter, searchControls);
      
      while(namingEnumeration.hasMore()) {
        SearchResult searchResult = namingEnumeration.next();
        result = searchResult.getName();
        if(result != null && result.length() > 0) {
          if(result.indexOf(" admin") >= 0) {
            continue;
          }
          else {
            break;
          }
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    finally {
      if(ldapContext != null) try { ldapContext.close(); } catch(Exception ex) {}
    }
    if(result != null && result.length() > 0) {
      return result + "," + baseDN;
    }
    return result;
  }

  protected 
  boolean auth(String userDN, String password) 
  {
    boolean result = false;
    InitialLdapContext ldapContext = null;
    try {
      Hashtable<String, Object> environment = new Hashtable<String, Object>();
      environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      environment.put(Context.PROVIDER_URL,            LDAP_PROVIDER);
      environment.put(Context.SECURITY_AUTHENTICATION, "simple");
      environment.put(Context.SECURITY_PRINCIPAL,      userDN);
      environment.put(Context.SECURITY_CREDENTIALS,    password);
      
      ldapContext = new InitialLdapContext(environment, null);
      
      result = true;
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    finally {
      if(ldapContext != null) try { ldapContext.close(); } catch(Exception ex) {}
    }
    return result;
  }
}
