package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;

/**
 * Algorithms for transformations
 */
public abstract class AlgoTransformation extends AlgoElement {

	/**
	 * Create new transformation algo
	 * 
	 * @param c
	 */
	public AlgoTransformation(Construction c) {
		super(c);
	}

	/**
	 * Returns the resulting GeoElement
	 * 
	 * @return the resulting GeoElement
	 */
	public abstract GeoElement getResult();

	abstract protected void setTransformedObject(GeoElement g, GeoElement g2);

	protected void transformList(GeoList ageo2, GeoList bgeo2) {
		for (int i = bgeo2.size() - 1; i >= ageo2.size(); i--)
			bgeo2.remove(i);

		for (int i = 0; i < ageo2.size(); i++) {
			GeoElement trans = null;
			if (i < bgeo2.size()) {
				setTransformedObject(ageo2.get(i), bgeo2.get(i));
				compute();
			} else {
				trans = getResultTemplate(ageo2.get(i));

				setTransformedObject(ageo2.get(i), trans);
				compute();
				bgeo2.add(trans);
			}
		}
		setTransformedObject(ageo2, bgeo2);
	}

	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoPoly || geo.isLimitedPath())
			return copyInternal(cons, geo);
		if (geo.isGeoList())
			return new GeoList(cons);
		return copy(geo);
	}

	protected GeoElement copy(GeoElement geo) {
		return geo.copy();
	}

	protected GeoElement copyInternal(Construction cons, GeoElement geo) {
		return geo.copyInternal(cons);
	}

	protected void transformLimitedPath(GeoElement a, GeoElement b) {

		if (a instanceof GeoRay) {
			setTransformedObject(((GeoRay) a).getStartPoint(),
					((GeoRay) b).getStartPoint());
			compute();
			setTransformedObject(a, b);
		} else if (a instanceof GeoSegment) {
			setTransformedObject(((GeoSegment) a).getStartPoint(),
					((GeoSegment) b).getStartPoint());
			compute();
			setTransformedObject(((GeoSegment) a).getEndPoint(),
					((GeoSegment) b).getEndPoint());
			compute();
			setTransformedObject(a, b);
		}
		if (a instanceof GeoConicPartND) {
			double p = ((GeoConicPartND) a).getParameterStart();
			double q = ((GeoConicPartND) a).getParameterEnd();
			// Application.debug(p+","+q);
			((GeoConicPartND) b).setParameters(p, q,
					swapOrientation((GeoConicPartND) a));
		}
	}

	public boolean swapOrientation(GeoConicPartND p) {
		// Application.debug(positiveOrientation);
		return p == null || p.positiveOrientation();
	}

	private AlgoClosestPoint pt;
	private GeoPoint transformedPoint;

	protected void transformLimitedConic(GeoElement a, GeoElement b) {

		GeoConicPart arc = (GeoConicPart) b;
		if (a instanceof GeoConicPart) {
			GeoConicPart source = (GeoConicPart) a;
			arc.setParameters(0, Kernel.PI_2, true);
			if (pt == null) {
				transformedPoint = new GeoPoint(cons);
				pt = new AlgoClosestPoint(cons, arc, transformedPoint);
				cons.removeFromConstructionList(pt);
			}
			transformedPoint.removePath();
			setTransformedObject(source.getPointParam(0), transformedPoint);
			compute();
			transformedPoint.updateCascade();
			// Application.debug("start"+transformedPoint);
			double d = pt.getP().getPathParameter().getT();
			transformedPoint.removePath();
			setTransformedObject(source.getPointParam(1), transformedPoint);
			compute();
			transformedPoint.updateCascade();
			// Application.debug("end"+transformedPoint);
			double e = pt.getP().getPathParameter().getT();
			// Application.debug(d+","+e);
			arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2,
					swapOrientation(source));

			setTransformedObject(a, b);
		}
	}

	/**
	 * 
	 * used when transforming polygons
	 * 
	 * @return area scale factor of the transformation (-1 for reflections)
	 */
	public abstract double getAreaScaleFactor();
}
