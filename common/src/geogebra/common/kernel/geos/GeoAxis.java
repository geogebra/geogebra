/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.geos;

import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.NumberFormatAdapter;
/**
 * Cartesian axis
 * @author Markus
 */
public class GeoAxis extends GeoLine implements GeoAxisND {

	private GeoPoint origin;
	private int type;

	private String axisLabel;
	/**
	 * Creates new axis
	 * @param cons construction
	 * @param type GeoAxisND.X_AXIS or GeoAxisND.Y_AXIS
	 */
	public GeoAxis(Construction cons, int type) {
		super(cons);
		this.type = type;
		origin = new GeoPoint(cons);
		origin.setCoords(0, 0, 1);
		setStartPoint(origin);

		switch (type) {
		case X_AXIS:
			setCoords(0, 1, 0);
			label = "xAxis";
			setAxisLabel("x");
			setObjColor(AwtFactory.prototype.newColor(255, 0, 0));// will be
																	// Color.RED
			break;

		case Y_AXIS:
			setCoords(-1, 0, 0);
			label = "yAxis";
			setAxisLabel("y");
			setObjColor(AwtFactory.prototype.newColor(0, 0.5f, 0));
			break;
		}
		setFixed(true);
		setLabelVisible(false);
	}

	public int getType() {
		return type;
	}

	/**
	 * Returns whether this object is available at the given construction step
	 * (this depends on this object's construction index).
	 */
	@Override
	public boolean isAvailableAtConstructionStep(int step) {
		// this method is overwritten
		// in order to make the axes available
		// in empty constructions too (for step == -1)
		return true;
	}

	@Override
	public String getLabel(StringTemplate tpl) {
		if (tpl.isPrintLocalizedCommandNames()) {
			return app.getPlain(label);
		}
		return label;

	}

	/**
	 * Returns whether str is equal to this axis' label.
	 * 
	 * @param str
	 *            string for comparsion
	 * @return whether str is equal to this axis' label.
	 */
	public boolean equalsLabel(String str) {
		if (str == null)
			return false;
		return str.equals(label) || str.equals(app.getPlain(label));
	}
	
	@Override
	public String getTypeString() {
		return "Line";
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.AXIS;
	}

	// /////////////////////////////////////
	// GEOAXISND INTERFACE
	// /////////////////////////////////////
	/**
	 * @param label new label for this axis
	 */
	public void setAxisLabel(String label) {
		axisLabel = label;
	}

	public String getAxisLabel() {
		return axisLabel;
	}

	public String getUnitLabel() {
		// TODO Auto-generated method stub
		return "";
	}

	public int getTickStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getShowNumbers() {
		// TODO Auto-generated method stub
		return true;
	}

	// for numbers and ticks
	private NumberFormatAdapter numberFormat;
	private double numbersDistance;
	private int numbersXOffset, numbersYOffset;
	private int ticksize = 5; // TODO

	public NumberFormatAdapter getNumberFormat() {
		return numberFormat;
	}

	public double getNumbersDistance() {
		return numbersDistance;
	}

	public int getTickSize() {
		return ticksize;
	}

	public void updateDecorations(double distance,
			NumberFormatAdapter numberFormat1, int xOffset, int yOffset,
			int labelXOffset, int labelYOffset) {
		this.numbersDistance = distance;
		this.numberFormat = numberFormat1;
		this.numbersXOffset = xOffset;
		this.numbersYOffset = yOffset;
		setLabelOffset(labelXOffset, labelYOffset);

	}

	public int getNumbersXOffset() {
		return numbersXOffset;
	}

	public int getNumbersYOffset() {
		return numbersYOffset;
	}

	/**
	 * overrides GeoElement method : this is a "constant" element, so the label
	 * is set
	 */
	@Override
	public boolean isLabelSet() {
		return true;
	}

	@Override
	public Coords getDirectionInD3() {
		if (type == X_AXIS)
			return new Coords(1, 0, 0, 0);

		return new Coords(0, 1, 0, 0);
	}

	@Override
	public boolean isAxis() {
		return true;
	}
}
