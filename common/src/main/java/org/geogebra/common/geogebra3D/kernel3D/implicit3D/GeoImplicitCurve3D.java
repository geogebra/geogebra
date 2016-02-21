package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public class GeoImplicitCurve3D extends GeoImplicitCurve {

	private CoordSys coordSys;

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

}
