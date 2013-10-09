package geogebra.touch.gui.elements.stylebar;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.PointProperties;

import java.util.ArrayList;
import java.util.List;

public class StyleBarStatic {

	public static boolean applyAlpha(List<GeoElement> fillable, float alpha) {
		boolean needUndo = false;
		for (int i = 0; i < fillable.size(); i++) {
			final GeoElement geo = fillable.get(i);
			// apply object color to all other geos except images or text
			if (!(geo.getGeoElementForPropertiesDialog() instanceof GeoImage || geo
					.getGeoElementForPropertiesDialog() instanceof GeoText)) {
				if (geo.getAlphaValue() != alpha) {
					// if we change alpha for functions, hit won't work properly
					if (geo.isFillable()) {
						geo.setAlphaValue(alpha);
					}
					geo.updateVisualStyle();
					needUndo = true;
				}
			}
		}

		return needUndo;
	}

	public static boolean applyColor(ArrayList<GeoElement> geos, GColor color) {
		boolean needUndo = false;
		for (int i = 0; i < geos.size(); i++) {
			final GeoElement geo = geos.get(i);
			// apply object color to all other geos except images or text
			if (!(geo.getGeoElementForPropertiesDialog() instanceof GeoImage || geo
					.getGeoElementForPropertiesDialog() instanceof GeoText)) {
				if (geo.getObjectColor() != color) {
					geo.setObjColor(color);
					geo.updateVisualStyle();
					needUndo = true;
				}
			}
		}

		return needUndo;
	}

	/**
	 * only repaint once (not once per geo); does not change LineStyle
	 * 
	 * @see EuclidianStyleBarStatic#applyLineStyle
	 * 
	 * @param geos
	 * @param lineSize
	 * @return
	 */
	public static boolean applyLineSize(ArrayList<GeoElement> geos, int lineSize) {
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++) {
			final GeoElement geo = geos.get(i);
			if (geo.getLineThickness() != lineSize) {
				geo.setLineThickness(lineSize);
				geo.updateCascade();
				needUndo = true;
			}
			if (i == geos.size() - 1 && needUndo) {
				geo.updateRepaint();
			}
		}

		return needUndo;
	}

	/**
	 * only repaint once (not once per geo); does not change LineSize
	 * 
	 * @see EuclidianStyleBarStatic#applyLineStyle
	 * 
	 * @param geos
	 * @param lineStyleIndex
	 * @return
	 */
	public static boolean applyLineStyle(ArrayList<GeoElement> geos,
			int lineStyleIndex) {
		final int lineStyle = EuclidianStyleBarStatic.lineStyleArray[lineStyleIndex]
				.intValue();
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++) {
			final GeoElement geo = geos.get(i);
			if (geo.getLineType() != lineStyle) {
				geo.setLineType(lineStyle);
				geo.updateCascade();
				needUndo = true;
			}
			if (i == geos.size() - 1 && needUndo) {
				geo.updateRepaint();
			}
		}

		return needUndo;
	}

	public static boolean applyPointSize(ArrayList<GeoElement> geos,
			int pointSize) {
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++) {
			final GeoElement geo = geos.get(i);
			if(!(geo instanceof PointProperties)){
				continue;
			}
			PointProperties pt = (PointProperties)geo;
			if (pt.getPointSize() != pointSize) {
				pt.setPointSize(pointSize);
				geo.updateCascade();
				needUndo = true;
			}
			if (i == geos.size() - 1 && needUndo) {
				geo.updateRepaint();
			}
		}

		return needUndo;
	}
	
	public static boolean applyPointStyle(ArrayList<GeoElement> geos,
			int pointStyle) {
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++) {
			final GeoElement geo = geos.get(i);
			if(!(geo instanceof PointProperties)){
				continue;
			}
			PointProperties pt = (PointProperties)geo;
			if (pt.getPointStyle() != pointStyle) {
				pt.setPointStyle(pointStyle);
				geo.updateCascade();
				needUndo = true;
			}
			if (i == geos.size() - 1 && needUndo) {
				geo.updateRepaint();
			}
		}

		return needUndo;
	}
}
