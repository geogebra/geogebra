package geogebra.plugin;
/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.AbstractApplication;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.io.MyImageIO;
import geogebra.main.Application;

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

public class GgbAPI extends geogebra.common.plugin.GgbAPI{

	private Application         app=                null;   //References ...
   
   /** Constructor:
    *  Makes the api with a reference to the GeoGebra program.
    *  Called from GeoGebra.
    *  @param app Application
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
			return geogebra.common.util.Base64.encode(baos.toByteArray(), 0);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
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
			zipFile = geogebra.common.util.Base64.decode(base64);
		} catch (IOException e) {
			
			e.printStackTrace();
			return;
		}
		app.loadXML(zipFile);
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
			return ((GeoPoint2) geo).inhomX;
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
			return ((GeoPoint2) geo).inhomY;
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
			((GeoPoint2) geo).setCoords(x, y, 1);
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
			((GeoBoolean) geo).setValue(Kernel.isZero(x) ? false : true);
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
		app.getEuclidianView().setShowAxis(EuclidianViewInterface.AXIS_X, xVisible, false);
		app.getEuclidianView().setShowAxis(EuclidianViewInterface.AXIS_Y, yVisible, false);
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
	
		
	/**
	 * Returns an array with the names of all selected objects.
	 */
	public synchronized String [] getSelectedObjectNames() {			
		ArrayList<GeoElement> selGeos = app.getSelectedGeos();
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
		if (!Application.hasFullPermissions()) return false;
		final File file = new File(filename);

		if (file == null) return false;

		return (Boolean) AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Boolean run() {

				try {			
					// draw graphics view into image
					BufferedImage img =
						app.getEuclidianView().getExportImage(exportScale, transparent); 
					
					// write image to file
					MyImageIO.write(img, "png", (float)DPI,  file);	
					
					return true;
				} catch (Exception ex) {
					AbstractApplication.debug(ex.toString());
					return false;
				} catch (Error ex) {
					AbstractApplication.debug(ex.toString());
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
		    Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
		    ImageWriter writer = (ImageWriter) it.next();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

		    ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
		    
		    writer.setOutput(ios);
		
		    MyImageIO.writeImage(writer, img, DPI);
		    
		    byte[] image = baos.toByteArray();
			String ret = geogebra.common.util.Base64.encode(baos.toByteArray(), 0);
	    
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
	
	public synchronized void registerPenListener(String JSFunctionName) {
		app.getScriptManager().registerPenListener(JSFunctionName);
	}
	
	public synchronized void unregisterPenListener(String JSFunctionName) {
		app.getScriptManager().unregisterPenListener(JSFunctionName);
	}

	public boolean isMoveable(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isMoveable();
	}

	public void drawToImage(String label, double[] x, double[] y) {
		GeoElement ge = kernel.lookupLabel(label);
		
		if(ge == null){
			ge = new GeoImage(kernel.getConstruction());
			if(label.length()==0)
				label = null;
			ge.setLabel(label);
		}
		if(!ge.isGeoImage()){
			debug("Bad drawToImage arguments");
			return;
		}
		
		app.getEuclidianView().drawPoints((GeoImage)ge,x,y);
		
	}

	public void clearImage(String label) {
		GeoElement ge = kernel.lookupLabel(label);
		
		if(!ge.isGeoImage()){
			debug("Bad drawToImage arguments");
			return;
		}
		((GeoImage)ge).clearFillImage();
		
	}

	public String prompt(Object value0, Object value1) {
		return (String)JOptionPane.showInputDialog(
        app.getFrame(),
        value0,
        "GeoGebra",
        JOptionPane.PLAIN_MESSAGE,
        null,
        null,
        value1);
	}

	


		
	
          
}// class GgbAPI

