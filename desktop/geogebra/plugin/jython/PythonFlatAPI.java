package geogebra.plugin.jython;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.advanced.AlgoTurtle;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.algos.AlgoDependentPoint;
import geogebra.common.kernel.algos.AlgoDependentVector;
import geogebra.common.kernel.algos.AlgoDirection;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
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
import geogebra.common.kernel.geos.GeoAxis;
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
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.geos.GeoTurtle;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.plugin.Operation;
import geogebra.main.AppD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import javax.swing.JFrame;

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
	public static final Class<GeoFunctionNVar> GeoFunctionNVarClass = GeoFunctionNVar.class;
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
	@SuppressWarnings("javadoc")
	public static final Class<GeoTextField> GeoTextFieldClass = GeoTextField.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoAxis> GeoAxisClass = GeoAxis.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoPolygon> GeoPolygonClass = GeoPolygon.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoPolyLine> GeoPolyLineClass = GeoPolyLine.class;
	@SuppressWarnings("javadoc")
	public static final Class<GeoTurtle> GeoTurtleClass = GeoTurtle.class;
	
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
			return e.evaluate(StringTemplate.defaultTemplate);
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
		 * @param e 
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
		 * @param geo the geo
		 * @return the GeoClass of the wrapped Geo
		 */
		
		public static GeoClass getGeoClassType(GeoElement geo) {
			return geo.getGeoClassType();
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
		
		/**
		 * Check whether an element is infinite
		 * @param geo the geo element
		 * @return true if it is infinite
		 */
		public static boolean isInfinite(GeoElement geo) {
			return geo.isInfinite();
		}
		
		/**
		 * tells whether a GeoElement is dependent on others or not
		 * @param geo the GeoElement
		 * @return true if the GeoElement is free (does not depend on others)
		 */
		public static boolean isFree(GeoElement geo) {
			return geo.isIndependent();
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
			return geo.getLabel(StringTemplate.defaultTemplate);
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
		public static GColor getColor(GeoElement geo) {
			return geo.getObjectColor();
		}
		
		/**
		 * Set the Geo's color
		 * @param color the new color
		 */
		public static void setColor(GeoElement geo, GColor color) {
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
		public static GColor getLabelColor(GeoElement geo) {
			return geo.getLabelColor();
		}

		/**
		 * Set the color of the Geo's label
		 * @param color the new color for the label
		 */
		public static void setLabelColor(GeoElement geo, GColor color) {
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
		 * Set the label offset
		 * @param geo target GeoElement
		 * @param xcoord x offset
		 * @param ycoord y offset
		 */
		public static void setLabelOffset(GeoElement geo, int xcoord, int ycoord) {
			geo.labelOffsetX = xcoord;
			geo.labelOffsetY = ycoord;
		}
		
		/**
		 * Get the label offset
		 * @param geo target GeoElement
		 * @return the offset as an array { x-coord, y-coord }
		 */
		public static int[] getLabelOffset(GeoElement geo) {
			int[] offset = { geo.labelOffsetX, geo.labelOffsetY };
			return offset;
		}
		
		/**
		 * Get the animating state of the geo
		 * @param geo target GeoElement
		 * @return the current animating state
		 */
		public static boolean getAnimating(GeoElement geo) {
			return geo.isAnimating();
		}
		
		/**
		 * Set the animating state of the geo
		 * @param geo target GeoElement
		 * @param val the new animating state
		 */
		public static void setAnimating(GeoElement geo, boolean val) {
			geo.setAnimating(val);
		}
		
		/**
		 * @return the background color for the Geo
		 */
		public static GColor getBackgroundColor(GeoElement geo) {
			return geo.getBackgroundColor();
		}
		
		/**
		 * Set the background color for the Geo
		 * @param color the new background color for the Geo
		 */
		public static void setBackgroundColor(GeoElement geo, GColor color) {
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
		
		/**
		 * Get the layer of a Geo
		 * @param geo the geo
		 * @return the geo's layer
		 */
		public static int getLayer(GeoElement geo) {
			return geo.getLayer();
		}
		
		/**
		 * Set the layer of a geo
		 * @param geo the geo
		 * @param layer the new layer for the geo
		 */
		public static void setLayer(GeoElement geo, int layer) {
			geo.setLayer(layer);
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
		
		/* GeoPoint methods */
		
		/**
		 * Get the size of a point
		 * @param geo the point
		 * @return the size of the point
		 */
		public static int getPointSize(GeoPoint geo) {
			return geo.getPointSize();
		}
		
		/**
		 * Set the size of a point
		 * @param geo the point
		 * @param size the new size for the point
		 */
		public static void setPointSize(GeoPoint geo, int size) {
			geo.setPointSize(size);
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
		public static GeoPoint getStartPoint(GeoLine geo) {
			return geo.getStartPoint();
		}
		
		/**
		 * Set the start point of the wrapped GeoLine
		 * @param point the new start point
		 */
		public static void setStartPoint(GeoLine geo, GeoPoint point) {
			geo.setStartPoint(point);
		}
		
		/**
		 * @return the end point of the wrapped GeoLine
		 */
		public static GeoPoint getEndPoint(GeoLine geo) {
			return geo.getEndPoint();
		}
		
		/**
		 * Set the end point of the wrapped GeoLine
		 * @param point the new end point
		 */
		public static void setEndPoint(GeoLine geo, GeoPoint point) {
			geo.setEndPoint(point);
		}

		
		/* Text methods */

		
		/**
		 * @return the origin point of the wrapped GeoText
		 */
		public static GeoPointND getTextOrigin(GeoText geo) {
			return geo.getStartPoint();
		}
		
		/**
		 * Get the text string of a GeoText
		 * @param geo the GeoText
		 * @return the text value of the GeoText
		 */
		public static String getTextString(GeoText geo) {
			return geo.getTextString();
		}
		
		/**
		 * Set the text string of a GeoText
		 * @param geo the GeoText object
		 * @param text the new text string
		 */
		public static void setTextString(GeoText geo, String text) {
			geo.setTextString(text);
		}
		
		/**
		 * Find whether a GeoText is LaTeX
		 * @param geo the GeoText object
		 * @return true if the GeoText is interpreted as a LaTeX formula
		 */
		public static boolean isLatex(GeoText geo) {
			return geo.isLaTeX();
		}
		
		/**
		 * Set whether a GeoText is LaTeX
		 * @param geo the GeoText object
		 * @param val true if the GeoText is LaTeX
		 */
		public static void setLatex(GeoText geo, boolean val) {
			geo.setLaTeX(val, false);
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
		
		/* TextField methods */
		
		public static String getText(GeoTextField geo) {
			return geo.getText();
		}
		
		public static void setText(GeoTextField geo, String value) {
			geo.setText(value);
		}
		
		/* List methods */
		
		/**
		 * Return the item at a given position in a GeoList
		 * @param list the GeoList
		 * @param index the index of the item
		 * @return the item at position index
		 */
		public static GeoElement getListItem(GeoList list, int index) {
			return list.get(index);
		}
		
		/**
		 * Remove an item at a given position in a GeoList
		 * @param list the GeoList
		 * @param index the position of the item to remove
		 */
		public static void removeListItem(GeoList list, int index) {
			list.remove(index);
		}
		
		/**
		 * Add an element to the end of a GeoList
		 * @param list the GeoList
		 * @param obj the element to add
		 */
		public static void appendToList(GeoList list, GeoElement obj) {
			list.add(obj);
		}
		
		/**
		 * Return the length of a GeoList
		 * @param list the GeoList
		 * @return the length of the list
		 */
		public static int getListLength(GeoList list) {
			return list.size();
		}
		
		/**
		 * Clear a GeoList (i.e. remove all its items)
		 * @param list the GeoList to clear
		 */
		public static void clearList(GeoList list) {
			list.clear(); 
		}
		
		/* GeoPolygon */
		
		/**
		 * Return the boundary of a polygon
		 * @param poly the polygon
		 * @return the polygon's boundary
		 */
		public static Path getPolygonBoundary(GeoPoly poly) {
			return poly.getBoundary();
		}
		
		/**
		 * Return an array of points making a polygon
		 * @param poly the polygon
		 * @return an array containing all the polygon's points
		 */
		public static GeoPointND[] getPolygonPoints(GeoPoly poly) {
			return poly.getPoints();
		}
		
		/**
		 * Return the number of points in a polygon
		 * @param poly the polygon
		 * @return the polygon's number of points
		 */
		public static int getPolygonSize(GeoPolygon poly) {
			return poly.getPointsLength();
		}
		
		/**
		 * Return the directed area of a polygon
		 * @param poly the polygon
		 * @return its directed area
		 */
		public static double getPolygonDirectedArea(GeoPolygon poly) {
			return poly.getAreaWithSign();
		}
		
		/**
		 * Return the edges of a polygon
		 * @param poly the polygon
		 * @return and array with all the edges of the polygon
		 */
		public static GeoSegment[] getPolygonEdges(GeoPolygon poly) {
			GeoSegmentND[] nd_edges = poly.getSegments();
			GeoSegment[] edges = new GeoSegment[nd_edges.length];
			for (int i = 0; i < nd_edges.length; i++) {
				edges[i] = (GeoSegment) nd_edges[i];
			}
			return edges;
		}
		
		/* Functions */
		
		/**
		 * Return the number of variables of a GeoFunctionNVar
		 * @param f the function 
		 * @return the number of variables of f
		 */
		public static int getFunctionArity(GeoFunctionNVar f) {
			return f.getVarNumber();
		}
	
		/* Turtles */
		
		/**
		 * Make a turtle move forward
		 * @param t the GeoTurtle
		 * @param d the distance to move
		 */
		public static void turtleForward(GeoTurtle t, double d) {
			t.forward(d);
		}
		
		/**
		 * Make turtle turn
		 * @param t the GeoTurtle
		 * @param angle the angle to move (positive means anticlockwise)
		 */
		public static void turtleTurn(GeoTurtle t, double angle) {
			t.turn(angle);
		}
		
		/**
		 * Check is turtle pen is down
		 * @param t the GeoTurtle
		 * @return true if the turtle pen is down
		 */
		public static boolean isTurtlePenDown(GeoTurtle t) {
			return t.getPenDown();
		}
		
		/**
		 * Set the state of the turtle pen
		 * @param t the GeoTurtle
		 * @param penIsDown true to put the pen down, false to lift it
		 */
		public static void setTurtlePenDown(GeoTurtle t, boolean penIsDown) {
			t.setPenDown(penIsDown);
		}
		
		/**
		 * Get the color of the turtle pen
		 * @param t the GeoTurtle
		 * @return the color of the turtle pen
		 */
		public static GColor getTurtlePenColor(GeoTurtle t) {
			return t.getPenColor();
		}
		
		/**
		 * Set the color of the turtle pen
		 * @param t the GeoTurtle
		 * @param c the new color
		 */
		public static void setTurtlePenColor(GeoTurtle t, GColor c) {
			t.setPenColor(c);
		}
		
		/**
		 * Return the turtle pen thickness
		 * @param t the GeoTurtle
		 * @return the thickness of the turtle pen
		 */
		public static int getTurtlePenThickness(GeoTurtle t) {
			return t.getPenThickness();
		}
		
		/**
		 * Define the thickness of the turtle pen
		 * @param t the GeoTurtle
		 * @param val the new thickness
		 */
		public static void setTurtlePenThickness(GeoTurtle t, int val) {
			t.setPenThickness(val);
		}
		
		/**
		 * Get the position of the turtle
		 * @param t the GeoTurtle
		 * @return the position of the turtle
		 */
		public static GeoPointND getTurtlePosition(GeoTurtle t) {
			return t.getPosition();
		}
		
		/**
		 * Set the position of the turtle
		 * @param t the GeoTurtle
		 * @param p the new position of the turtle
		 */
		public static void setTurtlePosition(GeoTurtle t, GeoPoint p) {
			t.setPosition(p.getInhomX(), p.getInhomY());
		}
		
		/**
		 * Set the position of the turtle
		 * @param t the GeoTurtle
		 * @param x new x-coordinate
		 * @param y new y-coordinate
		 */
		public static void setTurtlePosition(GeoTurtle t, double x, double y) {
			t.setPosition(x, y);
		}
		
		/**
		 * Get the turning angle of the turtle
		 * @param t the GeoTurtle
		 * @return the turning angle of the turtle
		 */
		public static double getTurtleAngle(GeoTurtle t) {
			return t.getTurnAngle();
		}
		
		/**
		 * Set the turning angle of the turtle
		 * @param t the GeoTurtle
		 * @param a the new angle
		 */
		public static void setTurtleAngle(GeoTurtle t, double a) {
			t.setTurnAngle(a);
		}
		
		/**
		 * Clear a turtle (i.e reset position and angle and erase path)
		 * @param t the GeoTurtle
		 */
		public static void clearTurtle(GeoTurtle t) {
			t.clear();
		}
		
		public static double getTurtleSpeed(GeoTurtle t) {
			return t.getSpeed();
		}
		
		public static void setTurtleSpeed(GeoTurtle t, double speed) {
			t.setSpeed(speed);
		}
		
		public static void rewindTurtle(GeoTurtle t) {
			t.resetProgress();
		}
		
		public static String[] getTurtleHistory(GeoTurtle t) {
			String[] history = new String[t.getTurtleCommandList().size()];
			int i=0;
			for (GeoTurtle.Command cmd : t.getTurtleCommandList()) {
				history[i++] = cmd.toString();
			}
			return history;
		}
		
		public static void stepTurtle(GeoTurtle t) {
			t.stepTurtle();
		}
		
		public static void stepTurtle(GeoTurtle t, double nSteps) {
			t.stepTurtle(nSteps);
		}
	}
	
	public AppD app;
	public Kernel kernel;
	public Construction cons;
	public AlgebraProcessor algProcessor;

	/**
	 * Create a new PythonAPI instance
	 * 
	 * @param app
	 *            the running application instance
	 */
	public PythonFlatAPI(AppD app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.algProcessor = kernel.getAlgebraProcessor();
	}
	
	/**
	 * @return the JFrame for the api's app
	 */
	public JFrame getAppFrame() {
		return app.getFrame();
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
	public GeoVector geoVector(GeoPoint start, GeoPoint end) {
		return kernel.Vector(null, start, end);
	}

	/**
	 * Create a position vector Geo
	 * @param pos the point
	 * @return new vector Geo from O to pos
	 */
	public GeoVector geoVector(GeoPoint pos) {
		return kernel.Vector(null, pos);
	}

	/**
	 * @param line a Geo line
	 * @return the direction of the line
	 */
	public GeoVector geoLineDirection(GeoLine line) {
		AlgoDirection algo = new AlgoDirection(cons, null, line);
		GeoVector v = algo.getVector();
		return v;
	}
	
	/**
	 * Create a new point from an expression
	 * @param expr expression giving the coordinates of the point
	 * @return new Geo point
	 */
	public GeoPoint geoPoint(ExpressionValue expr) {
		AlgoDependentPoint algo = new AlgoDependentPoint(cons, getNode(expr), false);
		return algo.getPoint();
	}

	/**
	 * Create a new point from coordinates
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return new Geo point with coordinates (x, y)
	 */
	public GeoPoint geoPoint(double x, double y) {
		return Point(null, x, y);
	}
	
	/** Point label with cartesian coordinates (x,y) */
	final private GeoPoint Point(String label, double x, double y) {
		GeoPoint p = new GeoPoint(cons);
		p.setCoords(x, y, 1.0);
		p.setMode(Kernel.COORD_CARTESIAN);
		p.setLabel(label); // invokes add()
		return p;
	}

	/**
	 * Create a new point on a path
	 * @param path the path to point the point on
	 * @param param parameter to set the location of the point on the path
	 * @return new Geo on path
	 */
	public GeoPoint geoPointOnPath(Path path, NumberValue param) {
		return kernel.Point(null, path, param);
	}

	/**
	 * Create a new line through two points
	 * @param p first point on the line
	 * @param q second point on the line
	 * @return new Geo line
	 */
	public GeoLine geoLinePP(GeoPoint p, GeoPoint q) {
		return kernel.Line(null, p, q);
	}

	/**
	 * Create a new line through a point with direction given by a vector
	 * @param p point on the line
	 * @param q direction vector for the line
	 * @return new Geo line
	 */
	public GeoLine geoLinePV(GeoPoint p, GeoVector q) {
		return kernel.Line(null, p, q);
	}

	/**
	 * Create a new line through a point parallel to another line
	 * @param p point on the line
	 * @param l line parallel to the new line
	 * @return new Geo line
	 */
	public GeoLine geoLinePL(GeoPoint p, GeoLine l) {
		return kernel.Line(null, p, l);
	}

	/**
	 * Create a new segment with two given end points
	 * @param p the first end point
	 * @param q the second end point
	 * @return new Geo segment
	 */
	public GeoSegment geoSegment(GeoPoint p, GeoPoint q) {
		return kernel.Segment(null, p, q);
	}

	/**
	 * Create a new ray from a point through another
	 * @param p the origin of the ray
	 * @param q a point on the ray
	 * @return new Geo ray
	 */
	public GeoRay geoRayPP(GeoPoint p, GeoPoint q) {
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
		GeoElement[] geos = algProcessor.processFunction(func);
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
		GeoElement[] geos = algProcessor.processFunctionNVar(func);
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
		return kernel.getAlgebraProcessor().Text(null, text);
	}

	/**
	 * Create a new conic through 5 points
	 * @param geos array of points
	 * @return the new Geo conic
	 */
	public GeoConic geoConic(GeoPoint[] points) {
		return kernel.Conic(null, points);
	}

	/**
	 * Create a new circle with given center and point on the circumference
	 * @param center the center
	 * @param point point on the circumference
	 * @return new Geo circle
	 */
	public GeoConic geoCircleCP(GeoPoint center, GeoPoint point) {
		return kernel.Circle(null, center, point);
	}

	/**
	 * Create a new circle through three points
	 * @param p first point on the circumference
	 * @param q second point on the circumference
	 * @param r third point on the circumference
	 * @return new Geo circle
	 */
	public GeoConic geoCirclePPP(GeoPoint p, GeoPoint q, GeoPoint r) {
		return kernel.Circle(null, p, q, r);
	}

	/**
	 * Create a new circle with given center and radius
	 * @param c center of the circle
	 * @param s segment giving the radius of the circle
	 * @return new Geo circle
	 */
	public GeoConic geoCircleCS(GeoPoint c, GeoSegment s) {
		return kernel.Circle(null, c, s);
	}

	/**
	 * Create a new circle with given center and radius
	 * @param c center of the circle
	 * @param r radius of the circle
	 * @return new Geo circle
	 */
	public GeoConic geoCircleCR(GeoPoint c, NumberValue r) {
		return kernel.Circle(null, c, r);
	}

	/**
	 * Create a new ellipse with two given foci and going through a given point
	 * @param s1 first focus of the ellipse
	 * @param s2 second focus of the ellipse
	 * @param p point on the ellipse
	 * @return new Geo ellipse
	 */
	public GeoConic geoEllipseFFP(GeoPoint s1, GeoPoint s2, GeoPoint p) {
		return kernel.Ellipse(null, s1, s2, p);
	}

	/**
	 * Create a new ellipse with two given foci and a given semi-major axis length
	 * @param s1 first focus of the ellipse
	 * @param s2 second focus of the ellipse
	 * @param a length of semi-major axis
	 * @return new Geo ellipse
	 */
	public GeoConic geoEllipseFFA(GeoPoint s1, GeoPoint s2, NumberValue a) {
		return kernel.Ellipse(null, s1, s2, a);
	}


	/**
	 * Create a new hyperbola with two given foci and going through a given point
	 * @param s1 first focus of the hyperbola
	 * @param s2 second focus of the hyperbola
	 * @param p point on the ellipse
	 * @return new Geo hyperbola
	 */
	public GeoConic geoHyperbolaFFP(GeoPoint s1, GeoPoint s2, GeoPoint p) {
		return kernel.Hyperbola(null, s1, s2, p);
	}

	/**
	 * Create a new hyperbola with two given foci and a given semi-major axis length
	 * @param s1 first focus of the hyperbola
	 * @param s2 second focus of the hyperbola
	 * @param a length of semi-major axis
	 * @return new Geo hyperbola
	 */
	public GeoConic geoHyperbolaFFA(GeoPoint s1, GeoPoint s2, NumberValue a) {
		return kernel.Hyperbola(null, s1, s2, a);
	}

	/**
	 * Create a new parabola with a given focus and directrix
	 * @param s the focus
	 * @param l the directrix
	 * @return new Geo parabola
	 */
	public GeoConic geoParabola(GeoPoint s, GeoLine l) {
		return kernel.Parabola(null, s, l);
	}
	
	/* Lists */
	
	/**
	 * @param geos array of Geos to put in the list
	 * @return new geoList
	 */
	public GeoList geoList(GeoElement[] geos) {
		ArrayList<GeoElement> geoslist = new ArrayList<GeoElement>(Arrays.asList(geos));
		return kernel.List(null, geoslist, true);
	}
	
	/* Turtles */
	
	/**
	 * Create a new GeoTurtle
	 * @return the newly created GeoTurtle
	 */
	public GeoTurtle geoTurtle() {
		AlgoTurtle algo = new AlgoTurtle(cons, null);
		GeoTurtle geo = algo.getTurtle();
		geo.setEuclidianVisible(true);
		return geo;
	}
	
	/* Polygons */
	
	/**
	 * Create new polygon
	 * @param points the vertices of the polygon
	 * @return new polygon
	 */
	public GeoPolygon geoPolygon(GeoPointND[] points) {
		return (GeoPolygon) kernel.Polygon(null, points)[0];
	}
	
	/**
	 * Create new polyline
	 * @param points the vertices of the polyline
	 * @return new polyline
	 */
	public GeoPolyLine geoPolyLine(GeoPointND[] points) {
		return (GeoPolyLine) kernel.PolyLine(null, points, false)[0];
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
	public GeoPoint[] intersectLineConic(GeoLine l, GeoConic c) {
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
	 * Axes
	 */
	
	/**
	 * Set an axis's visibility in the Euclidian View 1
	 * @param axis the axis object
	 * @param visible true to make it visible
	 */
	public void setAxisVisible(GeoAxis axis, boolean visible) {
		app.getEuclidianView1().setShowAxis(axis.getType(), visible, false);
	}
	
	/**
	 * Get an axis's visibility in the Euclidian View 2
	 * @param axis the axis object
	 * @return true if the axis is visible
	 */
	public boolean isAxisVisible(GeoAxis axis) {
		return app.getEuclidianView1().getShowAxis(axis.getType());
	}
	
	/**
	 * tell kernel to tell views to repaint
	 */
	public void refreshViews() {
		app.refreshViews();
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
	
	/**
	 * Evaluate a command
	 * @param cmdname the name of the command
	 * @param args the commands arguments as ExpressionValue's
	 * @return the geo elements created
	 */
	public GeoElement[] evalCommand(String cmdname, ExpressionValue[] args) {
		Command cmd = new Command(kernel, cmdname, true);
		for (int i = 0; i < args.length; i++) {
			cmd.addArgument(getNode(args[i]));
		}
		return algProcessor.processCommand(cmd, true);
	}
	
	/**
	 * Tell the application to update its menu bar
	 */
	public void updateMenubar() {
		app.updateMenubar();
	}
}
