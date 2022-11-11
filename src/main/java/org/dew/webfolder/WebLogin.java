package org.dew.webfolder;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "WebLogin", loadOnStartup = 0, urlPatterns = { "/login" })
public 
class WebLogin extends HttpServlet 
{
  private static final long serialVersionUID = 4490730091656762603L;

  private static IUserService userService;

  @Override
  public
  void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
  {
    doPost(request, response);
  }

  @Override
  protected 
  void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    HttpSession httpSession = request.getSession();
    if(httpSession == null) {
      sendMessage(request, response, "Session disabled");
      return;
    }
    
    String username = request.getParameter("j_username");
    if(username == null || username.length() == 0) {
      sendMessage(request, response, "Invalid username");
      return;
    }
    request.setAttribute("username", username);
    
    String password = request.getParameter("j_password");
    if(password == null || password.length() == 0) {
      sendMessage(request, response, "Invalid password");
      return;
    }
    
    User user = null;
    try {
      user = authenticate(username, password);
      if(user != null) {
        httpSession.setAttribute("user", user);
      }
      else {
        request.setAttribute("j_username", username);
        
        sendMessage(request, response, "Utente non riconosciuto");
        return;
      }
    }
    catch(Exception ex) {
      sendMessage(request, response, "Errore: " + ex);
      return;
    }
    
    RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
    requestDispatcher.forward(request, response);
  }

  protected static
  void sendMessage(HttpServletRequest request, HttpServletResponse response, String message)
      throws ServletException, IOException
  {
    request.setAttribute("message", message);

    RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
    requestDispatcher.forward(request, response);
  }

  protected
  User authenticate(String username, String password)
      throws Exception
  {
    if(username == null || username.length() == 0) return null;
    if(password == null || password.length() == 0) return null;
    
    User user = null;
    
    if(username.equalsIgnoreCase("guest") && password.equalsIgnoreCase("guest")) {
      return new User(username, "guest");
    }
    
    IUserService userService = WebLogin.getUserService();
    
    String role = userService.login(username, password);
    
    if(role != null && role.length() > 0) {
      user = new User(username, role);
    }
    
    return user;
  }

  public static
  User getUserLogged(HttpServletRequest request)
  {
    if(request == null) return null;
    
    HttpSession httpSession = request.getSession();
    if(httpSession == null) {
      return null;
    }
    
    Object user = httpSession.getAttribute("user");
    
    if(user instanceof User) {
      return (User) user;
    }
    
    return null;
  }

  public static
  IUserService getUserService()
  {
    if(userService == null) {
      userService = new LDAPUserService();
    }
    
    return userService;
  }
}
