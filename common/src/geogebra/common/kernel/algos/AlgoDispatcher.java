package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.KernelCAS;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.TransformDilate;
import geogebra.common.kernel.TransformMirror;
import geogebra.common.kernel.TransformRotate;
import geogebra.common.kernel.TransformTranslate;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.implicit.AlgoImplicitPolyFunction;
import geogebra.common.kernel.implicit.AlgoIntersectImplicitpolyParametric;
import geogebra.common.kernel.implicit.AlgoIntersectImplicitpolys;
import geogebra.common.kernel.implicit.AlgoTangentImplicitpoly;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

import java.util.ArrayList;

public class AlgoDispatcher {
	
	protected Construction cons;
	


	public AlgoDispatcher(Construction cons) {
		this.cons = cons;
	}
	
	/*
	 * to avoid multiple calculations of the intersection points of the same two
	 * objects, we remember all the intersection algorithms created
	 */
	protected ArrayList<AlgoIntersectAbstract> intersectionAlgos = new ArrayList<AlgoIntersectAbstract>();
	public void removeIntersectionAlgorithm(AlgoIntersectAbstract algo) {
		intersectionAlgos.remove(algo);
	}

	public void addIntersectionAlgorithm(AlgoIntersectAbstract algo) {
		intersectionAlgos.add(algo);
	}


	/** Point label with cartesian coordinates (x,y) */
	final public GeoPoint Point(String label, double x, double y,
			boolean complex) {
		GeoPoint p = new GeoPoint(cons);
		p.setCoords(x, y, 1.0);
		if (complex) {
			p.setMode(Kernel.COORD_COMPLEX);
			/*
			 * removed as this sets the mode back to COORD_CARTESIAN
			 * 
			 * // we have to reset the visual style as the constructor // did
			 * not know that this was a complex number
			 * //p.setConstructionDefaults();
			 */
		} else {
			p.setMode(Kernel.COORD_CARTESIAN);
		}
		p.setLabel(label); // invokes add()
		return p;
	}

	/** Vector label with cartesian coordinates (x,y) */
	final public GeoVector Vector(String label, double x, double y) {
		GeoVector v = Vector(x, y);
		v.setLabel(label); // invokes add()
		return v;
	}

	/** Vector label with cartesian coordinates (x,y) */
	final public GeoVector Vector(double x, double y) {
		GeoVector v = new GeoVector(cons);
		v.setCoords(x, y, 0.0);
		v.setMode(Kernel.COORD_CARTESIAN);
		return v;
	}


	/** Point on path with cartesian coordinates (x,y) */
	final public GeoPoint Point(String label, Path path, double x, double y,
			boolean addToConstruction, boolean complex, boolean coords2D) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

		}
		AlgoPointOnPath algo = new AlgoPointOnPath(cons, label, path, x, y);
		GeoPoint p = algo.getP();
		if (complex) {
			p.setMode(Kernel.COORD_COMPLEX);
			p.update();
		}else if (!coords2D){
			p.setCartesian3D();
			p.update();
		}
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}

	/** Point anywhere on path with */
	final public GeoPoint Point(String label, Path path, NumberValue param) {
		// try (0,0)
		AlgoPointOnPath algo = null;
		if (param == null) {
			algo = new AlgoPointOnPath(cons, label, path, 0, 0);
		} else {
			algo = new AlgoPointOnPath(cons, label, path, 0, 0, param);
		}
		GeoPoint p = algo.getP();

		// try (1,0)
		if (!p.isDefined()) {
			p.setCoords(1, 0, 1);
			algo.update();
		}

		// try (random(),0)
		if (!p.isDefined()) {
			p.setCoords(Math.random(), 0, 1);
			algo.update();
		}

		return p;
	}

	/********************
	 * ALGORITHMIC PART *
	 ********************/


	/**
	 * Line named label through Points P and Q
	 */
	final public GeoLine Line(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPoints algo = new AlgoJoinPoints(cons, label, P, Q);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Ray named label through Points P and Q
	 */
	final public GeoRay Ray(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPointsRay algo = new AlgoJoinPointsRay(cons, label, P, Q);
		return algo.getRay();
	}


	/**
	 * Ray named label through Point P with direction of vector v
	 */
	final public GeoRay Ray(String label, GeoPoint P, GeoVector v) {
		AlgoRayPointVector algo = new AlgoRayPointVector(cons, label, P, v);
		return algo.getRay();
	}

	/**
	 * Line named label through Point P parallel to Line l
	 */
	final public GeoLine Line(String label, GeoPoint P, GeoLine l) {
		AlgoLinePointLine algo = new AlgoLinePointLine(cons, label, P, l);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Line named label through Point P orthogonal to vector v
	 */
	final public GeoLine OrthogonalLine(String label, GeoPoint P, GeoVector v) {
		AlgoOrthoLinePointVector algo = new AlgoOrthoLinePointVector(cons,
				label, P, v);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Line named label through Point P orthogonal to line l
	 */
	final public GeoLine OrthogonalLine(String label, GeoPoint P, GeoLine l) {
		AlgoOrthoLinePointLine algo = new AlgoOrthoLinePointLine(cons, label,
				P, l);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Line bisector of points A, B
	 */
	final public GeoLine LineBisector(String label, GeoPoint A, GeoPoint B) {
		AlgoLineBisector algo = new AlgoLineBisector(cons, label, A, B);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Line bisector of segment s
	 */
	final public GeoLine LineBisector(String label, GeoSegment s) {
		AlgoLineBisectorSegment algo = new AlgoLineBisectorSegment(cons, label,
				s);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Angular bisector of points A, B, C
	 */
	final public GeoLine AngularBisector(String label, GeoPoint A,
			GeoPoint B, GeoPoint C) {
		AlgoAngularBisectorPoints algo = new AlgoAngularBisectorPoints(cons,
				label, A, B, C);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Angular bisectors of lines g, h
	 */
	final public GeoLine[] AngularBisector(String[] labels, GeoLine g, GeoLine h) {
		AlgoAngularBisectorLines algo = new AlgoAngularBisectorLines(cons,
				labels, g, h);
		GeoLine[] lines = algo.getLines();
		return lines;
	}

	/**
	 * Vector named label from Point P to Q
	 */
	final public GeoVector Vector(String label, GeoPoint P, GeoPoint Q) {
		AlgoVector algo = new AlgoVector(cons, label, P, Q);
		GeoVector v = (GeoVector) algo.getVector();
		v.setEuclidianVisible(true);
		v.update();
		//notifyUpdate(v);
		return v;
	}

	/**
	 * Vector (0,0) to P
	 */
	final public GeoVectorND Vector(String label, GeoPointND P) {
		GeoVectorND v = createVector(label, P);
		v.setEuclidianVisible(true);
		v.update();
		//notifyUpdate(v);
		return v;
	}
	
	protected GeoVectorND createVector(String label, GeoPointND P){
		AlgoVectorPoint algo = new AlgoVectorPoint(cons, label, P);
		return algo.getVector();
		
	}

	/**
	 * Slope of line g
	 */
	final public GeoNumeric Slope(String label, GeoLine g) {
		AlgoSlope algo = new AlgoSlope(cons, label, g);
		GeoNumeric slope = algo.getSlope();
		return slope;
	}

	/**
	 * LineSegment named label from Point P to Point Q
	 */
	final public GeoSegment Segment(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(cons, label, P,
				Q);
		GeoSegment s = algo.getSegment();
		return s;
	}

	/**
	 * Creates a free list object with the given
	 * 
	 * @param label
	 * @param geoElementList
	 *            list of GeoElement objects
	 * @return
	 */
	final public GeoList List(String label,
			ArrayList<GeoElement> geoElementList, boolean isIndependent) {
		if (isIndependent) {
			GeoList list = new GeoList(cons);
			int size = geoElementList.size();
			for (int i = 0; i < size; i++) {
				list.add(geoElementList.get(i));
			}
			list.setLabel(label);
			return list;
		}
		AlgoDependentList algoList = new AlgoDependentList(cons, label,
				geoElementList);
		return algoList.getGeoList();
	}


	/**
	 * Function dependent on coefficients of arithmetic expressions with
	 * variables, represented by trees. e.g. f(x) = a x��� + b x���
	 */
	final public GeoFunction DependentFunction(String label, Function fun) {
		AlgoDependentFunction algo = new AlgoDependentFunction(cons, label, fun);
		GeoFunction f = algo.getFunction();
		return f;
	}

	public GeoTextField textfield(String label, GeoElement geoElement) {
		AlgoTextfield at = new AlgoTextfield(cons, label, geoElement);
		return at.getResult();
	}

	/**
	 * Line named label through Point P with direction of vector v
	 */
	final public GeoLine Line(String label, GeoPoint P, GeoVector v) {
		AlgoLinePointVector algo = new AlgoLinePointVector(cons, label, P, v);
		GeoLine g = algo.getLine();
		return g;
	}

	/**
	 * Creates new point B with distance n from A and new segment AB The
	 * labels[0] is for the segment, labels[1] for the new point
	 */
	final public GeoElement[] Segment(String[] labels, GeoPoint A,
			NumberValue n) {
		// this is actually a macro
		String pointLabel = null, segmentLabel = null;
		if (labels != null) {
			switch (labels.length) {
			case 2:
				pointLabel = labels[1];

			case 1:
				segmentLabel = labels[0];

			default:
			}
		}

		// create a circle around A with radius n
		AlgoCirclePointRadius algoCircle = new AlgoCirclePointRadius(cons, A, n);
		cons.removeFromConstructionList(algoCircle);
		// place the new point on the circle
		AlgoPointOnPath algoPoint = new AlgoPointOnPath(cons, pointLabel,
				algoCircle.getCircle(), A.inhomX + n.getDouble(), A.inhomY);

		// return segment and new point
		GeoElement[] ret = { Segment(segmentLabel, A, algoPoint.getP()),
				algoPoint.getP() };
		return ret;
	}


	/**
	 * Creates a new point C by rotating B around A using angle alpha and a new
	 * angle BAC (for positive orientation) resp. angle CAB (for negative
	 * orientation). The labels[0] is for the angle, labels[1] for the new point
	 */
	final public GeoElement[] Angle(String[] labels, GeoPoint B, GeoPoint A,
			NumberValue alpha, boolean posOrientation) {
		// this is actually a macro
		String pointLabel = null, angleLabel = null;
		if (labels != null) {
			switch (labels.length) {
			case 2:
				pointLabel = labels[1];

			case 1:
				angleLabel = labels[0];

			default:
			}
		}

		// rotate B around A using angle alpha
		GeoPoint C = (GeoPoint) Rotate(pointLabel, B, alpha, A)[0];

		// create angle according to orientation
		GeoAngle angle;
		if (posOrientation) {
			angle = Angle(angleLabel, B, A, C);
		} else {
			angle = Angle(angleLabel, C, A, B);
		}

		// return angle and new point
		GeoElement[] ret = { angle, C };
		return ret;
	}
	
	/**
	 * rotate geoRot by angle phi around Q
	 */
	final public GeoElement[] Rotate(String label, GeoElement geoRot,
			NumberValue phi, GeoPoint Q) {
		Transform t = new TransformRotate(cons, phi, Q);
		return t.transform(geoRot, label);
	}

	/**
	 * Angle named label between line g and line h
	 */
	final public GeoAngle Angle(String label, GeoLine g, GeoLine h) {
		AlgoAngleLines algo = new AlgoAngleLines(cons, label, g, h);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/**
	 * Angle named label between vector v and vector w
	 */
	final public GeoAngle Angle(String label, GeoVector v, GeoVector w) {
		AlgoAngleVectors algo = new AlgoAngleVectors(cons, label, v, w);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/**
	 * Angle named label between three points
	 */
	final public GeoAngle Angle(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoAnglePoints algo = new AlgoAnglePoints(cons, label, A, B, C);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/**
	 * all angles of given polygon
	 */
	final public GeoElement[] Angles(String[] labels, GeoPolygon poly) {
		AlgoAnglePolygon algo = new AlgoAnglePolygon(cons, labels, poly);
		GeoElement[] angles = algo.getAngles();
		// for (int i=0; i < angles.length; i++) {
		// angles[i].setAlphaValue(0.0f);
		// }
		return angles;
	}

	public GeoNumeric getDefaultNumber(boolean isAngle) {
		return (GeoNumeric) cons.getConstructionDefaults().getDefaultGeo(
				isAngle ? ConstructionDefaults.DEFAULT_ANGLE
						: ConstructionDefaults.DEFAULT_NUMBER);
	}

	/**
	 * circle with through points A, B, C
	 */
	final public GeoConic Circle(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoCircleThreePoints algo = new AlgoCircleThreePoints(cons, label, A,
				B, C);
		GeoConic circle = (GeoConic) algo.getCircle();
		circle.setToSpecific();
		circle.update();
		//notifyUpdate(circle);
		return circle;
	}

	/**
	 * circle arc from three points
	 */
	final public GeoConicPart CircumcircleArc(String label, GeoPoint A,
			GeoPoint B, GeoPoint C) {
		AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons,
				label, A, B, C, GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}

	/**
	 * circle sector from three points
	 */
	final public GeoConicPart CircumcircleSector(String label, GeoPoint A,
			GeoPoint B, GeoPoint C) {
		AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons,
				label, A, B, C, GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}

	/**
	 * circle arc from center and twho points on arc
	 */
	final public GeoConicPart CircleArc(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, label, A, B,
				C, GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}

	/**
	 * circle sector from center and twho points on arc
	 */
	final public GeoConicPart CircleSector(String label, GeoPoint A,
			GeoPoint B, GeoPoint C) {
		AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, label, A, B,
				C, GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}

	/**
	 * Center of conic
	 */
	final public GeoPoint Center(String label, GeoConic c) {
		AlgoCenterConic algo = new AlgoCenterConic(cons, label, c);
		GeoPoint midpoint = algo.getPoint();
		return midpoint;
	}


	/*********************************************
	 * CONIC PART
	 *********************************************/

	/**
	 * circle with midpoint M and radius r
	 */
	final public GeoConic Circle(String label, GeoPoint M, NumberValue r) {
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, M,
				r);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		//notifyUpdate(circle);
		return circle;
	}




	/**
	 * circle with midpoint M and radius segment Michael Borcherds 2008-03-15
	 */
	final public GeoConic Circle(String label, GeoPoint A, GeoSegment segment) {

		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A,
				segment, true);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		//notifyUpdate(circle);
		return circle;
	}

	/**
	 * circle with midpoint M through point P
	 */
	final public GeoConic Circle(String label, GeoPoint M, GeoPoint P) {
		AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, label, M, P);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		//notifyUpdate(circle);
		return circle;
	}

	/**
	 * semicircle with midpoint M through point P
	 */
	final public GeoConicPart Semicircle(String label, GeoPoint M, GeoPoint P) {
		AlgoSemicircle algo = new AlgoSemicircle(cons, label, M, P);
		return algo.getSemicircle();
	}

	/**
	 * parabola with focus F and line l
	 */
	final public GeoConic Parabola(String label, GeoPoint F, GeoLine l) {
		AlgoParabolaPointLine algo = new AlgoParabolaPointLine(cons, label, F,
				l);
		GeoConic parabola = algo.getParabola();
		return parabola;
	}

	/**
	 * ellipse with foci A, B and length of first half axis a
	 */
	final public GeoConic Ellipse(String label, GeoPoint A, GeoPoint B,
			NumberValue a) {
		AlgoEllipseFociLength algo = new AlgoEllipseFociLength(cons, label, A,
				B, a);
		GeoConic ellipse = algo.getConic();
		return ellipse;
	}

	/**
	 * ellipse with foci A, B passing thorugh C Michael Borcherds 2008-04-06
	 */
	final public GeoConic Ellipse(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoEllipseFociPoint algo = new AlgoEllipseFociPoint(cons, label, A, B,
				C);
		GeoConic ellipse = algo.getEllipse();
		return ellipse;
	}

	/**
	 * hyperbola with foci A, B and length of first half axis a
	 */
	final public GeoConic Hyperbola(String label, GeoPoint A, GeoPoint B,
			NumberValue a) {
		AlgoHyperbolaFociLength algo = new AlgoHyperbolaFociLength(cons, label,
				A, B, a);
		GeoConic hyperbola = algo.getConic();
		return hyperbola;
	}

	/**
	 * hyperbola with foci A, B passing thorugh C Michael Borcherds 2008-04-06
	 */
	final public GeoConic Hyperbola(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoHyperbolaFociPoint algo = new AlgoHyperbolaFociPoint(cons, label,
				A, B, C);
		GeoConic hyperbola = algo.getHyperbola();
		return hyperbola;
	}

	/**
	 * conic through five points
	 */
	final public GeoConic Conic(String label, GeoPoint[] points) {
		AlgoConicFivePoints algo = new AlgoConicFivePoints(cons, label, points);
		GeoConic conic = algo.getConic();
		return conic;
	}


	/**
	 * diameter line conjugate to direction of g relative to c
	 */
	final public GeoLine DiameterLine(String label, GeoLine g, GeoConic c) {
		AlgoDiameterLine algo = new AlgoDiameterLine(cons, label, c, g);
		GeoLine diameter = algo.getDiameter();
		return diameter;
	}

	/**
	 * diameter line conjugate to v relative to c
	 */
	final public GeoLine DiameterLine(String label, GeoVector v, GeoConic c) {
		AlgoDiameterVector algo = new AlgoDiameterVector(cons, label, c, v);
		GeoLine diameter = algo.getDiameter();
		return diameter;
	}
	


	/**
	 * Regular polygon with vertices A and B and n total vertices. The labels
	 * name the polygon itself, its segments and points
	 */
	final public GeoElement[] RegularPolygon(String[] labels, GeoPoint A,
			GeoPoint B, NumberValue n) {
		AlgoPolygonRegular algo = new AlgoPolygonRegular(cons, labels, A, B, n);
		return algo.getOutput();
	}
	
	/**
	 * Area named label of conic
	 */
	final public GeoNumeric Area(String label, GeoConicND c) {
		AlgoAreaConic algo = new AlgoAreaConic(cons, label, c);
		GeoNumeric num = algo.getArea();
		return num;
	}

	/**
	 * Perimeter named label of GeoPolygon
	 */
	final public GeoNumeric Perimeter(String label, GeoPolygon polygon) {
		AlgoPerimeterPoly algo = new AlgoPerimeterPoly(cons, label, polygon);
		return algo.getCircumference();
	}

	/**
	 * Circumference named label of GeoConic
	 */
	final public GeoNumeric Circumference(String label, GeoConicND conic) {
		AlgoCircumferenceConic algo = new AlgoCircumferenceConic(cons, label,
				conic);
		return algo.getCircumference();
	}

	/**
	 * dilate geoRot by r from S
	 */
	final public GeoElement[] Dilate(String label, GeoElement geoDil,
			NumberValue r, GeoPoint S) {
		Transform t = new TransformDilate(cons, r, S);
		return t.transform(geoDil, label);
	}


	/**
	 * Distance named label between points P and Q
	 */
	final public GeoNumeric Distance(String label, GeoPointND P, GeoPointND Q) {
		AlgoDistancePoints algo = new AlgoDistancePoints(cons, label, P, Q);
		GeoNumeric num = algo.getDistance();
		return num;
	}

	/**
	 * Distance named label between point P and line g
	 */
	final public GeoNumeric Distance(String label, GeoPointND P, GeoElement g) {
		AlgoDistancePointObject algo = new AlgoDistancePointObject(cons, label,
				P, g);
		GeoNumeric num = algo.getDistance();
		return num;
	}

	final public GeoImplicitPoly ImplicitPoly(String label, GeoFunctionNVar func) {
		AlgoImplicitPolyFunction algo = new AlgoImplicitPolyFunction(cons,
				label, func);
		GeoImplicitPoly implicitPoly = algo.getImplicitPoly();
		return implicitPoly;
	}


	/********************
	 * ALGORITHMIC PART *
	 ********************/


	/** Point in region with cartesian coordinates (x,y) */
	final public GeoPoint PointIn(String label, Region region, double x,
			double y, boolean addToConstruction, boolean complex, boolean coords2D) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

		}
		AlgoPointInRegion algo = new AlgoPointInRegion(cons, label, region, x,
				y);
		// Application.debug("PointIn - \n x="+x+"\n y="+y);
		GeoPoint p = algo.getP();
		if (complex) {
			p.setMode(Kernel.COORD_COMPLEX);
		}else if (!coords2D){
			p.setCartesian3D();
			p.update();
		}
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}


	/**
	 * Midpoint M = (P + Q)/2
	 */
	final public GeoPoint Midpoint(String label, GeoPoint P, GeoPoint Q) {
		AlgoMidpoint algo = new AlgoMidpoint(cons, label, P, Q);
		GeoPoint M = algo.getPoint();
		return M;
	}

	/**
	 * Midpoint of segment
	 */
	final public GeoPoint Midpoint(String label, GeoSegment s) {
		AlgoMidpointSegment algo = new AlgoMidpointSegment(cons, label, s);
		GeoPoint M = algo.getPoint();
		return M;
	}

	/**
	 * Length[list]
	 */
	final public GeoNumeric Length(String label, GeoList list) {
		AlgoListLength algo = new AlgoListLength(cons, label, list);
		return algo.getLength();
	}

	/**
	 * Length[locus]
	 */
	final public GeoNumeric Length(String label, GeoLocus locus) {
		AlgoLengthLocus algo = new AlgoLengthLocus(cons, label, locus);
		return algo.getLength();
	}

	/**
	 * polygon P[0], ..., P[n-1] The labels name the polygon itself and its
	 * segments
	 */
	final public GeoElement[] Polygon(String[] labels, GeoPointND[] P) {
		AlgoPolygon algo = new AlgoPolygon(cons, labels, P);
		return algo.getOutput();
	}

	// G.Sturr 2010-3-14
	/**
	 * Polygon with vertices from geolist Only the polygon is labeled, segments
	 * are not labeled
	 */
	final public GeoElement[] Polygon(String[] labels, GeoList pointList) {
		AlgoPolygon algo = new AlgoPolygon(cons, labels, pointList);
		return algo.getOutput();
	}

	// END G.Sturr

	/**
	 * polygon P[0], ..., P[n-1] The labels name the polygon itself and its
	 * segments
	 */
	final public GeoElement[] PolyLine(String[] labels, GeoPointND[] P, boolean penStroke) {
		AlgoPolyLine algo = new AlgoPolyLine(cons, labels, P, penStroke);
		return algo.getOutput();
	}
	
	/**
	 * Intersect[polygon,polygon] G. Sturr
	 */
	final public GeoElement[] IntersectPolygons(String[] labels,
			GeoPolygon poly0, GeoPolygon poly1) {
		AlgoPolygonIntersection algo = new AlgoPolygonIntersection(cons,
				labels, poly0, poly1);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}

	/**
	 * Union[polygon,polygon] G. Sturr
	 */
	final public GeoElement[] Union(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1) {
		AlgoPolygonUnion algo = new AlgoPolygonUnion(cons, labels, poly0, poly1);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}
	
	/**
	 * locus line for Q dependent on P. Note: P must be a point on a path.
	 */
	final public GeoLocus Locus(String label, GeoPoint Q, GeoPoint P) {
		if (P.getPath() == null || Q.getPath() != null || !P.isParentOf(Q))
			return null;
		if (P.getPath() instanceof GeoList)
			if (((GeoList)P.getPath()).shouldUseAlgoLocusList(true))
				return (new AlgoLocusList(cons, label, Q, P)).getLocus();
		return (new AlgoLocus(cons, label, Q, P)).getLocus();
	}

	/**
	 * locus line for Q dependent on P. Note: P must be a visible slider
	 */
	final public GeoLocus Locus(String label, GeoPoint Q, GeoNumeric P) {
		if (!P.isSlider() || !P.isDefined() || !P.isAnimatable() || // !P.isSliderable()
																	// ||
																	// !P.isDrawable()
																	// ||
				Q.getPath() != null || !P.isParentOf(Q))
			return null;
		AlgoLocusSlider algo = new AlgoLocusSlider(cons, label, Q, P);
		return algo.getLocus();
	}
	
	
	/**
	 * Distance named label between line g and line h
	 */
	public GeoNumeric Distance(String label, GeoLineND g, GeoLineND h) {
		AlgoDistanceLineLine algo = new AlgoDistanceLineLine(cons, label, (GeoLine)g, (GeoLine)h);
		GeoNumeric num = algo.getDistance();
		return num;
	}
	
	/**
	 * IntersectLines yields intersection point named label of lines g, h
	 */
	public GeoPointND IntersectLines(String label, GeoLineND g, GeoLineND h) {
		AlgoIntersectLines algo = new AlgoIntersectLines(cons, label,
				(GeoLine) g, (GeoLine) h);
		GeoPoint S = algo.getPoint();
		return S;
	}
	
	/**
	 * Solves a system of ODEs
	 */
	final public GeoLocus[] NSolveODE(String[] labels, GeoList fun, GeoNumeric startX, 
			 GeoList startY, GeoNumeric endX) {
		   AlgoNSolveODE algo = new AlgoNSolveODE(cons, labels, fun, startX, startY, endX);
		   return algo.getResult();
	}

	/**
	 * yields intersection points named label of line g and polyLine p
	 */
	final public GeoElement[] IntersectLinePolyLine(String[] labels, GeoLine g,
			GeoPolyLine p) {
		AlgoIntersectLinePolyLine algo = new AlgoIntersectLinePolyLine(cons,
				labels, g, p);
		return algo.getOutput();
	}

	/**
	 * yields intersection points named label of line g and polyLine p
	 */
	final public GeoElement[] IntersectLineCurve(String[] labels, GeoLine g,
			GeoCurveCartesian p) {
		AlgoIntersectLineCurve algo = new AlgoIntersectLineCurve(cons,
				labels, g, p);
		return algo.getOutput();
	}




	/**
	 * yields intersection points named label of line g and polygon p (as
	 * boundary)
	 */
	final public GeoElement[] IntersectLinePolygon(String[] labels, GeoLine g,
			GeoPolygon p) {
		AlgoIntersectLinePolyLine algo = new AlgoIntersectLinePolyLine(cons,
				labels, g, p);
		return algo.getOutput();
	}

	/**
	 * Intersects f and g using starting point A (with Newton's root finding)
	 */
	final public GeoPoint IntersectFunctions(String label, GeoFunction f,
			GeoFunction g, GeoPoint A) {
		AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton(
				cons, label, f, g, A);
		GeoPoint S = algo.getIntersectionPoint();
		return S;
	}

	/**
	 * Intersects f and l using starting point A (with Newton's root finding)
	 */
	final public GeoPoint IntersectFunctionLine(String label, GeoFunction f,
			GeoLine l, GeoPoint A) {

		AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(
				cons, label, f, l, A);
		GeoPoint S = algo.getIntersectionPoint();
		return S;
	}


	/**
	 * IntersectLineConic yields intersection points named label1, label2 of
	 * line g and conic c
	 */
	final public GeoPoint[] IntersectLineConic(String[] labels, GeoLine g,
			GeoConic c) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		GeoElement.setLabels(labels, points);
		return points;
	}

	/**
	 * IntersectConics yields intersection points named label1, label2, label3,
	 * label4 of conics c1, c2
	 */
	public GeoPointND[] IntersectConics(String[] labels, GeoConicND a,
			GeoConicND b) {
		AlgoIntersectConics algo = getIntersectionAlgorithm((GeoConic) a,
				(GeoConic) b);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		GeoElement.setLabels(labels, points);
		return points;
	}

	/**
	 * IntersectPolynomials yields all intersection points of polynomials a, b
	 */
	final public GeoPoint[] IntersectPolynomials(String[] labels,
			GeoFunction a, GeoFunction b) {

		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) {

			// dummy point
			GeoPoint A = new GeoPoint(cons);
			A.setZero();
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

	/**
	 * get only one intersection point of two polynomials a, b that is near to
	 * the given location (xRW, yRW)
	 */
	final public GeoPoint IntersectPolynomialsSingle(String label,
			GeoFunction a, GeoFunction b, double xRW, double yRW) {
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false))
			return null;

		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}


	/**
	 * IntersectPolyomialLine yields all intersection points of polynomial f and
	 * line l
	 */
	final public GeoPoint[] IntersectPolynomialLine(String[] labels,
			GeoFunction f, GeoLine l) {

		if (!f.isPolynomialFunction(false)) {

			// dummy point
			GeoPoint A = new GeoPoint(cons);
			A.setZero();
			// we must check that getLabels() didn't return null
			String label = labels == null ? null : labels[0];
			AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(
					cons, label, f, l, A);
			GeoPoint[] ret = { algo.getIntersectionPoint() };
			return ret;

		}

		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
		algo.setPrintedInXML(true);
		algo.setLabels(labels);
		GeoPoint[] points = algo.getIntersectionPoints();
		return points;
	}






	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final public GeoPoint IntersectConicsSingle(String label, GeoConic a,
			GeoConic b, double xRW, double yRW) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get intersection points of a polynomial and a conic
	 */
	final public GeoPoint[] IntersectPolynomialConic(String[] labels,
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
	 */
	final public GeoPoint[] IntersectImplicitpolyLine(String[] labels,
			GeoImplicitPoly p, GeoLine l) {
		AlgoIntersectImplicitpolyParametric algo = getIntersectionAlgorithm(p,
				l);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}



	/**
	 * get intersection points of a implicitPoly and a polynomial
	 */
	final public GeoPoint[] IntersectImplicitpolyPolynomial(String[] labels,
			GeoImplicitPoly p, GeoFunction f) {
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
	 */
	final public GeoPoint[] IntersectImplicitpolys(String[] labels,
			GeoImplicitPoly p1, GeoImplicitPoly p2) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}





	/**
	 * get intersection points of implicitPoly and conic
	 */
	final public GeoPoint[] IntersectImplicitpolyConic(String[] labels,
			GeoImplicitPoly p1, GeoConic c1) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, c1);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return points;
	}



	// intersect polynomial and conic
	public AlgoIntersectPolynomialConic getIntersectionAlgorithm(GeoFunction f,
			GeoConic c) {

		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(f, c);
		if (existingAlgo != null)
			return (AlgoIntersectPolynomialConic) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomialConic algo = new AlgoIntersectPolynomialConic(
				cons, f, c);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	}

	// intersect line and conic
	public AlgoIntersectLineConic getIntersectionAlgorithm(GeoLine g, GeoConic c) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(g, c);
		if (existingAlgo != null)
			return (AlgoIntersectLineConic) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineConic algo = new AlgoIntersectLineConic(cons, g, c);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	}

	// intersect conics
	public AlgoIntersectConics getIntersectionAlgorithm(GeoConic a, GeoConic b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null)
			return (AlgoIntersectConics) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectConics algo = new AlgoIntersectConics(cons, a, b);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	}

	// intersection of polynomials
	public AlgoIntersectPolynomials getIntersectionAlgorithm(GeoFunction a,
			GeoFunction b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null)
			return (AlgoIntersectPolynomials) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomials algo = new AlgoIntersectPolynomials(cons, a, b);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	}

	// intersection of polynomials
	public AlgoIntersectPolynomialLine getIntersectionAlgorithm(GeoFunction a,
			GeoLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, l);
		if (existingAlgo != null)
			return (AlgoIntersectPolynomialLine) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomialLine algo = new AlgoIntersectPolynomialLine(
				cons, a, l);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	}

	// intersection of GeoImplicitPoly, GeoLine
	public AlgoIntersectImplicitpolyParametric getIntersectionAlgorithm(
			GeoImplicitPoly p, GeoLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, l);
		if (existingAlgo != null)
			return (AlgoIntersectImplicitpolyParametric) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric(
				cons, p, l);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	}

	// intersection of GeoImplicitPoly, polynomial
	public AlgoIntersectImplicitpolyParametric getIntersectionAlgorithm(
			GeoImplicitPoly p, GeoFunction f) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, f);
		if (existingAlgo != null)
			return (AlgoIntersectImplicitpolyParametric) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric(
				cons, p, f);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	}

	// intersection of two GeoImplicitPoly
	public AlgoIntersectImplicitpolys getIntersectionAlgorithm(GeoImplicitPoly p1,
			GeoImplicitPoly p2) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p1, p2);
		if (existingAlgo != null)
			return (AlgoIntersectImplicitpolys) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons,
				p1, p2);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	}

	public AlgoIntersectImplicitpolys getIntersectionAlgorithm(GeoImplicitPoly p1,
			GeoConic c1) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p1, c1);
		if (existingAlgo != null)
			return (AlgoIntersectImplicitpolys) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons,
				p1, c1);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	}

	public AlgoElement findExistingIntersectionAlgorithm(GeoElement a,
			GeoElement b) {
		int size = intersectionAlgos.size();
		AlgoElement algo;
		for (int i = 0; i < size; i++) {
			algo = intersectionAlgos.get(i);
			GeoElement[] input = algo.getInput();
			if (a == input[0] && b == input[1] || a == input[1]
					&& b == input[0])
				// we found an existing intersection algorithm
				return algo;
		}
		return null;
	}

	/**
	 * tangents to c through P
	 */
	final public GeoLine[] Tangent(String[] labels, GeoPoint P, GeoConic c) {
		AlgoTangentPoint algo = new AlgoTangentPoint(cons, labels, P, c);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}

	/**
	 * common tangents to c1 and c2 dsun48 [6/26/2011]
	 */
	final public GeoLine[] CommonTangents(String[] labels, GeoConic c1,
			GeoConic c2) {
		AlgoCommonTangents algo = new AlgoCommonTangents(cons, labels, c1, c2);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}

	/**
	 * tangents to c parallel to g
	 */
	final public GeoLine[] Tangent(String[] labels, GeoLine g, GeoConic c) {
		AlgoTangentLine algo = new AlgoTangentLine(cons, labels, g, c);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}


	/**
	 * tangent to f in x = x(P)
	 */
	final public GeoLine Tangent(String label, GeoPoint P, GeoFunction f) {
		
		return KernelCAS.Tangent(cons, label, P, f);
	}

	/**
	 * tangents to p through P
	 */
	final public GeoLine[] Tangent(String[] labels, GeoPoint R,
			GeoImplicitPoly p) {
		AlgoTangentImplicitpoly algo = new AlgoTangentImplicitpoly(cons,
				labels, p, R);
		algo.setLabels(labels);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}

	/**
	 * tangents to p parallel to g
	 */
	final public GeoLine[] Tangent(String[] labels, GeoLine g, GeoImplicitPoly p) {
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
	 */
	final public GeoElement[] Translate(String label, GeoElement geoTrans,
			GeoVec3D v) {
		Transform t = new TransformTranslate(cons, v);
		return t.transform(geoTrans, label);
	}

	/**
	 * mirror geoMir at point Q
	 */
	final public GeoElement[] Mirror(String label, GeoElement geoMir,
			GeoPoint Q) {
		Transform t = new TransformMirror(cons, Q);
		return t.transform(geoMir, label);
	}

	/**
	 * mirror (invert) element Q in circle Michael Borcherds 2008-02-10
	 */
	final public GeoElement[] Mirror(String label, GeoElement Q, GeoConic conic) {
		Transform t = new TransformMirror(cons, conic);
		return t.transform(Q, label);
	}




	/**
	 * mirror geoMir at line g
	 */
	final public GeoElement[] Mirror(String label, GeoElement geoMir, GeoLine g) {
		Transform t = new TransformMirror(cons, g);
		return t.transform(geoMir, label);

	}
	
	public boolean attach(GeoPointND p, Path path, EuclidianViewInterfaceCommon view, double mx, double my) {
		
		GeoPoint point = (GeoPoint) p;
	
		try {
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			//checkZooming(); 
			
			GeoPoint newPoint = Point(null, path,
					view.toRealWorldCoordX(mx), view.toRealWorldCoordY(my),
					false, false, p.getMode()!=Kernel.COORD_CARTESIAN_3D);
			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			cons.replace(point, newPoint);
			//clearSelections();
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}

	public boolean attach(GeoPointND p, Region region, EuclidianViewInterfaceCommon view, double mx, double my) {
	
		GeoPoint point = (GeoPoint) p;
		
		try {
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			//checkZooming(); 
			
			GeoPoint newPoint = PointIn(null, region,
					view.toRealWorldCoordX(mx), view.toRealWorldCoordY(my),
					false, false, true);
			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			cons.replace(point, newPoint);
			//clearSelections();
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	public boolean detach(GeoPointND point, EuclidianViewInterfaceCommon view) {
		
		GeoPoint p = (GeoPoint) point;
		
		//getSelectedPoints();
		//getSelectedRegions();
		//getSelectedPaths();

		// move point (20,20) pixels when detached
		double x = view.toScreenCoordX(p.inhomX) + 20;
		double y = view.toScreenCoordY(p.inhomY) + 20;

		try {
			boolean oldLabelCreationFlag = cons
					.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			//checkZooming(); 
			
			GeoPoint newPoint = new GeoPoint(
					cons, null,
					view.toRealWorldCoordX(x),
					view.toRealWorldCoordY(y), 1.0);
			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			cons.replace(p, newPoint);
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		//clearSelections();
		return true;
	}
	
	/**
	 * one intersection point of polynomial f and line l near to (xRW, yRW)
	 */
	public final GeoPoint IntersectPolynomialLineSingle(String label,
			GeoFunction f, GeoLine l, double xRW, double yRW) {

		if (!f.isPolynomialFunction(false))
			return null;

		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	public final GeoPoint IntersectPolynomialConicSingle(String label,
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
	 */
	public final GeoPoint IntersectLineConicSingle(String label, GeoLine g,
			GeoConic c, double xRW, double yRW) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}


	/**
	 * get single intersection points of a implicitPoly and a line
	 */
	final public GeoPoint IntersectImplicitpolyLineSingle(String label,
			GeoImplicitPoly p, GeoLine l, double x, double y) {
		AlgoIntersect algo = getIntersectionAlgorithm(p, l);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of a implicitPoly and a line
	 */
	final public GeoPoint IntersectImplicitpolyPolynomialSingle(String label,
			GeoImplicitPoly p, GeoFunction f, double x, double y) {
		if (!f.isPolynomialFunction(false))
			return null;
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
	 * @param x
	 * @param y
	 */
	final public GeoPoint IntersectImplicitpolyConicSingle(String label,
			GeoImplicitPoly p1, GeoConic c1, double x, double y) {
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
	 * @param x
	 * @param y
	 */
	final public GeoPoint IntersectImplicitpolysSingle(String label,
			GeoImplicitPoly p1, GeoImplicitPoly p2, double x, double y) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * 
	 * @param cons2
	 * @param path
	 * @param point
	 * @return new algo closest point for path and point
	 */
	public AlgoClosestPoint getNewAlgoClosestPoint(Construction cons2, Path path,
			GeoPointND point) {
		return new AlgoClosestPoint(cons2, path, point);
	}

}
