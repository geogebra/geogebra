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
import geogebra.common.kernel.EquationSolver;
import geogebra.common.kernel.EquationSolverInterface;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.MacroInterface;
import geogebra.common.kernel.Manager3DInterface;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.SystemOfEquationsSolver;
import geogebra.common.kernel.Transform;
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
import geogebra.common.kernel.barycentric.AlgoBarycenter;
import geogebra.common.kernel.barycentric.AlgoKimberling;
import geogebra.common.kernel.barycentric.AlgoTriangleCubic;
import geogebra.common.kernel.barycentric.AlgoTriangleCurve;
import geogebra.common.kernel.barycentric.AlgoTrilinear;
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
import geogebra.common.kernel.cas.AlgoPolynomialMod;
import geogebra.common.kernel.cas.AlgoSimplify;
import geogebra.common.kernel.cas.AlgoSolveODECas;
import geogebra.common.kernel.cas.AlgoTangentCurve;
import geogebra.common.kernel.cas.AlgoTangentFunctionNumber;
import geogebra.common.kernel.cas.AlgoTangentFunctionPoint;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.AbstractCommandDispatcher;
import geogebra.common.kernel.geos.AbstractGeoElementSpreadsheet;
import geogebra.common.kernel.geos.AbstractGeoTextField;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoInterval;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.implicit.AlgoDependentImplicitPoly;
import geogebra.common.kernel.implicit.AlgoIntersectImplicitpolyParametric;
import geogebra.common.kernel.implicit.AlgoIntersectImplicitpolys;
import geogebra.common.kernel.implicit.AlgoTangentImplicitpoly;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
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
import geogebra.kernel.algos.AlgoPolygonIntersection;
import geogebra.kernel.algos.AlgoPolygonUnion;
import geogebra.kernel.algos.AlgoSolveODE;
import geogebra.kernel.algos.AlgoSolveODE2;
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
import geogebra.util.GeoLaTeXCache;
import geogebra.util.GgbMat;
import geogebra.util.MyMath2;
import geogebra.util.NumberFormatDesktop;
import geogebra.util.ScientificFormat;

import java.text.DecimalFormat;
import java.util.TreeSet;

public class Kernel extends AbstractKernel{
			
	

	protected AbstractApplication app;	
	
	private EquationSolver eqnSolver;
	private SystemOfEquationsSolver sysEqSolv;
	private ExtremumFinder extrFinder;
	protected Parser parser;

	

	
	
	
	/** 3D manager */
	private Manager3DInterface manager3D;
				
	public Kernel(AbstractApplication app) {
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
	final public AbstractApplication getApplication() {
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

	@Override
	final public GeoElement  DependentImplicitPoly(String label, Equation equ) {
		AlgoDependentImplicitPoly algo = new AlgoDependentImplicitPoly((Construction)cons, label, equ);
		GeoElement geo = algo.getGeo();
		return geo;
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
	
	
	@Override
	public GeoElement [] PolygonND(String [] labels, GeoPointND [] P) {
		return Polygon(labels,P);
	}

	
	@Override
	public GeoElement [] PolyLineND(String [] labels, GeoPointND [] P) {
		return PolyLine(labels,P);
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
	 * Corner of Drawing Pad Michael Borcherds 2008-05-10
	 */
	final public GeoPoint2 CornerOfDrawingPad(String label, NumberValue number, NumberValue ev) {
		AlgoDrawingPadCorner algo = new AlgoDrawingPadCorner((Construction)cons, label, number, ev);	
		return algo.getCorner();
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
