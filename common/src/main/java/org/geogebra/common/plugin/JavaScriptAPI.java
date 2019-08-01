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
	public byte[] getGGBfile();

	/**
	 * Returns current construction in XML format. May be used for saving.
	 * 
	 * @return XML representation of construction
	 */
	public String getXML();

	/**
	 * @return XML representation of perspective
	 */
	public String getPerspectiveXML();

	/**
	 * @return base64 representation of current file
	 */
	public String getBase64();

	/**
	 * @param includeThumbnail
	 *            whether thumbnail should be included
	 * @return base64 representation of current file
	 */
	public String getBase64(boolean includeThumbnail);

	public abstract void uploadToGeoGebraTube();

	/**
	 * Returns the GeoGebra XML string for the given GeoElement object, i.e.
	 * only the &lt;element&gt; tag is returned.
	 * 
	 * @param objName
	 *            object name
	 * @return style XML
	 */
	public String getXML(String objName);

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
	public String getAlgorithmXML(String objName);

	/**
	 * Opens construction given in XML format. May be used for loading
	 * constructions.
	 * 
	 * @param xml
	 *            construction XML
	 */
	public void setXML(String xml);

	/**
	 * Loads encoded file into the applet
	 * 
	 * @param base64
	 *            base64 encoded content
	 */
	public void setBase64(String base64);

	/**
	 * Evaluates the given XML string and changes the current construction.
	 * Note: the construction is NOT cleared before evaluating the XML string.
	 * 
	 * @param xmlString
	 *            (partial) construction XML
	 */
	public void evalXML(String xmlString);

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's input
	 * text field.
	 * 
	 * @param cmdString
	 *            command
	 * @return whether execution was successful
	 */
	public boolean evalCommand(String cmdString);

	/**
	 * Runs command in CAS without checking GeoGebra variables
	 * 
	 * @param cmdString
	 *            CAS command
	 * @return CAS result
	 */
	public String evalCommandCAS(String cmdString);

	/**
	 * Runs command in CAS, all variables are substituted by GeoGebra objects
	 * 
	 * @param cmdString
	 *            CAS command
	 * @return CAS result
	 */
	public String evalGeoGebraCAS(String cmdString);

	/**
	 * prints a string to the JavaScript / Java Console
	 * 
	 * @param string
	 *            string to be printed in console
	 */
	public void debug(String string);

	/**
	 * Turns showing of error dialogs on (true) or (off). Note: this is
	 * especially useful together with evalCommand().
	 */
	public void setErrorDialogsActive(boolean flag);

	/**
	 * Turns on the fly creation of points in graphics view on (true) or (off).
	 * Note: this is useful if you don't want tools to have the side effect of
	 * creating points. For example, when this flag is set to false, the tool
	 * "line through two points" will not create points on the fly when you
	 * click on the background of the graphics view.
	 */
	public void setOnTheFlyPointCreationActive(boolean flag);

	public void setUndoPoint();

	/**
	 * Resets the initial construction (given in filename parameter) of this
	 * applet.
	 */
	public void reset();

	/**
	 * Refreshs all views. Note: clears traces in geometry window.
	 */
	public void refreshViews();

	/**
	 * Loads a construction from a file (given URL). ...but the actual code is
	 * in a thread to avoid JavaScript security issues
	 */
	public void openFile(String strURL);

	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public void setVisible(String objName, boolean visible);

	/**
	 * returns true or false depending on whether the object is visible
	 * 
	 * @param objName
	 *            object label
	 * @return whether object is visible
	 */
	public boolean getVisible(String objName);

	/**
	 * @param objName
	 *            object name
	 * @param view
	 *            graphics view ID: 1 or 2
	 * @return whether object is visible in given view
	 */
	public boolean getVisible(String objName, int view);

	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	public void setLayer(String objName, int layer);

	/**
	 * Returns the layer of the object with the given name in the geometry
	 * window. returns layer, or -1 if object doesn't exist Michael Borcherds
	 * 2008-02-27
	 * 
	 * @param objName
	 *            object label
	 * @return layer index or -1
	 */
	public int getLayer(String objName);

	/**
	 * Shows or hides a complete layer Michael Borcherds 2008-02-27
	 * 
	 * @param layer
	 *            layer index
	 * @param visible
	 *            visibility flag
	 */
	public void setLayerVisible(int layer, boolean visible);

	/**
	 * Sets the fixed state of the object with the given name.
	 */
	public void setFixed(String objName, boolean flag);

	public void setAuxiliary(String objName, boolean flag);

	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	public void setTrace(String objName, boolean flag);

	/**
	 * @param objName
	 *            object name
	 * @return whether the trace for given object is on
	 */
	public boolean isTracing(String objName);

	/**
	 * Shows or hides the label of the object with the given name in the
	 * geometry window.
	 */
	public void setLabelVisible(String objName, boolean visible);

	/**
	 * @param objName
	 *            object label
	 * @return whether its label is visible
	 */
	public boolean getLabelVisible(String objName);

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
	public void setLabelStyle(String objName, int style);

	/**
	 * Returns labeling style of the object
	 * 
	 * @param objName
	 *            object label
	 * @return labeling style
	 */
	public int getLabelStyle(String objName);

	/**
	 * Sets the line thickness of the object with the given name.
	 */
	public void setLineThickness(String objName, int thickness);

	/**
	 * Returns the line thickness of the object
	 * 
	 * @param objName
	 *            object label
	 * @return line thickness
	 */
	public int getLineThickness(String objName);

	/**
	 * Sets the lineType of the object with the given name.(if possible)
	 */
	public void setLineStyle(String objName, int style);

	/**
	 * Returns the lineType of the object
	 * 
	 * @param objName
	 *            object label
	 * @return line style
	 */
	public int getLineStyle(String objName);

	/**
	 * Sets the filling of the object with the given name. (if possible)
	 */
	public void setFilling(String objName, double filling);

	/**
	 * Returns the filling of the object as an int (or -1 for no filling)
	 * 
	 * @param objName
	 *            object label
	 * @return the filling of the object as an int (or -1 for no filling)
	 */
	public double getFilling(String objName);

	/**
	 * Returns the point style of the object as an int (or -1 for default, or
	 * not a point)
	 * 
	 * @param objName
	 *            object label
	 * @return the point style of the object as an int (or -1 for default, or
	 *         not a point)
	 */
	public int getPointStyle(String objName);

	/**
	 * Sets the point style of the object (-1 for default)
	 * 
	 * @param objName
	 *            object label
	 * @param style
	 *            point style
	 */
	public void setPointSize(String objName, int style);

	/**
	 * Returns the point style of the object as an int (or -1 for default, or
	 * not a point)
	 * 
	 * @param objName
	 *            object label
	 * @return point size
	 */
	public int getPointSize(String objName);

	/**
	 * Sets the point style of the object (-1 for default)
	 * 
	 * @param objName
	 *            object label
	 * @param style
	 *            point style
	 */
	public void setPointStyle(String objName, int style);

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
	public void setColor(String objName, int red, int green, int blue);

	/**
	 * @param red
	 *            red part (0-255)
	 * @param green
	 *            green part (0-255)
	 * @param blue
	 *            blue part (0-255)
	 */
	public void setPenColor(int red, int green, int blue);

	/**
	 * @param size
	 *            size in pixels
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
	 * Returns the color of the object as an hex string. Note that the
	 * hex-string starts with # and uses upper case letters, e.g. "#FF0000" for
	 * red.
	 * 
	 * @param objName
	 *            object label
	 * @return hex color
	 */
	public String getColor(String objName);

	/**
	 * Deletes the object with the given name.
	 */
	public void deleteObject(String objName);

	/**
	 * Returns true if the object with the given name exists.
	 * 
	 * @param objName
	 *            object label
	 * @return whether object exists
	 */
	public boolean exists(String objName);

	/**
	 * Renames an object from oldName to newName.
	 * 
	 * @return whether renaming worked
	 */
	public boolean renameObject(String oldObjName, String newObjName,
			boolean forceRename);

	/**
	 * Renames an object from oldName to newName.
	 * 
	 * @return whether renaming worked
	 */
	public boolean renameObject(String oldObjName, String newObjName);

	/**
	 * Sets whether an object should be animated. This does not start the
	 * animation yet, use startAnimation() to do so.
	 */
	public void setAnimating(String objName, boolean animate);

	/**
	 * Sets the animation speed of an object.
	 */
	public void setAnimationSpeed(String objName, double speed);

	/**
	 * Starts automatic animation for all objects with the animating flag set.
	 * 
	 * @see #setAnimating(String, boolean)
	 */
	public void startAnimation();

	/**
	 * Stops animation for all objects with the animating flag set.
	 * 
	 * @see #setAnimating(String, boolean)
	 */
	public void stopAnimation();

	/**
	 * @param hideCursorWhenDragging
	 *            Whether or not to show the mouse pointer (cursor) when
	 *            dragging
	 */
	public void hideCursorWhenDragging(boolean hideCursorWhenDragging);

	/**
	 * Returns whether automatic animation is currently running.
	 * 
	 * @return whether automatic animation is currently running.
	 */
	public boolean isAnimationRunning();

	/**
	 * Current frame rate of the animation.
	 * 
	 * @return in seconds
	 */

	public double getFrameRate();

	/**
	 * Returns true if the object with the given name has a vaild value at the
	 * moment.
	 * 
	 * @param objName
	 *            object label
	 * @return whether it's currently defined
	 */
	public boolean isDefined(String objName);

	/**
	 * Returns true if the object with the given name is independent.
	 * 
	 * @param objName
	 *            object label
	 * @return whether it is independent on other objects
	 */
	public boolean isIndependent(String objName);

	/**
	 * @param objName
	 *            object label
	 * @return whether it can be moved
	 */
	public boolean isMoveable(String objName);

	/**
	 * Returns the value of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object name
	 * @return value string
	 */
	public String getValueString(String objName);

	/**
	 * Returns the description of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object label
	 * @return description string
	 */
	public String getDefinitionString(String objName);

	/**
	 * Returns the description of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object label
	 * @param localize
	 *            whether to localize it
	 * @return description string
	 */
	public String getDefinitionString(String objName, boolean localize);

	/**
	 * Returns the object with the given name as a LaTeX string.
	 * 
	 * @param objName
	 *            object label
	 * @return object value as LaTeX
	 */
	public String getLaTeXString(String objName);

	/**
	 * Returns the command of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object label
	 * @return defining command
	 */
	public String getCommandString(String objName);

	/**
	 * Returns the command of the object with the given name as a string.
	 * 
	 * @param objName
	 *            object name
	 * @param localize
	 *            whether local or English command should be used
	 * @return command description of given object
	 */
	public String getCommandString(String objName, boolean localize);

	/**
	 * @param objName
	 *            object name
	 * @param substituteVars
	 *            whether %n, %v, ... should be replaced by name, value, ...
	 * @return caption
	 */
	public String getCaption(String objName, boolean substituteVars);

	/**
	 * @param objName
	 *            object name
	 * @param caption
	 *            new caption
	 */
	public void setCaption(String objName, String caption);

	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 * 
	 * @param objName
	 *            object label
	 * @return x-coordinate
	 */
	public double getXcoord(String objName);

	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 * 
	 * @param objName
	 *            object label
	 * @return y-coordinate
	 */
	public double getYcoord(String objName);

	/**
	 * Returns the z-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 * 
	 * @param objName
	 *            object label
	 * @return z-coordinate
	 */
	public double getZcoord(String objName);

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
	public void setCoords(String objName, double x, double y);

	public void setCoords(String objName, double x, double y, double z);

	/**
	 * Returns the double value of the object with the given name. Note: returns
	 * 0 if the object does not have a value.
	 * 
	 * @param objName
	 *            object label
	 * @return value or 0
	 */
	public double getValue(String objName);

	/**
	 * Sets the double value of the object with the given name. Note: if the
	 * specified object is not a number, nothing happens.
	 * 
	 * @param objName
	 *            object label
	 * @param value
	 *            value
	 */
	public void setValue(String objName, double value);

	/**
	 * @param objName
	 *            object label
	 * @param x
	 *            text value
	 */
	public void setTextValue(String objName, String x);

	/**
	 * Sets the double value of the specified index of the list. Can be used to
	 * extend the size of a list
	 */
	public void setListValue(String objName, double x, double y);

	/**
	 * Turns the repainting of all views on or off.
	 */
	public void setRepaintingActive(boolean flag);

	public boolean writePNGtoFile(String filename, double exportScale,
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
	public String getPNGBase64(double exportScale, boolean transparent,
			double dpi, boolean copyToClipboard, boolean greyscale);

	/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	public void setCoordSystem(double xmin, double xmax, double ymin,
			double ymax);

	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics
	 * window.
	 */
	public void setAxesVisible(boolean xVisible, boolean yVisible);

	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics
	 * window.
	 */
	public void setAxesVisible(int view, boolean xVisible, boolean yVisible,
			boolean zVisible);

	public void setAxisSteps(int view, String xStep, String yStep,
			String zStep);

	public void setAxisLabels(int view, String xLabel, String yLabel,
			String zLabel);

	public void setAxisUnits(int view, String xLabel, String yLabel,
			String zLabel);

	public void setPointCapture(int view, int capture);

	/**
	 * Shows or hides the coordinate grid in the graphics windows 1 and 2.
	 * 
	 * @param flag
	 *            visibility flag
	 */
	public void setGridVisible(boolean flag);

	/**
	 * Shows or hides the coordinate grid in the given graphics window.
	 */
	public void setGridVisible(int view, boolean flag);

	/**
	 * @param view
	 *            view number
	 * @return whether grid is visible in that view
	 */
	public boolean getGridVisible(int view);

	/**
	 * @return whether grid is visible in graphics 1
	 */
	public boolean getGridVisible();

	/**
	 * Returns an array with all object names.
	 * 
	 * @return all object names
	 */
	public String[] getAllObjectNames();

	/**
	 * Returns an array with all object names.
	 * 
	 * @param type
	 *            object type
	 * @return objects of this type
	 */
	public String[] getAllObjectNames(String type);

	/**
	 * Returns the number of objects in the construction.
	 * 
	 * @return number of objects
	 */
	public int getObjectNumber();

	/**
	 * Returns the name of the n-th object of this construction.
	 * 
	 * @param i
	 *            index in construction
	 * @return object label
	 */
	public String getObjectName(int i);

	/**
	 * Returns the type of the object with the given name as a string (e.g.
	 * point, line, circle, ...)
	 * 
	 * @param objName
	 *            object label
	 * @return object type
	 */
	public String getObjectType(String objName);

	/**
	 * Sets the mode of the geometry window (EuclidianView).
	 * 
	 * @param mode
	 *            app mode
	 */
	public void setMode(int mode);

	/**
	 * @return the current mode
	 */
	public int getMode();

	/**
	 * Registers a JavaScript function as an add listener for the applet's
	 * construction. Whenever a new object is created in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the newly created object as a single argument.
	 */
	public void registerAddListener(String JSFunctionName);

	/**
	 * Removes a previously registered add listener
	 * 
	 * @see #registerAddListener(String)
	 */
	public void unregisterAddListener(String JSFunctionName);

	/**
	 * Registers a JavaScript function as a remove listener for the applet's
	 * construction. Whenever an object is deleted in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the deleted object as a single argument.
	 */
	public void registerRemoveListener(String JSFunctionName);

	/**
	 * Removes a previously registered remove listener
	 * 
	 * @see #registerRemoveListener(String)
	 */
	public void unregisterRemoveListener(String JSFunctionName);

	/**
	 * Registers a JavaScript function as a clear listener for the applet's
	 * construction. Whenever the construction in the GeoGebraApplet's is
	 * cleared (i.e. all objects are removed), the JavaScript function
	 * JSFunctionName is called using no arguments.
	 */
	public void registerClearListener(String JSFunctionName);

	/**
	 * Removes a previously registered clear listener
	 * 
	 * @see #registerClearListener(String)
	 */
	public void unregisterClearListener(String JSFunctionName);

	/**
	 * Registers a JavaScript function as a rename listener for the applet's
	 * construction. Whenever an object is renamed in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the deleted object as a single argument.
	 */
	public void registerRenameListener(String JSFunctionName);

	/**
	 * Removes a previously registered rename listener.
	 * 
	 * @see #registerRenameListener(String)
	 */
	public void unregisterRenameListener(String JSFunctionName);

	/**
	 * Registers a JavaScript function as an update listener for the applet's
	 * construction. Whenever any object is updated in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the updated object as a single argument.
	 */
	public void registerUpdateListener(String JSFunctionName);

	/**
	 * Removes a previously registered update listener.
	 * 
	 * @see #registerRemoveListener(String)
	 */
	public void unregisterUpdateListener(String JSFunctionName);

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
	public void registerObjectUpdateListener(String objName,
			String JSFunctionName);

	/**
	 * Removes a previously set change listener for the given object.
	 * 
	 * @see #registerObjectUpdateListener
	 */
	public void unregisterObjectUpdateListener(String objName);

	/**
	 * Registers a JavaScript function as an click listener for the applet's
	 * construction. Whenever any object is clicked in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the updated object as a single argument.
	 */
	public void registerClickListener(String JSFunctionName);

	/**
	 * Removes a previously registered Click listener.
	 */
	public void unregisterClickListener(String JSFunctionName);

	public void registerClientListener(String JSFunctionName);

	public void unregisterClientListener(String JSFunctionName);

	/**
	 * Registers a JavaScript Click listener for an object. Whenever the object
	 * with the given name changes, a JavaScript function named JSFunctionName
	 * is called using the name of the changed object as the single argument. If
	 * objName previously had a mapping JavaScript function, the old value is
	 * replaced.
	 */
	public void registerObjectClickListener(String objName,
			String JSFunctionName);

	/**
	 * Removes a previously set change listener for the given object.
	 * 
	 * @see #registerObjectClickListener
	 */
	public void unregisterObjectClickListener(String objName);

	public void registerStoreUndoListener(String objName);

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
	public double getListValue(String objName, int index);

	public void setCorner(String objName, double x, double y, int index);

	public void setCorner(String objName, double x, double y);

	public void setPerspective(String s);

	public int getCASObjectNumber();

	public String getVersion();

	public void enableCAS(boolean enable);

	public void enable3D(boolean enable);

	/**
	 * @param enable
	 *            whether geogebra-web applet rightclick enabled or not
	 */
	public void enableRightClick(boolean enable);

	/**
	 * @param enable
	 * 
	 *            wheter labels draggable in geogebra-web applets or not
	 */
	public void enableLabelDrags(boolean enable);

	/**
	 * @param enable
	 * 
	 *            wheter shift - drag - zoom enabled in geogebra-web applets or
	 *            not
	 */
	public void enableShiftDragZoom(boolean enable);

	public void setFont(String label, int size, boolean bold, boolean italic,
			boolean serif);

	public void setRounding(String format);

	public void newConstruction();

	/**
	 * Cast undo
	 * 
	 * @param repaint
	 *            true to repaint the views afterwards
	 */
	public void undo(boolean repaint);

	/**
	 * Cast redo
	 * 
	 * @param repaint
	 *            true to repaint the views afterwards
	 */
	public void redo(boolean repaint);

	public String getViewProperties(int viewID);

	public void logout();

	public void login(String token);

	/**
	 * Returns localized name of given tool.
	 *
	 * @param mode
	 *            number
	 * @return name of given tool.
	 */
	public String getToolName(int mode);

	public void evalLaTeX(String input, int mode);

	/**
	 * 
	 * @return 3D model exported in collada format
	 */
	public String exportCollada(double xmin, double xmax, double ymin,
			double ymax, double zmin, double zmax, double xyScale,
			double xzScale, double xTickDistance, double yTickDistance,
			double zTickDistance);

	/**
	 * 
	 * @return 3D model exported in simple 3d format
	 */
	public String exportSimple3d(String name, double xmin, double xmax,
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
}
