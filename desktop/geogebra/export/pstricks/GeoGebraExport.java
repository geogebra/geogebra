package geogebra.export.pstricks;

import geogebra.awt.GGraphics2DD;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.euclidian.draw.DrawInequality;
import geogebra.common.euclidian.draw.DrawLine;
import geogebra.common.euclidian.draw.DrawPoint;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.common.kernel.algos.AlgoBoxPlot;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import geogebra.common.kernel.algos.AlgoIntegralFunctions;
import geogebra.common.kernel.algos.AlgoSlope;
import geogebra.common.kernel.algos.AlgoSumLeft;
import geogebra.common.kernel.algos.AlgoSumLower;
import geogebra.common.kernel.algos.AlgoSumRectangle;
import geogebra.common.kernel.algos.AlgoSumTrapezoidal;
import geogebra.common.kernel.algos.AlgoSumUpper;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.IneqTree;
import geogebra.common.kernel.arithmetic.Inequality;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.statistics.AlgoHistogram;
import geogebra.common.util.MyMath;
import geogebra.common.util.StringUtil;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.export.epsgraphics.ColorMode;
import geogebra.export.epsgraphics.EpsGraphics;
import geogebra.main.AppD;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;


/*
 import org.mozilla.javascript.Context;
 import org.mozilla.javascript.Scriptable;
 import org.mozilla.javascript.ScriptableObject;
 */
public abstract class GeoGebraExport  {
	protected int beamerSlideNumber = 1;
	protected final double PRECISION_XRANGE_FUNCTION = 0.00001;
	protected StringBuilder code, codePoint, codePreamble, codeFilledObject,
			codeBeginDoc;
	protected AppD app;
	protected Kernel kernel;
	protected Construction construction;
	protected EuclidianViewND euclidianView;
	protected ExportFrame frame;
	protected HashMap<geogebra.common.awt.GColor, String> CustomColor;
	protected double xunit, yunit, xmin, xmax, ymin, ymax;
	// The exported format: Latex, tex, ConTexT, Beamer
	protected int format = 0;
	protected boolean isBeamer = false;

	public GeoGebraExport(AppD app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.construction = kernel.getConstruction();
		this.euclidianView = app.getActiveEuclidianView();
		initGui();
	}

	public AppD getApp() {
		return app;
	}
	
	protected String format(double d){
		return kernel.format(d, getStringTemplate());
	}

	// Functions added to access and modify xmin, xmax, ymin and ymax
	// When xmin,xmax,ymin or ymax are changed
	// the selected area is reported accodingly on the euclidianView.
	// This is not visible, on the view, but one may expect that when
	// the selection rectangle is changed it is displayed on the view.
	// This may be implemented by changing the class EuclidianView.
	// Furthermore the definition of a class EuclidianView listerner
	// which this class would implement would be desirable so that
	// when the selection is modified by the mouse, this is reported
	// to the values xmin, xmax, ymin and ymax of instances of this class.
	// refresh the selection rectangle when values change in TextField
	public void refreshSelectionRectangle() {
		int x1 = euclidianView.toScreenCoordX(xmin);
		int x2 = euclidianView.toScreenCoordX(xmax);
		int y1 = euclidianView.toScreenCoordY(ymin);
		int y2 = euclidianView.toScreenCoordY(ymax);
		geogebra.common.awt.GRectangle rec = geogebra.common.factories.AwtFactory.prototype
				.newRectangle(x1, y2, x2 - x1, y1 - y2);
		// Application.debug(x1+" "+x2+" "+y1+" "+y2);
		euclidianView.setSelectionRectangle(rec);
		euclidianView.repaint();
	}

	protected void setxmin(double xmin) {
		this.xmin = xmin;
		this.refreshSelectionRectangle();
	}

	protected void setxmax(double xmax) {
		this.xmax = xmax;
		this.refreshSelectionRectangle();
	}

	protected void setymin(double ymin) {
		this.ymin = ymin;
		this.refreshSelectionRectangle();
	}

	protected void setymax(double ymax) {
		this.ymax = ymax;
		this.refreshSelectionRectangle();
	}

	protected double getxmin() {
		return this.xmin;
	}

	protected double getxmax() {
		return this.xmax;
	}

	protected double getymin() {
		return this.ymin;
	}

	protected double getymax() {
		return this.ymax;
	}

	/**
	 * Initialize Gui JFrame
	 */
	private void initGui() {
		xunit = 1;
		yunit = 1;
		// Changes to make xmin,xmax,ymin,ymax be defined by the selection
		// rectangle
		// when this one is defined.
		geogebra.common.awt.GRectangle rect = this.euclidianView.getSelectionRectangle();
		if (rect != null) {
			xmin = euclidianView.toRealWorldCoordX(rect.getMinX());
			xmax = euclidianView.toRealWorldCoordX(rect.getMaxX());
			ymin = euclidianView.toRealWorldCoordY(rect.getMaxY());
			ymax = euclidianView.toRealWorldCoordY(rect.getMinY());
		} else {
			xmin = euclidianView.getXmin();
			xmax = euclidianView.getXmax();
			ymin = euclidianView.getYmin();
			ymax = euclidianView.getYmax();
		}
		createFrame();
	}

	/**
	 * When The Button "generate Code" has been clicked
	 */
	public void setBeamer(boolean beamer) {
		isBeamer = beamer;
	}

	/**
	 * This method converts a double with engineering notation to decimal<br>
	 * Example: 3E-4 becomes 0.0003
	 * 
	 * @param d
	 *            The double to translate
	 * @return The resulting String
	 */
	protected String sci2dec(double d) {
		String s = StringUtil.toLowerCase(String.valueOf(d));
		StringTokenizer st = new StringTokenizer(s, "e");
		StringBuilder number;
		if (st.countTokens() == 1)
			return s;
		String token1 = st.nextToken();
		String token2 = st.nextToken();
		number = new StringBuilder(token1);
		int exp = Integer.parseInt(token2);
		if (exp > 0) {
			int id_point = number.indexOf(".");
			if (id_point == -1) {
				for (int i = 0; i < exp; i++)
					number.append("0");
			} else {
				number.deleteCharAt(id_point);
				int zeros = exp - (number.length() - id_point);
				for (int i = 0; i < zeros; i++)
					number.append("0");
			}
		} else {
			exp = -exp;
			int id_point = number.indexOf(".");
			number.deleteCharAt(id_point);
			for (int i = 0; i < exp - 1; i++)
				number.insert(0, "0");
			number.insert(0, "0.");
		}
		return number.toString();
	}

	/**
	 * This method creates the name for all custom colors
	 * 
	 * @param red
	 *            The red color part
	 * @param green
	 *            The green color part
	 * @param blue
	 *            The blue color part
	 * @return The name for the color uses hexadecimal decomposition
	 */

	String createCustomColor(int red, int green, int blue) {
		final String suff = "qrstuvwxyzabcdef";
		int[] nb = { red, green, blue };
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nb.length; i++) {
			int quot = nb[i] / 16;
			int reste = nb[i] % 16;
			sb.append(suff.charAt(quot));
			sb.append(suff.charAt(reste));
		}
		return new String(sb);
	}

	/**
	 * This method is generic method to generate code according to GeoElement
	 * nature
	 * 
	 * @param g
	 *            GeoElement g
	 * @param fromGeoList
	 *            if GeoElement comes from a GeoList object
	 * @param trimmedInter
	 */
	protected void drawGeoElement(GeoElement g, boolean fromGeoList,
			boolean trimmedInter) {
		if (g.isGeoList()) {
			GeoList geo = ((GeoList) g);
			for (int i = 0; i < geo.size(); i++) {
				drawGeoElement(geo.get(i), true, false);
			}
		} else if (g.isEuclidianVisible() || trimmedInter) {
			if (g.isGeoPoint()) {
				drawGeoPoint((GeoPoint) g);
				drawLabel(g, null);
			} else if (g.isGeoSegment()) {
				drawGeoSegment((GeoSegment) g);
				drawLabel(g, null);
			} else if (g.isGeoRay()) {
				drawGeoRay((GeoRay) g);
				drawLabel(g, null);
			} else if (g instanceof GeoPolyLine) {
				drawPolyLine((GeoPolyLine) g);
			} else if (g.isGeoLine()) {
				drawGeoLine((GeoLine) g);
				drawLabel(g, null);
			} else if (g.isGeoPolygon()) {
				drawPolygon((GeoPolygon) g);
				drawLabel(g, null);
			} else if (g.isGeoAngle()) {
				if (g.isIndependent()) {
					// independent number may be shown as slider
					drawSlider((GeoNumeric) g);
				} else {
					drawAngle((GeoAngle) g);
					// String
					// label="$"+Util.toLaTeXString(g.getLabelDescription(),true)+"$";
					drawLabel(g, euclidianView.getDrawableFor(g));
				}
			} else if (g.isGeoImplicitPoly()) {
				drawImplicitPoly((GeoImplicitPoly) g);
			}
			// To draw Inequalities
			else if (g.getTypeString().equals("Inequality")) {
				if(g.isGeoFunctionBoolean()){
					drawGeoInequalities(null,g);
				}else{
					drawGeoInequalities((GeoFunctionNVar) g,null);
				}
			}

			else if (g.isGeoNumeric()) {
				AlgoElement algo = g.getParentAlgorithm();
				if (algo == null) {
					// indpendent number may be shown as slider
					drawSlider((GeoNumeric) g);
				} else if (algo instanceof AlgoSlope) {
					drawSlope((GeoNumeric) g);
					drawLabel(g, null);
				} else if (algo instanceof AlgoIntegralDefinite) {
					drawIntegral((GeoNumeric) g);
					drawLabel(g, null);
				} else if (algo instanceof AlgoIntegralFunctions) {
					drawIntegralFunctions((GeoNumeric) g);
					drawLabel(g, null);
				}
				// BoxPlot
				else if (algo instanceof AlgoBoxPlot) {
					drawBoxPlot((GeoNumeric) g);
				} else if (algo instanceof AlgoFunctionAreaSums) {
					// Trapezoidal Sum
					if (algo instanceof AlgoSumTrapezoidal)
						drawSumTrapezoidal((GeoNumeric) g);

					// Histogram
					else if (algo instanceof AlgoHistogram)
						drawHistogram((GeoNumeric) g);
					// Bar Chart
					else if (algo instanceof AlgoBarChart)
						drawHistogram((GeoNumeric) g);
					// Lower or Upper Sum, Left Sum or Rectangle Sum
					else if (algo instanceof AlgoSumUpper
							|| algo instanceof AlgoSumLower
							|| algo instanceof AlgoSumLeft
							|| algo instanceof AlgoSumRectangle)
						drawSumUpperLower((GeoNumeric) g);
					drawLabel(g, null);
				}
			} else if (g.isGeoVector()) {
				drawGeoVector((GeoVector) g);
				drawLabel(g, null);
			} else if (g.isGeoConicPart()) {
				GeoConicPart geo = (GeoConicPart) g;
				drawGeoConicPart(geo);
				if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_ARC
						|| geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR)
					drawLabel(g, null);
			} else if (g.isGeoConic()) {
				if (isSinglePointConic(g)) {
					GeoConic geo = (GeoConic) g;
					GeoPoint point = geo.getSinglePoint();
					point.copyLabel(geo);
					point.setObjColor(geo.getObjectColor());
					point.setLabelColor(geo.getLabelColor());
					point.setPointSize(geo.lineThickness);
					point.setLabelOffset(geo.labelOffsetX, geo.labelOffsetY);
					DrawPoint drawPoint = new DrawPoint(euclidianView, point);
					drawPoint.setGeoElement(geo);
					drawGeoPoint(point);
					drawLabel(point, drawPoint);
				} else if (isDoubleLineConic(g)) {
					GeoConic geo = (GeoConic) g;
					GeoLine[] lines = geo.getLines();
					DrawLine[] drawLines = new DrawLine[2];
					for (int i = 0; i < 2; i++) {
						lines[i].copyLabel(geo);
						lines[i].setObjColor(geo.getObjectColor());
						lines[i].setLabelColor(geo.getLabelColor());
						lines[i].lineThickness = geo.lineThickness;
						lines[i].lineType = geo.lineType;
					}
					drawLines[0] = new DrawLine(euclidianView, lines[0]);
					drawLines[1] = new DrawLine(euclidianView, lines[1]);
					drawLines[0].setGeoElement(geo);
					drawLines[1].setGeoElement(geo);
					drawGeoLine(lines[0]);
					drawGeoLine(lines[1]);
					drawLabel(lines[0], drawLines[0]);
					drawLabel(lines[1], drawLines[1]);
				} else if (isEmpty(g)) {
					//
				} else {
					drawGeoConic((GeoConic) g);
					drawLabel(g, null);
				}
			} else if (g.isGeoFunction()) {
				drawFunction((GeoFunction) g);
				drawLabel(g, null);
			} else if (g.isGeoCurveCartesian()) {
				drawCurveCartesian((GeoCurveCartesian) g);
				drawLabel(g, null);
			} else if (g.isGeoText()) {
				drawText((GeoText) g);
			} else if (g.isGeoImage()) {
				// Image --> export to eps is better and easier!
			} else if (g.isGeoLocus()) {
				drawLocus((GeoLocus) g);
			}
		}

	}

	protected boolean isSinglePointConic(GeoElement geo) {
		if (geo.isGeoConic()) {
			if (((GeoConic) geo).getType() == GeoConicNDConstants.CONIC_SINGLE_POINT)
				return true;
		}
		return false;
	}

	protected boolean isDoubleLineConic(GeoElement geo) {
		if (geo.isGeoConic()) {
			if (((GeoConic) geo).getType() == GeoConicNDConstants.CONIC_DOUBLE_LINE
					|| ((GeoConic) geo).getType() == GeoConicNDConstants.CONIC_INTERSECTING_LINES
					|| ((GeoConic) geo).getType() == GeoConicNDConstants.CONIC_PARALLEL_LINES)
				return true;
		}
		return false;
	}

	protected boolean isEmpty(GeoElement geo) {
		if (geo.isGeoConic()) {
			if (((GeoConic) geo).getType() == GeoConicNDConstants.CONIC_EMPTY)
				return true;
		}
		return false;
	}

	protected int resizePt(int size) {
		double height_geogebra = euclidianView.getHeight() / 30;
		double height_latex = frame.getLatexHeight();
		double ratio = height_latex / height_geogebra;
		int tmp = (int) Math.round(ratio * size);
		if (tmp != 0)
			return tmp;
		return 1;
	}

	/**
	 * Export as PSTricks or PGF/TikZ GeoPoint
	 * 
	 * @param geo
	 *            The point to export
	 */

	abstract protected void drawGeoPoint(GeoPoint geo);

	/**
	 * Export as PSTricks or PGF/TikZ GeoLine line[A,B]
	 * 
	 * @param geo
	 *            The line to export
	 */

	abstract protected void drawGeoLine(GeoLine geo);

	/**
	 * Export as PSTricks or PGF/TikZ GeoRay ray[A,B]
	 * 
	 * @param geo
	 *            The ray to export
	 */
	abstract protected void drawGeoRay(GeoRay geo);

	/**
	 * Export as PSTricks or PGF/TikZ GeoSegment segment[A,B]
	 * 
	 * @param geo
	 *            The segment to export
	 */
	abstract protected void drawGeoSegment(GeoSegment geo);

	/**
	 * Export as PSTricks or PGF/TikZ Objects created by command: polygon[A,B,C]
	 * 
	 * @param geo
	 *            The polygon to export
	 */

	abstract protected void drawPolygon(GeoPolygon geo);

	/**
	 * Export as PSTricks or PGF/TikZ Objects created by command:
	 * slider[1,2,0.1]
	 * 
	 * @param geo
	 *            The slider to export
	 */

	abstract protected void drawSlider(GeoNumeric geo);

	abstract protected void drawSlope(GeoNumeric geo);

	/**
	 * Export as PSTricks or PGF/TikZ Objects created by command:
	 * integral(f(x),a,b)
	 * 
	 * @param geo
	 *            The object to export
	 */
	abstract protected void drawIntegral(GeoNumeric geo);

	/**
	 * Export as PSTricks or PGF/TikZ Objects created by command:
	 * integral(f(x),g(x),a,b)
	 * 
	 * @param geo
	 *            The object to export
	 */

	abstract protected void drawIntegralFunctions(GeoNumeric geo);

	/**
	 * Export as PSTricks or PGF/TikZ Objects created by following commands
	 * leftSum, rectangleSum, lowerSum upperSum
	 * 
	 * @param geo
	 *            The object to export
	 */
	abstract protected void drawSumUpperLower(GeoNumeric geo);

	/**
	 * Export as PSTricks or PGF/TikZ Objects created by command trapezoidalsum
	 * 
	 * @param geo
	 *            The object to export
	 */
	abstract protected void drawSumTrapezoidal(GeoNumeric geo);

	/**
	 * Export as PSTricks or PGF/TikZ Objects created by command: BoxPlot[0, 1,
	 * {2,2,3,4,5,5,6,7,7,8,8,8,9}]
	 * 
	 * @param geo
	 *            The boxplot to export
	 */

	abstract protected void drawBoxPlot(GeoNumeric geo);

	/**
	 * Export as PSTricks or PGF/TikZ Objects created by command: Histogram[{0,
	 * 1, 2, 3, 4, 5}, {2, 6, 8, 3, 1}]
	 * 
	 * @param geo
	 *            The histogram to export
	 */
	abstract protected void drawHistogram(GeoNumeric geo);

	/**
	 * Export as PSTricks or PGF/TikZ GeoAngle objects angle[A,B,C]
	 * 
	 * @param geo
	 *            The angle to export
	 */
	abstract protected void drawAngle(GeoAngle geo);

	/**
	 * Export as PSTricks or PGF/TikZ GeoVector objects vector[A,B]
	 * 
	 * @param geo
	 *            The vector to export
	 */

	abstract protected void drawGeoVector(GeoVector geo);

	/**
	 * Export as PSTricks or PGF/TikZ GeoConic objects conic[A,B,C,D,E]
	 * 
	 * @param geo
	 *            The conic to export
	 */

	abstract protected void drawGeoConic(GeoConic geo);

	/**
	 * Export as PSTricks or PGF/TikZ GeoConicPart objects (sectors...)
	 * 
	 * @param geo
	 *            The conic part to export
	 */
	abstract protected void drawGeoConicPart(GeoConicPart geo);

	/**
	 * Export as PSTricks or PGF/TikZ the object's label
	 * 
	 * @param geo
	 *            The label to export
	 * @param drawGeo
	 *            Drawable object attached to geo
	 */
	abstract protected void drawLabel(GeoElement geo, DrawableND drawGeo);

	/**
	 * Export as PSTricks or PGF/TikZ the GeoFunction object
	 * 
	 * @param geo
	 *            The function to export
	 */
	abstract protected void drawFunction(GeoFunction geo);

	/**
	 * Export as PSTricks or PGF/TikZ implicit functions
	 * 
	 * @param geo
	 *            The function to export
	 */
	abstract protected void drawImplicitPoly(GeoImplicitPoly geo);

	/**
	 * Export as PSTricks or PGF/TikZ parametric functions
	 * 
	 * @param geo
	 *            The function to export
	 */
	abstract protected void drawCurveCartesian(GeoCurveCartesian geo);

	/**
	 * Export as PSTricks or PGF/TikZ Text on euclidian view
	 * 
	 * @param geo
	 *            The text to export
	 */

	abstract protected void drawText(GeoText geo);

	/**
	 * Export as PSTricks or PGF/TikZ locus
	 * 
	 * @param geo
	 *            The locus to export
	 */

	abstract protected void drawLocus(GeoLocus geo);

	/**
	 * Exports as PStricks or PGF a line defined by all its parameter
	 * 
	 * @param x1
	 *            x start
	 * @param y1
	 *            y start
	 * @param x2
	 *            x end
	 * @param y2
	 *            y end
	 * @param geo
	 *            geo Object
	 */

	abstract protected void drawLine(double x1, double y1, double x2,
			double y2, GeoElement geo);

	/**
	 * Export as PStricks or PGF arc mark for angle
	 * 
	 * @param geo
	 *            geo Object
	 * @param vertex
	 *            center
	 * @param angSt
	 *            angle start
	 * @param angEnd
	 *            angle end
	 * @param r
	 *            radius
	 */
	abstract protected void drawArc(GeoAngle geo, double[] vertex,
			double angSt, double angEnd, double r);

	/**
	 * Export as PStricks or PGF segment mark
	 * 
	 * @param geo
	 *            geo Object
	 * @param vertex
	 * @param angleTick
	 *            angle
	 */
	abstract protected void drawTick(GeoAngle geo, double[] vertex,
			double angleTick);

	abstract protected void drawArrowArc(GeoAngle geo, double[] vertex,
			double angSt, double angEnd, double r, boolean clockwise);

	abstract protected void createFrame();

	abstract protected void generateAllCode();

	/**
	 * Export as PSTricks or PGF/TikZ color's code
	 * 
	 * @param color
	 *            The color
	 * @param sb
	 *            The StringBuilder to complete
	 */

	abstract protected void ColorCode(geogebra.common.awt.GColor color, StringBuilder sb);

	/**
	 * Export as PSTricks or PGF/TikZ PolyLine objects polyline[A,B,C,D,E]
	 * 
	 * @param geo
	 *            The Polyline objects
	 */
	abstract protected void drawPolyLine(GeoPolyLine geo);

	/**
	 * Export inequalities as PSTricks or PGF or Asymptote
	 * 
	 * @param geo
	 *            The inequality function
	 * @param e 
	 * 			  If is inequality with one variable eg. x>3
	 */
	
	protected void drawGeoInequalities(GeoFunctionNVar geo, GeoElement e){
		FunctionalNVar ef = null;
		if (geo == null) {
			ef = (FunctionalNVar) e;
		} else {
			ef = geo;
		}
		DrawInequality drawable = new DrawInequality(euclidianView, ef);
		MyGraphics g = null;
		IneqTree tree = ef.getFunction().getIneqs();
		try {
			if (tree.getLeft() != null) {
				for (int i = 0; i < tree.getLeft().getSize(); i++) {
					g = createGraphics(ef, tree.getLeft().get(i), euclidianView);
					drawable.draw(g);
				}
			}
			if (tree.getRight() != null) {
				for (int i = 0; i < tree.getLeft().getSize(); i++) {
					g = createGraphics(ef, tree.getRight().get(i),
							euclidianView);
					drawable.draw(g);
				}
			}
			if (tree.getIneq() != null) {
				g = createGraphics(ef, tree.getIneq(), euclidianView);
				drawable.draw(g);
			}
			//Only for syntax. Never throws
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	//Create the appropriate instance of MyGraphics of various implementations (pstricks,pgf,asymptote)
	abstract protected MyGraphics createGraphics(FunctionalNVar ef, Inequality inequality,
			EuclidianViewND euclidianView2) throws IOException;

	/**
	 * @return the xmin
	 */
	protected double getXmin() {
		return xmin;
	}

	/**
	 * @param xmin
	 *            the xmin to set
	 */
	protected void setXmin(double xmin) {
		this.xmin = xmin;
	}

	/**
	 * @return the xmax
	 */
	protected double getXmax() {
		return xmax;
	}

	/**
	 * @param xmax
	 *            the xmax to set
	 */
	protected void setXmax(double xmax) {
		this.xmax = xmax;
	}

	/**
	 * @return the ymin
	 */
	protected double getYmin() {
		return ymin;
	}

	/**
	 * @param ymin
	 *            the ymin to set
	 */
	protected void setYmin(double ymin) {
		this.ymin = ymin;
	}

	/**
	 * @return the ymax
	 */
	protected double getYmax() {
		return ymax;
	}

	/**
	 * @param ymax
	 *            the ymax to set
	 */
	protected void setYmax(double ymax) {
		this.ymax = ymax;
	}

	/**
	 * @return the xunit
	 */
	protected double getXunit() {
		return xunit;
	}

	/**
	 * @param xunit
	 *            the xunit to set
	 */
	protected void setXunit(double xunit) {
		this.xunit = xunit;
	}

	/**
	 * @return the yunit
	 */
	protected double getYunit() {
		return yunit;
	}

	/**
	 * @param yunit
	 *            the yunit to set
	 */
	protected void setYunit(double yunit) {
		this.yunit = yunit;
	}

	/**
	 * This method draws decoration on segment using abstract method drawLine
	 * 
	 * @param A
	 *            First Point
	 * @param B
	 *            Second Point
	 * @param deco
	 *            Decoration type
	 * @param geo
	 *            GeoElement
	 */

	protected void mark(double[] A, double[] B, int deco, GeoElement geo) {
		// calc midpoint (midX, midY) and perpendicular vector (nx, ny)
		euclidianView.toScreenCoords(A);
		euclidianView.toScreenCoords(B);
		double midX = (A[0] + B[0]) / 2.0;
		double midY = (A[1] + B[1]) / 2.0;
		double nx = A[1] - B[1];
		double ny = B[0] - A[0];
		double nLength = MyMath.length(nx, ny);
		// tick spacing and length.
		double tickSpacing = 2.5 + geo.lineThickness / 2d;
		double tickLength = tickSpacing + 1;
		// Michael Borcherds 20071006 start
		double arrowlength = 1.5;
		// Michael Borcherds 20071006 end
		double vx, vy, factor, x1, x2, y1, y2;
		switch (deco) {
		case GeoElement.DECORATION_SEGMENT_ONE_TICK:
			factor = tickLength / nLength;
			nx *= factor / xunit;
			ny *= factor / yunit;
			x1 = euclidianView.toRealWorldCoordX(midX - nx);
			y1 = euclidianView.toRealWorldCoordY(midY - ny);
			x2 = euclidianView.toRealWorldCoordX(midX + nx);
			y2 = euclidianView.toRealWorldCoordY(midY + ny);
			drawLine(x1, y1, x2, y2, geo);
			break;
		case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
			// vector (vx, vy) to get 2 points around midpoint
			factor = tickSpacing / (2 * nLength);
			vx = -ny * factor;
			vy = nx * factor;
			// use perpendicular vector to set ticks
			factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
			x1 = euclidianView.toRealWorldCoordX(midX + vx - nx);
			x2 = euclidianView.toRealWorldCoordX(midX + vx + nx);
			y1 = euclidianView.toRealWorldCoordY(midY + vy - ny);
			y2 = euclidianView.toRealWorldCoordY(midY + vy + ny);
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - vx - nx);
			x2 = euclidianView.toRealWorldCoordX(midX - vx + nx);
			y1 = euclidianView.toRealWorldCoordY(midY - vy - ny);
			y2 = euclidianView.toRealWorldCoordY(midY - vy + ny);
			drawLine(x1, y1, x2, y2, geo);
			break;
		case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
			// vector (vx, vy) to get 2 points around midpoint
			factor = tickSpacing / nLength;
			vx = -ny * factor;
			vy = nx * factor;
			// use perpendicular vector to set ticks
			factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
			x1 = euclidianView.toRealWorldCoordX(midX + vx - nx);
			x2 = euclidianView.toRealWorldCoordX(midX + vx + nx);
			y1 = euclidianView.toRealWorldCoordY(midY + vy - ny);
			y2 = euclidianView.toRealWorldCoordY(midY + vy + ny);
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - nx);
			x2 = euclidianView.toRealWorldCoordX(midX + nx);
			y1 = euclidianView.toRealWorldCoordY(midY - ny);
			y2 = euclidianView.toRealWorldCoordY(midY + ny);
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - vx - nx);
			x2 = euclidianView.toRealWorldCoordX(midX - vx + nx);
			y1 = euclidianView.toRealWorldCoordY(midY - vy - ny);
			y2 = euclidianView.toRealWorldCoordY(midY - vy + ny);
			drawLine(x1, y1, x2, y2, geo);
			break;
		// Michael Borcherds 20071006 start
		case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
			// vector (vx, vy) to get 2 points around midpoint
			factor = tickSpacing / (nLength);
			vx = -ny * factor;
			vy = nx * factor;
			// use perpendicular vector to set ticks
			factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
			x1 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx
					+ arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy
					+ arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx
					+ arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy
					+ arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			break;
		case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
			// vector (vx, vy) to get 2 points around midpoint
			factor = tickSpacing / (nLength);
			vx = -ny * factor;
			vy = nx * factor;
			// use perpendicular vector to set ticks
			factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
			x1 = euclidianView.toRealWorldCoordX(midX - 2 * arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - 2 * arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX - 2 * arrowlength * vx
					+ arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY - 2 * arrowlength * vy
					+ arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - 2 * arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - 2 * arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX - 2 * arrowlength * vx
					+ arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY - 2 * arrowlength * vy
					+ arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);

			x1 = euclidianView.toRealWorldCoordX(midX);
			y1 = euclidianView.toRealWorldCoordY(midY);
			x2 = euclidianView
					.toRealWorldCoordX(midX + arrowlength * (nx + vx));
			y2 = euclidianView
					.toRealWorldCoordY(midY + arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX);
			y1 = euclidianView.toRealWorldCoordY(midY);
			x2 = euclidianView.toRealWorldCoordX(midX + arrowlength
					* (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY + arrowlength
					* (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			break;
		case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
			// vector (vx, vy) to get 2 points around midpoint
			factor = tickSpacing / nLength;
			vx = -ny * factor;
			vy = nx * factor;
			// use perpendicular vector to set ticks
			factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
			x1 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx
					+ arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy
					+ arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx
					+ arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy
					+ arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);

			x1 = euclidianView.toRealWorldCoordX(midX + arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY + arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX + arrowlength * vx
					+ arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY + arrowlength * vy
					+ arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX + arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY + arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX + arrowlength * vx
					+ arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY + arrowlength * vy
					+ arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);

			x1 = euclidianView.toRealWorldCoordX(midX - 3 * arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - 3 * arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX - 3 * arrowlength * vx
					+ arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY - 3 * arrowlength * vy
					+ arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - 3 * arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - 3 * arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(midX - 3 * arrowlength * vx
					+ arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(midY - 3 * arrowlength * vy
					+ arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			break;
		// Michael Borcherds 20071006 end
		}
	}

	/**
	 * This Method draws The decoration for GeoAngle geo
	 * 
	 * @param geo
	 *            The GeoAngle
	 * @param r
	 *            The Radius
	 * @param vertex
	 *            The vertex coordinates
	 * @param angSt
	 *            Angle Start
	 * @param angEnd
	 *            Angle End
	 */
	protected void markAngle(GeoAngle geo, double r, double[] vertex,
			double angSt, double angEnd) {
		double rdiff;
		switch (geo.decorationType) {
		case GeoElement.DECORATION_ANGLE_TWO_ARCS:
			rdiff = 4 + geo.lineThickness / 2d;
			drawArc(geo, vertex, angSt, angEnd, r);
			r -= rdiff / euclidianView.getXscale();
			drawArc(geo, vertex, angSt, angEnd, r);
			break;
		case GeoElement.DECORATION_ANGLE_THREE_ARCS:
			rdiff = 4 + geo.lineThickness / 2d;
			drawArc(geo, vertex, angSt, angEnd, r);
			r -= rdiff / euclidianView.getXscale();
			drawArc(geo, vertex, angSt, angEnd, r);
			r -= rdiff / euclidianView.getXscale();
			drawArc(geo, vertex, angSt, angEnd, r);
			break;
		case GeoElement.DECORATION_ANGLE_ONE_TICK:
			drawArc(geo, vertex, angSt, angEnd, r);
			euclidianView.toScreenCoords(vertex);
			drawTick(geo, vertex, (angSt + angEnd) / 2);

			break;
		case GeoElement.DECORATION_ANGLE_TWO_TICKS:
			drawArc(geo, vertex, angSt, angEnd, r);
			euclidianView.toScreenCoords(vertex);
			double angleTick[] = new double[2];
			angleTick[0] = (2 * angSt + 3 * angEnd) / 5;
			angleTick[1] = (3 * angSt + 2 * angEnd) / 5;
			if (Math.abs(angleTick[1] - angleTick[0]) > DrawAngle.MAX_TICK_DISTANCE) {
				angleTick[0] = (angSt + angEnd) / 2
						- DrawAngle.MAX_TICK_DISTANCE / 2;
				angleTick[1] = (angSt + angEnd) / 2
						+ DrawAngle.MAX_TICK_DISTANCE / 2;
			}

			drawTick(geo, vertex, angleTick[0]);
			drawTick(geo, vertex, angleTick[1]);
			break;
		case GeoElement.DECORATION_ANGLE_THREE_TICKS:
			drawArc(geo, vertex, angSt, angEnd, r);
			euclidianView.toScreenCoords(vertex);
			angleTick = new double[2];
			angleTick[0] = (5 * angSt + 3 * angEnd) / 8;
			angleTick[1] = (3 * angSt + 5 * angEnd) / 8;
			if (Math.abs(angleTick[1] - angleTick[0]) > DrawAngle.MAX_TICK_DISTANCE) {
				angleTick[0] = (angSt + angEnd) / 2
						- DrawAngle.MAX_TICK_DISTANCE / 2;
				angleTick[1] = (angSt + angEnd) / 2
						+ DrawAngle.MAX_TICK_DISTANCE / 2;
			}
			drawTick(geo, vertex, (angSt + angEnd) / 2);
			drawTick(geo, vertex, angleTick[0]);
			drawTick(geo, vertex, angleTick[1]);
			break;
		case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
			drawArrowArc(geo, vertex, angSt, angEnd, r, false);
			break;
		case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
			drawArrowArc(geo, vertex, angSt, angEnd, r, true);
			break;
		}
	}

	protected void drawAllElements() {
		boolean increment = (euclidianView.getShowGrid()
				|| euclidianView.getShowXaxis() || euclidianView.getShowYaxis());
		for (int step = 0; step < construction.steps(); step++) {
			if (increment)
				beamerSlideNumber = step + 2;
			else
				beamerSlideNumber = step + 1;
			GeoElement[] geos = construction.getConstructionElement(step)
					.getGeoElements();
			for (int j = 0; j < geos.length; j++) {
				GeoElement g = geos[j];
				drawGeoElement(g, false, false);
			}
		}

	}

	protected void startBeamer(StringBuilder sb) {
		if (isBeamer)
			sb.append("\\onslide<" + beamerSlideNumber + "->{\n  ");
	}

	protected void endBeamer(StringBuilder sb) {
		if (isBeamer)
			sb.append("}\n");
	}

	protected void resizeFont(StringBuilder sb) {

		// Number of units that represents the font size:
		int ggbSize = app.getGUIFontSize();
		double ggbYUnit = euclidianView.getYscale();
		double fontUnits = ggbSize / ggbYUnit;
		// Now, on the output, calculate the size in centimeter
		double yunit = frame.getYUnit();
		double sizeCM = fontUnits * yunit;
		// 1cm=1/2.54 in
		// 1 in=72.27pt
		// new size in pt:
		int sizept = (int) (sizeCM / 2.54 * 72.27 + 0.5);
		int texSize = frame.getFontSize();
		if (texSize == 10) {
			if (sizept <= 5)
				sb.append("\\tiny{");
			else if (sizept <= 7)
				sb.append("\\scriptsize{");
			else if (sizept == 8)
				sb.append("\\footnotesize{");
			else if (sizept == 9)
				sb.append("\\small{");
			else if (sizept == 10)
				sb.append("\\normalsize{");
			else if (sizept <= 12)
				sb.append("\\large{");
			else if (sizept <= 14)
				sb.append("\\Large{");
			else if (sizept <= 17)
				sb.append("\\LARGE{");
			else if (sizept <= 20)
				sb.append("\\huge{");
			else
				sb.append("\\Huge{");
		} else if (texSize == 11) {
			if (sizept <= 6)
				sb.append("\\tiny{");
			else if (sizept <= 8)
				sb.append("\\scriptsize{");
			else if (sizept == 9)
				sb.append("\\footnotesize{");
			else if (sizept == 10)
				sb.append("\\small{");
			else if (sizept == 11)
				sb.append("\\normalsize{");
			else if (sizept == 12)
				sb.append("\\large{");
			else if (sizept <= 14)
				sb.append("\\Large{");
			else if (sizept <= 17)
				sb.append("\\LARGE{");
			else if (sizept <= 20)
				sb.append("\\huge{");
			else
				sb.append("\\Huge{");
		} else if (texSize == 12) {
			if (sizept <= 6)
				sb.append("\\tiny{");
			else if (sizept <= 8)
				sb.append("\\scriptsize{");
			else if (sizept <= 10)
				sb.append("\\footnotesize{");
			else if (sizept == 11)
				sb.append("\\small{");
			else if (sizept == 12)
				sb.append("\\normalsize{");
			else if (sizept <= 14)
				sb.append("\\large{");
			else if (sizept <= 17)
				sb.append("\\Large{");
			else if (sizept <= 20)
				sb.append("\\LARGE{");
			else if (sizept <= 25)
				sb.append("\\huge{");
			else
				sb.append("\\Huge{");
		}
	}

	protected String getImplicitExpr(GeoImplicitPoly geo) {
		StringBuilder sb = new StringBuilder();
		double[][] coeff = geo.getCoeff();
		boolean first = true;
		for (int i = 0; i < coeff.length; i++) {
			for (int j = 0; j < coeff[i].length; j++) {
				double tmp = coeff[i][j];
				if (tmp != 0) {
					if (tmp > 0) {
						if (!first)
							sb.append("+");
					}
					sb.append(tmp);
					if (i == 0) {
						if (j != 0) {
							sb.append("*y^");
							sb.append(j);
						}
					} else {
						sb.append("*x^");
						sb.append(i);
						if (j != 0) {
							sb.append("*y^");
							sb.append(j);
						}
					}
					first = false;
				}
			}
		}
		return new String(sb);
	}
	
	protected StringTemplate getStringTemplate(){
    	return StringTemplate.get(StringType.PSTRICKS);
    }
	protected StringBuilder drawNoLatexFunction(GeoFunction geo, double xrangemax, double xrangemin, int point,String template){
		StringBuilder lineBuilder = new StringBuilder();
		double y = geo.evaluate(xrangemin);
		if (Math.abs(y) < 0.001)
			y = 0;
		double yprec = y;
		double step = (xrangemax - xrangemin) / point;
		double xprec = xrangemin - step;
		double x = xprec;
		for (; x <= xrangemax; x += step) {
			y = geo.evaluate(x);
			if (Math.abs(yprec - y) < (ymax - ymin)) {
				if (Math.abs(y) < 0.001)
					y = 0;
				lineBuilder.append(String.format(template,xprec,yprec,x,y));				
			}
			yprec = y;
			xprec = x;
		}
		return lineBuilder;
	}

	protected boolean isLatexFunction(String s) {
		// used if there are other non-latex
		return !s.toLowerCase().contains("erf(")
				&& !s.toLowerCase().contains("gamma(")
				&& !s.toLowerCase().contains("gammaRegularized(")
				&& !s.toLowerCase().contains("cbrt(")
				&& !s.toLowerCase().contains("csc(")
				&& !s.toLowerCase().contains("csch(")
				&& !s.toLowerCase().contains("sec(")
				&& !s.toLowerCase().contains("cot(")
				&& !s.toLowerCase().contains("coth(")
				&& !s.toLowerCase().contains("sech(");
	}
	protected  void initializeSymbols(Properties symbols){			
			try {
				symbols.load(GeoGebraExport.class.getResourceAsStream("unicodetex"));
			} catch (IOException e) {
				//FileMenu catch this
			}
	}
	
	
	//To avoid duplicate inequalities drawing algorithms  replacing  Graphics.  
	//In the three implementations (pstricks, pgf, asymptote) print the appropriate commands
	abstract class MyGraphics extends GGraphics2DD{

		protected double []ds;
		protected Inequality ineq;
		protected EuclidianView view;
		protected FunctionalNVar geo;
		
		public MyGraphics(FunctionalNVar geo, Inequality ineq, EuclidianViewND euclidianView) throws IOException {
			super(new MyGraphics2D(null,System.out,0,0,0,0, ColorMode.COLOR_RGB));
			view=euclidianView;
			this.geo=geo;
			this.ds=geo.getKernel().getViewBoundsForGeo((GeoElement)geo);
			this.ineq=ineq;
		}
		@Override
		public abstract void fill(GShape s);
	}
	
	protected void addTextPackage(){
		StringBuilder packages=new StringBuilder();
		if (codePreamble.indexOf("amssymb")==-1){
			packages.append("amssymb,");
		}
		if (codePreamble.indexOf("fancyhdr")==-1){
			packages.append("fancyhdr,");
		}
		if (codePreamble.indexOf("txfonts")==-1){
			packages.append("txfonts,");
		}
		if (codePreamble.indexOf("pxfonts")==-1){
			packages.append("pxfonts,");
		}
		if (packages.length()!=0){
			packages.delete(packages.length()-1, packages.length());
			codePreamble.append("\\usepackage{"+packages.toString()+"}\n");
		}
	}
	// Created just for the constructor of MyGraphics.EpsGraphics used to avoid
	// having all methods of Graphics2D. None of his methods is used
	class MyGraphics2D extends EpsGraphics {

		public MyGraphics2D(String title, OutputStream outputStream, int minX,
				int minY, int maxX, int maxY, ColorMode colorMode) throws IOException{
			super(title, outputStream, minX, minY, maxX, maxY, colorMode);	
		}
	}
}