/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra;

//INTENTIONAL ERROR TO TEST AUTOTEST MACHINERY import geogebra.common.GeoGebraConstants;

import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;
 
public class GeoGebra extends Object implements GeoGebraConstants {
	
	public static Frame splashFrame = null;
	
    public static void main(String[] cmdArgs) {  
		CommandLineArguments args = new CommandLineArguments(cmdArgs);
				
    	boolean showSplash = true;
    	if(!args.getBooleanValue("showSplash", true)) {
    		showSplash = false;
    	}
    	
    	if(args.containsArg("help")||args.containsArg("v")) {
    		showSplash = false;
    	}

			if(args.containsArg("regressionFile")) {
				showSplash = false;
			}
    	
    	if (showSplash) {
    	  // Show splash screen
		  URL imageURL = GeoGebra.class.getResource("/geogebra/"+SPLASH_STRING);
		  if (imageURL != null) {
		      splashFrame = SplashWindow.splash(
		          Toolkit.getDefaultToolkit().createImage(imageURL)
		      );
		  } else {
		      System.err.println("Splash image not found");
		  }
    	}
		  
		  // Start GeoGebra
		  try {        			  		
			  startGeoGebra(args);               	
		  } catch (Throwable e) {
	      e.printStackTrace();
	      System.err.flush();
	      System.exit(10);
		  }
		  
		  // Hide splash screen
		  if (splashFrame != null) splashFrame.setVisible(false);
    }
    
    private static void startGeoGebra(CommandLineArguments args) {
    	// create and open first GeoGebra window        	
    	geogebra.gui.app.GeoGebraFrame.main(args);
    }
    
}