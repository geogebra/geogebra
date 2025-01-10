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
		return new OutputHandler<>(new ElementFactory<GeoElement>() {
			@Override
			public GeoPoint3D newElement() {
				GeoPoint3D pt = new GeoPoint3D(cons);
				pt.setCoords(0, 0, 0, 1);
				pt.setParentAlgorithm(AlgoVertexPolygon3D.this);
				return pt;
			}
		});
	}

}
