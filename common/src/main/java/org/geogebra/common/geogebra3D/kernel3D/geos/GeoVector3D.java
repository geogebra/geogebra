package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.VectorToMatrix;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 3D vector class
 * 
 * @author ggb3D
 * 
 */
public class GeoVector3D extends GeoVec4D
		implements GeoVectorND, RotateableND, 
		MirrorableAtPlane, Transformable, Dilateable, MatrixTransformable {

	private GeoPointND startPoint;

	private CoordMatrix matrix;

	private Coords labelPosition = new Coords(0, 0, 0, 0);

	private StringBuilder sbBuildValueString = new StringBuilder(50);
	private StringBuilder sbToString = new StringBuilder(50);

	private StringBuilder sb;

	private boolean trace;
	private VectorToMatrix converter = null;

	/**
	 * simple constructor
	 * 
	 * @param c
	 *            construction
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
	 *            construction
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public GeoVector3D(Construction c, double x, double y, double z) {
		super(c, x, y, z, 0);
		matrix = new CoordMatrix(4, 2);
		setCartesian3D();
	}

	@Override
	public void setCoords(double[] vals) {
		super.setCoords(vals);

		if (matrix == null) {
			matrix = new CoordMatrix(4, 2);
		}

		// sets the drawing matrix
		matrix.set(getCoords(), 1);

	}

	/**
	 * update the start point position
	 */
	@Override
	public void updateStartPointPosition() {

		if (startPoint != null) {
			matrix.set(startPoint.getInhomCoordsInD3(), 2);
		} else {
			for (int i = 1; i < 4; i++) {
				matrix.set(i, 2, 0.0);
			}
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
	public boolean isEqual(GeoElementND geo) {

		if (!geo.isGeoVector()) {
			return false;
		}

		GeoVectorND v1 = (GeoVectorND) geo;

		if (!(isFinite() && v1.isFinite())) {
			return false;
		}

		Coords c1 = getCoords();
		Coords c2 = v1.getCoordsInD3();

		return DoubleUtil.isEqual(c1.getX(), c2.getX())
				&& DoubleUtil.isEqual(c1.getY(), c2.getY())
				&& DoubleUtil.isEqual(c1.getZ(), c2.getZ());

	}

	@Override
	final public boolean isInfinite() {
		Coords v1 = getCoords();
		return Double.isInfinite(v1.getX()) || Double.isInfinite(v1.getY())
				|| Double.isInfinite(v1.getZ());
	}

	@Override
	final public boolean isFinite() {
		return !isInfinite();
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo.isGeoPoint()) {
			GeoPointND p = (GeoPointND) geo;
			setCoords(p.getCoordsInD3().get());
		} else if (geo.isGeoVector()) {
			GeoVectorND vec = (GeoVectorND) geo;
			setCoords(vec.getCoordsInD3().get());
			reuseDefinition(geo);

			// don't set start point for macro output
			// see AlgoMacro.initRay()
			if (geo.getConstruction() != cons && isAlgoMacroOutput()) {
				return;
			}

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
				Log.debug("set GeoVector3D: CircularDefinitionException");
			}
		}
	}

	@Override
	public void setUndefined() {
		setCoords(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
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
			break;

		case Kernel.COORD_STYLE_AUSTRIAN:
			// no equal sign
			break;

		default:
			sbToString.append(" = ");
		}

		sbToString.append(buildValueString(tpl));
		return sbToString.toString();
	}

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
		if (tpl.getStringType() == ExpressionNodeConstants.StringType.GIAC) {
			sbBuildValueString.append("ggbvect[");
			sbBuildValueString.append(kernel.format(getX(), tpl));
			sbBuildValueString.append(',');
			sbBuildValueString.append(kernel.format(getY(), tpl));
			sbBuildValueString.append(',');
			sbBuildValueString.append(kernel.format(getZ(), tpl));
			sbBuildValueString.append("]");
			return sbBuildValueString;
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
		if (tpl.getCoordStyle(kernel.getCoordStyle()) == Kernel.COORD_STYLE_AUSTRIAN) {
			sbBuildValueString.append(" | ");
		} else {
			sbBuildValueString.append(", ");
		}
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		resetStringBuilder();

		if (getToStringMode() == Kernel.COORD_CARTESIAN_3D) {
			GeoVector.buildLatexValueStringCoordCartesian3D(kernel, tpl, getX(),
					getY(), getZ(), sb, this, symbolic);
			return sb.toString();
		}

		if (getToStringMode() == Kernel.COORD_SPHERICAL) {
			GeoPoint.buildValueStringCoordSpherical(kernel, tpl, getX(), getY(),
					getZ(), sb);
			return sb.toString();
		}

		// cartesian 2D / polar / complex not possible
		if (!DoubleUtil.isZero(getZ())) {
			if (getToStringMode() == Kernel.COORD_POLAR) {
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
				getToStringMode(), getX(), getY(), this);

	}

	private void resetStringBuilder() {
		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sbXml) {
		super.getXMLtags(sbXml);

		// polar or cartesian coords
		switch (getToStringMode()) {
		case Kernel.COORD_POLAR:
			sbXml.append("\t<coordStyle style=\"polar\"/>\n");
			break;

		case Kernel.COORD_COMPLEX:
			sbXml.append("\t<coordStyle style=\"complex\"/>\n");
			break;

		case Kernel.COORD_CARTESIAN:
			sbXml.append("\t<coordStyle style=\"cartesian\"/>\n");
			break;

		case Kernel.COORD_SPHERICAL:
			sbXml.append("\t<coordStyle style=\"spherical\"/>\n");
			break;

		default:
			// don't save default (Kernel.COORD_CARTESIAN_3D)
		}

		// line thickness and type
		getLineStyleXML(sbXml);

		// startPoint of vector
		if (startPoint != null) {
			startPoint.appendStartPointXML(sbXml);
		}

	}

	// /////////////////////////////////////////////
	// LOCATEABLE INTERFACE
	// /////////////////////////////////////////////

	@Override
	public GeoPointND getStartPoint() {
		return startPoint;
	}

	@Override
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {

		// Application.debug("point : "+((GeoElement) pI).getLabel());

		// GeoPoint3D p = (GeoPoint3D) pI;

		if (startPoint == p) {
			return;
		}

		// macro output uses initStartPoint() only
		if (isAlgoMacroOutput()) {
			return;
		}

		// check for circular definition
		if (isParentOf(p)) {
			throw new CircularDefinitionException();
		}

		// remove old dependencies
		if (startPoint != null) {
			startPoint.getLocateableList().unregisterLocateable(this);
		}

		// set new location
		startPoint = p;

		// add new dependencies
		if (startPoint != null) {
			startPoint.getLocateableList().registerLocateable(this);
		}

		// update position matrix
		// updateStartPointPosition();

	}

	@Override
	public GeoPointND[] getStartPoints() {
		if (startPoint == null) {
			return null;
		}

		GeoPointND[] ret = new GeoPointND[1];
		ret[0] = startPoint;
		return ret;
	}

	@Override
	public boolean hasAbsoluteLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}

	@Override
	public void initStartPoint(GeoPointND p, int number) {
		startPoint = p;

	}

	@Override
	public boolean isAlwaysFixed() {
		return false;
	}

	@Override
	public void removeStartPoint(GeoPointND p) {
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch (Exception e) {
				// ignore circular definition here
			}
		}

	}

	@Override
	public void setStartPoint(GeoPointND p, int number)
			throws CircularDefinitionException {
		setStartPoint(p);

	}

	@Override
	public void setWaitForStartPoint() {
		// TODO Auto-generated method stub

	}

	@Override
	public Geo3DVec getVector() {
		return new Geo3DVec(kernel, v.getX(), v.getY(), v.getZ());
	}

	@Override
	public double[] getPointAsDouble() {
		return new double[] { v.getX(), v.getY(), v.getZ() };
	}

	@Override
	public Coords getCoordsInD2() {
		Coords ret = new Coords(3);
		ret.setValues(v, 3);
		return ret;
	}

	@Override
	public Coords getCoordsInD3() {
		Coords ret = new Coords(4);
		ret.setValues(v, 4);
		return ret;
	}

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	@Override
	public Coords getDirectionInD3() {
		return getCoordsInD3();
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	@Override
	public void getInhomCoords(double[] coords) {
		coords[0] = v.getX();
		coords[1] = v.getY();
		coords[2] = v.getZ();
	}

	@Override
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
				Operation.PLUS,
				new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS,
						kernel.getAlgebraProcessor().getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getYBracket(), // "y("
				Operation.PLUS,
				new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS,
						kernel.getAlgebraProcessor().getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getZBracket(), // "z("
				Operation.PLUS,
				new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS,
						kernel.getAlgebraProcessor().getCloseBracket())))); // ")"

	}

	@Override
	public TraceModesEnum getTraceModes() {
		return TraceModesEnum.SEVERAL_VALUES_OR_COPY;
	}

	@Override
	public String getTraceDialogAsValues() {
		String name = getLabelTextOrHTML(false);
		return "x(" +	name +	"), y(" + name + "), z(" +	name +	")";
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

	@Override
	public void updateLocation() {
		updateGeo(false);
		kernel.notifyUpdateLocation(this);
	}

	@Override
	final public void rotate(NumberValue phiValue) {

		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x = getX();
		double y = getY();
		double z = getZ();

		setCoords(x * cos - y * sin, x * sin + y * cos, z, getW());
	}

	@Override
	final public void rotate(NumberValue phiValue, GeoPointND Q) {
		rotate(phiValue);
	}

	@Override
	public void rotate(NumberValue phiValue, GeoPointND S,
			GeoDirectionND orientation) {

		// origin ignored
		Coords vn = orientation.getDirectionInD3();

		rotate(phiValue, vn);

	}

	private void rotate(NumberValue phiValue, Coords vn) {

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

	@Override
	public void rotate(NumberValue phiValue, GeoLineND line) {

		// origin ignored
		Coords vn = line.getDirectionInD3();

		rotate(phiValue, vn);

	}

	@Override
	public void mirror(Coords Q) {

		setCoords(v.mul(-1));

	}

	@Override
	public void mirror(GeoLineND line) {

		Coords vn = line.getDirectionInD3().normalized();
		setCoords(vn.mul(2 * v.dotproduct(vn)).add(v.mul(-1)));

	}

	@Override
	public void mirror(GeoCoordSys2D plane) {

		Coords vn = plane.getDirectionInD3().normalized();
		setCoords(v.add(vn.mul(-2 * v.dotproduct(vn))));

	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {

		setCoords(v.mul(rval.getDouble()));
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public void matrixTransform(double a, double b, double c, double d) {
		double x = getX();
		double y = getY();

		double x1 = a * x + b * y;
		double y1 = c * x + d * y;

		setCoords(x1, y1, getZ(), getW());
	}

	@Override
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

	@Override
	public void setCartesian() {
		setMode(Kernel.COORD_CARTESIAN);
	}

	@Override
	public void setCartesian3D() {
		setMode(Kernel.COORD_CARTESIAN_3D);
	}

	@Override
	public void setSpherical() {
		setMode(Kernel.COORD_SPHERICAL);
	}

	@Override
	public void setPolar() {
		setMode(Kernel.COORD_POLAR);
	}

	@Override
	public void setComplex() {
		setMode(Kernel.COORD_COMPLEX);
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

	@Override
	public ValueType getValueType() {
		return ValueType.VECTOR3D;
	}

	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	public ValidExpression toValidExpression() {
		return getVector();
	}

	@Override
	public String toValueStringAsColumnVector(StringTemplate tpl) {
		return buildColumnVectorValueString(tpl);
	}

	private String buildColumnVectorValueString(StringTemplate tpl) {
		if (getToStringMode() != Kernel.COORD_CARTESIAN
				&& getToStringMode() != Kernel.COORD_CARTESIAN_3D) {
			return buildValueString(tpl).toString();
		}
		return getConverter().build(tpl, getDefinition(), getX(), getY(), getZ());
	}

	private VectorToMatrix getConverter() {
		if (converter == null) {
			converter = new VectorToMatrix(kernel);
		}
		return converter;
	}
}
