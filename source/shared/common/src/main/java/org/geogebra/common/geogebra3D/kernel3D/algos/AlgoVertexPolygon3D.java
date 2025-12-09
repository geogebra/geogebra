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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoVertexPolygon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Vertices of a 3D polygon
 * 
 * @author mathieu
 *
 */
public class AlgoVertexPolygon3D extends AlgoVertexPolygon {
	/**
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param p
	 *            polygon
	 */
	public AlgoVertexPolygon3D(Construction cons, String[] labels, GeoPoly p) {
		super(cons, labels, p);
	}

	/**
	 * @param cons
	 *            construction
	 * @param p
	 *            polygon
	 */
	public AlgoVertexPolygon3D(Construction cons, GeoPoly p) {
		super(cons, p);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param p
	 *            polygon
	 * @param v
	 *            vertex index
	 */
	public AlgoVertexPolygon3D(Construction cons, String label, GeoPoly p,
			GeoNumberValue v) {
		super(cons, label, p, v);
	}

	@Override
	public GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint3D(cons1);
	}

	@Override
	protected void setPoint(GeoPointND point, int i) {
		((GeoPoint3D) point).setCoords(((GeoPolygon3D) p).getPoint3D(i));
	}

	@Override
	protected OutputHandler<GeoElement> createOutputPoints() {
		return new OutputHandler<>(() -> {
			GeoPoint3D pt = new GeoPoint3D(cons);
			pt.setCoords(0, 0, 0, 1);
			pt.setParentAlgorithm(this);
			return pt;
		});
	}

}
