package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoTranslate;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * 3D translation
 */
public class AlgoTranslate3D extends AlgoTranslate {

	/**
	 * @param cons
	 *            construction
	 * @param in
	 *            input
	 * @param v
	 *            transform vector
	 */
	public AlgoTranslate3D(Construction cons, GeoElement in, GeoElement v) {
		super(cons, in, v);
	}

	@Override
	protected Coords getVectorCoords() {
		GeoVectorND vec = (GeoVectorND) v;
		return vec.getCoordsInD3();
	}

	@Override
	protected GeoElement copy(GeoElement geo) {
		if (v.isGeoElement3D()) {
			return kernel.copy3D(geo);
		}
		return super.copy(geo);
	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if ((geo instanceof GeoFunction || geo instanceof GeoCurveCartesian)
				&& v.isGeoElement3D()) {
			return new GeoCurveCartesian3D(cons);
		}

		return super.getResultTemplate(geo);
	}

	@Override
	protected GeoElement copyInternal(Construction cons1, GeoElement geo) {
		if (v.isGeoElement3D()) {
			return kernel.copyInternal3D(cons1, geo);
		}
		return super.copyInternal(cons1, geo);
	}

	@Override
	protected void setOutGeo() {
		if (inGeo instanceof GeoFunction && v.isGeoElement3D()) {
			AlgoTransformation3D.toGeoCurveCartesian(kernel,
					(GeoFunction) inGeo, (GeoCurveCartesian3D) outGeo);
		} else {
			super.setOutGeo();
		}
	}

}
