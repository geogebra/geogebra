package geogebra.plugin.jython;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import geogebra.common.awt.Color;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.algos.AlgoDependentPoint;
import geogebra.common.kernel.algos.AlgoDependentVector;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyVecNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.Kernel;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.main.Application;


/**
 * @author arno
 * API for interaction with Python - could be used for other languages as well
 * This class must not be obfuscated!
 */
public class PythonAPI {
	
	/*
	 * Classes that pyggb.py needs to know about
	 */
	
	public static Class<GeoPoint2> GeoPointClass = GeoPoint2.class;
	public static Class<GeoElement> GeoElementClass = GeoElement.class;
	public static Class<GeoNumeric> GeoNumericClass = GeoNumeric.class;
	public static Class<GeoVector> GeoVectorClass = GeoVector.class;
	public static Class<GeoFunction> GeoFunctionClass = GeoFunction.class;
	public static Class<GeoText> GeoTextClass = GeoText.class;
	public static Class<GeoConic> GeoConicClass = GeoConic.class;
	public static Class<GeoLine> GeoLineClass = GeoLine.class;
	public static Class<GeoSegment> GeoSegmentClass = GeoSegment.class;
	public static Class<GeoRay> GeoRayClass = GeoRay.class;
	public static Class<GeoBoolean> GeoBooleanClass = GeoBoolean.class;
	public static Class<GeoLocus> GeoLocusClass = GeoLocus.class;
	
	/**
	 * @author arno
	 * Wrapper for various kinds of ExpressionValues
	 */
	public static class Expression {
		
		/**
		 * The wrapped expression
		 */
		protected ExpressionValue expr;
		
		/**
		 * Constructor
		 * @param expr wrapped expression
		 */
		public Expression(ExpressionValue expr) {
			this.expr = expr;
		}
		
		@Override
		public String toString() {
			return expr.toString();
		}
		
		/**
		 * Evaluate the wrapped expression
		 * @return wrapped evaluated expression
		 */
		public Expression evaluate() {
			return new Expression(expr.evaluate());
		}
		
		/**
		 * Test if an expression has numerical value
		 * @return true is expression is a number
		 */
		public boolean isNumber() {
			return expr.isNumberValue();
		}
		
		public double getNumber() {
			return ((NumberValue) expr).getDouble();
		}
		
		public boolean isVector() {
			return expr.isVectorValue();
		}
		
		public double[] getCoords() {
			return ((MyVecNode) expr).getCoords();
		}
		
		public boolean isBoolean() {
			return expr.isBooleanValue();
		}
		
		public boolean getBoolean() {
			return ((BooleanValue) expr).getBoolean();
		}
		
		public boolean isNode() {
			return expr.isExpressionNode();
		}
		
		public String getNodeLabel() {
			return ((ExpressionNode) expr).getLabel();
		}
		
		public void setNodeLabel(String label) {
			((ExpressionNode) expr).setLabel(label);
		}
	}
	
	/**
	 * @author arno
	 * Adapter for various GeoElements
	 */
	public static class Geo extends Expression {
				
		protected GeoElement geo;
		
		public Geo(GeoElement geo) {
			super(geo);
			this.geo = geo;
		}
		
		@Override
		public String toString() {
			return geo.toString();
		}
		
		public boolean equals(Geo other) {
			return this.geo == other.geo;
		}
		
		public boolean __eq__(Geo other) {
			return this.geo == other.geo;
		}
		
		@Override
		public int hashCode() {
			return geo.hashCode();
		}
		
		public Class<? extends GeoElement> getType() {
			return geo.getClass();
		}
		
		public String getTypeString() {
			return geo.getTypeString();
		}
		
		/* General GeoElement methods */
		
		public void updateRepaint() {
			geo.updateRepaint();
		}
		public String getLabel() {
			return geo.getLabel();
		}
		public void setLabel(String label) {
			geo.setLabel(label);
		}
		public Color getColor() {
			return geo.getObjectColor();
		}
		public void setColor(Color color) {
			geo.setObjColor(color);
		}
		public String getCaption() {
			return geo.getCaption();
		}
		public void setCaption(String caption) {
			geo.setCaption(caption);
		}
		public int getLabelMode() {
			return geo.getLabelMode();
		}
		public void setLabelMode(int mode) {
			geo.setLabelMode(mode);
		}
		public Color getLabelColor() {
			return geo.getLabelColor();
		}
		public void setLabelColor(Color color) {
			geo.setLabelColor(color);
		}
		public boolean isLabelVisible() {
			return geo.isLabelVisible();
		}
		public void setLabelVisible(boolean v) {
			geo.setLabelVisible(v);
		}
		public Color getBackgroundColor() {
			return geo.getBackgroundColor();
		}
		public void setBackgroundColor(Color color) {
			geo.setBackgroundColor(color);
		}
		public int getLineThickness() {
			return geo.getLineThickness();
		}
		public void setLineThickness(int thickness) {
			geo.setLineThickness(thickness);
		}
		public int getLineType() {
			return geo.getLineType();
		}
		public void setLineType(int type) {
			geo.setLineType(type);
		}
		
		/* GeoVec3D methods */
		
		public void setCoords(double x, double y, double z) {
			((GeoVec3D) geo).setCoords(x, y, z);
		}
		
		/* Path methods */
		
		public boolean isOnPath(Geo point, double eps) {
			return ((Path) geo).isOnPath((GeoPointND) point.geo, eps);
		}
		
		/* GeoLine methods */
		
		public Geo getStartPoint() {
			return new Geo(((GeoLine) geo).getStartPoint());
		}
		public void setStartPoint(Geo point) {
			((GeoLine) geo).setStartPoint((GeoPoint2) point.geo);
		}
		public Geo getEndPoint() {
			return new Geo(((GeoLine) geo).getEndPoint());
		}
		public void setEndPoint(Geo point) {
			((GeoLine) geo).setEndPoint((GeoPoint2) point.geo);
		}
		
		/* Text methods */
		
		public Geo getTextOrigin() {
			GeoText txt = (GeoText) geo;
			return new Geo((GeoPoint2) txt.getStartPoint());
		}
		
		public void setTextOrigin(Geo start) throws CircularDefinitionException {
			GeoText txt = (GeoText) geo;
			txt.setStartPoint((GeoPointND) start.geo);
		}
		
		public void removeTextOrigin() {
			GeoText txt = (GeoText) geo;
			GeoPointND orig = txt.getStartPoint();
			txt.removeStartPoint(orig);
		}
	}
	
	private Application app;
	private Kernel kernel;
	private Construction cons;
	private AlgebraProcessor algProcessor;
	
	/**
	 * Create a new PythonAPI instance
	 * @param app the running application instance
	 */
	public PythonAPI(Application app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.algProcessor = kernel.getAlgebraProcessor();
	}
	
	/**
	 * Find a Geo with a given label
	 * @param label a GeoElement's label
	 * @return the Geo with that label
	 */
	public Geo lookupLabel(String label) {
		return new Geo(kernel.lookupLabel(label));
	}
	
	/*
	 * Creating geos
	 */
	
	public Geo geoNumber(double x) {
		return new Geo(new GeoNumeric(cons, x));
	}
	
	public Geo geoNumber(Expression expr) {
		
		AlgoDependentNumber algo = new AlgoDependentNumber(cons, getNode(expr.expr), false);
		return new Geo(algo.getNumber());
	}
	
	public Geo geoVector(Expression expr) {
		AlgoDependentVector algo = new AlgoDependentVector(cons, getNode(expr.expr));
		return new Geo(algo.getVector());
	}
	
	public Geo geoVector(double x, double y) {
		return new Geo(kernel.Vector(x, y));
	}
	
	public Geo geoVector(Geo start, Geo end) {
		return new Geo(kernel.Vector(null, (GeoPoint2) start.geo, (GeoPoint2) end.geo));
	}
	
	public Geo geoVector(Geo pos) {
		return new Geo(kernel.Vector(null, (GeoPoint2) pos.geo));
	}
	
	public Geo geoLineDirection(Geo line) {
		return new Geo(kernel.Direction(null, (GeoLine) line.geo));
	}
	
	public Geo geoPoint(Expression expr) {
		AlgoDependentPoint algo = new AlgoDependentPoint(cons, getNode(expr.expr), false);
		return new Geo(algo.getPoint());
	}
	
	public Geo geoPoint(double x, double y) {
		return new Geo(kernel.Point(null, x, y));
	}
	
	public Geo geoPointOnPath(Geo path, Geo param) {
		return new Geo(kernel.Point(null, (Path) path.geo, (NumberValue) param.geo));
	}
	
	public Geo geoLinePP(Geo p, Geo q) {
		return new Geo(kernel.Line(null,  (GeoPoint2) p.geo, (GeoPoint2) q.geo));
	}
	
	public Geo geoLinePV(Geo p, Geo q) {
		return new Geo(kernel.Line(null,  (GeoPoint2) p.geo, (GeoVector) q.geo));
	}
	
	public Geo geoLinePL(Geo p, Geo l) {
		return new Geo(kernel.Line(null,  (GeoPoint2) p.geo, (GeoLine) l.geo));
	}
	
	public Geo geoSegment(Geo p, Geo q) {
		return new Geo(kernel.Segment(null, (GeoPoint2) p.geo , (GeoPoint2) q.geo));
	}
	
	public Geo geoRayPP(Geo p, Geo q) {
		return new Geo(kernel.Ray(null, (GeoPoint2) p.geo, (GeoPoint2) q.geo));
	}
	
	public Geo geoFunction(Expression f, Expression x) {
		Function func = new Function(getNode(f.expr), (FunctionVariable) x.expr);
		GeoElement[] geos = algProcessor.processFunction(null, func);
		return new Geo(geos[0]);
	}
	
	public Geo geoFunctionNVar(Expression f, Expression[] xs) {
		FunctionVariable[] vars = new FunctionVariable[xs.length];
		for (int i = 0; i < xs.length; i++) {
			vars[i] = (FunctionVariable) xs[i].expr;
		}
		FunctionNVar func = new FunctionNVar(getNode(f.expr), vars);
		GeoElement[] geos = algProcessor.processFunctionNVar(null, func);
		return new Geo(geos[0]);
	}
	
	public Geo geoImplicitPoly(Geo f) {
		return new Geo(kernel.ImplicitPoly(null, (GeoFunctionNVar) f.geo));
	}
	
	public Geo geoText(String text) {
		return new Geo(kernel.Text(null, text));
	}
	
	public Geo geoConic(Geo[] geos) {
		GeoPoint2[] points = (GeoPoint2[]) unwrapGeos(geos);
		return new Geo(kernel.Conic(null, points));
	}
	
	public Geo geoCircleCP(Geo center, Geo point) {
		return new Geo(kernel.Circle(null, (GeoPoint2) center.geo, (GeoPoint2) point.geo));
	}
	
	public Geo geoCirclePPP(Geo p, Geo q, Geo r) {
		return new Geo(kernel.Circle(null, (GeoPoint2) p.geo, (GeoPoint2) q.geo, (GeoPoint2) r.geo));
	}
	
	public Geo geoCircleCS(Geo c, Geo s) {
		return new Geo(kernel.Circle(null, (GeoPoint2) c.geo, (GeoSegment) s.geo));
	}
	
	public Geo geoCircleCR(Geo c, Geo r) {
		return new Geo(kernel.Circle(null, (GeoPoint2) c.geo, (NumberValue) r.geo));
	}
	
	public Geo geoEllipseFFP(Geo s1, Geo s2, Geo p) {
		return new Geo(kernel.Ellipse(null, (GeoPoint2) s1.geo, (GeoPoint2) s2.geo, (GeoPoint2) p.geo));
	}
	
	public Geo geoEllipseFFA(Geo s1, Geo s2, Geo a) {
		return new Geo(kernel.Ellipse(null, (GeoPoint2) s1.geo, (GeoPoint2) s2.geo, (NumberValue) a.geo));
	}
	
	public Geo geoHyperbolaFFP(Geo s1, Geo s2, Geo p) {
		return new Geo(kernel.Hyperbola(null, (GeoPoint2) s1.geo, (GeoPoint2) s2.geo, (GeoPoint2) p.geo));
	}
	
	public Geo geoHyperbolaFFA(Geo s1, Geo s2, Geo a) {
		return new Geo(kernel.Hyperbola(null, (GeoPoint2) s1.geo, (GeoPoint2) s2.geo, (NumberValue) a.geo));
	}
	
	public Geo geoParabola(Geo s, Geo l) {
		return new Geo(kernel.Parabola(null, (GeoPoint2) s.geo, (GeoLine) l.geo));
	}
	
	/*
	 * Creating expressions
	 */
	
	/**
	 * Create an expression representing a 2D vector
	 * @param x = x-coord expression
	 * @param y = y-coord expression
	 * @return vector (x, y)
	 */
	public Expression vectorExpression(Expression x, Expression y) {
		MyVecNode node = new MyVecNode(kernel, x.expr, y.expr);
		return new Expression(node);
	}
	
	/**
	 * Create an expression out a double
	 * @param x = a double
	 * @return number expression x
	 */
	public Expression numberExpression(double x) {
		MyDouble number = new MyDouble(kernel, x);
		return new Expression(number);
	}
	
	/**
	 * Return an expression for the x-coord of a geo
	 * @param geo geo to find the x-coord of
	 * @return x-coord of geo
	 */
	public Expression xCoordExpression(Geo geo) {
		ExpressionNode node = new ExpressionNode(kernel, geo.geo, Operation.XCOORD, null);
		return new Expression(node);
	}
	
	/**
	 * Return an expression for the y-coord of a geo
	 * @param geo geo to find the y-coord of
	 * @return y-coord of geo
	 */
	public Expression yCoordExpression(Geo geo) {
		ExpressionNode node = new ExpressionNode(kernel, geo.geo, Operation.YCOORD, null);
		return new Expression(node);
	}
	
	private ExpressionNode getNode(ExpressionValue e) {
		if (e.isExpressionNode()) {
			return (ExpressionNode) e;
		}
		return new ExpressionNode(kernel, e);
	}
	
	/**
	 * Return an expression whose isNode() returns true
	 * @param expr = an expression
	 * @return a node version of the expression
	 */
	public Expression nodeExpression(Expression expr) {
		if (expr.isNode()) {
			return expr;
		}
		return new Expression(getNode(expr.expr));
	}
	
	/**
	 * Create a node combining two operations together
	 * @param left = left operand
	 * @param op = operator
	 * @param right = right operand
	 * @return the expression (left op right)
	 */
	public Expression nodeExpression(Expression left, Operation op, Expression right) {
		ExpressionNode node = new ExpressionNode(kernel, left.expr, op, right.expr);
		return new Expression(node);
	}
	
	/**
	 * Create a unary operation node
	 * @param arg operand
	 * @param op operator / function
	 * @return the expression
	 */
	public Expression nodeExpression(Expression arg, Operation op) {
		ExpressionNode node = new ExpressionNode(kernel, arg.expr, op, null);
		return new Expression(node);
	}
	
	/**
	 * Create an expression containing a variable (for building functions)
	 * @param varname name of the variable
	 * @return a variable expression
	 */
	public Expression variableExpression(String varname) {
		return new Expression(new FunctionVariable(kernel, varname));
	}
	
	/**
	 * @param expr the expression to make a geo of
	 * @return a geo of the expression
	 */
	public Geo getGeo(Expression expr) {
		boolean flag = cons.isSuppressLabelsActive();
		ExpressionNode node = getNode(expr.expr);
		try {
			cons.setSuppressLabelCreation(true);
			GeoElement[] geos = algProcessor.processExpressionNode(node);
			return new Geo(geos[0]);
		} finally {
			cons.setSuppressLabelCreation(flag);			
		}
	}
	
	/*
	 * Intersections
	 */
	
	/**
	 * Find the intersection of two lines
	 * @param l1 first line
	 * @param l2 second line
	 * @return intersection point
	 */
	public Geo intersectLines(Geo l1, Geo l2) {
		GeoPointND p = kernel.IntersectLines(null, (GeoLineND) l1.geo, (GeoLineND) l2.geo);
		return new Geo((GeoPoint2) p);
	}
	
	static private Geo[] wrapGeoElements(GeoElement[] geos) {
		Geo[] wrapped = new Geo[geos.length];
		for (int i = 0; i < geos.length; i++) {
			wrapped[i] = new Geo(geos[i]);
		}
		return wrapped;
	}
	
	static private GeoElement[] unwrapGeos(Geo[] geos) {
		GeoElement [] unwrapped = new GeoElement[geos.length];
		for (int i = 0; i < geos.length; i++) {
			unwrapped[i] = geos[i].geo;
		}
		return unwrapped;
	}
	
	/**
	 * Find the intersection of a line and a conic
	 * @param l line
	 * @param c conic
	 * @return intersection points of l and c
	 */
	public Geo[] intersectLineConic(Geo l, Geo c) {
		GeoPoint2[] geos = kernel.IntersectLineConic(null, (GeoLine) l.geo, (GeoConic) c.geo);
		return wrapGeoElements(geos);
	}
	
	/**
	 * Find the interscrtion of two conics
	 * @param c1 first conic
	 * @param c2 second conic
	 * @return intersection points of conics
	 */
	public Geo[] intersectConics(Geo c1, Geo c2) {
		GeoPoint2[] geos = (GeoPoint2[]) kernel.IntersectConics(null, (GeoConicND) c1.geo, (GeoConicND) c2.geo);
		return wrapGeoElements(geos);
	}
	
	/*
	 * Selection
	 */
	
	/**
	 * Return all selected geoelements
	 * @return array of selected geoelements
	 */
	public Geo[] getSelectedGeos() {
		ArrayList<GeoElement> geos = app.getSelectedGeos();
		return wrapGeoElements((GeoElement[]) geos.toArray());
	}
	
	/*
	 * Construction manipulation
	 */
	
	/**
	 * Return all geos of a certain type
	 * @param type the GeoClass desired
	 * @return array of all geos in the construction of given type
	 */
	public Geo[] getGeos(GeoClass type) {
		TreeSet<GeoElement> geoSet = cons.getGeoSetLabelOrder(type);
		Geo[] geos = new Geo[geoSet.size()];
		Iterator<GeoElement> it = geoSet.iterator();
		int i = 0;
		while (it.hasNext()) {
			geos[i++] = new Geo(it.next());
		}
		return geos;
	}
	
	/*
	 * Listening to selections
	 */

	/**
	 * start listening to selections
	 */
	public void startSelectionListener() {
		app.setSelectionListenerMode(app.getPythonBridge());
	}
	
	/**
	 * Stop listening to selections
	 */
	public void stopSelectionListener() {
		app.setSelectionListenerMode(null);
	}
}
