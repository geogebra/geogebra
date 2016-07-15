package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GTemplate;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 3D extension of implicit curves
 *
 */
public class GeoImplicitCurve3D extends GeoImplicitCurve {

	private CoordSys coordSys;

	/**
	 * @param c
	 *            construction
	 */
	public GeoImplicitCurve3D(Construction c) {
		super(c);
		this.coordSys = new CoordSys(2);
	}

	@Override
	public CoordSys getCoordSys() {
		return coordSys;
	}

	@Override
	public void set(GeoElementND geo) {
		super.set(geo);
		coordSys.set(((GeoImplicit) geo).getCoordSys());
	}

	@Override
	public GeoImplicitCurve3D copy() {
		GeoImplicitCurve3D curve = new GeoImplicitCurve3D(cons);
		curve.set(this);
		return curve;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		StringBuilder valueSb = new StringBuilder(50);
		valueSb.append("(");
		String eqn = super.toValueString(tpl);
		valueSb.append(eqn);
		valueSb.append(",");
		valueSb.append(new GTemplate(tpl, kernel).buildImplicitEquation(
				coordSys,
				GeoPlane3D.VAR_STRING, false, true));
		valueSb.append(")");
		return valueSb.toString();

	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}


	@Override
	protected void setPointOnCoordsys(GeoPointND PI) {
		Coords coords = PI.getInhomCoordsInD3();
		Coords vec = coordSys.getEquationVector();
		if (!Kernel.isZero(vec.getZ())) {
			coords.setZ(-vec.getX() * coords.getX() / vec.getZ() - vec.getY()
					* coords.getY() / vec.getZ() - vec.getW() / vec.getZ());
		} else {
			if (!Kernel.isZero(vec.getY())) {
				double oldY = coords.getY();
				coords.setY(-vec.getX() * coords.getX() / vec.getY()

				- vec.getW() / vec.getY());
				coords.setZ(oldY);
			} else {
				double oldX = coords.getX();
				coords.setX(-vec.getW() / vec.getX());
				coords.setZ(coords.getY());
				coords.setY(oldX);

			}
		}
		PI.setCoords(coords, false);
		PI.updateCoords();
	}

}
