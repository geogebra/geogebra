package geogebra.test.util;

import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import geogebra.cas.GeoGebraCasIntegrationTest;

public class TestRunner {
 
 public static void main(String[] args){
	 JUnitCore c = new JUnitCore();
	 GeoGebraCasIntegrationTest.silent = true;
	 Result res = c.run(GeoGebraCasIntegrationTest.class);
	 List<Failure> failures = res.getFailures();
	 System.out.println("*******************************");
	 for(Failure f:failures){
		 System.out.println(f.getDescription());
		 System.out.println(f.getMessage());
	 }
	 
		 ;
	 
 }
}
