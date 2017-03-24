package org.geogebra.web.web.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Composite;

/**
 * Widget to show line with thickness and style.
 * 
 * @author Laszlo Gal
 *
 */
public class LineStylePreview extends Composite {
	/** The value canvas next to the slider */
	private Canvas canvas;

	private GGraphics2DW g2;
	private GeoLine line;
	private DrawLine drawLine;
	private int marginX = 0;
	private int marginY;

	public LineStylePreview(AppW app, int width, int height) {
		canvas = Canvas.createIfSupported();
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		initWidget(canvas);
		g2 = new GGraphics2DW(canvas);
		line = new GeoLine(app.getKernel().getConstruction(), 0, 1, 0);
		line.setLineType(1);
		drawLine = new DrawLine(app.getActiveEuclidianView(), line);
		marginY = height / 2 - 1;

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
		g2.clearRect(0, 0, getOffsetWidth(), getOffsetHeight());
		drawLine.drawStylePreview(g2, marginX, marginY, getOffsetWidth());
	}



}
