package org.geogebra.common.kernel.construction;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Initializes the properties of the default GeoElement objects
 */
public class GeoElementPropertyInitializer {

	private Construction construction;
	private int angleSize = EuclidianStyleConstants.DEFAULT_ANGLE_SIZE;

	GeoElementPropertyInitializer(Construction construction) {
		this.construction = construction;
	}

	/**
	 * Sets the default line style.
	 * @param geo Sets the line style to this GeoElement
	 */
	public void setDefaultLineStyle(GeoElement geo) {
		if (geo instanceof GeoAngle
				&& construction.getApplication().isUnbundledGeometry()) {
			geo.setLineThickness(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_THICKNESS_ANGLE_GEOMETRY);
		} else {
			geo.setLineThickness(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_THICKNESS);
		}
		if (geo.hasLineOpacity()) {
			if (construction.getApplication().isUnbundledOrWhiteboard()) {
				setLineOpacity(geo);
			} else {
				geo.setLineOpacity(
						EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_OPACITY);
			}
		}
	}

	private void setLineOpacity(GeoElement geo) {
		if (geo instanceof GeoAngle) {
			geo.setLineOpacity(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_OPACITY_ANGLE);
		} else if (geo instanceof GeoPolygon) {
			geo.setLineOpacity(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_OPACITY_POLYGON);
		} else if (geo instanceof GeoConicPart) {
			geo.setLineOpacity(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_OPACITY_SECTOR);
		} else if (construction.getApplication().isUnbundledGeometry()
				&& (geo instanceof GeoLine || geo instanceof GeoSegment
				|| geo instanceof GeoRay || geo instanceof GeoVector
				|| geo instanceof GeoPolyLine
				|| geo instanceof GeoConic)) {
			geo.setLineOpacity(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_OPACITY_GEOMETRY);
		} else if (construction.getApplication().isUnbundledGraphing()
				&& (geo instanceof GeoFunction)) {
			geo.setLineOpacity(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_OPACITY_FUNCTION_GEOMETRY);
		} else if (construction.getApplication().isUnbundledGraphing()
				&& (geo instanceof GeoCurveCartesian)) {
			geo.setLineOpacity(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_OPACITY_CURVE_GEOMETRY);
		} else {
			geo.setLineOpacity(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_OPACITY);
		}
	}

	public int getAngleSize() {
		return angleSize;
	}

	public void setAngleSize(int angleSize) {
		this.angleSize = angleSize;
	}
}
