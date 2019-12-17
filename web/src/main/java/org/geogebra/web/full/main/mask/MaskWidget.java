package org.geogebra.web.full.main.mask;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * DOM widget representing mask, used to mask embedded elements
 */
class MaskWidget extends FlowPanel {
	private final Style style;
	private EuclidianView view;
	private GeoPolygon polygon;

	/**
	 *
	 * @param polygon represents the mask.
	 * @param view {@link EuclidianView}
	 */
	MaskWidget(GeoPolygon polygon, EuclidianView view) {
		this.polygon = polygon;
		this.view = view;
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
		GeoSegmentND[] segments = polygon.getSegments();
		GPoint2D pA = toScreenPoint(segments[0].getStartPoint().getCoords());
		GPoint2D pB = toScreenPoint(segments[3].getStartPoint().getCoords());
		GPoint2D pC = toScreenPoint(segments[2].getStartPoint().getCoords());
		double ratio = ((AppW) view.getApplication()).getPixelRatio();
		double m11 = (pA.getX() - pB.getX()) * ratio;
		double m12 = (pC.getX() - pB.getX()) * ratio;
		double m13 = pB.getX();

		double m21 = (pA.getY() - pB.getY()) * ratio;
		double m22 = (pC.getY() - pB.getY()) * ratio;
		double m23 = pB.getY();
		String sb = "matrix(" + m11 + ", " + m21
				+ ", " + m12 + ", " + m22
				+ ", " + m13 + ", " +	m23 + ")";
		Browser.setTransform(style, sb);
	}

	private GPoint2D toScreenPoint(Coords source) {
		return AwtFactory.getPrototype().newPoint2D(view.toScreenCoordXd(source.getX()),
				view.toScreenCoordYd(source.getY()));
	}
}
