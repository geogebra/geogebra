package org.geogebra.web.html5.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.awt.GGraphics2DW;

import com.google.gwt.canvas.dom.client.ImageData;

public class DropDownListW implements DropDownList {
	private static final int BOX_ROUND = 8;
	public void drawSelected(GeoElement geo, GGraphics2D g2, GColor bgColor,
			int left, int top, int width, int height) {
		g2.setPaint(bgColor);
		g2.fillRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);

		// TF Rectangle
		g2.setPaint(GColor.LIGHT_GRAY);
		g2.drawRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);

	}

	public void drawControl(GGraphics2D g2, int left, int top, int width,
			int height, GColor bgColor, boolean pressed) {

		GGraphics2DW g2w = (GGraphics2DW) g2;
		ImageData triangle = BasicIcons.createDropDownIcon(pressed, width,
				height, bgColor);
		g2w.getCanvas().getContext2d().putImageData(triangle, left, top);
	}

}
