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
	private GeoPoint transformedPoint;
	protected GeoElement inGeo;
	protected GeoElement outGeo;

	/**
	 * Create new transformation algo
	 * 
	 * @param c
	 *            construction
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

	/**
	 * @param source
	 *            source
	 * @param target
	 *            target
	 */
	abstract protected void setTransformedObject(GeoElement source,
			GeoElement target);

	/**
	 * @param ageo2
	 *            source list
	 * @param bgeo2
	 *            target list
	 */
	protected void transformList(GeoList ageo2, GeoList bgeo2) {
		for (int i = bgeo2.size() - 1; i >= ageo2.size(); i--) {
			bgeo2.remove(i);
		}

		for (int i = 0; i < ageo2.size(); i++) {
			GeoElement trans;
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

	/**
	 * @param geo
	 *            source element
	 * @return template element that can be used for result
	 */
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoPoly || geo.isLimitedPath()) {
			return copyInternal(cons, geo);
		}
		if (geo.isGeoList()) {
			return new GeoList(cons);
		}
		return copy(geo);
	}

	/**
	 * @param geo
	 *            template
	 * @return copy
	 */
	protected GeoElement copy(GeoElement geo) {
		return geo.copy();
	}

	/**
	 * @param cons1
	 *            construction
	 * @param geo
	 *            source geo
	 * @return copy
	 */
	protected GeoElement copyInternal(Construction cons1, GeoElement geo) {
		return geo.copyInternal(cons1);
	}

	/**
	 * @param a
	 *            source
	 * @param b
	 *            target
	 */
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
			boolean orientation = swapOrientation((GeoConicPartND) a);
			if (orientation) {
				((GeoConicPartND) b).setParameters(p, q, true);
			} else {
				((GeoConicPartND) b).setParameters(q, p, false);
			}
		}
	}

	/**
	 * @param arc
	 *            arc
	 * @return what orientation should the result have
	 */
	public boolean swapOrientation(GeoConicPartND arc) {
		// Application.debug(positiveOrientation);
		return arc == null || arc.positiveOrientation();
	}

	/**
	 * @param a
	 *            source
	 * @param b
	 *            target
	 */
	protected void transformLimitedConic(GeoElement a, GeoElement b) {

		GeoConicPart arc = (GeoConicPart) b;
		if (a instanceof GeoConicPart) {
			GeoConicPart source = (GeoConicPart) a;
			arc.setParameters(0, Kernel.PI_2, true);
			if (transformedPoint == null) {
				transformedPoint = new GeoPoint(cons);
			}
			transformedPoint.removePath();
			setTransformedObject(source.getPointParam(0), transformedPoint);
			compute();
			arc.pointChanged(transformedPoint);
			transformedPoint.updateCoords();
			// Application.debug("start"+transformedPoint);
			double d = transformedPoint.getPathParameter().getT();
			transformedPoint.removePath();
			setTransformedObject(source.getPointParam(1), transformedPoint);
			compute();
			arc.pointChanged(transformedPoint);
			transformedPoint.updateCoords();
			// Application.debug("end"+transformedPoint);
			double e = transformedPoint.getPathParameter().getT();
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

	@Override
	public boolean euclidianViewUpdate() {
		compute();
		return true;
	}

	/**
	 * set inGeo to outGeo
	 */
	protected void setOutGeo() {
		outGeo.set(inGeo);
		outGeo.resetDefinition();
	}
}
