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

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.matrix.Coords;

public class DrawAxisLabel3D extends DrawLabel3D {

	/**
	 * common constructor
	 * @param view 3D view
	 * @param drawable the 3D drawable
	 */
	public DrawAxisLabel3D(EuclidianView3D view,
			Drawable3D drawable) {
		super(view, drawable);
		setCaption(new AxisCaptionText(view.getSettings()));
	}

	@Override
	public void update(String text0, GFont font0, GColor fgColor, Coords v,
			float xOffset0, float yOffset0, float zOffset0, GGraphics2D measuringGraphics) {
		if (caption != null) {
			caption.update(text0, font0, fgColor);
			if (view.drawsLabels()) {
				update(text0, font0, v, xOffset0, yOffset0, zOffset0, measuringGraphics);
			}
		}
	}
}
