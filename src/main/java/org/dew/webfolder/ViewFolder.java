package org.dew.webfolder;

import java.io.File;

import java.util.Arrays;

public 
class ViewFolder 
{
  protected String folder;

  public ViewFolder()
  {
  }

  public ViewFolder(String folder)
  {
    this.folder = folder;
  }

  public ViewFolder(User user)
  {
    if(user != null) {
      this.folder = user.getFolder();
    }
  }

  protected 
  void list(File directory, StringBuilder sb, int level, String pathInfo)
  {
    File[] files = directory.listFiles();
    if(files == null || files.length == 0) {
      return;
    }
    Arrays.sort(files);
    if(level > 0) {
      sb.append("<li id=\"foldhead\"> <p>" + normalize(directory.getName(), true) + "</p></li>\n");
      sb.append("<ul id=\"foldlist\" style=\"display:none\">\n");
    }
    else {
      sb.append("<li id=\"foldhead\" class=\"open\"> <p>" + normalize(directory.getName(), true) + "</p></li>\n");
      sb.append("<ul id=\"foldlist\">\n");
    }
    for(int i = 0; i < files.length; i++) {
      File file_i = files[i];
      
      String fileName = file_i.getName();
      
      if(file_i.isDirectory()) {
        list(file_i, sb, level + 1, pathInfo + "/" + fileName);
      }
      else if(fileName.endsWith(".html") || fileName.endsWith(".htm")) {
        sb.append("<li><a href=\"" + pathInfo + "/" + fileName + "\" target=\"_blank\">" + normalize(fileName, false) + "</a></li>\n");
      }
      else if(fileName.endsWith(".pdf")) {
        sb.append("<li><a href=\"" + pathInfo + "/" + fileName + "\" target=\"_blank\">" + normalize(fileName, false) + "</a></li>\n");
      }
      else {
        sb.append("<li><a href=\"" + pathInfo + "/" + fileName + "\">" + normalize(fileName, false) + "</a></li>\n");
      }
    }
    sb.append("</ul>\n");
  }
  
  protected static 
  String normalize(String fileName, boolean isDirectory) 
  {
    if(fileName == null || fileName.length() == 0) {
      return "File";
    }
    
    char c0 = fileName.charAt(0);
    int fileNameLen = fileName.length();
    int startName = 0;
    if(Character.isDigit(c0) && fileNameLen > 2) {
      for(int j = 0; j < fileNameLen; j++) {
        char cj = fileName.charAt(j);
        if(!Character.isDigit(cj)) {
          startName = j + 1;
          break;
        }
      }
    }
    
    int endName = fileNameLen;
    if(!isDirectory) {
      int sep = fileName.lastIndexOf('.');
      if(sep > 0) {
        endName = sep;
      }
    }
    if(startName >= endName) {
      startName = 0;
    }
    
    return toHTML(fileName.substring(startName, endName).replace('$', ':'));
  }
  
  public static
  String toHTML(String text)
  {
    if(text == null || text.length() <= 1) return text;
    // i < sText.length() - 1 because String s = sText.substring(i, i + 2);
    boolean boReplaced = false;
    StringBuilder sb = new StringBuilder(text.length());
    for(int i = 0; i < text.length() - 1; i++) {
      char c = text.charAt(i);
      String s = text.substring(i, i + 2);
      boReplaced = true;
      if(s.equals("a'")) sb.append("&agrave;");
      else if(s.equals("e'")) sb.append("&egrave;");
      else if(s.equals("i'")) sb.append("&igrave;");
      else if(s.equals("o'")) {
        if(i > 1 && !text.substring(i-2,i).equalsIgnoreCase(" p") && !text.substring(i-2,i).equalsIgnoreCase("\np")) {
          sb.append("&ograve;");
        }
        else {
          sb.append("o'");
        }
      }
      else if(s.equals("u'")) sb.append("&ugrave;");
      else if(s.equals("a`")) sb.append("&aacute;");
      else if(s.equals("e`")) sb.append("&eacute;");
      else if(s.equals("i`")) sb.append("&iacute;");
      else if(s.equals("o`")) sb.append("&oacute;");
      else if(s.equals("u`")) sb.append("&uacute;");
      else if(s.equals("A'")) sb.append("&Agrave;");
      else if(s.equals("E'")) sb.append("&Egrave;");
      else if(s.equals("I'")) sb.append("&Igrave;");
      else if(s.equals("O'")) {
        if(i > 1 && !text.substring(i-2,i).equalsIgnoreCase(" p") && !text.substring(i-2,i).equalsIgnoreCase("\np")) {
          sb.append("&Ograve;");
        }
        else {
          sb.append("O'");
        }
      }
      else if(s.equals("U'")) sb.append("&Ugrave;");
      else if(s.equals("A`")) sb.append("&Aacute;");
      else if(s.equals("E`")) sb.append("&Eacute;");
      else if(s.equals("I`")) sb.append("&Iacute;");
      else if(s.equals("O`")) sb.append("&Oacute;");
      else if(s.equals("U`")) sb.append("&Uacute;"); 
      else {
        sb.append(c);
        boReplaced = false;
      }
      if(boReplaced) i++;
    }
    char cLast = text.charAt(text.length() - 1);
    if(cLast != '\'' && cLast != '`') sb.append(cLast);
    return sb.toString();
  }

  @Override
  public boolean equals(Object object) {
    if(object instanceof ViewFolder) {
      return this.hashCode() == object.hashCode();
    }
    return false;
  }

  @Override
  public int hashCode() {
    if(folder == null) return 0;
    return folder.hashCode();
  }

  @Override
  public String toString() {
    boolean checkRoot = false;
    File root = new File(WebFile.ROOT_FOLDER);
    checkRoot = root.exists();
    if(!checkRoot) {
      checkRoot = root.mkdirs();
    }
    if(!checkRoot) {
      return "<p>The folder <strong>" + WebFile.ROOT_FOLDER + "</strong> does not exist.</p>";
    }
    if(folder == null || folder.length() == 0) {
      folder = "guest";
    }
    
    String pathFolder = WebFile.ROOT_FOLDER + File.separator + folder;
    
    File fileFolder = new File(pathFolder);
    if(!fileFolder.exists()) {
      return "<p>The folder <strong>" + fileFolder + "</strong> does not exist.</p>";
    }
    
    File[] files = fileFolder.listFiles();
    if(files == null || files.length == 0) {
      return "<p>The folder <strong>" + fileFolder + "</strong> contains no files.</p>";
    }
    Arrays.sort(files);
    
    StringBuilder sb = new StringBuilder();
    sb.append("<div id=\"tree-container\">");
    sb.append("<ul>\n");
    list(fileFolder, sb, 0, "file");
    sb.append("</ul>\n");
    sb.append("</div>");
    
    return sb.toString();
  }
}
