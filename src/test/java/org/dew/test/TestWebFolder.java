package org.dew.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestWebFolder extends TestCase {
  
  public TestWebFolder(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestWebFolder.class);
  }
  
  public void testApp() throws Exception {
  }
}
