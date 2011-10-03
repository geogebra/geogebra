package geogebra.plugin;
/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */
import geogebra.GeoGebra;
import geogebra.cas.GeoGebraCAS;
import geogebra.euclidian.EuclidianView;
import geogebra.io.MyImageIO;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.PointProperties;
import geogebra.kernel.Traceable;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.main.Application;

import java.awt.Color;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;


/** 
<h3>GgbAPI - API for PlugLets </h3>
<pre>
   The Api the plugin program can use.
</pre>
<ul><h4>Interface:</h4>
<li>GgbAPI(Allication)      //Application owns it
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

public class GgbAPI {

    ///// ----- Properties ----- /////
    private Application         app=                null;   //References ...
    private Kernel              kernel=             null;
    private Construction        construction=       null;
    private AlgebraProcessor    algebraprocessor=   null;
   // private PluginManager       pluginmanager=      null;    
    ///// ----- Interface ----- /////
   
   /** Constructor:
    *  Makes the api with a reference to the GeoGebra program.
    *  Called from GeoGebra.
    */
    public GgbAPI(Application app) {
        this.app=app;
        kernel=app.getKernel();
        algebraprocessor=kernel.getAlgebraProcessor();
        construction=kernel.getConstruction();
    //    pluginmanager=app.getPluginManager();
    }//Constructor
    
    /** Returns reference to Application */
    public Application getApplication(){return this.app;}
    
    /** Returns reference to Construction */
    public Construction getConstruction(){return this.construction;}
    
    /** Returns reference to Kernel */
    public Kernel getKernel(){return this.kernel;}
    
    /** Returns reference to AlgebraProcessor */
    public AlgebraProcessor getAlgebraProcessor(){return this.algebraprocessor;}

    /** Returns reference to PluginManager */
//    public PluginManager getPluginManager() {
//    	if(pluginmanager==null){
//    		this.pluginmanager=app.getPluginManager();
//    	}//if not initialized
//    	return this.pluginmanager;
//    }//getPluginManager()

    /** Returns reference to ClassPathManipulator*/
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
			app.getXMLio().writeGeoGebraFile(bos, true);
			bos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public synchronized String getXML() {
		return app.getXML();
	}
	
	/**
	 * Returns current construction in Base64 format. May be used for saving.
	 */
	public synchronized String getBase64(boolean includeThumbnail) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			app.getXMLio().writeGeoGebraFile(baos, includeThumbnail);
			return geogebra.util.Base64.encode(baos.toByteArray(), 0);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the GeoGebra XML string for the given GeoElement object, 
	 * i.e. only the <element> tag is returned. 
	 */
	public synchronized String getXML(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return "";	
		else {
			//if (geo.isIndependent()) removed as we want a way to get the <element> tag for all objects
				return geo.getXML();
			//else
			//	return "";
		}
	}
	
	/**
	 * For a dependent GeoElement objName the XML string of 
	 * the parent algorithm and all its output objects is returned. 
	 * For a free GeoElement objName "" is returned.
	 */
	public synchronized String getAlgorithmXML(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return "";	
		else {
			if (geo.isIndependent())
				return "";
			else
				return geo.getParentAlgorithm().getXML();
		}
	}	
	
	/**
	 * Opens construction given in XML format. May be used for loading constructions.
	 */
	public synchronized void setXML(String xml) {
		app.setXML(xml, true);
	}
	
	/**
	 * Opens construction given in XML format. May be used for loading constructions.
	 */
	public synchronized void setBase64(String base64) {
		byte[] zipFile;
		try {
			zipFile = geogebra.util.Base64.decode(base64);
		} catch (IOException e) {
			
			e.printStackTrace();
			return;
		}
		app.loadXML(zipFile);
	}
	
	/**
	 * Evaluates the given XML string and changes the current construction. 
	 * Note: the construction is NOT cleared before evaluating the XML string. 	 
	 */
	public synchronized void evalXML(String xmlString) {		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\">\n");
		sb.append("<construction>\n");
		sb.append(xmlString);
		sb.append("</construction>\n");
		sb.append("</geogebra>\n");
		app.setXML(sb.toString(), false);
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public synchronized boolean evalCommand(String cmdString) {
		
		//Application.debug("evalCommand called..."+cmdString);
		GeoElement [] result;
		
		if (cmdString.indexOf('\n') == -1) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(cmdString, false);
			// return success
			return result != null;
			
		}

		boolean ret = true;
		String[] cmdStrings = cmdString.split("[\\n]+");
		for (int i = 0 ; i < cmdStrings.length ; i++) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(cmdStrings[i], false);
			ret = ret & (result != null);
		}
		
		return ret;
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
	
	/**
	 * Evaluates the given string as if it was entered into GeoGebra CAS's
	 * input text field.  
	 * @return evaluation result in GeoGebraCAS syntax
	 */
	public synchronized String evalGeoGebraCAS(String cmdString){
		return evalGeoGebraCAS(cmdString, false);		
	}
		
	/**
	 * Evaluates the given string as if it was entered into GeoGebra CAS's
	 * input text field. 
	 * @param debugOutput states whether debugging information should be printed to the console
	 * @return evaluation result in GeoGebraCAS syntax
	 */
	public synchronized String evalGeoGebraCAS(String cmdString, boolean debugOutput) {
		String ret="";
		GeoGebraCAS	ggbcas=(GeoGebraCAS)kernel.getGeoGebraCAS();
		try{
			ret= ggbcas.evaluateGeoGebraCAS(cmdString);
		}catch(Throwable t){
			Application.debug(t.toString());
		}//try-catch
		
		// useful for debugging JavaScript
		if (debugOutput)
			Application.debug("evalGeoGebraCAS\n input:"+cmdString+"\n"+"output: "+ret);
		return ret;
	}//evalGeoGebraCAS(String)
	
	
	/**
	 * prints a string to the Java Console
	 */
	public synchronized void debug(String string) {
		
		Application.debug(string);
	}

	/**
	 * Turns showing of error dialogs on (true) or (off). 
	 * Note: this is especially useful together with evalCommand().
	 */
	public synchronized void setErrorDialogsActive(boolean flag) {
		app.setErrorDialogsActive(flag);
	}
	
	/**
	 * Resets the initial construction (given in filename parameter) of this applet.	
	 * ...but the actual code is in a thread to avoid JavaScript security issues 
	 */
	public synchronized void reset() {
		
		//rewrite in this context
	}
	
	/**
	 * Clears the construction and resets all views.
	 */
	public synchronized void fileNew() {
		app.fileNew();
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
			String lowerCase = strURL.toLowerCase(Locale.US);
			URL url = new URL(strURL);
			app.loadXML(url, lowerCase.endsWith(Application.FILE_EXT_GEOGEBRA_TOOL));
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
	
	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized void setVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setEuclidianVisible(visible);
		geo.updateRepaint();
	}
	
	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized boolean getVisible(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return false;		
		return (geo.isEuclidianVisible());
	}
	
	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayer(String objName, int layer) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLayer(layer);		
		geo.updateRepaint();
	}
	
	/**
	 * Returns the layer of the object with the given name in the geometry window.
	 * returns layer, or -1 if object doesn't exist
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized int getLayer(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		return geo.getLayer();		
	}
	
	/**
	 * Shows or hides a complete layer
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayerVisible(int layer, boolean visible) {
		if (layer<0 || layer > EuclidianView.MAX_LAYERS) return;
		String [] names = getObjNames();
		for (int i=0 ; i < names.length ; i++)
		{
			GeoElement geo = kernel.lookupLabel(names[i]);
			if (geo != null) if (geo.getLayer() == layer)
			{
				geo.setEuclidianVisible(visible);		
				geo.updateRepaint();
			}
		}	
	}
	
	

	/**
	 * Sets the fixed state of the object with the given name.
	 */
	public synchronized void setFixed(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null && geo.isFixable()) {		
			geo.setFixed(flag);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	public synchronized void setTrace(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null && geo.isTraceable()) {		
			((Traceable)geo).setTrace(flag);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public synchronized void setLabelVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLabelVisible(visible);		
		geo.updateRepaint();
	}
	
	/**
	 * Sets the label style of the object with the given name in the geometry window.
	 * Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE = 2.
	 */
	public synchronized void setLabelStyle(String objName, int style) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLabelMode(style);		
		geo.updateRepaint();
	}
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public synchronized void setLabelMode(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLabelVisible(visible);
		geo.updateRepaint();
	}
	
	/**
	 * Sets the color of the object with the given name.
	 */
	public synchronized void setColor(String objName, int red, int green, int blue) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		Color col = new Color(red, green, blue);		
		geo.setObjColor(col);
		geo.updateRepaint();
	}	
	
	/**
	 * Starts/stops an object animating
	 */
	public void setAnimating(String objName, boolean animate) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null) 
			geo.setAnimating(animate);					
	}
	
	/**
	 * Sets the animation speed of an object
	 */
	public void setAnimationSpeed(String objName, double speed) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null) {
			geo.setAnimationSpeed(speed);
		}
	}
	

	
	/**
	 * Returns the color of the object as an hex string. Note that the hex-string 
	 * starts with # and uses upper case letters, e.g. "#FF0000" for red.
	 */
	public synchronized String getColor(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return "#" + geogebra.util.Util.toHexString(geo.getObjectColor());		
	}	
	
	public synchronized int getLineThickness(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		return geo.getLineThickness();		
	}	
	
	public synchronized void setLineThickness(String objName, int thickness) {
		if (thickness == -1) thickness = EuclidianView.DEFAULT_LINE_THICKNESS;
		if (thickness < 1 || thickness > 13) return;
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLineThickness(thickness);		
		geo.updateRepaint();
	}	
	
	public synchronized int getPointStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).getPointStyle();	
		else
			return -1;
	}	
	
	public synchronized void setPointStyle(String objName, int style) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;	
		if (geo instanceof PointProperties) {
			((PointProperties) geo).setPointStyle(style);		
			geo.updateRepaint();
		}
	}	
	
	public synchronized int getPointSize(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).getPointSize();	
		else
			return -1;
	}	
	
	public synchronized void setPointSize(String objName, int style) {
		if (style < 1 || style > 9) return;
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;	
		if (geo.isGeoPoint()) {
			((GeoPoint) geo).setPointSize(style);
			geo.updateRepaint();
		}
	}	
	
	public synchronized double getFilling(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		return geo.getAlphaValue();		
	}	
	
	public synchronized void setFilling(String objName, double filling) {
		if (filling < 0.0 || filling > 1.0)
			return;
		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		

		geo.setAlphaValue((float)filling);
		geo.updateRepaint();
	}	
	
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
				app.getEuclidianView().getExportImage(1);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, format.toLowerCase(Locale.US), baos);
			
			MessageDigest md5 = getMessageDigestMD5();
			byte[] bytesOut = baos.toByteArray();
			md5.update(bytesOut);
			byte[] md5hash = new byte[32];
			md5hash = md5.digest();
			return Application.convertToHex(md5hash);	

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
	
	private static MessageDigest messageDigestMD5 = null;

	public static MessageDigest getMessageDigestMD5()
			throws NoSuchAlgorithmException {
		if (messageDigestMD5 == null)
			messageDigestMD5 = MessageDigest.getInstance("MD5");

		return messageDigestMD5;
	}
	

	public synchronized int getLineStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		int type = geo.getLineType();	
		
		// convert from 0,10,15,20,30
		// to 0,1,2,3,4
		
		Integer[] types = EuclidianView.getLineTypes();
		for (int i = 0 ; i < types.length ; i++) {
			if (type == types[i].intValue())
				return i;
		}
		
		return -1; // unknown type
	}	
	
	public synchronized void setLineStyle(String objName, int style) {
		Integer[] types = EuclidianView.getLineTypes();
		
		if (style < 0 || style >= types.length)
			return;
		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		
		geo.setLineType(types[style].intValue());
		geo.updateRepaint();
	}	
	
	/**
	 * Deletes the object with the given name.
	 */
	public synchronized void deleteObject(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.remove();
		kernel.notifyRepaint();
	}	
	
	/**
	 * Renames an object from oldName to newName.
	 * @return whether renaming worked
	 */
	public synchronized boolean renameObject(String oldName, String newName) {		
		GeoElement geo = kernel.lookupLabel(oldName);
		if (geo == null) 
			return false;
		
		// try to rename
		boolean success = geo.rename(newName);
		kernel.notifyRepaint();
		
		return success;
	}	
	
	/**
	 * Returns true if the object with the given name exists.
	 */
	public synchronized boolean exists(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo != null);				
	}	
	
	/**
	 * Returns true if the object with the given name has a vaild
	 * value at the moment.
	 */
	public synchronized boolean isDefined(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isDefined();
	}	
	
	/**
	 * Returns true if the object with the given name is independent.
	 */
	public synchronized boolean isIndependent(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isIndependent();
	}	
	
	/**
	 * Returns the value of the object with the given name as a string.
	 */
	public synchronized String getValueString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";	
		
		if (geo.isGeoText())
			return ((GeoText)geo).getTextString();
		
		return geo.getAlgebraDescription();
	}
	
	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public synchronized String getDefinitionString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getDefinitionDescription();
	}
	
	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public synchronized String getCommandString(String objName) {		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getCommandDescription();
	}
	
	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getXcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return 0;
		
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).inhomX;
		else if (geo.isGeoVector())
			return ((GeoVector) geo).x;
		else
			return 0;
	}
	
	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getYcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return 0;
		
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).inhomY;
		else if (geo.isGeoVector())
			return ((GeoVector) geo).y;
		else
			return 0;
	}
	
	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 */
	public synchronized void setCoords(String objName, double x, double y) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;
		
		if (geo.isGeoPoint()) {
			((GeoPoint) geo).setCoords(x, y, 1);
			geo.updateRepaint();
		}
		else if (geo.isGeoVector()) {
			((GeoVector) geo).setCoords(x, y, 0);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Returns the double value of the object with the given name.
	 * For a boolean, returns 0 for false, 1 for true
	 * Note: returns 0 if the object does not have a value.
	 */
	public synchronized double getValue(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return 0;
		
		if (geo.isNumberValue())
			return ((NumberValue) geo).getDouble();		
		else if (geo.isGeoBoolean())
			return ((GeoBoolean) geo).getBoolean() ? 1 : 0;		
		
		return 0;
	}
	
	/**
	 * Sets the double value of the object with the given name.
	 * For a boolean 0 -> false, any other value -> true
	 * Note: if the specified object is not a number, nothing happens.
	 */
	public synchronized void setValue(String objName, double x) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null || !geo.isIndependent()) return;
		
		if (geo.isGeoNumeric()) {
			((GeoNumeric) geo).setValue(x);
			geo.updateRepaint();
		} else if (geo.isGeoBoolean()) {
			((GeoBoolean) geo).setValue(kernel.isZero(x) ? false : true);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Turns the repainting of all views on or off.
	 */
	public synchronized void setRepaintingActive(boolean flag) {		
		//Application.debug("set repainting: " + flag);
		kernel.setNotifyRepaintActive(flag);
	}	
	

	/*
	 * Methods to change the geometry window's properties	 
	 */
	
	/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	public synchronized void setCoordSystem(double xmin, double xmax, double ymin, double ymax) {
		app.getEuclidianView().setRealWorldCoordSystem(xmin, xmax, ymin, ymax);
	}
	
	/**
	 * Returns the dimensions of the real world coordinate system in the graphics view
	 * as [xmin, ymin, width, height]
	 */
	public synchronized Rectangle2D.Double getCoordSystemRectangle() {
		EuclidianView ev = app.getEuclidianView();
		return new Rectangle2D.Double(ev.getXmin(), ev.getYmin(), 
				ev.getXmax() - ev.getXmin(), ev.getYmax() - ev.getYmin());
	}
	
	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics window.
	 */
	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {		
		app.getEuclidianView().setShowAxis(EuclidianView.AXIS_X, xVisible, false);
		app.getEuclidianView().setShowAxis(EuclidianView.AXIS_Y, yVisible, false);
		kernel.notifyRepaint();
	}	
	
	/**
	 * If the origin is off screen and the axes are visible, GeoGebra shows coordinates
	 * of the upper-left and bottom-right screen corner. This method lets you
	 * hide these corner coordinates.
	 */
	public synchronized void setAxesCornerCoordsVisible(boolean showAxesCornerCoords) {		
		app.getEuclidianView().setAxesCornerCoordsVisible(showAxesCornerCoords);
	}	
	
	/**
	 * Shows or hides the coordinate grid in the graphics window.
	 */
	public synchronized void setGridVisible(boolean flag) {		
		app.getSettings().getEuclidian(1).showGrid(flag);
		app.getSettings().getEuclidian(2).showGrid(flag);
	}
	
	/*
	 * Methods to get all object names of the construction 
	 */
	
	private String [] objNames;
	public int lastGeoElementsIteratorSize = 0;		//ulven 29.05.08: Had to change to public, used by applet
	
	/**
	 * 
	 * @return
	 */
	public String [] getObjNames() {			//ulven 29.05.08: Had to change to public, used by applet

		Construction cons = kernel.getConstruction();
		TreeSet geoSet =  cons.getGeoSetConstructionOrder();
		int size = geoSet.size();
		
		/* removed Michael Borcherds 2009-02-09
		 * BUG!
		 *
		// don't build objNames if nothing changed
		if (size == lastGeoElementsIteratorSize)
			return objNames;		
			*/
		
		// build objNames array
		lastGeoElementsIteratorSize = size;		
		objNames = new String[size];
				
		int i=0; 
		Iterator it = geoSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			objNames[i] = geo.getLabel();
			i++;
		}
		return objNames;
		
	}
	
	/**
	 * Returns an array with all object names.
	 */
	public synchronized String [] getAllObjectNames() {			
		return getObjNames();
	}	
	
	/**
	 * Returns an array with the names of all selected objects.
	 */
	public synchronized String [] getSelectedObjectNames() {			
		ArrayList selGeos = app.getSelectedGeos();
		String [] objNames = new String[selGeos.size()];
		
		for (int i=0; i < selGeos.size(); i++) {
			GeoElement geo = (GeoElement) selGeos.get(i);
			objNames[i] = geo.getLabel();
		}
		return objNames;
	}	
	
	/**
	 * Returns the number of objects in the construction.
	 */
	public synchronized int getObjectNumber() {					
		return getObjNames().length;			
	}	
	
	/**
	 * Returns the name of the n-th object of this construction.
	 */
	public synchronized String getObjectName(int i) {					
		String [] names = getObjNames();
					
		try {
			return names[i];
		} catch (Exception e) {
			return "";
		}
	}
	
	
	/*
	 * saves a PNG file
	 * signed applets only
	 */
	public synchronized boolean writePNGtoFile(String filename, final double exportScale, final boolean transparent, final double DPI) {
		if (!app.hasFullPermissions()) return false;
		final File file = new File(filename);

		if (file == null) return false;

		return (Boolean) AccessController.doPrivileged(new PrivilegedAction() {
			public Boolean run() {

				try {			
					// draw graphics view into image
					BufferedImage img =
						app.getEuclidianView().getExportImage(exportScale, transparent); 
					
					// write image to file
					MyImageIO.write(img, "png", (float)DPI,  file);	
					
					return true;
				} catch (Exception ex) {
					Application.debug(ex.toString());
					return false;
				} catch (Error ex) {
					Application.debug(ex.toString());
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
			app.getEuclidianView().getExportImage(exportScale, transparent); 

		
	    try {
		    Iterator it = ImageIO.getImageWritersByFormatName("png");
		    ImageWriter writer = (ImageWriter) it.next();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

		    ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
		    
		    writer.setOutput(ios);
		
		    MyImageIO.writeImage(writer, img, DPI);
		    
		    byte[] image = baos.toByteArray();
			String ret = geogebra.util.Base64.encode(baos.toByteArray(), 0);
	    
			baos.close();
			
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Returns the type of the object with the given name as a string (e.g. point, line, circle, ...)
	 */
	public synchronized String getObjectType(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo == null) ? "" : geo.getObjectType().toLowerCase(Locale.US);
	}
	
	/**
	 * Sets the mode of the geometry window (EuclidianView). 
	 */
	public synchronized void setMode(int mode) {
		app.setMode(mode);
	}	
	
	
	
	public synchronized void registerLoggerListener(String JSFunctionName) {
		app.getScriptManager().getUSBFunctions().registerLoggerListener(JSFunctionName);
	}

	public synchronized void unregisterLoggerListener(String JSFunctionName) {
		app.getScriptManager().getUSBFunctions().unregisterLoggerListener(JSFunctionName);
	}
	
	public synchronized void registerAddListener(String JSFunctionName) {
		app.getScriptManager().registerAddListener(JSFunctionName);
	}

	public synchronized void unregisterAddListener(String JSFunctionName) {
		app.getScriptManager().unregisterAddListener(JSFunctionName);
	}
	
	public synchronized void registerRemoveListener(String JSFunctionName) {
		app.getScriptManager().registerRemoveListener(JSFunctionName);
	}
	
	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		app.getScriptManager().unregisterRemoveListener(JSFunctionName);
	}
	
	public synchronized void registerClearListener(String JSFunctionName) {
		app.getScriptManager().registerClearListener(JSFunctionName);
	}

	public synchronized void unregisterClearListener(String JSFunctionName) {
		app.getScriptManager().unregisterClearListener(JSFunctionName);
	}

	public synchronized void registerRenameListener(String JSFunctionName) {
		app.getScriptManager().registerRenameListener(JSFunctionName);
	}
	
	public synchronized void unregisterRenameListener(String JSFunctionName) {
		app.getScriptManager().unregisterRenameListener(JSFunctionName);
	}
	
	public synchronized void registerUpdateListener(String JSFunctionName) {
		app.getScriptManager().registerUpdateListener(JSFunctionName);
	}
	
	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		app.getScriptManager().unregisterUpdateListener(JSFunctionName);
	}

	public synchronized void registerObjectUpdateListener(String objName, String JSFunctionName) {
		app.getScriptManager().registerObjectUpdateListener(objName, JSFunctionName);
	}
	
	public synchronized void unregisterObjectUpdateListener(String objName) {
		app.getScriptManager().unregisterObjectUpdateListener(objName);
	}

	public boolean isMoveable(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isMoveable();
	}


		
	
          
}// class GgbAPI

