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

package org.geogebra.web.full.main.mask;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * DOM widget representing mask, used to mask embedded elements
 */
class MaskWidget extends FlowPanel {
	private final Style style;
	private EuclidianView view;
	private GeoPolygon polygon;
	private GeoGebraFrameFull frame;

	/**
	 * @param polygon represents the mask.
	 * @param view parent view
	 * @param frame app frame
	 */
	MaskWidget(GeoPolygon polygon, EuclidianView view, GeoGebraFrameFull frame) {
		this.polygon = polygon;
		this.view = view;
		this.frame = frame;
		addStyleName("maskWidget");
		style = getElement().getStyle();
		update();
	}

	private void update() {
		updateColor();
		transformWithMatrix();
	}

	private void updateColor() {
		GColor background = polygon.getObjectColor();
		style.setBackgroundColor(background.toString());
	}

	private void transformWithMatrix() {
		GPoint2D pA = toScreenPoint(polygon.getPoint(1).getCoords());
		GPoint2D pB = toScreenPoint(polygon.getPoint(2).getCoords());
		GPoint2D pC = toScreenPoint(polygon.getPoint(3).getCoords());
		// .maskWidget size is 100x100px to avoid rounding problem in Chrome
		double ratio = .01;
		double m11 = (pA.getX() - pB.getX()) * ratio;
		double m12 = (pC.getX() - pB.getX()) * ratio;
		double m13 = pB.getX();

		double m21 = (pA.getY() - pB.getY()) * ratio;
		double m22 = (pC.getY() - pB.getY()) * ratio;
		double m23 = pB.getY() + frame.getNotesTopBarHeight();
		String sb = "matrix(" + m11 + ", " + m21
				+ ", " + m12 + ", " + m22
				+ ", " + m13 + ", " + m23 + ")";
		style.setProperty("transform", sb);
	}

	private GPoint2D toScreenPoint(Coords source) {
		return new GPoint2D(view.toScreenCoordXd(source.getX()),
				view.toScreenCoordYd(source.getY()));
	}
}
