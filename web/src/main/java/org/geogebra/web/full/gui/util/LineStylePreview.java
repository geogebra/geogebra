package org.geogebra.web.full.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.web.html5.main.AppW;

/**
 * Widget to show line with thickness and style.
 * 
 * @author Laszlo Gal
 *
 */
public class LineStylePreview extends StylePreview {

	private GeoLine line;
	private DrawLine drawLine;

	/**
	 * @param app
	 *            application
	 * @param width
	 *            width of line
	 * @param height
	 *            height of line
	 */
	public LineStylePreview(AppW app, int width, int height) {
		super(app, width, height);
	}

	@Override
	protected void createPreviewGeo() {
		line = new GeoLine(app.getKernel().getConstruction(), 0, 1, 0);
		line.setLineType(1);
		drawLine = new DrawLine(app.getActiveEuclidianView(), line);
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
		line.setObjColor(color);
		line.setLineThickness(thickness);
		int lineStyle = EuclidianView.getLineType(typeIdx);
		line.setLineType(lineStyle);
		line.updateVisualStyleRepaint(GProperty.LINE_STYLE);
		clear();
		drawLine.drawStylePreview(g2, getMarginX(), getMarginY(),
				getOffsetWidth() == 0 ? 30 : getOffsetWidth());
	}
}
