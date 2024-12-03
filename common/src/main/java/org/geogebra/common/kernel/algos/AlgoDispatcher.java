package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.KernelCAS;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.TransformDilate;
import org.geogebra.common.kernel.TransformMirror;
import org.geogebra.common.kernel.TransformRotate;
import org.geogebra.common.kernel.TransformTranslate;
import org.geogebra.common.kernel.advanced.AlgoAxis;
import org.geogebra.common.kernel.advanced.AlgoCentroidPolygon;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.geos.Lineable2D;
import org.geogebra.common.kernel.implicit.AlgoImplicitPolyFunction;
import org.geogebra.common.kernel.implicit.AlgoIntersectImplicitpolyParametric;
import org.geogebra.common.kernel.implicit.AlgoIntersectImplicitpolyPolyLine;
import org.geogebra.common.kernel.implicit.AlgoIntersectImplicitpolys;
import org.geogebra.common.kernel.implicit.AlgoTangentImplicitpoly;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

/**
 * Factory for Algo classes
 */
public class AlgoDispatcher {
	/** pixel offset for point copy */
	protected final static int DETACH_OFFSET = 20;
	/**
	 * Construction
	 */
	protected Construction cons;

	/**
	 * Controls whether the cache is enabled for intersection algos.
	 * Default is true.
	 */
	protected boolean isIntersectCacheEnabled = true;

	/**
	 * to avoid multiple calculations of the intersection points of the same two
	 * objects, we remember all the intersection algorithms created
	 */
	protected ArrayList<AlgoIntersectAbstract> intersectionAlgos = new ArrayList<>();

	/**
	 * @param cons
	 *            construction
	 */
	public AlgoDispatcher(Construction cons) {
		this.cons = cons;
	}

	/**
	 * Sets whether this object reuses existing intersection algos,
	 * to avoid recomputations. Set this to false, to disable this behaviour.
	 * Default is enabled.
	 *
	 * @param enabled to enable the cache.
	 */
	public void setIntersectCacheEnabled(boolean enabled) {
		isIntersectCacheEnabled = enabled;
	}

	/**
	 * Tells whether the intersection cache is enabled.
	 *
	 * @return true if the cache is enabled
	 */
	public boolean isIntersectCacheEnabled() {
		return isIntersectCacheEnabled;
	}

	/**
	 * @param algo
	 *            intersect algo to remove from cache
	 */
	public void removeIntersectionAlgorithm(AlgoIntersectAbstract algo) {
		intersectionAlgos.remove(algo);
	}

	/**
	 * Add intersection to cache.
	 * 
	 * @param algo
	 *            intersection algo
	 */
	public void addIntersectionAlgorithm(AlgoIntersectAbstract algo) {
		if (!isIntersectCacheEnabled || cons.getKernel().isSilentMode()) {
			return;
		}
		intersectionAlgos.add(algo);
	}

	/**
	 * Point label with cartesian coordinates (x,y)
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @param complex
	 *            whether ot use complex coords
	 * @return point
	 */
	final public GeoPoint point(double x, double y,
			boolean complex) {
		int mode = complex ? Kernel.COORD_COMPLEX : Kernel.COORD_CARTESIAN;
		GeoPoint p = new GeoPoint(cons, mode);
		p.setCoords(x, y, 1.0);
		return p;
	}

	/**
	 * @param label
	 *            label
	 * @return labeled vector (0,0)
	 */
	public GeoVectorND vector(String label) {
		GeoVectorND ret = vector(0, 0);
		ret.setLabel(label);
		return ret;
	}

	/**
	 * @return vector (0,0)
	 */
	public GeoVectorND vector() {
		return vector(0, 0);
	}

	/**
	 * Vector label with cartesian coordinates (x,y)
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @return vector
	 */
	final public GeoVector vector(double x, double y) {
		GeoVector v = new GeoVector(cons);
		v.setCoords(x, y, 0.0);
		v.setMode(Kernel.COORD_CARTESIAN);
		return v;
	}

	/**
	 * Point on path with cartesian coordinates (x,y)
	 * 
	 * @param label
	 *            label
	 * @param path
	 *            parent path
	 * @param x
	 *            closest x-coord
	 * @param y
	 *            closest y-coord
	 * @param addToConstruction
	 *            whether to add point to construction
	 * @param complex
	 *            whether to use complex coords
	 * @param coords2D
	 *            whether to prefer 2D coords
	 * 
	 * @return point
	 */
	final public GeoPoint point(String label, Path path, double x, double y,
			boolean addToConstruction, boolean complex, boolean coords2D) {

		AlgoPointOnPath algo;

		algo = new AlgoPointOnPath(cons, path, x, y, 0, addToConstruction);

		GeoPoint p = (GeoPoint) algo.getP();
		if (complex) {
			p.setMode(Kernel.COORD_COMPLEX);
		} else if (!coords2D) {
			p.setCartesian3D();
		}

		if (addToConstruction) {
			p.setLabel(label);
		}
		return p;
	}

	/**
	 * @param label
	 *            point label
	 * @param path
	 *            parent path
	 * @param coords
	 *            coordinates
	 * @param addToConstruction
	 *            whether to add to construction
	 * @param complex
	 *            whether to use complex coords
	 * @param coords2D
	 *            whether to use 2D coord style
	 * @return point
	 */
	public GeoPointND point(String label, Path path, Coords coords,
			boolean addToConstruction, boolean complex, boolean coords2D) {
		return point(label, path, coords.getX(), coords.getY(),
				addToConstruction, complex, coords2D);
	}

	/**
	 * Point anywhere on path with
	 * 
	 * @param label
	 *            label
	 * @param path
	 *            path
	 * @param param
	 *            path parameter
	 * @return point
	 */
	final public GeoPoint point(String label, Path path, GeoNumberValue param) {
		// try (0,0)
		AlgoPointOnPath algo = null;
		if (param == null) {
			algo = new AlgoPointOnPath(cons, path, 0, 0);
		} else {
			algo = new AlgoPointOnPath(cons, path, param);
		}
		GeoPoint p = (GeoPoint) algo.getP();

		// try (1,0)
		if (!p.isDefined()) {
			p.setCoords(1, 0, 1);
		}

		// try (random(),0)
		if (!p.isDefined()) {
			p.setCoords(Math.random(), 0, 1);
		}
		p.setLabel(label);
		return p;
	}

	/********************
	 * ALGORITHMIC PART *
	 ********************/

	/**
	 * Line named label through Points P and Q
	 * 
	 * @param label
	 *            line label
	 * @param P
	 *            start point
	 * @param Q
	 *            end point
	 * @return line
	 */
	final public GeoLine line(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPoints algo = new AlgoJoinPoints(cons, label, P, Q);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Ray named label through Points P and Q
	 * 
	 * @param label
	 *            output label
	 * 
	 * @param P
	 *            start point
	 * @param Q
	 *            point on ray
	 * @return ray
	 */
	final public GeoRay ray(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPointsRay algo = new AlgoJoinPointsRay(cons, label, P, Q);
		return algo.getRay();
	}

	/**
	 * Ray named label through Point P with direction of vector v
	 * 
	 * @param label
	 *            output label
	 * @param P
	 *            start point
	 * @param v
	 *            direction
	 * @return ray
	 */
	final public GeoRay ray(String label, GeoPoint P, GeoVector v) {
		AlgoRayPointVector algo = new AlgoRayPointVector(cons, P, v);
		algo.getRay().setLabel(label);
		return algo.getRay();
	}

	/**
	 * Line named label through Point P parallel to Line l
	 * 
	 * @param label
	 *            label
	 * @param P
	 *            point
	 * @param l
	 *            line
	 * 
	 * @return parallel line
	 */
	final public GeoLine line(String label, GeoPoint P, Lineable2D l) {
		AlgoLinePointLine algo = new AlgoLinePointLine(cons, label, P, l);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Line named label through Point P orthogonal to vector v
	 * 
	 * @param label
	 *            label
	 * @param P
	 *            point
	 * @param v
	 *            normal vector
	 * @return line
	 */
	final public GeoLine orthogonalLine(String label, GeoPoint P, GeoVector v) {
		AlgoOrthoLinePointVector algo = new AlgoOrthoLinePointVector(cons,
				label, P, v);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Line named label through Point P orthogonal to line l
	 * 
	 * @param label
	 *            label
	 * @param P
	 *            point
	 * @param l
	 *            orthogonal line
	 * @return line
	 */
	final public GeoLine orthogonalLine(String label, GeoPoint P, Lineable2D l) {
		AlgoOrthoLinePointLine algo = new AlgoOrthoLinePointLine(cons, label, P,
				l);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Line bisector of points A, B
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            point
	 * @param B
	 *            point
	 * @return line bisector
	 */
	final public GeoLine lineBisector(String label, GeoPoint A, GeoPoint B) {
		AlgoLineBisector algo = new AlgoLineBisector(cons, label, A, B);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Line bisector of segment s
	 * 
	 * @param label
	 *            label
	 * @param s
	 *            segment
	 * 
	 * @return line bisector
	 */
	final public GeoLine lineBisector(String label, GeoSegment s) {
		AlgoLineBisectorSegment algo = new AlgoLineBisectorSegment(cons, label,
				s);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Angular bisector of points A, B, C
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 * 
	 * @return angular bisector
	 */
	final public GeoLine angularBisector(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoAngularBisectorPoints algo = new AlgoAngularBisectorPoints(cons,
				A, B, C);
		GeoLine g = algo.getLine();
		g.setLabel(label);
		return g;
	}

	/**
	 * Angular bisectors of lines g, h
	 * 
	 * @param labels
	 *            labels
	 * @param g
	 *            line
	 * @param h
	 *            line
	 * 
	 * @return angular bisectors
	 */
	final public GeoLine[] angularBisector(String[] labels, GeoLine g,
			GeoLine h) {
		AlgoAngularBisectorLines algo = new AlgoAngularBisectorLines(cons,
				labels, g, h);
		GeoLine[] lines = algo.getLines();
		return lines;
	}

	/**
	 * Vector named label from Point P to Q
	 * 
	 * @param label
	 *            label
	 * @param P
	 *            start point
	 * @param Q
	 *            end point
	 * 
	 * @return vector
	 */
	final public GeoVector vector(String label, GeoPoint P, GeoPoint Q) {
		AlgoVector algo = new AlgoVector(cons, P, Q);
		GeoVector v = (GeoVector) algo.getVector();
		v.setEuclidianVisible(true);
		v.setLabel(label);
		// notifyUpdate(v);
		return v;
	}

	/**
	 * Vector (0,0) to P
	 * 
	 * @param label
	 *            label
	 * @param P
	 *            endpoint
	 * 
	 * @return vector
	 */
	final public GeoVectorND vector(String label, GeoPointND P) {
		GeoVectorND v = createVector(label, P);
		v.setEuclidianVisible(true);
		v.update();
		// notifyUpdate(v);
		return v;
	}

	/**
	 * @param label
	 *            vector label
	 * @param P
	 *            endbpoint
	 * @return vector(O,P)
	 */
	protected GeoVectorND createVector(String label, GeoPointND P) {
		AlgoVectorPoint algo = new AlgoVectorPoint(cons, label, P);
		return algo.getVector();
	}

	/**
	 * @param label
	 *            vector label
	 * @param p0
	 *            start point
	 * @param p1
	 *            end point
	 * @return vector
	 */
	public GeoElement vectorND(String label, GeoPointND p0, GeoPointND p1) {
		if (p0.isGeoElement3D() || p1.isGeoElement3D()) {
			return cons.getKernel().getManager3D().vector3D(label, p0, p1);
		}
		return vector(label, (GeoPoint) p0, (GeoPoint) p1);
	}

	/**
	 * Slope of line g or function f
	 * 
	 * @param label
	 *            line label
	 * @param g
	 *            line
	 * @param f
	 *            function
	 * @return slope
	 */
	final public GeoNumeric slope(String label, GeoLine g, GeoFunction f) {
		AlgoSlope algo = new AlgoSlope(cons, g, f);
		GeoNumeric slope = algo.getSlope();
		slope.setLabel(label);
		return slope;
	}

	/**
	 * LineSegment named label from Point P to Point Q
	 * 
	 * @param label
	 *            output label
	 * @param P
	 *            start point
	 * @param Q
	 *            end point
	 * @return segment
	 */
	final public GeoSegment segment(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(cons,  P, Q);
		GeoSegment s = algo.getSegment();
		s.setLabel(label);
		return s;
	}

	/**
	 * Creates a free or dependent list object with the given elements
	 *
	 * @param geoElementList
	 *            list of GeoElement objects
	 * @param isIndependent
	 *            whether to create independent list
	 * @return list with given elements
	 */
	final public GeoList list(
			ArrayList<GeoElement> geoElementList, boolean isIndependent) {
		if (isIndependent) {
			GeoList list = new GeoList(cons);
			int size = geoElementList.size();
			for (int i = 0; i < size; i++) {
				list.add(geoElementList.get(i));
			}
			return list;
		}
		AlgoDependentList algoList = new AlgoDependentList(cons, geoElementList, false);
		return algoList.getGeoList();
	}

	/**
	 * Function dependent on coefficients of arithmetic expressions with
	 * variables, represented by trees.
	 * 
	 * @param fun
	 *            function definition
	 * @param info
	 *            evaluation flags
	 * @return function
	 */
	final public GeoFunction dependentFunction(Function fun, EvalInfo info) {
		AlgoDependentFunction algo = new AlgoDependentFunction(cons, fun,
				info.isLabelOutput(), !info.isUsingCAS());

		// auto label for f'' to be f'' etc

		return algo.getFunction();
	}

	/**
	 * @param label
	 *            output label
	 * @param geoElement
	 *            linked geo
	 * @return input box
	 */
	public GeoInputBox textfield(String label, GeoElement geoElement) {
		AlgoInputBox at = new AlgoInputBox(cons, label, geoElement);
		return at.getResult();
	}

	/**
	 * Line named label through Point P with direction of vector v
	 * 
	 * @param label
	 *            label
	 * @param P
	 *            point
	 * @param v
	 *            direction
	 * 
	 * @return line
	 */
	final public GeoLine line(String label, GeoPoint P, GeoVector v) {
		AlgoLinePointVector algo = new AlgoLinePointVector(cons, label, P, v);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Creates new point B with distance n from A and new segment AB The
	 * labels[0] is for the segment, labels[1] for the new point
	 * 
	 * @param labels
	 *            labels
	 * @param A
	 *            start point
	 * @param n
	 *            length
	 * 
	 * @return segment and endpoint
	 */
	final public GeoElement[] segment(String[] labels, GeoPointND A,
			GeoNumberValue n) {
		// this is actually a macro
		String pointLabel = null, segmentLabel = null;
		if (labels != null) {
			switch (labels.length) {
			case 2:
				segmentLabel = labels[0];
				pointLabel = labels[1];
				break;
			case 1:
				segmentLabel = labels[0];
				break;
			default:
			}
		}

		return segmentFixed(pointLabel, segmentLabel, A, n);
	}

	/**
	 * @param pointLabel
	 *            endpoint label
	 * @param segmentLabel
	 *            segment label
	 * @param a
	 *            start point
	 * @param n
	 *            length
	 * @return segment and endpoint
	 */
	protected GeoElement[] segmentFixed(String pointLabel, String segmentLabel,
			GeoPointND a, GeoNumberValue n) {
		GeoPoint A = (GeoPoint) a;

		// create a circle around A with radius n
		AlgoCirclePointRadius algoCircle = new AlgoCirclePointRadius(cons, A,
				n);
		cons.removeFromConstructionList(algoCircle);
		// place the new point on the circle
		AlgoPointOnPath algoPoint = new AlgoPointOnPath(cons,
				algoCircle.getCircle(), A.inhomX + n.getDouble(), A.inhomY);
		algoPoint.getP().setLabel(pointLabel);

		// return segment and new point
		GeoElement[] ret = {
				segment(segmentLabel, A, (GeoPoint) algoPoint.getP()),
				(GeoElement) algoPoint.getP() };

		return ret;
	}

	/**
	 * Creates a new point C by rotating B around A using angle alpha and a new
	 * angle BAC (for positive orientation) resp. angle CAB (for negative
	 * orientation).
	 * 
	 * @param labels
	 *            labels[0] is for the angle, labels[1] for the new point
	 * @param B
	 *            point
	 * @param A
	 *            point
	 * @param alpha
	 *            angle size
	 * @param posOrientation
	 *            orientation
	 * @return angle
	 */
	final public GeoElement[] angle(String[] labels, GeoPoint B, GeoPoint A,
			GeoNumberValue alpha, boolean posOrientation) {
		// this is actually a macro
		String pointLabel = null, angleLabel = null;
		if (labels != null) {
			switch (labels.length) {
			default:
				// do nothing
				break;
			case 2:
				pointLabel = labels[1];
				// fall through
			case 1:
				angleLabel = labels[0];
			}
		}

		// rotate B around A using angle alpha
		GeoPoint C = (GeoPoint) rotate(pointLabel, B, alpha, A)[0];

		// create angle according to orientation
		GeoAngle angle;
		if (posOrientation) {
			angle = angle(angleLabel, B, A, C);
		} else {
			angle = angle(angleLabel, C, A, B);
		}
		// ensure we won't get angle e.g. in 0-180degrees due to default
		angle.setAngleStyle(AngleStyle.ANTICLOCKWISE);

		// return angle and new point
		GeoElement[] ret = { angle, C };
		return ret;
	}

	/**
	 * rotate geoRot by angle phi around Q
	 * 
	 * @param label
	 *            label
	 * @param geoRot
	 *            rotation pre-image
	 * @param phi
	 *            angle
	 * @param Q
	 *            center
	 * @return rotated geos
	 */
	public GeoElement[] rotate(String label, GeoElement geoRot,
			GeoNumberValue phi, GeoPointND Q) {
		Transform t = new TransformRotate(cons, phi, Q);
		return t.transform(geoRot, label);
	}

	/**
	 * Angle named label between line g and line h
	 * 
	 * @param label
	 *            label
	 * @param g
	 *            line
	 * @param h
	 *            line
	 * @return angle
	 */
	final public GeoAngle angle(String label, GeoLine g, GeoLine h) {
		AlgoAngleLines algo = new AlgoAngleLines(cons, label, g, h);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/**
	 * Angle named label between vector v and vector w
	 * 
	 * @param label
	 *            label
	 * @param v
	 *            vector
	 * @param w
	 *            vector
	 * @return angle
	 */
	final public GeoAngle angle(String label, GeoVector v, GeoVector w) {
		AlgoAngleVectors algo = new AlgoAngleVectors(cons, v, w);
		GeoAngle angle = algo.getAngle();
		angle.setLabel(label);
		return angle;
	}

	/**
	 * Angle named label between three points
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 * @return angle
	 */
	final public GeoAngle angle(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoAnglePoints algo = new AlgoAnglePoints(cons, A, B, C);
		GeoAngle angle = algo.getAngle();
		angle.setLabel(label);
		return angle;
	}

	/**
	 * all angles of given polygon
	 * 
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 * @return all angles in a polygon
	 */
	final public GeoElement[] angles(String[] labels, GeoPolygon poly) {
		return angles(labels, poly, false);
	}

	/**
	 * all angles of given polygon
	 * 
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 * @param internalAngle
	 *            angles should be internal e.g. direction dependent
	 * @return all angles in a polygon
	 */
	final public GeoElement[] angles(String[] labels, GeoPolygon poly, boolean internalAngle) {
		AlgoAnglePolygon algo = new AlgoAnglePolygon(cons, labels, poly, internalAngle);
		GeoElement[] angles = algo.getAngles();
		return angles;
	}

	/**
	 * @param isAngle
	 *            whether to return angle
	 * @return construction default for number or angle
	 */
	public GeoNumeric getDefaultNumber(boolean isAngle) {
		return (GeoNumeric) cons.getConstructionDefaults()
				.getDefaultGeo(isAngle ? ConstructionDefaults.DEFAULT_ANGLE
						: ConstructionDefaults.DEFAULT_NUMBER);
	}

	/**
	 * circle with through points A, B, C
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            point on circle
	 * @param B
	 *            point on circle
	 * @param C
	 *            point on circle
	 * @return circle
	 */
	final public GeoConic circle(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoCircleThreePoints algo = new AlgoCircleThreePoints(cons, A,
				B, C);
		GeoConic circle = (GeoConic) algo.getCircle();
		circle.setLabel(label);
		return circle;
	}

	/**
	 * circle arc from three points
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            center
	 * @param B
	 *            start point
	 * @param C
	 *            end point
	 * 
	 * @return circle arc
	 */
	final public GeoConicPart circumcircleArc(String label, GeoPoint A,
			GeoPoint B, GeoPoint C) {
		AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons,
				label, A, B, C, GeoConicNDConstants.CONIC_PART_ARC);
		return algo.getConicPart();
	}

	/**
	 * circle sector from three points
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            start point
	 * @param B
	 *            point on arc
	 * @param C
	 *            end point
	 * @return circle arc
	 */
	final public GeoConicPart circumcircleSector(String label, GeoPoint A,
			GeoPoint B, GeoPoint C) {
		AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons,
				label, A, B, C, GeoConicNDConstants.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}

	/**
	 * circle arc/sector from center and two points on arc/sector
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            center
	 * @param B
	 *            start point
	 * @param C
	 *            end point
	 * @param type
	 *            type
	 * @return circle arc
	 */
	final public GeoConicPart circleArcSector(String label, GeoPoint A,
			GeoPoint B, GeoPoint C, int type) {
		AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, A, B, C,
				type);
		algo.getConicPart().setLabel(label);
		return algo.getConicPart();
	}

	/**
	 * Center of conic
	 * 
	 * @param label
	 *            label
	 * @param c
	 *            conic
	 * @return center
	 */
	final public GeoPointND center(String label, GeoConicND c) {
		AlgoCenterConic algo = new AlgoCenterConic(cons, label, c);
		GeoPointND midpoint = algo.getPoint();
		return midpoint;
	}

	/*********************************************
	 * CONIC PART
	 *********************************************/

	/**
	 * circle with midpoint M and radius r
	 * 
	 * @param label
	 *            label
	 * @param M
	 *            center
	 * @param r
	 *            radius
	 * @return circle
	 */
	public GeoConicND circle(String label, GeoPointND M, GeoNumberValue r) {
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons,
				(GeoPoint) M, r);
		GeoConic circle = algo.getCircle();
		circle.setLabel(label);
		return circle;
	}

	/**
	 * circle with midpoint M and radius segment Michael Borcherds 2008-03-15
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            point
	 * @param segment
	 *            radius
	 * @return circle
	 */
	final public GeoConic circle(String label, GeoPoint A, GeoSegment segment) {
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, A, segment);
		GeoConic circle = algo.getCircle();
		circle.setLabel(label);
		return circle;
	}

	/**
	 * circle with midpoint M through point P
	 * 
	 * @param label
	 *            label
	 * @param M
	 *            center
	 * @param P
	 *            point on circle
	 * @return circle
	 */
	final public GeoConic circle(String label, GeoPoint M, GeoPoint P) {
		AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, M, P);
		GeoConic circle = algo.getCircle();
		circle.setLabel(label);
		return circle;
	}

	/**
	 * semicircle with midpoint M through point P
	 * 
	 * @param label
	 *            label
	 * @param M
	 *            start point
	 * @param P
	 *            end point
	 * @return semicircle
	 */
	final public GeoConicPart semicircle(String label, GeoPoint M, GeoPoint P) {
		AlgoSemicircle algo = new AlgoSemicircle(cons, label, M, P);
		return algo.getSemicircle();
	}

	/**
	 * parabola with focus F and line l
	 * 
	 * @param label
	 *            label
	 * @param F
	 *            focus
	 * @param l
	 *            directrix
	 * @return parabola
	 */
	final public GeoConicND parabola(String label, GeoPointND F, GeoLineND l) {
		AlgoParabolaPointLine algo = new AlgoParabolaPointLine(cons, label, F,
				l);
		return algo.getParabola();
	}

	/**
	 * ellipse with foci A, B and length of first half axis a
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            focus
	 * @param B
	 *            focus
	 * @param a
	 *            half axis length
	 * @return ellipse
	 */
	final public GeoConicND ellipse(String label, GeoPointND A, GeoPointND B,
			GeoNumberValue a) {
		AlgoEllipseFociLength algo = new AlgoEllipseFociLength(cons, label, A,
				B, a);
		return algo.getConic();
	}

	/**
	 * ellipse with foci A, B passing thorugh C Michael Borcherds 2008-04-06
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            focus
	 * @param B
	 *            focus
	 * @param C
	 *            point on conic
	 * @param type
	 *            conic type
	 * @return ellipse or hyperbola
	 */
	final public GeoConicND ellipseHyperbola(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, final int type) {
		AlgoEllipseHyperbolaFociPoint algo = new AlgoEllipseHyperbolaFociPoint(
				cons, label, A, B, C, type);

		return algo.getConic();
	}

	/**
	 * hyperbola with foci A, B and length of first half axis a
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            focus
	 * @param B
	 *            focus
	 * @param a
	 *            half axis length
	 * @return hyperbola
	 */
	final public GeoConicND hyperbola(String label, GeoPointND A, GeoPointND B,
			GeoNumberValue a) {
		AlgoHyperbolaFociLength algo = new AlgoHyperbolaFociLength(cons, label,
				A, B, a);
		return algo.getConic();
	}

	/**
	 * conic through five points
	 * 
	 * @param label
	 *            label
	 * @param points
	 *            points
	 * @return conic
	 */
	final public GeoConicND conic(String label, GeoPoint[] points) {
		AlgoConicFivePoints algo = new AlgoConicFivePoints(cons, points);
		GeoConicND conic = algo.getConic();
		conic.setLabel(label);
		return conic;
	}

	/**
	 * diameter line conjugate to direction of g relative to c
	 * 
	 * @param label
	 *            label
	 * @param g
	 *            direction
	 * @param c
	 *            conic
	 * @return diameter line
	 */
	final public GeoElement diameterLine(String label, GeoLineND g,
			GeoConicND c) {
		AlgoDiameterLine algo = new AlgoDiameterLine(cons, label, c, g);
		return (GeoElement) algo.getDiameter();
	}

	/**
	 * diameter line conjugate to v relative to c
	 * 
	 * @param label
	 *            label
	 * @param v
	 *            vector
	 * @param c
	 *            conic
	 * @return diameter line
	 */
	final public GeoElement diameterLine(String label, GeoVectorND v,
			GeoConicND c) {
		AlgoDiameterVector algo = new AlgoDiameterVector(cons, label, c, v);
		return (GeoElement) algo.getDiameter();
	}

	/**
	 * Regular polygon with vertices A and B and n total vertices. The labels
	 * name the polygon itself, its segments and points
	 * 
	 * @param labels
	 *            labels
	 * @param A
	 *            first vertex
	 * @param B
	 *            second vertex
	 * @param n
	 *            number of vertices
	 * @return polygon + vertices + segments
	 */
	final public GeoElement[] regularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, GeoNumberValue n) {
		cons.getKernel().batchAddStarted();
		AlgoPolygonRegular algo = new AlgoPolygonRegular(cons, labels, A, B, n);
		cons.getKernel().batchAddComplete();
		return algo.getOutput();
	}

	/**
	 * Area named label of conic
	 * 
	 * @param label
	 *            label
	 * @param c
	 *            conic
	 * @return area
	 */
	final public GeoNumeric area(String label, GeoConicND c) {
		AlgoAreaConic algo = new AlgoAreaConic(cons, label, c);
		return algo.getArea();
	}

	/**
	 * Perimeter named label of GeoPolygon
	 * 
	 * @param label
	 *            label
	 * @param polygon
	 *            polygon
	 * @return perimeter
	 */
	final public GeoNumeric perimeter(String label, GeoPolygon polygon) {
		AlgoPerimeterPoly algo = new AlgoPerimeterPoly(cons, polygon);
		algo.getCircumference().setLabel(label);
		return algo.getCircumference();
	}

	/**
	 * Circumference named label of GeoConic
	 * 
	 * @param label
	 *            label
	 * @param conic
	 *            conic
	 * @return circumference
	 */
	final public GeoNumeric circumference(String label, GeoConicND conic) {
		AlgoCircumferenceConic algo = new AlgoCircumferenceConic(cons,
				conic);
		algo.getCircumference().setLabel(label);
		return algo.getCircumference();
	}

	/**
	 * dilate geoRot by r from S
	 * 
	 * @param label
	 *            label
	 * @param geoDil
	 *            geo to dilate
	 * @param r
	 *            ratio
	 * @param center
	 *            center
	 * @return rotated elements
	 */
	final public GeoElement[] dilate(String label, GeoElement geoDil,
			GeoNumberValue r, GeoPoint center) {
		Transform t = new TransformDilate(cons, r, center);
		return t.transform(geoDil, label);
	}

	/**
	 * Distance named label between points P and Q
	 * 
	 * @param label
	 *            label
	 * @param P
	 *            first point
	 * @param Q
	 *            second point
	 * @return distance
	 */
	final public GeoNumeric distance(String label, GeoPointND P, GeoPointND Q) {
		AlgoDistancePoints algo = new AlgoDistancePoints(cons, P, Q);
		GeoNumeric num = algo.getDistance();
		num.setLabel(label);
		return num;
	}

	/**
	 * Distance named label between point P and path g
	 * 
	 * @param label
	 *            label
	 * @param P
	 *            point
	 * @param g
	 *            path
	 * @return distance
	 */
	final public GeoNumeric distance(String label, GeoPointND P, GeoElementND g) {
		AlgoDistancePointObject algo = new AlgoDistancePointObject(cons, label,
				P, g);
		GeoNumeric num = algo.getDistance();
		return num;
	}

	/**
	 * @param label
	 *            output label
	 * @param func
	 *            funtion in x,y
	 * @return curve func(x,y)=0
	 */
	final public GeoImplicit implicitPoly(String label, GeoFunctionNVar func) {
		AlgoImplicitPolyFunction algo = new AlgoImplicitPolyFunction(cons,
				label, func);
		GeoImplicit implicitPoly = algo.getImplicitPoly();
		return implicitPoly;
	}

	/********************
	 * ALGORITHMIC PART *
	 ********************/

	/**
	 * Point in region with cartesian coordinates (x,y)
	 * 
	 * @param label
	 *            output label
	 * @param region
	 *            region
	 * @param x
	 *            initial x-coord
	 * @param y
	 *            initial y-coord
	 * @param addToConstruction
	 *            whether to add to the construction list
	 * @param complex
	 *            whether to use complex coords
	 * @param coords2D
	 *            whether to use 2d coords
	 * @return point in region
	 */
	final public GeoPoint pointIn(String label, Region region, double x,
			double y, boolean addToConstruction, boolean complex,
			boolean coords2D) {

		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
		}
		AlgoPointInRegion algo = new AlgoPointInRegion(cons, label, region, x,
				y);
		GeoPoint p = algo.getP();
		if (complex) {
			p.setMode(Kernel.COORD_COMPLEX);
		} else if (!coords2D) {
			p.setCartesian3D();
			p.update();
		}
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}

	/**
	 * @param label
	 *            point label
	 * @param region
	 *            region
	 * @param coords
	 *            close point coords
	 * @param addToConstruction
	 *            whether to add to construction
	 * @param complex
	 *            whether to use complex coords
	 * @param coords2D
	 *            whether to use 2D coords
	 * @return point
	 */
	public GeoPointND pointIn(String label, Region region, Coords coords,
			boolean addToConstruction, boolean complex, boolean coords2D) {
		return pointIn(label, region, coords.getX(), coords.getY(),
				addToConstruction, complex, coords2D);
	}

	/**
	 * Midpoint M = (P + Q)/2
	 * 
	 * @param label
	 *            label
	 * @param P
	 *            point
	 * @param Q
	 *            point
	 * @return midpoint
	 */
	final public GeoPoint midpoint(String label, GeoPoint P, GeoPoint Q) {
		AlgoMidpoint algo = new AlgoMidpoint(cons, P, Q);
		GeoPoint M = algo.getPoint();
		M.setLabel(label);
		return M;
	}

	/**
	 * Midpoint of segment
	 * 
	 * @param s
	 *            segment
	 * @return midpoint
	 */
	final public GeoPoint midpoint(GeoSegment s) {
		AlgoMidpointSegment algo = new AlgoMidpointSegment(cons, s);
		GeoPoint M = algo.getPoint();
		return M;
	}

	/**
	 * Length[list]
	 * 
	 * @param label
	 *            label
	 * @param list
	 *            list
	 * @return number of elements
	 */
	final public GeoNumeric length(String label, GeoList list) {
		AlgoListLength algo = new AlgoListLength(cons, list);
		algo.getLength().setLabel(label);
		return algo.getLength();
	}

	/**
	 * Length[locus]
	 * 
	 * @param label
	 *            output label
	 * @param locus
	 *            locus
	 * @return number of points in the locus
	 */
	final public GeoNumeric length(String label, GeoLocusable locus) {
		AlgoLengthLocus algo = new AlgoLengthLocus(cons, label, locus);
		return algo.getLength();
	}

	/**
	 * polygon P[0], ..., P[n-1] The labels name the polygon itself and its
	 * segments
	 * 
	 * @param labels
	 *            output labels
	 * @param P
	 *            vertices
	 * @return polygon
	 */
	public GeoElement[] polygon(String[] labels, GeoPointND[] P) {
		cons.getKernel().batchAddStarted();
		AlgoPolygon algo = new AlgoPolygon(cons, labels, P);
		cons.getKernel().batchAddComplete();
		return algo.getOutput();
	}

	/**
	 * Polygon with vertices from a list of points. Only the polygon is labeled, segments
	 * are not labeled (so no batch needed).
	 * 
	 * @param labels
	 *            labels
	 * @param pointList
	 *            list of vertices
	 * @return polygon
	 */
	public GeoElement[] polygon(String[] labels, GeoList pointList) {
		AlgoPolygon algo = new AlgoPolygon(cons, labels, pointList);
		return algo.getOutput();
	}

	/**
	 * polygon P[0], ..., P[n-1] The labels name the polygon itself and its
	 * segments
	 * 
	 * @param label
	 *            output label
	 * @param P
	 *            vertices
	 * @return polyline
	 */
	final public GeoElement[] polyLine(String label, GeoPointND[] P) {
		AlgoElement algo = new AlgoPolyLine(cons, P, null);
		algo.getOutput(0).setLabel(label);
		return algo.getOutput();
	}

	/**
	 * Intersect[polygon,polygon] G. Sturr
	 * 
	 * modified by thilina
	 * 
	 * @param labels
	 *            labels
	 * @param poly0
	 *            first polygon
	 * @param poly1
	 *            second polygon
	 * @param asRegion
	 *            whether to return region
	 * @return region or intersection points
	 */
	final public GeoElement[] intersectPolygons(String[] labels,
			GeoPolygon poly0, GeoPolygon poly1, boolean asRegion) {
		if (asRegion) {
			AlgoPolygonIntersection algo = new AlgoPolygonIntersection(cons,
					labels, poly0, poly1);
			GeoElement[] polygon = algo.getOutput();
			return polygon;
		}
		AlgoIntersectPolyLines algo = new AlgoIntersectPolyLines(cons, labels,
				poly0, poly1, true, true);
		return algo.getOutput();
	}

	/**
	 * Intersect[polygon, polygon] as region. This is used when loading saved
	 * files.
	 * 
	 * @author thilina
	 * 
	 * @param labels
	 *            labels
	 * @param poly0
	 *            first polygon
	 * @param poly1
	 *            second polygon
	 * @return region or intersection points
	 * 
	 * @param outputSizes
	 *            numbers of outputs per object type
	 * 
	 */

	final public GeoElement[] intersectPolygons(String[] labels,
			GeoPolygon poly0, GeoPolygon poly1, int[] outputSizes) {
		AlgoPolygonIntersection algo = new AlgoPolygonIntersection(cons, labels,
				poly0, poly1, outputSizes);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}

	/**
	 * Union[polygon,polygon] G. Sturr
	 * 
	 * @param labels
	 *            labels
	 * @param poly0
	 *            first polygon
	 * @param poly1
	 *            secod polygon
	 * @return union (polygon + vertices + segments)
	 */
	final public GeoElement[] union(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1) {
		AlgoPolygonUnion algo = new AlgoPolygonUnion(cons, labels, poly0,
				poly1);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}

	/**
	 * Union[polygon, polygon] as region. This is used when loading saved files
	 * 
	 * @author thilina
	 * @param labels
	 *            labels
	 * @param poly0
	 *            first polygon
	 * @param poly1
	 *            secod polygon
	 * @param outputSizes
	 *            output size per type
	 * @return union (polygon + vertices + segments)
	 */
	final public GeoElement[] union(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1, int[] outputSizes) {
		AlgoPolygonUnion algo = new AlgoPolygonUnion(cons, labels, poly0, poly1,
				outputSizes);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}

	/**
	 * Difference[polygon,polygon]
	 * 
	 * @author thilina
	 * @param labels
	 *            labels
	 * @param poly0
	 *            first polygon
	 * @param poly1
	 *            secod polygon
	 * @return set difference (polygon + vertices + segments)
	 */
	final public GeoElement[] difference(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1) {
		AlgoPolygonDifference algo = new AlgoPolygonDifference(cons, labels,
				poly0, poly1, null);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}

	/**
	 * Difference[polygon, polygon] as region. This is used when loading saved
	 * files
	 * 
	 * @author thilina
	 * @param labels
	 *            labels
	 * @param poly0
	 *            first polygon
	 * @param poly1
	 *            secod polygon
	 * @return set difference (polygon + vertices + segments)
	 */
	final public GeoElement[] difference(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1, int[] outputSizes) {
		AlgoPolygonDifference algo = new AlgoPolygonDifference(cons, labels,
				poly0, poly1, null, outputSizes);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}

	/**
	 * Difference[polygon,polygon, boolean exclusive]
	 * 
	 * @author thilina
	 * @param labels
	 *            output labels
	 * @param poly0
	 *            polygon
	 * @param poly1
	 *            polygon
	 * @param exclusive
	 *            whether to use XOR
	 * @return difference or XOR of the polygons
	 */
	final public GeoElement[] difference(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1, GeoBoolean exclusive) {
		AlgoPolygonDifference algo = new AlgoPolygonDifference(cons, labels,
				poly0, poly1, exclusive);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}

	/**
	 * locus line for Q dependent on P. Note: P must be a point on a path.
	 * 
	 * @param label
	 *            output label
	 * @param Q
	 *            locus point
	 * @param P
	 *            moving point
	 * @return locus
	 */
	final public GeoElement locus(String label, GeoPointND Q, GeoPointND P) {
		if (!locusCheck(P, Q)) {
			return null;
		}
		if (P.getPath() instanceof GeoList) {
			if (((GeoList) P.getPath()).shouldUseAlgoLocusList(true)) {
				return new AlgoLocusList(cons, label, (GeoPoint) Q,
						(GeoPoint) P).getLocus();
			}
		}
		return new AlgoLocus(cons, label, Q, P).getLocus();
	}

	/**
	 * @param P
	 *            locus point
	 * @param Q
	 *            moving point
	 * @return whether Locus(P,Q) is possible
	 */
	final public static boolean locusCheck(GeoPointND P, GeoPointND Q) {
		return P.getPath() != null && Q.getPath() == null
				&& ((GeoElement) P).isParentOf(Q);
	}

	/**
	 * locus line for Q dependent on P. Note: P must be a visible slider
	 * 
	 * @param label
	 *            label
	 * @param Q
	 *            locus point
	 * @param P
	 *            slider
	 * @return locus
	 */
	final public GeoElement locus(String label, GeoPointND Q, GeoNumeric P) {
		if (!locusCheck(Q, P)) {
			return null;
		}
		return locusNoCheck(label, Q, P);
	}

	/**
	 * @param Q
	 *            locus point
	 * @param P
	 *            point on path
	 * @return whether locus(Q,P) is possible
	 */
	public static boolean locusCheck(GeoPointND Q, GeoNumeric P) {
		return P.isSlider() && P.isDefined() && P.isAnimatable()
				&& Q.getPath() == null && P.isParentOf(Q);
	}

	/**
	 * @param label
	 *            output label
	 * @param Q
	 *            locus point
	 * @param P
	 *            slider
	 * @return locus
	 */
	protected GeoElement locusNoCheck(String label, GeoPointND Q,
			GeoNumeric P) {
		AlgoLocusSlider algo = new AlgoLocusSlider(cons, label, (GeoPoint) Q,
				P);
		return algo.getLocus();
	}

	/**
	 * Distance named label between line g and line h
	 * 
	 * @param label
	 *            output label
	 * @param g
	 *            line
	 * @param h
	 *            line
	 * @return distance between lines
	 */
	public GeoNumeric distance(String label, GeoLineND g, GeoLineND h) {
		AlgoDistanceLineLine algo = new AlgoDistanceLineLine(cons, label,
				(GeoLine) g, (GeoLine) h);
		GeoNumeric num = algo.getDistance();
		return num;
	}

	/**
	 * IntersectLines yields intersection point named label of lines g, h
	 * 
	 * @param label
	 *            label
	 * @param g
	 *            line
	 * @param h
	 *            line
	 * @return intersection
	 */
	public GeoPointND intersectLines(String label, GeoLineND g, GeoLineND h) {
		AlgoIntersectLines algo = new AlgoIntersectLines(cons, label,
				(GeoLine) g, (GeoLine) h);
		GeoPoint S = algo.getPoint();
		return S;
	}

	/**
	 * yields intersection points named label of line g and polyLine p
	 * 
	 * @param labels
	 *            labels
	 * @param g
	 *            line
	 * @param p
	 *            polyline
	 * @return intersections
	 */
	final public GeoElement[] intersectLinePolyLine(String[] labels, GeoLine g,
			GeoPolyLine p) {
		AlgoIntersectLinePolyLine algo = new AlgoIntersectLinePolyLine(cons,
				labels, g, p);
		return algo.getOutput();
	}

	/**
	 * yields intersection points named label of line g and polyLine p
	 * 
	 * @param labels
	 *            output labels
	 * @param g
	 *            line
	 * @param p
	 *            curve
	 * @return intersection points
	 */
	final public GeoElement[] intersectLineCurve(String[] labels, GeoLine g,
			GeoCurveCartesian p) {

		AlgoIntersectLineCurve algo = new AlgoIntersectLineCurve(cons, labels,
				g, p);
		return algo.getOutput();
	}

	/**
	 * yields intersection points named label of polyLine g and polyLine p
	 *
	 * @author thilina
	 * @param labels
	 *            output labels
	 * @param g
	 *            polyline
	 * @param p
	 *            polyline
	 * @return intersection points
	 */
	final public GeoElement[] intersectPolyLines(String[] labels, GeoPolyLine g,
			GeoPolyLine p) {
		AlgoIntersectPolyLines algo = new AlgoIntersectPolyLines(cons, labels,
				g, p, false, false);
		return algo.getOutput();
	}

	/**
	 * yields intersection points named label of curve g and curve p
	 * 
	 * @param labels
	 *            output labels
	 * @param g
	 *            curve
	 * @param p
	 *            curve
	 * @return intersection points
	 */
	final public GeoElement[] intersectCurveCurve(String[] labels,
			GeoCurveCartesian g, GeoCurveCartesian p) {
		AlgoIntersectCurveCurve algo = new AlgoIntersectCurveCurve(cons, labels,
				g, p);
		return algo.getOutput();
	}

	/**
	 * yields intersection points named label of curve c1 and curve c1 (x,y)
	 * determines the parameters for the iteration
	 * 
	 * @param labels
	 *            output labels
	 * @param c1
	 *            first curve
	 * @param c2
	 *            second coord
	 * @param x
	 *            x-coord estimate
	 * @param y
	 *            y-coord estimate
	 * @return intersection points
	 */
	final public GeoElement[] intersectCurveCurveSingle(String[] labels,
			GeoCurveCartesian c1, GeoCurveCartesian c2, double x, double y) {

		GeoPoint p = new GeoPoint(cons, x, y, 1.0);

		double t1 = c1.getClosestParameter(p,
				(c1.getMinParameter() + c1.getMaxParameter()) / 2);
		double t2 = c2.getClosestParameter(p,
				(c2.getMinParameter() + c2.getMaxParameter()) / 2);

		AlgoIntersectCurveCurve algo = new AlgoIntersectCurveCurve(cons, labels,
				c1, c2, new GeoNumeric(cons, t1), new GeoNumeric(cons, t2));
		return algo.getOutput();
	}

	/**
	 * yields intersection points named label of line g and polygon p (as
	 * boundary)
	 * 
	 * @param labels
	 *            output labels
	 * @param g
	 *            line
	 * @param p
	 *            polygon
	 * @return intersetion points
	 */
	final public GeoElement[] intersectLinePolygon(String[] labels, GeoLine g,
			GeoPolygon p) {
		AlgoIntersectLinePolyLine algo = new AlgoIntersectLinePolyLine(cons,
				labels, g, p);
		return algo.getOutput();
	}

	/**
	 * yields intersection points named label of PolyLine g and polygon p (as
	 * boundary)
	 * 
	 * @author thilina
	 * @param labels
	 *            output labels
	 * @param g
	 *            polyline
	 * @param p
	 *            polygon
	 * @return intersection points
	 */
	final public GeoElement[] intersectPolyLinePolygon(String[] labels,
			GeoPolyLine g, GeoPolygon p) {
		AlgoIntersectPolyLines algo = new AlgoIntersectPolyLines(cons, labels,
				g, p, false, true);
		return algo.getOutput();
	}

	/**
	 * Intersects f and g using starting point A (with Newton's root finding)
	 * 
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 * @param g
	 *            function
	 * @param A
	 *            initial point
	 * @return intersection point
	 */
	final public GeoPoint intersectFunctions(String label, GeoFunctionable f,
			GeoFunctionable g, GeoPoint A) {
		AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton(
				cons, label, f, g, A);
		GeoPoint S = algo.getIntersectionPoint();
		return S;
	}

	/**
	 * Intersects f and l using starting point A (with Newton's root finding)
	 * 
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 * @param l
	 *            line
	 * @param A
	 *            initial point
	 * @return intersection point
	 */
	final public GeoPoint intersectFunctionLine(String label, GeoFunctionable f,
			GeoLine l, GeoPoint A) {

		AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(
				cons, label, f, l, A);
		GeoPoint S = algo.getIntersectionPoint();
		return S;
	}

	/**
	 * IntersectLineConic yields intersection points named label1, label2 of
	 * line g and conic c
	 * 
	 * @param labels
	 *            output labels
	 * @param g
	 *            line
	 * @param c
	 *            conic
	 * @return intersection points
	 */
	public GeoPointND[] intersectLineConic(String[] labels, GeoLineND g,
			GeoConicND c) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm((GeoLine) g,
				(GeoConic) c);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		LabelManager.setLabels(labels, points);
		return points;
	}

	/**
	 * IntersectPolyLineConic yields intersection points of polyLine g and conic
	 * c
	 * 
	 * @param labels
	 *            output labels
	 * @param g
	 *            polyline
	 * @param c
	 *            conic
	 * @return intersection points
	 */
	public GeoElement[] intersectPolyLineConic(String[] labels, GeoPolyLine g,
			GeoConic c) {
		AlgoIntersectPolyLineConic algo = getIntersectionAlgorithm(
				g, c);
		algo.setPrintedInXML(true);
		GeoElement[] points = algo.getOutput();
		LabelManager.setLabels(labels, points);
		return points;
	}

	/**
	 * IntersectPolygonConic yields intersection points of polygon g and conic c
	 * 
	 * @param labels
	 *            output labels
	 * @param g
	 *            polygon
	 * @param c
	 *            conic
	 * @return intersection points or region
	 */
	public GeoElement[] intersectPolygonConic(String[] labels, GeoPolygon g,
			GeoConic c) {
		AlgoIntersectPolyLineConic algo = getIntersectionAlgorithm(g, c);
		algo.setPrintedInXML(true);
		GeoElement[] points = algo.getOutput();
		LabelManager.setLabels(labels, points);
		return points;
	}

	/**
	 * IntersectConics yields intersection points named label1, label2, label3,
	 * label4 of conics c1, c2
	 * 
	 * @param labels
	 *            output labels
	 * @param a
	 *            conic
	 * @param b
	 *            conic
	 * @return intersection points
	 */
	public GeoPointND[] intersectConics(String[] labels, GeoConicND a,
			GeoConicND b) {
		AlgoIntersectConics algo = getIntersectionAlgorithm((GeoConic) a,
				(GeoConic) b);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		LabelManager.setLabels(labels, points);
		return points;
	}

	/**
	 * IntersectPolynomials yields all intersection points of polynomials a, b
	 * 
	 * @param labels
	 *            output labels
	 * @param a
	 *            polynomial function
	 * @param b
	 *            polynomial function
	 * @return intersection points
	 */
	final public GeoPoint[] intersectPolynomials(String[] labels,
			GeoFunctionable a, GeoFunctionable b) {
		if (isConditionalPolynomial(a) && b.isPolynomialFunction(false)) {
			AlgoRootsPolynomialInterval algo = new AlgoRootsPolynomialInterval(
					cons, labels, a, b);
			GeoPoint[] g = algo.getRootPoints();
			return g;
		}
		if (isConditionalPolynomial(b) && a.isPolynomialFunction(false)) {
			AlgoRootsPolynomialInterval algo = new AlgoRootsPolynomialInterval(
					cons, labels, b, a);
			GeoPoint[] g = algo.getRootPoints();
			return g;
		}
		// TODO decide polynomial when CAS not loaded
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) {

			// dummy point
			GeoPoint A = createDummyPoint();
			// we must check that getLabels() didn't return null
			String label = labels == null ? null : labels[0];
			AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton(
					cons, label, a, b, A);
			GeoPoint[] ret = { algo.getIntersectionPoint() };
			return ret;
		}

		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);
		algo.setPrintedInXML(true);
		algo.setLabels(labels);
		GeoPoint[] points = algo.getIntersectionPoints();
		return points;
	}

	private GeoPoint createDummyPoint() {
		GeoPoint pt = new GeoPoint(cons);
		pt.setCoords(0, 0, 1);
		return pt;
	}

	/**
	 * get only one intersection point of two polynomials a, b that is near to
	 * the given location (xRW, yRW)
	 * 
	 * @param label
	 *            output label
	 * @param a
	 *            polynomial
	 * @param b
	 *            polynomial
	 * @param xRW
	 *            initial x-coord
	 * @param yRW
	 *            initial y-coord
	 * @return intersection close to initial point
	 */
	final public GeoPoint intersectPolynomialsSingle(String label,
			GeoFunctionable a, GeoFunctionable b, double xRW, double yRW) {
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) {
			return null;
		}

		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	private static boolean isConditionalFunction(GeoFunctionable f) {
		Function fun = f.getFunction();
		return fun.getFunctionExpression() != null
				&& fun.getFunctionExpression().getOperation().isIf();
	}

	private boolean isConditionalPolynomial(GeoFunctionable f) {
		Function fun = f.getFunction();
		if (fun.getFunctionExpression() != null
				&& fun.getFunctionExpression().getOperation().isIf()) {
			Function test = new Function(cons.getKernel(),
					fun.getFunctionExpression().deepCopy(cons.getKernel())
							.getRightTree());
			test.initFunction();
			return test.isPolynomialFunction(false, true);
		}
		return false;
	}

	/**
	 * IntersectPolyomialLine yields all intersection points of polynomial f and
	 * line l
	 * 
	 * @param labels
	 *            output labels
	 * @param f
	 *            function
	 * @param line
	 *            output line
	 * @param initPoint
	 *            initial point
	 * @return intersection points
	 */
	final public GeoPoint[] intersectPolynomialLine(String[] labels,
			GeoFunctionable f, GeoLine line, GeoPoint initPoint) {
		// TODO decide polynomial when CAS not loaded ?
		if (isConditionalPolynomial(f)) {
				AlgoRootsPolynomialInterval algo = new AlgoRootsPolynomialInterval(
					cons, labels, f, line);
				GeoPoint[] g = algo.getRootPoints();
				return g;
		}

		if (isConditionalFunction(f)) {
			GeoPoint A = initPoint;
			if (A == null) {
				A = createDummyPoint();
			}
			AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(
					cons, labels == null ? null : labels[0], f,
					line, A);
			GeoPoint g = algo.getRootPoint();
			GeoPoint[] ret = { g };
			return ret;
		}

		if (!f.isPolynomialFunction(false)) {

			// dummy point
			GeoPoint A = initPoint;
			if (A == null) {
				A = createDummyPoint();
			}
			// we must check that getLabels() didn't return null
			String label = labels == null ? null : labels[0];
			AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(
					cons, label, f, line, A);
			GeoPoint[] ret = { algo.getIntersectionPoint() };
			return ret;
		}

		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, line);
		algo.setPrintedInXML(true);
		algo.setLabels(labels);
		GeoPoint[] points = algo.getIntersectionPoints();
		return points;
	}

	/**
	 * Intersect function/polynomial-polyLine yields all intersection points of
	 * GeoFunction f and polyLine l
	 * 
	 * @author thilina
	 * @param labels
	 *            output labels
	 * @param f
	 *            function
	 * @param pl
	 *            polyline
	 * @return intersection points
	 */
	final public GeoElement[] intersectPolynomialPolyLine(String[] labels,
			GeoFunctionable f, GeoPolyLine pl) {
		AlgoIntersectPolynomialPolyLine algo = new AlgoIntersectPolynomialPolyLine(
				cons, labels, f, pl, false);
		return algo.getOutput();
	}

	/**
	 * Intersect function/Polynomial-polygon yields all intersection points of
	 * GeoFunction f and polygon pl
	 * 
	 * @author thilina
	 * @param labels
	 *            output labels
	 * @param f
	 *            function
	 * @param pl
	 *            polygon
	 * @return intersection points
	 */
	final public GeoElement[] intersectPolynomialPolygon(String[] labels,
			GeoFunctionable f, GeoPolygon pl) {

		AlgoIntersectPolynomialPolyLine algo = new AlgoIntersectPolynomialPolyLine(
				cons, labels, f, pl, true);

		return algo.getOutput();
	}

	/**
	 * Intersect function-polyline yields all intersection points of GeoFunction
	 * f and polyline pl
	 * 
	 * @author thilina
	 * @param labels
	 *            output labels
	 * @param f
	 *            function
	 * @param pl
	 *            polyline
	 * @param initPoint
	 *            initial point
	 * @return intersections
	 */
	final public GeoElement[] intersectNPFunctionPolyLine(String[] labels,
			GeoFunctionable f, GeoPolyLine pl, GeoPoint initPoint) {

		AlgoIntersectNpFunctionPolyLine algo = new AlgoIntersectNpFunctionPolyLine(
				cons, labels, initPoint, f, pl, false);
		return algo.getOutput();
	}

	/**
	 * Intersect function-polygon yields all intersection points of GeoFunction
	 * f and polygon pl
	 * 
	 * @author thilina
	 * @param labels
	 *            output labels
	 * @param f
	 *            function
	 * @param pl
	 *            polygon
	 * @param initPoint
	 *            initial point
	 * @return intersctions
	 */
	final public GeoElement[] intersectNPFunctionPolygon(String[] labels,
			GeoFunctionable f, GeoPolygon pl, GeoPoint initPoint) {

		AlgoIntersectNpFunctionPolyLine algo = new AlgoIntersectNpFunctionPolyLine(
				cons, labels, initPoint, f, pl, true);
		return algo.getOutput();
	}

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 * 
	 * @param label
	 *            output label
	 * @param a
	 *            conic
	 * @param b
	 *            conic
	 * @param xRW
	 *            initial x-coord
	 * @param yRW
	 *            initial y-coord
	 * @return intersection point
	 */
	final public GeoPoint intersectConicsSingle(String label, GeoConic a,
			GeoConic b, double xRW, double yRW) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get intersection points of a polynomial and a conic
	 * 
	 * @param labels
	 *            output labels
	 * @param f
	 *            polynomial function
	 * @param c
	 *            conic
	 * @return intersections
	 */
	final public GeoPoint[] intersectPolynomialConic(String[] labels,
			GeoFunction f, GeoConic c) {
		AlgoIntersectPolynomialConic algo = getIntersectionAlgorithm(f, c);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		// GeoElement.setLabels(labels, points);
		algo.setLabels(labels);
		return points;
	}

	/**
	 * get intersection points of a implicitPoly and a line
	 * 
	 * @param labels
	 *            output labels
	 * @param p
	 *            implicit curve
	 * @param l
	 *            line
	 * @return intersections
	 */
	final public GeoPoint[] intersectImplicitpolyLine(String[] labels,
			GeoImplicit p, GeoLine l) {
		AlgoIntersectImplicitpolyParametric algo = getIntersectionAlgorithm(p,
				l);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * get intersection points of a implicitPoly and a polyline
	 * 
	 * @param labels
	 *            output labels
	 * @param p
	 *            implicit curve
	 * @param l
	 *            polyline
	 * @return intersections
	 */
	final public GeoPoint[] intersectImplicitpolyPolyLine(String[] labels,
			GeoImplicit p, GeoPolyLine l) {
		AlgoIntersectImplicitpolyPolyLine algo = getIntersectionAlgorithm(p, l);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * get intersection points of a implicitPoly and a polygon
	 * 
	 * @param labels
	 *            output labels
	 * @param curve
	 *            implicit curve
	 * @param poly
	 *            polygon
	 * @return intersection points
	 */
	final public GeoPoint[] intersectImplicitpolyPolygon(String[] labels,
			GeoImplicit curve, GeoPolygon poly) {
		AlgoIntersectImplicitpolyPolyLine algo = getIntersectionAlgorithm(curve, poly);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * get intersection points of a implicitPoly and a polynomial
	 * 
	 * @param labels
	 *            output labels
	 * @param p
	 *            implicit curve
	 * @param f
	 *            function
	 * @return intersection points
	 */
	final public GeoPoint[] intersectImplicitpolyPolynomial(String[] labels,
			GeoImplicit p, GeoFunctionable f) {
		// if (!f.isPolynomialFunction(false))
		// return null;
		AlgoIntersectImplicitpolyParametric algo = getIntersectionAlgorithm(p,
				f);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * get intersection points of two implicitPolys
	 * 
	 * @param labels
	 *            output labels
	 * @param p1
	 *            implicit curve
	 * @param p2
	 *            implicit curve
	 * @return intersection points
	 */
	final public GeoPoint[] intersectImplicitpolys(String[] labels,
			GeoImplicit p1, GeoImplicit p2) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * get intersection points of implicitPoly and conic
	 * 
	 * @param labels
	 *            output labels
	 * @param p1
	 *            implicit curve
	 * @param c1
	 *            conic
	 * @return intersection points
	 */
	final public GeoPoint[] intersectImplicitpolyConic(String[] labels,
			GeoImplicit p1, GeoConic c1) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, c1);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * @param labels
	 *            output labels
	 * @param curve
	 *            implicit curve
	 * @param line
	 *            line
	 * @return intersection points
	 */
	final public GeoPoint[] intersectImplicitCurveLine(String[] labels,
			GeoImplicitCurve curve, GeoLine line) {
		AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric(
				cons, curve, line);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * @param labels
	 *            output labels
	 * @param curve
	 *            implicit curve
	 * @param conic
	 *            conic
	 * @return intersections
	 */
	final public GeoPoint[] intersectImplicitCurveConic(String[] labels,
			GeoImplicitCurve curve, GeoConic conic) {
		AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons,
				curve, conic);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * @param labels
	 *            output label
	 * @param curve
	 *            curve
	 * @param func
	 *            function
	 * @return intersections
	 */
	final public GeoPoint[] intersectImplicitCurveFunction(String[] labels,
			GeoImplicitCurve curve, GeoFunction func) {
		AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric(
				cons, curve, func);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * @param labels
	 *            output labels
	 * @param curve1
	 *            implicit curve
	 * @param curve2
	 *            implicit curve
	 * @return intersection points
	 */
	final public GeoPoint[] intersectImplicitCurveImpCurve(String[] labels,
			GeoImplicit curve1, GeoImplicit curve2) {
		AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons,
				curve1, curve2);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}

	/**
	 * @param labels
	 *            output labels
	 * @param surf
	 *            implicit surface
	 * @param line
	 *            line
	 * @return intersection of surface and line
	 */
	public GeoElement[] intersectImplicitSurfaceLine(String[] labels,
			GeoImplicitSurfaceND surf, GeoElementND line) {
		return new GeoPoint[0];
	}

	/**
	 * intersect path with point
	 * 
	 * @param label
	 *            output label
	 * @param path
	 *            path
	 * @param point
	 *            point
	 * @return point if it's on path, undefined otherwise
	 */
	public GeoElement[] intersectPathPoint(String label, Path path,
			GeoPointND point) {
		AlgoIntersectPathPoint algo = new AlgoIntersectPathPoint(cons, label,
				path, point);
		GeoElement[] p = new GeoElement[1];
		p[0] = algo.getP().toGeoElement();
		return p;
	}

	/**
	 * @param f
	 *            function
	 * @param c
	 *            conic
	 * @return intersection point
	 */
	public AlgoIntersectPolynomialConic getIntersectionAlgorithm(GeoFunction f,
			GeoConic c) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(f, c);

		if (existingAlgo instanceof AlgoIntersectPolynomialConic) {
			return (AlgoIntersectPolynomialConic) existingAlgo;
		}

		if (existingAlgo != null) {
			Log.debug("unexpected class returned: " + existingAlgo.getClass()
					+ " " + existingAlgo.getClassName() + " "
					+ existingAlgo.toString(StringTemplate.defaultTemplate));
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomialConic algo = new AlgoIntersectPolynomialConic(
				cons, f, c);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param g
	 *            line
	 * @param c
	 *            conic
	 * @return intersection algo
	 */
	public AlgoIntersectLineConic getIntersectionAlgorithm(GeoLine g,
			GeoConic c) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(g, c);
		if (existingAlgo != null) {
			return (AlgoIntersectLineConic) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineConic algo = new AlgoIntersectLineConic(cons, g, c);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	// intersect polyLine and Conic
	private AlgoIntersectPolyLineConic getIntersectionAlgorithm(GeoPolyLine g,
			GeoConic c) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(g, c);
		if (existingAlgo != null) {
			return (AlgoIntersectPolyLineConic) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolyLineConic algo = new AlgoIntersectPolyLineConic(cons,
				c, g, false);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param g
	 *            polygon
	 * @param c
	 *            conic
	 * @return intersection algo
	 */
	public AlgoIntersectPolyLineConic getIntersectionAlgorithm(GeoPolygon g,
			GeoConic c) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(g, c);
		if (existingAlgo != null) {
			return (AlgoIntersectPolyLineConic) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolyLineConic algo = new AlgoIntersectPolyLineConic(cons,
				c, g, true);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param a
	 *            first conic
	 * @param b
	 *            second conic
	 * @return intersection algo
	 */
	public AlgoIntersectConics getIntersectionAlgorithm(GeoConic a,
			GeoConic b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null) {
			return (AlgoIntersectConics) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectConics algo = new AlgoIntersectConics(cons, a, b);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param a
	 *            first function
	 * @param b
	 *            second function
	 * @return intersection algo
	 */
	public AlgoIntersectPolynomials getIntersectionAlgorithm(GeoFunctionable a,
			GeoFunctionable b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null) {
			return (AlgoIntersectPolynomials) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomials algo = new AlgoIntersectPolynomials(cons, a,
				b);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param a
	 *            function
	 * @param l
	 *            line
	 * @return intersection algo
	 */
	public AlgoIntersectPolynomialLine getIntersectionAlgorithm(
			GeoFunctionable a, GeoLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, l);
		if (existingAlgo != null) {
			return (AlgoIntersectPolynomialLine) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomialLine algo = new AlgoIntersectPolynomialLine(cons,
				a, l);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param p
	 *            implicit curve
	 * @param l
	 *            line
	 * @return intersection algo
	 */
	public AlgoIntersectImplicitpolyParametric getIntersectionAlgorithm(
			GeoImplicit p, GeoLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, l);
		if (existingAlgo != null) {
			return (AlgoIntersectImplicitpolyParametric) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric(
				cons, p, l);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	private AlgoIntersectImplicitpolyPolyLine getIntersectionAlgorithm(
			GeoImplicit p, GeoPolyLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, l);
		if (existingAlgo != null) {
			return (AlgoIntersectImplicitpolyPolyLine) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolyPolyLine algo = new AlgoIntersectImplicitpolyPolyLine(
				cons, p, l, false);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param p
	 *            implicit curve
	 * @param l
	 *            polygon
	 * @return intersection algo
	 */
	public AlgoIntersectImplicitpolyPolyLine getIntersectionAlgorithm(
			GeoImplicit p, GeoPolygon l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, l);
		if (existingAlgo != null) {
			return (AlgoIntersectImplicitpolyPolyLine) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolyPolyLine algo = new AlgoIntersectImplicitpolyPolyLine(
				cons, p, l, true);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param p
	 *            implicit curve
	 * @param f
	 *            function
	 * @return intersection algo
	 */
	public AlgoIntersectImplicitpolyParametric getIntersectionAlgorithm(
			GeoImplicit p, GeoFunctionable f) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, f);
		if (existingAlgo != null) {
			return (AlgoIntersectImplicitpolyParametric) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric(
				cons, p, f);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param p1
	 *            implicit curve
	 * @param p2
	 *            implicit curve
	 * @return intersection algo
	 */
	public AlgoIntersectImplicitpolys getIntersectionAlgorithm(GeoImplicit p1,
			GeoImplicit p2) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p1, p2);
		if (existingAlgo != null) {
			return (AlgoIntersectImplicitpolys) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons,
				p1, p2);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param p1
	 *            implicit curve
	 * @param c1
	 *            conic
	 * @return intersect algo
	 */
	public AlgoIntersectImplicitpolys getIntersectionAlgorithm(GeoImplicit p1,
			GeoConic c1) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p1, c1);
		if (existingAlgo != null) {
			return (AlgoIntersectImplicitpolys) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons,
				p1, c1);
		algo.setPrintedInXML(false);
		addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * @param a
	 *            first arg of Intersect
	 * @param b
	 *            second arg of Intersect
	 * @return cached intersection geo
	 */
	public AlgoElement findExistingIntersectionAlgorithm(GeoElementND a,
			GeoElementND b) {
		if (!isIntersectCacheEnabled) {
			return null;
		}
		int size = intersectionAlgos.size();
		AlgoElement algo;
		for (int i = 0; i < size; i++) {
			algo = intersectionAlgos.get(i);
			GeoElement[] input = algo.getInput();
			if (a == input[0] && b == input[1]
					|| a == input[1] && b == input[0]) {
				// we found an existing intersection algorithm
				return algo;
			}
		}
		return null;
	}

	/**
	 * tangents to c through P
	 * 
	 * @param labels
	 *            output labels
	 * @param P
	 *            point
	 * @param c
	 *            conic
	 * @return tangents
	 */
	final public GeoElement[] tangent(String[] labels, GeoPointND P,
			GeoConicND c) {
		AlgoTangentPoint algo = new AlgoTangentPoint(cons, labels, P, c);
		return algo.getOutput();
	}

	/**
	 * common tangents to c1 and c2
	 * 
	 * @author dsun48 [6/26/2011]
	 * @param labels
	 *            output labels
	 * @param c1
	 *            conic
	 * @param c2
	 *            conic
	 * @return common tangents
	 */
	final public GeoElement[] commonTangents(String[] labels, GeoConicND c1,
			GeoConicND c2) {
		AlgoCommonTangents algo = new AlgoCommonTangents(cons, labels, c1, c2);
		return algo.getOutput();
	}

	/**
	 * tangents to c parallel to g
	 * 
	 * @param labels
	 *            output labels
	 * @param g
	 *            line
	 * @param c
	 *            conic
	 * @return tangents
	 */
	final public GeoElement[] tangent(String[] labels, GeoLineND g,
			GeoConicND c) {
		AlgoTangentLine algo = new AlgoTangentLine(cons, labels, g, c);
		return algo.getOutput();
	}

	/**
	 * tangent to f in x = x(P)
	 * 
	 * @param label
	 *            output label
	 * @param P
	 *            point on function
	 * @param f
	 *            function
	 * @return tangent
	 */
	final public GeoLine tangent(String label, GeoPointND P,
			GeoFunctionable f) {
		return KernelCAS.tangent(cons, label, P, f);
	}

	/**
	 * tangents to p through P
	 * 
	 * @param labels
	 *            output labels
	 * @param R
	 *            point
	 * @param p
	 *            implicit curve
	 * @return tangents
	 */
	final public GeoLine[] tangent(String[] labels, GeoPointND R,
			GeoImplicit p) {
		AlgoTangentImplicitpoly algo = new AlgoTangentImplicitpoly(cons, labels,
				p, R);
		algo.setLabels(labels);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}

	/**
	 * tangents to p parallel to g
	 * 
	 * @param labels
	 *            output labels
	 * @param g
	 *            parallel line
	 * @param p
	 *            implicit curve
	 * @return tangents
	 **/
	final public GeoLine[] tangent(String[] labels, GeoLineND g, GeoImplicit p) {
		AlgoTangentImplicitpoly algo = new AlgoTangentImplicitpoly(cons,
				labels, p, g);
		algo.setLabels(labels);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}

	/********************************************************************
	 * TRANSFORMATIONS
	 ********************************************************************/

	/**
	 * translate geoTrans by vector v
	 * 
	 * @param label
	 *            output label
	 * @param geoTrans
	 *            original object
	 * @param v
	 *            vector
	 * @return translated object
	 */
	final public GeoElement[] translate(String label, GeoElementND geoTrans,
			GeoVec3D v) {
		Transform t = new TransformTranslate(cons, v);
		return t.transform(geoTrans, label);
	}

	/**
	 * @param label
	 *            output label
	 * @param geoTrans
	 *            original object
	 * @param v
	 *            vector
	 * @return translated object
	 */
	public GeoElement[] translateND(String label, GeoElementND geoTrans,
			GeoVectorND v) {
		return translate(label, geoTrans, (GeoVec3D) v);
	}

	/**
	 * mirror geoMir at point Q
	 * 
	 * @param label
	 *            output label
	 * @param geoMir
	 *            original object
	 * @param Q
	 *            center
	 * @return mirrored object
	 */
	final public GeoElement[] mirror(String label, GeoElement geoMir,
			GeoPoint Q) {
		Transform t = new TransformMirror(cons, Q);
		return t.transform(geoMir, label);
	}

	/**
	 * mirror (invert) element Q in circle
	 * 
	 * @author Michael Borcherds
	 * @param label
	 *            output label
	 * @param Q
	 *            original object
	 * @param conic
	 *            circle (result undefined for other conics)
	 * @return mirrored object
	 */
	final public GeoElement[] mirror(String label, GeoElement Q,
			GeoConic conic) {
		Transform t = new TransformMirror(cons, conic);
		return t.transform(Q, label);
	}

	/**
	 * mirror geoMir at line g
	 * 
	 * @param label
	 *            output label
	 * @param geoMir
	 *            original object
	 * @param g
	 *            line
	 * @return mirrored object
	 */
	final public GeoElement[] mirror(String label, GeoElement geoMir,
			GeoLine g) {
		Transform t = new TransformMirror(cons, g);
		return t.transform(geoMir, label);
	}

	/**
	 * @param point
	 *            point to be attached
	 * @param path
	 *            path
	 * @param view
	 *            view
	 * @param locRW
	 *            real world coords
	 * @return redefined point
	 */
	public GeoPointND attach(GeoPointND point, Path path,
			EuclidianViewInterfaceCommon view, Coords locRW) {

		try {
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			// checkZooming();

			boolean setDefaultColor = false;
			if (((GeoElement) point).getColorFunction() == null) {
				setDefaultColor = ((GeoElement) point).getObjectColor()
						.equals(cons.getConstructionDefaults()
								.getDefaultGeo(
										ConstructionDefaults.DEFAULT_POINT_FREE)
								.getObjectColor());
			}

			GeoPointND newPoint = point(null, path, locRW, false, false,
					point.getToStringMode() != Kernel.COORD_CARTESIAN_3D);

			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			cons.replace((GeoElement) point, (GeoElement) newPoint);
			// clearSelections();

			if (setDefaultColor) {
				newPoint.setObjColor(cons.getConstructionDefaults()
						.getDefaultGeo(
								ConstructionDefaults.DEFAULT_POINT_ON_PATH)
						.getObjectColor());
			}

			return newPoint;
		} catch (Exception e1) {
			Log.error(e1.getMessage());
			return null;
		} catch (Error e2) {
			// eg try to attach dependent point of regular polygon
			Log.error(e2.getMessage());
			return null;
		}
	}

	/**
	 * @param point
	 *            point to be attached
	 * @param region
	 *            region
	 * @param view
	 *            view
	 * @param locRW
	 *            real world coords
	 * @return redefined point
	 */
	public GeoPointND attach(GeoPointND point, Region region,
			EuclidianViewInterfaceCommon view, Coords locRW) {

		try {
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			// checkZooming();

			boolean setDefaultColor = false;
			if (point.getColorFunction() == null) {
				setDefaultColor = point.getObjectColor()
						.equals(cons.getConstructionDefaults()
								.getDefaultGeo(
										ConstructionDefaults.DEFAULT_POINT_FREE)
								.getObjectColor());
			}

			GeoPointND newPoint = pointIn(null, region, locRW, false, false,
					true);

			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			cons.replace((GeoElement) point, (GeoElement) newPoint, null);

			if (setDefaultColor) {
				newPoint.setObjColor(cons.getConstructionDefaults()
						.getDefaultGeo(
								ConstructionDefaults.DEFAULT_POINT_IN_REGION)
						.getObjectColor());
			}

			// clearSelections();
			return newPoint;
		} catch (Exception | MyError e1) {
			Log.debug(e1);
			return null;
		}
	}

	/**
	 * @param p
	 *            point on path or in region
	 * @param view
	 *            view
	 * @return redefined point
	 */
	public GeoPointND detach(GeoPointND p, EuclidianViewInterfaceCommon view) {
		try {
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

			boolean setDefaultColor = false;
			if (((GeoElement) p).getColorFunction() == null) {
				if (p.isPointOnPath()) {
					setDefaultColor = ((GeoElement) p).getObjectColor()
							.equals(cons.getConstructionDefaults()
									.getDefaultGeo(
											ConstructionDefaults.DEFAULT_POINT_ON_PATH)
									.getObjectColor());
				} else if (p.hasRegion()) {
					setDefaultColor = p.getObjectColor()
							.equals(cons.getConstructionDefaults()
									.getDefaultGeo(
											ConstructionDefaults.DEFAULT_POINT_IN_REGION)
									.getObjectColor());
				}
			}

			GeoPointND newPoint = copyFreePoint(p, view);

			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			cons.replace((GeoElement) p, (GeoElement) newPoint);

			if (setDefaultColor) {
				newPoint.setObjColor(cons.getConstructionDefaults()
						.getDefaultGeo(ConstructionDefaults.DEFAULT_POINT_FREE)
						.getObjectColor());
			}

			return newPoint;

		} catch (Exception e1) {
			Log.debug(e1);
			return null;
		}
	}

	protected GeoPointND copyFreePoint(GeoPointND point,
			EuclidianViewInterfaceCommon view) {
		double xOffset = 0, yOffset = 0;
		if (!view.isEuclidianView3D()) {
			xOffset = DETACH_OFFSET * view.getInvXscale();
			yOffset = DETACH_OFFSET * view.getInvYscale();
		}

		return new GeoPoint(cons, null, point.getInhomX() + xOffset,
				point.getInhomY() + yOffset, 1.0);
	}

	/**
	 * detaches a GeoPoint and sets its real world coordinates to (rwX, rwY)
	 * 
	 * @param point
	 *            the GeoPoint to be detached
	 * @return success
	 */
	public boolean detach(GeoPointND point, double d, double e,
			boolean wasOnPath, boolean wasOnRegion) {
		try {
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

			boolean setDefaultColor = false;
			if (((GeoElement) point).getColorFunction() == null) {
				if (wasOnPath) {
					setDefaultColor = ((GeoElement) point).getObjectColor()
							.equals(cons.getConstructionDefaults()
									.getDefaultGeo(
											ConstructionDefaults.DEFAULT_POINT_ON_PATH)
									.getObjectColor());
				} else if (wasOnRegion) {
					setDefaultColor = ((GeoElement) point).getObjectColor()
							.equals(cons.getConstructionDefaults()
									.getDefaultGeo(
											ConstructionDefaults.DEFAULT_POINT_IN_REGION)
									.getObjectColor());
				}
			}

			GeoPoint newPoint = new GeoPoint(cons, null, d, e, 1.0);
			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			cons.replace((GeoElement) point, newPoint);

			if (setDefaultColor) {
				newPoint.setObjColor(cons.getConstructionDefaults()
						.getDefaultGeo(ConstructionDefaults.DEFAULT_POINT_FREE)
						.getObjectColor());
			}
		} catch (Exception e1) {
			Log.debug(e1);
			return false;
		}
		return true;
	}

	/**
	 * one intersection point of polynomial f and line l near to (xRW, yRW)
	 * 
	 * @param label
	 *            output label
	 * @param f
	 *            polynomial function
	 * @param l
	 *            line
	 * @param xRW
	 *            initial x-coord
	 * @param yRW
	 *            initial y-coord
	 * @return intersection point
	 */
	public final GeoPoint intersectPolynomialLineSingle(String label,
			GeoFunctionable f, GeoLine l, double xRW, double yRW) {

		if (!f.getConstruction().isFileLoading()
				&& !f.isPolynomialFunction(false)) {
			return null;
		}

		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 * @param c
	 *            conic
	 * @param x
	 *            close x-coord
	 * @param y
	 *            close y-coord
	 * @return intersection
	 */
	public final GeoPoint intersectPolynomialConicSingle(String label,
			GeoFunction f, GeoConic c, double x, double y) {
		AlgoIntersect algo = getIntersectionAlgorithm(f, c);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 * 
	 * @param label
	 *            output label
	 * @param g
	 *            line
	 * @param c
	 *            conic
	 * @param xRW
	 *            initial x-coord
	 * @param yRW
	 *            initial y-coord
	 * @return intersection point
	 */
	public final GeoPoint intersectLineConicSingle(String label, GeoLine g,
			GeoConic c, double xRW, double yRW) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of a implicitPoly and a line
	 * 
	 * @param label
	 *            output label
	 * @param p
	 *            implicit curve
	 * @param l
	 *            line
	 * @param x
	 *            initial x-coord
	 * @param y
	 *            initial y-coord
	 * @return intersection point
	 */
	final public GeoPoint intersectImplicitpolyLineSingle(String label,
			GeoImplicit p, GeoLine l, double x, double y) {
		AlgoIntersect algo = getIntersectionAlgorithm(p, l);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of a implicitPoly and a line
	 * 
	 * @param label
	 *            output label
	 * @param p
	 *            implicit curve
	 * @param f
	 *            function
	 * @param x
	 *            initial x-coord
	 * @param y
	 *            initial y-coord
	 * @return intersection point
	 */
	final public GeoPoint intersectImplicitpolyPolynomialSingle(String label,
			GeoImplicit p, GeoFunction f, double x, double y) {
		if (!f.getConstruction().isFileLoading()
				&& !f.isPolynomialFunction(false)) {
			return null;
		}
		AlgoIntersect algo = getIntersectionAlgorithm(p, f);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of implicitPolys and conic near given
	 * Point (x,y)
	 * 
	 * @param label
	 *            output label
	 * @param p1
	 *            implicit curve
	 * @param c1
	 *            conic
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @return intersection point
	 */
	final public GeoPoint intersectImplicitpolyConicSingle(String label,
			GeoImplicit p1, GeoConic c1, double x, double y) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, c1);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of two implicitPolys near given Point
	 * (x,y)
	 * 
	 * @param label
	 *            output label
	 * @param p1
	 *            implicit curve
	 * @param p2
	 *            implicit curve
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @return intersection point
	 */
	final public GeoPoint intersectImplicitpolysSingle(String label,
			GeoImplicit p1, GeoImplicit p2, double x, double y) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * @param cons2
	 *            construction
	 * @param path
	 *            parent path
	 * @param point
	 *            close point
	 * @return new algo closest point for path and point
	 */
	public AlgoClosestPoint getNewAlgoClosestPoint(Construction cons2,
			Path path, GeoPointND point) {
		return new AlgoClosestPoint(cons2, path, point);
	}

	/**
	 * @param line1
	 *            line
	 * @param line2
	 *            line
	 * @return angle between lines
	 */
	public GeoAngle createLineAngle(GeoLine line1, GeoLine line2) {
		GeoAngle angle = null;

		// did we get two segments?
		if ((line1 instanceof GeoSegment) && (line2 instanceof GeoSegment)) {
			// check if the segments have one point in common
			GeoSegment a = (GeoSegment) line1;
			GeoSegment b = (GeoSegment) line2;
			// get endpoints
			GeoPoint a1 = a.getStartPoint();
			GeoPoint a2 = a.getEndPoint();
			GeoPoint b1 = b.getStartPoint();
			GeoPoint b2 = b.getEndPoint();

			if (a1 == b1) {
				angle = angle(null, a2, a1, b2);
			} else if (a1 == b2) {
				angle = angle(null, a2, a1, b1);
			} else if (a2 == b1) {
				angle = angle(null, a1, a2, b2);
			} else if (a2 == b2) {
				angle = angle(null, a1, a2, b1);
			}
		}

		if (angle == null) {
			angle = angle(null, line1, line2);
		}

		return angle;
	}

	/**
	 * 
	 * @param polyCons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param p
	 *            parent polygon
	 * @return new AlgoVertexPolygon
	 */
	public AlgoVertexPolygon newAlgoVertexPolygon(Construction polyCons,
			String[] labels, GeoPoly p) {
		return new AlgoVertexPolygon(polyCons, labels, p);
	}

	/**
	 * @param geoPolygon
	 *            Polygon
	 * @return Polygon's Centroid
	 */
	public GeoElement centroid(GeoPolygon geoPolygon) {
		AlgoCentroidPolygon algo = new AlgoCentroidPolygon(cons, geoPolygon);
		GeoPointND centroid = algo.getPoint();
		return (GeoElement) centroid;
	}

	/**
	 * @param labels
	 *            labels
	 * @param c
	 *            conic
	 * @return axes algo
	 */
	public AlgoAxesQuadricND axesConic(GeoQuadricND c, String[] labels) {
		return new AlgoAxes(cons, labels, (GeoConic) c);
	}

	/**
	 * @param label
	 *            label
	 * @param conic
	 *            conic
	 * @return axis algo
	 */
	public AlgoAxis axis(String label, GeoConicND conic, int axisId) {
		return new AlgoAxis(cons, label, conic, axisId);
	}

	/**
	 * polar line to P relative to c
	 *
	 * @param label
	 *            output label
	 * @param P
	 *            point
	 * @param c
	 *            conic
	 * @return polar
	 */
	public GeoElement polarLine(String label, GeoPointND P, GeoConicND c) {
		AlgoPolarLine algo = new AlgoPolarLine(cons, label, c, P);
		return (GeoElement) algo.getLine();
	}

	/**
	 * pole of line relative to c
	 *
	 * @param label
	 *            output label
	 * @param line
	 *            line
	 * @param c
	 *            conic
	 * @return pole line
	 */
	public GeoElement polarPoint(String label, GeoLineND line,
			GeoConicND c) {
		AlgoPolarPoint algo = new AlgoPolarPoint(cons, label, c, line);
		return (GeoElement) algo.getPoint();
	}
}
