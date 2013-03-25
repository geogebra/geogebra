/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoVector.java
 *
 * The vector (x,y) has homogenous coordinates (x,y,0)
 *
 * Created on 30. August 2001, 17:39
 */

package geogebra.common.kernel.geos;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MatrixTransformable;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.PathMoverGeneric;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoDependentVector;
import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.common.util.MyMath;
import geogebra.common.util.Unicode;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author  Markus
 */
final public class GeoVector extends GeoVec3D
implements Path, VectorValue, Translateable, PointRotateable, Mirrorable, Dilateable, MatrixTransformable, 
Transformable, GeoVectorND, SpreadsheetTraceable, SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private GeoPoint startPoint;

	// for path interface we use a segment
	private GeoSegment pathSegment;
	private GeoPoint pathStartPoint, pathEndPoint;
	private boolean waitingForStartPoint = false;
	private HashSet<GeoPointND> waitingPointSet;

	/** Creates new GeoVector 
	 * @param c construction
	 */
	public GeoVector(Construction c) {
		super(c); 
		setConstructionDefaults();
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.VECTOR;
	}   

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	/** Creates new GeoVector 
	 * @param c construction
	 * @param label label
	 * @param x x-coord
	 * @param y y-coord
	 * @param z z-coord*/
	public GeoVector(Construction c, String label, double x, double y, double z) {
		super(c, x, y, z); // GeoVec3D constructor 
		setConstructionDefaults();
		setLabel(label); 
		//setEuclidianVisible(false);
	}

	/**
	 * Copy constructor
	 * @param vector vector to copy
	 */
	public GeoVector(GeoVector vector) {
		this(vector.cons);
		set(vector);
		//setEuclidianVisible(false);
	}

	@Override
	final public void setCoords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}     

	final public void setCoords(double[] c) {
		setCoords(c[0],c[1],c[2]);
	}  

	@Override
	final public void setCoords(GeoVec3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
	} 

	@Override
	public void set(GeoElement geo) {
		super.set(geo);	
		if (!geo.isGeoVector()) return;

		GeoVector vec = (GeoVector) geo;		

		// don't set start point for macro output
		// see AlgoMacro.initRay()
		if (geo.cons != cons && isAlgoMacroOutput())
			return;

		try {
			if (vec.startPoint != null) {
				if (vec.hasAbsoluteLocation()) {
					//	create new location point	
					setStartPoint(new GeoPoint(vec.startPoint));
				} else {
					//	take existing location point	
					setStartPoint(vec.startPoint);
				}
			}
		}
		catch (CircularDefinitionException e) {
			App.debug("set GeoVector: CircularDefinitionException");
		}		
	}

	@Override
	public GeoElement copy() {
		return new GeoVector(this);        
	} 

	/**
	 * @param r radius
	 * @param phi phase
	 */
	final public void setPolarCoords(double r, double phi) {
		// convert angle to radiant              
		x = r * Math.cos( phi );
		y = r * Math.sin( phi );        
		z = 0.0d;        
	}
	/**
	 * Sets coords to (x,y,0)
	 * @param v vector (x,y)
	 */
	final public void setCoords(GeoVec2D v) {
		x = v.getX();
		y = v.getY();
		z = 0.0d;
	}      

	/** Converts the homogeneous coordinates (x,y,z)
	 * of this GeoVec3D to the inhomogeneous coordinates (x/z, y/z)
	 * of a new GeoVec2D.
	 * @return vector containing inhomogeneous coords
	 */
	final public GeoVec2D getInhomVec() {
		return new GeoVec2D(kernel, x, y);
	}

	/**
	 * Retuns starting point of this vector or null.
	 */
	final public GeoPoint getStartPoint() {
		return startPoint;
	}   

	public GeoPoint [] getStartPoints() {
		if (startPoint == null)
			return null;

		GeoPoint [] ret = new GeoPoint[1];
		ret[0] = startPoint;
		return ret;			
	}

	public boolean hasAbsoluteLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}

	public void setStartPoint(GeoPointND p, int number)  throws CircularDefinitionException {
		setStartPoint(p);
	}

	/**
	 * Sets the startpoint without performing any checks.
	 * This is needed for macros.	 
	 */
	public void initStartPoint(GeoPointND p, int number) {
		startPoint = (GeoPoint) p;
	}

	public void removeStartPoint(GeoPointND p) {    
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch(Exception e) {
				//ignore circular definition here
			}
		}
	}



	public void setStartPoint(GeoPointND pI) throws CircularDefinitionException {  

		GeoPoint p = (GeoPoint) pI;

		if (startPoint == p) return;

		// macro output uses initStartPoint() only
		if (isAlgoMacroOutput()) return; 				

		// check for circular definition
		if (isParentOf(p)){
			App.debug(this+" startpoint "+p);
			//throw new CircularDefinitionException();
		}

		// remove old dependencies
		if (startPoint != null) startPoint.getLocateableList().unregisterLocateable(this);	

		// set new location	
		startPoint = p;		

		//	add new dependencies
		if (startPoint != null) startPoint.getLocateableList().registerLocateable(this);	

		// reinit path
		if (pathSegment != null) {
			initPathSegment();
		}

		// update the waiting points
		if (waitingForStartPoint) {
			waitingForStartPoint = false;

			if (waitingPointSet != null) {
				updatePathSegment();

				GeoPoint P;
				Iterator<GeoPointND> it = waitingPointSet.iterator();
				while (it.hasNext()) {
					P = (GeoPoint) it.next();
					pathSegment.pointChanged(P);					
					P.updateCoords();
				}	
			}
			waitingPointSet = null;
		}
	}

	public void setWaitForStartPoint() {
		// the startpoint should not be used as long
		// as waitingForStartPoint is true
		// This is important for points on this vector:
		// their coords should not be changed until 
		// the startPoint was finally set
		waitingForStartPoint = true;
	}

	@Override
	public void doRemove() {
		super.doRemove();
		// tell startPoint	
		if (startPoint != null) startPoint.getLocateableList().unregisterLocateable(this);
	}

	final public boolean isFinite() {
		return !isInfinite();
	}

	@Override
	final public boolean isInfinite() {
		return Double.isInfinite(x) || Double.isInfinite(y);  
	}

	@Override
	final protected boolean showInEuclidianView() {               
		return isDefined() && !isInfinite();
	}    

	@Override
	public final boolean showInAlgebraView() {
		// independent or defined
		// return isIndependent() || isDefined();
		return true;
	}    

	/** 
	 * Yields true if the coordinates of this vector are equal to
	 * those of vector v. Infinite points are checked for linear dependency.
	 */
	// Michael Borcherds 2008-05-01
	@Override
	final public boolean isEqual(GeoElement geo) {        

		if (!geo.isGeoVector()) return false;

		GeoVector v = (GeoVector)geo;

		if (!(isFinite() && v.isFinite())) return false;
		return Kernel.isEqual(x, v.x) && Kernel.isEqual(y, v.y);                                            
	}


	/***********************************************************
	 * MOVEMENTS
	 ***********************************************************/
	/**
	 * rotate this vector by angle phi around (0,0)
	 */
	final public void rotate(NumberValue phi) {    	
		rotateXY(phi);  

	}            

	/** 
	 * Called when transforming Ray[point,direction] -- doesn't do anything.
	 */
	public void translate(Coords v) {
		//do nothing
	}

	public void rotate(NumberValue r, GeoPoint S) {
		rotateXY(r);
	}

	public void mirror(GeoPoint Q) {

		setCoords(- x,- y, z );

	}

	public void mirror(GeoLine g) {
		mirrorXY(2.0 * Math.atan2(-g.getX(), g.getY()));

	}

	public void dilate(NumberValue rval, GeoPoint S) {
		double r = rval.getDouble();	
		setCoords(r * x, r * y, z);

	}

	public void matrixTransform(double a,double b,double c,double d) {

		Double x1 = a*x + b*y;
		Double y1 = c*x + d*y;

		setCoords(x1, y1, z);

	}


	/*********************************************************************/   

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

		// Without toString, there was an InvocationTargetException here
		String str = buildValueString(tpl).toString();
		sbToString.append(str);

		return sbToString.toString();
	}

	@Override
	final public String toStringMinimal(StringTemplate tpl) {            
		sbToString.setLength(0);
		sbToString.append(regrFormat(x) + " " + regrFormat(y));
		return sbToString.toString();
	}

	
	private StringBuilder sbToString = new StringBuilder(50); 

	@Override
	final public String toValueString(StringTemplate tpl) {
		return buildValueString(tpl).toString();
	}

	@SuppressWarnings("cast")
	// see http://code.google.com/p/google-web-toolkit/issues/detail?id=4097
	private StringBuilder buildValueString(StringTemplate tpl) {
		sbBuildValueString.setLength(0);

		switch (tpl.getStringType()) {
		case MATH_PIPER:
			sbBuildValueString.append("{");
			sbBuildValueString.append(getInhomVec().getX());
			sbBuildValueString.append(", ");
			sbBuildValueString.append(getInhomVec().getY());
			sbBuildValueString.append("}");
			return sbBuildValueString;

		case GIAC:
			sbBuildValueString.append("[");
			sbBuildValueString.append(getInhomVec().getX());
			sbBuildValueString.append(", ");
			sbBuildValueString.append(getInhomVec().getY());
			sbBuildValueString.append("]");
			return sbBuildValueString;

		case MPREDUCE:
			sbBuildValueString.append("myvect(");
			sbBuildValueString.append(getInhomVec().getX());
			sbBuildValueString.append(",");
			sbBuildValueString.append(getInhomVec().getY());
			sbBuildValueString.append(")");
			return sbBuildValueString;

		default: // continue below
		}
		switch (toStringMode) {
		case Kernel.COORD_POLAR:                	
			sbBuildValueString.append("(");		
			sbBuildValueString.append(kernel.format(MyMath.length(x, y),tpl));
			sbBuildValueString.append("; ");
			sbBuildValueString.append((CharSequence)kernel.formatAngle(Math.atan2(y, x),tpl));
			sbBuildValueString.append(")");
			break;

		case Kernel.COORD_COMPLEX:              	
			sbBuildValueString.append(kernel.format(x,tpl));
			sbBuildValueString.append(" ");
			kernel.formatSigned(y,sbBuildValueString,tpl);
			sbBuildValueString.append(Unicode.IMAGINARY);
			break;                                

		default: // CARTESIAN
			sbBuildValueString.append("(");		
			sbBuildValueString.append(kernel.format(x,tpl));
			switch (tpl.getCoordStyle(kernel.getCoordStyle())) {
			case Kernel.COORD_STYLE_AUSTRIAN:
				sbBuildValueString.append(" | ");
				break;

			default:
				sbBuildValueString.append(", ");												
			}
			sbBuildValueString.append(kernel.format(y,tpl));
			sbBuildValueString.append(")");
			break;       
		}
		return sbBuildValueString;
	}
	private StringBuilder sbBuildValueString = new StringBuilder(50); 

	/**
	 * interface VectorValue implementation
	 */    
	public GeoVec2D getVector() {        
		GeoVec2D ret = new GeoVec2D(kernel, x, y);
		ret.setMode(toStringMode);
		return ret;
	}        
	
	/** POLAR or CARTESIAN */

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder xmlsb) {
		super.getXMLtags(xmlsb);
		//	line thickness and type  
		getLineStyleXML(xmlsb);

		// polar or cartesian coords
		switch(toStringMode) {
		case Kernel.COORD_POLAR:
			xmlsb.append("\t<coordStyle style=\"polar\"/>\n");
			break;

		case Kernel.COORD_COMPLEX:
			xmlsb.append("\t<coordStyle style=\"complex\"/>\n");
			break;

		default:
			xmlsb.append("\t<coordStyle style=\"cartesian\"/>\n");
		}

		//	startPoint of vector
		if (startPoint != null) {
			xmlsb.append(startPoint.getStartPointXML());
		}

	}   

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean isVectorValue() {
		return true;
	}

	@Override
	public boolean isPolynomialInstance() {
		return false;
	}   

	@Override
	public boolean isTextValue() {
		return false;
	}   

	/* 
	 * Path interface
	 */	 

	public boolean isClosedPath() {
		return false;
	}

	public void pointChanged(GeoPointND P) {
		if (startPoint == null && waitingForStartPoint) {
			// remember waiting points
			if (waitingPointSet == null) waitingPointSet = new HashSet<GeoPointND>();
			waitingPointSet.add(P);
			return;
		}

		if (pathSegment == null) updatePathSegment();
		pathSegment.pointChanged(P);
	}

	public void pathChanged(GeoPointND P) {	
		
		//if kernel doesn't use path/region parameters, do as if point changed its coords
		if(!getKernel().usePathAndRegionParameters(P)){
			pointChanged(P);
			return;
		}
		
		updatePathSegment();
		pathSegment.pathChanged(P);
	}

	public boolean isOnPath(GeoPointND P, double eps) {
		updatePathSegment(); // Michael Borcherds 2008-06-10 bugfix
		return pathSegment.isOnPath(P, eps);
	}

	@Override
	public boolean isPath() {
		return true;
	}

	public double getMinParameter() {
		return 0;
	}

	public double getMaxParameter() {
		return 1;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	private void initPathSegment() {
		if (startPoint != null) {
			pathStartPoint = startPoint;
		} else {
			pathStartPoint = new GeoPoint(cons);
			pathStartPoint.setCoords(0, 0, 1);
		}

		pathEndPoint = new GeoPoint(cons);
		pathSegment = new GeoSegment(cons, pathStartPoint, pathEndPoint);
	}

	private void updatePathSegment() {
		if (pathSegment == null) initPathSegment();

		// update segment
		pathEndPoint.setCoords(pathStartPoint.inhomX + x,
				pathStartPoint.inhomY + y, 
				1.0);

		GeoVec3D.lineThroughPoints(pathStartPoint, pathEndPoint, pathSegment);
		// length is used in GeoSement.pointChanged() and GeoSegment.pathChanged()
		pathSegment.calcLength(); 
	}

	@Override
	public boolean isGeoVector() {
		return true;
	}

	public boolean isAlwaysFixed() {
		return false;
	}

	@Override
	public boolean isVector3DValue() {
		return false;		
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	private StringBuilder sb;

	@SuppressWarnings("cast")
	// see http://code.google.com/p/google-web-toolkit/issues/detail?id=4097
	@Override
	public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		if (sb == null) sb = new StringBuilder();
		else sb.setLength(0);

		switch (toStringMode) {
		case Kernel.COORD_POLAR:                	
			sb.append("(");		
			sb.append(kernel.format(MyMath.length(x, y),tpl));
			sb.append("; ");
			sb.append((CharSequence)kernel.formatAngle(Math.atan2(y, x),tpl));
			sb.append(")");
			break;

		case Kernel.COORD_COMPLEX:              	
			sb.append(kernel.format(x,tpl));
			sb.append(" ");
			kernel.formatSigned(y,sb,tpl);
			sb.append(Unicode.IMAGINARY);
			break;                                

		default: // CARTESIAN

			String[] inputs;
			if (symbolic && getParentAlgorithm() instanceof AlgoDependentVector) {
				AlgoDependentVector algo = (AlgoDependentVector)getParentAlgorithm();
				String symbolicStr = algo.toString(tpl);
				inputs = symbolicStr.substring(1, symbolicStr.length() - 1).split(",");
			} else {
				inputs = new String[2];
				inputs[0] = kernel.format(x,tpl);
				inputs[1] = kernel.format(y,tpl);
			}
			
			// MathQuill can't render v = \left( \begin{tabular}{r}-10 \\ 0 \\ \end{tabular} \right)
			// so use eg \binom{ -10 }{ 0 } in web
			// see #1987
			if (inputs.length == 2 && app.isHTML5Applet()) {
				sb.append(" \\binom{ ");
				sb.append(inputs[0]);
				sb.append(" }{ ");
				sb.append(inputs[1]);
				sb.append(" }");
				
			} else {
			

				boolean alignOnDecimalPoint = true;
				for (int i = 0 ; i < inputs.length ; i++) {
					if (inputs[i].indexOf('.') == -1) {
						alignOnDecimalPoint = false;
						continue;
					}
				}

				if (alignOnDecimalPoint) {
					sb.append("\\left( \\begin{tabular}{r@{.}l}");
					for (int i = 0 ; i < inputs.length ; i++) {
						inputs[i] = inputs[i].replace('.', '&');
					}
				} else {			
					sb.append("\\left( \\begin{tabular}{r}");
				}


				for (int i = 0 ; i < inputs.length ; i++) {
					sb.append(inputs[i]);
					sb.append(" \\\\ ");    			
				}

				sb.append("\\end{tabular} \\right)"); 
			}
			break;
		}

		return sb.toString();
	}     


	public Coords getCoordsInD(int dimension){
		Coords ret = new Coords(dimension+1);

		ret.setX(getX());
		ret.setY(getY());
		ret.setZ(getZ());

		return ret;
	}


	@Override
	public boolean hasDrawable3D(){
		return true;
	}


	//only used for 3D
	public void updateStartPointPosition() {
		//3D only
	}


	public Coords getDirectionInD3(){
		return getCoordsInD(3);
	}

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		double x1 = a00 * x + a01 * y + a02 * 1;
		double y1 = a10 * x + a11 * y + a12 * 1;
		double z1 = a20 * x + a21 * y + a22 * 1;			
		setCoords(x1/z1,y1/z1,0);

	}
	
	@Override
	public  boolean isLaTeXDrawableGeo() {
		return true;
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
		
		
	}
	


	@Override
	public TraceModesEnum getTraceModes(){
		return TraceModesEnum.SEVERAL_VALUES_OR_COPY;
	}
	
	@Override
	public String getTraceDialogAsValues(){
		String name = getLabelTextOrHTML(false);
	
		StringBuilder sbTrace = new StringBuilder();
		sbTrace.append("x(");
		sbTrace.append(name);
		sbTrace.append("), y(");
		sbTrace.append(name);
		sbTrace.append(")");
				
		return sbTrace.toString();
	}

		
	
	@Override
	public void addToSpreadsheetTraceList(ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric xx = new GeoNumeric(cons, getInhomVec().getX());
		spreadsheetTraceList.add(xx);
		GeoNumeric yy = new GeoNumeric(cons, getInhomVec().getY());
		spreadsheetTraceList.add(yy);
	}
	
	public SymbolicParameters getSymbolicParameters() {
		if (algoParent != null
				&& (algoParent instanceof SymbolicParametersAlgo)) {
			return new SymbolicParameters((SymbolicParametersAlgo) algoParent);
		}
		return null;
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (algoParent != null
				&& (algoParent instanceof SymbolicParametersAlgo)) {
			((SymbolicParametersAlgo) algoParent)
					.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if (algoParent != null
				&& (algoParent instanceof SymbolicParametersAlgo)) {
			return ((SymbolicParametersAlgo) algoParent)
					.getDegrees();
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(final HashMap<Variable,BigInteger> values) throws NoSymbolicParametersException{
		if (algoParent != null
	&& (algoParent instanceof SymbolicParametersAlgo)) {
			return ((SymbolicParametersAlgo) algoParent).getExactCoordinates(values);
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (algoParent != null && algoParent instanceof SymbolicParametersAlgo) {
			return ((SymbolicParametersAlgo) algoParent).getPolynomials();
		}
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		if (algoParent != null
				&& algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent).getBotanaVars(this);
		}
		return null;
	}
	
	public Polynomial[] getBotanaPolynomials(GeoElement geo) throws NoSymbolicParametersException  {
		if (algoParent != null && algoParent instanceof SymbolicParametersBotanaAlgo) {
		return ((SymbolicParametersBotanaAlgo) algoParent).getBotanaPolynomials(this);
		}
	throw new NoSymbolicParametersException();
	}

	public double[] getInhomCoords() {
		double[] ret = new double[2];
		ret[0] = getX();
		ret[1] = getY();
		return ret;
	}
	
	public void updateLocation() {
		updateGeo();
		kernel.notifyUpdateLocation(this);	
	}
}
