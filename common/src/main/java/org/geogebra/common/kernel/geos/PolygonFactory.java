package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.error.ErrorHelper;

import com.google.j2objc.annotations.Weak;

/**
 * Factory for creating special polygons.
 */
public class PolygonFactory {

	@Weak
	private Construction cons;
	@Weak
	private Kernel kernel;

	/**
	 * @param kernel
	 *            kernel
	 */
	public PolygonFactory(Kernel kernel) {
		this.kernel = kernel;
		this.cons = kernel.getConstruction();
	}

	/**
	 * @param labels
	 *            output labels
	 * @param points
	 *            vertices
	 * @return vector polygon
	 */
	final public GeoElement[] vectorPolygon(String[] labels, GeoPointND[] points) {

		/*
		 * cons.setSuppressLabelCreation(true); getAlgoDispatcher().Circle(null,
		 * (GeoPoint) points[0], new MyDouble(cons.getKernel(),
		 * points[0].distance(points[1])));
		 * cons.setSuppressLabelCreation(oldMacroMode);
		 */

		StringBuilder sb = new StringBuilder();

		double xA = points[0].getInhomX();
		double yA = points[0].getInhomY();

		for (int i = 1; i < points.length; i++) {

			double xC = points[i].getInhomX();
			double yC = points[i].getInhomY();

			GeoNumeric nx = new GeoNumeric(cons, xC - xA);
			GeoNumeric ny = new GeoNumeric(cons, yC - yA);
			nx.setLabel(null);
			ny.setLabel(null);
			StringTemplate tpl = StringTemplate.maxPrecision;
			// make string like this
			// (a+x(A),b+y(A))
			sb.setLength(0);
			sb.append('(');
			sb.append(nx.getLabel(tpl));
			sb.append("+x(");
			sb.append(points[0].getLabel(tpl));
			sb.append("),");
			sb.append(ny.getLabel(tpl));
			sb.append("+y(");
			sb.append(points[0].getLabel(tpl));
			sb.append("))");

			// Application.debug(sb.toString());

			GeoPoint pp = (GeoPoint) kernel.getAlgebraProcessor().evaluateToPoint(sb.toString(),
					ErrorHelper.silent(), true);

			try {
				cons.replace((GeoElement) points[i], pp);
				points[i] = pp;
				// points[i].setEuclidianVisible(false);
				points[i].update();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		points[0].update();

		return kernel.getAlgoDispatcher().polygon(labels, points);

	}

	/**
	 * makes a copy of a polygon that can be dragged and rotated but stays
	 * congruent to original
	 * 
	 * @param poly
	 *            polygon
	 * @param offsetX
	 *            translation x
	 * @param offsetY
	 *            translation y
	 * @param labels
	 *            output labels
	 * @return draggable copy of a polygon
	 */
	final public GeoElement[] rigidPolygon(GeoPolygon poly, double offsetX,
			double offsetY, String[] labels) {

		GeoPointND[] p = new GeoPointND[poly.getPointsLength()];

		// create free point p0
		p[0] = poly.getPoint(0).copy();
		p[0].setLabel(null);

		GeoPointND[] pts = poly.getPoints();

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		// create p1 = point on circle (so it can be dragged to rotate the whole
		// shape)

		GeoSegment radius = kernel.getAlgoDispatcher().segment((String) null, (GeoPoint) pts[0],
				(GeoPoint) pts[1]);

		GeoConicND circle = kernel.getAlgoDispatcher().circle(null, p[0], radius);
		cons.setSuppressLabelCreation(oldMacroMode);

		p[1] = kernel.getAlgoDispatcher().point(null, circle, poly.getPoint(1).inhomX,
				poly.getPoint(1).inhomY, true, false, true);

		p[1].setLabel(null);

		boolean oldVal = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);

		String sb;

		int n = poly.getPointsLength();
		String angle = "arg(" + label(p[1]) + "-" + label(p[0]) + ")-arg("
				+ label(pts[1]) + "-" + label(pts[0]) + ")";
		for (int i = 2; i < n; i++) {
			// build string like
			// A1 + Rotate[D - A, arg(B1 - A1) - arg(B - A)]
			sb = label(p[0]) + "+Rotate[" + label(pts[i]) + "-" + label(pts[0]) + "," + angle + "]";
			p[i] = kernel.getAlgebraProcessor().evaluateToPoint(sb, ErrorHelper.silent(),
					true);
			p[i].setEuclidianVisible(false);
			p[i].setLabel(null);
		}

		kernel.setUseInternalCommandNames(oldVal);

		AlgoPolygon algo = new AlgoPolygon(cons, labels, p);
		GeoElement[] ret = { algo.getOutput(0) };

		GeoPointND firstPoint = ((GeoPolygon) ret[0]).getPoints()[0];

		firstPoint.updateCoords2D();

		firstPoint.setCoords(firstPoint.getX2D() + offsetX, firstPoint.getY2D() + offsetY, 1.0);
		firstPoint.updateRepaint();

		return ret;
	}

	private String label(GeoPointND pt) {
		return pt.getLabel(StringTemplate.noLocalDefault);
	}

	/**
	 * @param labels
	 *            output labels
	 * @param points
	 *            points
	 * @return rigid polygon
	 */
	final public GeoElement[] rigidPolygon(String[] labels, GeoPointND[] points) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();

		cons.setSuppressLabelCreation(true);
		GeoConicND circle = kernel.getAlgoDispatcher().circle(null, points[0],
				new GeoNumeric(cons, points[0].distance(points[1])));
		cons.setSuppressLabelCreation(oldMacroMode);

		GeoPointND p = kernel.rigidPolygonPointOnCircle(circle, points[1]);

		try {
			(cons).replace((GeoElement) points[1], (GeoElement) p);
			points[1] = p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder sb = new StringBuilder();

		double xA = points[0].getInhomX();
		double yA = points[0].getInhomY();
		double xB = points[1].getInhomX();
		double yB = points[1].getInhomY();

		GeoVec2D a = new GeoVec2D(cons.getKernel(), xB - xA, yB - yA); // vector
																		// AB
		GeoVec2D b = new GeoVec2D(cons.getKernel(), yA - yB, xB - xA); // perpendicular
																		// to
		// AB
		// changed to use this instead of Unit(Orthoganal)Vector
		// https://www.geogebra.org/forum/viewtopic.php?f=13&p=82764#p82764
		double aLength = Math.sqrt(a.inner(a));

		boolean oldVal = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);

		a.makeUnitVector();
		b.makeUnitVector();
		StringTemplate tpl = StringTemplate.maxPrecision;
		boolean is3D = points[0].isGeoElement3D() || points[1].isGeoElement3D();
		for (int i = 2; i < points.length; i++) {

			double xC = points[i].getInhomX();
			double yC = points[i].getInhomY();

			GeoVec2D d = new GeoVec2D(cons.getKernel(), xC - xA, yC - yA); // vector
																			// AC

			// make string like this
			// A+3.76UnitVector[Segment[A,B]]+-1.74UnitPerpendicularVector[Segment[A,B]]
			sb.setLength(0);
			sb.append(points[0].getLabel(tpl));
			sb.append('+');
			sb.append(kernel.format(a.inner(d) / aLength, tpl));

			// use internal command name
			sb.append("Vector[");
			sb.append(points[0].getLabel(tpl));
			sb.append(',');
			sb.append(points[1].getLabel(tpl));
			sb.append("]+");
			sb.append(kernel.format(b.inner(d) / aLength, tpl));
			// use internal command name
			sb.append("OrthogonalVector[Segment[");
			sb.append(points[0].getLabel(tpl));
			sb.append(',');
			sb.append(points[1].getLabel(tpl));
			rigidPolygonAddEndOfCommand(sb, is3D);

			// Application.debug(sb.toString());

			GeoPointND pp = kernel.getAlgebraProcessor().evaluateToPoint(sb.toString(),
					ErrorHelper.silent(), true);

			try {
				(cons).replace((GeoElement) points[i], (GeoElement) pp);
				points[i] = pp;
				points[i].setEuclidianVisible(false);
				points[i].update();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		kernel.setUseInternalCommandNames(oldVal);

		points[0].update();

		return kernel.getAlgoDispatcher().polygon(labels, points);

	}

	private static void rigidPolygonAddEndOfCommand(StringBuilder sb, boolean is3D) {
		if (is3D) {
			sb.append("],xOyPlane]");
		} else {
			sb.append("]]");
		}
	}
}
