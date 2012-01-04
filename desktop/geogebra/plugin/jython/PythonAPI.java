package geogebra.plugin.jython;

import java.util.ArrayList;

import geogebra.common.awt.Color;
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
import geogebra.common.kernel.arithmetic.Operation;
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
import geogebra.main.Application;


/**
 * @author arno
 * API for interaction with Python - could be used for other languages as well
 * This class must not be obfuscated!
 */
public class PythonAPI {
	
	public static Class GeoPointClass = GeoPoint2.class;
	public static Class GeoElementClass = GeoElement.class;
	public static Class GeoNumericClass = GeoNumeric.class;
	public static Class GeoVectorClass = GeoVector.class;
	public static Class GeoFunctionClass = GeoFunction.class;
	public static Class GeoTextClass = GeoText.class;
	public static Class GeoConicClass = GeoConic.class;
	public static Class GeoLineClass = GeoLine.class;
	public static Class GeoSegmentClass = GeoSegment.class;
	public static Class GeoRayClass = GeoRay.class;
	public static Class GeoBooleanClass = GeoBoolean.class;
	public static Class GeoLocusClass = GeoLocus.class;
	
	/**
	 * @author arno
	 * Adapter for various GeoElements
	 */
	public static class Geo {
				
		protected GeoElement geo;
		
		public Geo(GeoElement geo) {
			this.geo = geo;
		}
		
		public String toString() {
			return geo.toString();
		}
		
		public boolean equals(Geo other) {
			return this.geo == other.geo;
		}
		
		public boolean __eq__(Geo other) {
			return this.geo == other.geo;
		}
		
		public int hashCode() {
			return geo.hashCode();
		}
		
		public Class getType() {
			return geo.getClass();
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
		
	}
	
	/**
	 * @author arno
	 * Adapter for various kinds of ExpressionValues
	 */
	public static class Expression {
		
		protected ExpressionValue expr;
		
		public Expression(ExpressionValue expr) {
			this.expr = expr;
		}
		
		public String toString() {
			return expr.toString();
		}
		
		public Expression evaluate() {
			return new Expression(expr.evaluate());
		}
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
	
}
