package org.dew.webfolder;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "WebLogout", loadOnStartup = 0, urlPatterns = { "/logout" })
public 
class WebLogout extends HttpServlet 
{
  private static final long serialVersionUID = 4490730091656762603L;

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
    if(httpSession == null) return;
    
    User user = WebLogin.getUserLogged(request);
    if(user == null) return;
    
    IUserService userService = null;
    try {
      userService = WebLogin.getUserService();
      
      userService.logout(user.getName());
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    
    try {
      httpSession.invalidate();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    
    RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
    requestDispatcher.forward(request, response);
  }
}
