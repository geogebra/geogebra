package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.EuclidianPen;
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
		super(app, width, height);
	}

	@Override
	protected void createPreviewGeo() {
		pen = app.getActiveEuclidianView().getEuclidianController().getPen();
	}

	/**
	 * Update preview
	 */
	public void update() {
		clear();
		pen.drawStylePreview(g2, pen.getPenColor(), pen.getPenSize(),
				getMarginX(), getMarginY(), 30);
	}

}
