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

package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class that describes the geometry of buttons for 3D view
 * 
 * @author ggb3D
 *
 */
public class PlotterViewInFrontOf {

	static private float start = 60f;
	static private float end = 0f;

	private int[] index;

	/**
	 * common constructor
	 */
	public PlotterViewInFrontOf(Manager manager) {

		manager.setScalerIdentity();

		index = new int[1];

		// arrow
		PlotterBrush brush = manager.getBrush();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);

		// sets the thickness for arrows
		brush.setThickness(7, 1f);

		// brush.setAffineTexture(0.5f, 0.125f);

		brush.start(-1);
		brush.setColor(GColor.GRAY, 0.5f);
		// brush.setThickness(thickness); //re sets the thickness
		brush.segment(new Coords(0, 0, start, 1), new Coords(0, 0, end, 1));
		index[0] = brush.end();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
		manager.setScalerView();
	}

	// ////////////////////////////////
	// INDEX
	// ////////////////////////////////

	/**
	 * return geometry index for view in front of arrow
	 * 
	 * @return geometry index for view in front of arrow
	 */
	public int getIndex() {
		return index[0];
	}

}
