package geogebra.web.kernel;

import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.AbstractUndoManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MacroKernelInterface;
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
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.util.LaTeXCache;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.web.main.Application;
import geogebra.web.util.NumberFormat;

public class Kernel extends AbstractKernel {

	public Kernel(Application app) {
		this();
		this.app = app;
		geogebra.common.factories.AwtFactory.prototype = new geogebra.web.factories.AwtFactory();
		geogebra.common.util.StringUtil.prototype = new geogebra.common.util.StringUtil();
		// TODO: probably there is better way
		geogebra.common.awt.Color.black = geogebra.web.awt.Color.black;
		geogebra.common.awt.Color.white = geogebra.web.awt.Color.white;
		geogebra.common.awt.Color.blue = geogebra.web.awt.Color.blue;
		geogebra.common.awt.Color.gray = geogebra.web.awt.Color.gray;
		geogebra.common.awt.Color.lightGray = geogebra.web.awt.Color.lightGray;
		geogebra.common.awt.Color.darkGray = geogebra.web.awt.Color.darkGray;
		
		geogebra.common.euclidian.HatchingHandler.prototype = new geogebra.web.euclidian.HatchingHandler();
		geogebra.common.euclidian.EuclidianStatic.prototype = new geogebra.web.euclidian.EuclidianStatic();
		newConstruction();
		getExpressionNodeEvaluator();
	}
	
	public Kernel() {
		super();
	}

	@Override
    public NumberFormatAdapter getNumberFormat() {
	    // TODO Auto-generated method stub
	    return new NumberFormat();
    }

	@Override
    public NumberFormatAdapter getNumberFormat(String s) {
	    // TODO Auto-generated method stub
	    return new NumberFormat(s);
    }

	@Override
    public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public ScientificFormatAdapter getScientificFormat(int a, int b, boolean c) {
	    // TODO Auto-generated method stub
	    return null;
    }


	@Override
    public LaTeXCache newLaTeXCache() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoGebraCasInterface newGeoGebraCAS() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractAnimationManager getAnimatonManager() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractGeoElementSpreadsheet getGeoElementSpreadsheet() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractUndoManager getUndoManager(Construction cons) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractCommandDispatcher getCommandDispatcher() {
	    // TODO Auto-generated method stub
	    return null;
    }
	
	@Override
    public AbstractGeoTextField getGeoTextField(Construction cons) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement TravelingSalesman(String a, GeoList b) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement Voronoi(String a, GeoList b) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement ConvexHull(String a, GeoList b) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement MinimumSpanningTree(String a, GeoList b) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement DelauneyTriangulation(String a, GeoList b) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement ShortestDistance(String label, GeoList geoList,
            GeoPointND geoPointND, GeoPointND geoPointND2, GeoBoolean geoBoolean) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement SolveODE(String label, FunctionalNVar functionalNVar,
            FunctionalNVar functionalNVar2, GeoNumeric geoNumeric,
            GeoNumeric geoNumeric2, GeoNumeric geoNumeric3,
            GeoNumeric geoNumeric4) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement SolveODE2(String label, GeoFunctionable geoFunctionable,
            GeoFunctionable geoFunctionable2, GeoFunctionable geoFunctionable3,
            GeoNumeric geoNumeric, GeoNumeric geoNumeric2,
            GeoNumeric geoNumeric3, GeoNumeric geoNumeric4,
            GeoNumeric geoNumeric5) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement[] Union(String[] labels, GeoPolygon geoPolygon,
            GeoPolygon geoPolygon2) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement Hull(String label, GeoList geoList, GeoNumeric geoNumeric) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement[] IntersectPolygons(String[] labels,
            GeoPolygon geoPolygon, GeoPolygon geoPolygon2) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public MacroKernelInterface newMacroKernel() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
}