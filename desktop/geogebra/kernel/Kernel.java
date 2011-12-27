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
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MacroKernelInterface;
import geogebra.common.kernel.algos.AlgoTextfield;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.AbstractCommandDispatcher;
import geogebra.common.kernel.geos.AbstractGeoElementSpreadsheet;
import geogebra.common.kernel.geos.AbstractGeoTextField;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.AbstractApplication.CasType;
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
import geogebra.util.NumberFormatDesktop;
import geogebra.util.ScientificFormat;

public class Kernel extends AbstractKernel {



	public Kernel(AbstractApplication app) {
		this();
		this.app = app;

		geogebra.common.factories.AwtFactory.prototype = new geogebra.factories.AwtFactory();
		geogebra.common.util.StringUtil.prototype = new geogebra.util.StringUtil();
		// TODO: probably there is better way
		geogebra.common.awt.Color.black = geogebra.awt.Color.black;
		geogebra.common.awt.Color.white = geogebra.awt.Color.white;
		geogebra.common.awt.Color.blue = geogebra.awt.Color.blue;
		geogebra.common.awt.Color.gray = geogebra.awt.Color.gray;
		geogebra.common.awt.Color.lightGray = geogebra.awt.Color.lightGray;
		geogebra.common.awt.Color.darkGray = geogebra.awt.Color.darkGray;
		
		geogebra.common.euclidian.HatchingHandler.prototype = new geogebra.euclidian.HatchingHandler();
		geogebra.common.euclidian.EuclidianStatic.prototype = new geogebra.euclidian.EuclidianStatic();
		newConstruction();
		getExpressionNodeEvaluator();

		setManager3D(newManager3D(this));
	}

	public Kernel() {
		super();
	}

	
	/**
	 * returns GeoElement at (row,col) in spreadsheet may return nully
	 * 
	 * @param col
	 *            Spreadsheet column
	 * @param row
	 *            Spreadsheet row
	 * @return Spreadsheet cell content (may be null)
	 */
	public GeoElement getGeoAt(int col, int row) {
		return lookupLabel(GeoElementSpreadsheet.getSpreadsheetCellName(col,
				row));
	}

	
	/**
	 * Evaluates an expression in MathPiper syntax with.
	 * 
	 * @return result string (null possible)
	 * @throws Throwable
	 * 
	 *             final public String evaluateMathPiper(String exp) { if
	 *             (ggbCAS == null) { getGeoGebraCAS(); }
	 * 
	 *             return ggbCAS.evaluateMathPiper(exp); }
	 */

	/**
	 * Evaluates an expression in Maxima syntax with.
	 * 
	 * @return result string (null possible)
	 * @throws Throwable
	 * 
	 *             final public String evaluateMaxima(String exp) { if (ggbCAS
	 *             == null) { getGeoGebraCAS(); }
	 * 
	 *             return ggbCAS.evaluateMaxima(exp); }
	 */

	/**
	 * Returns this kernel's GeoGebraCAS object.
	 */



	// end G.Sturr

	final public CasType getCurrentCAS() {
		return ((GeoGebraCAS) getGeoGebraCAS()).currentCAS;
	}

	/**
	 * returns 10^(-PrintDecimals)
	 * 
	 * final public double getPrintPrecision() { return PRINT_PRECISION; }
	 */

	/*
	 * GeoElement specific
	 */

	
	// final public void notifyRemoveAll(View view) {
	// Iterator it = cons.getGeoSetConstructionOrder().iterator();
	// while (it.hasNext()) {
	// GeoElement geo = (GeoElement) it.next();
	// view.remove(geo);
	// }
	// }

	/**
	 * Tells views to update all labeled elements of current construction.
	 * 
	 * final public static void notifyUpdateAll() {
	 * notifyUpdate(kernelConstruction.getAllGeoElements()); }
	 */

	

	
	@Override
	final public GeoLocus Voronoi(String label, GeoList list) {
		AlgoVoronoi algo = new AlgoVoronoi(cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}

	@Override
	final public GeoLocus Hull(String label, GeoList list, GeoNumeric percent) {
		AlgoHull algo = new AlgoHull(cons, label, list, percent);
		GeoLocus ret = algo.getResult();
		return ret;
	}

	@Override
	final public GeoLocus TravelingSalesman(String label, GeoList list) {
		AlgoTravelingSalesman algo = new AlgoTravelingSalesman(cons, label,
				list);
		GeoLocus ret = algo.getResult();
		return ret;
	}

	@Override
	final public GeoLocus ConvexHull(String label, GeoList list) {
		AlgoConvexHull algo = new AlgoConvexHull(cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}

	@Override
	final public GeoLocus MinimumSpanningTree(String label, GeoList list) {
		AlgoMinimumSpanningTree algo = new AlgoMinimumSpanningTree(cons, label,
				list);
		GeoLocus ret = algo.getResult();
		return ret;
	}

	@Override
	final public GeoLocus ShortestDistance(String label, GeoList list,
			GeoPointND start, GeoPointND end, GeoBoolean weighted) {
		AlgoShortestDistance algo = new AlgoShortestDistance(cons, label, list,
				start, end, weighted);
		GeoLocus ret = algo.getResult();
		return ret;
	}

	@Override
	final public GeoLocus DelauneyTriangulation(String label, GeoList list) {
		AlgoDelauneyTriangulation algo = new AlgoDelauneyTriangulation(cons,
				label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}



	/**
	 * Intersect[polygon,polygon] G. Sturr
	 */
	@Override
	final public GeoElement[] IntersectPolygons(String[] labels,
			GeoPolygon poly0, GeoPolygon poly1) {
		AlgoPolygonIntersection algo = new AlgoPolygonIntersection(cons,
				labels, poly0, poly1);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}

	/**
	 * Union[polygon,polygon] G. Sturr
	 */
	@Override
	final public GeoElement[] Union(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1) {
		AlgoPolygonUnion algo = new AlgoPolygonUnion(cons, labels, poly0, poly1);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}

	/***********************************
	 * CALCULUS
	 ***********************************/

	@Override
	final public GeoLocus SolveODE(String label, FunctionalNVar f,
			FunctionalNVar g, GeoNumeric x, GeoNumeric y, GeoNumeric end,
			GeoNumeric step) {
		AlgoSolveODE algo = new AlgoSolveODE(cons, label, f, g, x, y, end, step);
		return algo.getResult();
	}

	/*
	 * second order ODEs
	 */
	@Override
	final public GeoLocus SolveODE2(String label, GeoFunctionable f,
			GeoFunctionable g, GeoFunctionable h, GeoNumeric x, GeoNumeric y,
			GeoNumeric yDot, GeoNumeric end, GeoNumeric step) {
		AlgoSolveODE2 algo = new AlgoSolveODE2(cons, label, f, g, h, x, y,
				yDot, end, step);
		return algo.getResult();
	}

	/**
	 * Numeric search for extremum of function f in interval [left,right] Ulven
	 * 2011-2-5
	 * 
	 * final public GeoPoint[] Extremum(String label,GeoFunction f,NumberValue
	 * left,NumberValue right) { AlgoExtremumNumerical algo=new
	 * AlgoExtremumNumerical(cons,label,f,left,right); GeoPoint
	 * g=algo.getNumericalExtremum(); //All variants return array... GeoPoint[]
	 * result=new GeoPoint[1]; result[0]=g; return result;
	 * }//Extremum(label,geofunction,numbervalue,numbervalue)
	 */

	/***********************************
	 * PACKAGE STUFF
	 ***********************************/

	// temp for buildEquation

	/*
	 * final private String formatAbs(double x) { if (isZero(x)) return "0";
	 * else return formatNF(Math.abs(x)); }
	 */

	


	@Override
	final public AbstractAnimationManager getAnimatonManager() {
		if (animationManager == null) {
			animationManager = new AnimationManager(this);
		}
		return animationManager;
	}

	public GeoTextField textfield(String label, GeoElement geoElement) {
		AlgoTextfield at = new AlgoTextfield(cons, label, geoElement);
		return (GeoTextField) at.getResult();
	}

	@Deprecated
	@Override
	public LaTeXCache newLaTeXCache() {
		return new GeoLaTeXCache();
	}

	@Override
	public GeoGebraCasInterface newGeoGebraCAS() {
		return new geogebra.cas.GeoGebraCAS(this);
	}

	// This is a temporary place for adapter creation methods which will move
	// into factories later

	@Override
	public NumberFormatAdapter getNumberFormat() {
		return new NumberFormatDesktop();
	}

	@Override
	public NumberFormatAdapter getNumberFormat(String pattern) {
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

	private GeoElementSpreadsheet ges = new GeoElementSpreadsheet();

	@Override
	public AbstractGeoElementSpreadsheet getGeoElementSpreadsheet() {
		return ges;
	}

	@Override
	public Geo3DVec getGeo3DVec(double x, double y, double z) {
		return new geogebra3D.kernel3D.Geo3DVec(this, x, y, z);
	}

	@Deprecated
	@Override
	public UndoManager getUndoManager(Construction cons) {
		return new UndoManager(cons);
	}

	@Deprecated
	@Override
	public AbstractCommandDispatcher getCommandDispatcher() {
		return new CommandDispatcher(this);
	}

	@Override
	public AbstractGeoTextField getGeoTextField(Construction cons) {
		return new GeoTextField(cons);
	}

	@Deprecated
	@Override
	public MacroKernelInterface newMacroKernel() {
		return new MacroKernel(this);
	}
}
