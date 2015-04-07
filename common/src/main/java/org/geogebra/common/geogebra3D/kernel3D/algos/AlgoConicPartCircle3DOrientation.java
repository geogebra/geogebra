package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Circular arc or sector defined by the circle's center, one point on the
 * circle (start point) and another point (angle for end-point), and
 * orientation.
 */
public class AlgoConicPartCircle3DOrientation extends AlgoConicPartCircle3D {

	private GeoDirectionND orientation;

	/**
	 * constructor
	 * 
	 * @param cons
	 * @param label
	 * @param center
	 * @param startPoint
	 * @param endPoint
	 * @param orientation
	 * @param type
	 */
	public AlgoConicPartCircle3DOrientation(Construction cons, String label,
			GeoPointND center, GeoPointND startPoint, GeoPointND endPoint,
			GeoDirectionND orientation, int type) {
		super(cons, label, center, startPoint, endPoint, orientation, type);
	}

	@Override
	protected void setOrientation(GeoDirectionND orientation) {
		this.orientation = orientation;
	}

	@Override
	protected boolean getPositiveOrientation() {
		return conic.getMainDirection().dotproduct(
				orientation.getDirectionInD3()) >= 0;
	}

	@Override
	protected void setInput() {
		setInput(4);
		input[3] = (GeoElement) orientation;
	}

	@Override
	protected void semiCircle(Coords center, Coords v1) {
		AlgoCircle3DAxisPoint.setCircle(conic, conic.getCoordSys(), center, v1,
				orientation.getDirectionInD3());
		setConicPart(0, Math.PI);
	}
}
