/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Kernel.java
 *
 * Created on 30. August 2001, 20:12
 */

package geogebra.kernel;

import geogebra.cas.GeoGebraCAS;
import geogebra.common.adapters.Geo3DVec;
import geogebra.common.io.MyXMLHandler;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.EquationSolverInterface;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.MacroInterface;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.TransformApplyMatrix;
import geogebra.common.kernel.TransformDilate;
import geogebra.common.kernel.TransformMirror;
import geogebra.common.kernel.TransformRotate;
import geogebra.common.kernel.TransformShearOrStretch;
import geogebra.common.kernel.TransformTranslate;
import geogebra.common.kernel.algos.*;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Polynomial;
import geogebra.common.kernel.cas.AlgoCoefficients;
import geogebra.common.kernel.cas.AlgoDegree;
import geogebra.common.kernel.cas.AlgoDerivative;
import geogebra.common.kernel.cas.AlgoExpand;
import geogebra.common.kernel.cas.AlgoFactor;
import geogebra.common.kernel.cas.AlgoFactors;
import geogebra.common.kernel.cas.AlgoIntegral;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.cas.AlgoLengthCurve;
import geogebra.common.kernel.cas.AlgoLengthCurve2Points;
import geogebra.common.kernel.cas.AlgoLengthFunction;
import geogebra.common.kernel.cas.AlgoLengthFunction2Points;
import geogebra.common.kernel.cas.AlgoLimit;
import geogebra.common.kernel.cas.AlgoLimitAbove;
import geogebra.common.kernel.cas.AlgoLimitBelow;
import geogebra.common.kernel.cas.AlgoPartialFractions;
import geogebra.common.kernel.cas.AlgoPolynomialDiv;
import geogebra.common.kernel.cas.AlgoPolynomialMod;
import geogebra.common.kernel.cas.AlgoSimplify;
import geogebra.common.kernel.cas.AlgoSolveODECas;
import geogebra.common.kernel.cas.AlgoTangentCurve;
import geogebra.common.kernel.cas.AlgoTangentFunctionNumber;
import geogebra.common.kernel.cas.AlgoTangentFunctionPoint;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.AbstractCommandDispatcher;
import geogebra.common.kernel.geos.*;
import geogebra.common.kernel.implicit.AlgoAsymptoteImplicitPoly;
import geogebra.common.kernel.implicit.AlgoDependentImplicitPoly;
import geogebra.common.kernel.implicit.AlgoImplicitPolyFunction;
import geogebra.common.kernel.implicit.AlgoImplicitPolyThroughPoints;
import geogebra.common.kernel.implicit.AlgoIntersectImplicitpolyParametric;
import geogebra.common.kernel.implicit.AlgoIntersectImplicitpolys;
import geogebra.common.kernel.implicit.AlgoTangentImplicitpoly;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.optimization.ExtremumFinder;
import geogebra.common.kernel.parser.Parser;
import geogebra.common.kernel.statistics.*;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.AbstractApplication.CasType;
import geogebra.common.main.MyError;
import geogebra.common.util.AbstractMyMath2;
import geogebra.common.util.LaTeXCache;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.kernel.algos.AlgoDrawingPadCorner;
import geogebra.common.kernel.algos.AlgoFitPoly;
import geogebra.common.kernel.algos.AlgoFitPow;
import geogebra.common.kernel.algos.AlgoFitSin;
import geogebra.common.kernel.algos.AlgoFrequencyPolygon;
import geogebra.common.kernel.algos.AlgoHistogram;
import geogebra.common.kernel.algos.AlgoIntersectFunctions;
import geogebra.common.kernel.algos.AlgoIntersectPolynomialConic;
import geogebra.common.kernel.algos.AlgoPolynomialFromFunction;
import geogebra.common.kernel.algos.AlgoReducedRowEchelonForm;
import geogebra.common.kernel.algos.AlgoRootInterval;
import geogebra.common.kernel.algos.AlgoSurdText;
import geogebra.common.kernel.algos.AlgoSurdTextPoint;
import geogebra.kernel.algos.AlgoFrequency;
import geogebra.kernel.algos.AlgoFrequencyTable;
import geogebra.kernel.algos.AlgoIntersectFunctionLineNewton;
import geogebra.kernel.algos.AlgoIntersectFunctionsNewton;
import geogebra.kernel.algos.AlgoNormalQuantilePlot;
import geogebra.kernel.algos.AlgoPolygonIntersection;
import geogebra.kernel.algos.AlgoPolygonUnion;
import geogebra.kernel.algos.AlgoRootNewton;
import geogebra.kernel.algos.AlgoSolveODE;
import geogebra.kernel.algos.AlgoSolveODE2;
import geogebra.kernel.algos.AlgoStemPlot;
import geogebra.kernel.algos.AlgoUnique;
import geogebra.kernel.barycentric.AlgoBarycenter;
import geogebra.kernel.barycentric.AlgoKimberling;
import geogebra.kernel.barycentric.AlgoTriangleCubic;
import geogebra.kernel.barycentric.AlgoTriangleCurve;
import geogebra.kernel.barycentric.AlgoTrilinear;
import geogebra.kernel.commands.CommandDispatcher;
import geogebra.kernel.discrete.AlgoConvexHull;
import geogebra.kernel.discrete.AlgoDelauneyTriangulation;
import geogebra.kernel.discrete.AlgoHull;
import geogebra.kernel.discrete.AlgoMinimumSpanningTree;
import geogebra.kernel.discrete.AlgoShortestDistance;
import geogebra.kernel.discrete.AlgoTravelingSalesman;
import geogebra.kernel.discrete.AlgoVoronoi;
import geogebra.kernel.geos.GeoElementGraphicsAdapterDesktop;
import geogebra.kernel.geos.GeoElementSpreadsheet;
import geogebra.kernel.geos.GeoTextField;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.statistics.*;
import geogebra.main.Application;
import geogebra.util.GeoLaTeXCache;
import geogebra.util.GgbMat;
import geogebra.util.MyMath2;
import geogebra.util.NumberFormatDesktop;
import geogebra.util.ScientificFormat;

import java.text.DecimalFormat;
import java.util.TreeSet;

public class Kernel extends AbstractKernel{
			
	

	protected Application app;	
	
	private EquationSolver eqnSolver;
	private SystemOfEquationsSolver sysEqSolv;
	private ExtremumFinder extrFinder;
	protected Parser parser;

	

	
	
	
	/** 3D manager */
	private Manager3DInterface manager3D;
				
	public Kernel(Application app) {
		this();
		this.app = app;

		geogebra.common.factories.AwtFactory.prototype = new geogebra.factories.AwtFactory();
		geogebra.common.util.StringUtil.prototype = new geogebra.util.StringUtil();
		//TODO: probably there is better way
		geogebra.common.awt.Color.black = geogebra.awt.Color.black;
		geogebra.common.awt.Color.white = geogebra.awt.Color.white;
		geogebra.common.awt.Color.blue = geogebra.awt.Color.blue;
		geogebra.common.awt.Color.gray = geogebra.awt.Color.gray;
		geogebra.common.awt.Color.lightGray = geogebra.awt.Color.lightGray;
		geogebra.common.awt.Color.darkGray = geogebra.awt.Color.darkGray;
		
		newConstruction();
		getExpressionNodeEvaluator();
		
		setManager3D(newManager3D(this));
	}
	
	public Kernel() {
		super();
	}
	
	/**
	 * @param kernel
	 * @return a new 3D manager
	 */
	protected Manager3DInterface newManager3D(Kernel kernel){
		return null;
	}
	
	/**
	 * sets the 3D manager
	 * @param manager
	 */
	public void setManager3D(Manager3DInterface manager){
		this.manager3D = manager;
	}
	
	/**
	 * @return the 3D manager of this
	 */
	public Manager3DInterface getManager3D(){
		return manager3D;
	}
	
	
	
	/**
	 * creates the construction cons
	 */
	protected void newConstruction(){
		cons = new Construction(this);	
	}
	
	
	/**
	 * creates a new MyXMLHandler (used for 3D)
	 * @param cons construction used in MyXMLHandler constructor
	 * @return a new MyXMLHandler
	 */
	public MyXMLHandler newMyXMLHandler(Construction cons){
		return newMyXMLHandler(this, cons);		
	}
	
	/**
	 * creates a new MyXMLHandler (used for 3D)
	 * @param kernel
	 * @param cons
	 * @return a new MyXMLHandler
	 */
	public MyXMLHandler newMyXMLHandler(Kernel kernel, Construction cons){
		return new MyXMLHandler(kernel, cons);		
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * returns GeoElement at (row,col) in spreadsheet
	 * may return nully 
	 * @param col Spreadsheet column
	 * @param row Spreadsheet row
	 * @return Spreadsheet cell content (may be null)
	 */
	public GeoElement getGeoAt(int col, int row) {
		return lookupLabel(GeoElementSpreadsheet.getSpreadsheetCellName(col, row));
	}
	
	
	
	@Override
	final public Application getApplication() {
		return app;
	}		
	
	
	
	@Override
	final public EquationSolver getEquationSolver() {
		if (eqnSolver == null)
			eqnSolver = new EquationSolver(this);
		return eqnSolver;
	}
	
	@Override
	final public SystemOfEquationsSolver getSystemOfEquationsSolver(EquationSolverInterface eSolver) {
		if (sysEqSolv == null)
			sysEqSolv = new SystemOfEquationsSolver((EquationSolver)eSolver);
		return sysEqSolv;
	}
	
	@Override
	final public ExtremumFinder getExtremumFinder() {
		if (extrFinder == null)
			extrFinder = new ExtremumFinder();
		return extrFinder;
	}
	
	
	@Override
	final public Parser getParser() {
    	if (parser == null)
    		parser = new Parser(this, cons);
    	return parser;
    }	
			
	
	
	/** 
	 * Evaluates an expression in MathPiper syntax with.
     * @return result string (null possible)
	 * @throws Throwable 
     *
	final public String evaluateMathPiper(String exp) {
		if (ggbCAS == null) {
			getGeoGebraCAS();		
		}
		
		return ggbCAS.evaluateMathPiper(exp);
	}	*/
	
	/** 
	 * Evaluates an expression in Maxima syntax with.
     * @return result string (null possible)
	 * @throws Throwable 
     *
	final public String evaluateMaxima(String exp) {
		if (ggbCAS == null) {
			getGeoGebraCAS();		
		}
		
		return ggbCAS.evaluateMaxima(exp);
	}*/	
			
	
	
	
	
	/**
	 * Returns this kernel's GeoGebraCAS object.
	 */
	
	
	/**
	 * Resets the GeoGebraCAS and clears all variables.
	 */
	@Override
	public void resetGeoGebraCAS() {
		if (!isGeoGebraCASready()) return;
		
		// do NOT reset CAS because we are using one static CAS for all applicatin windows
		// see http://www.geogebra.org/trac/ticket/1415
		// instead we clear variable names of this kernel individually below			
		//ggbCAS.reset();

		// CAS reset may not clear user variables right now, 
		// see http://www.geogebra.org/trac/ticket/1249 
		// so we clear all user variable names individually from the CAS
		for (GeoElement geo : ((Construction)cons).getGeoSetWithCasCellsConstructionOrder()) {
			geo.unbindVariableInCAS();			
		}
	}
	
	


		
    
	
	
	
		
	
		
	//G.Sturr 2009-10-18
	final public void setAlgebraStyle(int style) {
		algebraStyle = style;
	}

	final public int getAlgebraStyle() {
		return algebraStyle;
	}
	//end G.Sturr
	
	
	
	
		 	
	final public CasType getCurrentCAS() {
		return ((GeoGebraCAS)getGeoGebraCAS()).currentCAS;
	}


		
	/**
	 * returns 10^(-PrintDecimals)
	 *
	final public double getPrintPrecision() {
		return PRINT_PRECISION;
	} */
	
	

	
	/*
	 * GeoElement specific
	 */
	
	
	
	
	/**
     * Creates a new GeoElement object for the given type string.
     * @param type String as produced by GeoElement.getXMLtypeString()
     */
    public GeoElement createGeoElement(Construction cons, String type) throws MyError {    	
    	// the type strings are the classnames in lowercase without the beginning "geo"
    	// due to a bug in GeoGebra 2.6c the type strings for conics
        // in XML may be "ellipse", "hyperbola", ...  
    	    	
    	switch (type.charAt(0)) {
    		case 'a': //angle    			
    			return new GeoAngle(cons);	    			     		    			
    			
    		case 'b': //angle
    			if (type.equals("boolean"))
    				return new GeoBoolean(cons);
    			else
        			return new GeoButton(cons); // "button"
    		
    		case 'c': // conic
    			if (type.equals("conic"))
    				return new GeoConic(cons);   
    			else if (type.equals("conicpart"))    					
    				return new GeoConicPart(cons, 0);
    			else if (type.equals("circle")) { // bug in GeoGebra 2.6c
    				return new GeoConic(cons);
    			}
    			
    		case 'd': // doubleLine 			// bug in GeoGebra 2.6c
    			return new GeoConic(cons);    			
    			
    		case 'e': // ellipse, emptyset	//  bug in GeoGebra 2.6c
				return new GeoConic(cons);     			
    				
    		case 'f': // function
    			return new GeoFunction(cons);
    		
    		case 'h': // hyperbola			//  bug in GeoGebra 2.6c
				return new GeoConic(cons);     			
    			
    		case 'i': // image,implicitpoly
    			if (type.equals("image"))    				
    				return new GeoImage(cons);
    			else if (type.equals("intersectinglines")) //  bug in GeoGebra 2.6c
    				return new GeoConic(cons);
    			else if (type.equals("implicitpoly"))
    				return new GeoImplicitPoly(cons);
    		
    		case 'l': // line, list, locus
    			if (type.equals("line"))
    				return new GeoLine(cons);
    			else if (type.equals("list"))
    				return new GeoList(cons);    					
    			else 
    				return new GeoLocus(cons);
    		
    		case 'n': // numeric
    			return new GeoNumeric(cons);
    			
    		case 'p': // point, polygon
    			if (type.equals("point"))
    				return new GeoPoint2(cons);
    			else if (type.equals("polygon"))
    				return new GeoPolygon(cons, null);
    			else if (type.equals("polyline"))
    				return new GeoPolyLine(cons, null);
    			else // parabola, parallelLines, point //  bug in GeoGebra 2.6c
    				return new GeoConic(cons);
    			
    		case 'r': // ray
    			return new GeoRay(cons, null);
    			
    		case 's': // segment    			
    			return new GeoSegment(cons, null, null);	    			    			
    			
    		case 't': 
    			if (type.equals("text"))
    				return new GeoText(cons); // text
    			else
        			return new GeoTextField(cons); // textfield
   			
    		case 'v': // vector
				return new GeoVector(cons);
    		
    		default:    			
    			throw new MyError(cons.getApplication(), "Kernel: GeoElement of type "
    		            + type + " could not be created.");		    		
    	}    		    
    }  
    
    
  
    
    
    
	    
    
    
    
    
	
	
	
	
	

//	final public void notifyRemoveAll(View view) {
//		Iterator it = cons.getGeoSetConstructionOrder().iterator();
//		while (it.hasNext()) {
//			GeoElement geo = (GeoElement) it.next();
//			view.remove(geo);
//		}	
//	}

	/**
	 * Tells views to update all labeled elements of current construction.
	 *
	final public static void notifyUpdateAll() {
		notifyUpdate(kernelConstruction.getAllGeoElements());
	}*/

	
	
	
	
	/**
	 * Creates a new algorithm that uses the given macro.
	 * @return output of macro algorithm
	 */
	@Override
	final public GeoElement [] useMacro(String [] labels, MacroInterface macro, GeoElement [] input) {		
		try {
			AlgoMacro algo = new AlgoMacro(cons, labels, (Macro)macro, input);
			return algo.getOutput();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}						
	}
	
	
	/** Implicit Polynomial  */
	final public GeoImplicitPoly ImplicitPoly(String label,Polynomial poly) {
		GeoImplicitPoly implicitPoly = new GeoImplicitPoly((Construction)cons, label, poly);
		return implicitPoly;
	}	

	
	/********************
	 * ALGORITHMIC PART *
	 ********************/


	
	/** 
	 * Sequence command:
 	 * Sequence[ <expression>, <number-var>, <from>, <to>, <step> ]  
 	 * @return array with GeoList object and its list items
	 */
	final public GeoElement [] Sequence(String label, 
			GeoElement expression, GeoNumeric localVar, 
			NumberValue from, NumberValue to, NumberValue step) {
		
			AlgoSequence algo = new AlgoSequence((Construction)cons, label, expression, localVar, from, to, step);
			return algo.getOutput();	
	}	

	
	
		
	@Override
	final public GeoElement  DependentImplicitPoly(String label, Equation equ) {
		AlgoDependentImplicitPoly algo = new AlgoDependentImplicitPoly((Construction)cons, label, equ);
		GeoElement geo = algo.getGeo();
		return geo;
	}


	
	/** 
	 * Name of geo.
	 */
	final public GeoText Name(
		String label,
		GeoElement geo) {
		AlgoName algo = new AlgoName((Construction)cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Object from name
	 */
	final public GeoElement Object(
		String label,
		GeoText text) {
		AlgoObject algo = new AlgoObject((Construction)cons, label, text);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	
	
	
	/** 
	 * LaTeX of geo.
	 */
	final public GeoText LaTeX(
		String label,
		GeoElement geo, GeoBoolean substituteVars, GeoBoolean showName) {
		AlgoLaTeX algo = new AlgoLaTeX((Construction)cons, label, geo, substituteVars, showName);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * LaTeX of geo.
	 */
	final public GeoText LaTeX(
		String label,
		GeoElement geo) {
		AlgoLaTeX algo = new AlgoLaTeX((Construction)cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo) {
		AlgoText algo = new AlgoText((Construction)cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoBoolean substituteVars) {
		AlgoText algo = new AlgoText((Construction)cons, label, geo, substituteVars);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoPoint2 p, GeoBoolean substituteVars) {
		AlgoText algo = new AlgoText((Construction)cons, label, geo, p, substituteVars);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoPoint2 p, GeoBoolean substituteVars, GeoBoolean latex) {
		AlgoText algo = new AlgoText((Construction)cons, label, geo, p, substituteVars, latex);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoPoint2 p) {
		AlgoText algo = new AlgoText((Construction)cons, label, geo, p);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Row of geo.
	 */
	final public GeoNumeric Row(
		String label,
		GeoElement geo) {
		AlgoRow algo = new AlgoRow(cons, label, geo);
		GeoNumeric ret = algo.getResult();
		return ret;
	}
	

	
	/** 
	 * ToNumber
	 */
	final public GeoNumeric LetterToUnicode(
		String label,
		GeoText geo) {
		AlgoLetterToUnicode algo = new AlgoLetterToUnicode((Construction)cons, label, geo);
		GeoNumeric ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ToNumbers
	 */
	final public GeoList TextToUnicode(
		String label,
		GeoText geo) {
		AlgoTextToUnicode algo = new AlgoTextToUnicode((Construction)cons, label, geo);
		GeoList ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ToText(number)
	 */
	final public GeoText UnicodeToLetter(String label, NumberValue a) {
		AlgoUnicodeToLetter algo = new AlgoUnicodeToLetter((Construction)cons, label, a);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * ToText(list)
	 */
	final public GeoText UnicodeToText(
		String label,
		GeoList geo) {
		AlgoUnicodeToText algo = new AlgoUnicodeToText((Construction)cons, label, geo);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Ordinal(list)
	 */
	final public GeoText Ordinal(
		String label,
		GeoNumeric geo) {
		AlgoOrdinal algo = new AlgoOrdinal((Construction)cons, label, geo);
		GeoText ret = algo.getResult();
		return ret;
	}
	


	
		
		
	
	/** Point in region with cartesian coordinates (x,y)   */
	final public GeoPoint2 PointIn(String label, Region region, double x, double y, boolean addToConstruction, boolean complex) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);		

		}
		AlgoPointInRegion algo = new AlgoPointInRegion((Construction)cons, label, region, x, y);
		//Application.debug("PointIn - \n x="+x+"\n y="+y);
		GeoPoint2 p = algo.getP();    
		if (complex) {
			p.setMode(COORD_COMPLEX);
		}
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}
	
	/** Point in region */
	final public GeoPoint2 PointIn(String label, Region region) {  
		return PointIn(label,region,0,0, true, false); //TODO do as for paths
	}	
	


	/** 
	 * Returns the projected point of P on line g (or nearest for a Segment)
	 */
	final public GeoPoint2 ClosestPoint(GeoPoint2 P, GeoLine g) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);		
				
		AlgoClosestPoint cp = new AlgoClosestPoint(cons, g, P);
	
		cons.setSuppressLabelCreation(oldMacroMode);
		return cp.getP();
	}

	
	/** 
	 * Midpoint M = (P + Q)/2
	 */
	final public GeoPoint2 Midpoint(
		String label,
		GeoPoint2 P,
		GeoPoint2 Q) {
		AlgoMidpoint algo = new AlgoMidpoint((Construction)cons, label, P, Q);
		GeoPoint2 M = algo.getPoint();
		return M;
	}
	
	/** 
	 * Creates Midpoint M = (P + Q)/2 without label (for use as e.g. start point)
	 */
	final public GeoPoint2 Midpoint(
		GeoPoint2 P,
		GeoPoint2 Q) {

		boolean oldValue = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		GeoPoint2 midPoint = Midpoint(null, P, Q);
		cons.setSuppressLabelCreation(oldValue);
		return midPoint;
	}
	
	/** 
	 * Midpoint of segment
	 */
	final public GeoPoint2 Midpoint(
		String label,
		GeoSegment s) {
		AlgoMidpointSegment algo = new AlgoMidpointSegment((Construction)cons, label, s);
		GeoPoint2 M = algo.getPoint();
		return M;
	}

	/** 
	 * Midpoint of interval
	 */
	final public GeoNumeric Midpoint(
		String label,
		GeoInterval s) {
		AlgoIntervalMidpoint algo = new AlgoIntervalMidpoint((Construction)cons, label, s);
		GeoNumeric n = algo.getResult();
		return n;
	}

	/** 
	 * Min of interval
	 */
	final public GeoNumeric Min(
		String label,
		GeoInterval s) {
		AlgoIntervalMin algo = new AlgoIntervalMin((Construction)cons, label, s);
		GeoNumeric n = algo.getResult();
		return n;
	}

	/** 
	 * Max of interval
	 */
	final public GeoNumeric Max(
		String label,
		GeoInterval s) {
		AlgoIntervalMax algo = new AlgoIntervalMax((Construction)cons, label, s);
		GeoNumeric n = algo.getResult();
		return n;
	}
	
	
	/** 
	 * DotPlot
	 * G.Sturr 2010-8-10
	 */
	final public GeoList DotPlot(String label, GeoList list) {
		AlgoDotPlot algo = new AlgoDotPlot((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	
	/** 
	 * ResidualPlot
	 * G.Sturr 2011-2-5
	 */
	final public GeoList ResidualPlot(String label, GeoList list, GeoFunction function) {
		AlgoResidualPlot algo = new AlgoResidualPlot((Construction)cons, label, list, function);
		GeoList result = algo.getResult();
		return result;
	}
	
	
	/** 
	 * NormalQuantilePlot
	 * G.Sturr 2011-6-29
	 */
	final public GeoList NormalQuantilePlot(String label, GeoList list) {
		AlgoNormalQuantilePlot algo = new AlgoNormalQuantilePlot((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	
	
	/** 
	 * UpperSum of function f 
	 */
	final public GeoNumeric UpperSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumUpper algo = new AlgoSumUpper((Construction)cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * TrapezoidalSum of function f 
	 */
	final public GeoNumeric TrapezoidalSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumTrapezoidal algo = new AlgoSumTrapezoidal((Construction)cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}	

	/** 
	 * LowerSum of function f 
	 */
	final public GeoNumeric LowerSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumLower algo = new AlgoSumLower((Construction)cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}	
	/** 
	 * LeftSum of function f 
	 * Ulven 09.02.11
	 */
	final public GeoNumeric LeftSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumLeft algo = new AlgoSumLeft((Construction)cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}	
	
	/** 
	 * RectangleSum of function f 
	 * Ulven 09.02.11
	 */
	final public GeoNumeric RectangleSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n,NumberValue d) {
		AlgoSumRectangle algo = new AlgoSumRectangle((Construction)cons, label, f, a, b, n,d);
		GeoNumeric sum = algo.getSum();
		return sum;
	}	
	
	/**
	 * SumSquaredErrors[<List of Points>,<Function>]
	 * Hans-Petter Ulven
	 * 2010-02-22
	 */
	final public GeoNumeric SumSquaredErrors(String label, GeoList list, GeoFunctionable function) {
		AlgoSumSquaredErrors algo = new AlgoSumSquaredErrors((Construction)cons, label, list, function);
		GeoNumeric sse=algo.getsse();
		return sse;
	}	

	/**
	 * RSquare[<List of Points>,<Function>]
	 */
	final public GeoNumeric RSquare(String label, GeoList list, GeoFunctionable function) {
		AlgoRSquare algo = new AlgoRSquare((Construction)cons, label, list, function);
		GeoNumeric r2=algo.getRSquare();
		return r2;
	}	

	
	/**
	 * ResidualPlot[<List of Points>,<Function>]
	 */
	final public GeoList ResidualPlot(String label, GeoList list, GeoFunctionable function) {
		AlgoResidualPlot algo = new AlgoResidualPlot((Construction)cons, label, list, function);
		GeoList result = algo.getResult();
		return result;
	}	

	
	
	
	/** 
	 * unit vector of line g
	 */
	final public GeoVector UnitVector(String label, GeoLine g) {
		AlgoUnitVectorLine algo = new AlgoUnitVectorLine((Construction)cons, label, g);
		GeoVector v = algo.getVector();
		return v;
	}

	/** 
	 * unit vector of vector v
	 */
	final public GeoVector UnitVector(String label, GeoVector v) {
		AlgoUnitVectorVector algo = new AlgoUnitVectorVector((Construction)cons, label, v);
		GeoVector u = algo.getVector();
		return u;
	}

	/** 
	 * orthogonal vector of line g
	 */
	final public GeoVector OrthogonalVector(String label, GeoLine g) {
		AlgoOrthoVectorLine algo = new AlgoOrthoVectorLine((Construction)cons, label, g);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * orthogonal vector of vector v
	 */
	final public GeoVector OrthogonalVector(String label, GeoVector v) {
		AlgoOrthoVectorVector algo = new AlgoOrthoVectorVector((Construction)cons, label, v);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * unit orthogonal vector of line g
	 */
	final public GeoVector UnitOrthogonalVector(
		String label,
		GeoLine g) {
		AlgoUnitOrthoVectorLine algo = new AlgoUnitOrthoVectorLine((Construction)cons, label, g);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * unit orthogonal vector of vector v
	 */
	final public GeoVector UnitOrthogonalVector(
		String label,
		GeoVector v) {
		AlgoUnitOrthoVectorVector algo =
			new AlgoUnitOrthoVectorVector((Construction)cons, label, v);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * Length named label of vector v
	 */
	final public GeoNumeric Length(String label, GeoVec3D v) {
		AlgoLengthVector algo = new AlgoLengthVector((Construction)cons, label, v);
		GeoNumeric num = algo.getLength();
		return num;
	}
	
	/** 
	 * Length named label of segment seg
	 */
	final public GeoNumeric Length(String label, GeoSegmentND seg) {
		AlgoLengthSegment algo = new AlgoLengthSegment((Construction)cons, label, seg);
		GeoNumeric num = algo.getLength();
		return num;
	}



	
	/** 
	 * Mod[a, b]
	 */
	final public GeoNumeric Mod(String label, NumberValue a, NumberValue b) {
		AlgoMod algo = new AlgoMod((Construction)cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}

	
	/** 
	 * Mod[a, b] Polynomial remainder
	 */
	final public GeoFunction Mod(String label, GeoFunction a, GeoFunction b) {
		AlgoPolynomialMod algo = new AlgoPolynomialMod((Construction)cons, label, a, b);
		GeoFunction f = algo.getResult();
		return f;
	}
	

	
	/** 
	 * Min[a, b]
	 */
	final public GeoNumeric Min(String label, NumberValue a, NumberValue b) {
		AlgoMin algo = new AlgoMin((Construction)cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Min[list]
	 */
	final public GeoNumeric Min(String label, GeoList list) {
		AlgoListMin algo = new AlgoListMin((Construction)cons, label, list);
		GeoNumeric num = algo.getMin();
		return num;
	}

	/**
	 *  Min[function,left,right]
	 *  Ulven 20.02.11
	 *  4.0: Numerical minimum of function in open interval <a,b>
	 */
	final public GeoPoint2 Min(String label, GeoFunction f, NumberValue a, NumberValue b){
		AlgoFunctionMin algo = new AlgoFunctionMin((Construction)cons, label, f, a, b);
		GeoPoint2 minpoint = algo.getPoint();
		return minpoint;
	}//Min(GeoFunction,a,b)
	
	/** 
	 * Max[a, b]
	 */
	final public GeoNumeric Max(String label, NumberValue a, NumberValue b) {
		AlgoMax algo = new AlgoMax((Construction)cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Max[list]
	 */
	final public GeoNumeric Max(String label, GeoList list) {
		AlgoListMax algo = new AlgoListMax((Construction)cons, label, list);
		GeoNumeric num = algo.getMax();
		return num;
	}

	/**
	 *  Max[function,left,right]
	 *  Ulven 20.02.11
	 *  4.0: Numerical maximum of function in open interval <a,b>
	 */
	final public GeoPoint2 Max(String label, GeoFunction f, NumberValue a, NumberValue b){
		AlgoFunctionMax algo = new AlgoFunctionMax((Construction)cons, label, f, a, b);
		GeoPoint2 maxpoint = algo.getPoint();
		return maxpoint;
	}//Max(GeoFunction,a,b)
	
	/** 
	 * LCM[a, b]
	 * Michael Borcherds
	 */
	final public GeoNumeric LCM(String label, NumberValue a, NumberValue b) {
		AlgoLCM algo = new AlgoLCM((Construction)cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * LCM[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric LCM(String label, GeoList list) {
		AlgoListLCM algo = new AlgoListLCM((Construction)cons, label, list);
		GeoNumeric num = algo.getLCM();
		return num;
	}

	

	
	/** 
	 * SigmaXY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXY(String label, GeoList list) {
		AlgoListSigmaXY algo = new AlgoListSigmaXY((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaYY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaYY(String label, GeoList list) {
		AlgoListSigmaYY algo = new AlgoListSigmaYY((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}

	
	
	/** 
	 * Spearman[list]
	 * G. Sturr
	 */
	final public GeoNumeric Spearman(String label, GeoList list) {
		AlgoSpearman algo = new AlgoSpearman((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Spearman[list, list]
	 * G. Sturr
	 */
	final public GeoNumeric Spearman(String label, GeoList list, GeoList list2) {
		AlgoSpearman algo = new AlgoSpearman((Construction)cons, label, list, list2);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	
	

	
	/** 
	 * SigmaXY[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXY(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSigmaXY algo = new AlgoDoubleListSigmaXY((Construction)cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaXX[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXX(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSigmaXX algo = new AlgoDoubleListSigmaXX((Construction)cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaYY[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaYY(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSigmaYY algo = new AlgoDoubleListSigmaYY((Construction)cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	

	
	/** 
	 * FitLineY[list of coords]
	 * Michael Borcherds
	 */
	final public GeoLine FitLineY(String label, GeoList list) {
		AlgoFitLineY algo = new AlgoFitLineY((Construction)cons, label, list);
		GeoLine line = algo.getFitLineY();
		return line;
	}
	
	/** 
	 * FitLineX[list of coords]
	 * Michael Borcherds
	 */
	final public GeoLine FitLineX(String label, GeoList list) {
		AlgoFitLineX algo = new AlgoFitLineX((Construction)cons, label, list);
		GeoLine line = algo.getFitLineX();
		return line;
	}
	
	final public GeoLocus Voronoi(String label, GeoList list) {
		AlgoVoronoi algo = new AlgoVoronoi((Construction)cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus Hull(String label, GeoList list, GeoNumeric percent) {
		AlgoHull algo = new AlgoHull((Construction)cons, label, list, percent);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus TravelingSalesman(String label, GeoList list) {
		AlgoTravelingSalesman algo = new AlgoTravelingSalesman((Construction)cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus ConvexHull(String label, GeoList list) {
		AlgoConvexHull algo = new AlgoConvexHull((Construction)cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus MinimumSpanningTree(String label, GeoList list) {
		AlgoMinimumSpanningTree algo = new AlgoMinimumSpanningTree((Construction)cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus ShortestDistance(String label, GeoList list, GeoPointND start, GeoPointND end, GeoBoolean weighted) {
		AlgoShortestDistance algo = new AlgoShortestDistance((Construction)cons, label, list, start, end, weighted);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus DelauneyTriangulation(String label, GeoList list) {
		AlgoDelauneyTriangulation algo = new AlgoDelauneyTriangulation((Construction)cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * FitPoly[list of coords,degree]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitPoly(String label, GeoList list, NumberValue degree) {
		AlgoFitPoly algo = new AlgoFitPoly((Construction)cons, label, list, degree);
		GeoFunction function = algo.getFitPoly();
		return function;
	}

	/** 
	 * FitExp[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitExp(String label, GeoList list) {
		AlgoFitExp algo = new AlgoFitExp((Construction)cons, label, list);
		GeoFunction function = algo.getFitExp();
		return function;
	}
   
	/** 
	 * FitLog[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitLog(String label, GeoList list) {
		AlgoFitLog algo = new AlgoFitLog((Construction)cons, label, list);
		GeoFunction function = algo.getFitLog();
		return function;
	}
	/** 
	 * FitPow[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitPow(String label, GeoList list) {
		AlgoFitPow algo = new AlgoFitPow((Construction)cons, label, list);
		GeoFunction function = algo.getFitPow();
		return function;
	}

	/** 
	 * FitSin[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitSin(String label, GeoList list) {
		AlgoFitSin algo = new AlgoFitSin((Construction)cons, label, list);
		GeoFunction function = algo.getFitSin();
		return function;
	}
	
	/** 
	 * FitLogistic[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitLogistic(String label, GeoList list) {
		AlgoFitLogistic algo = new AlgoFitLogistic((Construction)cons, label, list);
		GeoFunction function = algo.getFitLogistic();
		return function;
	}	
	
	/** 
	 * Fit[list of points,list of functions]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction Fit(String label, GeoList ptslist,GeoList funclist) {
		AlgoFit algo = new AlgoFit((Construction)cons, label, ptslist,funclist);
		GeoFunction function = algo.getFit();
		return function;
	}	
	
	/** 
	 * Fit[list of points,function]
	 * NonLinear case, one function with glider parameters
	 * Hans-Petter Ulven
	 */
	final public GeoFunction Fit(String label, GeoList ptslist,GeoFunction function) {
		AlgoFitNL algo = new AlgoFitNL((Construction)cons, label, ptslist,function);
		GeoFunction geofunction = algo.getFitNL();
		return geofunction;
	}	

	/**
	 * 'FitGrowth[<List of Points>]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitGrowth(String label, GeoList list) {
		AlgoFitGrowth algo = new AlgoFitGrowth((Construction)cons, label, list);
		GeoFunction function=algo.getFitGrowth();
		return function;
	}
	

	
	/** 
	 * RandomPoisson[lambda]
	 * Michael Borcherds
	 */
	final public GeoNumeric RandomPoisson(String label, NumberValue a) {
		AlgoRandomPoisson algo = new AlgoRandomPoisson((Construction)cons, label, a);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** 
	 * InverseNormal[mean,variance,x]
	 * Michael Borcherds
	 */
	final public GeoNumeric InverseNormal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseNormal algo = new AlgoInverseNormal((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Normal[mean,variance,x]
	 * Michael Borcherds
	 */
	final public GeoNumeric Normal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoNormal algo = new AlgoNormal((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * TDistribution[degrees of freedom,x]
	 * Michael Borcherds
	 */
	final public GeoNumeric TDistribution(String label, NumberValue a, NumberValue b) {
		AlgoTDistribution algo = new AlgoTDistribution((Construction)cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseTDistribution(String label, NumberValue a, NumberValue b) {
		AlgoInverseTDistribution algo = new AlgoInverseTDistribution((Construction)cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}

	
	final public GeoNumeric InverseChiSquared(String label, NumberValue a, NumberValue b) {
		AlgoInverseChiSquared algo = new AlgoInverseChiSquared((Construction)cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}

	
	final public GeoNumeric InverseExponential(String label, NumberValue a, NumberValue b) {
		AlgoInverseExponential algo = new AlgoInverseExponential((Construction)cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseFDistribution(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseFDistribution algo = new AlgoInverseFDistribution((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseGamma(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseGamma algo = new AlgoInverseGamma((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}

	
	final public GeoNumeric InverseCauchy(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseCauchy algo = new AlgoInverseCauchy((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Weibull(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoWeibull algo = new AlgoWeibull((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseWeibull(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseWeibull algo = new AlgoInverseWeibull((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Zipf(String label, NumberValue a, NumberValue b, NumberValue c, GeoBoolean isCumulative) {
		AlgoZipf algo = new AlgoZipf((Construction)cons, label, a, b, c, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Zipf(String label, NumberValue a, NumberValue b) {
		AlgoZipfBarChart algo = new AlgoZipfBarChart((Construction)cons, label, a, b);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	final public GeoNumeric Zipf(String label, NumberValue a, NumberValue b, GeoBoolean cumulative) {
		AlgoZipfBarChart algo = new AlgoZipfBarChart((Construction)cons, label, a, b, cumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	final public GeoNumeric InverseZipf(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseZipf algo = new AlgoInverseZipf((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Pascal[] probability */
	final public GeoNumeric Pascal(String label, NumberValue a, NumberValue b, NumberValue c, GeoBoolean isCumulative) {
		AlgoPascal algo = new AlgoPascal((Construction)cons, label, a, b, c, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Pascal[] bar chart */
	final public GeoNumeric Pascal(String label, NumberValue a, NumberValue b) {
		AlgoPascalBarChart algo = new AlgoPascalBarChart((Construction)cons, label, a, b);
		GeoNumeric num = algo.getSum();
		return num;
	}
	/** Pascal[] bar chart with cumulative option */
	final public GeoNumeric Pascal(String label, NumberValue a, NumberValue b, GeoBoolean isCumulative) {
		AlgoPascalBarChart algo = new AlgoPascalBarChart((Construction)cons, label, a, b, isCumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	
	final public GeoNumeric InversePascal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInversePascal algo = new AlgoInversePascal((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Poisson[] probability */
	final public GeoNumeric Poisson(String label, NumberValue a, NumberValue b, GeoBoolean isCumulative) {
		AlgoPoisson algo = new AlgoPoisson((Construction)cons, label, a, b, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Poisson[] bar chart */
	final public GeoNumeric Poisson(String label, NumberValue a) {
		AlgoPoissonBarChart algo = new AlgoPoissonBarChart((Construction)cons, label, a);
		GeoNumeric num = algo.getSum();
		return num;
	}
	/** Poisson[] bar chart with cumulative option */
	final public GeoNumeric Poisson(String label, NumberValue a, GeoBoolean isCumulative) {
		AlgoPoissonBarChart algo = new AlgoPoissonBarChart((Construction)cons, label, a, isCumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	
	
	final public GeoNumeric InversePoisson(String label, NumberValue a, NumberValue b) {
		AlgoInversePoisson algo = new AlgoInversePoisson((Construction)cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** HyperGeometric[] probability */
	final public GeoNumeric HyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c, NumberValue d,
			GeoBoolean isCumulative) {
		AlgoHyperGeometric algo = new AlgoHyperGeometric((Construction)cons, label, a, b, c, d, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	/** HyperGeometric[] bar chart */
	final public GeoNumeric HyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoHyperGeometricBarChart algo = new AlgoHyperGeometricBarChart((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getSum();
		return num;
	}
	/** HyperGeometric[] bar chart with cumulative option */
	final public GeoNumeric HyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c, GeoBoolean isCumulative) {
		AlgoHyperGeometricBarChart algo = new AlgoHyperGeometricBarChart((Construction)cons, label, a, b, c, isCumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	
	
	
	
	final public GeoNumeric InverseHyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c, NumberValue d) {
		AlgoInverseHyperGeometric algo = new AlgoInverseHyperGeometric((Construction)cons, label, a, b, c, d);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Binomial[] probability */
	final public GeoNumeric BinomialDist(String label, NumberValue a, NumberValue b, NumberValue c, GeoBoolean isCumulative) {
		AlgoBinomialDist algo = new AlgoBinomialDist((Construction)cons, label, a, b, c, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	


	public GeoNumeric Bernoulli(String label, NumberValue probability,
			GeoBoolean cumulative) {
		AlgoBernoulliBarChart algo = new AlgoBernoulliBarChart((Construction)cons, label, probability, cumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	/** Binomial[] bar chart */
	final public GeoNumeric BinomialDist(String label, NumberValue a, NumberValue b) {
		AlgoBinomialDistBarChart algo = new AlgoBinomialDistBarChart((Construction)cons, label, a, b);
		GeoNumeric num = algo.getSum();
		return num;
	}
	/** Binomial[] bar chart with cumulative option */
	final public GeoNumeric BinomialDist(String label, NumberValue a, NumberValue b, GeoBoolean isCumulative) {
		AlgoBinomialDistBarChart algo = new AlgoBinomialDistBarChart((Construction)cons, label, a, b, isCumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	
	final public GeoNumeric InverseBinomial(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseBinomial algo = new AlgoInverseBinomial((Construction)cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** ANOVATest[]  */
	final public GeoList ANOVATest(String label, GeoList dataArrayList) {
		AlgoANOVA algo = new AlgoANOVA((Construction)cons, label, dataArrayList);
		GeoList result = algo.getResult();
		return result;
	}
	
	/** TTest[] with sample data */
	final public GeoList TTest(String label, GeoList sampleList, GeoNumeric hypMean, GeoText tail) {
		AlgoTTest algo = new AlgoTTest((Construction)cons, label, sampleList, hypMean, tail);
		GeoList result = algo.getResult();
		return result;
	}
	
	/** TTest[] with sample statistics */
	final public GeoList TTest(String label, GeoNumeric mean, GeoNumeric sd, GeoNumeric n, GeoNumeric hypMean, GeoText tail) {
		AlgoTTest algo = new AlgoTTest((Construction)cons, label, mean, sd, n, hypMean, tail);
		GeoList result = algo.getResult();
		return result;
	}
	

	/** TTestPaired[] */
	final public GeoList TTestPaired(String label, GeoList sampleList1, GeoList sampleList2, GeoText tail) {
		AlgoTTestPaired algo = new AlgoTTestPaired((Construction)cons, label, sampleList1, sampleList2, tail);
		GeoList result = algo.getResult();
		return result;
	}
	
	/** TTest2[] with sample data */
	final public GeoList TTest2(String label, GeoList sampleList1, GeoList sampleList2, GeoText tail, GeoBoolean pooled) {
		AlgoTTest2 algo = new AlgoTTest2((Construction)cons, label, sampleList1, sampleList2, tail, pooled);
		GeoList result = algo.getResult();
		return result;
	}
	
	/** TTest2[] with sample statistics */
	final public GeoList TTest2(String label, GeoNumeric mean1, GeoNumeric sd1, GeoNumeric n1, GeoNumeric mean2, 
			GeoNumeric sd2, GeoNumeric n2, GeoText tail, GeoBoolean pooled) {
		AlgoTTest2 algo = new AlgoTTest2((Construction)cons, label, mean1, mean2, sd1, sd2, n1, n2, tail, pooled);
		GeoList result = algo.getResult();
		return result;
	}
	
	
	/** TMeanEstimate[] with sample data */
	final public GeoList TMeanEstimate(String label, GeoList sampleList, GeoNumeric level) {
		AlgoTMeanEstimate algo = new AlgoTMeanEstimate((Construction)cons, label, sampleList, level);
		GeoList resultList = algo.getResult();
		return resultList;
	}
	
	/** TMeanEstimate[] with sample statistics */
	final public GeoList TMeanEstimate(String label, GeoNumeric mean, GeoNumeric sd, GeoNumeric n, GeoNumeric level) {
		AlgoTMeanEstimate algo = new AlgoTMeanEstimate((Construction)cons, label, mean, sd, n, level);
		GeoList resultList = algo.getResult();
		return resultList;
	}
	
	/** TMean2Estimate[] with sample data */
	final public GeoList TMean2Estimate(String label, GeoList sampleList1, GeoList sampleList2, GeoNumeric level, GeoBoolean pooled) {
		AlgoTMean2Estimate algo = new AlgoTMean2Estimate((Construction)cons, label, sampleList1, sampleList2, level, pooled);
		GeoList resultList = algo.getResult();
		return resultList;
	}
	
	/** TMean2Estimate[] with sample statistics */
	final public GeoList TMean2Estimate(String label, GeoNumeric mean1, GeoNumeric sd1, GeoNumeric n1, 
			GeoNumeric mean2, GeoNumeric sd2, GeoNumeric n2, GeoNumeric level, GeoBoolean pooled) {
		AlgoTMean2Estimate algo = new AlgoTMean2Estimate((Construction)cons, label, mean1, sd1, n1, mean2, sd2, n2,level, pooled);
		GeoList resultList = algo.getResult();
		return resultList;
	}
	
	
	
	/** 
	 * Sort[list]
	 * Michael Borcherds
	 */
	final public GeoList Sort(String label, GeoList list) {
		AlgoSort algo = new AlgoSort((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * OrdinalRank[list]
	 * Michael Borcherds
	 */
	final public GeoList OrdinalRank(String label, GeoList list) {
		AlgoOrdinalRank algo = new AlgoOrdinalRank((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * TiedRank[list]
	 */
	final public GeoList TiedRank(String label, GeoList list) {
		AlgoTiedRank algo = new AlgoTiedRank((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Percentile[list, value]
	 * G. Sturr
	 */
	final public GeoNumeric Percentile(String label, GeoList list, GeoNumeric value) {
		AlgoPercentile algo = new AlgoPercentile((Construction)cons, label, list, value);
		GeoNumeric result = algo.getResult();
		return result;
	}
	
	/** 
	 * Shuffle[list]
	 * Michael Borcherds
	 */
	final public GeoList Shuffle(String label, GeoList list) {
		AlgoShuffle algo = new AlgoShuffle((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * PointList[list]
	 * Michael Borcherds
	 */
	final public GeoList PointList(String label, GeoList list) {
		AlgoPointList algo = new AlgoPointList((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * RootList[list]
	 * Michael Borcherds
	 */
	final public GeoList RootList(String label, GeoList list) {
		AlgoRootList algo = new AlgoRootList((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}


	/** 
	 * Last[string,n]
	 * Michael Borcherds
	 */
	final public GeoText Last(String label, GeoText list, GeoNumeric n) {
		AlgoLastString algo = new AlgoLastString((Construction)cons, label, list, n);
		GeoText list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * First[string,n]
	 * Michael Borcherds
	 */
	final public GeoText Take(String label, GeoText list, GeoNumeric m, GeoNumeric n) {
		AlgoTakeString algo = new AlgoTakeString((Construction)cons, label, list, m, n);
		GeoText list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Last[list,n]
	 * Michael Borcherds
	 */
	final public GeoList Last(String label, GeoList list, GeoNumeric n) {
		AlgoLast algo = new AlgoLast((Construction)cons, label, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Take[list,m,n]
	 * Michael Borcherds
	 */
	final public GeoList Take(String label, GeoList list, GeoNumeric m, GeoNumeric n) {
		AlgoTake algo = new AlgoTake((Construction)cons, label, list, m, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	

	/** 
	 * IndexOf[text,text]
	 */
	final public GeoNumeric IndexOf(String label, GeoText needle, GeoText haystack) {
		AlgoIndexOf algo = new AlgoIndexOf((Construction)cons, label, needle, haystack);
		GeoNumeric index = algo.getResult();
		return index;
	}
	/** 
	 * IndexOf[text,text,start]
	 */
	final public GeoNumeric IndexOf(String label, GeoText needle, GeoText haystack,NumberValue start) {
		AlgoIndexOf algo = new AlgoIndexOf((Construction)cons, label, needle, haystack,start);
		GeoNumeric index = algo.getResult();
		return index;
	}
	/** 
	 * IndexOf[object,list]
	 */
	final public GeoNumeric IndexOf(String label, GeoElement geo, GeoList list) {
		AlgoIndexOf algo = new AlgoIndexOf((Construction)cons, label, geo, list);
		GeoNumeric index = algo.getResult();
		return index;
	}
	/** 
	 * IndexOf[object,list,start]
	 */
	final public GeoNumeric IndexOf(String label, GeoElement geo, GeoList list,NumberValue nv) {
		AlgoIndexOf algo = new AlgoIndexOf((Construction)cons, label, geo, list,nv);
		GeoNumeric index = algo.getResult();
		return index;
	}
	
	
	/** 
	 * Join[list,list]
	 * Michael Borcherds
	 */
	final public GeoList Join(String label, GeoList list) {
		AlgoJoin algo = new AlgoJoin((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	

	
	
	
	/** 
	 * Union[list,list]
	 * Michael Borcherds
	 */
	final public GeoList Union(String label, GeoList list, GeoList list1) {
		AlgoUnion algo = new AlgoUnion((Construction)cons, label, list, list1);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	
	/** 
	 * Intersection[list,list]
	 * Michael Borcherds
	 */
	final public GeoList Intersection(String label, GeoList list, GeoList list1) {
		AlgoIntersection algo = new AlgoIntersection((Construction)cons, label, list, list1);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Insert[list,list,n]
	 * Michael Borcherds
	 */
	final public GeoList Insert(String label, GeoElement geo, GeoList list, GeoNumeric n) {
		AlgoInsert algo = new AlgoInsert((Construction)cons, label, geo, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	

	/** 
	 * RemoveUndefined[list]
	 * Michael Borcherds
	 */
	final public GeoList RemoveUndefined(String label, GeoList list) {
		AlgoRemoveUndefined algo = new AlgoRemoveUndefined((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Keep[boolean condition, list]
	 * Michael Borcherds
	 */
	final public GeoList KeepIf(String label, GeoFunction boolFun, GeoList list) {
		AlgoKeepIf algo = new AlgoKeepIf((Construction)cons, label, boolFun, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	

	
	/** 
	 * IsInteger[number]
	 * Michael Borcherds
	 */
	final public GeoBoolean IsInteger(String label, GeoNumeric geo) {
		AlgoIsInteger algo = new AlgoIsInteger((Construction)cons, label, geo);
		GeoBoolean result = algo.getResult();
		return result;
	}
	
	/** 
	 * Mode[list]
	 * Michael Borcherds
	 */
	final public GeoList Mode(String label, GeoList list) {
		AlgoMode algo = new AlgoMode((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * PrimeFactors[list]
	 * Michael Borcherds
	 */
	final public GeoList PrimeFactors(String label, NumberValue num) {
		AlgoPrimeFactors algo = new AlgoPrimeFactors((Construction)cons, label, num);
		GeoList list2 = algo.getResult();
		return list2;
	}

	
	/** 
	 * Invert[matrix]
	 * Michael Borcherds
	 */
	final public GeoList Invert(String label, GeoList list) {
		AlgoInvert algo = new AlgoInvert((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Transpose[matrix]
	 * Michael Borcherds
	 */
	final public GeoList Transpose(String label, GeoList list) {
		AlgoTranspose algo = new AlgoTranspose((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Transpose[matrix]
	 * Michael Borcherds
	 */
	final public GeoList ReducedRowEchelonForm(String label, GeoList list) {
		AlgoReducedRowEchelonForm algo = new AlgoReducedRowEchelonForm((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Transpose[matrix]
	 * Michael Borcherds
	 */
	final public GeoNumeric Determinant(String label, GeoList list) {
		AlgoDeterminant algo = new AlgoDeterminant((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Reverse[list]
	 * Michael Borcherds
	 */
	final public GeoList Reverse(String label, GeoList list) {
		AlgoReverse algo = new AlgoReverse((Construction)cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Product[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Product(String label, GeoList list) {
		AlgoProduct algo = new AlgoProduct((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Product[list,n]
	 * Zbynek Konecny
	 */
	final public GeoNumeric Product(String label, GeoList list,GeoNumeric n) {
		AlgoProduct algo = new AlgoProduct((Construction)cons, label, list,n);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Sum[list]
	 * Michael Borcherds
	 */
	final public GeoElement Sum(String label, GeoList list) {
		AlgoSum algo = new AlgoSum((Construction)cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list,n]
	 * Michael Borcherds
	 */
	final public GeoElement Sum(String label, GeoList list, GeoNumeric n) {
		AlgoSum algo = new AlgoSum((Construction)cons, label, list, n);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of functions]
	 * Michael Borcherds
	 */
	final public GeoElement SumFunctions(String label, GeoList list) {
		AlgoSumFunctions algo = new AlgoSumFunctions((Construction)cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of functions,n]
	 * Michael Borcherds
	 */
	final public GeoElement SumFunctions(String label, GeoList list, GeoNumeric num) {
		AlgoSumFunctions algo = new AlgoSumFunctions((Construction)cons, label, list, num);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of points]
	 * Michael Borcherds
	 */
	final public GeoElement SumPoints(String label, GeoList list) {
		AlgoSumPoints algo = new AlgoSumPoints((Construction)cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of points,n]
	 * Michael Borcherds
	 */
	final public GeoElement SumPoints(String label, GeoList list, GeoNumeric num) {
		AlgoSumPoints algo = new AlgoSumPoints((Construction)cons, label, list, num);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of points]
	 * Michael Borcherds
	 */
	final public GeoElement SumText(String label, GeoList list) {
		AlgoSumText algo = new AlgoSumText((Construction)cons, label, list);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of text,n]
	 * Michael Borcherds
	 */
	final public GeoElement SumText(String label, GeoList list, GeoNumeric num) {
		AlgoSumText algo = new AlgoSumText((Construction)cons, label, list, num);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sample[list,n]
	 * Michael Borcherds
	 */
	final public GeoElement Sample(String label, GeoList list, NumberValue n) {
		AlgoSample algo = new AlgoSample((Construction)cons, label, list, n, null);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sample[list,n, withReplacement]
	 * Michael Borcherds
	 */
	final public GeoElement Sample(String label, GeoList list, NumberValue n, GeoBoolean withReplacement) {
		AlgoSample algo = new AlgoSample((Construction)cons, label, list, n, withReplacement);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Table[list]
	 * Michael Borcherds
	 */
	final public GeoText TableText(String label, GeoList list, GeoText args) {
		AlgoTableText algo = new AlgoTableText((Construction)cons, label, list, args);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * StemPlot[list]
	 * Michael Borcherds
	 */
	final public GeoText StemPlot(String label, GeoList list) {
		AlgoStemPlot algo = new AlgoStemPlot((Construction)cons, label, list, null);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * StemPlot[list, number]
	 * Michael Borcherds
	 */
	final public GeoText StemPlot(String label, GeoList list, GeoNumeric num) {
		AlgoStemPlot algo = new AlgoStemPlot((Construction)cons, label, list, num);
		GeoText text = algo.getResult();
		return text;
	}
	
	
	/** 
	 * Frequency[dataList]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency((Construction)cons, label, null, null, dataList);
		GeoList list = algo.getResult();
		return list;
	}
	
	/** 
	 * Frequency[isCumulative, dataList]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label,GeoBoolean isCumulative, GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency((Construction)cons, label, isCumulative, null, dataList);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	/** 
	 * Frequency[classList, dataList]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoList classList, GeoList dataList ) {
		AlgoFrequency algo = new AlgoFrequency((Construction)cons, label, null, classList, dataList);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	/** 
	 * Frequency[classList, dataList, useDensity]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity) {
		AlgoFrequency algo = new AlgoFrequency((Construction)cons, label, null, classList, dataList, useDensity, null);
		GeoList list = algo.getResult();
		return list;
	}

	/** 
	 * Frequency[classList, dataList, useDensity, scaleFactor]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequency algo = new AlgoFrequency((Construction)cons, label, null, classList, dataList, useDensity, scaleFactor);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	/** 
	 * Frequency[isCumulative, classList, dataList]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency((Construction)cons, label, isCumulative, classList, dataList, null, null);
		GeoList list = algo.getResult();
		return list;
	}
	

	
	/** 
	 * Frequency[isCumulative, classList, dataList, useDensity]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity) {
		AlgoFrequency algo = new AlgoFrequency((Construction)cons, label, isCumulative, classList, dataList, useDensity, null);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	
	/** 
	 * Frequency[isCumulative, classList, dataList, useDensity, scaleFactor]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequency algo = new AlgoFrequency((Construction)cons, label, isCumulative, classList, dataList, useDensity, scaleFactor);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	/** 
	 * FrequencyTable[dataList]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoList dataList) {		
		AlgoFrequencyTable algo = new AlgoFrequencyTable((Construction)cons, label, null, null, dataList);
		GeoText table = algo.getResult();
		return table;
	}
	
	/** 
	 * FrequencyTable[isCumulative, dataList]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label,GeoBoolean isCumulative, GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable((Construction)cons, label, isCumulative, null, dataList);
		GeoText table = algo.getResult();
		return table;
	}
	
	
	/** 
	 * FrequencyTable[classList, dataList]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoList classList, GeoList dataList ) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable((Construction)cons, label, null, classList, dataList);
		GeoText table = algo.getResult();
		return table;
	}
	
	
	/** 
	 * FrequencyTable[classList, dataList, useDensity]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable((Construction)cons, label, null, classList, dataList, useDensity, null);
		GeoText table = algo.getResult();
		return table;
	}

	/** 
	 * FrequencyTable[classList, dataList, useDensity, scaleFactor]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable((Construction)cons, label, null, classList, dataList, useDensity, scaleFactor);
		GeoText table = algo.getResult();
		return table;
	}
	
	
	/** 
	 * FrequencyTable[isCumulative, classList, dataList]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable((Construction)cons, label, isCumulative, classList, dataList, null, null);
		GeoText table = algo.getResult();
		return table;
	}
	

	
	/** 
	 * FrequencyTable[isCumulative, classList, dataList, useDensity]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable((Construction)cons, label, isCumulative, classList, dataList, useDensity, null);
		GeoText table = algo.getResult();
		return table;
	}
	
	
	
	/** 
	 * FrequencyTable[isCumulative, classList, dataList, useDensity, scaleFactor]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable((Construction)cons, label, isCumulative, classList, dataList, useDensity, scaleFactor);
		GeoText table = algo.getResult();
		return table;
	}
	
		
	/** 
	 * Unique[dataList]
	 * G. Sturr
	 */
	final public GeoList Unique(String label, GeoList dataList) {
		AlgoUnique algo = new AlgoUnique((Construction)cons, label, dataList);
		GeoList list = algo.getResult();
		return list;
	}
	

	

	
	
	

	
	/** 
	 * SurdText[number]
	 * Kai Chung Tam
	 */
	final public GeoText SurdText(String label, GeoNumeric num) {
		AlgoSurdText algo = new AlgoSurdText((Construction)cons, label, num);
		GeoText text = algo.getResult();
		return text;
	}
	
	
	/** 
	 * SurdText[Point]
	 */
	final public GeoText SurdText(String label, GeoPoint2 p) {
		AlgoSurdTextPoint algo = new AlgoSurdTextPoint((Construction)cons, label, p);
		GeoText text = algo.getResult();
		return text;
	}
	
	
	/** 
	 * Mean[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Mean(String label, GeoList list) {
		AlgoMean algo = new AlgoMean((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoText VerticalText(String label, GeoText args) {
		AlgoVerticalText algo = new AlgoVerticalText((Construction)cons, label, args);
		GeoText text = algo.getResult();
		return text;
	}
	
	final public GeoText RotateText(String label, GeoText args, GeoNumeric angle) {
		AlgoRotateText algo = new AlgoRotateText((Construction)cons, label, args, angle);
		GeoText text = algo.getResult();
		return text;
	}
	
	
	/** 
	 * Variance[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Variance(String label, GeoList list) {
		AlgoVariance algo = new AlgoVariance((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SampleVariance[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SampleVariance(String label, GeoList list) {
		AlgoSampleVariance algo = new AlgoSampleVariance((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SD[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric StandardDeviation(String label, GeoList list) {
		AlgoStandardDeviation algo = new AlgoStandardDeviation((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SampleSD[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SampleStandardDeviation(String label, GeoList list) {
		AlgoSampleStandardDeviation algo = new AlgoSampleStandardDeviation((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaXX[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXX(String label, GeoList list) {
		GeoNumeric num;
		GeoElement geo = list.get(0);
		if (geo.isNumberValue())
		{  // list of numbers
			AlgoSigmaXX algo = new AlgoSigmaXX((Construction)cons, label, list);
			num = algo.getResult();
		}
		else
		{  // (probably) list of points
			AlgoListSigmaXX algo = new AlgoListSigmaXX((Construction)cons, label, list);			
			num = algo.getResult();
		}
		return num;
	}
	
	/** 
	 * Median[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Median(String label, GeoList list) {
		AlgoMedian algo = new AlgoMedian((Construction)cons, label, list);
		GeoNumeric num = algo.getMedian();
		return num;
	}
	
	/** 
	 * Q1[list] lower quartile
	 * Michael Borcherds
	 */
	final public GeoNumeric Q1(String label, GeoList list) {
		AlgoQ1 algo = new AlgoQ1((Construction)cons, label, list);
		GeoNumeric num = algo.getQ1();
		return num;
	}
	
	/** 
	 * Q3[list] upper quartile
	 * Michael Borcherds
	 */
	final public GeoNumeric Q3(String label, GeoList list) {
		AlgoQ3 algo = new AlgoQ3((Construction)cons, label, list);
		GeoNumeric num = algo.getQ3();
		return num;
	}
	
	/** 
	 * GeometricMean[list]
	 * G. Sturr
	 */
	final public GeoNumeric GeometricMean(String label, GeoList list) {
		AlgoGeometricMean algo = new AlgoGeometricMean((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * HarmonicMean[list]
	 * G. Sturr
	 */
	final public GeoNumeric HarmonicMean(String label, GeoList list) {
		AlgoHarmonicMean algo = new AlgoHarmonicMean((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/**
	 * 
	 * @param label
	 * @param list
	 * @return
	 */
	final public GeoNumeric RootMeanSquare(String label, GeoList list) {
		AlgoRootMeanSquare algo = new AlgoRootMeanSquare((Construction)cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** 
	 * Iteration[ f(x), x0, n ] 
	 */
	final public GeoNumeric Iteration(String label, GeoFunction f, NumberValue start,
			NumberValue n) {
		AlgoIteration algo = new AlgoIteration((Construction)cons, label, f, start, n);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * IterationList[ f(x), x0, n ] 
	 */
	final public GeoList IterationList(String label, GeoFunction f, NumberValue start,
			NumberValue n) {
		AlgoIterationList algo = new AlgoIterationList((Construction)cons, label, f, start, n);
		return algo.getResult();				
	}
	
	/** 
	 * RandomElement[list]
	 */
	final public GeoElement RandomElement(String label, GeoList list) {
		AlgoRandomElement algo = new AlgoRandomElement((Construction)cons, label, list);
		GeoElement geo = algo.getElement();
		return geo;
	}		
	
	
	
	/** 
	 * SelectedElement[list]
	 */
	final public GeoElement SelectedElement(String label, GeoList list) {
		AlgoSelectedElement algo = new AlgoSelectedElement((Construction)cons, label, list);
		GeoElement geo = algo.getElement();
		return geo;
	}		
	
	/** 
	 * SelectedElement[list]
	 */
	final public GeoElement SelectedIndex(String label, GeoList list) {
		AlgoSelectedIndex algo = new AlgoSelectedIndex((Construction)cons, label, list);
		GeoElement geo = algo.getElement();
		return geo;
	}		
	
	
	/** 
	 * Length[list]
	 */
	final public GeoNumeric Length(String label, GeoList list) {
		AlgoListLength algo = new AlgoListLength((Construction)cons, label, list);
		return algo.getLength();
	}
	
	/** 
	 * Length[locus]
	 */
	final public GeoNumeric Length(String label, GeoLocus locus) {
		AlgoLengthLocus algo = new AlgoLengthLocus((Construction)cons, label, locus);
		return algo.getLength();
	}
	
	/** 
	 * Element[text, number]
	 */
	final public GeoElement Element(String label, GeoText text, NumberValue n) {
		AlgoTextElement algo = new AlgoTextElement((Construction)cons, label, text, n);
		GeoElement geo = algo.getText();
		return geo;
	}		
	
	/** 
	 * Length[text]
	 */
	final public GeoNumeric Length(String label, GeoText text) {
		AlgoTextLength algo = new AlgoTextLength((Construction)cons, label, text);
		return algo.getLength();
	}
	
	// PhilippWeissenbacher 2007-04-10
	
	
	/**
	 * Path Parameter for eg point on circle
	 */
	final public GeoNumeric PathParameter(String label, GeoPoint2 p) {
	    AlgoPathParameter algo = new AlgoPathParameter((Construction)cons, label, p);
	    return algo.getResult();
	}
	
	// PhilippWeissenbacher 2007-04-10
		
	/** 
	 * polygon P[0], ..., P[n-1]
	 * The labels name the polygon itself and its segments
	 */
	final public GeoElement [] Polygon(String [] labels, GeoPointND [] P) {
		AlgoPolygon algo = new AlgoPolygon((Construction)cons, labels, P);
		return algo.getOutput();
	}
	
	@Override
	public GeoElement [] PolygonND(String [] labels, GeoPointND [] P) {
		return Polygon(labels,P);
	}
	
	//G.Sturr 2010-3-14
	/** 
	 * Polygon with vertices from geolist 
	 * Only the polygon is labeled, segments are not labeled
	 */
	final public GeoElement [] Polygon(String [] labels, GeoList pointList) {
		AlgoPolygon algo = new AlgoPolygon((Construction)cons, labels, pointList);
		return algo.getOutput();
	}
	//END G.Sturr
	
	/** 
	 * polygon P[0], ..., P[n-1]
	 * The labels name the polygon itself and its segments
	 */
	final public GeoElement [] PolyLine(String [] labels, GeoPointND [] P) {
		AlgoPolyLine algo = new AlgoPolyLine((Construction)cons, labels, P);
		return algo.getOutput();
	}
	
	@Override
	public GeoElement [] PolyLineND(String [] labels, GeoPointND [] P) {
		return PolyLine(labels,P);
	}
	
	final public GeoElement [] PolyLine(String [] labels, GeoList pointList) {
		AlgoPolyLine algo = new AlgoPolyLine((Construction)cons, labels, pointList);
		return algo.getOutput();
	}
	
	final public GeoElement [] VectorPolygon(String [] labels, GeoPoint2 [] points) {
    	boolean oldMacroMode = cons.isSuppressLabelsActive();
    	
    	cons.setSuppressLabelCreation(true);	
    	Circle(null, points[0], new MyDouble(this, points[0].distance(points[1])));
		cons.setSuppressLabelCreation(oldMacroMode);
		
	
	StringBuilder sb = new StringBuilder();
	
	double xA = points[0].inhomX;
	double yA = points[0].inhomY;
	
	for (int i = 1; i < points.length ; i++) {

		double xC = points[i].inhomX;
		double yC = points[i].inhomY;
		
		GeoNumeric nx = new GeoNumeric(cons, null, xC - xA);
		GeoNumeric ny = new GeoNumeric(cons, null, yC - yA);
		
		// make string like this
		// (a+x(A),b+y(A))
		sb.setLength(0);
		sb.append('(');
		sb.append(nx.getLabel());
		sb.append("+x(");
		sb.append(points[0].getLabel());
		sb.append("),");
		sb.append(ny.getLabel());
		sb.append("+y(");
		sb.append(points[0].getLabel());
		sb.append("))");
			
		//Application.debug(sb.toString());

		GeoPoint2 pp = (GeoPoint2)getAlgebraProcessor().evaluateToPoint(sb.toString(), true);
		
		try {
			((Construction)cons).replace(points[i], pp);
			points[i] = pp;
			//points[i].setEuclidianVisible(false);
			points[i].update();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	points[0].update();
	
	return Polygon(labels, points);
	
	}
	
	final public GeoElement [] RigidPolygon(String [] labels, GeoPoint2 [] points) {
    	boolean oldMacroMode = cons.isSuppressLabelsActive();
    	
    	cons.setSuppressLabelCreation(true);	
    	GeoConic circle = Circle(null, points[0], new MyDouble(this, points[0].distance(points[1])));
		cons.setSuppressLabelCreation(oldMacroMode);
		
    	GeoPoint2 p = Point(null, (Path)circle, points[1].inhomX, points[1].inhomY, true, false);
	try {
		((Construction)cons).replace(points[1], p);
		points[1] = p;
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
	
	StringBuilder sb = new StringBuilder();
	
	double xA = points[0].inhomX;
	double yA = points[0].inhomY;
	double xB = points[1].inhomX;
	double yB = points[1].inhomY;
	
	GeoVec2D a = new GeoVec2D(this, xB - xA, yB - yA ); // vector AB
	GeoVec2D b = new GeoVec2D(this, yA - yB, xB - xA ); // perpendicular to AB
	
	a.makeUnitVector();
	b.makeUnitVector();

	for (int i = 2; i < points.length ; i++) {

		double xC = points[i].inhomX;
		double yC = points[i].inhomY;
		
		GeoVec2D d = new GeoVec2D(this, xC - xA, yC - yA ); // vector AC
		
		setTemporaryPrintFigures(15);
		// make string like this
		// A+3.76UnitVector[Segment[A,B]]+-1.74UnitPerpendicularVector[Segment[A,B]]
		sb.setLength(0);
		sb.append(points[0].getLabel());
		sb.append('+');
		sb.append(format(a.inner(d)));

		// use internal command name
		sb.append("UnitVector[Segment[");
		sb.append(points[0].getLabel());
		sb.append(',');
		sb.append(points[1].getLabel());
		sb.append("]]+");
		sb.append(format(b.inner(d)));
		// use internal command name
		sb.append("UnitOrthogonalVector[Segment[");
		sb.append(points[0].getLabel());
		sb.append(',');
		sb.append(points[1].getLabel());
		sb.append("]]");
		
		restorePrintAccuracy();
					
			
		//Application.debug(sb.toString());

		GeoPoint2 pp = (GeoPoint2)getAlgebraProcessor().evaluateToPoint(sb.toString(), true);
		
		try {
			((Construction)cons).replace(points[i], pp);
			points[i] = pp;
			points[i].setEuclidianVisible(false);
			points[i].update();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	points[0].update();
	
	return Polygon(labels, points);
	
	}
	
	
	
	/** 
	 * Regular polygon with vertices A and B and n total vertices.
	 * The labels name the polygon itself, its segments and points
	 */
	final public GeoElement [] RegularPolygon(String [] labels, GeoPoint2 A, GeoPoint2 B, NumberValue n) {
		AlgoPolygonRegular algo = new AlgoPolygonRegular((Construction)cons, labels, A, B, n);
		return algo.getOutput();
	}
	
	
	
	
	/** 
	 * IntersectLines yields intersection point named label of lines g, h
	 */
	public GeoPointND IntersectLines(
		String label,
		GeoLineND g,
		GeoLineND h) {
		AlgoIntersectLines algo = new AlgoIntersectLines((Construction)cons, label, (GeoLine) g, (GeoLine) h);
		GeoPoint2 S = algo.getPoint();
		return S;
	}

	
	/** 
	 * yields intersection points named label of line g and polyLine p
	 */
	final public GeoElement[] IntersectLinePolyLine(
		String[] labels,
		GeoLine g,
		GeoPolyLine p) {
		AlgoIntersectLinePolyLine algo = new AlgoIntersectLinePolyLine((Construction)cons, labels, g, p);
		return algo.getOutput();
	}

	
	
	/** 
	 * yields intersection segments named label of line g and polygon p (as region)
	 */
	final public GeoElement[] IntersectLinePolygonalRegion(
		String[] labels,
		GeoLine g,
		GeoPolygon p) {
		AlgoIntersectLinePolygonalRegion algo = new AlgoIntersectLinePolygonalRegion((Construction)cons, labels, g, p);
		return algo.getOutput();
	}

	/** 
	 * IntersectLineConic yields intersection points named label1, label2
	 * of line g and conic c
	 * and intersection lines named in lowcase of the label
	 */
	final public GeoLine[] IntersectLineConicRegion(
		String[] labels,
		GeoLine g,
		GeoConic c) {
		AlgoIntersectLineConicRegion algo = new AlgoIntersectLineConicRegion((Construction)cons, labels, g, c);
		
		GeoLine[] lines = algo.getIntersectionLines();		
		
		return lines;
	}

	
	/** 
	 * yields intersection points named label of line g and polygon p (as boundary)
	 */
	final public GeoElement[] IntersectLinePolygon(
		String[] labels,
		GeoLine g,
		GeoPolygon p) {
		AlgoIntersectLinePolyLine algo = new AlgoIntersectLinePolyLine((Construction)cons, labels, g, p);
		return algo.getOutput();
	}
	
	/** 
	 * Intersects f and g using starting point A (with Newton's root finding)
	 */
	final public GeoPoint2 IntersectFunctions(
			String label,
			GeoFunction f,
			GeoFunction g, GeoPoint2 A) {
		AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton((Construction)cons, label, f, g, A);
		GeoPoint2 S = algo.getIntersectionPoint();
		return S;
	}
	
	/** 
	 * Intersects f and l using starting point A (with Newton's root finding)
	 */
	final public GeoPoint2 IntersectFunctionLine(
			String label,
			GeoFunction f,
			GeoLine l, GeoPoint2 A) {
				
		AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton((Construction)cons, label, f, l, A);
		GeoPoint2 S = algo.getIntersectionPoint();
		return S;
	}
	
	/** 
	 * Intersects f and g in interfal [left,right] numerically
	 */
	final public GeoPoint2[] IntersectFunctions(
			String[] labels,
			GeoFunction f,
			GeoFunction g, 
			NumberValue left,
			NumberValue right) {
		AlgoIntersectFunctions algo = new AlgoIntersectFunctions((Construction)cons, labels, f, g, left, right);
		GeoPoint2[] S = algo.getIntersectionPoints();
		return S;
	}//IntersectFunctions(label,f,g,left,right)
	
	
	/** 
	 * Intersect[polygon,polygon]
	 * G. Sturr
	 */
	final public GeoElement[] IntersectPolygons(String[] labels, GeoPolygon poly0, GeoPolygon poly1) {
		AlgoPolygonIntersection algo = new AlgoPolygonIntersection((Construction)cons, labels, poly0, poly1);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}
	
	/** 
	 * Union[polygon,polygon]
	 * G. Sturr
	 */
	final public GeoElement[] Union(String[] labels, GeoPolygon poly0, GeoPolygon poly1) {
		AlgoPolygonUnion algo = new AlgoPolygonUnion((Construction)cons, labels, poly0, poly1);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}
	
	
	
		
	/** 
	 * locus line for Q dependent on P. Note: P must be a point
	 * on a path.
	 */
	final public GeoLocus Locus(String label, GeoPoint2 Q, GeoPoint2 P) {		
		if (P.getPath() == null || 
			Q.getPath() != null || 
			!P.isParentOf(Q)) return null;
		AlgoLocus algo = new AlgoLocus((Construction)cons, label, Q, P);
		return algo.getLocus();
	}
	
	/**
	 * locus line for Q dependent on P. Note: P must be a visible slider
	 */
	final public GeoLocus Locus(String label, GeoPoint2 Q, GeoNumeric P) {
		if (!P.isSlider() || !P.isDefined() || !P.isAnimatable() || // !P.isSliderable() || !P.isDrawable() ||
			 Q.getPath() != null ||
			!P.isParentOf(Q)) return null;
		AlgoLocusSlider algo = new AlgoLocusSlider((Construction)cons, label, Q, P);
		return algo.getLocus();
	}

	
	
	/** 
	 * Corner of Drawing Pad Michael Borcherds 2008-05-10
	 */
	final public GeoPoint2 CornerOfDrawingPad(String label, NumberValue number, NumberValue ev) {
		AlgoDrawingPadCorner algo = new AlgoDrawingPadCorner((Construction)cons, label, number, ev);	
		return algo.getCorner();
	}

	
	/** 
	 * IntersectLineConic yields intersection points named label1, label2
	 * of line g and conic c
	 */
	final public GeoPoint2[] IntersectLineConic(
		String[] labels,
		GeoLine g,
		GeoConic c) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
		algo.setPrintedInXML(true);
		GeoPoint2[] points = algo.getIntersectionPoints();		
		GeoElement.setLabels(labels, points,getGeoElementSpreadsheet());	
		return points;
	}


	/** 
	 * IntersectConics yields intersection points named label1, label2, label3, label4
	 * of conics c1, c2
	 */
	public GeoPointND[] IntersectConics(
		String[] labels,
		GeoConicND a,
		GeoConicND b) {
		AlgoIntersectConics algo = getIntersectionAlgorithm((GeoConic) a, (GeoConic) b);
		algo.setPrintedInXML(true);
		GeoPoint2[] points = algo.getIntersectionPoints();
		GeoElement.setLabels(labels, points,getGeoElementSpreadsheet());
		return points;
	}
	
	/** 
	 * IntersectPolynomials yields all intersection points 
	 * of polynomials a, b
	 */
	final public GeoPoint2[] IntersectPolynomials(String[] labels, GeoFunction a, GeoFunction b) {
		
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) {
			
			// dummy point 
			GeoPoint2 A = new GeoPoint2(cons);
			A.setZero();
			//we must check that getLabels() didn't return null
			String label = labels == null ? null : labels[0];						
			AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton((Construction)cons, label, a, b, A);
			GeoPoint2[] ret = {algo.getIntersectionPoint()};
			return ret;
		}
			
		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);
		algo.setPrintedInXML(true);
		algo.setLabels(labels);
		GeoPoint2[] points = algo.getIntersectionPoints();		
		return points;
	}
	
	/** 
	 * get only one intersection point of two polynomials a, b 
	 * that is near to the given location (xRW, yRW)	 
	 */
	final public GeoPoint2 IntersectPolynomialsSingle(
		String label, GeoFunction a, GeoFunction b, 
		double xRW, double yRW) 
	{
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) return null;
			
		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);		
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of two polynomials a, b 
	 * with given index	 
	 */
	final public GeoPoint2 IntersectPolynomialsSingle(
		String label,
		GeoFunction a,
		GeoFunction b, NumberValue index) {
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) return null;
		
		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * IntersectPolyomialLine yields all intersection points
	 * of polynomial f and line l
	 */
	final public GeoPoint2[] IntersectPolynomialLine(
			String[] labels,		
			GeoFunction f,
			GeoLine l) {
				
		if (!f.isPolynomialFunction(false)) {
			
			// dummy point 
			GeoPoint2 A = new GeoPoint2(cons);
			A.setZero();
			//we must check that getLabels() didn't return null
			String label = labels == null ? null : labels[0];						
			AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton((Construction)cons, label, f, l, A);
			GeoPoint2[] ret = {algo.getIntersectionPoint()};
			return ret;

		}

		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
		algo.setPrintedInXML(true);
		algo.setLabels(labels);
		GeoPoint2[] points = algo.getIntersectionPoints();	
		return points;
	}
	
	/** 
	 * one intersection point of polynomial f and line l near to (xRW, yRW)
	 */
	final public GeoPoint2 IntersectPolynomialLineSingle(
			String label,		
			GeoFunction f,
			GeoLine l, double xRW, double yRW) {
		
		if (!f.isPolynomialFunction(false)) return null;
			
		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
		int index = algo.getClosestPointIndex(xRW, yRW);		
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint2 point = salgo.getPoint();
		return point;		
	}	
	
	/** 
	 * get only one intersection point of a line and a function 
	 */
	final public GeoPoint2 IntersectPolynomialLineSingle(
			String label,		
		GeoFunction f,
		GeoLine l, NumberValue index) {
			if (!f.isPolynomialFunction(false)) return null;	
			
			AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);		
			AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
			GeoPoint2 point = salgo.getPoint();
			return point;	
	}	
	
	/** 
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final public GeoPoint2 IntersectLineConicSingle(
		String label,
		GeoLine g,
		GeoConic c, double xRW, double yRW) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of a line and a conic 
	 */
	final public GeoPoint2 IntersectLineConicSingle(
		String label,
		GeoLine g,
		GeoConic c, NumberValue index) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}

	/** 
	 * get only one intersection point of line/Conic near to a given point
	 */
	final public GeoPoint2 IntersectLineConicSingle(
			String label, GeoLine a, GeoConic b, GeoPoint2 refPoint) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, refPoint);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	
	/** 
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final public GeoPoint2 IntersectConicsSingle(
		String label,
		GeoConic a,
		GeoConic b, double xRW, double yRW) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);
		int index = algo.getClosestPointIndex(xRW, yRW) ; 				
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of two conics 
	 */
	final public GeoPoint2 IntersectConicsSingle(
			String label, GeoConic a, GeoConic b, NumberValue index) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	/** 
	 * get only one intersection point of two conics 
	 */
	final public GeoPoint2 IntersectConicsSingle(
			String label, GeoConic a, GeoConic b, GeoPoint2 refPoint) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, refPoint);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	
	/** 
	 * get intersection points of a polynomial and a conic
	 */
	final public GeoPoint2[] IntersectPolynomialConic(
		String[] labels,
		GeoFunction f,
		GeoConic c) {
		AlgoIntersectPolynomialConic algo = getIntersectionAlgorithm(f, c);
		algo.setPrintedInXML(true);
		GeoPoint2[] points = algo.getIntersectionPoints();		
	//	GeoElement.setLabels(labels, points);	
		algo.setLabels(labels);
		return points;
	}
	
	final public GeoPoint2 IntersectPolynomialConicSingle(String label,
			GeoFunction f, GeoConic c,NumberValue idx){
		AlgoIntersect algo = getIntersectionAlgorithm(f, c);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	final public GeoPoint2 IntersectPolynomialConicSingle(String label,
			GeoFunction f, GeoConic c,double x,double y){
		AlgoIntersect algo = getIntersectionAlgorithm(f, c);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get intersection points of a implicitPoly and a line
	 */
	final public GeoPoint2[] IntersectImplicitpolyLine(
		String[] labels,
		GeoImplicitPoly p,
		GeoLine l) {
		AlgoIntersectImplicitpolyParametric algo = getIntersectionAlgorithm(p, l);
		algo.setPrintedInXML(true);
		GeoPoint2[] points = algo.getIntersectionPoints();		
		algo.setLabels(labels);
		return points;
	}
	
	/** 
	 * get single intersection points of a implicitPoly and a line
	 * @param idx index of choosen point
	 */
	final public GeoPoint2 IntersectImplicitpolyLineSingle(
		String label,
		GeoImplicitPoly p,
		GeoLine l,NumberValue idx) {
		AlgoIntersect algo = getIntersectionAlgorithm(p, l);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get single intersection points of a implicitPoly and a line
	 */
	final public GeoPoint2 IntersectImplicitpolyLineSingle(
		String label,
		GeoImplicitPoly p,
		GeoLine l,double x,double y) {
		AlgoIntersect algo = getIntersectionAlgorithm(p, l);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get intersection points of a implicitPoly and a polynomial
	 */
	final public GeoPoint2[] IntersectImplicitpolyPolynomial(
		String[] labels,
		GeoImplicitPoly p,
		GeoFunction f) {
		//if (!f.isPolynomialFunction(false))
			//return null;
		AlgoIntersectImplicitpolyParametric algo = getIntersectionAlgorithm(p, f);
		algo.setPrintedInXML(true);
		GeoPoint2[] points = algo.getIntersectionPoints();		
		algo.setLabels(labels);
		return points;
	}
	
	/** 
	 * get single intersection points of a implicitPoly and a line
	 * @param idx index of choosen point
	 */
	final public GeoPoint2 IntersectImplicitpolyPolynomialSingle(
		String label,
		GeoImplicitPoly p,
		GeoFunction f,NumberValue idx) {
		if (!f.isPolynomialFunction(false))
			return null;
		AlgoIntersect algo = getIntersectionAlgorithm(p, f);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get single intersection points of a implicitPoly and a line
	 */
	final public GeoPoint2 IntersectImplicitpolyPolynomialSingle(
		String label,
		GeoImplicitPoly p,
		GeoFunction f,double x,double y) {
		if (!f.isPolynomialFunction(false))
			return null;
		AlgoIntersect algo = getIntersectionAlgorithm(p, f);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get intersection points of two implicitPolys
	 */
	final public GeoPoint2[] IntersectImplicitpolys(
		String[] labels,
		GeoImplicitPoly p1,
		GeoImplicitPoly p2) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		algo.setPrintedInXML(true);
		GeoPoint2[] points = algo.getIntersectionPoints();		
		algo.setLabels(labels);
		return points;
	}
	
	/** 
	 * get single intersection points of two implicitPolys
	 * @param idx index of choosen point
	 */
	final public GeoPoint2 IntersectImplicitpolysSingle(
		String label,
		GeoImplicitPoly p1,
		GeoImplicitPoly p2,NumberValue idx) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get single intersection points of two implicitPolys near given Point (x,y)
	 * @param x 
	 * @param y
	 */
	final public GeoPoint2 IntersectImplicitpolysSingle(
		String label,
		GeoImplicitPoly p1,
		GeoImplicitPoly p2,double x,double y) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get intersection points of implicitPoly and conic
	 */
	final public GeoPoint2[] IntersectImplicitpolyConic(
		String[] labels,
		GeoImplicitPoly p1,
		GeoConic c1) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, c1);
		algo.setPrintedInXML(true);
		GeoPoint2[] points = algo.getIntersectionPoints();		
		algo.setLabels(labels);
		return points;
	}
	
	/** 
	 * get single intersection points of implicitPoly and conic
	 * @param idx index of choosen point
	 */
	final public GeoPoint2 IntersectImplicitpolyConicSingle(
		String label,
		GeoImplicitPoly p1,
		GeoConic c1,NumberValue idx) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, c1);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get single intersection points of implicitPolys and conic near given Point (x,y)
	 * @param x 
	 * @param y
	 */
	final public GeoPoint2 IntersectImplicitpolyConicSingle(
		String label,
		GeoImplicitPoly p1,
		GeoConic c1,double x,double y) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, c1);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint2 point = salgo.getPoint();
		return point;
	}
	
	
	 
	 // intersect polynomial and conic
	 AlgoIntersectPolynomialConic getIntersectionAlgorithm(GeoFunction f, GeoConic c) {

		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(f, c);
		if (existingAlgo != null) return (AlgoIntersectPolynomialConic) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomialConic algo = new AlgoIntersectPolynomialConic((Construction)cons, f, c);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersect line and conic
	 AlgoIntersectLineConic getIntersectionAlgorithm(GeoLine g, GeoConic c) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(g, c);
		if (existingAlgo != null) return (AlgoIntersectLineConic) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineConic algo = new AlgoIntersectLineConic((Construction)cons, g, c);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersect conics
	 AlgoIntersectConics getIntersectionAlgorithm(GeoConic a, GeoConic b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null) return (AlgoIntersectConics) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectConics algo = new AlgoIntersectConics((Construction)cons, a, b);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersection of polynomials
	 AlgoIntersectPolynomials getIntersectionAlgorithm(GeoFunction a, GeoFunction b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null) return (AlgoIntersectPolynomials) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomials algo = new AlgoIntersectPolynomials((Construction)cons, a, b);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersection of polynomials
	 AlgoIntersectPolynomialLine getIntersectionAlgorithm(GeoFunction a, GeoLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, l);
		if (existingAlgo != null) return (AlgoIntersectPolynomialLine) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomialLine algo = new AlgoIntersectPolynomialLine((Construction)cons, a, l);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	// intersection of GeoImplicitPoly, GeoLine
	 AlgoIntersectImplicitpolyParametric getIntersectionAlgorithm(GeoImplicitPoly p, GeoLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, l);
		if (existingAlgo != null) return (AlgoIntersectImplicitpolyParametric) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric((Construction)cons, p, l);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	// intersection of GeoImplicitPoly, polynomial
	 AlgoIntersectImplicitpolyParametric getIntersectionAlgorithm(GeoImplicitPoly p, GeoFunction f) {
			AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, f);
			if (existingAlgo != null) return (AlgoIntersectImplicitpolyParametric) existingAlgo;
			
			// we didn't find a matching algorithm, so create a new one
			AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric((Construction)cons, p, f);
			algo.setPrintedInXML(false);
			intersectionAlgos.add(algo); // remember this algorithm
			return algo;
		 }
	 
	// intersection of two GeoImplicitPoly
	 AlgoIntersectImplicitpolys getIntersectionAlgorithm(GeoImplicitPoly p1, GeoImplicitPoly p2) {
			AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p1, p2);
			if (existingAlgo != null) return (AlgoIntersectImplicitpolys) existingAlgo;
			
			// we didn't find a matching algorithm, so create a new one
			AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys((Construction)cons, p1, p2);
			algo.setPrintedInXML(false);
			intersectionAlgos.add(algo); // remember this algorithm
			return algo;
		 }
	 
	 AlgoIntersectImplicitpolys getIntersectionAlgorithm(GeoImplicitPoly p1, GeoConic c1) {
			AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p1, c1);
			if (existingAlgo != null) return (AlgoIntersectImplicitpolys) existingAlgo;
			
			// we didn't find a matching algorithm, so create a new one
			AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys((Construction)cons, p1, c1);
			algo.setPrintedInXML(false);
			intersectionAlgos.add(algo); // remember this algorithm
			return algo;
		 }
	  
	 public AlgoElement findExistingIntersectionAlgorithm(GeoElement a, GeoElement b) {
		int size = intersectionAlgos.size();
		AlgoElement algo;
		for (int i=0; i < size; i++) {
			algo = (AlgoElement) intersectionAlgos.get(i);
			GeoElement [] input = algo.getInput();
			if (a == input[0] && b == input[1] ||
				 a == input[1] && b == input[0])
				// we found an existing intersection algorithm
				return algo;
		}
		return null;
	 }
	 

	 
	 
	/** 
	 * tangents to c through P
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoPoint2 P,
		GeoConic c) {
		AlgoTangentPoint algo = new AlgoTangentPoint((Construction)cons, labels, P, c);
		GeoLine[] tangents = algo.getTangents();		
		return tangents;
	}

	/** 
	 * common tangents to c1 and c2
	 * dsun48 [6/26/2011]
	 */
	final public GeoLine[] CommonTangents(
		String[] labels,
		GeoConic c1,
		GeoConic c2) {
		AlgoCommonTangents algo = new AlgoCommonTangents((Construction)cons, labels, c1, c2);
		GeoLine[] tangents = algo.getTangents();		
		return tangents;
	}
	
	/** 
	 * tangents to c parallel to g
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoLine g,
		GeoConic c) {
		AlgoTangentLine algo = new AlgoTangentLine((Construction)cons, labels, g, c);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}

	/** 
	 * tangent to f in x = a
	 */
	final public GeoLine Tangent(
		String label,
		NumberValue a,
		GeoFunction f) {
		AlgoTangentFunctionNumber algo =
			new AlgoTangentFunctionNumber((Construction)cons, label, a, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();   
		notifyUpdate(t);  
		return t;
	}

	/** 
	 * tangent to f in x = x(P)
	 */
	final public GeoLine Tangent(
		String label,
		GeoPoint2 P,
		GeoFunction f) {
		AlgoTangentFunctionPoint algo =
			new AlgoTangentFunctionPoint((Construction)cons, label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();     
		notifyUpdate(t);
		return t;
	}
	
	/** 
	 * tangents to p through P
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoPoint2 R,
		GeoImplicitPoly p) {
		AlgoTangentImplicitpoly algo = new AlgoTangentImplicitpoly((Construction)cons, labels, p, R);
		algo.setLabels(labels);
		GeoLine[] tangents = algo.getTangents();		
		return tangents;
	}

	/** 
	 * tangents to p parallel to g
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoLine g,
		GeoImplicitPoly p) {
		AlgoTangentImplicitpoly algo = new AlgoTangentImplicitpoly((Construction)cons, labels, p, g);
		algo.setLabels(labels);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}
	


	/** 
	 * second axis of c
	 */
	final public GeoLine SecondAxis(String label, GeoConic c) {
		AlgoAxisSecond algo = new AlgoAxisSecond((Construction)cons, label, c);
		GeoLine axis = algo.getAxis();
		return axis;
	}



	/** 
	 * second axis' length of c
	 */
	final public GeoNumeric SecondAxisLength(String label, GeoConic c) {
		AlgoAxisSecondLength algo = new AlgoAxisSecondLength((Construction)cons, label, c);
		GeoNumeric length = algo.getLength();
		return length;
	}

	/** 
	 * (parabola) parameter of c
	 */
	final public GeoNumeric Parameter(String label, GeoConic c) {
		AlgoParabolaParameter algo = new AlgoParabolaParameter((Construction)cons, label, c);
		GeoNumeric length = algo.getParameter();
		return length;
	}

	/** 
	 * (circle) radius of c
	 */
	final public GeoNumeric Radius(String label, GeoConic c) {
		AlgoRadius algo = new AlgoRadius((Construction)cons, label, c);
		GeoNumeric length = algo.getRadius();
		return length;
	}


	/********************************************************************
	 * TRANSFORMATIONS
	 ********************************************************************/

	/**
	 * translate geoTrans by vector v
	 */
	final public GeoElement [] Translate(String label, GeoElement geoTrans, GeoVec3D v) {
		Transform t = new TransformTranslate((Construction)cons, v);
		return t.transform(geoTrans, label);				
	}
	
	/**
	 * translates vector v to point A. The resulting vector is equal
	 * to v and has A as startPoint
	 */
	final public GeoVector Translate(String label, GeoVec3D v, GeoPoint2 A) {
		AlgoTranslateVector algo = new AlgoTranslateVector((Construction)cons, label, v, A);
		GeoVector vec = algo.getTranslatedVector();
		return vec;
	}	

	/**
	 * rotate geoRot by angle phi around (0,0)
	 */
	final public GeoElement [] Rotate(String label, GeoElement geoRot, NumberValue phi) {
		Transform t = new TransformRotate((Construction)cons, phi);
		return t.transform(geoRot, label);					
	}


	/**
	 * rotate geoRot by angle phi around Q
	 */
	final public GeoElement [] Rotate(String label, GeoElement geoRot, NumberValue phi, GeoPoint2 Q) {
		Transform t = new TransformRotate((Construction)cons, phi,Q);
		return t.transform(geoRot, label);		
	}
		


	/**
	 * mirror geoMir at point Q
	 */
	final public GeoElement [] Mirror(String label, GeoElement geoMir, GeoPoint2 Q) {	
		Transform t = new TransformMirror((Construction)cons, Q);
		return t.transform(geoMir, label);
	}

	/**
	 * mirror (invert) element Q in circle 
	 * Michael Borcherds 2008-02-10
	 */
	final public GeoElement [] Mirror(String label, GeoElement Q, GeoConic conic) {	
		Transform t = new TransformMirror((Construction)cons, conic);
		return t.transform(Q, label);
	}


	
	/**
	 * shear
	 */
	final public GeoElement [] Shear(String label, GeoElement Q, GeoVec3D l, GeoNumeric num) {	
		Transform t = new TransformShearOrStretch((Construction)cons, l, num, true);
		return t.transform(Q, label);
	}
	/**
	 * apply matrix 
	 * Michael Borcherds 2010-05-27
	 */
	final public GeoElement [] Stretch(String label, GeoElement Q, GeoVec3D l, GeoNumeric num) {	
		Transform t = new TransformShearOrStretch((Construction)cons, l, num, false);
		return t.transform(Q, label);
	}

	/**
	 * mirror geoMir at line g
	 */
	final public GeoElement [] Mirror(String label, GeoElement geoMir, GeoLine g) {
		Transform t = new TransformMirror((Construction)cons, g);
		return t.transform(geoMir, label);
		
			
	}			
	
	
	static final int TRANSFORM_TRANSLATE = 0;
	static final int TRANSFORM_MIRROR_AT_POINT = 1;
	static final int TRANSFORM_MIRROR_AT_LINE = 2;	
	static final int TRANSFORM_ROTATE = 3;
	static final int TRANSFORM_ROTATE_AROUND_POINT = 4;
	static final int TRANSFORM_DILATE = 5;
	
	public static boolean keepOrientationForTransformation(int transformationType) {
		switch (transformationType) {
			case TRANSFORM_MIRROR_AT_LINE:	
				return false;
			
			default:
				return true;									
		}
	}
	
	
	/***********************************
	 * CALCULUS
	 ***********************************/

	
	/**
	 * Tries to expand a function f to a polynomial.
	 */
	final public GeoFunction PolynomialFunction(String label, GeoFunction f) {		
		AlgoPolynomialFromFunction algo = new AlgoPolynomialFromFunction((Construction)cons, label, f);
		return algo.getPolynomial();			
	}
	
	/**
	 * Fits a polynomial exactly to a list of coordinates
	 * Michael Borcherds 2008-01-22
	 */
	final public GeoFunction PolynomialFunction(String label, GeoList list) {		
		AlgoPolynomialFromCoordinates algo = new AlgoPolynomialFromCoordinates((Construction)cons, label, list);
		return algo.getPolynomial();			
	}

	
	final public GeoElement Simplify(String label, CasEvaluableFunction func) {		
		AlgoSimplify algo = new AlgoSimplify((Construction)cons, label, func);
		return algo.getResult();			
	}
	
	final public GeoElement SolveODE(String label, CasEvaluableFunction func) {		
		AlgoSolveODECas algo = new AlgoSolveODECas((Construction)cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Simplify text, eg "+-x" to "-x"
	 * @author Michael Borcherds 
	 */
	final public GeoElement Simplify(String label, GeoText text) {		
		AlgoSimplifyText algo = new AlgoSimplifyText((Construction)cons, label, text);
		return algo.getGeoText();		
	}
	

	

	
	final public GeoLocus SolveODE(String label, FunctionalNVar f, FunctionalNVar g, GeoNumeric x, GeoNumeric y, GeoNumeric end, GeoNumeric step) {		
		AlgoSolveODE algo = new AlgoSolveODE((Construction)cons, label, f, g, x, y, end, step);
		return algo.getResult();			
	}
	
	/*
	 * second order ODEs
	 */
	final public GeoLocus SolveODE2(String label, GeoFunctionable f, GeoFunctionable g, GeoFunctionable h, GeoNumeric x, GeoNumeric y, GeoNumeric yDot, GeoNumeric end, GeoNumeric step) {		
		AlgoSolveODE2 algo = new AlgoSolveODE2((Construction)cons, label, f, g, h, x, y, yDot, end, step);
		return algo.getResult();			
	}
	

	/**
	 * Numerator
	 * Michael Borcherds 
	 */
	final public GeoFunction Numerator(String label, GeoFunction func) {		
		AlgoNumerator algo = new AlgoNumerator((Construction)cons, label, func);
		return algo.getResult();			
	}
	

	

	/**
	 * Limit
	 * Michael Borcherds 
	 */
	final public GeoNumeric Limit(String label, GeoFunction func, NumberValue num) {		
		AlgoLimit algo = new AlgoLimit((Construction)cons, label, func, num);
		return algo.getResult();			
	}
	
	/**
	 * LimitBelow
	 * Michael Borcherds 
	 */
	final public GeoNumeric LimitBelow(String label, GeoFunction func, NumberValue num) {		
		AlgoLimitBelow algo = new AlgoLimitBelow((Construction)cons, label, func, num);
		return algo.getResult();			
	}
	
	/**
	 * LimitAbove
	 * Michael Borcherds 
	 */
	final public GeoNumeric LimitAbove(String label, GeoFunction func, NumberValue num) {		
		AlgoLimitAbove algo = new AlgoLimitAbove((Construction)cons, label, func, num);
		return algo.getResult();			
	}
	
	/**
	 * Partial Fractions
	 * Michael Borcherds 
	 */
	final public GeoElement PartialFractions(String label, CasEvaluableFunction func) {		
		AlgoPartialFractions algo = new AlgoPartialFractions((Construction)cons, label, func);
		return algo.getResult();			
	}
	


	
	/**
	 * Taylor series of function f about point x=a of order n
	 */
	final public GeoFunction TaylorSeries(
		String label,
		GeoFunction f,
		NumberValue a, 
		NumberValue n) {
		
		AlgoTaylorSeries algo = new AlgoTaylorSeries((Construction)cons, label, f, a, n);
		return algo.getPolynomial();
	}

	/**
	 * Integral of function f
	 */
	final public GeoElement Integral(String label, CasEvaluableFunction f, GeoNumeric var) {
		AlgoIntegral algo = new AlgoIntegral((Construction)cons, label, f, var);
		return algo.getResult();
	}
	
	/**
	 * definite Integral of function f from x=a to x=b
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, NumberValue a, NumberValue b) {
		AlgoIntegralDefinite algo = new AlgoIntegralDefinite((Construction)cons, label, f, a, b);
		GeoNumeric n = algo.getIntegral();
		return n;
	}

	/**
	 * definite Integral of function f from x=a to x=b 
	 * with option to evaluate  (evaluate == false allows shade-only drawing)
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, NumberValue a, NumberValue b, GeoBoolean evaluate) {
		AlgoIntegralDefinite algo = new AlgoIntegralDefinite((Construction)cons, label, f, a, b, evaluate);
		GeoNumeric n = algo.getIntegral();
		return n;
	}
	
	
	/** 
	 * definite integral of function (f - g) in interval [a, b]
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, GeoFunction g,
												NumberValue a, NumberValue b) {
		AlgoIntegralFunctions algo = new AlgoIntegralFunctions((Construction)cons, label, f, g, a, b);
		GeoNumeric num = algo.getIntegral();
		return num;
	}		
	
	
	/** 
	 * definite integral of function (f - g) in interval [a, b]
	 * with option to not evaluate  (evaluate == false allows shade-only drawing)
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, GeoFunction g,
												NumberValue a, NumberValue b, GeoBoolean evaluate) {
		AlgoIntegralFunctions algo = new AlgoIntegralFunctions((Construction)cons, label, f, g, a, b,evaluate);
		GeoNumeric num = algo.getIntegral();
		return num;
	}		
	
	
	

	
	/**
	 * all Roots of polynomial f (works only for polynomials and functions
	 * that can be simplified to factors of polynomials, e.g. sqrt(x) to x)
	 */
	final public GeoPoint2 [] Root(String [] labels, GeoFunction f) {
		// allow functions that can be simplified to factors of polynomials
		if (!f.isPolynomialFunction(true)) return null;
		
		AlgoRootsPolynomial algo = new AlgoRootsPolynomial((Construction)cons, labels, f);
		GeoPoint2 [] g = algo.getRootPoints();
		return g;
	}	
	
	@Override
	final public GeoPoint2 [] RootMultiple(String [] labels, GeoFunction f) {
		// allow functions that can be simplified to factors of polynomials
		if (!f.isPolynomialFunction(true)) return null;
		
		AlgoRootsPolynomial algo = new AlgoRootsPolynomial((GeoFunction)f);
		GeoPoint2 [] g = algo.getRootPoints();
		return g;
	}

	
	/**
	 * Root of a function f to given start value a (works only if first derivative of f exists)
	 */
	final public GeoPoint2 Root(String label, GeoFunction f, NumberValue a) {			 
		AlgoRootNewton algo = new AlgoRootNewton((Construction)cons, label, f, a);
		GeoPoint2 p = algo.getRootPoint();
		return p;
	}	

	/**
	 * Root of a function f in given interval [a, b]
	 */
	final public GeoPoint2 Root(String label, GeoFunction f, NumberValue a, NumberValue b) {			 
		AlgoRootInterval algo = new AlgoRootInterval((Construction)cons, label, f, a, b);
		GeoPoint2 p = algo.getRootPoint();
		return p;
	}	

	/**
	 * Roots of a function f in given interval [a, b]
	 * Numerical version
	 */
	final public GeoPoint2[] Roots(String[] labels, GeoFunction f, NumberValue a, NumberValue b) {			 
		AlgoRoots algo = new AlgoRoots((Construction)cons, labels, f, a, b);
		GeoPoint2[] pts = algo.getRootPoints();
		return pts;
	}//Roots(label,f,a,b)
	

	/**
	 * Numeric search for extremum of function f in interval [left,right]
	 * Ulven 2011-2-5
	 
	final public GeoPoint[] Extremum(String label,GeoFunction f,NumberValue left,NumberValue right) {
		AlgoExtremumNumerical algo=new AlgoExtremumNumerical(cons,label,f,left,right);
		GeoPoint g=algo.getNumericalExtremum();	//All variants return array...
		GeoPoint[] result=new GeoPoint[1];
		result[0]=g;
		return result;
	}//Extremum(label,geofunction,numbervalue,numbervalue)
	*/

	/**
	* Trying to maximize dependent variable with respect to independen variable
	* Ulven 2011-2-13
	*
	*/
	final public GeoElement Maximize(String label, GeoElement dep, GeoNumeric indep) {
		AlgoMaximize algo=new AlgoMaximize((Construction)cons,label,dep,indep);
		/*
		GeoElement[] geo=new GeoElement[1];
		geo[0]=algo.getMaximized();	//All variants return array...
		*/
		return algo.getResult();//geo;
	}//Maximize(lbl,dep,indep);	
	
	/**
	* Trying to minimize dependent variable with respect to independen variable
	* Ulven 2011-2-13
	*
	*/
	final public GeoElement Minimize(String label, GeoElement dep, GeoNumeric indep) {
		AlgoMinimize algo=new AlgoMinimize((Construction)cons,label,dep,indep);	//	true: minimize
		/*GeoElement geo=algo.getMaximized();	//All variants return array...
		 * 
		 */
		return algo.getResult();
	}//Minimize(lbl,dep,indep,minimize);
	
	
	
	/**
	 * all Turning points of function f (works only for polynomials)
	 */
	final public GeoPoint2 [] TurningPoint(String [] labels, GeoFunction f) {
		//	check if this is a polynomial at the moment
		if (!f.isPolynomialFunction(true)) return null;
			 
		AlgoTurningPointPolynomial algo = new AlgoTurningPointPolynomial((Construction)cons, labels, f);
		GeoPoint2 [] g = algo.getRootPoints();
		return g;
	}	




	



	/**
	 * Osculating Circle of a function f in point A
	 */

	final public GeoConic OsculatingCircle(String label,GeoPoint2 A,GeoFunction f){

		  AlgoOsculatingCircle algo = new AlgoOsculatingCircle((Construction)cons,label,A,f);
		  GeoConic circle = algo.getCircle();
		  return circle;

	}

	

	/**
	 * Osculating Circle of a curve f in point A
	 */

	final public GeoConic OsculatingCircleCurve(String label,GeoPoint2 A,GeoCurveCartesian f){

		  AlgoOsculatingCircleCurve algo = new AlgoOsculatingCircleCurve((Construction)cons,label,A,f);
		  GeoConic circle = algo.getCircle();
		  return circle;

	}

	

	/**
	 * Calculate Function Length between the numbers A and B: integral from A to B on T = sqrt(1+(f')^2)
	 */

	final public GeoNumeric FunctionLength(String label,GeoFunction f,GeoNumeric A,GeoNumeric B){

		  AlgoLengthFunction algo = new AlgoLengthFunction((Construction)cons,label,f,A,B);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/**
	 * Calculate Function Length between the points A and B: integral from A to B on T = sqrt(1+(f')^2)
	 */

	final public GeoNumeric FunctionLength2Points(String label,GeoFunction f,GeoPoint2 A,GeoPoint2 B){

		  AlgoLengthFunction2Points algo = new AlgoLengthFunction2Points((Construction)cons,label,f,A,B);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/**

	 * Calculate Curve Length between the parameters t0 and t1: integral from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)

	 */

	final public GeoNumeric CurveLength(String label, GeoCurveCartesian c, GeoNumeric t0,GeoNumeric t1){

		  AlgoLengthCurve algo = new AlgoLengthCurve((Construction)cons,label,c,t0,t1);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/**
	 * Calculate Curve Length between the points A and B: integral from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)
	 */
	final public GeoNumeric CurveLength2Points(String label, GeoCurveCartesian c, GeoPoint2 A,GeoPoint2 B){
		  AlgoLengthCurve2Points algo = new AlgoLengthCurve2Points((Construction)cons,label,c,A,B);
		  GeoNumeric length = algo.getLength();
		  return length;
	}


	/** 
	 * tangent to Curve f in point P: (b'(t), -a'(t), a'(t)*b(t)-a(t)*b'(t))
	 */
	final public GeoLine Tangent(String label,GeoPoint2 P,GeoCurveCartesian f) {
		AlgoTangentCurve algo = new AlgoTangentCurve((Construction)cons, label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();     
		notifyUpdate(t);
		return t;
	}

	/**
	 * Victor Franco Espino 18-04-2007: End new commands 
	 */


	

	/***********************************
	 * PACKAGE STUFF
	 ***********************************/



	
		
	// temp for buildEquation    
	




	

	/*
	final private String formatAbs(double x) {
		if (isZero(x))
			return "0";
		else
			return formatNF(Math.abs(x));
	}*/

	
	
	
	
	////////////////////////////////////////////////
	// FORMAT FOR NUMBERS
	////////////////////////////////////////////////
	
	public double axisNumberDistance(double units, NumberFormatAdapter numberFormat){

		// calc number of digits
		int exp = (int) Math.floor(Math.log(units) / Math.log(10));
		int maxFractionDigtis = Math.max(-exp, getPrintDecimals());
		
		// format the numbers
		if (numberFormat instanceof DecimalFormat)
			((DecimalFormat) numberFormat).applyPattern("###0.##");	
		numberFormat.setMaximumFractionDigits(maxFractionDigtis);
		
		// calc the distance
		double pot = Math.pow(10, exp);
		double n = units / pot;
		double distance;

		if (n > 5) {
			distance = 5 * pot;
		} else if (n > 2) {
			distance = 2 * pot;
		} else {
			distance = pot;
		}
		
		return distance;
	}
	
	/**
	 * Checks if x is close (Kernel.MIN_PRECISION) to a decimal fraction,  
	 * eg 2.800000000000001. If it is, the decimal fraction eg 2.8 is returned, 
	 * otherwise x is returned.
	 */	
	
			
	/*******************************************************
	 * SAVING
	 *******************************************************/

	private boolean isSaving;
	
	public synchronized boolean isSaving() {
		return isSaving;
	}
	
	public synchronized void setSaving(boolean saving) {
		isSaving = saving;
	}
	
		
	
	


	

	
	
	
	
	@Override
	final public AbstractAnimationManager getAnimatonManager() {		
		if (animationManager == null) {
			animationManager = new AnimationManager(this);			
		}
		return animationManager;		
	}
	
	
	
	
	

	
	
	final public static String defaultLibraryJavaScript = "function ggbOnInit() {}";
	
	String libraryJavaScript = defaultLibraryJavaScript;

	
	
	public void resetLibraryJavaScript() {
		libraryJavaScript = defaultLibraryJavaScript;
	}
	
	public void setLibraryJavaScript(String str) {
		AbstractApplication.debug(str);
		libraryJavaScript = str;
		
		//libraryJavaScript = "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');ggbApplet.registerObjectUpdateListener('A','listener');}function listener() {//java.lang.System.out.println('add listener called'); var x = ggbApplet.getXcoord('A');var y = ggbApplet.getYcoord('A');var len = Math.sqrt(x*x + y*y);if (len > 5) { x=x*5/len; y=y*5/len; }ggbApplet.unregisterObjectUpdateListener('A');ggbApplet.setCoords('A',x,y);ggbApplet.registerObjectUpdateListener('A','listener');}";
		//libraryJavaScript = "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');}";
	}
	
	//public String getLibraryJavaScriptXML() {
	//	return Util.encodeXML(libraryJavaScript);
	//}
	
	public String getLibraryJavaScript() {
		return libraryJavaScript;
	}
	
	
	
	/** return all points of the current construction */
	public TreeSet<GeoElement> getPointSet(){
		return getConstruction().getGeoSetLabelOrder(GeoClass.POINT);
	}
	
	/**
	 * test kernel
	 */
	public static void mainx(String [] args) {
		// create kernel with null application for testing
		Kernel kernel = new Kernel(null);
		Construction cons = kernel.getConstruction();
		
		// create points A and B
		GeoPoint2 A = new GeoPoint2(cons, "A", 0, 1, 1);
		GeoPoint2 B = new GeoPoint2(cons, "B", 3, 4, 1);
		
		// create line g through points A and B
		GeoLine g = kernel.Line("g", A, B);
		
		// print current objects
		System.out.println(A);
		System.out.println(B);
		System.out.println(g);
		
		// change B
		B.setCoords(3, 2, 1);
		B.updateCascade();
		
		// print current objects
		System.out.println("changed " +B);
		System.out.println(g);
	}
	

	
	final public GeoPoint2 Kimberling(String label, GeoPoint2 A, GeoPoint2 B, GeoPoint2 C, NumberValue v) {
		AlgoKimberling algo = new AlgoKimberling((Construction)cons, label, A,B,C,v);
		GeoPoint2 P = algo.getResult();
		return P;
	}
	
	final public GeoPoint2 Barycenter(String label, GeoList A, GeoList B) {
		AlgoBarycenter algo = new AlgoBarycenter((Construction)cons, label, A,B);
		GeoPoint2 P = algo.getResult();
		return P;
	}
	
	final public GeoPoint2 Trilinear(String label, GeoPoint2 A, GeoPoint2 B, GeoPoint2 C, 
			NumberValue a, NumberValue b, NumberValue c) {
		AlgoTrilinear algo = new AlgoTrilinear((Construction)cons, label, A,B,C,a,b,c);
		GeoPoint2 P = algo.getResult();
		return P;
	}
	
	final public GeoImplicitPoly TriangleCubic(String label, GeoPoint2 A, GeoPoint2 B, GeoPoint2 C, NumberValue v) {
		AlgoTriangleCubic algo = new AlgoTriangleCubic((Construction)cons, label, A,B,C,v);
		GeoImplicitPoly poly = algo.getResult();
		return poly;
	}
	
	final public GeoImplicitPoly TriangleCubic(String label, GeoPoint2 A, GeoPoint2 B,
			GeoPoint2 C, GeoImplicitPoly v, GeoNumeric a, GeoNumeric b, GeoNumeric c) {
		AlgoTriangleCurve algo = new AlgoTriangleCurve((Construction)cons, label, A,B,C,v,a,b,c);
		GeoImplicitPoly poly = algo.getResult();

		return poly;
	}

	public GeoTextField textfield(String label,GeoElement geoElement) {
		AlgoTextfield at = new AlgoTextfield((Construction)cons,label,geoElement);
		return  (GeoTextField) at.getResult();		
		
		
	}

	

	
	/**
	 * 
	 * @return default plane (null for 2D implementation, xOy plane for 3D)
	 */
	public GeoPlaneND getDefaultPlane(){
		return null;
	}

	
	
	
    
    
    @Override
    public LaTeXCache newLaTeXCache(){
    	return new GeoLaTeXCache();
    }
    
    @Override
	public GeoGebraCasInterface newGeoGebraCAS(){
    	return new geogebra.cas.GeoGebraCAS(this);
    }

	

	// This is a temporary place for adapter creation methods which will move into factories later

	@Override
	public NumberFormatAdapter getNumberFormat(){
		return new NumberFormatDesktop();
	}
	
	@Override
	public NumberFormatAdapter getNumberFormat(String pattern){
		return new NumberFormatDesktop(pattern);
	}
	
	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new GeoElementGraphicsAdapterDesktop(app);
	} 
	
	@Override
	public ScientificFormatAdapter getScientificFormat(int a, int b, boolean c) {
		return new ScientificFormat(a, b, c);
	}
	private MyMath2 myMath2;
	@Override
	public AbstractMyMath2 getMyMath2() {
		if(myMath2==null)
			myMath2 = new MyMath2();
		return myMath2; 
	}

	private GeoElementSpreadsheet ges = new GeoElementSpreadsheet();
	@Override
	public AbstractGeoElementSpreadsheet getGeoElementSpreadsheet() {
		return ges;
	}

	@Override 
	public GgbMat getGgbMat(MyList myList) {
		return new GgbMat((MyList)myList);
	}

	@Override 
	public GgbMat getGgbMat(GeoList inputList) {
		return new GgbMat(inputList);
	}	
		
	
	@Override
	public GeoConicPart newGeoConicPart(Construction cons, int type) {//temporary
		return new GeoConicPart(cons, type);
	}

	@Override
	public GeoImplicitPoly newGeoImplicitPoly(Construction cons) {
		return new GeoImplicitPoly(cons);
	}
	
	@Override
	public Geo3DVec getGeo3DVec(double x, double y, double z) {
		return new geogebra3D.kernel3D.Geo3DVec(this, x, y, z);
	}
	
	@Override
	public UndoManager getUndoManager(Construction cons){
		return new UndoManager(cons);
	
	}

	@Override
	public AbstractCommandDispatcher getCommandDispatcher() {
		return new CommandDispatcher(this);
	}
	
	@Override
	public AbstractGeoTextField getGeoTextField(Construction cons) {
		return new GeoTextField(cons);
	}	
}
