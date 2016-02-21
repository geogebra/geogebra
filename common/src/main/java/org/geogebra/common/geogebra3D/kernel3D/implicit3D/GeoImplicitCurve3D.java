package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

public class GeoImplicitCurve3D extends GeoImplicitCurve {

	private CoordSys coordSys;

	public GeoImplicitCurve3D(Construction c) {
		super(c);
		this.coordSys = new CoordSys(2);
	}

	public CoordSys getCoordSys() {
		return coordSys;
	}

}
