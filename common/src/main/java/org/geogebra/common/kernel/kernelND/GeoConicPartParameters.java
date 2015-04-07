package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.integration.EllipticArcLength;

/**
 * Parameters for 2D/3D GeoConicPart
 * @author mathieu
 *
 */
public class GeoConicPartParameters {
	
	private GeoConicND conic;

	public double paramStart, paramEnd, paramExtent;
	public boolean posOrientation = true;
	public int conic_part_type;

	public double value, area, arcLength;
	public boolean value_defined;

	public EllipticArcLength ellipticArcLength;
	public boolean allowOutlyingIntersections = false;
	public boolean keepTypeOnGeometricTransform = true;
	
	public GeoConicPartParameters(GeoConicND conic, int type){
		this.conic = conic;
		conic_part_type = type;
	}
	
	public void set(GeoConicPartParameters cp){
		paramStart = cp.paramStart;
		paramEnd = cp.paramEnd;
		paramExtent = cp.paramExtent;
		posOrientation = cp.posOrientation;
		conic_part_type = cp.conic_part_type;

		value = cp.value;
		area = cp.area;
		value_defined = cp.value_defined;

		keepTypeOnGeometricTransform = cp.keepTypeOnGeometricTransform;
	}
	
	final public boolean isEqual(GeoConicPartParameters other) {
		return posOrientation == other.posOrientation
				&& conic_part_type == other.conic_part_type
				&& Kernel.isEqual(paramStart, other.paramStart)
				&& Kernel.isEqual(paramEnd, other.paramEnd);
	}
	
	
	final public void setParameters(boolean isDefined, double start, double end,
			boolean positiveOrientation) {
		
		double startParam =start;
		double endParam = end;
		value_defined = isDefined;
		if (!value_defined) {
			value = Double.NaN;
			return;
		}

		posOrientation = positiveOrientation;
		if (!posOrientation) {
			// internally we always use positive orientation, i.e. a <= b
			// the orientation flag is important for points on this path (see
			// pathChanged())
			double tmp = startParam;
			startParam = endParam;
			endParam = tmp;
		}

		// handle conic types
		switch (conic.getType()) {
		case GeoConicNDConstants.CONIC_CIRCLE:
			paramStart = Kernel.convertToAngleValue(startParam);
			paramEnd = Kernel.convertToAngleValue(endParam);
			paramExtent = paramEnd - paramStart;
			if (paramExtent < 0)
				paramExtent += Kernel.PI_2;

			double r = conic.getHalfAxis(0);
			arcLength = r * paramExtent;
			if (conic_part_type == GeoConicNDConstants.CONIC_PART_ARC) {
				value =  arcLength;
				// area arc = area sector - area triangle
				area = r * r * (paramExtent - Math.sin(paramExtent)) / 2.0;
			} else {
				value = r * r * paramExtent / 2.0; // area
				area = value; // area
			}
			value_defined = !Double.isNaN(value) && !Double.isInfinite(value);
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:
			paramStart = Kernel.convertToAngleValue(startParam);
			paramEnd = Kernel.convertToAngleValue(endParam);
			paramExtent = paramEnd - paramStart;
			if (paramExtent < 0)
				paramExtent += Kernel.PI_2;

			if (ellipticArcLength == null)
				ellipticArcLength = new EllipticArcLength(conic);

			if (conic_part_type == GeoConicNDConstants.CONIC_PART_ARC) {
				// length
				value = ellipticArcLength.compute(paramStart, paramEnd);
			} else {
				// area
				value = conic.getHalfAxis(0) * conic.getHalfAxis(1) * paramExtent / 2.0;
			}
			value_defined = !Double.isNaN(value) && !Double.isInfinite(value);

			break;

		// a circular arc through 3 points may degenerate
		// to a segment or two rays
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
			if (conic_part_type == GeoConicNDConstants.CONIC_PART_ARC && posOrientation) {
				// length of segment
				// bugfix Michael Borcherds 2008-05-27
				GeoPoint startPoint = conic.lines[0].getStartPoint();
				GeoPoint endPoint = conic.lines[0].getEndPoint();
				if (startPoint != null && endPoint != null) {
					value = startPoint.distance(endPoint);
				} else {
					value = Double.POSITIVE_INFINITY;
					value_defined = false;
					break;
				}
				// bugfix end

			} else { // sector or two rays
				value = Double.POSITIVE_INFINITY; // area or length of rays
			}
			value_defined = true;
			break;

		default:
			value_defined = false;
			// Application.debug("GeoConicPart: unsupported conic part for conic type: "
			// + type);
		}
	}
	
	/**
	 * Returns arc length / area as appropriate
	 * 
	 * @return arc length / area as appropriate
	 */
	final public double getValue() {
		if (!value_defined)
			return Double.NaN;
		return value;
	}
	
	/**
	 * Returns arc length 
	 * 
	 * @return arc length
	 */
	final public double getArcLength() {
		if (!value_defined)
			return Double.NaN;
		return arcLength;
	}

	/**
	 * Returns the area
	 * 
	 * @return area
	 */
	final public double getArea() {
		if (!value_defined)
			return Double.NaN;
		return area;
	}
	
	public void setEllipseParameter(Coords P, PathParameter pp) {
		// let GeoConic do the work
		((GeoConicPartND) conic).superPointChanged(P, pp);

		// now transform parameter t from [paramStart, paramEnd] to [0, 1]
		if (pp.t < 0)
			pp.t += Kernel.PI_2;
		double t = pp.t - paramStart;
		if (t < 0)
			t += Kernel.PI_2;
		pp.t = t / paramExtent;
	}

	public void clipEllipseParameter(Coords P, PathParameter pp) {
		// make sure we don't get outside [0,1]
		// the values of the path parameter are now
		// between [0, 2pi/paramExtent]
		// [0, 1] is ok.
		// handle [1, 2pi/paramExtent]:
		// take 0 for parameter > (1 + 2pi/paramExtent)/2
		// else take 1
		if (pp.t > 0.5 + Math.PI / paramExtent) {
			if (posOrientation)
				pp.t = 0;
			else
				pp.t = 1;
			conic.pathChanged(P, pp);
		} else if (pp.t > 1) {
			if (posOrientation)
				pp.t = 1;
			else
				pp.t = 0;
			conic.pathChanged(P, pp);
		} else if (!posOrientation) {
			pp.t = 1.0 - pp.t;
		}
	}
	

	public void getXMLtags(StringBuilder sb) {

		// allowOutlyingIntersections
		sb.append("\t<outlyingIntersections val=\"");
		sb.append(allowOutlyingIntersections);
		sb.append("\"/>\n");

		// keepTypeOnGeometricTransform
		sb.append("\t<keepTypeOnTransform val=\"");
		sb.append(keepTypeOnGeometricTransform);
		sb.append("\"/>\n");

	}
	
	public boolean isInRegion(double x0, double y0) {

		// for sector, check if (x0,y0) is on the arc outline
		if (conic_part_type == GeoConicNDConstants.CONIC_PART_SECTOR) {
			double arg = computeArg(x0, y0);
			if (arg < 0)
				arg += Kernel.PI_2;
			// Application.debug(arg+" <? "+paramExtent);

			return ((arg >= -Kernel.STANDARD_PRECISION) && (arg <= paramExtent
					+ Kernel.STANDARD_PRECISION));
		}

		// for arc, check if is inside the arc : cross product with limit
		// Application.debug(posOrientation);
		Coords midPoint = conic.getMidpoint2D();
		Coords firstVec = conic.getEigenvec(0).mul(
				conic.getHalfAxis(0) * Math.cos(paramStart)).add(
						conic.getEigenvec(1).mul(conic.getHalfAxis(1) * Math.sin(paramStart)));
		Coords secondVec = conic.getEigenvec(0).mul(
				conic.getHalfAxis(0) * Math.cos(paramEnd)).add(
						conic.getEigenvec(1).mul(conic.getHalfAxis(1) * Math.sin(paramEnd)));

		double vx = (x0 - midPoint.getX()) - firstVec.getX(), vy = (y0 - midPoint
				.getY()) - firstVec.getY();
		double lx = secondVec.getX() - firstVec.getX(), ly = secondVec.getY()
				- firstVec.getY();

		return (vx * ly - vy * lx > 0);
	}
	
	private double computeArg(double x0, double y0) {
		Coords b = conic.getMidpoint2D();
		double px = x0 - b.getX();
		double py = y0 - b.getY();

		// rotate by -alpha
		double px2 = px * conic.getEigenvec(0).getX() + py * conic.getEigenvec(0).getY();
		py = px * conic.getEigenvec(1).getX() + py * conic.getEigenvec(1).getY();

		// calc parameter

		// relation between the internal parameter t and the angle theta:
		// t = atan(a/b tan(theta)) where tan(theta) = py / px
		double arg = Math.atan2(conic.getHalfAxis(0) * py, conic.getHalfAxis(1) * px2);
		if (arg < 0)
			arg += Kernel.PI_2;
		return arg - paramStart;
	}
}
