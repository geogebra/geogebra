/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Relation.java
 *
 * Created on 12. Dezember 2001, 12:37
 */

package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoIntersectConics;
import geogebra.common.kernel.algos.AlgoIntersectLineConic;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.main.App;
import geogebra.common.main.Localization;

/**
 * 
 * @author Markus
 */
public class Relation {

	private App app;
	private Localization loc;
	private Construction cons;

	/**
	 * Creates new relation
	 * @param kernel kernel
	 */
	public Relation(Kernel kernel) {
		app = kernel.getApplication();
		loc = app.getLocalization();
		cons = kernel.getConstruction();
	}

	/**
	 * description of the relation between two GeoElements a, b (equal,
	 * incident, intersect, parallel, linear dependent, tangent of, ...)
	 * @param a first geo
	 * @param b second geo
	 * @return string describing relation between these two
	 */
	final public String relation(GeoElement a, GeoElement b) {
		// check defined state
		if (!a.isDefined()) {
			return getPlainNumerical("AisNotDefined", a.getNameDescription());
		} else if (!b.isDefined()) {
			return getPlainNumerical("AisNotDefined", b.getNameDescription());
		}

		// decide what relation method can be used

		// point, point
		if (a instanceof GeoPoint && b instanceof GeoPoint)
			return relation((GeoPoint) a, (GeoPoint) b);
		else if (a instanceof GeoVector && b instanceof GeoVector)
			return relation((GeoVector) a, (GeoVector) b);
		else if (a instanceof GeoSegment && b instanceof GeoSegment)
			return relation((GeoSegment) a, (GeoSegment) b);
		else if (a instanceof GeoLine && b instanceof GeoLine)
			return relation((GeoLine) a, (GeoLine) b);
		else if (a instanceof GeoConicPart && b instanceof GeoConicPart)
			return relation((GeoConicPart) a, (GeoConicPart) b);
		else if (a instanceof GeoConic && b instanceof GeoConic)
			return relation((GeoConic) a, (GeoConic) b);
		else if (a instanceof GeoFunction && b instanceof GeoFunction)
			return relation((GeoFunction) a, (GeoFunction) b);

		else if (a instanceof GeoPoint && b instanceof GeoPolygon)
			return relation((GeoPoint) a, (GeoPolygon) b);
		else if (a instanceof GeoPolygon && b instanceof GeoPoint)
			return relation((GeoPoint) b, (GeoPolygon) a);

		else if (a instanceof GeoPoint && b instanceof Path)
			return relation((GeoPoint) a, (Path) b);
		else if (a instanceof Path && b instanceof GeoPoint)
			return relation((GeoPoint) b, (Path) a);

		else if (a instanceof GeoConic && b instanceof GeoLine)
			return relation((GeoLine) b, (GeoConic) a);
		else if (a instanceof GeoLine && b instanceof GeoConic)
			return relation((GeoLine) a, (GeoConic) b);

		else if (a instanceof NumberValue && b instanceof NumberValue)
			return relation((NumberValue) a, (NumberValue) b);
		else if (a instanceof GeoList && b instanceof GeoList)
			return relation((GeoList) a, (GeoList) b);
		else {
			return getPlainNumerical("AandBcannotBeCompared",
					a.getNameDescription(), b.getNameDescription());
		}
	}

	/**
	 * description of the relation between two lists a, b (equal, unequal)
	 */
	final private String relation(GeoList a, GeoList b) {
		String str = equalityStringNumerical(a.toGeoElement(), b.toGeoElement(),
				a.isEqual(b));
		return str;
	}

	/**
	 * description of the relation between two numbers a, b (equal, unequal)
	 */
	final private String relation(NumberValue a, NumberValue b) {
		String str = equalityStringNumerical(a.toGeoElement(),
				b.toGeoElement(),
				Kernel.isEqual(a.getDouble(), b.getDouble()));
		return str;
	}

	/**
	 * description of the relation between segment a and segment b (equal,
	 * unequal)
	 */
	final private String relation(GeoSegment a, GeoSegment b) {
		StringBuilder sb = new StringBuilder();
		sb.append(equalityStringNumerical(a, b, a.isEqual(b)));
		sb.append("\n");
		// sb.append(getPlain("Length"));
		// sb.append(": ");
		// sb.append(relation((NumberValue) a, (NumberValue) b));
		if (Kernel.isEqual(((NumberValue) a).getDouble(),
				((NumberValue) b).getDouble()))
			sb.append(getPlainNumerical("AhasTheSameLengthAsB",
					a.getNameDescription(), b.getNameDescription()));
		else
			sb.append(getPlainNumerical("AdoesNothaveTheSameLengthAsB",
					a.getNameDescription(), b.getNameDescription()));
		return sb.toString();
	}

	/**
	 * description of the relation between two points A, B (equal, unequal)
	 */
	final private String relation(GeoPoint A, GeoPoint B) {
		String str = equalityStringNumerical(A, B, A.isEqual(B));
		return str;
	}

	/**
	 * description of the relation between two vectors a, b (equal, linear
	 * dependent, linear independent)
	 */
	final private String relation(GeoVector a, GeoVector b) {
		String str;
		if (a.isEqual(b)) {
			str = equalityStringNumerical(a, b, true);
		} else {
			str = linDependencyString(a, b, a.linDep(b));
		}
		return str;
	}

	/**
	 * description of the relation between point A and a polygon ((not) on
	 * perimeter)
	 */
	final private String relation(GeoPoint A, GeoPolygon p) {
		return incidencePerimeterString(A, p.toGeoElement(),
				p.isOnPath(A, Kernel.STANDARD_PRECISION));
	}

	/**
	 * description of the relation between point A and a path (incident, not
	 * incident)
	 */
	final private String relation(GeoPoint A, Path path) {
		return incidenceString(A, path.toGeoElement(),
				path.isOnPath(A, Kernel.STANDARD_PRECISION));
	}

	/**
	 * description of the relation between lines g and h (equal, parallel or
	 * intersecting)
	 */
	final private String relation(GeoLine g, GeoLine h) {
		String str;
		// check for equality
		if (g.isEqual(h)) {
			str = equalityStringNumerical(g, h, true);
		} else {
			if (g.isParallel(h))
				str = parallelString(g, h);
			else if (g.isPerpendicular(h))
				str = perpendicularString(g, h);
			else {
				// check if intersection point really lies on both objects (e.g.
				// segments)
				GeoPoint tempPoint = new GeoPoint(g.cons);
				GeoVec3D.cross(g, h, tempPoint);
				boolean isIntersection = g.isIntersectionPointIncident(
						tempPoint, Kernel.STANDARD_PRECISION)
						&& h.isIntersectionPointIncident(tempPoint,
								Kernel.STANDARD_PRECISION);

				str = intersectString(g, h, isIntersection);
			}
		}
		return str;
	}

	/**
	 * description of the relation between line g and conic c (intersection
	 * type: tangent, secant, ...)
	 */
	final private String relation(GeoLine g, GeoConic c) {
		int type;
		String str;

		// limited paths have to handled differently (e.g. segments, arcs)
		if (g.isLimitedPath() || c.isLimitedPath()) {
			// intersect line and conic
			// precision setting is not needed here (done by algorithm)
			AlgoIntersectLineConic algo = new AlgoIntersectLineConic(cons, g, c);
			GeoPoint[] points = algo.getIntersectionPoints();
			cons.removeFromConstructionList(algo);

			// check for defined intersection points
			boolean intersect = false;
			for (int i = 0; i < points.length; i++) {
				if (points[i].isDefined()) {
					intersect = true;
					break;
				}
			}
			// build relation string
			str = intersectString(g, c, intersect);

			// remove algorithm by removing one of its points
			points[0].remove();
			return str;
		}

		// is line defined as tangent or asymptote of c?
		if (g.isDefinedTangent(c)) {
			str = lineConicString(g, c,
					AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE);
		} else if (g.isDefinedAsymptote(c)) {
			str = lineConicString(g, c,
					AlgoIntersectLineConic.INTERSECTION_ASYMPTOTIC_LINE);
		} else {
			// intersect line and conic
			GeoPoint[] points = { new GeoPoint(cons), new GeoPoint(cons) };
			type = AlgoIntersectLineConic.intersectLineConic(g, c, points, Kernel.STANDARD_PRECISION);
			str = lineConicString(g, c, type);
		}
		return str;
	}

	/**
	 * description of the relation between conci parts a, b (equal, intersecting
	 * or not intersecting)
	 */
	final private String relation(GeoConicPart a, GeoConicPart b) {
		StringBuilder sb = new StringBuilder();
		sb.append(equalityStringNumerical(a, b, a.isEqual(b)));

		int type = a.getConicPartType();
		if (type == b.getConicPartType()) {
			sb.append("\n");
			if (type == GeoConicNDConstants.CONIC_PART_ARC) {
				if (Kernel.isEqual(((NumberValue) a).getDouble(),
						((NumberValue) b).getDouble()))
					sb.append(getPlainNumerical("AhasTheSameLengthAsB",
							a.getNameDescription(), b.getNameDescription()));
				else
					sb.append(getPlainNumerical("AdoesNothaveTheSameLengthAsB",
							a.getNameDescription(), b.getNameDescription()));
			} else {
				// sb.append(app.getCommand("Area"));
				if (Kernel.isEqual(((NumberValue) a).getDouble(),
						((NumberValue) b).getDouble()))
					sb.append(getPlainNumerical("AhasTheSameAreaAsB",
							a.getNameDescription(), b.getNameDescription()));
				else
					sb.append(getPlainNumerical("AdoesNothaveTheSameAreaAsB",
							a.getNameDescription(), b.getNameDescription()));
			}
			// sb.append(": ");
			// sb.append(relation((NumberValue) a, (NumberValue) b));
		}

		return sb.toString();
	}

	/**
	 * description of the relation between conics a, b (equal, intersecting or
	 * not intersecting)
	 */
	final private String relation(GeoConic a, GeoConic b) {
		String str;

		if (a.isEqual(b)) {
			str = equalityStringNumerical(a, b, true);
		} else {
			// intersect conics
			// precision setting is not needed here (done by algorithm)
			AlgoIntersectConics algo = new AlgoIntersectConics(cons, a, b);
			GeoPoint[] points = algo.getIntersectionPoints();
			cons.removeFromConstructionList(algo);

			// check for defined intersection points
			boolean intersect = false;
			for (int i = 0; i < points.length; i++) {
				if (points[i].isDefined()) {
					intersect = true;
					break;
				}
			}
			// build relation string
			str = intersectString(a, b, intersect);

			// remove algorithm by removing one of its points
			points[0].remove();
		}
		return str;
	}

	/**
	 * description of the relation between functions
	 */
	final private String relation(GeoFunction a, GeoFunction b) {
		
		return equalityStringExact(a, b, a.isEqual(b));
	}

	/***************************
	 * private methods
	 ***************************/

	// "Relation of a and b: equal"
	// "Relation of a and b: unequal"
	final private String equalityStringNumerical(GeoElement a, GeoElement b,
			boolean equal) {
		if (equal) {
			return getPlainNumerical("AandBareEqual", a.getNameDescription(),
					b.getNameDescription());
		}
		return getPlainNumerical("AandBareNotEqual", a.getNameDescription(),
				b.getNameDescription());
	}

	// "Relation of a and b: equal"
	// "Relation of a and b: unequal"
	final private String equalityStringExact(GeoElement a, GeoElement b,
			boolean equal) {
		if (equal) {
			return loc.getPlain("AandBareEqual", a.getNameDescription(),
					b.getNameDescription());
		}
		return loc.getPlain("AandBareNotEqual", a.getNameDescription(),
				b.getNameDescription());
	}

	// "Relation of a and b: linear dependent"
	// "Relation of a and b: linear independent"
	final private String linDependencyString(GeoElement a, GeoElement b,
			boolean dependent) {
		if (dependent) {
			return getPlainNumerical("AandBareLinearlyDependent",
					a.getNameDescription(), b.getNameDescription());
		}
		return getPlainNumerical("AandBareLinearlyIndependent",
				a.getNameDescription(), b.getNameDescription());
	}

	// "a lies on b"
	// "a does not lie on b"
	final private String incidenceString(GeoPoint a, GeoElement b,
			boolean incident) {
		if (incident) {
			return getPlainNumerical("AliesOnB", a.getNameDescription(),
					b.getNameDescription());
		}
		return getPlainNumerical("AdoesNotLieOnB", a.getNameDescription(),
				b.getNameDescription());
	}

	// "a lies on the perimeter of b"
	// "a does not lie on the perimeter of b"
	final private String incidencePerimeterString(GeoPoint a, GeoElement b,
			boolean incident) {
		if (incident) {
			return getPlainNumerical("AliesOnThePerimeterOfB",
					a.getNameDescription(), b.getNameDescription());
		}
		return getPlainNumerical("AdoesNotLieOnThePerimeterOfB",
				a.getNameDescription(), b.getNameDescription());
	}

	// "Relation of a and b: parallel"
	final private String parallelString(GeoLine a, GeoLine b) {
		return getPlainNumerical("AandBareParallel", a.getNameDescription(),
				b.getNameDescription());
	}

	// Michael Borcherds 2008-05-15
	final private String perpendicularString(GeoLine a, GeoLine b) {
		return getPlainNumerical("AandBarePerpendicular",
				a.getNameDescription(), b.getNameDescription());
	}

	// "a intersects with b"
	final private String intersectString(GeoElement a, GeoElement b,
			boolean intersects) {
		StringBuilder sb = new StringBuilder();
		// Michael Borcherds 2008-05-14
		// updated for better translation
		if (intersects)
			sb.append(getPlainNumerical("AIntersectsWithB",
					a.getNameDescription(), b.getNameDescription()));
		else
			sb.append(getPlainNumerical("ADoesNotIntersectWithB",
					a.getNameDescription(), b.getNameDescription()));
		return sb.toString();
	}

	// e.g "a is tangent of b"
	// types are defined in AlgoIntersectLineConic
	final private String lineConicString(GeoLine a, GeoConic b, int type) {

		switch (type) {
		case AlgoIntersectLineConic.INTERSECTION_PRODUCING_LINE:
			// strType = getPlain("producingLine");
			return getPlainNumerical("AisaDegenerateBranchOfB",
					a.getNameDescription(), b.getNameDescription());
			// break;

		case AlgoIntersectLineConic.INTERSECTION_ASYMPTOTIC_LINE:
			// strType = getPlain("asymptoticLine");
			return getPlainNumerical("AisAnAsymptoteToB",
					a.getNameDescription(), b.getNameDescription());
			// break;

		case AlgoIntersectLineConic.INTERSECTION_MEETING_LINE:
			// strType = getPlain("meetingLine");
			return getPlainNumerical("AintersectsWithBOnce",
					a.getNameDescription(), b.getNameDescription());
			// break;

		case AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE:
			// strType = getPlain("tangentLine");
			return getPlainNumerical("AisaTangentToB", a.getNameDescription(),
					b.getNameDescription());
			// break;

		case AlgoIntersectLineConic.INTERSECTION_SECANT_LINE:
			// strType = getPlain("secantLine");
			return getPlainNumerical("AintersectsWithBTwice",
					a.getNameDescription(), b.getNameDescription());
			// break;

		default:
			// case AlgoIntersectLineConic.INTERSECTION_PASSING_LINE:
			// strType = getPlain("passingLine");
			return getPlainNumerical("ADoesNotIntersectWithB",
					a.getNameDescription(), b.getNameDescription());
			// break;
		}

	}

	private String getPlainNumerical(String string, String nameDescription,
			String nameDescription2) {

		return loc.getPlain(string, nameDescription, nameDescription2) + "\n"
				+ app.getPlain("CheckedNumerically");
	}

	private String getPlainNumerical(String string, String nameDescription) {
		return loc.getPlain(string, nameDescription) + "\n"
				+ app.getPlain("CheckedNumerically");
	}

}
