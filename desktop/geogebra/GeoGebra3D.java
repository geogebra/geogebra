/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra;

import geogebra3D.euclidian3D.opengl.RendererJogl;

import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;


public class GeoGebra3D extends GeoGebra
{
	
	// File format versions
	public static final String XML_FILE_FORMAT = "5.0";
	
	
	static {
		RendererJogl.initSingleton();
	}

	/* WARNING: This code is the same as in GeoGebra.java.
	 * TODO: Make the same code common.
	 */
	
	public static Frame splashFrame = null;
	
	public static void main(String[] cmdArgs) {  
		CommandLineArguments args = new CommandLineArguments(cmdArgs);
				
    	boolean showSplash = true;
    	if (!args.getBooleanValue("showSplash", true)) {
    		showSplash = false;
    	}
    	
    	if (args.containsArg("help") || args.containsArg("proverhelp")
    			|| args.containsArg("v") || args.containsArg("regressionFile")) {
    		showSplash = false;
    	}
    	
    	if (showSplash) {
    	  // Show splash screen
		  URL imageURL = GeoGebra.class.getResource("/geogebra3D/splash5beta.png");
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
    	geogebra.gui.app.GeoGebraFrame3D.main(args);
    }

	

}