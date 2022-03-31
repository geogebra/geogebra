package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
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
		setCaption(new AxisCaptionText());
	}

	@Override
	public void update(String text0, GFont font0, GColor fgColor, Coords v,
			float xOffset0, float yOffset0, float zOffset0) {
		caption.update(text0, font0, fgColor);
		if (view.drawsLabels()) {
			update(text0, font0, null, fgColor, v, xOffset0, yOffset0, zOffset0);
		}
	}
}
