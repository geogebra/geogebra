/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * RelationNumerical.java
 *
 * Created on 27 June 2014, 14:17
 * 
 * based on Relation.java by Markus
 * created on 12 December 2001, 12:37
 */

package org.geogebra.common.kernel;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.geogebra.common.kernel.RelationNumerical.Report.RelationCommand;
import org.geogebra.common.kernel.algos.AlgoIntersectConics;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.DoubleUtil;

/**
 * Markus' original code has been extensively rewritten. On the other hand, the
 * new behavior is backward compatible and the basic design is still the same.
 * 
 * @author Zoltan Kovacs
 */
public class RelationNumerical {
	private App app;
	private Localization loc;
	private Construction cons;

	private Set<Report> reports;

	/**
	 * Stores information about geometric facts being computed numerically.
	 * 
	 */
	public static class Report {
		/**
		 * True if the numerical computation resulted in "yes". False if the
		 * computation resulted in "no". Null if computation was not done due to
		 * some error.
		 */
		public Boolean boolResult;

		/**
		 * The internal name of the symbolic check, mostly the name of the Are*
		 * command.
		 */
		public enum RelationCommand {
			/**
			 * equality
			 */
			AreEqual,
			/**
			 * parallelism
			 */
			AreParallel,
			/**
			 * orthogonality
			 */
			ArePerpendicular,
			/**
			 * member of a path
			 */
			IsOnPath,
			/**
			 * congruent segments
			 */
			AreCongruent,
			/** collinear points */
			AreCollinear,
			/** concyclic points */
			AreConcyclic,
			/** concurrent lines */
			AreConcurrent,
			/** line tangent to conic */
			IsTangent
		}

		/**
		 * Null if no further symbolic check is proposed. (Sometimes there are
		 * no suitable checks.) Otherwise the name of the symbolic check (mostly
		 * AreEqual).
		 */
		public RelationCommand symbolicCheck;

		/**
		 * Localized version of the numerical computation check.
		 */
		public String stringResult;

		/**
		 * Creates a numerical computation report.
		 * 
		 * @param boolres
		 *            Boolean result.
		 * @param command
		 *            GeoGebra's Are... command to be done for further symbolic
		 *            checking.
		 * @param stringres
		 *            Localized string result.
		 */
		Report(Boolean boolres, RelationCommand command, String stringres) {
			boolResult = boolres;
			symbolicCheck = command;
			stringResult = stringres;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Report)) {
				return false;
			}
			// This does not work in GWT, maybe there is something equivalent.
			// if (!Report.class.isAssignableFrom(obj.getClass())) {
			// return false;
			// }
			// Maybe this is not really required at all...
			return this.stringResult
					.equalsIgnoreCase(((Report) obj).stringResult);
		}

		@Override
		public int hashCode() {
			return stringResult.hashCode();
		}

	}

	/**
	 * Sort the relation reports alphabetically
	 * 
	 * @param reports
	 *            unsorted relation reports
	 * @return alphabetically sorted relation reports
	 */
	public static SortedSet<Report> sortAlphabetically(Set<Report> reports) {

		Comparator<Report> myComparator = new Comparator<Report>() {
			@Override
			public int compare(Report r1, Report r2) {
				return r1.stringResult.compareTo(r2.stringResult);
			}
		};

		TreeSet<Report> sortedReports = new TreeSet<>(myComparator);
		sortedReports.addAll(reports);
		return sortedReports;
	}

	private void register(Boolean boolres, RelationCommand command,
			String stringres) {
		Report r = new Report(boolres, command, stringres);
		reports.add(r);
	}

	/**
	 * Creates new relation
	 * 
	 * @param kernel
	 *            kernel
	 */
	public RelationNumerical(Kernel kernel) {
		app = kernel.getApplication();
		loc = app.getLocalization();
		cons = kernel.getConstruction();
		reports = new HashSet<>();
	}

	/**
	 * Description of the relation between three GeoElements a, b and c (equal,
	 * incident, intersect, parallel, linear dependent, tangent of, ...)
	 * 
	 * @param a
	 *            first geo
	 * @param b
	 *            second geo
	 * @param c
	 *            third geo (optional)
	 * @return string describing relation between these two
	 */
	final public Set<Report> relation(GeoElement a, GeoElement b,
			GeoElement c) {
		// check defined state
		if (!a.isDefined()) {
			register(null, null,
					loc.getPlain("AisNotDefined", getColoredLabel(a)));
			return reports;
		} else if (!b.isDefined()) {
			register(null, null,
					loc.getPlain("AisNotDefined", getColoredLabel(b)));
			return reports;
		} else if (!c.isDefined()) {
			register(null, null,
					loc.getPlain("AisNotDefined", getColoredLabel(c)));
			return reports;
		}
		// decide what relation method can be used

		// point, point, point
		// segment, segment, segment
		// line, line, line
		if (a instanceof GeoPoint && b instanceof GeoPoint
				&& c instanceof GeoPoint) {
			return relation((GeoPoint) a, (GeoPoint) b, (GeoPoint) c);
		} else if (a instanceof GeoSegmentND && b instanceof GeoSegmentND
				&& c instanceof GeoSegmentND) {
			return relation((GeoSegmentND) a, (GeoSegmentND) b,
					(GeoSegmentND) c);
		} else if (a instanceof GeoLine && b instanceof GeoLine
				&& c instanceof GeoLine) {
			return relation((GeoLine) a, (GeoLine) b, (GeoLine) c);
		}

		register(null, null, loc.getMenu("ComparisonNotPossible"));
		return reports;
	}

	private String getColoredLabel(GeoElement geo) {
		if (app.isDesktop()) {
			return geo.getColoredLabel();
		} else {
			String formattedLabel = geo.getLabel(StringTemplate.defaultTemplate);
			return "<strong>" + GeoElement.indicesToHTML(formattedLabel, false) + "</strong>";
		}
	}

	/**
	 * description of the relation between two GeoElements a, b (equal,
	 * incident, intersect, parallel, linear dependent, tangent of, ...)
	 * 
	 * @param a
	 *            first geo
	 * @param b
	 *            second geo
	 * @param c
	 *            third geo (optional)
	 * @param d
	 *            forth geo (optional)
	 * @return string describing relation between these two
	 */
	final public Set<Report> relation(GeoElement a, GeoElement b, GeoElement c,
			GeoElement d) {
		if (d == null) {
			if (c == null) {
				return relation(a, b);
			}
			return relation(a, b, c);
		}
		// check defined state
		if (!a.isDefined()) {
			register(null, null,
					loc.getPlain("AisNotDefined", getColoredLabel(a)));
			return reports;
		} else if (!b.isDefined()) {
			register(null, null,
					loc.getPlain("AisNotDefined", getColoredLabel(b)));
			return reports;
		} else if (!c.isDefined()) {
			register(null, null,
					loc.getPlain("AisNotDefined", getColoredLabel(c)));
			return reports;
		} else if (!d.isDefined()) {
			register(null, null,
					loc.getPlain("AisNotDefined", getColoredLabel(d)));
			return reports;
		}

		// decide what relation method can be used

		// point, point, point, point
		if (a instanceof GeoPoint && b instanceof GeoPoint
				&& c instanceof GeoPoint && d instanceof GeoPoint) {
			return relation((GeoPoint) a, (GeoPoint) b, (GeoPoint) c,
					(GeoPoint) d);
		}
		register(null, null, loc.getMenu("ComparisonNotPossible"));
		return reports;

	}

	/**
	 * description of the relation between two GeoElements a, b (equal,
	 * incident, intersect, parallel, linear dependent, tangent of, ...)
	 * 
	 * @param a
	 *            first geo
	 * @param b
	 *            second geo
	 * @return string describing relation between these two
	 */
	final public Set<Report> relation(GeoElement a, GeoElement b) {
		// check defined state
		if (!a.isDefined()) {
			register(null, null,
					loc.getPlain("AisNotDefined", getColoredLabel(a)));
			return reports;
		} else if (!b.isDefined()) {
			register(null, null,
					loc.getPlain("AisNotDefined", getColoredLabel(b)));
			return reports;
		}

		// decide what relation method can be used

		// point, point
		if (a instanceof GeoPoint && b instanceof GeoPoint) {
			return relation((GeoPoint) a, (GeoPoint) b);
		} else if (a instanceof GeoVector && b instanceof GeoVector) {
			return relation((GeoVector) a, (GeoVector) b);
		} else if (a instanceof GeoSegmentND && b instanceof GeoSegmentND) {
			return relation((GeoSegmentND) a, (GeoSegmentND) b);
		} else if (a instanceof GeoLine && b instanceof GeoLine) {
			return relation((GeoLine) a, (GeoLine) b);
		} else if (a instanceof GeoConicPart && b instanceof GeoConicPart) {
			return relation((GeoConicPart) a, (GeoConicPart) b);
		} else if (a instanceof GeoConic && b instanceof GeoConic) {
			return relation((GeoConic) a, (GeoConic) b);
		} else if (a instanceof GeoFunction && b instanceof GeoFunction) {
			return relation((GeoFunction) a, (GeoFunction) b);
		} else if (a instanceof GeoPoint && b instanceof GeoPolygon) {
			return relation((GeoPoint) a, (GeoPolygon) b);
		} else if (a instanceof GeoPolygon && b instanceof GeoPoint) {
			return relation((GeoPoint) b, (GeoPolygon) a);
		} else if (a instanceof GeoPolygon && b instanceof GeoPolygon) {
			return relation((GeoPolygon) a, (GeoPolygon) b);
		} else if (a instanceof GeoPoint && b instanceof Path) {
			return relation((GeoPoint) a, (Path) b);
		} else if (a instanceof Path && b instanceof GeoPoint) {
			return relation((GeoPoint) b, (Path) a);
		} else if (a instanceof GeoConic && b instanceof GeoLine) {
			return relation((GeoLine) b, (GeoConic) a);
		} else if (a instanceof GeoLine && b instanceof GeoConic) {
			return relation((GeoLine) a, (GeoConic) b);
		} else if (a instanceof GeoNumberValue && b instanceof GeoNumberValue) {
			return relation((GeoNumberValue) a, (GeoNumberValue) b);
		} else if (a instanceof GeoList && b instanceof GeoList) {
			return relation((GeoList) a, (GeoList) b);
		} else {
			register(null, null, loc.getPlain("AandBcannotBeCompared",
					getColoredLabel(a), getColoredLabel(b)));
			return reports;
		}
	}

	/**
	 * description of the relation between two lists a, b (equal, unequal)
	 */
	final private Set<Report> relation(GeoList a, GeoList b) {
		Boolean bool = a.isEqual(b);
		String str = equalityString(a.toGeoElement(), b.toGeoElement(), bool);
		register(bool, null, str);
		return reports;
	}

	/**
	 * description of the relation between two numbers a, b (equal, unequal)
	 */
	final private Set<Report> relation(GeoNumberValue a, GeoNumberValue b) {
		Boolean bool = DoubleUtil.isEqual(a.getDouble(), b.getDouble());
		String str = equalityString(a.toGeoElement(), b.toGeoElement(), bool);
		register(bool, RelationCommand.AreEqual, str);
		return reports;
	}

	/**
	 * description of the relation between segment a and segment b (equal,
	 * unequal)
	 */
	final private Set<Report> relation(GeoSegmentND a, GeoSegmentND b) {
		Boolean bool;
		String str;
		if (DoubleUtil.isEqual(a.evaluateDouble(), b.evaluateDouble())) {

			if (a.isEqual(b)) {
				// numerical report on perfect match of segments (equality)
				register(true, null,
						equalityString((GeoElement) a, (GeoElement) b, true));
				// temporarily allow symbolic check of congruency
				// (until equality is not implemented symbolically)
				register(true, RelationCommand.AreCongruent,
						congruentSegmentString((GeoElement) a, (GeoElement) b,
								true, loc));
			} else {
				register(true, RelationCommand.AreCongruent,
						congruentSegmentString((GeoElement) a, (GeoElement) b,
								true, loc));
			}
		} else {
			register(false, null, congruentSegmentString((GeoElement) a,
					(GeoElement) b, false, loc));
		}

		// Checking parallelism:
		bool = ((GeoLine) a).isParallel((GeoLine) b);
		if (bool) {
			str = parallelString((GeoLine) a, (GeoLine) b);
			register(true, RelationCommand.AreParallel, str);
		}

		// Checking orthogonality:
		bool = ((GeoLine) a).isPerpendicular((GeoLine) b);
		if (bool) {
			str = perpendicularString((GeoLine) a, (GeoLine) b, true);
			register(true, RelationCommand.ArePerpendicular, str);
		}

		return reports;
	}

	/**
	 * description of the relation among segments a, b and c (equal, unequal)
	 */
	final private Set<Report> relation(GeoSegmentND a, GeoSegmentND b,
			GeoSegmentND c) {
		/* Checking if the objects/lengths are equal. */
		if (DoubleUtil.isEqual(a.getDouble(), b.getDouble())
				&& DoubleUtil.isEqual(b.getDouble(), c.getDouble())) {
			if (a.isEqual(b) && b.isEqual(c)) {
				register(true, null, equalityString((GeoElement) a,
						(GeoElement) b, (GeoElement) c, true));
				return reports;
			}
			register(true, null, congruentSegmentString((GeoElement) a,
					(GeoElement) b, (GeoElement) c));
			return reports;
			}
		/*
		 * As segments there is no relation among them. Maybe there is positive
		 * result when they are considered as lines.
		 */
		return relation((GeoLine) a, (GeoLine) b, (GeoLine) c);
	}

	/**
	 * description of the relation between two points A, B (equal, unequal)
	 */
	final private Set<Report> relation(GeoPoint A, GeoPoint B) {
		Boolean bool = A.isEqual(B);
		String str = equalityString(A, B, bool);
		register(bool, RelationCommand.AreEqual, str);
		return reports;
	}

	/**
	 * description of the relation of three points A, B, C (equal, unequal,
	 * collinear)
	 */
	final private Set<Report> relation(GeoPoint A, GeoPoint B, GeoPoint C) {
		if (A.isEqual(B) && A.isEqual(C)) {
			String str = equalityString(A, B, C, true);
			// consider implementing Prove[A==B==C]
			register(true, null, str);
		} else if (GeoPoint.collinear(A, B, C)) {
			String str = collinearityString(A, B, C);
			register(true, RelationCommand.AreCollinear, str);
		} else {
			String str = equalityString(A, B, C, false);
			register(false, null, str);
		}
		return reports;
	}

	/**
	 * description of the relation of three points A, B, C, D (equal, unequal,
	 * collinear, concyclic)
	 */
	final private Set<Report> relation(GeoPoint A, GeoPoint B, GeoPoint C,
			GeoPoint D) {
		if (A.isEqual(B) && A.isEqual(C) && A.isEqual(D)) {
			String str = equalityString(A, B, C, D, true);
			// consider implementing Prove[A==B==C==D]
			register(true, null, str);
		} else if (GeoPoint.collinear(A, B, C) && GeoPoint.collinear(A, B, D)) {
			String str = collinearityString(A, B, C, D);
			// consider implementing Prove[AreCollinear[A,B,C,D]]
			register(true, null, str);
		} else if (GeoPoint.concyclic(A, B, C, D)) {
			String str = concyclicityString(A, B, C, D);
			register(true, RelationCommand.AreConcyclic, str);
		} else {
			String str = equalityString(A, B, C, D, false);
			register(false, null, str);
		}
		return reports;
	}

	/**
	 * description of the relation between two vectors a, b (equal, linear
	 * dependent, linear independent)
	 */
	final private Set<Report> relation(GeoVector a, GeoVector b) {
		String str;
		Boolean bool;
		if (a.isEqual(b)) {
			str = equalityString(a, b, true);
			bool = true;
		} else {
			str = linDependencyString(a, b, a.linDep(b));
			bool = false;
		}
		register(bool, RelationCommand.AreEqual, str);
		return reports;
	}

	/**
	 * description of the relation between point A and a polygon ((not) on
	 * perimeter)
	 */
	final private Set<Report> relation(GeoPoint A, GeoPolygon p) {
		Boolean bool = p.isOnPath(A, Kernel.STANDARD_PRECISION);
		String str = incidencePerimeterString(A, p.toGeoElement(), bool);
		register(bool, null, str);
		// TODO: Symbolically we cannot decide this yet.
		return reports;
	}

	/**
	 * description of the relation between point A and a path (incident, not
	 * incident)
	 */
	final private Set<Report> relation(GeoPoint A, Path path) {
		Boolean bool = path.isOnPath(A, Kernel.STANDARD_PRECISION);
		String str = incidenceString(A, path.toGeoElement(), bool);
		register(bool, RelationCommand.IsOnPath, str);
		return reports;
	}

	/**
	 * description of the relation between polygons a and b
	 */
	final private Set<Report> relation(GeoPolygon a, GeoPolygon b) {
		Boolean bool = a.hasSameArea(b);
		String str = equalAreaString(a, b, bool, loc);
		register(bool, RelationCommand.AreEqual, str);
		return reports;
	}

	/**
	 * description of the relation between lines g and h (equal, parallel or
	 * intersecting)
	 */
	final private Set<Report> relation(GeoLine g, GeoLine h) {
		String str;
		// check for equality
		if (g.isEqual(h)) {
			str = equalityString(g, h, true);
			register(true, RelationCommand.AreEqual, str);
		} else {
			if (g.isParallel(h)) {
				str = parallelString(g, h);
				register(true, RelationCommand.AreParallel, str);
			} else if (g.isPerpendicular(h)) {
				str = perpendicularString(g, h, true);
				register(true, RelationCommand.ArePerpendicular, str);
			} else {
				// check if intersection point really lies on both objects (e.g.
				// segments)
				// TODO: This cannot be done with the current symbolic methods
				// yet.
				GeoPoint tempPoint = new GeoPoint(g.cons);
				GeoVec3D.cross(g, h, tempPoint);
				boolean isIntersection = g.isIntersectionPointIncident(
						tempPoint, Kernel.STANDARD_PRECISION)
						&& h.isIntersectionPointIncident(tempPoint,
								Kernel.STANDARD_PRECISION);

				str = intersectString(g, h, isIntersection);
				register(isIntersection, null, str);
			}
		}
		return reports;
	}

	/**
	 * description of the relation between lines g, h and i (concurrency)
	 */
	final private Set<Report> relation(GeoLine g, GeoLine h, GeoLine i) {
		String str;
		// check for equality
		if (g.isEqual(h) && g.isEqual(i)) {
			str = equalityString(g, h, i, true);
			// consider implementing Prove[g==h==i]
			register(true, null, str);
		} else if (g.isParallel(h) && g.isParallel(i)) {
			str = parallelString(g, h, i);
			// consider implementing Prove[g||h||i]
			register(true, null, str);
		} else if (GeoLine.concurrent(g, h, i)) {
			str = concurrentString(g, h, i);
			register(true, RelationCommand.AreConcurrent, str);
		} else {
			str = equalityString(g, h, i, false);
			// consider implementing Prove[g==h==i]
			register(false, null, str);
		}
		return reports;
	}

	/**
	 * description of the relation between line g and conic c (intersection
	 * type: tangent, secant, ...)
	 */
	final private Set<Report> relation(GeoLine g, GeoConic c) {
		int type;
		String str;

		// limited paths have to handled differently (e.g. segments, arcs)
		if (g.isLimitedPath() || c.isLimitedPath()) {
			// intersect line and conic
			// precision setting is not needed here (done by algorithm)
			AlgoIntersectLineConic algo = new AlgoIntersectLineConic(cons, g,
					c);
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
			register(intersect, null, str);
			// TODO: Unsupported symbolically.
			return reports;
		}

		// is line defined as tangent or asymptote of c?
		if (g.isDefinedTangent(c)) {
			str = lineConicString(g, c,
					AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE);
			register(true, RelationCommand.IsTangent, str);
			// TODO: Consider showing "always true" in this case immediately
		} else if (g.isDefinedAsymptote(c)) {
			str = lineConicString(g, c,
					AlgoIntersectLineConic.INTERSECTION_ASYMPTOTIC_LINE);
			register(null, null, str); // unsupported symbolically
		} else {
			// intersect line and conic
			GeoPoint[] points = { new GeoPoint(cons), new GeoPoint(cons) };
			type = AlgoIntersectLineConic.intersectLineConic(g, c, points,
					Kernel.STANDARD_PRECISION);
			str = lineConicString(g, c, type);
			if (type == AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE) {
				register(true, RelationCommand.IsTangent, str);
			} else {
				register(false, null, str);
			}
		}
		return reports;
	}

	/**
	 * description of the relation between conic parts a, b (equal, intersecting
	 * or not intersecting)
	 */
	final private Set<Report> relation(GeoConicPart a, GeoConicPart b) {
		Boolean bool = a.isEqual(b);
		String str = equalityString(a, b, bool);
		register(bool, null, str);
		// TODO: No prover support for conic equality yet.

		bool = DoubleUtil.isEqual(a.evaluateDouble(), b.evaluateDouble());
		int type = a.getConicPartType();
		if (type == b.getConicPartType()) {
			if (type == GeoConicNDConstants.CONIC_PART_ARC) {
				str = congruentSegmentString(a, b, bool, loc);
				register(bool, null, str); // TODO: No symbolic support.
			} else {
				str = equalAreaString(a, b, bool, loc);
				register(bool, null, str); // TODO: No symbolic support.
			}
			// sb.append(": ");
			// sb.append(relation((NumberValue) a, (NumberValue) b));
		}
		return reports;
	}

	/**
	 * description of the relation between conics a, b (equal, intersecting or
	 * not intersecting)
	 */
	final private Set<Report> relation(GeoConic a, GeoConic b) {
		String str;

		if (a.isEqual(b)) {
			str = equalityString(a, b, true);
			register(true, null, str); // TODO: No symbolically supported.
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
			boolean touch = false;
			// check if we have one intersection point
			if (points[0].isEqual(points[1])) {
				touch = true;
			}
			// build relation string
			// case one intersection point
			if (touch) {
				str = touchString(a, b, touch);
			}
			// case more than one intersection point
			else {
				str = intersectString(a, b, intersect);
			}
			register(true, null, str); // TODO: No symbolically supported.

			// remove algorithm by removing one of its points
			points[0].remove();
		}
		return reports;
	}

	/**
	 * description of the relation between functions
	 */
	final private Set<Report> relation(GeoFunction a, GeoFunction b) {
		Boolean bool = a.isEqual(b);
		String str = equalityString(a, b, bool); // This was equalityStringExact
													// originally.
		register(bool, null, str); // No symbolically supported.
		return reports;
	}

	/***************************
	 * private methods
	 ***************************/

	// "Relation of a and b: equal"
	// "Relation of a and b: unequal"
	final private String equalityString(GeoElement a, GeoElement b,
			boolean equal) {
		return equalityString(a, b, equal, loc);
	}

	/**
	 * Internationalized string of "a and b are equal" (or not)
	 * 
	 * @param a
	 *            first object
	 * @param b
	 *            second object
	 * @param equal
	 *            yes or no
	 * @param loc
	 *            locale
	 * @return internationalized string
	 */
	final public String equalityString(GeoElement a, GeoElement b,
			boolean equal, Localization loc) {
		if (equal) {
			return loc.getPlain("AandBareEqual", getColoredLabel(a),
					getColoredLabel(b));
		}
		return loc.getPlain("AandBareNotEqual", getColoredLabel(a),
				getColoredLabel(b));
	}

	/**
	 * Internationalized string of "a, b and c are equal" (or not)
	 * 
	 * @param a
	 *            first object
	 * @param b
	 *            second object
	 * @param c
	 *            third object
	 * @param equal
	 *            if objects are equal
	 * 
	 * @return internationalized string
	 */
	final public String equalityString(GeoElement a, GeoElement b,
			GeoElement c, boolean equal) {
		String and = loc.getMenu("Symbol.And").toLowerCase();
		String pars = getColoredLabel(a) + ", " + getColoredLabel(b) + " "
				+ and + " " + getColoredLabel(c);

		if (equal) {
			return loc.getPlain("TheFollowingAreEqualA", pars);
		}
		return loc.getPlain("TheFollowingAreNotEqualA", pars);
	}

	/**
	 * Internationalized string of "a, b and c are congruent" (or not)
	 * 
	 * @param a
	 *            first object
	 * @param b
	 *            second object
	 * @param c
	 *            third object
	 * 
	 * @return internationalized string
	 */
	final public String congruentSegmentString(GeoElement a, GeoElement b,
			GeoElement c) {
		String and = loc.getMenu("Symbol.And").toLowerCase();
		String pars = getColoredLabel(a) + ", " + getColoredLabel(b) + " "
				+ and + " " + getColoredLabel(c);
		return loc.getPlain("TheFollowingAreCongruentA", pars);
	}

	/**
	 * Internationalized string of "a, b, c and d are equal" (or not)
	 * 
	 * @param a
	 *            first object
	 * @param b
	 *            second object
	 * @param c
	 *            third object
	 * @param d
	 *            forth object
	 * @param equal
	 *            if the objects are equal
	 * 
	 * @return internationalized string
	 */
	final public String equalityString(GeoElement a, GeoElement b, GeoElement c,
			GeoElement d, boolean equal) {
		String and = loc.getMenu("Symbol.And").toLowerCase();
		String pars = getColoredLabel(a) + ", " + getColoredLabel(b) + ", "
				+ getColoredLabel(c) + " " + and + " " + getColoredLabel(d);

		if (equal) {
			return loc.getPlain("TheFollowingAreEqualA", pars);
		}
		return loc.getPlain("TheFollowingAreNotEqualA", pars);
	}

	/**
	 * Internationalized string of "a and b are congruent" (or not)
	 * 
	 * @param a
	 *            first object
	 * @param b
	 *            second object
	 * @param equal
	 *            yes or no
	 * @param loc
	 *            locale
	 * @return internationalized string
	 */
	final public String congruentSegmentString(GeoElement a,
			GeoElement b, boolean equal, Localization loc) {
		if (equal) {
			return loc.getPlain("AhasTheSameLengthAsB", getColoredLabel(a),
					getColoredLabel(b));
		}
		return loc.getPlain("AdoesNothaveTheSameLengthAsB", getColoredLabel(a),
				getColoredLabel(b));
	}

	/**
	 * Internationalized string of "a and b have the same area" (or not)
	 * 
	 * @param a
	 *            first object
	 * @param b
	 *            second object
	 * @param equal
	 *            yes or no
	 * @param loc
	 *            locale
	 * @return internationalized string
	 */
	final public String equalAreaString(GeoElement a, GeoElement b,
			boolean equal, Localization loc) {
		if (equal) {
			return loc.getPlain("AhasTheSameAreaAsB", getColoredLabel(a),
					getColoredLabel(b));
		}
		return loc.getPlain("AdoesNothaveTheSameAreaAsB", getColoredLabel(a),
				getColoredLabel(b));
	}

	/**
	 * Internationalized string of "A, B and C are collinear"
	 * 
	 * @param A
	 *            first object
	 * @param B
	 *            second object
	 * @param C
	 *            third object
	 * 
	 * @return internationalized string
	 */
	final public String collinearityString(GeoElement A, GeoElement B,
			GeoElement C) {
		String and = loc.getMenu("Symbol.And").toLowerCase();
		String pars = getColoredLabel(A) + ", " + getColoredLabel(B) + " "
				+ and + " " + getColoredLabel(C);
		return loc.getPlain("TheFollowingAreCollinearA", pars);
	}

	/**
	 * Internationalized string of "A, B, C and D are collinear"
	 * 
	 * @param A
	 *            first object
	 * @param B
	 *            second object
	 * @param C
	 *            third object
	 * @param D
	 *            forth object
	 *
	 * @return internationalized string
	 */
	final public String collinearityString(GeoElement A, GeoElement B,
			GeoElement C, GeoElement D) {
		String and = loc.getMenu("Symbol.And").toLowerCase();
		String pars = getColoredLabel(A) + ", " + getColoredLabel(B) + ", "
				+ getColoredLabel(C) + " " + and + " " + getColoredLabel(D);
		return loc.getPlain("TheFollowingAreCollinearA", pars);
	}

	/**
	 * Internationalized string of "a, b and c are concurrent"
	 * 
	 * @param a
	 *            first object
	 * @param b
	 *            second object
	 * @param c
	 *            third object
	 *
	 * @return internationalized string
	 */
	final public String concurrentString(GeoElement a, GeoElement b,
			GeoElement c) {
		String and = loc.getMenu("Symbol.And").toLowerCase();
		String pars = getColoredLabel(a) + ", " + getColoredLabel(b) + " "
				+ and + " " + getColoredLabel(c);
		return loc.getPlain("TheFollowingAreConcurrentA", pars);
	}

	/**
	 * Internationalized string of "A, B, C and D are concyclic"
	 * 
	 * @param A
	 *            first object
	 * @param B
	 *            second object
	 * @param C
	 *            third object
	 * @param D
	 *            forth object
	 *
	 * @return internationalized string
	 */
	final public String concyclicityString(GeoElement A, GeoElement B,
			GeoElement C, GeoElement D) {
		String and = loc.getMenu("Symbol.And").toLowerCase();
		String pars = getColoredLabel(A) + ", " + getColoredLabel(B) + ", "
				+ getColoredLabel(C) + " " + and + " " + getColoredLabel(D);
		return loc.getPlain("TheFollowingAreConcyclicA", pars);
	}

	// "Relation of a and b: linear dependent"
	// "Relation of a and b: linear independent"
	final private String linDependencyString(GeoElement a, GeoElement b,
			boolean dependent) {
		if (dependent) {
			return loc.getPlain("AandBareLinearlyDependent",
					getColoredLabel(a), getColoredLabel(b));
		}
		return loc.getPlain("AandBareLinearlyIndependent", getColoredLabel(a),
				getColoredLabel(b));
	}

	// "a lies on b"
	// "a does not lie on b"
	final private String incidenceString(GeoPoint a, GeoElement b,
			boolean incident) {
		if (incident) {
			return loc.getPlain("AliesOnB", getColoredLabel(a),
					getColoredLabel(b));
		}
		return loc.getPlain("AdoesNotLieOnB", getColoredLabel(a),
				getColoredLabel(b));
	}

	// "a lies on the perimeter of b"
	// "a does not lie on the perimeter of b"
	final private String incidencePerimeterString(GeoPoint a, GeoElement b,
			boolean incident) {
		if (incident) {
			return loc.getPlain("AliesOnThePerimeterOfB", getColoredLabel(a),
					getColoredLabel(b));
		}
		return loc.getPlain("AdoesNotLieOnThePerimeterOfB", getColoredLabel(a),
				getColoredLabel(b));
	}

	// "Relation of a and b: parallel"
	final private String parallelString(GeoLine a, GeoLine b) {
		return parallelString(a, b, loc);
	}

	// "Relation of a and b and c: parallel"
	final private String parallelString(GeoLine a, GeoLine b, GeoLine c) {
		return parallelString(a, b, c, loc);
	}

	/**
	 * Internationalized string of "a and b are parallel"
	 * 
	 * @param a
	 *            first line
	 * @param b
	 *            second line
	 * @param loc
	 *            locale
	 * @return internationalized string
	 */
	final public String parallelString(GeoLine a, GeoLine b,
			Localization loc) {
		return loc.getPlain("AandBareParallel", getColoredLabel(a),
				getColoredLabel(b));
	}

	/**
	 * Internationalized string of "a, b and c are parallel"
	 * 
	 * @param a
	 *            first line
	 * @param b
	 *            second line
	 * @param c
	 *            second line
	 * @param loc
	 *            locale
	 * @return internationalized string
	 */
	final public String parallelString(GeoLine a, GeoLine b, GeoLine c,
			Localization loc) {
		String and = loc.getMenu("Symbol.And").toLowerCase();
		String pars = getColoredLabel(a) + ", " + getColoredLabel(b) + " "
				+ and + " " + getColoredLabel(c);
		return loc.getPlain("TheFollowingAreParallelA", pars);

	}

	/*
	 * This is not used yet. It requires support for 3 points in the Relation
	 * Tool.
	 */
	// final private String triangleNonDegenerateString(GeoPoint A, GeoPoint B,
	// GeoPoint C) {
	// return triangleNonDegenerateString(A, B, C, loc);
	// }

	/**
	 * Internationalized string of "Triangle ABC is non-degenerate"
	 * 
	 * @param A
	 *            first vertex
	 * @param B
	 *            second vertex
	 * @param C
	 *            third vertex
	 * @param loc
	 *            locale
	 * @return internationalized string
	 */
	final public String triangleNonDegenerateString(GeoPoint A,
			GeoPoint B, GeoPoint C, Localization loc) {
		return loc.getPlain("TriangleABCnonDegenerate", getColoredLabel(A)
				+ getColoredLabel(B) + getColoredLabel(C));
	}

	final private String perpendicularString(GeoLine a, GeoLine b,
			boolean perp) {
		return perpendicularString(a, b, perp, loc);
	}

	/**
	 * Internationalized string of "a and b are perpendicular" (or not)
	 * 
	 * @param a
	 *            first line
	 * @param b
	 *            second line
	 * @param perp
	 *            yes or no
	 * @param loc
	 *            locale
	 * @return internationalized string
	 */
	final public String perpendicularString(GeoLine a, GeoLine b,
			boolean perp, Localization loc) {
		if (perp) {
			return loc.getPlain("AandBarePerpendicular", getColoredLabel(a),
					getColoredLabel(b));
		}
		return loc.getPlain("AandBareNotPerpendicular", getColoredLabel(a),
				getColoredLabel(b));
	}

	// "a intersects with b"
	final private String intersectString(GeoElement a, GeoElement b,
			boolean intersects) {
		return intersectString(a, b, intersects, loc);
	}

	/**
	 * Internationalized string of "a intersects with b" (or not)
	 * 
	 * @param a
	 *            first object
	 * @param b
	 *            second object
	 * @param intersects
	 *            yes or no
	 * @param loc
	 *            locale
	 * @return internationalized string
	 */
	final public String intersectString(GeoElement a, GeoElement b,
			boolean intersects, Localization loc) {
		StringBuilder sb = new StringBuilder();
		// Michael Borcherds 2008-05-14
		// updated for better translation
		if (intersects) {
			sb.append(loc.getPlain("AIntersectsWithB", getColoredLabel(a),
					getColoredLabel(b)));
		} else {
			sb.append(loc.getPlain("ADoesNotIntersectWithB",
					getColoredLabel(a), getColoredLabel(b)));
		}
		return sb.toString();
	}

	// "a touches b"
	final private String touchString(GeoElement a, GeoElement b,
			boolean touches) {
		return touchString(a, b, touches, loc);
	}

	/**
	 * Internationalized string of "a touches b" (or not)
	 * 
	 * @param a
	 *            first object
	 * @param b
	 *            second object
	 * @param touches
	 *            yes or no
	 * @param loc
	 *            locale
	 * @return internationalized string
	 */
	final public String touchString(GeoElement a, GeoElement b,
			boolean touches, Localization loc) {
		StringBuilder sb = new StringBuilder();
		if (touches) {
			sb.append(loc.getPlain("ATouchesB", getColoredLabel(a),
					getColoredLabel(b)));
		} else {
			sb.append(loc.getPlain("ADoesNotIntersectWithB",
					getColoredLabel(a), getColoredLabel(b)));
		}
		return sb.toString();
	}

	// e.g "a is tangent of b"
	// types are defined in AlgoIntersectLineConic
	final private String lineConicString(GeoLine a, GeoConic b, int type) {

		switch (type) {
		case AlgoIntersectLineConic.INTERSECTION_PRODUCING_LINE:
			// strType = getPlain("producingLine");
			return loc.getPlain("AisaDegenerateBranchOfB", getColoredLabel(a),
					getColoredLabel(b));
		// break;

		case AlgoIntersectLineConic.INTERSECTION_ASYMPTOTIC_LINE:
			// strType = getPlain("asymptoticLine");
			return loc.getPlain("AisAnAsymptoteToB", getColoredLabel(a),
					getColoredLabel(b));
		// break;

		case AlgoIntersectLineConic.INTERSECTION_MEETING_LINE:
			// strType = getPlain("meetingLine");
			return loc.getPlain("AintersectsWithBOnce", getColoredLabel(a),
					getColoredLabel(b));
		// break;

		case AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE:
			// strType = getPlain("tangentLine");
			return loc.getPlain("AisaTangentToB", getColoredLabel(a),
					getColoredLabel(b));
		// break;

		case AlgoIntersectLineConic.INTERSECTION_SECANT_LINE:
			// strType = getPlain("secantLine");
			return loc.getPlain("AintersectsWithBTwice", getColoredLabel(a),
					getColoredLabel(b));
		// break;

		default:
			// case AlgoIntersectLineConic.INTERSECTION_PASSING_LINE:
			// strType = getPlain("passingLine");
			return loc.getPlain("ADoesNotIntersectWithB", getColoredLabel(a),
					getColoredLabel(b));
		// break;
		}
	}

}
