package geogebra.common.geogebra3D.util;

import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.util.CopyPaste;

import java.util.ArrayList;

public class CopyPaste3D extends CopyPaste {

	public CopyPaste3D() {
		// dummy, for now
	}

	@Override
	protected void addSubGeos(ArrayList<ConstructionElement> geos) {
		// even in 3D, there may be a lot of 2D objects
		super.addSubGeos(geos);

		GeoElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = (GeoElement) geos.get(i);
			if(geo.getParentAlgorithm()==null)
				continue;

			if (geo.isGeoElement3D()) {
				// TODO: implementation!
			}
		}
	}
}
