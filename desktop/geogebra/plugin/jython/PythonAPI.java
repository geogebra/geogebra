package geogebra.plugin.jython;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.StringTemplate;
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
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.plugin.Operation;
import geogebra.main.AppD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * API for interaction with Python - could be used for other
 * languages as well This class must not be obfuscated!
 * @author arno 
 * 
 */
public class PythonAPI {

	/*
	 * Classes that pyggb.py needs to know about
	 */

	@SuppressWarnings("javadoc")
	public static final Class<GeoPoint> GeoPointClass = GeoPoint.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoElement> GeoElementClass = GeoElement.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoNumeric> GeoNumericClass = GeoNumeric.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoAngle> GeoAngleClass = GeoAngle.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoVector> GeoVectorClass = GeoVector.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoFunction> GeoFunctionClass = GeoFunction.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoText> GeoTextClass = GeoText.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoButton> GeoButtonClass = GeoButton.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoConic> GeoConicClass = GeoConic.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoLine> GeoLineClass = GeoLine.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoSegment> GeoSegmentClass = GeoSegment.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoRay> GeoRayClass = GeoRay.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoBoolean> GeoBooleanClass = GeoBoolean.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoLocus> GeoLocusClass = GeoLocus.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoList> GeoListClass = GeoList.class;
	
	/**
	 * Wrapper for various kinds of ExpressionValues
	 * @author arno 
	 */
	public static class Expression {

		/**
		 * The wrapped expression
		 */
		protected ExpressionValue expr;

		/**
		 * Constructor
		 * 
		 * @param expr
		 *            wrapped expression
		 */
		public Expression(ExpressionValue expr) {
			this.expr = expr;
		}

		@Override
		public String toString() {
			return expr.toString(StringTemplate.defaultTemplate);
		}

		/**
		 * Evaluate the wrapped expression
		 * 
		 * @return wrapped evaluated expression
		 */
		public Expression evaluate() {
			return new Expression(expr.evaluate(StringTemplate.defaultTemplate));
		}

		/**
		 * Test if an expression has numerical value
		 * 
		 * @return true is expression is a number
		 */
		public boolean isNumber() {
			return expr.isNumberValue();
		}

		/**
		 * @return numerical value of the expression
		 */
		public double getNumber() {
			return ((NumberValue) expr).getDouble();
		}

		/**
		 * @return true if the expression is a vector
		 */
		public boolean isVector() {
			return expr.isVectorValue();
		}

		/**
		 * @return vector components of the expression
		 */
		public double[] getCoords() {
			return ((MyVecNode) expr).getCoords();
		}

		/**
		 * @return true if the expression is a boolean
		 */
		public boolean isBoolean() {
			return expr.isBooleanValue();
		}

		/**
		 * @return boolean value of the expression
		 */
		public boolean getBoolean() {
			return ((BooleanValue) expr).getBoolean();
		}

		/**
		 * @return true if the wrapped expression is a node
		 */
		public boolean isNode() {
			return expr.isExpressionNode();
		}

		/**
		 * @return the label of the expression, assuming it is a node
		 */
		public String getNodeLabel() {
			return ((ExpressionNode) expr).getLabel();
		}

		/**
		 * Set a new label for the wrapped expression, assuming it is a node
		 * 
		 * @param label
		 *            value of the new label
		 */
		public void setNodeLabel(String label) {
			((ExpressionNode) expr).setLabel(label);
		}
	}

	/**
	 * @author arno
	 * 
	 * Wrapper for various GeoElements
	 */
	public static class Geo extends Expression {

		/**
		 * The wrapped GeoElement
		 */
		protected GeoElement geo;

		/**
		 * Constructor
		 * 
		 * @param geo
		 *            the wrapped GeoElement
		 */
		public Geo(GeoElement geo) {
			super(geo);
			this.geo = geo;
		}

		@Override
		public String toString() {
			return geo.toString(StringTemplate.defaultTemplate);
		}

		/**
		 * 
		 * @param other
		 *            another Geo
		 * @return true if both wrapped geos refer to the same object
		 */
		public boolean equals(Geo other) {
			return this.geo == other.geo;
		}

		/**
		 * Override equality operator in Python
		 * 
		 * @param other
		 *            another Geo
		 * @return true if both wrapped geos refer to the sane object
		 */
		public boolean __eq__(Geo other) {
			return this.geo == other.geo;
		}

		@Override
		public int hashCode() {
			return geo.hashCode();
		}

		/**
		 * @return the class of the wrapped Geo
		 */
		public Class<? extends GeoElement> getType() {
			return geo.getClass();
		}

		/**
		 * @return the type string of the wrapped Geo
		 */
		public String getTypeString() {
			return geo.getTypeString();
		}

		/**
		 * Remove the geo
		 */
		public void remove() {
			geo.removeOrSetUndefinedIfHasFixedDescendent();
		}
		
		/* General GeoElement methods */

		/**
		 * update and repaint the Geo
		 */
		public void updateRepaint() {
			geo.updateRepaint();
		}

		/**
		 * @return the label of the wrapped Geo
		 */
		public String getLabel() {
			return geo.getLabelSimple();
		}
		
		/**
		 * Set the label of the wrapped Geo
		 * @param label the new label
		 */
		public void setLabel(String label) {
			geo.setLabel(label);
		}
		
		/**
		 * @return the Geo's color
		 */
		public GColor getColor() {
			return geo.getObjectColor();
		}
		
		/**
		 * Set the Geo's color
		 * @param color the new color
		 */
		public void setColor(GColor color) {
			geo.setObjColor(color);
		}
		
		/**
		 * @return the caption of the Geo
		 */
		public String getCaption() {
			return geo.getCaption(StringTemplate.defaultTemplate);
		}
		
		/**
		 * Set the caption of the Geo
		 * @param caption the new caption
		 */
		public void setCaption(String caption) {
			geo.setCaption(caption);
		}
		
		/**
		 * @return the label mode of the Geo
		 */
		public int getLabelMode() {
			return geo.getLabelMode();
		}
		
		/**
		 * Set the label mode of the Geo
		 * @param mode the new label mode
		 */
		public void setLabelMode(int mode) {
			geo.setLabelMode(mode);
		}
		
		/**
		 * @return the color of the Geo's label
		 */
		public GColor getLabelColor() {
			return geo.getLabelColor();
		}

		/**
		 * Set the color of the Geo's label
		 * @param color the new color for the label
		 */
		public void setLabelColor(GColor color) {
			geo.setLabelColor(color);
		}
		
		/**
		 * @return true if the label is visible
		 */
		public boolean isLabelVisible() {
			return geo.isLabelVisible();
		}
		
		/**
		 * Set the visibility of the Geo's label
		 * @param v true to make the label visible
		 */

		public void setLabelVisible(boolean v) {
			geo.setLabelVisible(v);
		}
		
		/**
		 * @return the background color for the Geo
		 */
		public GColor getBackgroundColor() {
			return geo.getBackgroundColor();
		}
		
		/**
		 * Set the background color for the Geo
		 * @param color the new background color for the Geo
		 */
		public void setBackgroundColor(GColor color) {
			geo.setBackgroundColor(color);
		}

		
		/**
		 * Get the trace value of the geo
		 * @return true if the geo is traceable and being traced
		 */
		public boolean getTrace() {
			return geo.isTraceable() && ((Traceable) geo).getTrace();
		}
		
		/**
		 * Set whether the geo is being traced or not
		 * @param trace the trace value
		 */
		public void setTrace(boolean trace) {
			if (geo.isTraceable()) {
				((Traceable) geo).setTrace(trace);
			}
		}
		/**
		 * @return the line thickness
		 */

		public int getLineThickness() {
			return geo.getLineThickness();
		}

		/**
		 * Set the Geo's line thickness
		 * @param thickness the new line thickness
		 */
		public void setLineThickness(int thickness) {
			geo.setLineThickness(thickness);
		}
		
		/**
		 * @return the Geo's line type
		 */
		public int getLineType() {
			return geo.getLineType();
		}
		
		/**
		 * Set the Geo's line type
		 * @param type the new line type
		 */
		public void setLineType(int type) {
			geo.setLineType(type);
		}

		/**
		 * @return true if the Geo is visible
		 */
		public boolean isEuclidianVisible() {
			return geo.isEuclidianVisible();
		}

		/**
		 * Set the visibility of the Geo
		 * @param b true to make the Geo visible
		 */
		public void setEuclidianVisible(boolean b) {
			geo.setEuclidianVisible(b);
		}

		/**
		 * @return true if the Geo is visible in algebra view
		 */
		public boolean isAlgebraVisible() {
			return geo.isAlgebraVisible();
		}
		
		/**
		 * Set the visibility of the Geo in algebra view
		 * @param b true to make the Geo visible
		 */
		public void setAlgebraVisible(boolean b) {
			geo.setAlgebraVisible(b);
		}
		
		/**
		 * @return true if object is auxiliary
		 */
		public boolean isAuxiliary() {
			return geo.isAuxiliaryObject();
		}
		
		/**
		 * Set whether the Geo is auxiliary
		 * @param b true to make the Geo auxiliary
		 */
		public void setAuxiliary(boolean b) {
			geo.setAuxiliaryObject(b);
		}
		
		/**
		 * @return true if this Geo conic is really a circle
		 */
		public boolean keepsType() {
			return ((GeoConicND) geo).keepsType();
		}
		
		/* Scripting */
		
		/**
		 * Get click script
		 * @return click script
		 */
		
		public String getClickScript() {
			return geo.getClickScript();
		}
		
		/**
		 * @return update script
		 */
		public String getUpdateScript() {
			return geo.getUpdateScript();
		}
		
		/**
		 * @param script new click script
		 */
		public void setClickScript(String script) {
			geo.setClickScriptType(GeoElement.ScriptType.PYTHON);
			geo.setClickScript(script, false);
		}
		
		/**
		 * @param script new update script
		 */
		public void setUpdateScript(String script) {
			geo.setUpdateScriptType(GeoElement.ScriptType.PYTHON);			
			geo.setUpdateScript(script, false);
		}
		
		/* GeoVec3D methods */

		/**
		 * Set the coordinates of the wrapped geoVec3D
		 * @param x new x-coordinate
		 * @param y new y-coordinate
		 * @param z new z-coordinate
		 */		
		public void setCoords(double x, double y, double z) {
			((GeoVec3D) geo).setCoords(x, y, z);
		}

		/* Path methods */

		/**
		 * @param point the point to check
		 * @param eps precision
		 * @return true if the point is on the path
		 */
		public boolean isOnPath(Geo point, double eps) {
			return ((Path) geo).isOnPath((GeoPointND) point.geo, eps);
		}

		/* GeoLine methods */

		/**
		 * @return the start point of the wrapped geoLine
		 */
		public Geo getStartPoint() {
			return new Geo(((GeoLine) geo).getStartPoint());
		}
		
		/**
		 * Set the start point of the wrapped GeoLine
		 * @param point the new start point
		 */
		public void setStartPoint(Geo point) {
			((GeoLine) geo).setStartPoint((GeoPoint) point.geo);
		}
		
		/**
		 * @return the end point of the wrapped GeoLine
		 */
		public Geo getEndPoint() {
			return new Geo(((GeoLine) geo).getEndPoint());
		}
		
		/**
		 * Set the end point of the wrapped GeoLine
		 * @param point the new end point
		 */
		public void setEndPoint(Geo point) {
			((GeoLine) geo).setEndPoint((GeoPoint) point.geo);
		}

		
		/* Text methods */

		
		/**
		 * @return the origin point of the wrapped GeoText
		 */
		public Geo getTextOrigin() {
			GeoText txt = (GeoText) geo;
			return new Geo((GeoPoint) txt.getStartPoint());
		}
		
		/* Numeric methods */
			
		/**
		 * @param x the new value
		 */
		public void setNumericValue(double x) {
			GeoNumeric num = (GeoNumeric) geo;
			num.setValue(x);
		}

		/**
		 * Set the start point of the wrapped GeoText
		 * @param start the new start point
		 * @throws CircularDefinitionException (dont' know)
		 */
		public void setTextOrigin(Geo start) throws CircularDefinitionException {
			GeoText txt = (GeoText) geo;
			txt.setStartPoint((GeoPointND) start.geo);
		}

		/**
		 * remove the start point of the wrapped GeoText
		 */
		public void removeTextOrigin() {
			GeoText txt = (GeoText) geo;
			GeoPointND orig = txt.getStartPoint();
			txt.removeStartPoint(orig);
		}
	}

	public AppD app;
	public Kernel kernel;
	public Construction cons;
	public AlgebraProcessor algProcessor;

	private static PythonAPI instance = null;
	
	/**
	 * Create a new PythonAPI instance
	 * 
	 * @param app
	 *            the running application instance
	 */
	private PythonAPI(AppD app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.algProcessor = kernel.getAlgebraProcessor();
	}
	
	/**
	 * Initialize the PythonAPI instance
	 * @param app the application
	 */
	public static void init(AppD app) {
		if (instance == null) {
			instance = new PythonAPI(app);
		}
	}
	
	/**
	 * @return the API instance
	 */
	public static PythonAPI getInstance() {
		return instance;
	}

	/**
	 * Find a Geo with a given label
	 * 
	 * @param label
	 *            a GeoElement's label
	 * @return the Geo with that label
	 */
	public Geo lookupLabel(String label) {
		return new Geo(kernel.lookupLabel(label));
	}

	/*
	 * Creating geos
	 */

	/**
	 * Create a new numeric Geo
	 * @param x the numeric value
	 * @return Geo with value x
	 */
	public Geo geoNumber(double x) {
		return new Geo(new GeoNumeric(cons, x));
	}

	/**
	 * Create a new numeric Geo from an expression
	 * @param expr expression to get the value of the geo from
	 * @return new Geo with the value of expr
	 */
	public Geo geoNumber(Expression expr) {

		AlgoDependentNumber algo = new AlgoDependentNumber(cons,
				getNode(expr.expr), false);
		return new Geo(algo.getNumber());
	}

	/**
	 * Create a new vector Geo from an expression
	 * @param expr expression to get the coordinates of the Geo from
	 * @return new vector Geo with coordinates given by expr
	 */
	public Geo geoVector(Expression expr) {
		AlgoDependentVector algo = new AlgoDependentVector(cons,
				getNode(expr.expr));
		return new Geo(algo.getVector());
	}

	/**
	 * Create a vector Geo from its coordinates
	 * @param x the vector's x-coordinate
	 * @param y the vector's y-coordinate
	 * @return new vector Geo with coordinates (x, y)
	 */
	public Geo geoVector(double x, double y) {
		return new Geo(kernel.Vector(x, y));
	}

	/**
	 * Create a new vector Geo
	 * @param start the vector's start point
	 * @param end the vector's end point
	 * @return new vector Geo from start to end
	 */
	public Geo geoVector(Geo start, Geo end) {
		return new Geo(kernel.Vector(null, (GeoPoint) start.geo,
				(GeoPoint) end.geo));
	}

	/**
	 * Create a position vector Geo
	 * @param pos the point
	 * @return new vector Geo from O to pos
	 */
	public Geo geoVector(Geo pos) {
		return new Geo(kernel.Vector(null, (GeoPoint) pos.geo));
	}

	/**
	 * @param line a Geo line
	 * @return the direction of the line
	 */
	public Geo geoLineDirection(Geo line) {
		return new Geo(kernel.Direction(null, (GeoLine) line.geo));
	}

	/**
	 * Create a new point from an expression
	 * @param expr expression giving the coordinates of the point
	 * @return new Geo point
	 */
	public Geo geoPoint(Expression expr) {
		AlgoDependentPoint algo = new AlgoDependentPoint(cons,
				getNode(expr.expr), false);
		return new Geo(algo.getPoint());
	}

	/**
	 * Create a new point from coordinates
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return new Geo point with coordinates (x, y)
	 */
	public Geo geoPoint(double x, double y) {
		return new Geo(kernel.Point(null, x, y));
	}

	/**
	 * Create a new point on a path
	 * @param path the path to point the point on
	 * @param param parameter to set the location of the point on the path
	 * @return new Geo on path
	 */
	public Geo geoPointOnPath(Geo path, Geo param) {
		return new Geo(kernel.Point(null, (Path) path.geo,
				(NumberValue) param.geo));
	}

	/**
	 * Create a new line through two points
	 * @param p first point on the line
	 * @param q second point on the line
	 * @return new Geo line
	 */
	public Geo geoLinePP(Geo p, Geo q) {
		return new Geo(kernel.Line(null, (GeoPoint) p.geo, (GeoPoint) q.geo));
	}

	/**
	 * Create a new line through a point with direction given by a vector
	 * @param p point on the line
	 * @param q direction vector for the line
	 * @return new Geo line
	 */
	public Geo geoLinePV(Geo p, Geo q) {
		return new Geo(kernel.Line(null, (GeoPoint) p.geo, (GeoVector) q.geo));
	}

	/**
	 * Create a new line through a point parallel to another line
	 * @param p point on the line
	 * @param l line parallel to the new line
	 * @return new Geo line
	 */
	public Geo geoLinePL(Geo p, Geo l) {
		return new Geo(kernel.Line(null, (GeoPoint) p.geo, (GeoLine) l.geo));
	}

	/**
	 * Create a new segment with two given end points
	 * @param p the first end point
	 * @param q the second end point
	 * @return new Geo segment
	 */
	public Geo geoSegment(Geo p, Geo q) {
		return new Geo(kernel.Segment(null, (GeoPoint) p.geo,
				(GeoPoint) q.geo));
	}

	/**
	 * Create a new ray from a point through another
	 * @param p the origin of the ray
	 * @param q a point on the ray
	 * @return new Geo ray
	 */
	public Geo geoRayPP(Geo p, Geo q) {
		return new Geo(kernel.Ray(null, (GeoPoint) p.geo, (GeoPoint) q.geo));
	}

	/**
	 * Create a new function from an expression and a variable
	 * @param f expression that represents the function
	 * @param x expression that represents the variable
	 * @return new Geo funcion
	 */
	public Geo geoFunction(Expression f, Expression x) {
		Function func = new Function(getNode(f.expr), (FunctionVariable) x.expr);
		GeoElement[] geos = algProcessor.processFunction(func);
		return new Geo(geos[0]);
	}

	/**
	 * Create a new function with several variables
	 * @param f the expression that represents the function
	 * @param xs and array of expression that represent the variables
	 * @return new Geo function
	 */
	public Geo geoFunctionNVar(Expression f, Expression[] xs) {
		FunctionVariable[] vars = new FunctionVariable[xs.length];
		for (int i = 0; i < xs.length; i++) {
			vars[i] = (FunctionVariable) xs[i].expr;
		}
		FunctionNVar func = new FunctionNVar(getNode(f.expr), vars);
		GeoElement[] geos = algProcessor.processFunctionNVar(func);
		return new Geo(geos[0]);
	}

	/**
	 * Create an implicit curve from a function
	 * @param f the function
	 * @return new Geo implicit curve representing f(x, y) = 0
	 */
	public Geo geoImplicitPoly(Geo f) {
		return new Geo(kernel.ImplicitPoly(null, (GeoFunctionNVar) f.geo));
	}

	/**
	 * Create a new text object
	 * @param text the text
	 * @return new Geo text
	 */
	public Geo geoText(String text) {
		return new Geo(kernel.Text(null, text));
	}

	/**
	 * Create a new conic through 5 points
	 * @param geos array of points
	 * @return the new Geo conic
	 */
	public Geo geoConic(Geo[] geos) {
		GeoPoint[] points = (GeoPoint[]) unwrapGeos(geos);
		return new Geo(kernel.Conic(null, points));
	}

	/**
	 * Create a new circle with given center and point on the circumference
	 * @param center the center
	 * @param point point on the circumference
	 * @return new Geo circle
	 */
	public Geo geoCircleCP(Geo center, Geo point) {
		return new Geo(kernel.Circle(null, (GeoPoint) center.geo,
				(GeoPoint) point.geo));
	}

	/**
	 * Create a new circle through three points
	 * @param p first point on the circumference
	 * @param q second point on the circumference
	 * @param r third point on the circumference
	 * @return new Geo circle
	 */
	public Geo geoCirclePPP(Geo p, Geo q, Geo r) {
		return new Geo(kernel.Circle(null, (GeoPoint) p.geo,
				(GeoPoint) q.geo, (GeoPoint) r.geo));
	}

	/**
	 * Create a new circle with given center and radius
	 * @param c center of the circle
	 * @param s segment giving the radius of the circle
	 * @return new Geo circle
	 */
	public Geo geoCircleCS(Geo c, Geo s) {
		return new Geo(kernel.Circle(null, (GeoPoint) c.geo,
				(GeoSegment) s.geo));
	}

	/**
	 * Create a new circle with given center and radius
	 * @param c center of the circle
	 * @param r radius of the circle
	 * @return new Geo circle
	 */
	public Geo geoCircleCR(Geo c, Geo r) {
		return new Geo(kernel.Circle(null, (GeoPoint) c.geo,
				(NumberValue) r.geo));
	}

	/**
	 * Create a new ellipse with two given foci and going through a given point
	 * @param s1 first focus of the ellipse
	 * @param s2 second focus of the ellipse
	 * @param p point on the ellipse
	 * @return new Geo ellipse
	 */
	public Geo geoEllipseFFP(Geo s1, Geo s2, Geo p) {
		return new Geo(kernel.Ellipse(null, (GeoPoint) s1.geo,
				(GeoPoint) s2.geo, (GeoPoint) p.geo));
	}

	/**
	 * Create a new ellipse with two given foci and a given semi-major axis length
	 * @param s1 first focus of the ellipse
	 * @param s2 second focus of the ellipse
	 * @param a length of semi-major axis
	 * @return new Geo ellipse
	 */
	public Geo geoEllipseFFA(Geo s1, Geo s2, Geo a) {
		return new Geo(kernel.Ellipse(null, (GeoPoint) s1.geo,
				(GeoPoint) s2.geo, (NumberValue) a.geo));
	}


	/**
	 * Create a new hyperbola with two given foci and going through a given point
	 * @param s1 first focus of the hyperbola
	 * @param s2 second focus of the hyperbola
	 * @param p point on the ellipse
	 * @return new Geo hyperbola
	 */
	public Geo geoHyperbolaFFP(Geo s1, Geo s2, Geo p) {
		return new Geo(kernel.Hyperbola(null, (GeoPoint) s1.geo,
				(GeoPoint) s2.geo, (GeoPoint) p.geo));
	}

	/**
	 * Create a new hyperbola with two given foci and a given semi-major axis length
	 * @param s1 first focus of the hyperbola
	 * @param s2 second focus of the hyperbola
	 * @param a length of semi-major axis
	 * @return new Geo hyperbola
	 */
	public Geo geoHyperbolaFFA(Geo s1, Geo s2, Geo a) {
		return new Geo(kernel.Hyperbola(null, (GeoPoint) s1.geo,
				(GeoPoint) s2.geo, (NumberValue) a.geo));
	}

	/**
	 * Create a new parabola with a given focus and directrix
	 * @param s the focus
	 * @param l the directrix
	 * @return new Geo parabola
	 */
	public Geo geoParabola(Geo s, Geo l) {
		return new Geo(
				kernel.Parabola(null, (GeoPoint) s.geo, (GeoLine) l.geo));
	}
	
	/* Lists */
	
	/**
	 * @param geos array of Geos to put in the list
	 * @return new geoList
	 */
	public Geo geoList(Geo[] geos) {
		ArrayList<GeoElement> lst = new ArrayList<GeoElement>();
		GeoElement[] unwrapped = unwrapGeos(geos);
		for (int i = 0; i < unwrapped.length; i++) {
			lst.add(unwrapped[i]);
		}
		return new Geo(kernel.List(null, lst, true));
	}
	
	/*
	 * Creating expressions
	 */

	/**
	 * Create an expression representing a 2D vector
	 * 
	 * @param x
	 *            = x-coord expression
	 * @param y
	 *            = y-coord expression
	 * @return vector (x, y)
	 */
	public Expression vectorExpression(Expression x, Expression y) {
		MyVecNode node = new MyVecNode(kernel, x.expr, y.expr);
		return new Expression(node);
	}

	/**
	 * Create an expression out a double
	 * 
	 * @param x
	 *            = a double
	 * @return number expression x
	 */
	public Expression numberExpression(double x) {
		MyDouble number = new MyDouble(kernel, x);
		return new Expression(number);
	}

	/**
	 * Return an expression for the x-coord of a geo
	 * 
	 * @param geo
	 *            geo to find the x-coord of
	 * @return x-coord of geo
	 */
	public Expression xCoordExpression(Geo geo) {
		ExpressionNode node = new ExpressionNode(kernel, geo.geo,
				Operation.XCOORD, null);
		return new Expression(node);
	}

	/**
	 * Return an expression for the y-coord of a geo
	 * 
	 * @param geo
	 *            geo to find the y-coord of
	 * @return y-coord of geo
	 */
	public Expression yCoordExpression(Geo geo) {
		ExpressionNode node = new ExpressionNode(kernel, geo.geo,
				Operation.YCOORD, null);
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
	 * 
	 * @param expr
	 *            = an expression
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
	 * 
	 * @param left
	 *            = left operand
	 * @param op
	 *            = operator
	 * @param right
	 *            = right operand
	 * @return the expression (left op right)
	 */
	public Expression nodeExpression(Expression left, Operation op,
			Expression right) {
		ExpressionNode node = new ExpressionNode(kernel, left.expr, op,
				right.expr);
		return new Expression(node);
	}

	/**
	 * Create a unary operation node
	 * 
	 * @param arg
	 *            operand
	 * @param op
	 *            operator / function
	 * @return the expression
	 */
	public Expression nodeExpression(Expression arg, Operation op) {
		ExpressionNode node = new ExpressionNode(kernel, arg.expr, op, null);
		return new Expression(node);
	}

	/**
	 * Create an expression containing a variable (for building functions)
	 * 
	 * @param varname
	 *            name of the variable
	 * @return a variable expression
	 */
	public Expression variableExpression(String varname) {
		return new Expression(new FunctionVariable(kernel, varname));
	}

	/**
	 * @param expr
	 *            the expression to make a geo of
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
	 * 
	 * @param l1
	 *            first line
	 * @param l2
	 *            second line
	 * @return intersection point
	 */
	public Geo intersectLines(Geo l1, Geo l2) {
		GeoPointND p = kernel.IntersectLines(null, (GeoLineND) l1.geo,
				(GeoLineND) l2.geo);
		return new Geo((GeoPoint) p);
	}

	static private Geo[] wrapGeoElements(GeoElement[] geos) {
		Geo[] wrapped = new Geo[geos.length];
		for (int i = 0; i < geos.length; i++) {
			wrapped[i] = new Geo(geos[i]);
		}
		return wrapped;
	}

	static private GeoElement[] unwrapGeos(Geo[] geos) {
		GeoElement[] unwrapped = new GeoElement[geos.length];
		for (int i = 0; i < geos.length; i++) {
			unwrapped[i] = geos[i].geo;
		}
		return unwrapped;
	}

	/**
	 * Find the intersection of a line and a conic
	 * 
	 * @param l
	 *            line
	 * @param c
	 *            conic
	 * @return intersection points of l and c
	 */
	public Geo[] intersectLineConic(Geo l, Geo c) {
		GeoPoint[] geos = kernel.IntersectLineConic(null, (GeoLine) l.geo,
				(GeoConic) c.geo);
		return wrapGeoElements(geos);
	}

	/**
	 * Find the interscrtion of two conics
	 * 
	 * @param c1
	 *            first conic
	 * @param c2
	 *            second conic
	 * @return intersection points of conics
	 */
	public Geo[] intersectConics(Geo c1, Geo c2) {
		GeoPoint[] geos = (GeoPoint[]) kernel.IntersectConics(null,
				(GeoConicND) c1.geo, (GeoConicND) c2.geo);
		return wrapGeoElements(geos);
	}

	/*
	 * Selection
	 */

	/**
	 * Return all selected geoelements
	 * 
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
	 * 
	 * @param type
	 *            the GeoClass desired
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

	/**
	 * Return all labelled geos in alphabetical order of typestring + label
	 * 
	 * @return array of all labelled geos in the construction
	 */
	public Geo[] getAllGeos() {
		TreeSet<GeoElement> geoSet = cons.getGeoSetNameDescriptionOrder();
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

	/**
	 * Arnaud's solution to fix #1670, comment: 15
	 * @return the GgbAPI instance of the application
	 */
	public GgbAPI getGgbApi() {
		return app.getGgbApi();
	}
	
	/**
	 * Get the Python init script
	 * @return the Python init script
	 */
	public String getInitScript() {
		return kernel.getLibraryPythonScript();
	}
	
	/**
	 * Set the Python init script
	 * @param script the new Python init script
	 */
	public void setInitScript(String script) {
		kernel.setLibraryPythonScript(script);
	}
}
