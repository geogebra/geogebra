package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoLinePoint;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.Unicode;

/**
 * 3D line
 */
public class GeoLine3D extends GeoCoordSys1D {

	private String parameter = Unicode.lambda + "";

	/**
	 * creates a line joining O and I
	 * 
	 * @param c
	 *            construction
	 * @param O
	 *            start point
	 * @param I
	 *            end point
	 */
	public GeoLine3D(Construction c, GeoPointND O, GeoPointND I) {
		super(c, O, I);
	}

	/**
	 * @param c
	 *            construction
	 */
	public GeoLine3D(Construction c) {
		this(c, false);
	}

	/**
	 * @param c
	 *            construction
	 * @param isIntersection
	 *            flag for intersection lines
	 */
	public GeoLine3D(Construction c, boolean isIntersection) {
		super(c, isIntersection);
	}

	/**
	 * @param c
	 *            construction
	 * @param o
	 *            start point
	 * @param v
	 *            diection
	 */
	public GeoLine3D(Construction c, Coords o, Coords v) {
		super(c, o, v);
	}

	@Override
	public void setToParametric(String s) {
		this.parameter = s;
	}

	@Override
	public final void setStartPoint(GeoPointND P) {
		startPoint = P;
	}

	@Override
	protected GeoCoordSys1D create(Construction cons1) {
		return new GeoLine3D(cons1);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.LINE3D;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		App.debug("unimplemented");
		return false;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return coordsys.isDefined();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return buildValueString(tpl).toString();
	}

	@Override
	final public String toString(StringTemplate tpl) {

		StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": "); // TODO use kernel property
		sbToString.append(buildValueString(tpl));
		return sbToString.toString();
	}

	private StringBuilder buildValueString(StringTemplate tpl) {

		StringBuilder sbToString = getSbBuildValueString();
		sbToString.setLength(0);

		if (!isDefined()) {
			sbToString.append("X = (?, ?, ?)");
			return sbToString;
		}

		AlgoElement algo = getParentAlgorithm();

		if (algo instanceof AlgoLinePoint) {
			AlgoLinePoint algoLP = (AlgoLinePoint) algo;

			GeoElement[] geos = algoLP.getInput();

			if (geos[0].isGeoPoint() && geos[1].isGeoVector()) {

				// use original coordinates for displaying, not normalized form
				// for Line[ A, u ]

				GeoPointND pt = (GeoPointND) geos[0];
				Coords coords1 = pt.getInhomCoordsInD3();
				GeoVectorND vec = (GeoVectorND) geos[1];

				Coords coords2 = vec.getCoordsInD3();

				sbToString.append("X = (");
				sbToString.append(kernel.format(coords1.getX(), tpl));
				sbToString.append(", ");
				sbToString.append(kernel.format(coords1.getY(), tpl));
				sbToString.append(", ");
				sbToString.append(kernel.format(coords1.getZ(), tpl));
				sbToString.append(") + ");
				sbToString.append(parameter);
				sbToString.append(" (");
				sbToString.append(kernel.format(coords2.getX(), tpl));
				sbToString.append(", ");
				sbToString.append(kernel.format(coords2.getY(), tpl));
				sbToString.append(", ");
				sbToString.append(kernel.format(coords2.getZ(), tpl));
				sbToString.append(")");

				return sbToString;
			}
		}

		Coords O = coordsys.getOrigin();// TODO inhom coords
		Coords V = coordsys.getVx();

		sbToString.append("X = (");
		sbToString.append(kernel.format(O.get(1), tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(O.get(2), tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(O.get(3), tpl));
		sbToString.append(") + ");
		sbToString.append(parameter);
		sbToString.append(" (");
		sbToString.append(kernel.format(V.get(1), tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(V.get(2), tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(V.get(3), tpl));
		sbToString.append(")");

		return sbToString;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		getCoordsXML(sb);

		sb.append("\t<eqnStyle style=\"parametric\" parameter=\"");
		sb.append(parameter);
		sb.append("\"/>\n");

	}

	/**
	 * set coords for XML
	 * 
	 * @param sb
	 *            string for XML
	 */
	protected void getCoordsXML(StringBuilder sb) {

		Coords o = coordsys.getOrigin();// TODO inhom coords
		Coords v = coordsys.getVx();

		sb.append("\t<coords");
		sb.append(" ox=\"");
		sb.append(o.getX());
		sb.append("\" oy=\"");
		sb.append(o.getY());
		sb.append("\" oz=\"");
		sb.append(o.getZ());
		sb.append("\" ow=\"");
		sb.append(o.getW());

		sb.append("\" vx=\"");
		sb.append(v.getX());
		sb.append("\" vy=\"");
		sb.append(v.getY());
		sb.append("\" vz=\"");
		sb.append(v.getZ());
		sb.append("\" vw=\"");
		sb.append(v.getW());

		sb.append("\"/>\n");
	}

	@Override
	final public boolean isGeoLine() {
		return true;
	}

	// Path3D interface

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	@Override
	public double getMaxParameter() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public double getMinParameter() {
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidCoord(double x) {
		return true;
	}

	@Override
	public final void removePointOnLine(GeoPointND p) {
		// TODO
	}

	@Override
	public boolean respectLimitedPath(double param) {
		return true;
	}

	@Override
	public void setStandardStartPoint() {
		// TODO Auto-generated method stub

	}

	/*
	 * //////////////////// // ROTATE ////////////////////
	 * 
	 * 
	 * 
	 * 
	 * public void rotate(NumberValue phiValue) {
	 * 
	 * Coords o = getCoordSys().getOrigin();
	 * 
	 * double z = o.getZ(); if (!Kernel.isZero(z)){ setUndefined(); return; }
	 * 
	 * Coords v = getCoordSys().getVx();
	 * 
	 * double vz = v.getZ(); if (!Kernel.isZero(vz)){ setUndefined(); return; }
	 * 
	 * double phi = phiValue.getDouble(); double cos = Math.cos(phi); double sin
	 * = Math.sin(phi);
	 * 
	 * double x = o.getX(); double y = o.getY(); double w = o.getW();
	 * 
	 * 
	 * Coords oRot = new Coords(x * cos - y * sin, x * sin + y * cos, z, w);
	 * 
	 * double vx = v.getX(); double vy = v.getY(); double vw = v.getW();
	 * 
	 * 
	 * Coords vRot = new Coords(vx * cos - vy * sin, vx * sin + vy * cos, vz,
	 * vw);
	 * 
	 * setCoord(oRot, vRot);
	 * 
	 * }
	 * 
	 * final public void rotate(NumberValue phiValue, GeoPoint Q) {
	 * 
	 * Coords o = getCoordSys().getOrigin();
	 * 
	 * double z = o.getZ(); if (!Kernel.isZero(z)){ setUndefined(); return; }
	 * 
	 * Coords v = getCoordSys().getVx();
	 * 
	 * double vz = v.getZ(); if (!Kernel.isZero(vz)){ setUndefined(); return; }
	 * 
	 * double phi = phiValue.getDouble(); double cos = Math.cos(phi); double sin
	 * = Math.sin(phi);
	 * 
	 * double x = o.getX(); double y = o.getY(); double w = o.getW();
	 * 
	 * double qx = w * Q.getInhomX(); double qy = w * Q.getInhomY();
	 * 
	 * Coords oRot = new Coords((x - qx) * cos + (qy - y) * sin + qx, (x - qx) *
	 * sin + (y - qy) * cos + qy, z, w);
	 * 
	 * double vx = v.getX(); double vy = v.getY(); double vw = v.getW();
	 * 
	 * 
	 * Coords vRot = new Coords(vx * cos - vy * sin, vx * sin + vy * cos, vz,
	 * vw);
	 * 
	 * setCoord(oRot, vRot);
	 * 
	 * 
	 * }
	 * 
	 * final private void rotate(NumberValue phiValue, Coords o1, Coords vn) {
	 * 
	 * if (vn.isZero()){ setUndefined(); return; } Coords vn2 = vn.normalized();
	 * 
	 * 
	 * Coords point = getCoordSys().getOrigin(); Coords s =
	 * point.projectLine(o1, vn)[0]; //point projected on the axis
	 * 
	 * Coords v1 = point.sub(s); //axis->point of the line
	 * 
	 * 
	 * Coords v = getCoordSys().getVx(); //direction of the line
	 * 
	 * 
	 * double phi = phiValue.getDouble(); double cos = Math.cos(phi); double sin
	 * = Math.sin(phi);
	 * 
	 * // new line origin Coords v2 = vn2.crossProduct4(v1); Coords oRot =
	 * s.add(v1.mul(cos)).add(v2.mul(sin));
	 * 
	 * // new line direction v2 = vn2.crossProduct4(v); v1 =
	 * v2.crossProduct4(vn2); Coords vRot =
	 * v1.mul(cos).add(v2.mul(sin)).add(vn2.mul(v.dotproduct(vn2)));
	 * 
	 * 
	 * setCoord(oRot, vRot); }
	 * 
	 * 
	 * 
	 * public void rotate(NumberValue phiValue, GeoPointND S, GeoDirectionND
	 * orientation) {
	 * 
	 * Coords o1 = S.getInhomCoordsInD3();
	 * 
	 * Coords vn = orientation.getDirectionInD3();
	 * 
	 * rotate(phiValue, o1, vn); }
	 * 
	 * public void rotate(NumberValue phiValue, GeoLineND line) {
	 * 
	 * Coords o1 = line.getStartInhomCoords(); Coords vn =
	 * line.getDirectionInD3();
	 * 
	 * 
	 * rotate(phiValue, o1, vn);
	 * 
	 * }
	 */

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_BOUNDARY;
	}

	@Override
	public boolean isParametric() {
		return true;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}

	@Override
	public Boolean isCongruent(GeoElement geo) {
		return geo.isGeoLine();
	}
}
