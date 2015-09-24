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

package org.geogebra.common.kernel.geos;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoDependentVector;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.DependentAlgo;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.Unicode;

/**
 *
 * @author  Markus
 */
final public class GeoVector extends GeoVec3D
implements Path, VectorValue, Translateable, PointRotateable, Mirrorable, Dilateable, MatrixTransformable, 
Transformable, GeoVectorND, SpreadsheetTraceable, SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private GeoPointND startPoint;

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
	public void set(GeoElementND geo) {
		if(geo.isGeoPoint()){
			GeoPointND p = (GeoPointND) geo;
			double[] coords = p.getCoordsInD3().get();
			if (Kernel.isZero(coords[2])) {
				setCoords(coords);
			} else {
				setUndefined();
			}
		}else{
			super.set(geo);
		}
		
		if (!geo.isGeoVector()) return;

		GeoVector vec = (GeoVector) geo;		

		// don't set start point for macro output
		// see AlgoMacro.initRay()
		if (geo.getConstruction() != cons && isAlgoMacroOutput())
			return;

		try {
			if (vec.startPoint != null) {
				if (vec.hasAbsoluteLocation()) {
					//	create new location point	
					setStartPoint(vec.startPoint.copy());
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
	final public GeoPointND getStartPoint() {
		return startPoint;
	}   

	public GeoPointND [] getStartPoints() {
		if (startPoint == null)
			return null;

		GeoPointND [] ret = new GeoPointND[1];
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
		startPoint = p;
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



	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {  

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

	public void rotate(NumberValue r, GeoPointND S) {
		rotateXY(r);
	}

	public void mirror(Coords Q) {

		setCoords(- x,- y, z );

	}

	public void mirror(GeoLineND g1) {
		GeoLine g = (GeoLine) g1;
		mirrorXY(2.0 * Math.atan2(-g.getX(), g.getY()));

	}

	public void dilate(NumberValue rval, Coords S) {
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

	private StringBuilder buildValueString(StringTemplate tpl) {
		sbBuildValueString.setLength(0);

		switch (tpl.getStringType()) {
		case GIAC:
			sbBuildValueString.append("ggbvect[");
			sbBuildValueString.append(kernel.format(getInhomVec().getX(), tpl));
			sbBuildValueString.append(',');
			sbBuildValueString.append(kernel.format(getInhomVec().getY(), tpl));
			sbBuildValueString.append("]");
			return sbBuildValueString;

		default: // continue below
		}
		switch (toStringMode) {
		case Kernel.COORD_POLAR:                	
			sbBuildValueString.append("(");		
			sbBuildValueString.append(kernel.format(MyMath.length(x, y),tpl));
			sbBuildValueString.append("; ");
			sbBuildValueString.append(kernel.formatAngle(Math.atan2(y, x), tpl, false));
			sbBuildValueString.append(")");
			break;

		case Kernel.COORD_COMPLEX:              	
			sbBuildValueString.append(kernel.format(x,tpl));
			sbBuildValueString.append(" ");
			kernel.formatSigned(y,sbBuildValueString,tpl);
			sbBuildValueString.append(Unicode.IMAGINARY);
			break;   
			
		case Kernel.COORD_CARTESIAN_3D:
			GeoPoint.buildValueStringCoordCartesian3D(kernel, tpl, x, y, 0, sbBuildValueString);
			break;
			
		case Kernel.COORD_SPHERICAL:
			GeoPoint.buildValueStringCoordSpherical(kernel, tpl, x, y, 0, sbBuildValueString);
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
			
		case Kernel.COORD_CARTESIAN_3D:
			xmlsb.append("\t<coordStyle style=\"cartesian3d\"/>\n");
			break;

		case Kernel.COORD_SPHERICAL:
			xmlsb.append("\t<coordStyle style=\"spherical\"/>\n");
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
	public boolean evaluatesToNonComplex2DVector() {
		return this.getMode() != Kernel.COORD_COMPLEX;
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		return this.getMode() != Kernel.COORD_COMPLEX;
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
		if (startPoint != null  && ! startPoint.isGeoElement3D()) { //TODO 3D case
			pathStartPoint = (GeoPoint) startPoint;
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
	public boolean isMatrixTransformable() {
		return true;
	}
	
	/**
	 * @param kernel kernel
	 * @param tpl string template
	 * @param x x-coord
	 * @param y y-coord
	 * @param z z-coord
	 * @param sb string builder
	 * @param vector the vector
	 * @param symbolic if symbolic
	 */
	public static final void buildLatexValueStringCoordCartesian3D(Kernel kernel, StringTemplate tpl, double x, double y, double z, StringBuilder sb, GeoVectorND vector, boolean symbolic) {
		String[] inputs;
		if (symbolic && vector.getParentAlgorithm() instanceof DependentAlgo) {
			AlgoElement algo = vector.getParentAlgorithm();
			String symbolicStr = algo.toString(tpl);
			// remove \left( and \right)
			int firstIndex = symbolicStr.indexOf("\\left(");
			int lastIndex = symbolicStr.lastIndexOf("\\right)");
			inputs = symbolicStr.substring(firstIndex + 6, lastIndex).split(
					",");
		} else {
			inputs = new String[3];
			inputs[0] = kernel.format(x,tpl);
			inputs[1] = kernel.format(y,tpl);
			inputs[2] = kernel.format(z,tpl);
		}

		if (inputs.length == 3
				&& kernel.getApplication().isLatexMathQuillStyle(tpl)) {
			sb.append("\\vectorize{\\ggbtable{\\ggbtr{\\ggbtd{");
			sb.append(inputs[0]); 
			sb.append("}}\\ggbtr{\\ggbtd{"); 
			sb.append(inputs[1]); 
			sb.append("}}\\ggbtr{\\ggbtd{"); 
			sb.append(inputs[2]); 
			sb.append("}}}}");
			return;
		}
		buildTabular(inputs, sb);

	}

	private static void buildTabular(String[] inputs, StringBuilder sb) {
		boolean alignOnDecimalPoint = true;
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i].indexOf('.') == -1) {
				alignOnDecimalPoint = false;
				continue;
			}
		}

		if (alignOnDecimalPoint) {
			sb.append("\\left(\\hspace{-0.4em} \\begin{tabular}{r@{.}l}");
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = inputs[i].replace('.', '&');
			}
		} else {
			sb.append("\\left(\\hspace{-0.4em} \\begin{tabular}{r}");
		}

		for (int i = 0; i < inputs.length; i++) {
			sb.append(inputs[i]);
			sb.append(" \\\\ ");
		}

		sb.append("\\end{tabular}\\hspace{-0.4em} \\right)");
		
	}


	private StringBuilder sb;

	@Override
	public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		if (sb == null) sb = new StringBuilder();
		else sb.setLength(0);
		
		return buildLatexString(kernel, sb, symbolic, tpl, toStringMode, x, y, this);
	}

	static final public String buildLatexString(Kernel kernel, StringBuilder sb, boolean symbolic, StringTemplate tpl, int toStringMode, double x, double y, GeoVectorND vector){
		switch (toStringMode) {
		case Kernel.COORD_POLAR:                	
			sb.append("(");		
			sb.append(kernel.format(MyMath.length(x, y),tpl));
			sb.append("; ");
			sb.append(kernel.formatAngle(Math.atan2(y, x), tpl, false));
			sb.append(")");
			break;

		case Kernel.COORD_COMPLEX:              	
			sb.append(kernel.format(x,tpl));
			sb.append(" ");
			kernel.formatSigned(y,sb,tpl);
			sb.append(Unicode.IMAGINARY);
			break;     

		case Kernel.COORD_CARTESIAN_3D:
			buildLatexValueStringCoordCartesian3D(kernel, tpl, x, y, 0, sb, vector, symbolic);
			break;
			
		case Kernel.COORD_SPHERICAL:
			GeoPoint.buildValueStringCoordSpherical(kernel, tpl, x, y, 0, sb);
			break;


		default: // CARTESIAN

			String[] inputs;
			if (symbolic && vector.getParentAlgorithm() instanceof AlgoDependentVector) {
				AlgoDependentVector algo = (AlgoDependentVector) vector.getParentAlgorithm();
				
				// need to do something different for (xx,yy) and a (1,2) + c
				
				ExpressionNode en = algo.getExpression();
				ExpressionValue ev = en.unwrap();
				
				if (ev instanceof MyVecNode) {
					MyVecNode vn = (MyVecNode) ev;
					
					inputs = new String[2];
					inputs[0] = vn.getX().toString(tpl);
					inputs[1] = vn.getY().toString(tpl);
				} else {
					return algo.toString(tpl);
				}
				
			} else {
				inputs = new String[2];
				inputs[0] = kernel.format(x,tpl);
				inputs[1] = kernel.format(y,tpl);
			}
			
			// MathQuillGGB can't render v = \left( \begin{tabular}{r}-10 \\ 0 \\ \end{tabular} \right)
			// so use eg \binom{ -10 }{ 0 } in web
			// see #1987
			if (inputs.length == 2
					&& kernel.getApplication().isLatexMathQuillStyle(tpl)) {
				sb.append("\\vectorize{\\ggbtable{\\ggbtr{\\ggbtd{");
				sb.append(inputs[0]);
				sb.append("}}\\ggbtr{\\ggbtd{");
				sb.append(inputs[1]);
				sb.append("}}}}");
				
			} else {
			

				buildTabular(inputs, sb);
			}
			break;
		}

		return sb.toString();
	}     


	
	public Coords getCoordsInD2(){
		Coords ret = new Coords(3);
		ret.setX(getX());
		ret.setY(getY());
		ret.setZ(getZ());
		return ret;
	}
	public Coords getCoordsInD3(){
		Coords ret = new Coords(4);
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
		return getCoordsInD3();
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
 kernel.getAlgebraProcessor().getXBracket(), // "x("
								Operation.PLUS, 
								new ExpressionNode(kernel,
										getNameGeo(), // Name[this]
										Operation.PLUS, 
 kernel.getAlgebraProcessor()
								.getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(
				getColumnHeadingText(  
						new ExpressionNode(kernel,
 kernel.getAlgebraProcessor().getYBracket(), // "y("
								Operation.PLUS, 
								new ExpressionNode(kernel,
										getNameGeo(), // Name[this]
										Operation.PLUS, 
 kernel.getAlgebraProcessor()
								.getCloseBracket())))); // ")"
		
		
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
	

	
	@Override
	final public HitType getLastHitType(){
		return HitType.ON_BOUNDARY;
	}

	public ValueType getValueType() {
		return getMode() == Kernel.COORD_COMPLEX ? ValueType.COMPLEX
				: ValueType.NONCOMPLEX2D;
	}

}
