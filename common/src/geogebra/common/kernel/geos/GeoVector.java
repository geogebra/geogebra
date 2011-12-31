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

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Locateable;
import geogebra.common.kernel.MatrixTransformable;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.PathMoverGeneric;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoDependentVector;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.MyMath;
import geogebra.common.util.Unicode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author  Markus
 * @version 
 */
final public class GeoVector extends GeoVec3D
implements Path, VectorValue, Locateable, Translateable, PointRotateable, Mirrorable, Dilateable, MatrixTransformable, 
Transformable, GeoVectorND, SpreadsheetTraceable {

	private GeoPoint2 startPoint;

	// for path interface we use a segment
	private GeoSegment pathSegment;
	private GeoPoint2 pathStartPoint, pathEndPoint;
	private boolean waitingForStartPoint = false;
	private HashSet<GeoPointND> waitingPointSet;

	/** Creates new GeoVector */
	public GeoVector(Construction c) {
		super(c); 
		//setEuclidianVisible(false);
	}

	public String getClassName() {
		return "GeoVector";
	}

	protected String getTypeString() {
		return "Vector";
	}

	public GeoClass getGeoClassType() {
		return GeoClass.VECTOR;
	}   

	final public boolean isCasEvaluableObject() {
		return true;
	}

	/** Creates new GeoVector */
	public GeoVector(Construction c, String label, double x, double y, double z) {
		super(c, x, y, z); // GeoVec3D constructor                 
		setLabel(label); 
		//setEuclidianVisible(false);
	}

	public GeoVector(GeoVector vector) {
		super(vector.cons);
		set(vector);
		//setEuclidianVisible(false);
	}

	final public void setCoords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}     

	final public void setCoords(double[] c) {
		setCoords(c[0],c[1],c[2]);
	}  

	final public void setCoords(GeoVec3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
	} 

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
					setStartPoint(new GeoPoint2(vec.startPoint));
				} else {
					//	take existing location point	
					setStartPoint(vec.startPoint);
				}
			}
		}
		catch (CircularDefinitionException e) {
			AbstractApplication.debug("set GeoVector: CircularDefinitionException");
		}		
	}

	public GeoElement copy() {
		return new GeoVector(this);        
	} 

	final public void setPolarCoords(double r, double phi) {
		// convert angle to radiant              
		x = r * Math.cos( phi );
		y = r * Math.sin( phi );        
		z = 0.0d;        
	}

	final public void setCoords(GeoVec2D v) {
		x = v.x;
		y = v.y;
		z = 0.0d;
	}      

	/** Converts the homogenous coordinates (x,y,z)
	 * of this GeoVec3D to the inhomogenous coordinates (x/z, y/z)
	 * of a new GeoVec2D.
	 */
	final public GeoVec2D getInhomVec() {
		return new GeoVec2D(kernel, x, y);
	}

	/**
	 * Retuns starting point of this vector or null.
	 */
	final public GeoPoint2 getStartPoint() {
		return startPoint;
	}   

	public GeoPoint2 [] getStartPoints() {
		if (startPoint == null)
			return null;

		GeoPoint2 [] ret = new GeoPoint2[1];
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
		startPoint = (GeoPoint2) p;
	}

	public void removeStartPoint(GeoPointND p) {    
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch(Exception e) {}
		}
	}



	public void setStartPoint(GeoPointND pI) throws CircularDefinitionException {  

		GeoPoint2 p = (GeoPoint2) pI;

		if (startPoint == p) return;

		// macro output uses initStartPoint() only
		if (isAlgoMacroOutput()) return; 				

		// check for circular definition
		if (isParentOf(p)){
			AbstractApplication.debug(this+" startpoint "+p);
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

				GeoPoint2 P;
				Iterator<GeoPointND> it = waitingPointSet.iterator();
				while (it.hasNext()) {
					P = (GeoPoint2) it.next();
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

	public void doRemove() {
		super.doRemove();
		// tell startPoint	
		if (startPoint != null) startPoint.getLocateableList().unregisterLocateable(this);
	}

	final public boolean isFinite() {
		return !isInfinite();
	}

	final public boolean isInfinite() {
		return Double.isInfinite(x) || Double.isInfinite(y);  
	}

	final protected boolean showInEuclidianView() {               
		return isDefined() && !isInfinite();
	}    

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
	final public boolean isEqual(GeoElement geo) {        

		if (!geo.isGeoVector()) return false;

		GeoVector v = (GeoVector)geo;

		if (!(isFinite() && v.isFinite())) return false;                                        
		else return AbstractKernel.isEqual(x, v.x) && AbstractKernel.isEqual(y, v.y);                                            
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

	}

	public void rotate(NumberValue r, GeoPoint2 S) {
		rotateXY(r);
	}

	public void mirror(GeoPoint2 Q) {

		setCoords(- x,- y, z );

	}

	public void mirror(GeoLine g) {
		mirrorXY(2.0 * Math.atan2(-g.getX(), g.getY()));

	}

	public void dilate(NumberValue rval, GeoPoint2 S) {
		double r = rval.getDouble();	
		setCoords(r * x, r * y, z);

	}

	public void matrixTransform(double a,double b,double c,double d) {

		Double x1 = a*x + b*y;
		Double y1 = c*x + d*y;

		setCoords(x1, y1, z);

	}


	/*********************************************************************/   

	final public String toString() {            
		sbToString.setLength(0);
		sbToString.append(label);

		switch (kernel.getCoordStyle()) {
		case AbstractKernel.COORD_STYLE_FRENCH:
			// no equal sign
			sbToString.append(": ");

		case AbstractKernel.COORD_STYLE_AUSTRIAN:
			// no equal sign
			break;

		default: 
			sbToString.append(" = ");
		}

		sbToString.append(buildValueString());
		return sbToString.toString();
	}

	final public String toStringMinimal() {            
		sbToString.setLength(0);
		sbToString.append(regrFormat(x) + " " + regrFormat(y));
		return sbToString.toString();
	}

	
	private StringBuilder sbToString = new StringBuilder(50); 

	final public String toValueString() {
		return buildValueString().toString();
	}

	private StringBuilder buildValueString() {
		sbBuildValueString.setLength(0);

		switch (kernel.getCASPrintForm()) {
		case MATH_PIPER:
			sbBuildValueString.append("{");
			sbBuildValueString.append(getInhomVec().x);
			sbBuildValueString.append(", ");
			sbBuildValueString.append(getInhomVec().y);
			sbBuildValueString.append("}");
			return sbBuildValueString;

		case MAXIMA:
			sbBuildValueString.append("[");
			sbBuildValueString.append(getInhomVec().x);
			sbBuildValueString.append(", ");
			sbBuildValueString.append(getInhomVec().y);
			sbBuildValueString.append("]");
			return sbBuildValueString;

		case MPREDUCE:
			sbBuildValueString.append("list(");
			sbBuildValueString.append(getInhomVec().x);
			sbBuildValueString.append(",");
			sbBuildValueString.append(getInhomVec().y);
			sbBuildValueString.append(")");
			return sbBuildValueString;

		default: // continue below
		}
		switch (toStringMode) {
		case AbstractKernel.COORD_POLAR:                	
			sbBuildValueString.append("(");		
			sbBuildValueString.append(kernel.format(MyMath.length(x, y)));
			sbBuildValueString.append("; ");
			sbBuildValueString.append(kernel.formatAngle(Math.atan2(y, x)));
			sbBuildValueString.append(")");
			break;

		case AbstractKernel.COORD_COMPLEX:              	
			sbBuildValueString.append(kernel.format(x));
			sbBuildValueString.append(" ");
			sbBuildValueString.append(kernel.formatSigned(y));
			sbBuildValueString.append(Unicode.IMAGINARY);
			break;                                

		default: // CARTESIAN
			sbBuildValueString.append("(");		
			sbBuildValueString.append(kernel.format(x));
			switch (kernel.getCoordStyle()) {
			case AbstractKernel.COORD_STYLE_AUSTRIAN:
				sbBuildValueString.append(" | ");
				break;

			default:
				sbBuildValueString.append(", ");												
			}
			sbBuildValueString.append(kernel.format(y));
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

	public boolean isConstant() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> varset = new HashSet<GeoElement>();        
		varset.add(this);        
		return varset;          
	}


	/** POLAR or CARTESIAN */


	public ExpressionValue evaluate() { return this; }

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		//	line thickness and type  
		getLineStyleXML(sb);

		// polar or cartesian coords
		switch(toStringMode) {
		case AbstractKernel.COORD_POLAR:
			sb.append("\t<coordStyle style=\"polar\"/>\n");
			break;

		case AbstractKernel.COORD_COMPLEX:
			sb.append("\t<coordStyle style=\"complex\"/>\n");
			break;

		default:
			sb.append("\t<coordStyle style=\"cartesian\"/>\n");
		}

		//	startPoint of vector
		if (startPoint != null) {
			sb.append(startPoint.getStartPointXML());
		}

	}   

	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return true;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   

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
		if(!getKernel().usePathAndRegionParameters()){
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

	public boolean isPath() {
		return true;
	}

	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return
	 */
	public double getMinParameter() {
		return 0;
	}

	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return
	 */
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
			pathStartPoint = new GeoPoint2(cons);
			pathStartPoint.setCoords(0, 0, 1);
		}

		pathEndPoint = new GeoPoint2(cons);
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

	public boolean isGeoVector() {
		return true;
	}

	public boolean isAlwaysFixed() {
		return false;
	}

	public boolean isVector3DValue() {
		return false;		
	}

	public boolean isMatrixTransformable() {
		return true;
	}

	private StringBuilder sb;

	public String toLaTeXString(boolean symbolic) {
		if (sb == null) sb = new StringBuilder();
		else sb.setLength(0);

		switch (toStringMode) {
		case AbstractKernel.COORD_POLAR:                	
			sb.append("(");		
			sb.append(kernel.format(MyMath.length(x, y)));
			sb.append("; ");
			sb.append(kernel.formatAngle(Math.atan2(y, x)));
			sb.append(")");
			break;

		case AbstractKernel.COORD_COMPLEX:              	
			sb.append(kernel.format(x));
			sb.append(" ");
			sb.append(kernel.formatSigned(y));
			sb.append(Unicode.IMAGINARY);
			break;                                

		default: // CARTESIAN

			String[] inputs;
			if (symbolic && getParentAlgorithm() instanceof AlgoDependentVector) {
				AlgoDependentVector algo = (AlgoDependentVector)getParentAlgorithm();
				String symbolicStr = algo.toString();
				inputs = symbolicStr.substring(1, symbolicStr.length() - 1).split(",");
			} else {
				inputs = new String[2];
				inputs[0] = kernel.format(x);
				inputs[1] = kernel.format(y);
			}

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


	public boolean hasDrawable3D(){
		return true;
	}


	//only used for 3D
	public void updateStartPointPosition() {


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
	
	public  boolean isLaTeXDrawableGeo(String latexStr) {
		return true;
	}
	
	public StringBuilder[] getColumnHeadings() {
		if (spreadsheetColumnHeadings == null) {
			spreadsheetColumnHeadings = new StringBuilder[2];
			spreadsheetColumnHeadings[0] = new StringBuilder(4);
			spreadsheetColumnHeadings[1] = new StringBuilder(4);
		} else {
			spreadsheetColumnHeadings[0].setLength(0);
			spreadsheetColumnHeadings[1].setLength(0);
		}
		spreadsheetColumnHeadings[0].append("x(");
		spreadsheetColumnHeadings[0].append(getLabel());
		spreadsheetColumnHeadings[0].append(')');
		
		spreadsheetColumnHeadings[1].append("y(");
		spreadsheetColumnHeadings[1].append(getLabel());
		spreadsheetColumnHeadings[1].append(')');
		
		return spreadsheetColumnHeadings;
	}
	
	public ArrayList<GeoNumeric> getSpreadsheetTraceList() {
		if (spreadsheetTraceList == null) {
			spreadsheetTraceList = new ArrayList<GeoNumeric>();
			GeoNumeric xx = new GeoNumeric(cons, getInhomVec().x);
			spreadsheetTraceList.add(xx);
			GeoNumeric yy = new GeoNumeric(cons, getInhomVec().y);
			spreadsheetTraceList.add(yy);
		} else {
			spreadsheetTraceList.get(0).setValue(getInhomVec().x);
			spreadsheetTraceList.get(1).setValue(getInhomVec().y);
		}
		
		return spreadsheetTraceList;
	}

}
