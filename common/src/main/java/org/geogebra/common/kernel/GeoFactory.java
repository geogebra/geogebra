package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoFunctionableToFunction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
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
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoInputBox;
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
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesian2D;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
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

		switch (type) {
		case  "angle":
			return new GeoAngle(cons1);
		case "audio":
			return new GeoAudio(cons1);
		case "axis":
			return new GeoAxis(cons1, 1);
		case "boolean":
			return new GeoBoolean(cons1);
		case "button":
			return new GeoButton(cons1);
		case "conic":
		case "circle":  // bug in GeoGebra 2.6c
		case "doubleLine":
		case "ellipse":
		case "emtpyset":
		case "hyperbola":
		case "intersectinglines":
		case "parabola":
		case "parallellines":
			return new GeoConic(cons1);
		case "conicpart":
			return new GeoConicPart(cons1, 0);
		case "curvecartesian":
			return new GeoCurveCartesian(cons1);
		case "cascell":
			return new GeoCasCell(cons1);
		case "embed":
			return new GeoEmbed(cons1);
		case "formula":
			return new GeoFormula(cons1, null);
		case "function":
		case "functionconditional":
		case "interval":
			return new GeoFunction(cons1);
		case "functionnvar":
			return new GeoFunctionNVar(cons1);
		case "image":
			return new GeoImage(cons1);
		case "implicitpoly":
			return newImplicitPoly(cons1).toGeoElement();
		case "inlinetext":
			return new GeoInlineText(cons1, null);
		case "line":
			GeoLine geoLine = new GeoLine(cons1);
			geoLine.showUndefinedInAlgebraView(true);
			return geoLine;
		case "list":
			GeoList geoList = new GeoList(cons1);
			geoList.setUndefined();
			return geoList;
		case "locus":
			return new GeoLocus(cons1);
		case "numeric":
			return new GeoNumeric(cons1);
		case "point":
			return new GeoPoint(cons1);
		case "polygon":
			return new GeoPolygon(cons1, null);
		case "polyline":
			return new GeoPolyLine(cons1, new GeoPointND[]{});
		case "ray":
			return new GeoRay(cons1, null);
		case "segment":
			return new GeoSegment(cons1, null, null);
		case "surfacecartesian":
			return new GeoSurfaceCartesian2D(cons1, null, null);
		case "text":
			return new GeoText(cons1);
		case "textfield":
			return new GeoInputBox(cons1);
		case "table":
			return new GeoInlineTable(cons1, null);
		case "video":
			return new GeoVideo(cons1);
		case "vector":
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

	/**
	 * @param cons construction
	 * @param point point expression
	 * @param fun x, y (and z) functions
	 * @return surface
	 */
	public GeoSurfaceCartesianND newSurface(Construction cons, ExpressionNode point,
			FunctionNVar[] fun) {
		return new GeoSurfaceCartesian2D(cons, point, fun);
	}
}
