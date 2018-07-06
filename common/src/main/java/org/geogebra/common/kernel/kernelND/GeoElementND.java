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
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
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
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.Localization;
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
public interface GeoElementND extends ExpressionValue {
	/**
	 * @param string
	 *            new label
	 */
	void setLabel(String string);

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
	 * @return true if label was set
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
	 * @return parent algorithm
	 */
	public AlgoElement getParentAlgorithm();

	/**
	 * @return true if this is defined
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
	 * @return true for points on path
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
	 * @return set of algos that depend on this geo
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
	 * @return label mode, may be GeoElement.LABEL_NAME, LABEL_VALUE etc
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
	 * @return whether this is a point
	 */
	boolean isGeoPoint();

	/**
	 * @return whether this is a number
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
	 * @return whether this is a ray
	 */
	boolean isGeoPolygon();

	/**
	 * @return whether this is a ray
	 */
	boolean isGeoRay();

	/**
	 * @return whether this is a conic arc
	 */
	boolean isGeoConicPart();

	/**
	 * @return whether this is a vector
	 */
	boolean isGeoVector();

	/**
	 * @return geo type
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
	 *            dynamic color
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

	void doRemove();

	boolean hasChildren();

	boolean isVisibleInView3D();

	/**
	 * @param geo
	 *            other geo
	 * @return whether the elements are equal in geometric sense (for congruency
	 *         use isCongruent)
	 */
	public boolean isEqual(GeoElementND geo);

	Kernel getKernel();

	boolean doHighlighting();

	/**
	 * @return alpha value (transparency)
	 * 
	 *         NOTE: can be -1 for lists, see GeoList.getAlphaValue(),
	 *         GeoList.setgetAlphaValue()
	 */
	double getAlphaValue();

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

	FillType getFillType();

	/**
	 * 
	 * @return color of label
	 */
	GColor getLabelColor();

	String getLabelDescription();

	GColor getObjectColor();

	String getImageFileName();

	Object getLaTeXdescription();

	/**
	 * @return color of object for selection
	 */
	GColor getSelColor();

	boolean isHatchingEnabled();

	void setHatchingAngle(int hatchingAngle);

	/**
	 * Changes transparency of this geo
	 * 
	 * @param alpha
	 *            new alpha value
	 */
	void setAlphaValue(double alpha);

	String getCaption(StringTemplate defaulttemplate);

	MyImage getFillImage();

	String getFillSymbol();

	void setFillType(FillType fillType);

	int getHatchingDistance();

	void setFillSymbol(String symbol);

	boolean isFillable();

	boolean isGeoFunction();

	boolean isTraceable();

	double getHatchingAngle();

	void setImageFileName(String fileName);

	boolean getShowTrimmedIntersectionLines();

	/**
	 * @return whether object should be visible in at least one view
	 */
	boolean isVisible();

	public LaTeXCache getLaTeXCache();

	public void updateVisualStyleRepaint(GProperty prop);

	/**
	 * Just changes the basic visual styles. If the style of a geo is reset this
	 * is required as we don't want to overwrite advanced settings in that case.
	 * 
	 * @param geo
	 *            source geo
	 */
	void setVisualStyle(GeoElement geo);

	boolean isParametric();

	/**
	 * We may need a simple method to set the label, as in the CopyPaste class.
	 * 
	 * @param labelSimple
	 *            the label to set
	 */
	void setLabelSimple(String labelSimple);

	GeoBoolean getShowObjectCondition();

	GeoList getColorFunction();

	/**
	 * @return color space of dynamic color
	 */
	int getColorSpace();

	boolean isSetEuclidianVisible();

	/**
	 * Copy advanced properties -- cond. visibility, dynamic colors, TODO
	 * corners Used in macros where we can't reference the objects directly
	 * 
	 * @param geo
	 *            style source
	 */
	void setAdvancedVisualStyleCopy(GeoElementND geo);

	void setDrawAlgorithm(DrawInformationAlgo copy);

	/**
	 * @return whether this can be moved
	 */
	public boolean isMoveable();

	/**
	 * Returns the definition of this GeoElement for the input field, e.g. A1 =
	 * 5, B1 = A1 + 2
	 *
	 * @return definition for input field
	 */
	String getDefinitionForInputBar();

	String getDefinition(StringTemplate tpl);

	/**
	 * Used to convert various interfaces into GeoElement
	 * 
	 * @return this
	 */
	GeoElement toGeoElement();

	/**
	 * @return whether this is output of random() or randomizable algo
	 */
	boolean isRandomGeo();

	/**
	 * Randomize this and update parent algo (no cascade)
	 */
	void updateRandomGeo();

	void addAlgorithm(AlgoElement algoElement);

	String getFreeLabel(String label);

	/**
	 * if an object has a fixed descendent, we want to set it undefined
	 */
	void removeOrSetUndefinedIfHasFixedDescendent();

	void removeAlgorithm(AlgoElement algoAttachCopyToView);

	boolean addToUpdateSets(AlgoElement algorithm);

	boolean removeFromUpdateSets(AlgoElement algorithm);

	void addToUpdateSetOnly(AlgoElement algoElement);

	boolean canBeRemovedAsInput();

	boolean isGeoCasCell();

	int getMinConstructionIndex();

	boolean setCaption(String object);

	boolean isGeoConic();

	void addToAlgorithmListOnly(AlgoElement algoElement);

	boolean isVisibleInputForMacro();

	String getNameDescription();

	Script getScript(EventType type);

	String getDefaultLabel();

	String getLongDescription();

	/**
	 * return black if the color is white, so it can be seen
	 * 
	 * @return color for algebra view (same as label or black)
	 */
	GColor getAlgebraColor();

	boolean isGeoPolyLine();

	Object getOldLabel();

	/**
	 * @param b
	 * @param flag
	 *            true to make this selected
	 * @return true if state is changed
	 */
	boolean setSelected(boolean b);

	TreeSet<GeoElement> getAllChildren();

	void setSelectionAllowed(boolean b);

	/**
	 * @return layer of this geo (0 to 9)
	 */
	int getLayer();

	/**
	 * @param mode
	 *            new tooltip mode
	 */
	void setTooltipMode(int tooltipOff);

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
	void addView(int viewEuclidian);

	void removeView(int viewEuclidian2);

	/**
	 * In future, this can be used to turn on/off whether transformed objects
	 * have the same style as the original object
	 * 
	 * @param geo
	 *            source geo
	 */
	void setVisualStyleForTransformations(GeoElement geo);

	public void resetDefinition();

	DescriptionMode needToShowBothRowsInAV();

	boolean isGeoFunctionable();

	boolean isLaTeXDrawableGeo();

	String getIndexLabel(String labelPrefix);

	boolean isGeoCurveCartesian();

	boolean isChildOf(GeoElementND autoCreateGeo);

	void setAllVisualProperties(GeoElement value, boolean b);

	void setShowObjectCondition(GeoBoolean newConditionToShowObject)
			throws CircularDefinitionException;

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
	String getRedefineString(boolean useChangeable, boolean useOutputValueString);

	/**
	 * @return true for auxiliary objects
	 */
	boolean isAuxiliaryObject();

	String getFormulaString(StringTemplate latextemplate, boolean b);

	String getValueForInputBar();

	boolean isGeoAngle();

	boolean isGeoLine();

	boolean rename(String newLabel);

	boolean isGeoImage();

	void setLoadedLabel(String label);

	void setScripting(GeoElement value);

	/**
	 * @return true for GeoLists
	 */
	boolean isGeoList();

	boolean isGeoBoolean();

	boolean hasChangeableCoordParentNumbers();

	boolean isGeoPlane();

	Coords getMainDirection();

	public ArrayList<GeoPointND> getFreeInputPoints(
			final EuclidianViewInterfaceSlim view);

	boolean isTranslateable();

	boolean isMoveable(EuclidianViewInterfaceSlim view);

	boolean isGeoInputBox();

	void recordChangeableCoordParentNumbers(EuclidianView view);

	boolean hasMoveableInputPoints(EuclidianViewInterfaceSlim view);

	boolean isChangeable();

	boolean isGeoImplicitCurve();

	boolean hasIndexLabel();

	/**
	 * @return true for limited paths
	 */
	boolean isLimitedPath();

	long getID();

	int compareTo(ConstructionElement cycleNext);

	/**
	 * @param viewId
	 *            view id
	 * @return whether this geo is visible in given view
	 */
	boolean isVisibleInView(int viewID);

	boolean isVisibleInViewForPlane();

	boolean isAlgebraViewEditable();

	/**
	 * Returns true if color was explicitly set
	 * 
	 * @return true if color was explicitly set
	 */
	boolean isColorSet();

	boolean hasDrawable3D();

	boolean isShape();

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
	boolean addAuralCaption(StringBuilder sb);

	/**
	 * add geo type for reader.
	 * 
	 * @param loc
	 *            The Localization object
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralType(Localization loc, StringBuilder sb);

	/**
	 * add geo type and its label for reader.
	 * 
	 * @param loc
	 *            The Localization object
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralLabel(Localization loc, StringBuilder sb);

	/**
	 * add Caption for reader if defined, type and label otherwise.
	 * 
	 * @param loc
	 *            The Localization object
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralName(Localization loc, StringBuilder sb);

	/**
	 * Add content aural description if any.
	 * 
	 * @param loc
	 *            The Localization object
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralContent(Localization loc, StringBuilder sb);

	/**
	 * Add aural text for status of the geo.
	 * 
	 * @param loc
	 *            The Localization object
	 * 
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralStatus(Localization loc, StringBuilder sb);

	/**
	 * Add aural text for the possible operations of the geo.
	 * 
	 * @param loc
	 *            The Localization object
	 * @param sb
	 *            StringBuilder to add to.
	 */
	void addAuralOperations(Localization loc, StringBuilder sb);

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

}
