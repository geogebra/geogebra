package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.web.html5.main.AppW;

/**
 * Widget to show line with thickness and style.
 * 
 * @author Laszlo Gal
 *
 */
public class PenPreview extends StylePreview {
	private EuclidianPen pen;

	/**
	 * @param app
	 *            app
	 * @param width
	 *            pixel width
	 * @param height
	 *            pixel height
	 */
	public PenPreview(AppW app, int width, int height) {
		super(width, height);
		pen = app.getActiveEuclidianView().getEuclidianController().getPen();
	}

	/**
	 * Update preview
	 */
	public void update() {
		clear();
		drawStylePreview(pen.getPenColor(), pen.getPenSize(),
				EuclidianStyleConstants.LINE_TYPE_FULL, 30);
	}

}
