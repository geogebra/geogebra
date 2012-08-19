package geogebra.plugin;
/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.App;
import geogebra.common.util.StringUtil;
import geogebra.io.MyImageIO;
import geogebra.kernel.EvalCommandQueue;
import geogebra.main.AppD;
import geogebra.util.Util;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JOptionPane;


/** 
<h3>GgbAPI - API for PlugLets </h3>
<pre>
   The Api the plugin program can use.
</pre>
<ul><h4>Interface:</h4>
<li>GgbAPI(Application)      //Application owns it
<li>getApplication()
<li>getKernel()
<li>getConstruction()
<li>getAlgebraProcessor()
<li>getPluginManager()
<li>evalCommand(String)
<li>and the rest of the methods from the Applet JavaScript/Java interface
<li>...
</ul>
@author      H-P Ulven
@version     31.10.08
29.05.08:
    Tranferred applet interface methods (the relevant ones) from GeoGebraAppletBase
*/

public class GgbAPID extends geogebra.common.plugin.GgbAPI {

   
   /** Constructor:
    *  Makes the api with a reference to the GeoGebra program.
    *  Called from GeoGebra.
    *  @param app Application
    */
    public GgbAPID(AppD app) {
        this.app=app;
        kernel=app.getKernel();
        algebraprocessor=kernel.getAlgebraProcessor();
        construction=kernel.getConstruction();
    //    pluginmanager=app.getPluginManager();
    }//Constructor
    
    /** Returns reference to PluginManager */
//    public PluginManager getPluginManager() {
//    	if(pluginmanager==null){
//    		this.pluginmanager=app.getPluginManager();
//    	}//if not initialized
//    	return this.pluginmanager;
//    }//getPluginManager()

    /**
     * TODO decide whether we can remove this method 
     * Returns reference to ClassPathManipulator
     * @deprecated always returns null 
     * @return null*/
    @Deprecated
	public ClassPathManipulator getClassPathManipulator(){
        return null;//ClassPathManipulator;
    }//getClassPathManipulator()
    
    /** Executes a GeoGebra command 
    29.05.08 commented out
    as the right one is copied from applet interface.
    I never saw that it should return boolean before now...
    
    public void evalCommand(String cmd) {
        if(algebraprocessor!=null) {
            algebraprocessor.processAlgebraCommand(cmd, true);
        }else{
            Application.debug("Cannot find the GeoGebra AlgebraProcessor!");
        }//if ggb not null
    }//evalCommand(String)
    */
    /// --- 17.02.09 Ulven: --- ///
    /** MathPiper console in Java Console
    public void mathPiperJavaConsole(){
    	System.out.println("---MathPiper Console---");
    	org.mathpiper.ui.text.consoles.Console cons=new org.mathpiper.ui.text.consoles.Console();
    	cons.repl(System.in,System.out);
    }//mathPiperJavaConsole()
    */
    
    /** Making MathPiper available for Plugins
     *  (Silent version, without debug to console)
     *  @deprecated since GeoGebra 4.0
     *
    public String evaluateMathPiper(String cmdString){
    	return kernel.evaluateMathPiper(cmdString);
    }//evaluateMathPiper(String)*/
    	
    /// --- 29.05.08 Ulven: --- ///
    
    
    ///* JAVA SCRIPT INTERFACE */
	
	/**
	 * Returns current construction as a ggb file in form of a byte array.
	 * @return null if something went wrong 
	 */
	public synchronized byte [] getGGBfile() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			((AppD) app).getXMLio().writeGeoGebraFile(bos, true);
			bos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns current construction in Base64 format. May be used for saving.
	 */
	@Override
	public synchronized String getBase64(boolean includeThumbnail) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			((AppD) app).getXMLio().writeGeoGebraFile(baos, includeThumbnail);
			return geogebra.common.util.Base64.encode(baos.toByteArray(), 0);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Opens construction given in XML format. May be used for loading constructions.
	 */
	public synchronized void setBase64(String base64) {
		byte[] zipFile;
		try {
			zipFile = geogebra.common.util.Base64.decode(base64);
		} catch (IOException e) {
			
			e.printStackTrace();
			return;
		}
		((AppD) app).loadXML(zipFile);
	}
	
	
	
	/**
	 * Evaluates the given string as if it was entered into MathPiper's 
	 * input text field. 	 
	 * @deprecated since GeoGebra 4.0, use evalGeoGebraCAS() instead
	 *
	public synchronized String evalMathPiper(String cmdString) {
		
		String ret = kernel.evaluateMathPiper(cmdString);
		
		// useful for debugging JavaScript
		// do not remove!
		Application.debug("evalMathPiper\n input:"+cmdString+"\n"+"output: "+ret);
		
		return ret;

	}*/

	/**
	 * Evaluates the given string as if it was entered into Maxima's
	 * input text field. 
	 * @author Ulven
	 * @version 2010-03-10	
	 * @deprecated since GeoGebra 4.0, use evalGeoGebraCAS() instead 
	 *
	public synchronized String evalMaxima(String cmdString) {
		
		String ret = kernel.evaluateMaxima(cmdString);
		
		// useful for debugging JavaScript
		// do not remove!
		Application.debug("evalMaxima\n input:"+cmdString+"\n"+"output: "+ret);
		
		return ret;

	}//evalMaxima(String cmdString)*/
	
	/* Not needed, see next command:
	public synchronized geogebra.cas.GeoGebraCAS getCurrentCAS(){
		return 	(geogebra.cas.GeoGebraCAS) kernel.getGeoGebraCAS();
	}//getCurrentCas()
	*/
	
	public synchronized boolean evalCommand(final String cmdString, boolean waitForResult) {
		if (waitForResult) {
			return evalCommand(cmdString);
		}
		
		//evalCommand(cmdString);
		getEvalCommandQueue().addCommand(cmdString);
		
		
		return true;

	}
	
	EvalCommandQueue evq;
	
	private EvalCommandQueue getEvalCommandQueue() {
		if (evq == null) {
			evq = new EvalCommandQueue(this);
		}
		
		return evq;
	}

	/**
	 * Turns showing of error dialogs on (true) or (off). 
	 * Note: this is especially useful together with evalCommand().
	 */
	public synchronized void setErrorDialogsActive(boolean flag) {
		((AppD) app).setErrorDialogsActive(flag);
	}
	
	/**
	 * Resets the initial construction (given in filename parameter) of this applet.	
	 * ...but the actual code is in a thread to avoid JavaScript security issues 
	 */
	public synchronized void reset() {
		
		App.warn("unimplemented");
	}
	
	/**
	 * Clears the construction and resets all views.
	 */
	public synchronized void fileNew() {
		((AppD) app).fileNew();
	}
	
	/**
	 * Refreshs all views. Note: clears traces in
	 * geometry window.
	 */
	public synchronized void refreshViews() {
		app.refreshViews();
	}
			
	/**
	 * Loads a construction from a  file (given URL).
	 * Note that this method does NOT refresh the user interface.
	 */
	public synchronized void openFile(String strURL) {
		try {
			String lowerCase = StringUtil.toLowerCase(strURL);
			URL url = new URL(strURL);
			((AppD) app).loadXML(url, lowerCase.endsWith(AppD.FILE_EXT_GEOGEBRA_TOOL));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	/*
	public synchronized void setLanguage(String isoLanguageString) {	
		app.setLanguage(new Locale(isoLanguageString));
	}
	
	public synchronized void setLanguage(String isoLanguageString, String isoCountryString) {
		app.setLanguage(new Locale(isoLanguageString, isoCountryString));
	}
	*/
	
		
	/*
	 * used by the automatic file tester (from JavaScript)
	 * returns a checksum of the graphics view
	 * to check it is the same as the baseline version
	 */
	public String getGraphicsViewCheckSum(String algorithm, String format) {
		
		if (!algorithm.equals("MD5"))
			return "";
		
		try {
		
			BufferedImage img =
				((AppD)app).getEuclidianView1().getExportImage(1);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, StringUtil.toLowerCase(format), baos);
			
			MessageDigest md5 = getMessageDigestMD5();
			byte[] bytesOut = baos.toByteArray();
			md5.update(bytesOut);
			byte[] md5hash = new byte[32];
			md5hash = md5.digest();
			return StringUtil.convertToHex(md5hash);	

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
	
	private static MessageDigest messageDigestMD5 = null;

	/**
	 * @return reference to MD5 algorithm
	 * @throws NoSuchAlgorithmException if algorithm is not supported
	 */
	public static MessageDigest getMessageDigestMD5()
			throws NoSuchAlgorithmException {
		if (messageDigestMD5 == null)
			messageDigestMD5 = MessageDigest.getInstance("MD5");

		return messageDigestMD5;
	}
	


	
	
	/*
	 * saves a PNG file
	 * signed applets only
	 */
	public synchronized boolean writePNGtoFile(String filename, final double exportScale, final boolean transparent, final double DPI) {
		if (!AppD.hasFullPermissions()) return false;
		File file1 = null;
		try{
		 file1 = new File(filename);
		}
		catch(Throwable t){
			t.printStackTrace();
		}
		if (file1 == null) return false;
		final File file = file1;
		return (Boolean) AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Boolean run() {

				try {			
					// draw graphics view into image
					BufferedImage img =
							((AppD)getApplication()).getEuclidianView1().getExportImage(exportScale, transparent); 
					
					// write image to file
					MyImageIO.write(img, "png", (float)DPI,  file);	
					
					return true;
				} catch (Exception ex) {
					App.debug(ex.toString());
					return false;
				} catch (Error ex) {
					App.debug(ex.toString());
					return false;
				} 

			}
		});

		
		
		
	}
	
	/*
	 * returns a String (base-64 encoded PNG file of the Graphics View)
	 */
	public synchronized String getPNGBase64(double exportScale, boolean transparent, double DPI) {
		BufferedImage img =
				((AppD)app).getEuclidianView1().getExportImage(exportScale, transparent); 

		
	    try {
		    Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
		    ImageWriter writer = it.next();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

		    ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
		    
		    writer.setOutput(ios);
		
		    MyImageIO.writeImage(writer, img, DPI);
		    
			String ret = geogebra.common.util.Base64.encode(baos.toByteArray(), 0);
	    
			baos.close();
			
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	/**
	 * @param JSFunctionName name of logger listener function
	 */
	public synchronized void registerLoggerListener(String JSFunctionName) {
		((AppD)app).getScriptManager().getUSBFunctions().registerLoggerListener(JSFunctionName);
	}

	/**
	 * @param JSFunctionName name of logger listener function
	 */
	public synchronized void unregisterLoggerListener(String JSFunctionName) {
		((AppD)app).getScriptManager().getUSBFunctions().unregisterLoggerListener(JSFunctionName);
	}

	public void drawToImage(String label, double[] x, double[] y) {
		GeoElement ge = kernel.lookupLabel(label);
		
		if(ge == null){
			ge = new GeoImage(kernel.getConstruction());
			if(label == null || label.length()==0){
				ge.setLabel(null);
			}
			else{
				ge.setLabel(label);
			}
		}
		if(!ge.isGeoImage()){
			debug("Bad drawToImage arguments");
			return;
		}
		
		((AppD)app).getEuclidianView1().drawPoints((GeoImage)ge,x,y);
		
	}

	public void clearImage(String label) {
		GeoElement ge = kernel.lookupLabel(label);
		
		if(!ge.isGeoImage()){
			debug("Bad drawToImage arguments");
			return;
		}
		((GeoImage)ge).clearFillImage();
		
	}

	/**
	 * JavaScript-like prompt
	 * @param value0 prompt text
	 * @param value1 default value
	 * @return user's response
	 */
	public String prompt(Object value0, Object value1) {
		return (String)JOptionPane.showInputDialog(
				((AppD)app).getFrame(),
        value0,
        "GeoGebra",
        JOptionPane.PLAIN_MESSAGE,
        null,
        null,
        value1);
	}


	
	/**
	 * pops up message dialog with "OK" and "Stop Script"
	 * 
	 * @param message to display
	 */
	public void alert(String message) {
		Object[] options = {app.getPlain("StopScript"), app.getPlain("OK")};
		int n = JOptionPane.showOptionDialog(((AppD)app).getFrame(),
				message,
			    "GeoGebra",
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,     //do not use a custom Icon
			    options,  //the titles of buttons
			    options[0]); //default button title
		
		if (n == 0) throw new Error("Script stopped by user");
		
	}

	public String getIPAddress() {
		return Util.getIPAddress();
	}

	public String getHostname() {
		return Util.getHostname();
	}

	/**
	 * Returns the dimensions of the real world coordinate system in the graphics view
	 * as [xmin, ymin, width, height]
	 * @return dimensions of the real world coordinate system
	 */
	public synchronized Rectangle2D.Double getCoordSystemRectangle() {
		EuclidianView ev = app.getEuclidianView1();
		return new Rectangle2D.Double(ev.getXmin(), ev.getYmin(), 
				ev.getXmax() - ev.getXmin(), ev.getYmax() - ev.getYmin());
	}
	



		
	
          
}// class GgbAPI

