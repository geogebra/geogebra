package geogebra.plugin.jython;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import geogebra.common.awt.Color;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
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
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.Kernel;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.plugin.Operation;
import geogebra.main.Application;

/**
 * API for interaction with Python - could be used for other
 * languages as well This class must not be obfuscated!
 * @author arno 
 * 
 */
public class PythonFlatAPI {

	/*
	 * Classes that pyggb.py needs to know about
	 */

	@SuppressWarnings("javadoc")
	public static final Class<GeoPoint2> GeoPointClass = GeoPoint2.class;
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
		 * Evaluate the expression
		 * @param e expression to evaluate
		 * 
		 * @return evaluated expression
		 */
		public static ExpressionValue evaluate(ExpressionValue e) {
			return e.evaluate();
		}

		/**
		 * Test if an expression has numerical value
		 * 
		 * @return true is expression is a number
		 */
		public static boolean isNumber(ExpressionValue e) {
			return e.isNumberValue();
		}

		/**
		 * @return numerical value of the expression
		 */
		public static double getNumber(NumberValue e) {
			return e.getDouble();
		}

		/**
		 * @return true if the expression is a vector
		 */
		public static boolean isVector(ExpressionValue e) {
			return e.isVectorValue();
		}

		/**
		 * @return vector components of the expression
		 */
		public static double[] getCoords(MyVecNode v) {
			return v.getCoords();
		}

		/**
		 * @return true if the expression is a boolean
		 */
		public static boolean isBoolean(ExpressionValue e) {
			return e.isBooleanValue();
		}

		/**
		 * @return boolean value of the expression
		 */
		public static boolean getBoolean(BooleanValue b) {
			return b.getBoolean();
		}

		/**
		 * @return true if the wrapped expression is a node
		 */
		public static boolean isNode(ExpressionValue e) {
			return e.isExpressionNode();
		}

		/**
		 * @return the label of the expression, assuming it is a node
		 */
		public static String getNodeLabel(ExpressionNode n) {
			return n.getLabel();
		}

		/**
		 * Set a new label for the wrapped expression, assuming it is a node
		 * 
		 * @param label
		 *            value of the new label
		 */
		public static void setNodeLabel(ExpressionNode n, String label) {
			n.setLabel(label);
		}
	}

	/**
	 * @author arno
	 * 
	 * Wrapper for various GeoElements
	 */
	public static class Geo extends Expression {

		
		/**
		 * @return the class of the wrapped Geo
		 */
		public static Class<? extends GeoElement> getType(GeoElement geo) {
			return geo.getClass();
		}

		/**
		 * @return the type string of the wrapped Geo
		 */
		public static String getTypeString(GeoElement geo) {
			return geo.getTypeString();
		}

		/**
		 * Remove the geo
		 */
		public static void remove(GeoElement geo) {
			geo.removeOrSetUndefinedIfHasFixedDescendent();
		}
		
		/**
		 * Check whether an element is defined
		 * @param geo the geo element
		 * @return true if it is defined
		 */
		public static boolean isDefined(GeoElement geo) {
			return geo.isDefined();
		}
		/* General GeoElement methods */

		/**
		 * update and repaint the Geo
		 */
		public static void updateRepaint(GeoElement geo) {
			geo.updateRepaint();
		}

		/**
		 * @return the label of the wrapped Geo
		 */
		public static String getLabel(GeoElement geo) {
			return geo.getLabel();
		}
		
		/**
		 * Set the label of the wrapped Geo
		 * @param label the new label
		 */
		public static void setLabel(GeoElement geo, String label) {
			geo.setLabel(label);
		}
		
		/**
		 * @return the Geo's color
		 */
		public static Color getColor(GeoElement geo) {
			return geo.getObjectColor();
		}
		
		/**
		 * Set the Geo's color
		 * @param color the new color
		 */
		public static void setColor(GeoElement geo, Color color) {
			geo.setObjColor(color);
		}
		
		/**
		 * Find the opacity of the geo
		 * @param geo the target geo
		 * @return opacity between 0.0 and 1.0
		 */
		public static float getAlpha(GeoElement geo) {
			return geo.getAlphaValue();
		}
		
		/**
		 * Set the opacity of a geo
		 * @param geo the target geo
		 * @param alpha the new alpha
		 */
		public static void setAlpha(GeoElement geo, float alpha) {
			geo.setAlphaValue(alpha);
		}
		/**
		 * @return the caption of the Geo
		 */
		public static String getCaption(GeoElement geo) {
			return geo.getCaption(StringTemplate.defaultTemplate);
		}
		
		/**
		 * Set the caption of the Geo
		 * @param caption the new caption
		 */
		public static void setCaption(GeoElement geo, String caption) {
			geo.setCaption(caption);
		}
		
		/**
		 * @return the label mode of the Geo
		 */
		public static int getLabelMode(GeoElement geo) {
			return geo.getLabelMode();
		}
		
		/**
		 * Set the label mode of the Geo
		 * @param mode the new label mode
		 */
		public static void setLabelMode(GeoElement geo, int mode) {
			geo.setLabelMode(mode);
		}
		
		/**
		 * @return the color of the Geo's label
		 */
		public static Color getLabelColor(GeoElement geo) {
			return geo.getLabelColor();
		}

		/**
		 * Set the color of the Geo's label
		 * @param color the new color for the label
		 */
		public static void setLabelColor(GeoElement geo, Color color) {
			geo.setLabelColor(color);
		}
		
		/**
		 * @return true if the label is visible
		 */
		public static boolean isLabelVisible(GeoElement geo) {
			return geo.isLabelVisible();
		}
		
		/**
		 * Set the visibility of the Geo's label
		 * @param v true to make the label visible
		 */

		public static void setLabelVisible(GeoElement geo, boolean v) {
			geo.setLabelVisible(v);
		}
		
		/**
		 * @return the background color for the Geo
		 */
		public static Color getBackgroundColor(GeoElement geo) {
			return geo.getBackgroundColor();
		}
		
		/**
		 * Set the background color for the Geo
		 * @param color the new background color for the Geo
		 */
		public static void setBackgroundColor(GeoElement geo, Color color) {
			geo.setBackgroundColor(color);
		}

		
		/**
		 * Get the trace value of the geo
		 * @return true if the geo is traceable and being traced
		 */
		public static boolean getTrace(Traceable geo) {
			return geo.getTrace();
		}
		
		/**
		 * Set whether the geo is being traced or not
		 * @param trace the trace value
		 */
		public static void setTrace(Traceable geo, boolean trace) {
			geo.setTrace(trace);
		}
		/**
		 * @return the line thickness
		 */

		public static int getLineThickness(GeoElement geo) {
			return geo.getLineThickness();
		}

		/**
		 * Set the Geo's line thickness
		 * @param thickness the new line thickness
		 */
		public static void setLineThickness(GeoElement geo, int thickness) {
			geo.setLineThickness(thickness);
		}
		
		/**
		 * @return the Geo's line type
		 */
		public static int getLineType(GeoElement geo) {
			return geo.getLineType();
		}
		
		/**
		 * Set the Geo's line type
		 * @param type the new line type
		 */
		public static void setLineType(GeoElement geo, int type) {
			geo.setLineType(type);
		}

		/**
		 * @return true if the Geo is visible
		 */
		public static boolean isEuclidianVisible(GeoElement geo) {
			return geo.isEuclidianVisible();
		}

		/**
		 * Set the visibility of the Geo
		 * @param b true to make the Geo visible
		 */
		public static void setEuclidianVisible(GeoElement geo, boolean b) {
			geo.setEuclidianVisible(b);
		}

		/**
		 * @return true if the Geo is visible in algebra view
		 */
		public static boolean isAlgebraVisible(GeoElement geo) {
			return geo.isAlgebraVisible();
		}
		
		/**
		 * Set the visibility of the Geo in algebra view
		 * @param b true to make the Geo visible
		 */
		public static void setAlgebraVisible(GeoElement geo, boolean b) {
			geo.setAlgebraVisible(b);
		}
		
		/**
		 * @return true if object is auxiliary
		 */
		public static boolean isAuxiliary(GeoElement geo) {
			return geo.isAuxiliaryObject();
		}
		
		/**
		 * Set whether the Geo is auxiliary
		 * @param b true to make the Geo auxiliary
		 */
		public static void setAuxiliary(GeoElement geo, boolean b) {
			geo.setAuxiliaryObject(b);
		}
		
		/**
		 * @return true if this Geo conic is really a circle
		 */
		public static boolean keepsType(GeoConicND geo) {
			return geo.keepsType();
		}
		
		/* Scripting */
		
		/**
		 * Get click script
		 * @return click script
		 */
		
		public static String getClickScript(GeoElement geo) {
			return geo.getClickScript();
		}
		
		/**
		 * @return update script
		 */
		public static String getUpdateScript(GeoElement geo) {
			return geo.getUpdateScript();
		}
		
		/**
		 * @param script new click script
		 */
		public static void setClickScript(GeoElement geo, String script) {
			geo.setClickScriptType(GeoElement.ScriptType.PYTHON);
			geo.setClickScript(script, false);
		}
		
		/**
		 * @param script new update script
		 */
		public static void setUpdateScript(GeoElement geo, String script) {
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
		public static void setCoords(GeoVec3D geo, double x, double y, double z) {
			geo.setCoords(x, y, z);
		}

		/* Path methods */

		/**
		 * @param point the point to check
		 * @param eps precision
		 * @return true if the point is on the path
		 */
		public static boolean isOnPath(Path geo, GeoPointND point, double eps) {
			return geo.isOnPath(point, eps);
		}

		/* GeoLine methods */

		/**
		 * @return the start point of the wrapped geoLine
		 */
		public static GeoPoint2 getStartPoint(GeoLine geo) {
			return geo.getStartPoint();
		}
		
		/**
		 * Set the start point of the wrapped GeoLine
		 * @param point the new start point
		 */
		public static void setStartPoint(GeoLine geo, GeoPoint2 point) {
			geo.setStartPoint(point);
		}
		
		/**
		 * @return the end point of the wrapped GeoLine
		 */
		public static GeoPoint2 getEndPoint(GeoLine geo) {
			return geo.getEndPoint();
		}
		
		/**
		 * Set the end point of the wrapped GeoLine
		 * @param point the new end point
		 */
		public static void setEndPoint(GeoLine geo, GeoPoint2 point) {
			geo.setEndPoint(point);
		}

		
		/* Text methods */

		
		/**
		 * @return the origin point of the wrapped GeoText
		 */
		public static GeoPointND getTextOrigin(GeoText geo) {
			return geo.getStartPoint();
		}
		
		/* Numeric methods */
			
		/**
		 * @param x the new value
		 */
		public static void setNumericValue(GeoNumeric geo, double x) {
			geo.setValue(x);
		}

		/**
		 * Set the start point of the wrapped GeoText
		 * @param start the new start point
		 * @throws CircularDefinitionException (dont' know)
		 */
		public static void setTextOrigin(GeoText geo, GeoPointND start) throws CircularDefinitionException {
			geo.setStartPoint(start);
		}

		/**
		 * remove the start point of the wrapped GeoText
		 */
		public static void removeTextOrigin(GeoText geo) {
			geo.removeStartPoint(geo.getStartPoint());
		}
	}

	public Application app;
	public Kernel kernel;
	public Construction cons;
	public AlgebraProcessor algProcessor;

	/**
	 * Create a new PythonAPI instance
	 * 
	 * @param app
	 *            the running application instance
	 */
	public PythonFlatAPI(Application app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.algProcessor = kernel.getAlgebraProcessor();
	}
	

	/**
	 * Find a Geo with a given label
	 * 
	 * @param label
	 *            a GeoElement's label
	 * @return the Geo with that label
	 */
	public GeoElement lookupLabel(String label) {
		return kernel.lookupLabel(label);
	}

	/*
	 * Creating geos
	 */

	/**
	 * Create a new numeric Geo
	 * @param x the numeric value
	 * @return Geo with value x
	 */
	public GeoNumeric geoNumber(double x) {
		return new GeoNumeric(cons, x);
	}

	/**
	 * Create a new numeric Geo from an expression
	 * @param expr expression to get the value of the geo from
	 * @return new Geo with the value of expr
	 */
	public GeoNumeric geoNumber(ExpressionValue expr) {

		AlgoDependentNumber algo = new AlgoDependentNumber(cons, getNode(expr), false);
		return algo.getNumber();
	}

	/**
	 * Create a new vector Geo from an expression
	 * @param expr expression to get the coordinates of the Geo from
	 * @return new vector Geo with coordinates given by expr
	 */
	public GeoVector geoVector(ExpressionValue expr) {
		AlgoDependentVector algo = new AlgoDependentVector(cons, getNode(expr));
		return algo.getVector();
	}

	/**
	 * Create a vector Geo from its coordinates
	 * @param x the vector's x-coordinate
	 * @param y the vector's y-coordinate
	 * @return new vector Geo with coordinates (x, y)
	 */
	public GeoVector geoVector(double x, double y) {
		return kernel.Vector(x, y);
	}

	/**
	 * Create a new vector Geo
	 * @param start the vector's start point
	 * @param end the vector's end point
	 * @return new vector Geo from start to end
	 */
	public GeoVector geoVector(GeoPoint2 start, GeoPoint2 end) {
		return kernel.Vector(null, start, end);
	}

	/**
	 * Create a position vector Geo
	 * @param pos the point
	 * @return new vector Geo from O to pos
	 */
	public GeoVector geoVector(GeoPoint2 pos) {
		return kernel.Vector(null, pos);
	}

	/**
	 * @param line a Geo line
	 * @return the direction of the line
	 */
	public GeoVector geoLineDirection(GeoLine line) {
		return kernel.Direction(null, line);
	}

	/**
	 * Create a new point from an expression
	 * @param expr expression giving the coordinates of the point
	 * @return new Geo point
	 */
	public GeoPoint2 geoPoint(ExpressionValue expr) {
		AlgoDependentPoint algo = new AlgoDependentPoint(cons, getNode(expr), false);
		return algo.getPoint();
	}

	/**
	 * Create a new point from coordinates
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return new Geo point with coordinates (x, y)
	 */
	public GeoPoint2 geoPoint(double x, double y) {
		return kernel.Point(null, x, y);
	}

	/**
	 * Create a new point on a path
	 * @param path the path to point the point on
	 * @param param parameter to set the location of the point on the path
	 * @return new Geo on path
	 */
	public GeoPoint2 geoPointOnPath(Path path, NumberValue param) {
		return kernel.Point(null, path, param);
	}

	/**
	 * Create a new line through two points
	 * @param p first point on the line
	 * @param q second point on the line
	 * @return new Geo line
	 */
	public GeoLine geoLinePP(GeoPoint2 p, GeoPoint2 q) {
		return kernel.Line(null, p, q);
	}

	/**
	 * Create a new line through a point with direction given by a vector
	 * @param p point on the line
	 * @param q direction vector for the line
	 * @return new Geo line
	 */
	public GeoLine geoLinePV(GeoPoint2 p, GeoPoint2 q) {
		return kernel.Line(null, p, q);
	}

	/**
	 * Create a new line through a point parallel to another line
	 * @param p point on the line
	 * @param l line parallel to the new line
	 * @return new Geo line
	 */
	public GeoLine geoLinePL(GeoPoint2 p, GeoLine l) {
		return kernel.Line(null, p, l);
	}

	/**
	 * Create a new segment with two given end points
	 * @param p the first end point
	 * @param q the second end point
	 * @return new Geo segment
	 */
	public GeoSegment geoSegment(GeoPoint2 p, GeoPoint2 q) {
		return kernel.Segment(null, p, q);
	}

	/**
	 * Create a new ray from a point through another
	 * @param p the origin of the ray
	 * @param q a point on the ray
	 * @return new Geo ray
	 */
	public GeoRay geoRayPP(GeoPoint2 p, GeoPoint2 q) {
		return kernel.Ray(null, p, q);
	}

	/**
	 * Create a new function from an expression and a variable
	 * @param f expression that represents the function
	 * @param x expression that represents the variable
	 * @return new Geo funcion
	 */
	public GeoFunction geoFunction(ExpressionValue f, FunctionVariable x) {
		Function func = new Function(getNode(f), x);
		GeoElement[] geos = algProcessor.processFunction(null, func);
		return (GeoFunction) geos[0];
	}

	/**
	 * Create a new function with several variables
	 * @param f the expression that represents the function
	 * @param xs and array of expression that represent the variables
	 * @return new Geo function
	 */
	public GeoFunctionNVar geoFunctionNVar(ExpressionValue f, FunctionVariable[] xs) {
		FunctionNVar func = new FunctionNVar(getNode(f), xs);
		GeoElement[] geos = algProcessor.processFunctionNVar(null, func);
		return (GeoFunctionNVar) geos[0];
	}

	/**
	 * Create an implicit curve from a function
	 * @param f the function
	 * @return new Geo implicit curve representing f(x, y) = 0
	 */
	public GeoImplicitPoly geoImplicitPoly(GeoFunctionNVar f) {
		return kernel.ImplicitPoly(null, f);
	}

	/**
	 * Create a new text object
	 * @param text the text
	 * @return new Geo text
	 */
	public GeoText geoText(String text) {
		return kernel.Text(null, text);
	}

	/**
	 * Create a new conic through 5 points
	 * @param geos array of points
	 * @return the new Geo conic
	 */
	public GeoConic geoConic(GeoPoint2[] points) {
		return kernel.Conic(null, points);
	}

	/**
	 * Create a new circle with given center and point on the circumference
	 * @param center the center
	 * @param point point on the circumference
	 * @return new Geo circle
	 */
	public GeoConic geoCircleCP(GeoPoint2 center, GeoPoint2 point) {
		return kernel.Circle(null, center, point);
	}

	/**
	 * Create a new circle through three points
	 * @param p first point on the circumference
	 * @param q second point on the circumference
	 * @param r third point on the circumference
	 * @return new Geo circle
	 */
	public GeoConic geoCirclePPP(GeoPoint2 p, GeoPoint2 q, GeoPoint2 r) {
		return kernel.Circle(null, p, q, r);
	}

	/**
	 * Create a new circle with given center and radius
	 * @param c center of the circle
	 * @param s segment giving the radius of the circle
	 * @return new Geo circle
	 */
	public GeoConic geoCircleCS(GeoPoint2 c, GeoSegment s) {
		return kernel.Circle(null, c, s);
	}

	/**
	 * Create a new circle with given center and radius
	 * @param c center of the circle
	 * @param r radius of the circle
	 * @return new Geo circle
	 */
	public GeoConic geoCircleCR(GeoPoint2 c, NumberValue r) {
		return kernel.Circle(null, c, r);
	}

	/**
	 * Create a new ellipse with two given foci and going through a given point
	 * @param s1 first focus of the ellipse
	 * @param s2 second focus of the ellipse
	 * @param p point on the ellipse
	 * @return new Geo ellipse
	 */
	public GeoConic geoEllipseFFP(GeoPoint2 s1, GeoPoint2 s2, GeoPoint2 p) {
		return kernel.Ellipse(null, s1, s2, p);
	}

	/**
	 * Create a new ellipse with two given foci and a given semi-major axis length
	 * @param s1 first focus of the ellipse
	 * @param s2 second focus of the ellipse
	 * @param a length of semi-major axis
	 * @return new Geo ellipse
	 */
	public GeoConic geoEllipseFFA(GeoPoint2 s1, GeoPoint2 s2, NumberValue a) {
		return kernel.Ellipse(null, s1, s2, a);
	}


	/**
	 * Create a new hyperbola with two given foci and going through a given point
	 * @param s1 first focus of the hyperbola
	 * @param s2 second focus of the hyperbola
	 * @param p point on the ellipse
	 * @return new Geo hyperbola
	 */
	public GeoConic geoHyperbolaFFP(GeoPoint2 s1, GeoPoint2 s2, GeoPoint2 p) {
		return kernel.Hyperbola(null, s1, s2, p);
	}

	/**
	 * Create a new hyperbola with two given foci and a given semi-major axis length
	 * @param s1 first focus of the hyperbola
	 * @param s2 second focus of the hyperbola
	 * @param a length of semi-major axis
	 * @return new Geo hyperbola
	 */
	public GeoConic geoHyperbolaFFA(GeoPoint2 s1, GeoPoint2 s2, NumberValue a) {
		return kernel.Hyperbola(null, s1, s2, a);
	}

	/**
	 * Create a new parabola with a given focus and directrix
	 * @param s the focus
	 * @param l the directrix
	 * @return new Geo parabola
	 */
	public GeoConic geoParabola(GeoPoint2 s, GeoLine l) {
		return kernel.Parabola(null, s, l);
	}
	
	/* Lists */
	
	/**
	 * @param geos array of Geos to put in the list
	 * @return new geoList
	 */
	public GeoList geoList(ArrayList<GeoElement> geos) {
		return kernel.List(null, geos, true);
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
	public MyVecNode vectorExpression(ExpressionValue x, ExpressionValue y) {
		return new MyVecNode(kernel, x, y);
	}

	/**
	 * Create an expression out a double
	 * 
	 * @param x
	 *            = a double
	 * @return number expression x
	 */
	public MyDouble numberExpression(double x) {
		return  new MyDouble(kernel, x);
	}

	/**
	 * Return an expression for the x-coord of a geo
	 * 
	 * @param geo
	 *            geo to find the x-coord of
	 * @return x-coord of geo
	 */
	public ExpressionNode xCoordExpression(ExpressionValue e) {
		return new ExpressionNode(kernel, e, Operation.XCOORD, null);
	}

	/**
	 * Return an expression for the y-coord of a geo
	 * 
	 * @param geo
	 *            geo to find the y-coord of
	 * @return y-coord of geo
	 */
	public ExpressionNode yCoordExpression(ExpressionValue e) {
		return new ExpressionNode(kernel, e, Operation.YCOORD, null);
	}

	/**
	 * @param e
	 * @return
	 */
	public ExpressionNode getNode(ExpressionValue e) {
		if (e.isExpressionNode()) {
			return (ExpressionNode) e;
		}
		return new ExpressionNode(kernel, e);
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
	public ExpressionNode nodeExpression(ExpressionValue left, Operation op,
			ExpressionValue right) {
		return new ExpressionNode(kernel, left, op, right);
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
	public ExpressionNode nodeExpression(ExpressionValue arg, Operation op) {
		return new ExpressionNode(kernel, arg, op, null);
	}

	/**
	 * Create an expression containing a variable (for building functions)
	 * 
	 * @param varname
	 *            name of the variable
	 * @return a variable expression
	 */
	public FunctionVariable variableExpression(String varname) {
		return new FunctionVariable(kernel, varname);
	}

	/**
	 * @param expr
	 *            the expression to make a geo of
	 * @return a geo of the expression
	 */
	public GeoElement getGeo(ExpressionValue expr) {
		boolean flag = cons.isSuppressLabelsActive();
		ExpressionNode node = getNode(expr);
		try {
			cons.setSuppressLabelCreation(true);
			GeoElement[] geos = algProcessor.processExpressionNode(node);
			return geos[0];
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
	public GeoPointND intersectLines(GeoLineND l1, GeoLineND l2) {
		return kernel.IntersectLines(null, l1, l2);
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
	public GeoPoint2[] intersectLineConic(GeoLine l, GeoConic c) {
		return kernel.IntersectLineConic(null, l, c);
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
	public GeoPointND[] intersectConics(GeoConicND c1, GeoConicND c2) {
		return kernel.IntersectConics(null, c1, c2);
	}

	/*
	 * Selection
	 */

	/**
	 * Return all selected geoelements
	 * 
	 * @return array of selected geoelements
	 */
	public ArrayList<GeoElement> getSelectedGeos() {
		return app.getSelectedGeos();
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
	public GeoElement[] getGeos(GeoClass type) {
		TreeSet<GeoElement> geoSet = cons.getGeoSetLabelOrder(type);
		GeoElement[] geoList = new GeoElement[geoSet.size()];
		int i = 0;
		for (GeoElement geo : geoSet) {
			geoList[i++] = geo;
		}
		return geoList;
	}

	/**
	 * Return all labelled geos in alphabetical order of typestring + label
	 * 
	 * @return array of all labelled geos in the construction
	 */
	public GeoElement[] getAllGeos() {
		TreeSet<GeoElement> geoSet = cons.getGeoSetNameDescriptionOrder();
		GeoElement[] geos = new GeoElement[geoSet.size()];
		int i = 0;
		for (GeoElement geo : geoSet) {
			geos[i++] = geo;
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
	
	/**
	 * @param cmd the command to evaluate
	 * @return the geo elements created
	 * @throws Exception if it goes wrong!
	 */
	public GeoElement[] evalCommand(String cmd) throws Exception {
		return algProcessor.processAlgebraCommandNoExceptionHandling(cmd, false, false, false);
	}
}
