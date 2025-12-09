/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoMirror;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.debug.Log;

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
			if (out instanceof MirrorableAtPlane) {
				((MirrorableAtPlane) out).mirror(mirrorPlane);
			} else {
				out.setUndefined();
			}
		} else {
			super.computeRegardingMirror();
		}

	}

	@Override
	protected GeoElement copy(GeoElement geo) {
		if (mirror.isGeoElement3D()) {
			Log.debug("COPY " + geo.getGeoClassType());
			return kernel.copy3D(geo);
		}
		return super.copy(geo);
	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if ((geo instanceof GeoFunction || geo instanceof GeoCurveCartesian)
				&& mirror.isGeoElement3D()) {
			return new GeoCurveCartesian3D(cons);
		}
		if (geo instanceof GeoFunctionNVar && mirror != mirrorPoint) {
			return new GeoSurfaceCartesian3D(cons);
		}
		return super.getResultTemplate(geo);
	}

	@Override
	protected GeoElement copyInternal(Construction cons1, GeoElement geo) {
		if (mirror.isGeoElement3D()) {
			return kernel.copyInternal3D(cons1, geo);
		}
		return super.copyInternal(cons1, geo);
	}

	@Override
	protected void setOutGeo() {
		if (inGeo instanceof GeoFunction && mirror.isGeoElement3D()) {
			AlgoTransformation3D.toGeoCurveCartesian(kernel,
					(GeoFunction) inGeo, (GeoCurveCartesian3D) outGeo);
		}
		else if (inGeo instanceof GeoFunctionNVar && mirror != mirrorPoint) {
			AlgoTransformation3D.toGeoSurfaceCartesian(kernel,
					(GeoFunctionNVar) inGeo, (GeoSurfaceCartesian3D) outGeo);
		} else {
			super.setOutGeo();
		}
	}

	@Override
	protected Coords getMirrorCoords() {
		return mirrorPoint.getInhomCoordsInD3();
	}

}
