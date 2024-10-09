package org.geogebra.web.full.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;

/**
 * Widget to show line with thickness and style.
 * 
 * @author Laszlo Gal
 *
 */
public class LineStylePreview extends StylePreview {

	/**
	 * @param width
	 *            width of line
	 * @param height
	 *            height of line
	 */
	public LineStylePreview(int width, int height) {
		super(width, height);
	}

	/**
	 * Update preview
	 * 
	 * @param thickness
	 *            of the line.
	 * @param typeIdx
	 *            index of type.
	 * @param color
	 *            of the line.
	 */
	public void update(int thickness, int typeIdx, GColor color) {
		clear();
		drawStylePreview(color, thickness, EuclidianView.getLineType(typeIdx));
	}
}
