package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

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
	 *            construction
	 * @param label
	 *            output label
	 * @param center
	 *            center
	 * @param startPoint
	 *            arc start point
	 * @param endPoint
	 *            arc endpoint
	 * @param orientation
	 *            orientation
	 * @param type
	 *            sector or arc
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
	    Coords d = orientation.getDirectionInD3();
	    if (d == null) {
	        return true;
        }
		return conic.getMainDirection().dotproduct(d) >= 0;
	}

	@Override
	protected void setInput() {
		setInput(4);
		input[3] = (GeoElement) orientation;
	}

	@Override
	protected void semiCircle(Coords center, Coords v1) {
		Coords d = orientation.getDirectionInD3();
		if (d == null) {
			conicPart.setUndefined();
		} else {
			conicPart.setDefined();
			AlgoCircle3DAxisPoint.setCircle(conic, conic.getCoordSys(), center, v1, d);
			setConicPart(0, Math.PI);
		}
	}
}
