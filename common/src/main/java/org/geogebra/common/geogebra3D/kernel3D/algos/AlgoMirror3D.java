package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoMirror;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for mirror at 3D point / 3D line
 * 
 * @author mathieu
 *
 */
public class AlgoMirror3D extends AlgoMirror {

	private GeoCoordSys2D mirrorPlane;

	/**
	 * mirror at point
	 * 
	 * @param cons
	 *            construction
	 * @param in
	 *            input
	 * @param point
	 *            mirror point
	 */
	public AlgoMirror3D(Construction cons, GeoElement in, GeoPointND point) {
		super(cons, in, point);
	}

	/**
	 * mirror at line
	 * 
	 * @param cons
	 *            construction
	 * @param in
	 *            input
	 * @param line
	 *            mirror line
	 */
	public AlgoMirror3D(Construction cons, GeoElement in, GeoLineND line) {
		super(cons, in, line);
	}

	/**
	 * mirror at line
	 * 
	 * @param cons
	 *            construction
	 * @param in
	 *            input
	 * @param plane
	 *            mirror plane
	 */
	public AlgoMirror3D(Construction cons, GeoElement in, GeoCoordSys2D plane) {
		super(cons);
		mirrorPlane = plane;
		endOfConstruction(cons, in, (GeoElement) plane);
	}

	@Override
	protected void computeRegardingMirror() {
		if (mirror == mirrorPlane) {
			((MirrorableAtPlane) out).mirror(mirrorPlane);
		} else {
			super.computeRegardingMirror();
		}

	}

	@Override
	protected GeoElement copy(GeoElement geo) {
		if (mirror.isGeoElement3D())
			return kernel.copy3D(geo);
		return super.copy(geo);
	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if ((geo instanceof GeoFunction || geo instanceof GeoCurveCartesian)
				&& mirror.isGeoElement3D())
			return new GeoCurveCartesian3D(cons);

		return super.getResultTemplate(geo);
	}

	@Override
	protected GeoElement copyInternal(Construction cons, GeoElement geo) {
		if (mirror.isGeoElement3D())
			return kernel.copyInternal3D(cons, geo);
		return super.copyInternal(cons, geo);
	}

	@Override
	protected void setOutGeo() {
		if (inGeo instanceof GeoFunction && mirror.isGeoElement3D()) {
			AlgoTransformation3D.toGeoCurveCartesian(kernel,
					(GeoFunction) inGeo, (GeoCurveCartesian3D) outGeo);
		} else {
			super.setOutGeo();
		}
	}

	@Override
	protected Coords getMirrorCoords() {
		return mirrorPoint.getInhomCoordsInD3();
	}

}
