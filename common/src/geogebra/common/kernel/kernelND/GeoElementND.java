/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel.kernelND;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgorithmSet;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.ToGeoElement;
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
}
