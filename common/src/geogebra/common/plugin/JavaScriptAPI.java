package geogebra.common.plugin;



/**
 * JavaScript interface of GeoGebra applets.
 * 
 * @author Markus Hohenwarter, Michael Borcherds
 */
public interface JavaScriptAPI {
	
	/**
	 * Returns current construction as a ggb file in form of a byte array.
	 * @return null if something went wrong 
	 */
	public  byte [] getGGBfile();

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public  String getXML();
	public  String getBase64();
	public  String getBase64(boolean includeThumbnail);
	
	public abstract void uploadToGeoGebraTube();
	
	/**
	 * Returns the GeoGebra XML string for the given GeoElement object, 
	 * i.e. only the <element> tag is returned. 
	 */
	public String getXML(String objName);
	
	/**
	 * For a dependent GeoElement objName the XML string of 
	 * the parent algorithm and all its output objects is returned. 
	 * For a free GeoElement objName "" is returned.
	 */
	public String getAlgorithmXML(String objName);
	
	/**
	 * Opens construction given in XML format. May be used for loading constructions.
	 */
	public  void setXML(String xml);
	public  void setBase64(String base64);
	
	/**
	 * Evaluates the given XML string and changes the current construction. 
	 * Note: the construction is NOT cleared before evaluating the XML string. 	 
	 */
	public  void evalXML(String xmlString);

	
	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public boolean evalCommand(String cmdString);

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public boolean evalCommand(String cmdString, boolean waitForResult);

	/**
	 * prints a string to the Java Console
	 */
	public void debug(String string);
	
	/**
	 * Evaluates the given string as if it was entered into MathPiper's 
	 * input text field.
	 */
	//public String evalMathPiper(String cmdString);
	
	/**
	 * Evaluates the given string using the Yacas CAS.
	 * @deprecated: use evalMathPiper() instead
	 */
	//public String evalYacas(String cmdString);

	/**
	 * Turns showing of error dialogs on (true) or (off). 
	 * Note: this is especially useful together with evalCommand().
	 */
	public void setErrorDialogsActive(boolean flag);
	
	/**
	 * Turns on the fly creation of points in graphics view on (true) or (off). 
	 * Note: this is useful if you don't want tools to have the side effect
	 * of creating points. For example, when this flag is set to false, the 
	 * tool "line through two points" will not create points on the fly
	 * when you click on the background of the graphics view. 
	 */
	public void setOnTheFlyPointCreationActive(boolean flag);
	
	public void setUndoPoint();
	
	/**
	 * Resets the initial construction (given in filename parameter) of this applet.	
	 */
	public void reset();
	
	/**
	 * Refreshs all views. Note: clears traces in
	 * geometry window.
	 */
	public void refreshViews();
	
	/** 
	 * returns IP address	 
	 */
	public String getIPAddress();
			
	/**
	 *  returns hostname	 
	 */
	public String getHostname();
			
	/**
	 * Loads a construction from a  file (given URL).	
	 * ...but the actual code is in a thread to avoid JavaScript security issues  
	 */
	public void openFile(String strURL);
	
	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public void setVisible(String objName, boolean visible);
	
	/**
	 * returns true or false depending on whether the object is visible
	 */
	public boolean getVisible(String objName);
	
	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	public void setLayer(String objName, int layer);
	
	/**
	 * Returns the layer of the object with the given name in the geometry window.
	 * returns layer, or -1 if object doesn't exist
	 * Michael Borcherds 2008-02-27
	 */
	public int getLayer(String objName);
	
	/**
	 * Shows or hides a complete layer
	 * Michael Borcherds 2008-02-27
	 */
	public void setLayerVisible(int layer, boolean visible);	

	/**
	 * Sets the fixed state of the object with the given name.
	 */
	public void setFixed(String objName, boolean flag);
	
	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	public void setTrace(String objName, boolean flag);
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public void setLabelVisible(String objName, boolean visible);
	
	/**
	 * Sets the label style of the object with the given name in the geometry window.
	 * Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE = 2.
	 */
	public void setLabelStyle(String objName, int style);
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public void setLabelMode(String objName, boolean visible);
	
	/**
	 * Sets the line thickness of the object with the given name.
	 */
	public void setLineThickness(String objName, int thickness);
	
	/**
	 * Returns the line thickness of the object
	 */
	public int getLineThickness(String objName);
	
	/**
	 * Sets the lineType of the object with the given name.(if possible)
	 */
	public void setLineStyle(String objName, int style);
	
	/**
	 * Returns the lineType of the object 
	 */
	public int getLineStyle(String objName);
	
	/**
	 * Sets the filling of the object with the given name. (if possible)
	 */
	public void setFilling(String objName, double filling);
	
	public String getGraphicsViewCheckSum(String algorithm, String format);

		/**
	 * Returns the filling of the object as an int (or -1 for no filling)
	 */
	public double getFilling(String objName);
	
	/**
	 * Returns the point style of the object as an int (or -1 for default, or not a point)
	 */
	public int getPointStyle(String objName);
	
	/**
	 * Sets the point style of the object (-1 for default)
	 */
	public void setPointSize(String objName, int style);
	
	/**
	 * Returns the point style of the object as an int (or -1 for default, or not a point)
	 */
	public int getPointSize(String objName);
	
	/**
	 * Sets the point style of the object (-1 for default)
	 */
	public void setPointStyle(String objName, int style);
	
	/**
	 * Sets the color of the object with the given name.
	 */
	public void setColor(String objName, int red, int green, int blue);
	
	/**
	 * @param red red part (0-255)
	 * @param green green part (0-255)
	 * @param blue blue part (0-255)
	 */
	public void setPenColor(int red, int green, int blue);
	
	/**
	 * @param size size in pixels
	 */
	public void setPenSize(int size);
	
	/**
	 * 
	 * @return pen size in pixels
	 */
	public int getPenSize();
	
	/**
	 * 
	 * @return pen color as RGB hex string (eg #AB1234)
	 */
	public String getPenColor();
	
	/**
	 * Returns the color of the object as an hex string. Note that the hex-string 
	 * starts with # and uses upper case letters, e.g. "#FF0000" for red.
	 */
	public String getColor(String objName);
	
	/**
	 * Deletes the object with the given name.
	 */
	public void deleteObject(String objName);
	
	/**
	 * Returns true if the object with the given name exists.
	 */
	public boolean exists(String objName);
	
	/**
	 * Renames an object from oldName to newName.
	 * @return whether renaming worked
	 */
	public boolean renameObject(String oldObjName, String newObjName);
	
	/**
	 * Sets whether an object should be animated. This does not start
	 * the animation yet, use startAnimation() to do so.
	 */
	public void setAnimating(String objName, boolean animate);
	
	/**
	 * Sets the animation speed of an object.
	 */
	public void setAnimationSpeed(String objName, double speed);
	
	/**
	 * Starts automatic animation for all objects with the animating flag set.
	 * @see #setAnimating(String, boolean)
	 */
	public void startAnimation();
	
	/**
	 * Stops animation for all objects with the animating flag set.
	 * @see #setAnimating(String, boolean)
	 */
	public void stopAnimation();
	
	/**
	 * Whether or not to show the mouse pointer (cursor) when dragging
	 */
	public void hideCursorWhenDragging(boolean hideCursorWhenDragging);
	
	/**
	 * Returns whether automatic animation is currently running.
	 */
	public boolean isAnimationRunning();
	
	/**
	 * Returns true if the object with the given name has a vaild
	 * value at the moment.
	 */
	public boolean isDefined(String objName);
	
	/**
	 * Returns true if the object with the given name is independent.
	 */
	public boolean isIndependent(String objName);
	
	public boolean isMoveable(String objName);
	/**
	 * Returns the value of the object with the given name as a string.
	 */
	public String getValueString(String objName);
	
	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public String getDefinitionString(String objName);
	
	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public String getCommandString(String objName);
	
	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public double getXcoord(String objName);
	
	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public double getYcoord(String objName);
	
	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 */
	public void setCoords(String objName, double x, double y);
	
	/**
	 * Returns the double value of the object with the given name. Note: returns 0 if
	 * the object does not have a value.
	 */
	public double getValue(String objName);
	
	/**
	 * Sets the double value of the object with the given name. Note: if the
	 * specified object is not a number, nothing happens.
	 */
	public void setValue(String objName, double x);
	
	/**
	 * Turns the repainting of all views on or off.
	 */
	public void setRepaintingActive(boolean flag);
	
	
	public boolean writePNGtoFile(String filename, double exportScale, boolean transparent, double DPI);

	public String getPNGBase64(double exportScale, boolean transparent, double DPI);

		/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	public void setCoordSystem(double xmin, double xmax, double ymin, double ymax);
	
	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics window.
	 */
	public void setAxesVisible(boolean xVisible, boolean yVisible);
	
	/**
	 * Shows or hides the coordinate grid in the graphics window.
	 */
	public void setGridVisible(boolean flag);	
	
	/**
	 * Returns an array with all object names.
	 */
	public String [] getAllObjectNames();
	
	/**
	 * Returns the number of objects in the construction.
	 */
	public int getObjectNumber();
	
	/**
	 * Returns the name of the n-th object of this construction.
	 */
	public String getObjectName(int i);
	
	/**
	 * Returns the type of the object with the given name as a string (e.g. point, line, circle, ...)
	 */
	public String getObjectType(String objName);
	
	/**
	 * Sets the mode of the geometry window (EuclidianView). 
	 */
	public void setMode(int mode);
		
	/**
	 * Registers a JavaScript function as an add listener for the applet's construction.
	 *  Whenever a new object is created in the GeoGebraApplet's construction, the JavaScript 
	 *  function JSFunctionName is called using the name of the newly created object as a single argument. 
	 */
	public void registerAddListener(String JSFunctionName);
	
	/**
	 * Removes a previously registered add listener 
	 * @see #registerAddListener(String) 
	 */
	public void unregisterAddListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript function as a remove listener for the applet's construction.
	 * Whenever an object is deleted in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public void registerRemoveListener(String JSFunctionName);
	
	/**
	 * Removes a previously registered remove listener 
	 * @see #registerRemoveListener(String) 
	 */
	public void unregisterRemoveListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript function as a clear listener for the applet's construction.
	 * Whenever the construction in the GeoGebraApplet's is cleared (i.e. all objects are removed), the JavaScript 
	 * function JSFunctionName is called using no arguments. 	
	 */
	public void registerClearListener(String JSFunctionName) ;
	
	/**
	 * Removes a previously registered clear listener 
	 * @see #registerClearListener(String) 
	 */
	public void unregisterClearListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript function as a rename listener for the applet's construction.
	 * Whenever an object is renamed in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public void registerRenameListener(String JSFunctionName);
	
	/**
	 * Removes a previously registered rename listener.
	 * @see #registerRenameListener(String) 
	 */
	public void unregisterRenameListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript function as an update listener for the applet's construction.
	 * Whenever any object is updated in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the updated object as a single argument. 	
	 */
	public void registerUpdateListener(String JSFunctionName);
	
	/**
	 * Removes a previously registered update listener.
	 * @see #registerRemoveListener(String) 
	 */
	public void unregisterUpdateListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript update listener for an object. Whenever the object with 
	 * the given name changes, a JavaScript function named JSFunctionName 
	 * is called using the name of the changed object as the single argument. 
	 * If objName previously had a mapping JavaScript function, the old value 
	 * is replaced.
	 * 
	 * Example: First, set a change listening JavaScript function:
	 * ggbApplet.setChangeListener("A", "myJavaScriptFunction");
	 * Then the GeoGebra Applet will call the Javascript function
	 * myJavaScriptFunction("A");
	 * whenever object A changes.	
	 */
	public void registerObjectUpdateListener(String objName, String JSFunctionName);
	
	/**
	 * Removes a previously set change listener for the given object.
	 * @see #setChangeListener
	 */
	public void unregisterObjectUpdateListener(String objName);
	
	/**
	 * Registers a JavaScript function as an click listener for the applet's construction.
	 * Whenever any object is clicked in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the updated object as a single argument. 	
	 */
	public void registerClickListener(String JSFunctionName);
	
	/**
	 * Removes a previously registered Click listener.
	 */
	public void unregisterClickListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript Click listener for an object. Whenever the object with 
	 * the given name changes, a JavaScript function named JSFunctionName 
	 * is called using the name of the changed object as the single argument. 
	 * If objName previously had a mapping JavaScript function, the old value 
	 * is replaced.
	 */
	public void registerObjectClickListener(String objName, String JSFunctionName);
	
	/**
	 * Removes a previously set change listener for the given object.
	 * @see #setChangeListener
	 */
	public void unregisterObjectClickListener(String objName);

	public void drawToImage(String label,double[] x, double[] y);
	
	public void clearImage(String label);


	/** 
	 * Sets the double value of the specified index of the list.  
	 * Can be used to extend the size of a list 
	 */ 
	public void setListValue(String objName, int index, double x); 

	/** 
	 * Gets the double value of the specified index of the list.  
	 *  
	 * Returns Double.NaN if the object is not a GeoNumeric/Angle 
	 */ 
	public double getListValue(String objName, int index); 

}
