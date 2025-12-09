/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;

/**
 * Cartesian axis
 * 
 * @author Markus
 */
public class GeoAxis extends GeoLine implements GeoAxisND {

	private int type;
	// for numbers and ticks
	private int ticksize = 5; // TODO

	/** color used when axis is colored (not black) in 3D view */
	private GColor coloredColorFor3D;

	/**
	 * Creates new axis
	 * 
	 * @param cons
	 *            construction
	 * @param type
	 *            GeoAxisND.X_AXIS or GeoAxisND.Y_AXIS
	 */
	public GeoAxis(Construction cons, int type) {
		super(cons);
		this.type = type;
		GeoPoint origin = new GeoPoint(cons);
		origin.setCoords(0, 0, 1);
		setStartPoint(origin);

		GeoPoint end = new GeoPoint(cons);
		
		switch (type) {
		default:
		case X_AXIS:
			setCoords(0, 1, 0);
			label = "xAxis";
			coloredColorFor3D = GColor.RED;
			end.setCoords(1, 0, 1);
			setEndPoint(end);
			break;

		case Y_AXIS:
			setCoords(-1, 0, 0);
			label = "yAxis";
			coloredColorFor3D = GColor.DARK_GREEN;
			end.setCoords(0, 1, 1);
			setEndPoint(end);
			break;
		}

		setLabelSet(true);
		setFixed(true);
		setLabelVisible(false);
	}

	@Override
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
			return getLoc().getMenu(label);
		}
		return label;

	}

	/**
	 * Returns whether str is equal to this axis' label.
	 * 
	 * @param str
	 *            string for comparison
	 * @return whether str is equal to this axis' label.
	 */
	public boolean equalsLabel(String str) {
		if (str == null) {
			return false;
		}
		return str.equals(label) || str.equals(getLoc().getMenu(label));
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

	@Override
	public String getUnitLabel() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public int getTickStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getShowNumbers() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getTickSize() {
		return ticksize;
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
		return type == X_AXIS ? Coords.VX : Coords.VY;
	}

	@Override
	public boolean isAxis() {
		return true;
	}

	@Override
	public boolean isRenameable() {
		return false;
	}

	@Override
	public final boolean isSelectionAllowed(EuclidianViewInterfaceSlim ev) {

		EuclidianSettings settings = ev == null
				? kernel.getApplication().getActiveEuclidianView().getSettings()
				: ev.getSettings();

		if (settings != null) {
			return settings.isSelectionAllowed(type);
		}

		return true;
	}
	
	@Override
	public int getLineThickness() {
		return EuclidianStyleConstants.AXES_THICKNESS;
	}

	@Override
	public boolean isProtected(EventType eventType) {
		return true;
	}

	@Override
	public void setColoredFor3D(boolean colored) {
		if (colored) {
			setObjColor(coloredColorFor3D);
		} else {
			setObjColor(GColor.BLACK);
		}
	}

}
