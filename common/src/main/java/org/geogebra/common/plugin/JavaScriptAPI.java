package org.geogebra.common.plugin;

/**
 * JavaScript interface of GeoGebra applets.
 * 
 * @author Markus Hohenwarter, Michael Borcherds
 */
public interface JavaScriptAPI {

	/**
	 * Returns current construction as a ggb file in form of a byte array.
	 * 
	 * @return null if something went wrong
	 */
	byte[] getGGBfile();

	/**
	 * Returns current construction in XML format. May be used for saving.
	 * 
	 * @return XML representation of construction
	 */
	String getXML();

	/**
	 * @return XML representation of perspective
	 */
	String getPerspectiveXML();

	/**
	 * @return base64 representation of current file
	 */
	String getBase64();

	/**
	 * @param includeThumbnail
	 *            whether thumbnail should be included
	 * @return base64 representation of current file
	 */
	String getBase64(boolean includeThumbnail);

	void uploadToGeoGebraTube();

	/**
	 * Returns the GeoGebra XML string for the given GeoElement object, i.e.
	 * only the &lt;element&gt; tag is returned.
	 * 
	 * @param objName
	 *            object name
	 * @return style XML
	 */
	String getXML(String objName);

	/**
	 * For a dependent GeoElement objName the XML string of the parent algorithm
	 * and all its output objects is returned. For a free GeoElement objName ""
	 * is returned.
	 * 
	 * @param objName
	 *            object name
	 * 
	 * @return algorithm XML
	 */
	String getAlgorithmXML(String objName);

	/**
	 * Opens construction given in XML format. May be used for loading
	 * constructions.
	 * 
	 * @param xml
	 *            construction XML
	 */
	void setXML(String xml);

	/**
	 * Loads encoded file into the applet
	 * 
	 * @param base64
	 *            base64 encoded content
	 */
	void setBase64(String base64);

	/**
	 * Evaluates the given XML string and changes the current construction.
	 * Note: the construction is NOT cleared before evaluating the XML string.
	 * 
	 * @param xmlString
	 *            (partial) construction XML
	 */
	void evalXML(String xmlString);

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's input
	 * text field.
	 * 
	 * @param cmdString
	 *            command
	 * @return whether execution was successful
	 */
	boolean evalCommand(String cmdString);

	/**
	 * Runs command in CAS without checking GeoGebra variables
	 * 
	 * @param cmdString
	 *            CAS command
	 * @return CAS result
	 */
	String evalCommandCAS(String cmdString);

	/**
	 * Runs command in CAS, all variables are substituted by GeoGebra objects
	 * 
	 * @param cmdString
	 *            CAS command
	 * @return CAS result
	 */
	String evalGeoGebraCAS(String cmdString);

	/**
	 * prints a string to the JavaScript / Java Console
	 * 
	 * @param string
	 *            string to be printed in console
	 */
	void debug(String string);

	/**
	 * Turns showing of error dialogs on (true) or (off). Note: this is
	 * especially useful together with evalCommand().
	 */
	void setErrorDialogsActive(boolean flag);

	/**
	 * @param objName object name
	 * @return internal filename of the fill image
	 */
	String getImageFileName(String objName);

	/**
	 * Turns on the fly creation of points in graphics view on (true) or (off).
	 * Note: this is useful if you don't want tools to have the side effect of
	 * creating points. For example, when this flag is set to false, the tool
	 * "line through two points" will not create points on the fly when you
	 * click on the background of the graphics view.
	 */
	void setOnTheFlyPointCreationActive(boolean flag);

	void setUndoPoint();

	/**
	 * Resets the initial construction (given in filename parameter) of this
	 * applet.
	 */
	void reset();

	/**
	 * Refreshs all views. Note: clears traces in geometry window.
	 */
	void refreshViews();

	/**
	 * Loads a construction from a file (given URL). ...but the actual code is
	 * in a thread to avoid JavaScript security issues
	 */
	void openFile(String strURL);

	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	void setVisible(String objName, boolean visible);

	/**
	 * returns true or false depending on whether the object is visible
	 * 
	 * @param objName
	 *            object label
	 * @return whether object is visible
	 */
	boolean getVisible(String objName);

	/**
	 * @param objName
	 *            object name
	 * @param view
	 *            graphics view ID: 1 or 2
	 * @return whether object is visible in given view
	 */
	boolean getVisible(String objName, int view);

	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	void setLayer(String objName, int layer);

	/**
	 * Returns the layer of the object with the given name in the geometry
	 * window. returns layer, or -1 if object doesn't exist Michael Borcherds
	 * 2008-02-27
	 * 
	 * @param objName
	 *            object label
	 * @return layer index or -1
	 */
	int getLayer(String objName);

	/**
	 * Shows or hides a complete layer Michael Borcherds 2008-02-27
	 * 
	 * @param layer
	 *            layer index
	 * @param visible
	 *            visibility flag
	 */
	void setLayerVisible(int layer, boolean visible);

	/**
	 * Sets the fixed state of the object with the given name.
	 * 
	 * @param objName
	 *            object name
	 * @param fixed
	 *            whether the object should be fixed
	 */
	void setFixed(String objName, boolean fixed);

	/**
	 * Sets the fixed state of the object with the given name.
	 * 
	 * @param objName
	 *            object name
	 * @param fixed
	 *            whether it should be fixed
	 * @param selectionAllowed
	 *            whether selection should be allowed
	 */
	void setFixed(String objName, boolean fixed, boolean selectionAllowed);

	void setAuxiliary(String objName, boolean flag);

	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	void setTrace(String objName, boolean flag);

	/**
	 * @param objName
	 *            object name
	 * @return whether the trace for given object is on
	 */
	boolean isTracing(String objName);

	/**
	 * Shows or hides the label of the object with the given name in the
	 * geometry window.
	 */
	void setLabelVisible(String objName, boolean visible);

	/**
	 * @param objName
	 *            object label
	 * @return whether its label is visible
	 */
	boolean getLabelVisible(String objName);

	/**
	 * Sets the label style of the object with the given name in the geometry
	 * window.
	 * 
	 * @param objName
	 *            object label
	 * @param style
	 *            Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE =
	 *            2.
	 */
	void setLabelStyle(String objName, int style);

	/**
	 * Returns labeling style of the object
	 * 
	 * @param objName
	 *            object label
	 * @return labeling style
	 */
	int getLabelStyle(String objName);

	/**
	 * Sets the line thickness of the object with the given name.
	 */
	void setLineThickness(String objName, int thickness);

	/**
	 * Returns the line thickness of the object
	 * 
	 * @param objName
	 *            object label
	 * @return line thickness
	 */
	int getLineThickness(String objName);

	/**
	 * Sets the lineType of the object with the given name.(if possible)
	 */
	void setLineStyle(String objName, int style);

	/**
	 * Returns the lineType of the object
	 * 
	 * @param objName
	 *            object label
	 * @return line style
	 */
	int getLineStyle(String objName);

	/**
	 * Sets the filling of the object with the given name. (if possible)
	 */
	void setFilling(String objName, double filling);

	/**
	 * Returns the filling of the object as an int (or -1 for no filling)
	 * 
	 * @param objName
	 *            object label
	 * @return the filling of the object as an int (or -1 for no filling)
	 */
	double getFilling(String objName);

	/**
	 * Returns the point style of the object as an int (or -1 for default, or
	 * not a point)
	 * 
	 * @param objName
	 *            object label
	 * @return the point style of the object as an int (or -1 for default, or
	 *         not a point)
	 */
	int getPointStyle(String objName);

	/**
	 * Sets the point style of the object (-1 for default)
	 * 
	 * @param objName
	 *            object label
	 * @param style
	 *            point style
	 */
	void setPointSize(String objName, int style);

	/**
	 * Returns the point style of the object as an int (or -1 for default, or
	 * not a point)
	 * 
	 * @param objName
	 *            object label
	 * @return point size
	 */
	int getPointSize(String objName);

	/**
	 * Sets the point style of the object (-1 for default)
	 * 
	 * @param objName
	 *            object label
	 * @param style
	 *            point style
	 */
	void setPointStyle(String objName, int style);

	/**
	 * Sets the color of the object with the given name.
	 * 
	 * @param objName
	 *            object label
	 * @param red
	 *            red part (0-255)
	 * @param green
	 *            green part (0-255)
	 * @param blue
	 *            blue part (0-255)
	 */
	void setColor(String objName, int red, int green, int blue);

	/**
	 * @param red
	 *            red part (0-255)
	 * @param green
	 *            green part (0-255)
	 * @param blue
	 *            blue part (0-255)
	 */
	void setPenColor(int red, int green, int blue);

	/**
	 * @param size
	 *            size in pixels
	 */
	void setPenSize(int size);

	/**
	 * 
	 * @return pen size in pixels
	 */
	int getPenSize();

	/**
	 * 
	 * @return pen color as RGB hex string (eg #AB1234)
	 */
	String getPenColor();

	/**
	 * Returns the color of the object as an hex string. Note that the
	 * hex-string starts with # and uses upper case letters, e.g. "#FF0000" for
	 * red.
	 * 
	 * @param objName
	 *            object label
	 * @return hex color
	 */
	String getColor(String objName);

	/**
	 * Deletes the object with the given name.
	 */
	void deleteObject(String objName);

	/**
	 * Returns true if the object with the given name exists.
	 * 
	 * @param objName
	 *            object label
	 * @return whether object exists
	 */
	boolean exists(String objName);

	/**
	 * Renames an object from oldName to newName.
	 * 
	 * @return whether renaming worked
	 */
	boolean renameObject(String oldObjName, String newObjName,
			boolean forceRename);

	/**
	 * Renames an object from oldName to newName.
	 * 
	 * @return whether renaming worked
	 */
	boolean renameObject(String oldObjName, String newObjName);

	/**
	 * Sets whether an object should be animated. This does not start the
	 * animation yet, use startAnimation() to do so.
	 */
	void setAnimating(String objName, boolean animate);

	/**
	 * Sets the animation speed of an object.
	 */
	void setAnimationSpeed(String objName, double speed);

	/**
	 * Starts automatic animation for all objects with the animating flag set.
	 * 
	 * @see #setAnimating(String, boolean)
	 */
	void startAnimation();

	/**
	 * Stops animation for all objects with the animating flag set.
	 * 
	 * @see #setAnimating(String, boolean)
	 */
	void stopAnimation();

	/**
	 * @param hideCursorWhenDragging
	 *            Whether or not to show the mouse pointer (cursor) when
	 *            dragging
	 */
	void hideCursorWhenDragging(boolean hideCursorWhenDragging);

	/**
	 * Returns whether automatic animation is currently running.
	 * 
	 * @return whether automatic animation is currently running.
	 */
	boolean isAnimationRunning();

	/**
	 * Current frame rate of the animation.
	 * 
	 * @return in seconds
	 */

	double getFrameRate();

	/**
	 * Returns true if the object with the given name has a vaild value at the
	 * moment.
	 * 
	 * @param objName
	 *            object label
	 * @return whether it's currently defined
	 */
	boolean isDefined(String objName);

	/**
	 * Returns true if the object with the given name is independent.
	 * 
	 * @param objName
	 *            object label
	 * @return whether it is independent on other objects
	 */
	boolean isIndependent(String objName);

	void unregisterStoreUndoListener(Object jsFunction);

	/**
	 * @param objName
	 *            object label
	 * @return whether it can be moved
	 */
	boolean isMoveable(String objName);

	/**
	 * Returns the localized value of the object with the given name as a string.
	 *
	 * @param objName
	 *            object name
	 * @return value string
	 */
	String getValueString(String objName);

	/**
	 * Returns the value of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object name
	 * @param localized
	 *            if output should be localized
	 * @return value string
	 */
	String getValueString(String objName, boolean localized);

	/**
	 * Returns the description of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object label
	 * @return description string
	 */
	String getDefinitionString(String objName);

	/**
	 * Returns the description of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object label
	 * @param localize
	 *            whether to localize it
	 * @return description string
	 */
	String getDefinitionString(String objName, boolean localize);

	/**
	 * Returns the object with the given name as a LaTeX string.
	 * 
	 * @param objName
	 *            object label
	 * @return object value as LaTeX
	 */
	String getLaTeXString(String objName);

	/**
	 * Returns the command of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object label
	 * @return defining command
	 */
	String getCommandString(String objName);

	/**
	 * Returns the command of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object name
	 * @param localize
	 *            whether local or English command should be used
	 * @return command description of given object
	 */
	String getCommandString(String objName, boolean localize);

	/**
	 * @param objName
	 *            object name
	 * @param substituteVars
	 *            whether %n, %v, ... should be replaced by name, value, ...
	 * @return caption
	 */
	String getCaption(String objName, boolean substituteVars);

	/**
	 * @param objName
	 *            object name
	 * @param caption
	 *            new caption
	 */
	void setCaption(String objName, String caption);

	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 * 
	 * @param objName
	 *            object label
	 * @return x-coordinate
	 */
	double getXcoord(String objName);

	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 * 
	 * @param objName
	 *            object label
	 * @return y-coordinate
	 */
	double getYcoord(String objName);

	/**
	 * Returns the z-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 * 
	 * @param objName
	 *            object label
	 * @return z-coordinate
	 */
	double getZcoord(String objName);

	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 * 
	 * @param objName
	 *            object label
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */
	void setCoords(String objName, double x, double y);

	void setCoords(String objName, double x, double y, double z);

	/**
	 * Returns the double value of the object with the given name. Note: returns
	 * 0 if the object does not have a value.
	 * 
	 * @param objName
	 *            object label
	 * @return value or 0
	 */
	double getValue(String objName);

	/**
	 * Sets the double value of the object with the given name. Note: if the
	 * specified object is not a number, nothing happens.
	 * 
	 * @param objName
	 *            object label
	 * @param value
	 *            value
	 */
	void setValue(String objName, double value);

	/**
	 * @param objName
	 *            object label
	 * @param x
	 *            text value
	 */
	void setTextValue(String objName, String x);

	/**
	 * Sets the double value of the specified index of the list. Can be used to
	 * extend the size of a list
	 */
	void setListValue(String objName, double x, double y);

	/**
	 * Turns the repainting of all views on or off.
	 */
	void setRepaintingActive(boolean flag);

	boolean writePNGtoFile(String filename, double exportScale,
			boolean transparent, double DPI, boolean greyscale);

	/**
	 * @param exportScale
	 *            eg 1
	 * @param transparent
	 *            eg true
	 * @param dpi
	 *            eg 72
	 * @param copyToClipboard
	 *            only supported in desktop, waiting for
	 *            https://code.google.com/p/chromium/issues/detail?id=150835
	 * @param greyscale
	 *            true for monochrome
	 * @return base64 encoded picture of active view
	 */
	String getPNGBase64(double exportScale, boolean transparent,
			double dpi, boolean copyToClipboard, boolean greyscale);

	/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	void setCoordSystem(double xmin, double xmax, double ymin,
			double ymax);

	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics
	 * window.
	 */
	void setAxesVisible(boolean xVisible, boolean yVisible);

	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics
	 * window.
	 */
	void setAxesVisible(int view, boolean xVisible, boolean yVisible,
			boolean zVisible);

	void setAxisSteps(int view, String xStep, String yStep,
			String zStep);

	void setAxisLabels(int view, String xLabel, String yLabel,
			String zLabel);

	void setAxisUnits(int view, String xLabel, String yLabel,
			String zLabel);

	void setPointCapture(int view, int capture);

	/**
	 * Shows or hides the coordinate grid in the graphics windows 1 and 2.
	 * 
	 * @param flag
	 *            visibility flag
	 */
	void setGridVisible(boolean flag);

	/**
	 * Shows or hides the coordinate grid in the given graphics window.
	 */
	void setGridVisible(int view, boolean flag);

	/**
	 * @param view
	 *            view number
	 * @return whether grid is visible in that view
	 */
	boolean getGridVisible(int view);

	/**
	 * @return whether grid is visible in graphics 1
	 */
	boolean getGridVisible();

	/**
	 * Returns an array with all object names.
	 * 
	 * @return all object names
	 */
	String[] getAllObjectNames();

	/**
	 * Returns an array with all object names.
	 * 
	 * @param type
	 *            object type
	 * @return objects of this type
	 */
	String[] getAllObjectNames(String type);

	/**
	 * Returns the number of objects in the construction.
	 * 
	 * @return number of objects
	 */
	int getObjectNumber();

	/**
	 * Returns the name of the n-th object of this construction.
	 * 
	 * @param i
	 *            index in construction
	 * @return object label
	 */
	String getObjectName(int i);

	/**
	 * Returns the type of the object with the given name as a string (e.g.
	 * point, line, circle, ...)
	 * 
	 * @param objName
	 *            object label
	 * @return object type
	 */
	String getObjectType(String objName);

	/**
	 * Sets the mode of the geometry window (EuclidianView).
	 * 
	 * @param mode
	 *            app mode
	 */
	void setMode(int mode);

	/**
	 * @return the current mode
	 */
	int getMode();

	/**
	 * Registers a JavaScript function as an add listener for the applet's
	 * construction. Whenever a new object is created in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the newly created object as a single argument.
	 */
	void registerAddListener(Object jsFunction);

	/**
	 * Removes a previously registered add listener
	 * 
	 * @see #registerAddListener(Object)
	 */
	void unregisterAddListener(Object jsFunction);

	/**
	 * Registers a JavaScript function as a remove listener for the applet's
	 * construction. Whenever an object is deleted in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the deleted object as a single argument.
	 */
	void registerRemoveListener(Object jsFunction);

	/**
	 * Removes a previously registered remove listener
	 * 
	 * @see #registerRemoveListener(Object)
	 */
	void unregisterRemoveListener(Object jsFunction);

	/**
	 * Registers a JavaScript function as a clear listener for the applet's
	 * construction. Whenever the construction in the GeoGebraApplet's is
	 * cleared (i.e. all objects are removed), the JavaScript function
	 * JSFunctionName is called using no arguments.
	 */
	void registerClearListener(Object jsFunction);

	/**
	 * Removes a previously registered clear listener
	 * 
	 * @see #registerClearListener(Object)
	 */
	void unregisterClearListener(Object jsFunction);

	/**
	 * Registers a JavaScript function as a rename listener for the applet's
	 * construction. Whenever an object is renamed in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the deleted object as a single argument.
	 */
	void registerRenameListener(Object jsFunction);

	/**
	 * Removes a previously registered rename listener.
	 * 
	 * @see #registerRenameListener(Object)
	 */
	void unregisterRenameListener(Object jsFunction);

	/**
	 * Registers a JavaScript function as an update listener for the applet's
	 * construction. Whenever any object is updated in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the updated object as a single argument.
	 */
	void registerUpdateListener(Object jsFunction);

	/**
	 * Removes a previously registered update listener.
	 * 
	 * @see #registerRemoveListener(Object)
	 */
	void unregisterUpdateListener(Object jsFunction);

	/**
	 * Registers a JavaScript update listener for an object. Whenever the object
	 * with the given name changes, a JavaScript function named JSFunctionName
	 * is called using the name of the changed object as the single argument. If
	 * objName previously had a mapping JavaScript function, the old value is
	 * replaced.
	 * 
	 * Example: First, set a change listening JavaScript function:
	 * ggbApplet.registerObjectUpdateListener("A", "myJavaScriptFunction"); Then
	 * the GeoGebra Applet will call the Javascript function
	 * myJavaScriptFunction("A"); whenever object A changes.
	 */
	void registerObjectUpdateListener(String objName,  Object jsFunction);

	/**
	 * Removes a previously set change listener for the given object.
	 * 
	 * @see #registerObjectUpdateListener
	 */
	void unregisterObjectUpdateListener(String objName);

	/**
	 * Registers a JavaScript function as an click listener for the applet's
	 * construction. Whenever any object is clicked in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the updated object as a single argument.
	 */
	void registerClickListener(Object jsFunction);

	/**
	 * Removes a previously registered Click listener.
	 */
	void unregisterClickListener(Object jsFunction);

	void registerClientListener(Object jsFunction);

	void unregisterClientListener(Object jsFunction);

	/**
	 * Registers a JavaScript Click listener for an object. Whenever the object
	 * with the given name changes, a JavaScript function named JSFunctionName
	 * is called using the name of the changed object as the single argument. If
	 * objName previously had a mapping JavaScript function, the old value is
	 * replaced.
	 */
	void registerObjectClickListener(String objName,
			 Object jsFunction);

	/**
	 * Removes a previously set change listener for the given object.
	 * 
	 * @see #registerObjectClickListener
	 */
	void unregisterObjectClickListener(String objName);

	void registerStoreUndoListener(Object jsFunction);

	/**
	 * Gets the double value of the specified index of the list.
	 * 
	 * Returns Double.NaN if the object is not a GeoNumeric/Angle
	 * 
	 * @param objName
	 *            list label
	 * @param index
	 *            index
	 * @return value at index
	 */
	double getListValue(String objName, int index);

	void setCorner(String objName, double x, double y, int index);

	void setCorner(String objName, double x, double y);

	void setPerspective(String s);

	int getCASObjectNumber();

	String getVersion();

	void enableCAS(boolean enable);

	void enable3D(boolean enable);

	/**
	 * @param enable
	 *            whether geogebra-web applet rightclick enabled or not
	 */
	void enableRightClick(boolean enable);

	/**
	 * @param enable
	 * 
	 *            wheter labels draggable in geogebra-web applets or not
	 */
	void enableLabelDrags(boolean enable);

	/**
	 * @param enable
	 * 
	 *            wheter shift - drag - zoom enabled in geogebra-web applets or
	 *            not
	 */
	void enableShiftDragZoom(boolean enable);

	void setFont(String label, int size, boolean bold, boolean italic,
			boolean serif);

	void setRounding(String format);

	void newConstruction();

	/**
	 * Cast undo
	 * 
	 * @param repaint
	 *            true to repaint the views afterwards
	 */
	void undo(boolean repaint);

	/**
	 * Cast redo
	 * 
	 * @param repaint
	 *            true to repaint the views afterwards
	 */
	void redo(boolean repaint);

	String getViewProperties(int viewID);

	void logout();

	void login(String token);

	/**
	 * Returns localized name of given tool.
	 *
	 * @param mode
	 *            number
	 * @return name of given tool.
	 */
	String getToolName(int mode);

	void evalLaTeX(String input, int mode);

	/**
	 * 
	 * @return 3D model exported in collada format
	 */
	String exportCollada(double xmin, double xmax, double ymin,
			double ymax, double zmin, double zmax, double xyScale,
			double xzScale, double xTickDistance, double yTickDistance,
			double zTickDistance);

	/**
	 * 
	 * @return 3D model exported in simple 3d format
	 */
	String exportSimple3d(String name, double xmin, double xmax,
			double ymin,
			double ymax, double zmin, double zmax, double xyScale,
			double xzScale, double xTickDistance, double yTickDistance,
			double zTickDistance);

	/**
	 * @return rounding in the format acceptaable for
	 *         {@link #setRounding(String)}
	 */
	String getRounding();

	void exportGeometry3D(Geometry3DGetter getter, double xmin, double xmax,
			double ymin, double ymax, double zmin, double zmax, double xyScale,
			double xzScale, double xTickDistance, double yTickDistance,
			double zTickDistance);

	/**
	 * @param view
	 *            view ID
	 * @return axis labels
	 */
	String[] getAxisLabels(int view);

	/**
	 * @param view
	 *            view ID
	 * @return axis units
	 */
	String[] getAxisUnits(int view);

	/**
	 * Enables the fps measurement.
	 */
	void enableFpsMeasurement();

	/**
	 * Disables the fps measurement.
	 */
	void disableFpsMeasurement();

	/**
	 * Autonomously draws from the coords.json file.
	 */
	void testDraw();

	/**
	 * Records the drawing.
	 */
	void startDrawRecording();

	/**
	 * Ends the recording of the drawing and logs the results.
	 *
	 * For autonomous drawing, the logged result has to be copied into the coords.json file.
	 */
	void endDrawRecordingAndLogResults();

	/**
	 * add/delete/copy/paste slide actions
	 * @param eventType - event type (add, delete, etc.)
	 * @param pageIdx - page index
	 * @param appStat - appState
	 */
	void handleSlideAction(String eventType, String pageIdx, String appStat);

	/**
	 * select slide
	 * @param pageIdx - page index
	 */
	void selectSlide(String pageIdx);

	/**
	 * refresh slide
	 */
	void previewRefresh();

	/**
	 * groups objects
	 * @param objects - objects to be grouped
	 */
	void groupObjects(String[] objects);

	/**
	 * ungroup objects
	 * @param objects - objects to be ungrouped
	 */
	void ungroupObjects(String[] objects);

	/**
	 * @param object - label of element
	 * @return objects in the group of the element
	 */
	String[] getObjectsOfItsGroup(String object);

	/**
	 * add an element to a group
	 * @param object - object to be added to group
	 * @param objectInGroup - objects in group
	 */
	void addToGroup(String object, String[] objectInGroup);

	/**
	 * @param label - label of element
	 * @return whether element has unlabeled predecessors
	 */
	boolean hasUnlabeledPredecessors(String label);
}
