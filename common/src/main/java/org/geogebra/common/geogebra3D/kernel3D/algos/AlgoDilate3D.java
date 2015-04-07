package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoDilate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for dilate at 3D point
 * 
 * @author mathieu
 *
 */
public class AlgoDilate3D extends AlgoDilate {

	/**
	 * dilate at point
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            point dilated
	 * @param r
	 *            factor
	 * @param S
	 *            reference point
	 */
	public AlgoDilate3D(Construction cons, GeoElement A, NumberValue r,
			GeoPointND S) {
		super(cons, A, r, S);
	}

	@Override
	protected GeoElement copy(GeoElement geo) {
		return kernel.copy3D(geo);
	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if ((geo instanceof GeoFunction || geo instanceof GeoCurveCartesian) /*
																			 * &&
																			 * mirror
																			 * .
																			 * isGeoElement3D
																			 * (
																			 * )
																			 */)
			return new GeoCurveCartesian3D(cons);

		return super.getResultTemplate(geo);
	}

	@Override
	protected GeoElement copyInternal(Construction cons1, GeoElement geo) {
		return kernel.copyInternal3D(cons1, geo);
	}

	@Override
	protected void setOutGeo() {
		if (inGeo instanceof GeoFunction /* && mirror.isGeoElement3D() */) {
			AlgoTransformation3D.toGeoCurveCartesian(kernel,
					(GeoFunction) inGeo, (GeoCurveCartesian3D) outGeo);
		} else {
			super.setOutGeo();
		}
	}

	@Override
	protected Coords getPointCoords() {
		return S.getInhomCoordsInD3();
	}

}
