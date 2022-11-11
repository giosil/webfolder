package org.dew.webfolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URLConnection;

import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "WebFile", loadOnStartup = 0, urlPatterns = { "/file/*" })
public 
class WebFile extends HttpServlet 
{
  private static final long serialVersionUID = -7570610930139532148L;

  public static String ROOT_FOLDER  = System.getProperty("user.home");
  public static String STARTUP_TIME = String.valueOf(System.currentTimeMillis());

  @Override
  public
  void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
  {
    File file = getFile(request);
    if(file == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    URLConnection urlConnection = file.toURI().toURL().openConnection();
    if(urlConnection == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    String mimeType = urlConnection.getContentType();
    if(mimeType == null || mimeType.length() == 0) {
      mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if(mimeType == null || mimeType.length() == 0) {
        mimeType = "application/octet-stream";
      }
    }
    int length = urlConnection.getContentLength();
    if(length == 0) {
      length = (int) file.length();
    }
    String fileName = file.getName();
    boolean attachment = false;
    if(mimeType.indexOf("zip") >= 0) {
      attachment = true;
    }
    else if(mimeType.indexOf("octet-stream") >= 0) {
      attachment = true;
    }
    
    response.setContentType(mimeType);
    response.setContentLength(length);
    if(attachment) {
      response.addHeader("content-disposition", "attachment; filename=\"" + fileName + "\"");
    }
    
    OutputStream outputStream = response.getOutputStream();
    InputStream inputStream = null;
    try {
      inputStream = urlConnection.getInputStream();
      byte[] buffer = new byte[1024];
      int n;
      while ((n = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, n);
      }
      outputStream.flush();
    }
    catch(Exception ex) {
      throw new ServletException(ex);
    }
  }

  @Override
  public 
  void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
  {
    doGet(request, response);
  }

  @Override
  public 
  void doHead(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
  {
    File file = getFile(request);
    if(file == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    URLConnection urlConnection = file.toURI().toURL().openConnection();
    if(urlConnection == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    String mimeType = urlConnection.getContentType();
    if(mimeType == null || mimeType.length() == 0) {
      mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if(mimeType == null || mimeType.length() == 0) {
        mimeType = "application/octet-stream";
      }
    }
    int length = urlConnection.getContentLength();
    if(length == 0) {
      length = (int) file.length();
    }
    
    response.setContentType(mimeType);
    response.setContentLength(length);
    response.addHeader("content-disposition", "attachment; filename=\"" + file.getName() + "\"");
  }

  protected
  File getFile(HttpServletRequest request)
  {
    User user = WebLogin.getUserLogged(request);
    if(user == null) {
      return null;
    }
    
    String folder = user.getFolder();
    String pathInfo = request.getPathInfo();
    if(pathInfo.startsWith("/") || pathInfo.startsWith("\\")) {
      pathInfo = pathInfo.substring(1);
    }
    
    File file = new File(ROOT_FOLDER + File.separator + folder + File.separator + pathInfo);
    if(!file.exists()) {
      return null;
    }
    if(!file.isFile()) {
      return null;
    }
    
    return file;
  }
}
