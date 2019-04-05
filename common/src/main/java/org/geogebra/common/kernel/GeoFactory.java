package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoFunctionableToFunction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoInterval;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.util.debug.Log;

/**
 * Produces GeoElements, some methods overridden for 3D
 *
 */
public class GeoFactory {
	/**
	 * Creates a new GeoElement object for the given type string.
	 * 
	 * @param cons1
	 *            construction
	 * 
	 * @param type
	 *            String as produced by GeoElement.getXMLtypeString()
	 * @return created element
	 */
	public GeoElement createGeoElement(Construction cons1, String type) {
		// the type strings are the classnames in lowercase without the
		// beginning "geo"
		// due to a bug in GeoGebra 2.6c the type strings for conics
		// in XML may be "ellipse", "hyperbola", ...

		switch (type.charAt(0)) {
		case 'a': // angle
			if ("angle".equals(type)) {
				return new GeoAngle(cons1);
			} else if ("audio".equals(type)) {
				return new GeoAudio(cons1);
			}
			return new GeoAxis(cons1, 1);

		case 'b': // angle
			if ("boolean".equals(type)) {
				return new GeoBoolean(cons1);
			}
			return new GeoButton(cons1); // "button"

		case 'c': // conic
			if ("conic".equals(type)) {
				return new GeoConic(cons1);
			} else if ("conicpart".equals(type)) {
				return new GeoConicPart(cons1, 0);
			} else if ("curvecartesian".equals(type)) {
				return new GeoCurveCartesian(cons1);
			} else if ("cascell".equals(type)) {
				return new GeoCasCell(cons1);
			} else if ("circle".equals(type)) { // bug in GeoGebra 2.6c
				return new GeoConic(cons1);
			}

		case 'd': // doubleLine // bug in GeoGebra 2.6c
			return new GeoConic(cons1);

		case 'e': // ellipse, emptyset // bug in GeoGebra 2.6c
			if ("embed".equals(type)) {
				return new GeoEmbed(cons1);
			}
			return new GeoConic(cons1);

		case 'f': // function
			if ("function".equals(type)) {
				return new GeoFunction(cons1);
			} else if ("functionconditional".equals(type)) { // had special
																// class fror v
																// <5.0
				return new GeoFunction(cons1);
			} else {
				return new GeoFunctionNVar(cons1);
			}

		case 'h': // hyperbola // bug in GeoGebra 2.6c
			return new GeoConic(cons1);

		case 'i': // image,implicitpoly
			if ("image".equals(type)) {
				return new GeoImage(cons1);
			} else if ("intersectinglines".equals(type)) {
				return new GeoConic(cons1);
			} else if ("implicitpoly".equals(type)) {
				return newImplicitPoly(cons1).toGeoElement();
			} else if ("interval".equals(type)) {
				return new GeoInterval(cons1);
			}

		case 'l': // line, list, locus
			if ("line".equals(type)) {
				return new GeoLine(cons1);
			} else if ("list".equals(type)) {
				return new GeoList(cons1);
			} else {
				return new GeoLocus(cons1);
			}

		case 'n': // numeric
			return new GeoNumeric(cons1);

		case 'p': // point, polygon
			if ("point".equals(type)) {
				return new GeoPoint(cons1);
			} else if ("polygon".equals(type)) {
				return new GeoPolygon(cons1, null);
			} else if ("polyline".equals(type)) {
				return new GeoPolyLine(cons1, new GeoPointND[] {});
			} else {
				// parabola, parallelLines, point // bug in GeoGebra 2.6c
				return new GeoConic(cons1);
			}

		case 'r': // ray
			return new GeoRay(cons1, null);

		case 's': // segment
			return new GeoSegment(cons1, null, null);

		case 't':
			if ("text".equals(type)) {
				return new GeoText(cons1); // text
			}
			return new GeoInputBox(cons1); // textfield

		case 'v':
			if ("video".equals(type)) {
				return new GeoVideo(cons1);
			}
			// vector
			return new GeoVector(cons1);
		default:
			Log.error("GeoFactory: element of type " + type
					+ " could not be created.");
			return new GeoNumeric(cons1);
		}
	}

	/**
	 * @param cons2
	 *            construction
	 * @return implicit curve
	 */
	public GeoImplicit newImplicitPoly(Construction cons2) {
		return new GeoImplicitCurve(cons2);
	}

	/**
	 * 
	 * @param geo
	 *            source geo
	 * @return 3D copy of the geo (if exists)
	 */
	public GeoElement copy3D(GeoElement geo) {
		return geo.copy();
	}

	/**
	 * @param dimension
	 *            preferred dimension of point
	 * @param cons
	 *            construction
	 * @return point
	 */
	public GeoPointND newPoint(int dimension, Construction cons) {
		return new GeoPoint(cons);
	}

	/**
	 * @param dimension
	 *            preferred dimension of point
	 * @param cons
	 *            construction
	 * @return vector
	 */
	public GeoVectorND newVector(int dimension, Construction cons) {
		return new GeoVector(cons);
	}

	/**
	 * 
	 * @param cons1
	 *            target cons
	 * @param geo
	 *            source geo
	 * @return 3D copy internal of the geo (if exists)
	 */
	public GeoElement copyInternal3D(Construction cons1, GeoElement geo) {
		return geo.copyInternal(cons1);
	}

	/**
	 * @param dim
	 *            dimension
	 * @param cons
	 *            construction
	 * @return conic
	 */
	public GeoConicND newConic(int dim, Construction cons) {
		return new GeoConic(cons);
	}

	/**
	 * @param dimension
	 *            curve dimension
	 * @param cons
	 *            construction
	 * @return cartesian curve
	 */
	public GeoCurveCartesianND newCurve(int dimension, Construction cons) {
		return new GeoCurveCartesian(cons);
	}

	/**
	 * @param geoLine
	 *            line
	 * @return function
	 */
	public GeoFunction newFunction(GeoFunctionable geoLine) {
		Construction cons = geoLine.getConstruction();
		// we get a dependent function if this line has a label or is dependent
		if (geoLine.isLabelSet() || !geoLine.isIndependent()) {
			return new AlgoFunctionableToFunction(cons, geoLine).getFunction();
		}
		GeoFunction ret = new GeoFunction(cons);
		ret.setFunction(geoLine.getFunction());
		return ret;
	}
}
