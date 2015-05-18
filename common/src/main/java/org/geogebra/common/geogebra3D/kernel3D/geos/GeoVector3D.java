package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.SpreadsheetTraceable;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * 3D vector class
 * 
 * @author ggb3D
 * 
 */
public class GeoVector3D extends GeoVec4D implements GeoVectorND,
		Vector3DValue, SpreadsheetTraceable, RotateableND, Traceable,
		MirrorableAtPlane, Transformable, Dilateable, MatrixTransformable,
		CoordStyle {

	private GeoPointND startPoint;

	private CoordMatrix matrix;

	private Coords labelPosition = new Coords(0, 0, 0, 0);

	/**
	 * simple constructor
	 * 
	 * @param c
	 */
	public GeoVector3D(Construction c) {
		super(c);
		matrix = new CoordMatrix(4, 2);
		setCartesian3D();
	}

	/**
	 * simple constructor with (x,y,z) coords
	 * 
	 * @param c
	 * @param x
	 * @param y
	 * @param z
	 */
	public GeoVector3D(Construction c, double x, double y, double z) {
		super(c, x, y, z, 0);
		matrix = new CoordMatrix(4, 2);
		setCartesian3D();
	}

	@Override
	public void setCoords(double[] vals) {
		super.setCoords(vals);

		if (matrix == null)
			matrix = new CoordMatrix(4, 2);

		// sets the drawing matrix
		matrix.set(getCoords(), 1);

	}

	/**
	 * update the start point position
	 */
	public void updateStartPointPosition() {

		if (startPoint != null)
			matrix.set(startPoint.getInhomCoordsInD3(), 2);
		else {
			for (int i = 1; i < 4; i++)
				matrix.set(i, 2, 0.0);
			matrix.set(4, 2, 1.0);
		}

		labelPosition = matrix.getOrigin().add(matrix.getVx().mul(0.5));
	}

	@Override
	public Coords getLabelPosition() {
		return labelPosition;
	}

	@Override
	public GeoElement copy() {
		GeoVector3D ret = new GeoVector3D(getConstruction());
		ret.set(this);
		return ret;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.VECTOR3D;
	}

	@Override
	public boolean isDefined() {
		return (!(Double.isNaN(getX()) || Double.isNaN(getY())
				|| Double.isNaN(getZ()) || Double.isNaN(getW())));
	}

	@Override
	public boolean isEqual(GeoElement geo) {

		if (!geo.isGeoVector())
			return false;

		GeoVectorND v = (GeoVectorND) geo;

		if (!(isFinite() && v.isFinite()))
			return false;

		Coords c1 = getCoords();
		Coords c2 = v.getCoordsInD3();

		return Kernel.isEqual(c1.getX(), c2.getX())
				&& Kernel.isEqual(c1.getY(), c2.getY())
				&& Kernel.isEqual(c1.getZ(), c2.getZ());

	}

	@Override
	final public boolean isInfinite() {
		Coords v = getCoords();
		return Double.isInfinite(v.getX()) || Double.isInfinite(v.getY())
				|| Double.isInfinite(v.getZ());
	}

	final public boolean isFinite() {
		return !isInfinite();
	}

	@Override
	public void set(GeoElement geo) {
		if (geo.isGeoVector()) {
			GeoVectorND vec = (GeoVectorND) geo;
			setCoords(vec.getCoordsInD3().get());

			// don't set start point for macro output
			// see AlgoMacro.initRay()
			if (geo.cons != cons && isAlgoMacroOutput())
				return;

			try {
				GeoPointND sp = vec.getStartPoint();
				if (sp != null) {
					if (vec.hasAbsoluteLocation()) {
						// create new location point
						setStartPoint(sp.copy());
					} else {
						// take existing location point
						setStartPoint(sp);
					}
				}
			} catch (CircularDefinitionException e) {
				App.debug("set GeoVector3D: CircularDefinitionException");
			}
		}
	}

	@Override
	public void setUndefined() {
		setCoords(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined() && !isInfinite();
	}

	@Override
	public boolean evaluatesTo3DVector() {
		return true;
	}

	// for properties panel
	@Override
	public boolean isPath() {
		return true;
	}

	@Override
	public boolean isGeoVector() {
		return true;
	}

	// /////////////////////////////////////////////
	// TO STRING
	// /////////////////////////////////////////////

	@Override
	final public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);

		switch (tpl.getCoordStyle(kernel.getCoordStyle())) {
		case Kernel.COORD_STYLE_FRENCH:
			// no equal sign
			sbToString.append(": ");

		case Kernel.COORD_STYLE_AUSTRIAN:
			// no equal sign
			break;

		default:
			sbToString.append(" = ");
		}

		sbToString.append(buildValueString(tpl));
		return sbToString.toString();
	}

	private StringBuilder sbToString = new StringBuilder(50);

	@Override
	final public String toValueString(StringTemplate tpl) {
		return buildValueString(tpl).toString();
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		return true;
	}

	private StringBuilder buildValueString(StringTemplate tpl) {
		sbBuildValueString.setLength(0);

		switch (tpl.getStringType()) {
		case GIAC:
			sbBuildValueString.append("ggbvect[");
			sbBuildValueString.append(kernel.format(getX(), tpl));
			sbBuildValueString.append(',');
			sbBuildValueString.append(kernel.format(getY(), tpl));
			sbBuildValueString.append(',');
			sbBuildValueString.append(kernel.format(getZ(), tpl));
			sbBuildValueString.append("]");
			return sbBuildValueString;

		default: // continue below
		}

		/*
		 * switch (toStringMode) {
		 * 
		 * 
		 * case AbstractKernel.COORD_POLAR: sbBuildValueString.append("(");
		 * sbBuildValueString.append(kernel.format(GeoVec2D.length(x, y)));
		 * sbBuildValueString.append("; ");
		 * sbBuildValueString.append(kernel.formatAngle(Math.atan2(y, x)));
		 * sbBuildValueString.append(")"); break;
		 * 
		 * case AbstractKernel.COORD_COMPLEX:
		 * sbBuildValueString.append(kernel.format(x));
		 * sbBuildValueString.append(" ");
		 * sbBuildValueString.append(kernel.formatSigned(y));
		 * sbBuildValueString.append("i"); break;
		 * 
		 * default: // CARTESIAN sbBuildValueString.append("(");
		 * sbBuildValueString.append(kernel.format(x)); switch
		 * (kernel.getCoordStyle()) { case AbstractKernel.COORD_STYLE_AUSTRIAN:
		 * sbBuildValueString.append(" | "); break;
		 * 
		 * default: sbBuildValueString.append(", "); }
		 * sbBuildValueString.append(kernel.format(y));
		 * sbBuildValueString.append(")"); break; }
		 */

		sbBuildValueString.append("(");
		sbBuildValueString.append(kernel.format(getX(), tpl));
		setCoordSep(tpl);
		sbBuildValueString.append(kernel.format(getY(), tpl));
		setCoordSep(tpl);
		sbBuildValueString.append(kernel.format(getZ(), tpl));
		sbBuildValueString.append(")");

		return sbBuildValueString;
	}

	private void setCoordSep(StringTemplate tpl) {
		switch (tpl.getCoordStyle(kernel.getCoordStyle())) {
		case Kernel.COORD_STYLE_AUSTRIAN:
			sbBuildValueString.append(" | ");
			break;

		default:
			sbBuildValueString.append(", ");
		}
	}

	private StringBuilder sbBuildValueString = new StringBuilder(50);

	private StringBuilder sb;

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);

		if (getMode() == Kernel.COORD_CARTESIAN_3D) {
			GeoVector.buildLatexValueStringCoordCartesian3D(kernel, tpl,
					getX(), getY(), getZ(), sb, this, symbolic);
			return sb.toString();
		}

		if (getMode() == Kernel.COORD_SPHERICAL) {
			GeoPoint.buildValueStringCoordSpherical(kernel, tpl, getX(),
					getY(), getZ(), sb);
			return sb.toString();
		}

		// cartesian 2D / polar / complex not possible
		if (!Kernel.isZero(getZ())) {
			if (getMode() == Kernel.COORD_POLAR) {
				GeoPoint.buildValueStringCoordSpherical(kernel, tpl, getX(),
						getY(), getZ(), sb);
			} else {
				GeoVector.buildLatexValueStringCoordCartesian3D(kernel, tpl,
						getX(), getY(), getZ(), sb, this, symbolic);
			}
			return sb.toString();
		}

		// cartesian 2D / polar / complex are possible
		return GeoVector.buildLatexString(kernel, sb, symbolic, tpl,
				toStringMode, getX(), getY(), this);

	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// polar or cartesian coords
		switch (toStringMode) {
		case Kernel.COORD_POLAR:
			sb.append("\t<coordStyle style=\"polar\"/>\n");
			break;

		case Kernel.COORD_COMPLEX:
			sb.append("\t<coordStyle style=\"complex\"/>\n");
			break;

		case Kernel.COORD_CARTESIAN:
			sb.append("\t<coordStyle style=\"cartesian\"/>\n");
			break;

		case Kernel.COORD_SPHERICAL:
			sb.append("\t<coordStyle style=\"spherical\"/>\n");
			break;

		default:
			// don't save default (Kernel.COORD_CARTESIAN_3D)
		}

		// line thickness and type
		getLineStyleXML(sb);

		// startPoint of vector
		if (startPoint != null) {
			sb.append(startPoint.getStartPointXML());
		}

	}

	// /////////////////////////////////////////////
	// LOCATEABLE INTERFACE
	// /////////////////////////////////////////////

	public GeoPointND getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {

		// Application.debug("point : "+((GeoElement) pI).getLabel());

		// GeoPoint3D p = (GeoPoint3D) pI;

		if (startPoint == p)
			return;

		// macro output uses initStartPoint() only
		if (isAlgoMacroOutput())
			return;

		// check for circular definition
		if (isParentOf(p))
			throw new CircularDefinitionException();

		// remove old dependencies
		if (startPoint != null)
			startPoint.getLocateableList().unregisterLocateable(this);

		// set new location
		startPoint = p;

		// add new dependencies
		if (startPoint != null)
			startPoint.getLocateableList().registerLocateable(this);

		// update position matrix
		// updateStartPointPosition();

	}

	public GeoPointND[] getStartPoints() {
		if (startPoint == null)
			return null;

		GeoPointND[] ret = new GeoPointND[1];
		ret[0] = startPoint;
		return ret;
	}

	public boolean hasAbsoluteLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}

	public void initStartPoint(GeoPointND p, int number) {
		startPoint = p;

	}

	public boolean isAlwaysFixed() {
		return false;
	}

	public void removeStartPoint(GeoPointND p) {
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch (Exception e) {
				// ignore circular definition here
			}
		}

	}

	public void setStartPoint(GeoPointND p, int number)
			throws CircularDefinitionException {
		setStartPoint(p);

	}

	public void setWaitForStartPoint() {
		// TODO Auto-generated method stub

	}

	public Geo3DVec getVector() {
		return new Geo3DVec(kernel, v.getX(), v.getY(), v.getZ());
	}

	public double[] getPointAsDouble() {
		double[] ret = { v.getX(), v.getY(), v.getZ() };
		return ret;
	}

	public Coords getCoordsInD2() {
		Coords ret = new Coords(3);
		ret.setValues(v, 3);
		return ret;
	}

	public Coords getCoordsInD3() {
		Coords ret = new Coords(4);
		ret.setValues(v, 4);
		return ret;

	}

	private boolean trace;

	@Override
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}

	public Coords getDirectionInD3() {
		return getCoordsInD3();
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	public void getInhomCoords(double[] coords) {
		coords[0] = v.getX();
		coords[1] = v.getY();
		coords[2] = v.getZ();
	}

	public double[] getInhomCoords() {
		double[] coords = new double[3];
		getInhomCoords(coords);
		return coords;
	}

	@Override
	public void updateColumnHeadingsForTraceValues() {
		resetSpreadsheetColumnHeadings();

		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getXBracket(), // "x("
				Operation.PLUS, new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS, kernel.getAlgebraProcessor()
								.getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getYBracket(), // "y("
				Operation.PLUS, new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS, kernel.getAlgebraProcessor()
								.getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getZBracket(), // "z("
				Operation.PLUS, new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS, kernel.getAlgebraProcessor()
								.getCloseBracket())))); // ")"

	}

	@Override
	public TraceModesEnum getTraceModes() {
		return TraceModesEnum.SEVERAL_VALUES_OR_COPY;
	}

	@Override
	public String getTraceDialogAsValues() {
		String name = getLabelTextOrHTML(false);

		StringBuilder sb1 = new StringBuilder();
		sb1.append("x(");
		sb1.append(name);
		sb1.append("), y(");
		sb1.append(name);
		sb1.append("), z(");
		sb1.append(name);
		sb1.append(")");

		return sb1.toString();
	}

	@Override
	public void addToSpreadsheetTraceList(
			ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric xx = new GeoNumeric(cons, v.getX());
		spreadsheetTraceList.add(xx);
		GeoNumeric yy = new GeoNumeric(cons, v.getY());
		spreadsheetTraceList.add(yy);
		GeoNumeric zz = new GeoNumeric(cons, v.getZ());
		spreadsheetTraceList.add(zz);
	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	public void updateLocation() {
		updateGeo();
		kernel.notifyUpdateLocation(this);
	}

	final public void rotate(NumberValue phiValue) {

		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x = getX();
		double y = getY();
		double z = getZ();

		setCoords(x * cos - y * sin, x * sin + y * cos, z, getW());
	}

	final public void rotate(NumberValue phiValue, GeoPointND Q) {

		rotate(phiValue);

	}

	public void rotate(NumberValue phiValue, GeoPointND S,
			GeoDirectionND orientation) {

		Coords o1 = S.getInhomCoordsInD3();
		Coords vn = orientation.getDirectionInD3();

		rotate(phiValue, o1, vn);

	}

	private void rotate(NumberValue phiValue, Coords o1, Coords vn) {

		if (vn.isZero()) {
			setUndefined();
			return;
		}

		// Coords v = getCoordsInD3();

		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		Coords vn2 = vn.normalized();
		Coords v2 = vn2.crossProduct4(v);
		Coords v1 = v2.crossProduct4(vn2);
		setCoords(v1.mul(cos).add(v2.mul(sin)).add(vn2.mul(v.dotproduct(vn2))));

	}

	public void rotate(NumberValue phiValue, GeoLineND line) {

		Coords o1 = line.getStartInhomCoords();
		Coords vn = line.getDirectionInD3();

		rotate(phiValue, o1, vn);

	}

	public void mirror(Coords Q) {

		setCoords(v.mul(-1));

	}

	public void mirror(GeoLineND line) {

		Coords vn = line.getDirectionInD3().normalized();
		setCoords(vn.mul(2 * v.dotproduct(vn)).add(v.mul(-1)));

	}

	public void mirror(GeoCoordSys2D plane) {

		Coords vn = plane.getDirectionInD3().normalized();
		setCoords(v.add(vn.mul(-2 * v.dotproduct(vn))));

	}

	// //////////////////////
	// DILATE
	// //////////////////////

	public void dilate(NumberValue rval, Coords S) {

		setCoords(v.mul(rval.getDouble()));
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	public void matrixTransform(double a, double b, double c, double d) {

		double x = getX();
		double y = getY();

		Double x1 = a * x + b * y;
		Double y1 = c * x + d * y;

		setCoords(x1, y1, getZ(), getW());
	}

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		double x = getX();
		double y = getY();
		double z = getZ();

		double x1 = a00 * x + a01 * y + a02 * z;
		double y1 = a10 * x + a11 * y + a12 * z;
		double z1 = a20 * x + a21 * y + a22 * z;

		setCoords(x1, y1, z1, getW());

	}

	public void setCartesian() {
		setMode(Kernel.COORD_CARTESIAN);
	}

	public void setCartesian3D() {
		setMode(Kernel.COORD_CARTESIAN_3D);
	}

	public void setSpherical() {
		setMode(Kernel.COORD_SPHERICAL);
	}

	public void setPolar() {
		setMode(Kernel.COORD_POLAR);
	}

	public void setComplex() {
		setMode(Kernel.COORD_COMPLEX);
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_BOUNDARY;
	}
	
	
	@Override
	protected boolean moveVector(Coords rwTransVec, Coords endPosition) {

		boolean movedGeo = false;

		if (endPosition != null) {
			// setCoords(endPosition.x, endPosition.y, 1);
			// movedGeo = true;
		}

		// translate point
		else {

			Coords coords;
			Coords current = getCoords();

			if (current.getLength() < rwTransVec.getLength()) {
				coords = current.add(rwTransVec);
			} else {
				coords = current.addSmaller(rwTransVec);
			}
			setCoords(coords);

			movedGeo = true;
		}

		return movedGeo;

	}
}
