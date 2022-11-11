package org.dew.webfolder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RESTProxy", loadOnStartup = 0, urlPatterns = { "/restproxy/*" })
public 
class RESTProxy extends HttpServlet
{
  private static final long serialVersionUID = -7952480446481557464L;
  
  protected static String BASE_URL = "https://raw.githubusercontent.com/giosil/test-data/master";
  
  public
  void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    byte[] requestData = readRequest(request);
    
    String url = getForwardURL(request);
    
    List<Integer> httpCode = new ArrayList<Integer>();
    
    byte[] result = post(url, requestData, httpCode);
    
    if(httpCode != null && httpCode.size() > 0) {
      if(result == null) result = new byte[0];
      int statusCode = httpCode.get(0);
      
      response.setStatus(statusCode);
      response.setContentType("application/json");
      response.setContentLength(result.length);
      
      OutputStream outputStream = response.getOutputStream();
      outputStream.write(result);
      outputStream.flush();
    }
    else {
      sendDefaultResponse(response);
    }
  }
  
  public
  void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    String url = getForwardURL(request);
    
    List<Integer> httpCode = new ArrayList<Integer>();
    
    byte[] result = get(url, httpCode);
    
    if(httpCode != null && httpCode.size() > 0) {
      if(result == null) result = new byte[0];
      int statusCode = httpCode.get(0);
      
      response.setStatus(statusCode);
      response.setContentType("application/json");
      response.setContentLength(result.length);
      
      OutputStream outputStream = response.getOutputStream();
      outputStream.write(result);
      outputStream.flush();
    }
    else {
      sendDefaultResponse(response);
    }
  }
  
  public
  void sendDefaultResponse(HttpServletResponse response)
    throws ServletException, IOException
  {
    byte[] defaultResponse = "{}".getBytes();
    
    response.setContentType("application/json");
    response.setContentLength(defaultResponse.length);
    
    OutputStream outputStream = response.getOutputStream();
    outputStream.write(defaultResponse);
    outputStream.flush();
  }
  
  protected
  String getForwardURL(HttpServletRequest request)
  {
    String result = BASE_URL;
    
    String pathInfo = request.getPathInfo();
    if(pathInfo == null) pathInfo = "";
    if(!pathInfo.startsWith("/")) {
      pathInfo = "/" + pathInfo;
    }
    result += pathInfo;
    
    String queryString = request.getQueryString();
    if(queryString != null && queryString.length() > 0) {
      if(!queryString.startsWith("?")) {
        queryString = "?" + queryString;
      }
      result += queryString;
    }
    
    return result;
  }
  
  protected
  byte[] readRequest(HttpServletRequest request)
    throws IOException
  {
    byte[] result = null;
    InputStream in = null;
    try {
      in = request.getInputStream();
      
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] buff = new byte[1024];
      int n;
      while((n = in.read(buff)) > 0) {
        bos.write(buff, 0, n);
      }
      
      result = bos.toByteArray();
    }
    finally {
      if(in != null) try{ in.close(); } catch(Exception ex) {}
    }
    if(result == null) result = new byte[0];
    return result;
  }
  
  protected
  byte[] post(String url, byte[] data, List<Integer> httpCode)
    throws ServletException, IOException
  {
    byte[] result = null;
    boolean error = false;
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("POST");
      
      connection.setConnectTimeout(300000);
      connection.setReadTimeout(300000);
      
      if(data != null && data.length > 0) {
        connection.setDoOutput(true);
        
        connection.addRequestProperty("Content-Type",   "application/json");
        connection.addRequestProperty("Content-Length", String.valueOf(data.length));
        
        OutputStream out = connection.getOutputStream();
        out.write(data);
        out.flush();
        out.close();
      }
      
      int statusCode = connection.getResponseCode();
      error = statusCode >= 400;
      if(httpCode != null) {
        httpCode.add(statusCode);
      }
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        BufferedInputStream  bis = new BufferedInputStream(error ? connection.getErrorStream() : connection.getInputStream());
        byte[] buff = new byte[1024];
        int n;
        while((n = bis.read(buff)) > 0) baos.write(buff, 0, n);
        baos.flush();
        baos.close();
      }
      finally {
        if(connection != null) try{ connection.disconnect(); } catch(Exception ex) {}
      }
      
      result = baos.toByteArray();
    }
    catch(IOException ioex) {
      throw ioex;
    }
    catch(Exception ex) {
      throw new ServletException(ex);
    }
    return result;
  }
  
  protected
  byte[] get(String url, List<Integer> httpCode)
    throws ServletException, IOException
  {
    byte[] result = null;
    boolean error = false;
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("GET");
      
      connection.setConnectTimeout(300000);
      connection.setReadTimeout(300000);
      
      int statusCode = connection.getResponseCode();
      error = statusCode >= 400;
      if(httpCode != null) {
        httpCode.add(statusCode);
      }
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        BufferedInputStream  bis = new BufferedInputStream(error ? connection.getErrorStream() : connection.getInputStream());
        byte[] buff = new byte[1024];
        int n;
        while((n = bis.read(buff)) > 0) baos.write(buff, 0, n);
        baos.flush();
        baos.close();
      }
      finally {
        if(connection != null) try{ connection.disconnect(); } catch(Exception ex) {}
      }
      
      result = baos.toByteArray();
    }
    catch(IOException ioex) {
      throw ioex;
    }
    catch(Exception ex) {
      throw new ServletException(ex);
    }
    return result;
  }
}