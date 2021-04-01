/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.kernelND;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.AutoColor;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GeoElementConvertable;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgorithmSet;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.util.LaTeXCache;

/**
 * Common interface for all interfaces that represent GeoElements
 * 
 * @author Zbynek
 *
 */
public interface GeoElementND extends ExpressionValue, GeoElementConvertable {

	/** label mode: name */
	public static final int LABEL_NAME = 0;
	/** label mode: name + value */
	public static final int LABEL_NAME_VALUE = 1;
	/** label mode: value */
	public static final int LABEL_VALUE = 2;
	/** label mode: caption */
	public static final int LABEL_CAPTION = 3; // Michael Borcherds 2008-02-18
	/** label mode: default */
	public static final int LABEL_DEFAULT = 4;
	/** label mode: default, name */
	public static final int LABEL_DEFAULT_NAME = 5;
	/** label mode: default, name + value */
	public static final int LABEL_DEFAULT_NAME_VALUE = 6;
	/** label mode: default, value */
	public static final int LABEL_DEFAULT_VALUE = 7;
	/** label mode: default, caption */
	public static final int LABEL_DEFAULT_CAPTION = 8;
	/** caption + value */
	public static final int LABEL_CAPTION_VALUE = 9;

	/** tooltip mode: iff AV showing */
	public static final int TOOLTIP_ALGEBRAVIEW_SHOWING = 0;
	/** tooltip mode: always on */
	public static final int TOOLTIP_ON = 1;
	/** tooltip mode: always off */
	public static final int TOOLTIP_OFF = 2;
	/** tooltip mode: caption, always on */
	public static final int TOOLTIP_CAPTION = 3;
	/** tooltip mode: next spreadsheet cell, always on */
	public static final int TOOLTIP_NEXTCELL = 4;

	/** maximal animation speed */
	final public static double MAX_ANIMATION_SPEED = 100;
	/** animation type: oscillating */
	final public static int ANIMATION_OSCILLATING = 0;
	/** animation type: increasing */
	final public static int ANIMATION_INCREASING = 1;
	/** animation type: decreasing */
	final public static int ANIMATION_DECREASING = 2;
	/** animation type: increasing once */
	final public static int ANIMATION_INCREASING_ONCE = 3;

	/** Decoration type: no decoration */
	public static final int DECORATION_NONE = 0;
	// segment decorations
	/** Decoration type: one tick */
	public static final int DECORATION_SEGMENT_ONE_TICK = 1;
	/** Decoration type: two ticks */
	public static final int DECORATION_SEGMENT_TWO_TICKS = 2;
	/** Decoration type: three ticks */
	public static final int DECORATION_SEGMENT_THREE_TICKS = 3;
	// Michael Borcherds 2007-10-06
	/** Decoration type: one arow */
	public static final int DECORATION_SEGMENT_ONE_ARROW = 4;
	/** Decoration type: two arrows */
	public static final int DECORATION_SEGMENT_TWO_ARROWS = 5;
	/** Decoration type: three arrows */
	public static final int DECORATION_SEGMENT_THREE_ARROWS = 6;
	// Michael Borcherds 2007-10-06
	// angle decorations
	/** Decoration type for angles: two arcs */
	public static final int DECORATION_ANGLE_TWO_ARCS = 1;
	/** Decoration type for angles: three arcs */
	public static final int DECORATION_ANGLE_THREE_ARCS = 2;
	/** Decoration type for angles: one tick */
	public static final int DECORATION_ANGLE_ONE_TICK = 3;
	/** Decoration type for angles: two ticks */
	public static final int DECORATION_ANGLE_TWO_TICKS = 4;
	/** Decoration type for angles: three ticks */
	public static final int DECORATION_ANGLE_THREE_TICKS = 5;

	/**
	 * Decoration type for angles: counterclockwise arrow
	 * 
	 * @author Michael Borcherds, 2007-10-22
	 */
	public static final int DECORATION_ANGLE_ARROW_ANTICLOCKWISE = 6;
	/**
	 * Decoration type for angles: clockwise arrow
	 * 
	 * @author Michael Borcherds, 2007-10-22
	 */
	public static final int DECORATION_ANGLE_ARROW_CLOCKWISE = 7;

	/**
	 * Sets label of a GeoElement and updates Construction list and GeoElement
	 * table (String label, GeoElement geo) in Kernel. If the old label was
	 * null, a new free label is assigned starting with label as a prefix. If
	 * newLabel is not already used, this object is renamed to newLabel.
	 * Otherwise nothing is done.
	 * 
	 * @param labelNew
	 *            new label
	 */
	void setLabel(String labelNew);

	/**
	 * Updates this geo
	 */
	void update();

	/**
	 * @param objectColor
	 *            new color for this object
	 */
	void setObjColor(GColor objectColor);

	/**
	 * Allows drawing this in EV
	 * 
	 * @param visible
	 *            true to allow drawing this in EV
	 */
	void setEuclidianVisible(boolean visible);

	/**
	 * @return whether object should be drawn in euclidian view
	 */
	boolean isEuclidianVisible();

	/**
	 * Returns whether the label should be shown in Euclidian view.
	 * 
	 * @return true if label should be shown
	 */
	boolean isLabelVisible();

	/**
	 * Returns whether this object's label has been set and is valid now. (this
	 * is needed for saving: only object's with isLabelSet() == true should be
	 * saved)
	 * 
	 * @return true if this geo has valid label
	 */
	public boolean isLabelSet();

	/**
	 * Returns label or local variable label if set, returns output value string
	 * otherwise
	 * 
	 * @param tpl
	 *            string template
	 * @return label or output value string
	 */
	public String getLabel(StringTemplate tpl);

	/**
	 * @return true for infinite numbers / points
	 */
	public boolean isInfinite();

	/**
	 * Whether the label is visible in Algebra View.
	 *
	 * @return true if the label is visible in AV.
	 */
	boolean isAlgebraLabelVisible();

	/**
	 * Set whether the label should be visible in Algebra View.
	 *
	 * @param algebraLabelVisible true if the label should be visible in AV
	 */
	void setAlgebraLabelVisible(boolean algebraLabelVisible);

	/**
	 * Update visual style and notify kernel
	 * 
	 * @param prop
	 *            property being changed
	 */
	public void updateVisualStyle(GProperty prop);

	/**
	 * Remove this from construction
	 */
	public void remove();

	/**
	 * @return true if tracing to spreadsheet
	 */
	public boolean getSpreadsheetTrace();

	/**
	 * This method always returns a GeoElement of the SAME CLASS as this
	 * GeoElement. Furthermore the resulting geo is in construction cons.
	 * 
	 * @param cons
	 *            construction
	 * @return copy in given construction
	 */
	public GeoElement copyInternal(Construction cons);

	/**
	 * every subclass implements it's own copy method this is needed for
	 * assignment copies like: a = 2.7 b = a (here copy() is needed)
	 * 
	 * @return copy of current element
	 */
	public GeoElementND copy();

	/**
	 * @return true if this is free geo (noparent algo)
	 */
	public boolean isIndependent();

	/**
	 * @return algorithm responsible for computation of this object
	 */
	public AlgoElement getParentAlgorithm();

	/**
	 * Returns false for undefined objects
	 * 
	 * @return false when undefined
	 */
	public boolean isDefined();

	/**
	 * Makes object undefined, some objects lose their internally stored value
	 * when this is called
	 */
	public void setUndefined();

	/**
	 * @param type
	 *            line type
	 */
	public void setLineType(int type);

	/**
	 * @param th
	 *            line thickness
	 */
	public void setLineThickness(int th);

	/**
	 * @return true if it has a line opacity value between 0 and 255
	 */
	public boolean hasLineOpacity();

	/**
	 * Sets the line opacity for this {@code GeoElement}. <br>
	 * 
	 * @param opacity
	 *            opacity value between 0 - 255
	 */
	public void setLineOpacity(int opacity);

	/**
	 * @return The value for the line opacity (0 - 255). <br>
	 *         The default value is 255 (opaque)
	 */
	public int getLineOpacity();

	/**
	 * sets whether the object's label should be drawn in an EuclidianView
	 * 
	 * @param visible
	 *            true to make label visible
	 */
	public void setLabelVisible(boolean visible);

	/**
	 * Returns whether this GeoElement is a point on a path.
	 * 
	 * @return true for points on path
	 */
	public boolean isPointOnPath();

	/**
	 * Returns whether this GeoElement is a point in a region
	 * 
	 * @return true for points in region
	 */
	public boolean isPointInRegion();

	/**
	 * @param p
	 *            point
	 * @return distance from point
	 */
	public double distance(GeoPointND p);

	/**
	 * Update this geo and all its descendants
	 */
	void updateCascade();

	/**
	 * Update and repaint this geo
	 */
	void updateRepaint();

	/**
	 * @return line type
	 */
	int getLineType();

	/**
	 * @return line thickness
	 */
	int getLineThickness();

	/**
	 * @return whether the complement should be filled
	 */
	boolean isInverseFill();

	/**
	 * @return animation step as double
	 */
	public double getAnimationStep();

	/**
	 * @return construction index
	 */
	int getConstructionIndex();

	/**
	 * @return set of all dependent algos in topological order
	 */
	AlgorithmSet getAlgoUpdateSet();

	/**
	 * @return list of directly dependent algos
	 */
	ArrayList<AlgoElement> getAlgorithmList();

	/**
	 * @return whether the update set
	 */
	boolean hasAlgoUpdateSet();

	/**
	 * @return whether this is instance of GeoElement3D
	 */
	public boolean isGeoElement3D();

	/**
	 * 
	 * @return true if is region that produces 3D points
	 */
	public boolean isRegion3D();

	/**
	 * @return whether this is instance of GeoText
	 */
	public boolean isGeoText();

	/**
	 * Returns how should label look like in Euclidian view
	 * 
	 * @return label mode (name, value, name + value, caption) may be
	 *         GeoElement.LABEL_NAME, LABEL_VALUE etc
	 */
	int getLabelMode();

	/**
	 * Switch label mode among value, name, value+name and caption
	 * 
	 * @param labelMode
	 *            LABEL_ mode
	 */
	void setLabelMode(int labelMode);

	// public Kernel getKernel();
	/**
	 * We may need a simple method to get the label, as in the CopyPaste class.
	 * 
	 * 
	 * @return get the label if set; do not fallback to definition (unlike
	 *         {@link #getLabel(StringTemplate)})
	 */
	public String getLabelSimple();

	/**
	 * Update value and basic properties from other geo. Implemented in each
	 * subclass.
	 * 
	 * @param geo
	 *            other geo
	 */
	public void set(GeoElementND geo);

	/**
	 * Sets visibility if not given by condition to show object
	 * 
	 * @param visible
	 *            whether it should be visible
	 */
	public void setEuclidianVisibleIfNoConditionToShowObject(boolean visible);

	/**
	 * @return whether this is a point (ND)
	 */
	boolean isGeoPoint();

	/**
	 * @return whether this is a number (numeric, not just any NumberValue)
	 */
	boolean isGeoNumeric();

	/**
	 * @return whether this is a button
	 */
	boolean isGeoButton();

	/**
	 * @return whether this is an audio
	 */
	boolean isGeoAudio();

	/**
	 * @return whether this is an video
	 */
	boolean isGeoVideo();

	/**
	 * @return caption template including %v, %n, ...
	 */
	String getRawCaption();

	/**
	 * @return parent construction
	 */
	Construction getConstruction();

	/**
	 * @return whether this is a polyhedron
	 */
	boolean isGeoPolyhedron();

	/**
	 * @return IDs of views that contain this geo
	 */
	List<Integer> getViewSet();

	/**
	 * @return whether this is a segment
	 */
	boolean isGeoSegment();

	/**
	 * @return whether this is a polygon
	 */
	boolean isGeoPolygon();

	/**
	 * @return whether this is a ray
	 */
	boolean isGeoRay();

	/**
	 * @return whether this is a conic arc / segment
	 */
	boolean isGeoConicPart();

	/**
	 * @return whether this is a vector
	 */
	boolean isGeoVector();

	/**
	 * Returns the {@link GeoClass}
	 * 
	 * @return GeoClass
	 */
	GeoClass getGeoClassType();

	/**
	 * @param auxilliary
	 *            whether this is auxiliary object (not shown in AV by default)
	 */
	void setAuxiliaryObject(boolean auxilliary);

	/**
	 * @param fix
	 *            whether this should be prevented from moving
	 */
	void setFixed(boolean fix);

	/**
	 * @param wanted
	 *            whether label is needed
	 */
	void setLabelWanted(boolean wanted);

	/**
	 * @param colorSpace
	 *            color space of dynamic color
	 */
	void setColorSpace(int colorSpace);

	/**
	 * @param colorFunction
	 *            dynamic color as list of numbers {R,G,B} / {H,S,L} / {H,S,B}
	 */
	void setColorFunction(GeoList colorFunction);

	/**
	 * @param hatchingDistance
	 *            hatching distance in pixels
	 */
	void setHatchingDistance(int hatchingDistance);

	/**
	 * @return (lowercase) class name for XML
	 */
	String getXMLtypeString();

	/**
	 * Copy 3D visibility from other element
	 * 
	 * @param geo
	 *            other element
	 */
	void setVisibleInView3D(GeoElement geo);

	/**
	 * Make visible in given views.
	 * 
	 * @param viewSet
	 *            set of views where this may appear
	 */
	void setViewFlags(List<Integer> viewSet);

	/**
	 * Copy plane visibility from other element
	 * 
	 * @param geo
	 *            other element
	 */
	void setVisibleInViewForPlane(GeoElement geo);

	/**
	 * @return whether this can be drawn in 2D
	 */
	boolean isDrawable();

	/**
	 * @return defining expression
	 */
	ExpressionNode getDefinition();

	/**
	 * @param def
	 *            defining expression
	 */
	void setDefinition(ExpressionNode def);

	/**
	 * Returns whether geo depends on this object.
	 * 
	 * @param geo
	 *            other geo
	 * @return true if geo depends on this object.
	 */
	public boolean isParentOf(final GeoElementND geo);

	/**
	 * removes this GeoElement and all its dependents
	 */
	void doRemove();

	/**
	 * Returns whether this object is parent of other geos.
	 * 
	 * @return true if this object is parent of other geos.
	 */
	boolean hasChildren();

	/**
	 * @return true if visible in 3D view
	 */
	boolean isVisibleInView3D();

	/**
	 * @param geo
	 *            other geo
	 * 
	 * @return whether the elements are equal in geometric sense (for congruency
	 *         use isCongruent)
	 */
	public boolean isEqual(GeoElementND geo);

	/**
	 * @return parent kernel
	 */
	Kernel getKernel();

	/**
	 * @return true if highlighted or selected
	 */
	boolean doHighlighting();

	/**
	 * @return alpha value (transparency)
	 * 
	 *         NOTE: can be -1 for lists, see GeoList.getAlphaValue(),
	 *         GeoList.setgetAlphaValue()
	 */
	double getAlphaValue();

	/**
	 * @return algorithm responsible for drawing this
	 */
	AlgoElement getDrawAlgorithm();

	/**
	 * @return color of fill
	 */
	GPaint getFillColor();

	/**
	 * 
	 * @return color of background
	 */
	GColor getBackgroundColor();

	/**
	 * @return fill type (standard/hatch/image)
	 */
	FillType getFillType();

	/**
	 * 
	 * @return color of label
	 */
	GColor getLabelColor();

	/**
	 * Returns the label and/or value of this object for showing in
	 * EuclidianView. This depends on the current setting of labelMode:
	 * LABEL_NAME : only label LABEL_NAME_VALUE : label and value
	 * 
	 * @return label, value, label+value or caption
	 */
	String getLabelDescription();

	/**
	 * @return object color
	 */
	GColor getObjectColor();

	/**
	 * @return filename of fill image
	 */
	String getImageFileName();

	/**
	 * @return LaTeX description
	 */
	String getLaTeXdescription();

	/**
	 * @return color of object for selection
	 */
	GColor getSelColor();

	/**
	 * @return true if current fill style is hatch
	 */
	boolean isHatchingEnabled();

	/**
	 * @param hatchingAngle
	 *            hatching angle in degrees
	 */
	void setHatchingAngle(int hatchingAngle);

	/**
	 * Changes transparency of this geo
	 * 
	 * @param alpha
	 *            new alpha value between 0 and 1
	 */
	void setAlphaValue(double alpha);

	/**
	 * Caption string (with substitutions)
	 * 
	 * @param tpl
	 *            string template
	 * @return caption (or label if caption is null)
	 */
	String getCaption(StringTemplate tpl);

	/**
	 * @return fill image
	 */
	MyImage getFillImage();

	/**
	 * @return Unicode symbol used for fill
	 */
	String getFillSymbol();

	/**
	 * @param fillType
	 *            new fill type
	 */
	void setFillType(FillType fillType);

	/**
	 * @return hatching distance
	 */
	int getHatchingDistance();

	/**
	 * Just sets the fill symbol, fill type must be changed to SYMBOL separately
	 * 
	 * @param symbol
	 *            Unicode symbol used for fill
	 */
	void setFillSymbol(String symbol);

	/**
	 * @return true if this can be filled
	 */
	boolean isFillable();

	/**
	 * @return true for valid functions
	 */
	boolean isGeoFunction();

	/** @return true if tracing is posible */
	boolean isTraceable();

	/**
	 * @return hatching angle in degrees
	 */
	double getHatchingAngle();

	/**
	 * Tries to load the image using the given fileName.
	 * 
	 * @param fileName
	 *            filename
	 */
	void setImageFileName(String fileName);

	/**
	 * @return true if showing trimmed lines
	 */
	boolean getShowTrimmedIntersectionLines();

	/**
	 * @return whether object should be visible in at least one view
	 */
	boolean isVisible();

	/**
	 * @return latex cache
	 */
	public LaTeXCache getLaTeXCache();

	/**
	 * Updates visual properties and repaints this object
	 * 
	 * @param prop
	 *            property
	 */
	public void updateVisualStyleRepaint(GProperty prop);

	/**
	 * Just changes the basic visual styles. If the style of a geo is reset this
	 * is required as we don't want to overwrite advanced settings in that case.
	 * 
	 * @param geo
	 *            source geo
	 */
	void setVisualStyle(GeoElement geo);

	/**
	 * @return whether this geo can be parametrized
	 */
	boolean isParametric();

	/**
	 * We may need a simple method to set the label, as in the CopyPaste class.
	 * 
	 * @param labelSimple
	 *            the label to set
	 */
	void setLabelSimple(String labelSimple);

	/**
	 * @return condition to show this geo
	 */
	GeoBoolean getShowObjectCondition();

	/**
	 * @return dynamic color as list of numbers {R,G,B} / {H,S,L} / {H,S,B}
	 */
	GeoList getColorFunction();

	/**
	 * @return color space of dynamic color
	 */
	int getColorSpace();

	/**
	 * @return true if this is allowed to be drawn in EV
	 */
	boolean isSetEuclidianVisible();

	/**
	 * Copy advanced properties -- cond. visibility, dynamic colors, TODO
	 * corners Used in macros where we can't reference the objects directly
	 * 
	 * @param geo
	 *            style source
	 */
	void setAdvancedVisualStyleCopy(GeoElementND geo);

	/**
	 * @param algorithm
	 *            algorithm responsible for drawing this
	 */
	void setDrawAlgorithm(DrawInformationAlgo algorithm);

	/**
	 * Returns whether this GeoElement can be moved in Euclidian View. Note:
	 * this is needed for texts and points on path
	 * 
	 * @return true for moveable objects
	 */
	public boolean isMoveable();

	/**
	 * Returns the definition of this GeoElement for the input field, e.g. A1 =
	 * 5, B1 = A1 + 2
	 *
	 * @return definition for input field
	 */
	String getDefinitionForInputBar();

	/**
	 * @param tpl
	 *            string template
	 * @return definition string
	 */
	String getDefinition(StringTemplate tpl);

	/**
	 * Used to convert various interfaces into GeoElement
	 * 
	 * @return this
	 */
	GeoElement toGeoElement();

	/**
	 * @return whether this is output of random() or randomizable algo (number,
	 *         list, list element)
	 */
	boolean isRandomGeo();

	/**
	 * Randomize this and update parent algo (no cascade)
	 */
	void updateRandomGeo();

	/**
	 * add algorithm to dependency list of this GeoElement
	 * 
	 * @param algorithm
	 *            algorithm directly dependent on this
	 */
	void addAlgorithm(AlgoElement algorithm);

	/**
	 * Get a free label. Try the suggestedLabel first
	 * 
	 * @param suggestedLabel
	 *            label to be tried first
	 * @return free label -- either suggestedLabel or suggestedLabel_index
	 */
	String getFreeLabel(String suggestedLabel);

	/**
	 * if an object has a fixed descendent, we want to set it undefined
	 */
	void removeOrSetUndefinedIfHasFixedDescendent();

	/**
	 * remove algorithm from dependency list of this GeoElement
	 * 
	 * @param algorithm
	 *            algorithm to be removed
	 */
	void removeAlgorithm(AlgoElement algorithm);

	/**
	 * add algorithm to update sets up the construction graph
	 * 
	 * @param algorithm
	 *            algo to be added
	 * @return true if added
	 */
	boolean addToUpdateSets(AlgoElement algorithm);

	/**
	 * remove algorithm from update sets up the construction graph
	 * 
	 * @param algorithm
	 *            algo to be removed
	 * @return true if removed
	 */
	boolean removeFromUpdateSets(AlgoElement algorithm);

	/**
	 * Adds the given algorithm to the update set this GeoElement. Note: the
	 * algorithm is NOT added to the algorithm list, i.e. the dependency graph
	 * of the construction.
	 * 
	 * @param algorithm
	 *            algorithm to be added
	 */
	void addToUpdateSetOnly(AlgoElement algorithm);

	/**
	 * @return true if can be removed as input of algo -- only if just one algo
	 *         left
	 */
	boolean canBeRemovedAsInput();

	/**
	 * @return true for CAS cells
	 */
	boolean isGeoCasCell();

	/**
	 * @return the smallest possible construction index for this object in its
	 *         construction. For an independent object 0 is returned.
	 */
	int getMinConstructionIndex();

	/**
	 * @param caption
	 *            raw caption
	 * @return true if new caption is not null
	 */
	boolean setCaption(String caption);

	/**
	 * @return true for conics
	 */
	boolean isGeoConic();

	/**
	 * Adds the given algorithm to the dependency list of this GeoElement. The
	 * algorithm is NOT added to the updateSet of this GeoElement! I.e. when
	 * updateCascade() is called the given algorithm will not be updated.
	 * 
	 * @param algorithm
	 *            algo to be added
	 */
	void addToAlgorithmListOnly(AlgoElement algorithm);

	/**
	 * 
	 * @return true if this can be listed as input for a macro
	 */
	boolean isVisibleInputForMacro();

	/**
	 * @see ConstructionElement#getNameDescription()
	 * 
	 * @return type and name of this construction element (e.g. "Point A").
	 */
	String getNameDescription();

	/**
	 * Return script for event type (localized if ggbscript)
	 * 
	 * @param type
	 *            event type
	 * @return script
	 */
	Script getScript(EventType type);

	/**
	 * @return deafult label for this geo (depends on type)
	 */
	String getDefaultLabel();

	/**
	 * @return Type, label and definition information about this GeoElement (for
	 *         tooltips and error messages)
	 */
	String getLongDescription();

	/**
	 * return black if the color is white, so it can be seen
	 * 
	 * @return color for algebra view (same as label or black)
	 */
	GColor getAlgebraColor();

	/**
	 * @return true for polylines
	 */
	boolean isGeoPolyLine();

	/**
	 * Returns the label of this object before rename() was called.
	 * 
	 * @return label before renaming
	 */
	String getOldLabel();

	/**
	 * @param flag
	 *            true to make this selected
	 * @return true if state is changed
	 */
	boolean setSelected(boolean flag);

	/**
	 * Returns all children (of type GeoElement) that depend on this object.
	 * 
	 * @return set of all children of this geo
	 */
	TreeSet<GeoElement> getAllChildren();

	/**
	 * @param selection
	 *            true to allow selection
	 */
	void setSelectionAllowed(boolean selection);

	/**
	 * @return layer of this geo (0 to 9)
	 */
	int getLayer();

	/**
	 * @param mode
	 *            new tooltip mode
	 */
	void setTooltipMode(int mode);

	/**
	 * Sets layer
	 * 
	 * @param layer
	 *            layer from 0 to 9
	 */
	void setLayer(int layer);

	/**
	 * @param viewId
	 *            view id
	 */
	void addView(int viewId);

	/**
	 * Make this invisible in given view
	 * 
	 * @param viewId
	 *            view id
	 */
	void removeView(int viewId);

	/**
	 * In future, this can be used to turn on/off whether transformed objects
	 * have the same style as the original object
	 * 
	 * @param geo
	 *            source geo
	 */
	void setVisualStyleForTransformations(GeoElement geo);

	/**
	 * Set definition to null, no checks
	 */
	public void resetDefinition();

	/**
	 * Decides if definition differs from value as String. If so, AV should
	 * display both rows.
	 * 
	 * @return true, only if AV should display 2 rows in 'Definition And Value'
	 *         style.
	 */
	DescriptionMode getDescriptionMode();

	/**
	 * Check that this is a GeoFunctionable representing a function R-&gt; R
	 * (i.e. not a circle or boolean function)
	 * 
	 * @return true for functionables
	 */
	boolean isRealValuedFunction();

	/**
	 * @return true if the given GeoElement geo is to be drawn with LaTeX in
	 *         AV/Spreadsheet
	 */
	boolean isLaTeXDrawableGeo();

	/**
	 * Returns the next free indexed label using the given prefix.
	 * 
	 * @param prefix
	 *            e.g. "c"
	 * @return indexed label, e.g. "c_2"
	 */
	String getIndexLabel(String prefix);

	/**
	 * @return true for cartesian curves
	 */
	boolean isGeoCurveCartesian();

	/**
	 * Returns whether this object is dependent on geo.
	 * 
	 * @param geo
	 *            other geo
	 * @return true if this object is dependent on geo.
	 */
	boolean isChildOf(GeoElementND geo);

	/**
	 * Sets all visual values from given GeoElement. This will also affect
	 * tracing, label location and the location of texts for example.
	 * 
	 * @param geo
	 *            source geo
	 * @param keepAdvanced
	 *            true to skip copying color function and visibility condition
	 */
	void setAllVisualProperties(GeoElement geo, boolean keepAdvanced);

	/**
	 * @param cond
	 *            new condition to show this geo
	 * @throws CircularDefinitionException
	 *             if this == cond
	 */
	void setShowObjectCondition(GeoBoolean cond)
			throws CircularDefinitionException;

	/**
	 *
	 * @param useChangeable if false, point on path is ignored
	 * @param useOutputValueString if true, use outputValueString rather than valueString
	 * @return Calls the 2 parametrized version of the function, with the third parameter: StringTemplate.editTemplate
	 */
	String getRedefineString(boolean useChangeable, boolean useOutputValueString);

	/**
	 * Returns definition or value string of this object. Automatically
	 * increases decimals to at least 5, e.g. FractionText[4/3] -&gt;
	 * FractionText[1.333333333333333]
	 *
	 * @param useChangeable
	 *            if false, point on path is ignored
	 * @param useOutputValueString
	 *            if true, use outputValueString rather than valueString
	 * @return definition or value string of this object
	 */
	String getRedefineString(boolean useChangeable, boolean useOutputValueString,
							 StringTemplate tpl);

	/**
	 * @return true for auxiliary objects
	 */
	boolean isAuxiliaryObject();

	/**
	 * String getFormulaString(int, boolean substituteNumbers) substituteNumbers
	 * determines (for a function) whether you want "2*x^2" or "a*x^2" returns a
	 * string representing the formula of the GeoElement in the following
	 * formats: getFormulaString(StringType.GIAC) eg sqrt(x)
	 * getFormulaString(StringType.LATEX) eg \sqrt(x)
	 * getFormulaString(StringType.LIBRE_OFFICE) eg sqrt {x}
	 * getFormulaString(StringType.GEOGEBRA) eg sqrt(x)
	 * getFormulaString(StringType.GEOGEBRA_XML)
	 * 
	 * @param tpl
	 *            string template
	 * @param substituteNumbers
	 *            true to substitute numbers
	 * @return formula string
	 */
	String getFormulaString(StringTemplate tpl, boolean substituteNumbers);

	/**
	 * Returns the value of this GeoElement for the input field, e.g. A1 = 5, B1
	 * = A1 + 2
	 * 
	 * @return value for input field
	 */
	String getValueForInputBar();

	/**
	 * @return true for angles
	 */
	boolean isGeoAngle();

	/**
	 * @return true for lines
	 */
	boolean isGeoLine();

	/**
	 * renames this GeoElement to newLabel.
	 * 
	 * @param newLabel
	 *            new label
	 * @return true if label was changed
	 * @throws MyError
	 *             : if new label is already in use
	 */
	boolean rename(String newLabel);

	/**
	 * @return true for images
	 */
	boolean isGeoImage();

	/**
	 * Sets label of a GeoElement and updates GeoElement table (label,
	 * GeoElement). This method should only be used by MyXMLHandler.
	 * 
	 * @param label
	 *            label
	 */
	void setLoadedLabel(String label);

	/**
	 * copies the scripts from another geo. Used when redefining (so that the
	 * scripts aren't "deleted")
	 * 
	 * @param oldGeo
	 *            old GeoElement
	 */
	void setScripting(GeoElement oldGeo);

	/**
	 * @return true for GeoLists
	 */
	boolean isGeoList();

	/**
	 * @return true for booleans
	 */
	boolean isGeoBoolean();

	/**
	 * 
	 * @return true if has changeable coord parent numbers (e.g. point defined
	 *         by sliders)
	 */
	boolean hasChangeableCoordParentNumbers();

	/**
	 * @return true for planes
	 */
	boolean isGeoPlane();

	/**
	 * @return "main" direction of the element, e.g. for seeing it in a
	 *         "standard" view (for 3D). E.g. orthogonal to a plane, along a
	 *         line, ...
	 */
	Coords getMainDirection();

	/**
	 * Returns all free parent points of this GeoElement.
	 * 
	 * @param view
	 *            view
	 * @return all free parent points of this GeoElement.
	 */
	public ArrayList<GeoPointND> getFreeInputPoints(
			final EuclidianViewInterfaceSlim view);

	/**
	 * Returns whether this object's class implements the interface
	 * Translateable.
	 * 
	 * @return whether this object's class implements the interface
	 *         Translateable.
	 */
	boolean isTranslateable();

	/**
	 * @param view
	 *            view
	 * @return true if moveable in the view
	 */
	boolean isMoveable(EuclidianViewInterfaceSlim view);

	/**
	 * @return true for textfields (=Input Boxes)
	 */
	boolean isGeoInputBox();

	/**
	 * Returns whether this (dependent) GeoElement has input points that can be
	 * moved in Euclidian View.
	 * 
	 * @param view
	 *            view
	 * @return whether this geo has only moveable input points
	 */
	boolean hasMoveableInputPoints(EuclidianViewInterfaceSlim view);

	/**
	 * Returns whether this GeoElement can be changed directly. Note: for points
	 * on lines this is different than isIndependent()
	 * 
	 * @return whether this geo can be changed directly
	 */
	boolean isChangeable();

	/**
	 * @return true for implicit curve
	 */
	boolean isGeoImplicitCurve();

	/**
	 * Returns whether the label contains any indices (i.e. '_' chars).
	 * 
	 * @return whether the label contains any indices (i.e. '_' chars).
	 */
	boolean hasIndexLabel();

	/**
	 * @return true for limited paths
	 */
	boolean isLimitedPath();

	long getID();

	int compareTo(ConstructionElement cycleNext);

	/**
	 * @param viewID
	 *            view id
	 * @return whether this geo is visible in given view
	 */
	boolean isVisibleInView(int viewID);

	/**
	 * @return true if visible in view for plane
	 */
	boolean isVisibleInViewForPlane();

	/**
	 * @return true if this can be edited in AV directly
	 */
	boolean isAlgebraViewEditable();

	/**
	 * Returns true if color was explicitly set
	 * 
	 * @return true if color was explicitly set
	 */
	boolean isColorSet();

	/**
	 * 
	 * @return true if the geo is drawable in 3D view
	 */
	boolean hasDrawable3D();

	/**
	 * @return is geo created with shape tool
	 */
	boolean isShape();

	/**
	 * @return is geo created with mask tool
	 */
	boolean isMask();

	/**
	 * @param flag
	 *            true to make this highlighted
	 * 
	 * @return true if state is changed
	 */
	public boolean setHighlighted(final boolean flag);

	/**
	 * Also copy advanced settings of this object.
	 *
	 * @param defaultGeo
	 *            source geo
	 */
	void setAdvancedVisualStyle(GeoElement defaultGeo);

	/**
	 * add Caption simple for reader.
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 * @return true if caption was added - i.e. when it is not empty.
	 */
	boolean addAuralCaption(ScreenReaderBuilder sb);

	/**
	 * add geo type for reader.
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralType(ScreenReaderBuilder sb);

	/**
	 * add geo type and its label for reader.
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralLabel(ScreenReaderBuilder sb);

	/**
	 * add Caption for reader if defined, type and label otherwise.
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralName(ScreenReaderBuilder sb);

	/**
	 * Add content aural description if any.
	 * 
	 * @param loc
	 *            The Localization object
	 *
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralContent(Localization loc, ScreenReaderBuilder sb);

	/**
	 * Add aural text for status of the geo.
	 * 
	 * @param loc
	 *            The Localization object
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralStatus(Localization loc, ScreenReaderBuilder sb);

	/**
	 * Add aural text for the possible operations of the geo.
	 * 
	 * @param loc
	 *            The Localization object
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralOperations(Localization loc, ScreenReaderBuilder sb);

	/**
	 * 
	 * @return text to be read when pressing space key.
	 */
	String getAuralTextForSpace();

	/**
	 * 
	 * @return text to be read when geo was moved.
	 */
	String getAuralTextForMove();

	/**
	 * See DECORATION_ constants
	 * 
	 * @return decoration type, eg 3 lines
	 */
	public int getDecorationType();

	/**
	 * @param sb
	 *            screen reader output builder
	 * @return text that screen readers should read.
	 */
	String getAuralText(ScreenReaderBuilder sb);

	/**
	 * @return the value of geo as an expression for screen readers.
	 */
	String getAuralExpression();

	/**
	 * @return whether the to-be-drawn geoElement is filled, meaning the
	 *         alpha-value is greater zero, or hatching is enabled.
	 */
	boolean isFilled();

	/**
	 * @param i
	 *            line type for hidden lines
	 */
	void setLineTypeHidden(int i);

	/**
	 * @return true if geo is function or line and should be shown
	 *         in the Table Values view
	 */
	boolean hasTableOfValues();

	/**
	 * 
	 * @return auto color scheme
	 */
	AutoColor getAutoColorScheme();

	/**
	 * @return true if showable in EV
	 */
	boolean isEuclidianShowable();

	/**
	 * @return the unwrapped geo
	 */
	GeoElementND unwrapSymbolic();

	/**
	 * @return app
	 */
	App getApp();

	/**
	 * Recursively checks the algo parents to determine whether the element's value is safe to show
	 * to the user.
	 * @return True if it's allowed to show the element's value, otherwise false.
	 */
	boolean isAllowedToShowValue();

	/**
	 * Tells whether the equation was typed directly from the user
	 *
	 * @return true if the equation was typed by the user (and not created via
	 *         command or tool)
	 */
	boolean isFunctionOrEquationFromUser();

	/**
	 * @return true for cartesian surfaces
	 */
	boolean isGeoSurfaceCartesian();

	/**
	 * @return if it requires a special editing mode in symbolic input boxes
	 * (vector, matrix, point whose elements can be edited, as opposed to sth like A+B)
	 */
	boolean hasSpecialEditor();
}
