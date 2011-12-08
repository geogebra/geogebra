package geogebra.web.kernel;

import geogebra.common.awt.Color;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.EquationSolverInterface;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNodeEvaluatorInterface;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.cas.GeoGebraCasInterfaceSlim;
import geogebra.common.kernel.commands.AbstractAlgebraProcessor;
import geogebra.common.kernel.geos.AbstractGeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoConicInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.optimization.ExtremumFinderInterface;
import geogebra.common.kernel.parser.ParserInterface;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.GgbMat;
import geogebra.common.util.LaTeXCache;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.web.main.Application;


public class Kernel extends AbstractKernel {

	public Kernel(Application application) {
	    // TODO Auto-generated constructor stub
    }

	@Override
    public Color getColorAdapter(int red, int green, int blue) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Color getColorAdapter(int red, int green, int blue, int alpha) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Color getColorAdapter(float red, float green, float blue, float alpha) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public NumberFormatAdapter getNumberFormat() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public NumberFormatAdapter getNumberFormat(String s) {
	    // TODO Auto-generated method stub
	    return null;
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
    protected void notifyEuclidianViewCE() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public LaTeXCache newLaTeXCache() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement lookupLabel(String label) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElement lookupLabel(String label, boolean b) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractConstruction getConstruction() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractApplication getApplication() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void notifyRepaint() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void initUndoInfo() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public GeoGebraCasInterfaceSlim newGeoGebraCAS() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public int getConstructionStep() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public void storeUndoInfo() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public double[] getViewBoundsForGeo(GeoElementInterface geo) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void notifyUpdate(GeoElementInterface geo) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public AbstractAnimationManager getAnimatonManager() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void notifyRename(GeoElementInterface geoElement) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void notifyRemove(GeoElementInterface geoElement) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void notifyUpdateVisualStyle(GeoElementInterface geoElement) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void notifyUpdateAuxiliaryObject(GeoElementInterface geoElement) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void notifyAdd(GeoElementInterface geoElement) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public AbstractAlgebraProcessor getAlgebraProcessor() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElementInterface lookupCasCellLabel(String cmdName) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElementInterface lookupCasRowReference(String cmdName) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoElementInterface Semicircle(String label, GeoPoint2 geoPoint,
            GeoPoint2 geoPoint2) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AbstractGeoElementSpreadsheet getGeoElementSpreadsheet() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GgbMat getGgbMat(MyList myList) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public boolean isInsertLineBreaks() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public ExpressionNodeEvaluatorInterface getExpressionNodeEvaluator() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String[] getPolynomialCoeffs(String function, String var) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoPoint2[] RootMultiple(String[] labels, GeoFunction f) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public double getViewsXMax(GeoElement geo) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public double getViewsXMin(GeoElement geo) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public GeoNumeric getDefaultNumber(boolean geoAngle) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public boolean isNotifyViewsActive() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public GeoFunction DependentFunction(String label, Function fun) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoSegmentND SegmentND(String label, GeoPointND P, GeoPointND Q) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String temporaryGetInterGeoStringForAlgoPointOnPath(
            String classname, AlgoElement algo) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public ParserInterface getParser() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoConicInterface getGeoConic() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public ExtremumFinderInterface getExtremumFinder() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeoPoint2 getGeoPoint(double d, double e, int i) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void updateConstruction() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void notifyReset() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public EquationSolverInterface getEquationSolver() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void resetGeoGebraCAS() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void getKernelXML(StringBuilder sb, boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public Color getColorAdapter(float red, float green, float blue) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Object Ray(String label, GeoPoint2 P, GeoPoint2 Q) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Object RayND(String label, GeoPointND P, GeoPointND Q) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Object Ray(String label, GeoPoint2 P, GeoVector v) {
	    // TODO Auto-generated method stub
	    return null;
    }

}