/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package org.geogebra.common.kernel.kernelND;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgorithmSet;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.ToGeoElement;
import org.geogebra.common.plugin.GeoClass;
/**
 * Common interface for all interfaces that represent GeoElements
 * @author Zbynek
 *
 */
public interface GeoElementND extends ExpressionValue, ToGeoElement{
	/**
	 * @param string new label
	 */
	void setLabel(String string);
	/**
	 * Updates this geo
	 */
	void update();

	/**
	 * @param objectColor object color
	 */
	void setObjColor(GColor objectColor);

	/**
	 * @param visible whether should be visible in EV
	 */
	void setEuclidianVisible(boolean visible);
	
	/**
	 * @return true if this is visible in EV
	 */
	boolean isEuclidianVisible();
	
	/**
	 * @return true if label is visible
	 */
	boolean isLabelVisible();
	
	/**
	 * @return true if label was set
	 */
	public boolean isLabelSet();

	/**
	 * @param tpl template
	 * @return label or definition command
	 */
	public String getLabel(StringTemplate tpl);

	/**
	 * @return true for infinite numbers / points
	 */
	public boolean isInfinite();
	
	/**
	 * Update visual style and notify kernel
	 */
	public void updateVisualStyle();

	/**
	 * Remove this from construction
	 */
	public void remove();
	
	/**
	 * @return true if tracing to spreadsheet
	 */
	public boolean getSpreadsheetTrace();
	
	/**
	 * @param cons construction
	 * @return copy of this element in given construction
	 */
	public GeoElement copyInternal(Construction cons);

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
	 * Makes this geo undefined
	 */
	public void setUndefined();

	/**
	 * @param type line type
	 */
	public void setLineType(int type);

	/**
	 * @param th line thickness
	 */
	public void setLineThickness(int th);
	
	/**
	 * @return true if it has a line opacity value between 0 and 255
	 */
	public boolean hasLineOpacity();
	
	/**
	 * Sets the line opacity for this {@code GeoElement}. </br>
	 * @param opacity opacity value between 0 - 255
	 */
	public void setLineOpacity(int opacity);
	
	/**
	 * @return The value for the line opacity (0 - 255). </br>
	 * 			The default value is 255 (opaque)
	 */
	public int getLineOpacity();

	/**
	 * @param b true to make label visible
	 */
	public void setLabelVisible(boolean b);
	
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
	 * @param p point
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
	 * @return whether the update set
	 */
	boolean hasAlgoUpdateSet();
	
	
	/**
	 * @return whether this is instance of GeoElement3D
	 */
	public boolean isGeoElement3D();

	/**
	 * 
	 * @return true if is 3D region
	 */
	public boolean isRegion3D();

	/**
	 * @return whether this is instance of GeoText
	 */
	public boolean isGeoText();
	int getLabelMode();
	void setLabelMode(int labelName);
	//public Kernel getKernel();
	public String getLabelSimple();

	public void set(GeoElementND geo);
	/**
	 * Sets visibility if not given by condition to show object
	 * 
	 * @param visible
	 *            whether it should be visible
	 */
	public void setEuclidianVisibleIfNoConditionToShowObject(boolean visible);

	boolean isGeoPoint();

	boolean isGeoNumeric();

	boolean isGeoButton();

	String getRawCaption();

	Construction getConstruction();

	boolean isGeoPolyhedron();

	List<Integer> getViewSet();

	boolean isGeoSegment();

	boolean isGeoPolygon();

	boolean isGeoRay();

	boolean isGeoConicPart();

	boolean isGeoVector();

	GeoClass getGeoClassType();

	void setAuxiliaryObject(boolean auxilliary);

	void setFixed(boolean fix);

	void setLabelWanted(boolean b);

	void setColorSpace(int colorSpace);

	void setColorFunction(GeoList colorFunction);

	void setHatchingDistance(int hatchingDistance);

	String getXMLtypeString();

	void setVisibleInView3D(GeoElement geoList);

	void setViewFlags(List<Integer> viewSet);

	void setVisibleInViewForPlane(GeoElement geoList);

	boolean isDrawable();

	ExpressionNode getDefinition();

	void setDefinition(ExpressionNode def);
	
	/**
	 * Returns whether geo depends on this object.
	 * 
	 * @param geo other geo
	 * @return true if geo depends on this object.
	 */
	public boolean isParentOf(final GeoElementND geo);
}
