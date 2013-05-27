package geogebra3D.kernel3D;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoDependentVector;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic3D.Vector3DValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.SpreadsheetTraceable;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.kernel.kernelND.RotateableND;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;

import java.util.ArrayList;


/**
 * 3D vector class
 * 
 * @author ggb3D
 * 
 */
public class GeoVector3D extends GeoVec4D implements GeoVectorND,
		Vector3DValue, SpreadsheetTraceable, RotateableND {

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
			matrix.set(startPoint.getInhomCoordsInD(3), 2);
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
		Coords c2 = v.getCoordsInD(3);

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
			GeoVectorND v = (GeoVectorND) geo;
			setCoords(v.getCoordsInD(3).get());
			try {// TODO see GeoVector
				setStartPoint(v.getStartPoint());
			} catch (CircularDefinitionException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setUndefined() {
		setCoords(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
	}

	@Override
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isVector3DValue() {
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

	private StringBuilder buildValueString(StringTemplate tpl) {
		sbBuildValueString.setLength(0);

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
		if(tpl.hasType(StringType.MPREDUCE)){
				sbBuildValueString.append("myvect");				
		}
		sbBuildValueString.append("(");
		sbBuildValueString.append(kernel.format(getX(),tpl));
		setCoordSep(tpl);
		sbBuildValueString.append(kernel.format(getY(),tpl));
		setCoordSep(tpl);
		sbBuildValueString.append(kernel.format(getZ(),tpl));
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
	public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);

		String[] inputs;
		if (symbolic && getParentAlgorithm() instanceof AlgoDependentVector) {
			AlgoDependentVector algo = (AlgoDependentVector) getParentAlgorithm();
			String symbolicStr = algo.toString(tpl);
			inputs = symbolicStr.substring(1, symbolicStr.length() - 1).split(
					",");
		} else {
			inputs = new String[3];
			inputs[0] = kernel.format(getX(),tpl);
			inputs[1] = kernel.format(getY(),tpl);
			inputs[2] = kernel.format(getZ(),tpl);
		}

		boolean alignOnDecimalPoint = true;
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i].indexOf('.') == -1) {
				alignOnDecimalPoint = false;
				continue;
			}
		}

		if (alignOnDecimalPoint) {
			sb.append("\\left( \\begin{tabular}{r@{.}l}");
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = inputs[i].replace('.', '&');
			}
		} else {
			sb.append("\\left( \\begin{tabular}{r}");
		}

		for (int i = 0; i < inputs.length; i++) {
			sb.append(inputs[i]);
			sb.append(" \\\\ ");
		}

		sb.append("\\end{tabular} \\right)");
		return sb.toString();
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
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
		// TODO if (isAlgoMacroOutput()) return;

		// check for circular definition
		if (isParentOf((GeoElement) p))
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
		return startPoint == null; // TODO || startPoint.isAbsoluteStartPoint();
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

	public Geo3DVec get3DVec() {
		return new Geo3DVec(kernel, v.getX(), v.getY(), v.getZ());
	}

	public double[] getPointAsDouble() {
		double[] ret = { v.getX(), v.getY(), v.getZ() };
		return ret;
	}

	public Coords getCoordsInD(int dimension) {
		Coords ret = new Coords(dimension + 1);
		switch (dimension) {
		case 3:
			ret.setW(getW());
		case 2:
			ret.setX(getX());
			ret.setY(getY());
			ret.setZ(getZ());
		}
		return ret;
	}

	public boolean getTrace() {
		// TODO Auto-generated method stub
		return false;
	}

	public Coords getDirectionInD3() {
		return getCoordsInD(3);
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
	public void updateColumnHeadingsForTraceValues(){
		resetSpreadsheetColumnHeadings();

		spreadsheetColumnHeadings.add(
				getColumnHeadingText(
						new ExpressionNode(kernel,
								getXBracket(), // "x("
								Operation.PLUS, 
								new ExpressionNode(kernel,
										getNameGeo(), // Name[this]
										Operation.PLUS, 
										getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(
				getColumnHeadingText(
						new ExpressionNode(kernel,
								getYBracket(), // "y("
								Operation.PLUS, 
								new ExpressionNode(kernel,
										getNameGeo(), // Name[this]
										Operation.PLUS, 
										getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(
				getColumnHeadingText(
						new ExpressionNode(kernel,
								getZBracket(), // "z("
								Operation.PLUS, 
								new ExpressionNode(kernel,
										getNameGeo(), // Name[this]
										Operation.PLUS, 
										getCloseBracket())))); // ")"
		

	}
	

	@Override
	public TraceModesEnum getTraceModes(){
		return TraceModesEnum.SEVERAL_VALUES_OR_COPY;
	}
	
	@Override
	public String getTraceDialogAsValues(){
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
	public void addToSpreadsheetTraceList(ArrayList<GeoNumeric> spreadsheetTraceList) {
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

	final public void rotate(NumberValue phiValue, GeoPoint Q) {

		rotate(phiValue);
		
	}

	public void rotate(NumberValue phiValue, GeoPointND S, GeoDirectionND orientation){
		
		Coords o1 = S.getInhomCoordsInD(3);
		Coords vn = orientation.getDirectionInD3();
		
		
		rotate(phiValue, o1, vn);
		
	}
	
	private void rotate(NumberValue phiValue, Coords o1, Coords vn){
		
		if (vn.isZero()){
			setUndefined();
			return;
		}
		
		//Coords v = getCoordsInD(3);		
		
		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		
		Coords vn2 = vn.normalized();
		Coords v2 = vn2.crossProduct4(v);
		Coords v1 = v2.crossProduct4(vn2);
		setCoords(v1.mul(cos).add(v2.mul(sin)).add(vn2.mul(v.dotproduct(vn2))));
		
	}
	
	public void rotate(NumberValue phiValue, GeoLineND line){
		
		Coords o1 = line.getStartInhomCoords();
		Coords vn = line.getDirectionInD3();
		
		
		rotate(phiValue, o1, vn);
		

	}

}
