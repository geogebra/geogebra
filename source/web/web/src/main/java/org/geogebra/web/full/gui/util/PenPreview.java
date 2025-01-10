package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Widget to show line with thickness and style.
 * 
 * @author Laszlo Gal
 *
 */
public class PenPreview extends StylePreview {
	private EuclidianPen pen;

	/**
	 * @param pen
	 *            euclidian pen
	 * @param width
	 *            pixel width
	 * @param height
	 *            pixel height
	 */
	public PenPreview(EuclidianPen pen, int width, int height) {
		super(width, height);
		this.pen = pen;
	}

	/**
	 * Update preview
	 */
	public void update() {
		clear();
		drawStylePreview(pen.getPenColorWithOpacity(), pen.getPenSize(),
				EuclidianStyleConstants.LINE_TYPE_FULL);
	}
}
