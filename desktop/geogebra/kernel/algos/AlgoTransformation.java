package geogebra.kernel.algos;

import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.EuclidianViewCE;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoConicPartInterface;
import geogebra.kernel.geos.GeoPolyLineInterface;
import geogebra.kernel.geos.GeoRay;

/**
 * Algorithms for transformations
 */
public abstract class AlgoTransformation extends AlgoElement implements
		EuclidianViewCE {

	/**
	 * Create new transformation algo
	 * 
	 * @param c
	 */
	public AlgoTransformation(AbstractConstruction c) {
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
		if (geo instanceof GeoPolyLineInterface || geo.isLimitedPath())
			return copyInternal(cons, geo);
		if (geo.isGeoList())
			return new GeoList(cons);
		return copy(geo);
	}

	protected GeoElement copy(GeoElement geo) {
		return geo.copy();
	}

	protected GeoElement copyInternal(AbstractConstruction cons, GeoElement geo) {
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
		if (a instanceof GeoConicPartInterface) {
			double p = ((GeoConicPartInterface) a).getParameterStart();
			double q = ((GeoConicPartInterface) a).getParameterEnd();
			// Application.debug(p+","+q);
			((GeoConicPartInterface) b).setParameters(p, q,
					swapOrientation(((GeoConicPartInterface) a).positiveOrientation()));
		}
	}

	public boolean swapOrientation(boolean positiveOrientation) {
		// Application.debug(positiveOrientation);
		return positiveOrientation;
	}

	private AlgoClosestPoint pt;
	private GeoPoint2 transformedPoint;

	protected void transformLimitedConic(GeoElement a, GeoElement b) {

		GeoConicPartInterface arc = (GeoConicPartInterface) b;
		if (a instanceof GeoConicPartInterface) {
			((GeoConicPartInterface) b).setParameters(0, AbstractKernel.PI_2, true);
			if (pt == null) {
				transformedPoint = new GeoPoint2(cons);
				pt = new AlgoClosestPoint( cons, arc, transformedPoint);
				cons.removeFromConstructionList(pt);
			}
			transformedPoint.removePath();
			setTransformedObject(((GeoConicPartInterface) a).getPointParam(0),
					transformedPoint);
			compute();
			transformedPoint.updateCascade();
			// Application.debug("start"+transformedPoint);
			double d = pt.getP().getPathParameter().getT();
			transformedPoint.removePath();
			setTransformedObject(((GeoConicPartInterface) a).getPointParam(1),
					transformedPoint);
			compute();
			transformedPoint.updateCascade();
			// Application.debug("end"+transformedPoint);
			double e = pt.getP().getPathParameter().getT();
			// Application.debug(d+","+e);
			arc.setParameters(d * AbstractKernel.PI_2, e * AbstractKernel.PI_2,
					swapOrientation(((GeoConicPartInterface) a).positiveOrientation()));

			setTransformedObject(a, b);
		}
	}
}
