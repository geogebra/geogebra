package org.geogebra.common.export.pstricks;

import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.euclidian.draw.DrawInequality;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.euclidian.plot.CurveSegmentPlotter;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoBoxPlot;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import org.geogebra.common.kernel.algos.AlgoSlope;
import org.geogebra.common.kernel.algos.AlgoSumLeft;
import org.geogebra.common.kernel.algos.AlgoSumLower;
import org.geogebra.common.kernel.algos.AlgoSumRectangle;
import org.geogebra.common.kernel.algos.AlgoSumTrapezoidal;
import org.geogebra.common.kernel.algos.AlgoSumUpper;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.IneqTree;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.cas.AlgoIntegralFunctions;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.statistics.AlgoHistogram;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Superclass for text format (e.g. LaTeX) image exports.
 */
public abstract class GeoGebraExport {
	protected int beamerSlideNumber = 1;
	protected final static double PRECISION_XRANGE_FUNCTION = 0.00001;
	protected StringBuilder code;
	protected StringBuilder codePoint;
	protected StringBuilder codePreamble;
	protected StringBuilder codeFilledObject;
	protected StringBuilder codeBeginDoc;
	private App app;
	protected Kernel kernel;
	protected Construction construction;
	protected EuclidianView euclidianView;
	protected ExportSettings frame;
	protected HashMap<GColor, String> customColor;
	protected double xunit;
	protected double yunit;
	protected double xmin;
	protected double xmax;
	protected double ymin;
	protected double ymax;
	// The exported format: Latex, tex, ConTexT, Beamer
	protected int format = 0;
	protected boolean isBeamer = false;
	protected int barNumber;
	private StringTemplate tpl;

	/**
	 * @param app
	 *            application
	 */
	public GeoGebraExport(App app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.construction = kernel.getConstruction();
		this.euclidianView = app.getActiveEuclidianView();
		this.tpl = StringTemplate.printFigures(StringType.PSTRICKS, 12, false);
		initBounds();
	}

	/**
	 * @return application
	 */
	public App getApp() {
		return app;
	}

	protected String format(double d) {
		String ret = kernel.format(DoubleUtil.checkDecimalFraction(d), tpl);
		return StringUtil.canonicalNumber2(ret);
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
	/**
	 * Change selection rectanlge to fit user input.
	 */
	public void refreshSelectionRectangle() {
		int x1 = euclidianView.toScreenCoordX(xmin);
		int x2 = euclidianView.toScreenCoordX(xmax);
		int y1 = euclidianView.toScreenCoordY(ymin);
		int y2 = euclidianView.toScreenCoordY(ymax);
		GRectangle rec = AwtFactory.getPrototype().newRectangle(x1, y2, x2 - x1,
				y1 - y2);
		// Application.debug(x1+" "+x2+" "+y1+" "+y2);
		euclidianView.setSelectionRectangle(rec);
		euclidianView.repaint();
	}

	/**
	 * @param xmin
	 *            x-min
	 */
	public void setxmin(double xmin) {
		this.xmin = xmin;
		this.refreshSelectionRectangle();
	}

	/**
	 * @param xmax
	 *            x-max
	 */
	public void setxmax(double xmax) {
		this.xmax = xmax;
		this.refreshSelectionRectangle();
	}

	/**
	 * @param ymin
	 *            y-min
	 */
	public void setymin(double ymin) {
		this.ymin = ymin;
		this.refreshSelectionRectangle();
	}

	/**
	 * @param ymax
	 *            y-max
	 */
	public void setymax(double ymax) {
		this.ymax = ymax;
		this.refreshSelectionRectangle();
	}

	/**
	 * @return x-min
	 */
	public double getxmin() {
		return this.xmin;
	}

	/**
	 * @return x-max
	 */
	public double getxmax() {
		return this.xmax;
	}

	/**
	 * @return y-min
	 */
	public double getymin() {
		return this.ymin;
	}

	/**
	 * @return y-max
	 */
	public double getymax() {
		return this.ymax;
	}

	/**
	 * Initialize Gui JFrame
	 */
	private void initBounds() {
		xunit = 1;
		yunit = 1;
		// Changes to make xmin,xmax,ymin,ymax be defined by the selection
		// rectangle
		// when this one is defined.
		GRectangle rect = this.euclidianView.getSelectionRectangle();
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
	}

	/**
	 * @param beamer
	 *            beamer flag
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
		String s = StringUtil.toLowerCaseUS(String.valueOf(d));
		// StringTokenizer st = new StringTokenizer(s, "e");
		StringBuilder number;
		int posE = s.indexOf("e");
		if (posE == -1) {
			return s;
		}
		String token1 = s.substring(0, posE);
		String token2 = s.substring(posE + 1);
		number = new StringBuilder(token1);
		int exp = Integer.parseInt(token2);
		if (exp > 0) {
			int id_point = number.indexOf(".");
			if (id_point == -1) {
				for (int i = 0; i < exp; i++) {
					number.append("0");
				}
			} else {
				number.deleteCharAt(id_point);
				int zeros = exp - (number.length() - id_point);
				for (int i = 0; i < zeros; i++) {
					number.append("0");
				}
			}
		} else {
			exp = -exp;
			int id_point = number.indexOf(".");
			number.deleteCharAt(id_point);
			for (int i = 0; i < exp - 1; i++) {
				number.insert(0, "0");
			}
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

	protected String createCustomColor(int red, int green, int blue) {
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
	 *            whether to trim around intersection
	 */
	protected void drawGeoElement(GeoElement g, boolean fromGeoList,
			boolean trimmedInter) {
		if (g.isGeoList()) {
			GeoList geo = ((GeoList) g);
			for (int i = 0; i < geo.size(); i++) {
				drawGeoElement(geo.get(i), true, false);
			}
		} else if (g.isWhollyIn2DView(app.getEuclidianView1())
				&& (g.isEuclidianVisible() || trimmedInter)) {
			if (g instanceof GeoPointND) {
				drawGeoPoint((GeoPointND) g);
				drawLabel(g, null);
			} else if (g instanceof GeoSegmentND) {
				drawGeoSegment((GeoSegmentND) g);
				drawLabel(g, null);
			} else if (g instanceof GeoRayND) {
				drawGeoRay((GeoRayND) g);
				drawLabel(g, null);
			} else if (g instanceof GeoPolyLine) {
				drawPolyLine((GeoPolyLine) g);
			} else if (g instanceof GeoLine) {
				drawGeoLine((GeoLine) g);
				drawLabel(g, null);
			} else if (g instanceof GeoPolygon) {
				drawPolygon((GeoPolygon) g);
				drawLabel(g, null);
			} else if (g instanceof GeoAngle) {
				if (g.isIndependent()) {
					// independent number may be shown as slider
					drawSlider((GeoNumeric) g);
				} else {
					drawAngle((GeoAngle) g);
					// String
					// label="$"+Util.toLaTeXString(g.getLabelDescription(),true)+"$";
					drawLabel(g, euclidianView.getDrawableFor(g));
				}
			} else if (g instanceof GeoImplicit) {
				drawImplicitPoly((GeoImplicit) g);
			}
			// To draw Inequalities
			else if (g.getTypeString().equals("Inequality")) {
				if (g.isGeoFunctionBoolean()) {
					drawGeoInequalities(null, g);
				} else {
					drawGeoInequalities((GeoFunctionNVar) g, null);
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
					if (algo instanceof AlgoSumTrapezoidal) {
						drawSumTrapezoidal((GeoNumeric) g);
					} else if (algo instanceof AlgoHistogram) {
						drawBarChartOrHistogram((GeoNumeric) g);
					} else if (algo instanceof AlgoSumUpper
							|| algo instanceof AlgoSumLower
							|| algo instanceof AlgoSumLeft
							|| algo instanceof AlgoSumRectangle) {
						drawSumUpperLower((GeoNumeric) g);
					}
					drawLabel(g, null);
				}
				// Bar Chart
				else if (algo instanceof AlgoBarChart) {
					drawBarChartOrHistogram((GeoNumeric) g);
					drawLabel(g, null);
				}
			} else if (g instanceof GeoVector) {
				drawGeoVector((GeoVector) g);
				drawLabel(g, null);
			} else if (g instanceof GeoConicPart) {
				GeoConicPart geo = (GeoConicPart) g;
				drawGeoConicPart(geo);
				if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_ARC
						|| geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR) {
					drawLabel(g, null);
				}
			} else if (g instanceof GeoConicND) {
				if (isSinglePointConic(g)) {
					GeoConicND geo = (GeoConicND) g;
					GeoPoint point = geo.getSinglePoint();
					point.copyLabel(geo);
					point.setObjColor(geo.getObjectColor());
					point.setPointSize(geo.getLineThickness());
					point.setLabelOffset(geo.labelOffsetX, geo.labelOffsetY);
					DrawPoint drawPoint = new DrawPoint(euclidianView, point);
					drawPoint.setGeoElement(geo);
					drawGeoPoint(point);
					drawLabel(point, drawPoint);
				} else if (isDoubleLineConic(g)) {
					GeoConicND geo = (GeoConicND) g;
					GeoLine[] lines = geo.getLines();
					DrawLine[] drawLines = new DrawLine[2];
					for (int i = 0; i < 2; i++) {
						lines[i].copyLabel(geo);
						lines[i].setObjColor(geo.getObjectColor());
						lines[i].setLineThickness(geo.getLineThickness());
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
					drawGeoConic((GeoConicND) g);
					drawLabel(g, null);
				}
			} else if (g instanceof GeoFunction) {
				drawFunction((GeoFunction) g);
				drawLabel(g, null);
			} else if (g instanceof GeoCurveCartesian) {
				drawCurveCartesian(g);
				drawLabel(g, null);
			} else if (g.isGeoText()) {
				drawText((GeoText) g);
			} else if (g.isGeoImage()) {
				// Image --> export to eps is better and easier!
			} else if (g instanceof GeoLocus) {
				drawLocus((GeoLocus) g);
			} else {
				Log.debug("Export: unsupported GeoElement "
						+ g.getGeoClassType() + " " + g.isDrawable());
			}
		}

	}

	protected void drawBarChartOrHistogram(GeoNumeric g) {

		if (g.getParentAlgorithm() instanceof AlgoFunctionAreaSums) {
			AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums) g
					.getParentAlgorithm();
			drawHistogramOrBarChartBox(algo.getValues(), algo.getLeftBorder(),
					algo.getValues().length - 1, 0, g);
		} else if (g.getParentAlgorithm() instanceof AlgoBarChart) {
			AlgoBarChart algo = (AlgoBarChart) g.getParentAlgorithm();
			drawHistogramOrBarChartBox(algo.getValues(), algo.getLeftBorder(),
					algo.getValues().length, algo.getWidth(), g);

		}

	}

	protected boolean isSinglePointConic(GeoElementND geo) {
		if (geo.isGeoConic()) {
			if (((GeoConicND) geo)
					.getType() == GeoConicNDConstants.CONIC_SINGLE_POINT) {
				return true;
			}
		}
		return false;
	}

	protected boolean isDoubleLineConic(GeoElementND geo) {
		if (geo.isGeoConic()) {
			if (((GeoConicND) geo)
					.getType() == GeoConicNDConstants.CONIC_DOUBLE_LINE
					|| ((GeoConicND) geo)
							.getType() == GeoConicNDConstants.CONIC_INTERSECTING_LINES
					|| ((GeoConicND) geo)
							.getType() == GeoConicNDConstants.CONIC_PARALLEL_LINES) {
				return true;
			}
		}
		return false;
	}

	protected boolean isEmpty(GeoElementND geo) {
		if (geo.isGeoConic()) {
			if (((GeoConicND) geo)
					.getType() == GeoConicNDConstants.CONIC_EMPTY) {
				return true;
			}
		}
		return false;
	}

	protected int resizePt(int size) {
		double height_geogebra = euclidianView.getHeight() / 30.0;
		double height_latex = frame.getLatexHeight();
		double ratio = height_latex / height_geogebra;
		int tmp = (int) Math.round(ratio * size);
		if (tmp != 0) {
			return tmp;
		}
		return 1;
	}

	/**
	 * Export as PSTricks or PGF/TikZ GeoPoint
	 * 
	 * @param geo
	 *            The point to export
	 */

	abstract protected void drawGeoPoint(GeoPointND geo);

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
	abstract protected void drawGeoRay(GeoRayND geo);

	/**
	 * Export as PSTricks or PGF/TikZ GeoSegment segment[A,B]
	 * 
	 * @param geo
	 *            The segment to export
	 */
	abstract protected void drawGeoSegment(GeoSegmentND geo);

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

	abstract protected void drawGeoConic(GeoConicND geo);

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
	abstract protected void drawLabel(GeoElementND geo, DrawableND drawGeo);

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
	abstract protected void drawImplicitPoly(GeoImplicit geo);

	/**
	 * Export as PSTricks or PGF/TikZ parametric functions
	 * 
	 * @param geo
	 *            The function to export
	 */
	abstract protected void drawSingleCurveCartesian(GeoCurveCartesian geo,
			boolean trasparency);

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

	abstract protected void drawLine(double x1, double y1, double x2, double y2,
			GeoElementND geo);

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
	abstract protected void drawArc(GeoAngle geo, double[] vertex, double angSt,
			double angEnd, double r);

	/**
	 * Export as PStricks or PGF segment mark
	 * 
	 * @param geo
	 *            geo Object
	 * @param vertex
	 *            angle vertex coords
	 * @param angleTick
	 *            angle
	 */
	abstract protected void drawTick(GeoAngle geo, double[] vertex,
			double angleTick);

	abstract protected void drawArrowArc(GeoAngle geo, double[] vertex,
			double angSt, double angEnd, double r, boolean clockwise);

	/**
	 * @param settingsFrame
	 *            frame where user may edit the settings and where he becomes
	 *            the output
	 */
	public final void setFrame(ExportSettings settingsFrame) {
		frame = settingsFrame;
	}

	/**
	 * When The Button "generate Code" has been clicked
	 */
	public abstract void generateAllCode();

	/**
	 * Export as PSTricks or PGF/TikZ color's code
	 * 
	 * @param color
	 *            The color
	 * @param sb
	 *            The StringBuilder to complete
	 */

	abstract protected void colorCode(GColor color, StringBuilder sb);

	/**
	 * Export as PSTricks or PGF/TikZ PolyLine objects polyline[A,B,C,D,E]
	 * 
	 * @param geo
	 *            The Polyline objects
	 */
	abstract protected void drawPolyLine(GeoPolyLine geo);

	/**
	 * Export as PSTricks or PGF/TikZ BarChart or Histogram
	 * 
	 * @param values
	 *            Values of Histogram or BarChart
	 * @param leftBorder
	 *            class boundaries
	 * @param length
	 *            Length of data. Along with leftBorder length allows you to
	 *            choose between Histogram or BarChart
	 * @param width
	 *            Used if is a BarChart
	 * @param g
	 *            For visual style
	 */
	protected abstract void drawHistogramOrBarChartBox(double[] values,
			double[] leftBorder, int length, double width, GeoNumeric g);

	/**
	 * Export inequalities as PSTricks or PGF or Asymptote
	 * 
	 * @param geo
	 *            The inequality function
	 * @param e
	 *            If is inequality with one variable eg. x>3
	 */

	protected void drawGeoInequalities(GeoFunctionNVar geo, GeoElementND e) {
		FunctionalNVar ef = null;
		if (geo == null) {
			ef = (FunctionalNVar) e;
		} else {
			ef = geo;
		}
		DrawInequality drawable = new DrawInequality(euclidianView, ef);
		GGraphics2D g = null;
		IneqTree tree = ef.getFunction().getIneqs();

		if (tree.getLeft() != null) {
			for (int i = 0; i < tree.getLeft().getSize(); i++) {
				g = createGraphics(ef, tree.getLeft().get(i));
				drawable.draw(g);
			}
		}
		if (tree.getRight() != null) {
			for (int i = 0; i < tree.getLeft().getSize(); i++) {
				g = createGraphics(ef, tree.getRight().get(i));
				drawable.draw(g);
			}
		}
		if (tree.getIneq() != null) {
			g = createGraphics(ef, tree.getIneq());
			drawable.draw(g);
		}
		// Only for syntax. Never throws

	}

	// Create the appropriate instance of MyGraphics of various implementations
	// (pstricks,pgf,asymptote)
	abstract protected GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality);

	abstract protected boolean fillSpline(GeoCurveCartesian[] curves);

	/**
	 * @return the xmin
	 */
	public double getXmin() {
		return xmin;
	}

	/**
	 * @param xmin
	 *            the xmin to set
	 */
	public void setXmin(double xmin) {
		this.xmin = xmin;
	}

	/**
	 * @return the xmax
	 */
	public double getXmax() {
		return xmax;
	}

	/**
	 * @param xmax
	 *            the xmax to set
	 */
	public void setXmax(double xmax) {
		this.xmax = xmax;
	}

	/**
	 * @return the ymin
	 */
	public double getYmin() {
		return ymin;
	}

	/**
	 * @param ymin
	 *            the ymin to set
	 */
	public void setYmin(double ymin) {
		this.ymin = ymin;
	}

	/**
	 * @return the ymax
	 */
	public double getYmax() {
		return ymax;
	}

	/**
	 * @param ymax
	 *            the ymax to set
	 */
	public void setYmax(double ymax) {
		this.ymax = ymax;
	}

	/**
	 * @return the xunit
	 */
	public double getXunit() {
		return xunit;
	}

	/**
	 * @param xunit
	 *            the xunit to set
	 */
	public void setXunit(double xunit) {
		this.xunit = xunit;
	}

	/**
	 * @return the yunit
	 */
	public double getYunit() {
		return yunit;
	}

	/**
	 * @param yunit
	 *            the yunit to set
	 */
	public void setYunit(double yunit) {
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

	protected void mark(double[] A, double[] B, int deco, GeoElementND geo) {
		// calc midpoint (midX, midY) and perpendicular vector (nx, ny)
		euclidianView.toScreenCoords(A);
		euclidianView.toScreenCoords(B);
		double midX = (A[0] + B[0]) / 2.0;
		double midY = (A[1] + B[1]) / 2.0;
		double nx = A[1] - B[1];
		double ny = B[0] - A[0];
		double nLength = MyMath.length(nx, ny);
		// tick spacing and length.
		double tickSpacing = 2.5 + geo.getLineThickness() / 2d;
		double tickLength = tickSpacing + 1;
		double arrowlength = 1.5;
		double vx, vy, factor, x1, x2, y1, y2;
		switch (deco) {
		default:
			// do nothing
			break;
		case GeoElementND.DECORATION_SEGMENT_ONE_TICK:
			factor = tickLength / nLength;
			nx *= factor / xunit;
			ny *= factor / yunit;
			x1 = euclidianView.toRealWorldCoordX(midX - nx);
			y1 = euclidianView.toRealWorldCoordY(midY - ny);
			x2 = euclidianView.toRealWorldCoordX(midX + nx);
			y2 = euclidianView.toRealWorldCoordY(midY + ny);
			drawLine(x1, y1, x2, y2, geo);
			break;
		case GeoElementND.DECORATION_SEGMENT_TWO_TICKS:
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
		case GeoElementND.DECORATION_SEGMENT_THREE_TICKS:
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
		case GeoElementND.DECORATION_SEGMENT_ONE_ARROW:
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
			x2 = euclidianView.toRealWorldCoordX(
					midX - arrowlength * vx + arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY - arrowlength * vy + arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(
					midX - arrowlength * vx + arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY - arrowlength * vy + arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			break;
		case GeoElementND.DECORATION_SEGMENT_TWO_ARROWS:
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
			x2 = euclidianView.toRealWorldCoordX(
					midX - 2 * arrowlength * vx + arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY - 2 * arrowlength * vy + arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - 2 * arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - 2 * arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(
					midX - 2 * arrowlength * vx + arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY - 2 * arrowlength * vy + arrowlength * (-ny + vy));
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
			x2 = euclidianView
					.toRealWorldCoordX(midX + arrowlength * (-nx + vx));
			y2 = euclidianView
					.toRealWorldCoordY(midY + arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			break;
		case GeoElementND.DECORATION_SEGMENT_THREE_ARROWS:
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
			x2 = euclidianView.toRealWorldCoordX(
					midX - arrowlength * vx + arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY - arrowlength * vy + arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(
					midX - arrowlength * vx + arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY - arrowlength * vy + arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);

			x1 = euclidianView.toRealWorldCoordX(midX + arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY + arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(
					midX + arrowlength * vx + arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY + arrowlength * vy + arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX + arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY + arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(
					midX + arrowlength * vx + arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY + arrowlength * vy + arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);

			x1 = euclidianView.toRealWorldCoordX(midX - 3 * arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - 3 * arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(
					midX - 3 * arrowlength * vx + arrowlength * (nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY - 3 * arrowlength * vy + arrowlength * (ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			x1 = euclidianView.toRealWorldCoordX(midX - 3 * arrowlength * vx);
			y1 = euclidianView.toRealWorldCoordY(midY - 3 * arrowlength * vy);
			x2 = euclidianView.toRealWorldCoordX(
					midX - 3 * arrowlength * vx + arrowlength * (-nx + vx));
			y2 = euclidianView.toRealWorldCoordY(
					midY - 3 * arrowlength * vy + arrowlength * (-ny + vy));
			drawLine(x1, y1, x2, y2, geo);
			break;
		}
	}

	/**
	 * This Method draws The decoration for GeoAngle geo
	 * 
	 * @param geo
	 *            The GeoAngle
	 * @param r0
	 *            The radius
	 * @param vertex
	 *            The vertex coordinates
	 * @param angSt
	 *            Angle Start
	 * @param angEnd
	 *            Angle End
	 */
	protected void markAngle(GeoAngle geo, double r0, double[] vertex,
			double angSt, double angEnd) {
		double rdiff;
		double r = r0;
		switch (geo.getDecorationType()) {
		default:
			// do nothing
			break;
		case GeoElementND.DECORATION_ANGLE_TWO_ARCS:
			rdiff = 4 + geo.getLineThickness() / 2d;
			drawArc(geo, vertex, angSt, angEnd, r);
			r -= rdiff / euclidianView.getXscale();
			drawArc(geo, vertex, angSt, angEnd, r);
			break;
		case GeoElementND.DECORATION_ANGLE_THREE_ARCS:
			rdiff = 4 + geo.getLineThickness() / 2d;
			drawArc(geo, vertex, angSt, angEnd, r);
			r -= rdiff / euclidianView.getXscale();
			drawArc(geo, vertex, angSt, angEnd, r);
			r -= rdiff / euclidianView.getXscale();
			drawArc(geo, vertex, angSt, angEnd, r);
			break;
		case GeoElementND.DECORATION_ANGLE_ONE_TICK:
			drawArc(geo, vertex, angSt, angEnd, r);
			euclidianView.toScreenCoords(vertex);
			drawTick(geo, vertex, (angSt + angEnd) / 2);

			break;
		case GeoElementND.DECORATION_ANGLE_TWO_TICKS:
			drawArc(geo, vertex, angSt, angEnd, r);
			euclidianView.toScreenCoords(vertex);
			double[] angleTick = new double[2];
			angleTick[0] = (2 * angSt + 3 * angEnd) / 5;
			angleTick[1] = (3 * angSt + 2 * angEnd) / 5;
			if (Math.abs(angleTick[1]
					- angleTick[0]) > DrawAngle.MAX_TICK_DISTANCE) {
				angleTick[0] = (angSt + angEnd) / 2
						- DrawAngle.MAX_TICK_DISTANCE / 2;
				angleTick[1] = (angSt + angEnd) / 2
						+ DrawAngle.MAX_TICK_DISTANCE / 2;
			}

			drawTick(geo, vertex, angleTick[0]);
			drawTick(geo, vertex, angleTick[1]);
			break;
		case GeoElementND.DECORATION_ANGLE_THREE_TICKS:
			drawArc(geo, vertex, angSt, angEnd, r);
			euclidianView.toScreenCoords(vertex);
			angleTick = new double[2];
			angleTick[0] = (5 * angSt + 3 * angEnd) / 8;
			angleTick[1] = (3 * angSt + 5 * angEnd) / 8;
			if (Math.abs(angleTick[1]
					- angleTick[0]) > DrawAngle.MAX_TICK_DISTANCE) {
				angleTick[0] = (angSt + angEnd) / 2
						- DrawAngle.MAX_TICK_DISTANCE / 2;
				angleTick[1] = (angSt + angEnd) / 2
						+ DrawAngle.MAX_TICK_DISTANCE / 2;
			}
			drawTick(geo, vertex, (angSt + angEnd) / 2);
			drawTick(geo, vertex, angleTick[0]);
			drawTick(geo, vertex, angleTick[1]);
			break;
		case GeoElementND.DECORATION_ANGLE_ARROW_CLOCKWISE:
			drawArrowArc(geo, vertex, angSt, angEnd, r, false);
			break;
		case GeoElementND.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
			drawArrowArc(geo, vertex, angSt, angEnd, r, true);
			break;
		}
	}

	protected void drawAllElements() {
		boolean increment = (euclidianView.getShowGrid()
				|| euclidianView.getShowXaxis()
				|| euclidianView.getShowYaxis());
		for (int step = 0; step < construction.steps(); step++) {
			if (increment) {
				beamerSlideNumber = step + 2;
			} else {
				beamerSlideNumber = step + 1;
			}
			GeoElementND[] geos = construction.getConstructionElement(step)
					.getGeoElements();
			for (int j = 0; j < geos.length; j++) {
				GeoElement g = geos[j].toGeoElement();
				drawGeoElement(g, false, false);
			}
		}

	}

	// added by Hoszu Henrietta
	protected void drawAllInDependentElements() {
		boolean increment = (euclidianView.getShowGrid()
				|| euclidianView.getShowXaxis()
				|| euclidianView.getShowYaxis());
		for (int step = 0; step < construction.steps(); step++) {
			if (increment) {
				beamerSlideNumber = step + 2;
			} else {
				beamerSlideNumber = step + 1;
			}
			GeoElementND[] geos = construction.getConstructionElement(step)
					.getGeoElements();
			for (int j = 0; j < geos.length; j++) {
				GeoElement g = geos[j].toGeoElement();
				if (g.isIndependent()) {
					drawGeoElement(g, false, false);
				}
			}
		}

	}

	protected void startBeamer(StringBuilder sb) {
		if (isBeamer) {
			sb.append("\\onslide<" + beamerSlideNumber + "->{\n  ");
		}
	}

	protected void endBeamer(StringBuilder sb) {
		if (isBeamer) {
			sb.append("}\n");
		}
	}

	protected void resizeFont(StringBuilder sb) {

		// Number of units that represents the font size:
		int ggbSize = app.getGUIFontSize();
		double ggbYUnit = euclidianView.getYscale();
		double fontUnits = ggbSize / ggbYUnit;
		// Now, on the output, calculate the size in centimeter
		double yunit1 = frame.getYUnit();
		double sizeCM = fontUnits * yunit1;
		// 1cm=1/2.54 in
		// 1 in=72.27pt
		// new size in pt:
		int sizept = (int) (sizeCM / 2.54 * 72.27 + 0.5);
		int texSize = frame.getFontSize();
		if (texSize == 10) {
			if (sizept <= 5) {
				sb.append("\\tiny{");
			} else if (sizept <= 7) {
				sb.append("\\scriptsize{");
			} else if (sizept == 8) {
				sb.append("\\footnotesize{");
			} else if (sizept == 9) {
				sb.append("\\small{");
			} else if (sizept == 10) {
				sb.append("\\normalsize{");
			} else if (sizept <= 12) {
				sb.append("\\large{");
			} else if (sizept <= 14) {
				sb.append("\\Large{");
			} else if (sizept <= 17) {
				sb.append("\\LARGE{");
			} else if (sizept <= 20) {
				sb.append("\\huge{");
			} else {
				sb.append("\\Huge{");
			}
		} else if (texSize == 11) {
			if (sizept <= 6) {
				sb.append("\\tiny{");
			} else if (sizept <= 8) {
				sb.append("\\scriptsize{");
			} else if (sizept == 9) {
				sb.append("\\footnotesize{");
			} else if (sizept == 10) {
				sb.append("\\small{");
			} else if (sizept == 11) {
				sb.append("\\normalsize{");
			} else if (sizept == 12) {
				sb.append("\\large{");
			} else if (sizept <= 14) {
				sb.append("\\Large{");
			} else if (sizept <= 17) {
				sb.append("\\LARGE{");
			} else if (sizept <= 20) {
				sb.append("\\huge{");
			} else {
				sb.append("\\Huge{");
			}
		} else if (texSize == 12) {
			if (sizept <= 6) {
				sb.append("\\tiny{");
			} else if (sizept <= 8) {
				sb.append("\\scriptsize{");
			} else if (sizept <= 10) {
				sb.append("\\footnotesize{");
			} else if (sizept == 11) {
				sb.append("\\small{");
			} else if (sizept == 12) {
				sb.append("\\normalsize{");
			} else if (sizept <= 14) {
				sb.append("\\large{");
			} else if (sizept <= 17) {
				sb.append("\\Large{");
			} else if (sizept <= 20) {
				sb.append("\\LARGE{");
			} else if (sizept <= 25) {
				sb.append("\\huge{");
			} else {
				sb.append("\\Huge{");
			}
		}
	}

	protected String getImplicitExpr(GeoImplicit geo) {
		StringBuilder sb = new StringBuilder();
		double[][] coeff = geo.getCoeff();
		if (coeff == null) {
			return null;
		}
		boolean first = true;
		for (int i = 0; i < coeff.length; i++) {
			for (int j = 0; j < coeff[i].length; j++) {
				double tmp = coeff[i][j];
				if (tmp != 0) {
					if (tmp > 0) {
						if (!first) {
							sb.append("+");
						}
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

	protected StringTemplate getStringTemplate() {
		return tpl;
	}

	/**
	 * @param geo
	 *            curve
	 * @param xrangemax
	 *            max parameter value
	 * @param xrangemin
	 *            min parameter value
	 * @param point
	 *            number of pints
	 * @param template
	 *            template for outputing lines
	 * @return string builder with all the lines
	 */
	protected final StringBuilder drawNoLatexFunction(CurveEvaluable geo,
			double xrangemax, double xrangemin, int point, String template) {
		StringBuilder lineBuilder = new StringBuilder();
		double[] out = new double[2];
		geo.evaluateCurve(xrangemin, out);
		double y = out[1];
		double yprec = y;
		if (Math.abs(y) < 0.001) {
			y = yprec = 0;
		}
		double x = out[0];
		double xprec = x;
		if (Math.abs(x) < 0.001) {
			x = xprec = 0;
		}
		double step = (xrangemax - xrangemin) / point;
		double tprec = xrangemin;
		double t = tprec;
		for (; t <= xrangemax; t += step) {
			geo.evaluateCurve(t, out);
			y = out[1];
			x = out[0];
			if (Math.abs(y) < 0.001) {
				y = 0;
			}
			if (Math.abs(x) < 0.001) {
				x = 0;
			}
			if (Math.abs(yprec - y) < (ymax - ymin)) {
				if (CurveSegmentPlotter.isContinuous(geo, tprec, t, 8)) {
					lineBuilder.append(
							StringUtil.format(template, xprec, yprec, x, y));
				}
			}
			yprec = y;
			tprec = t;
			xprec = x;
		}
		return lineBuilder;
	}

	/**
	 * @param s
	 *            expression
	 * @return whether it contains functions not plottable in LaTeX
	 */
	protected boolean isLatexFunction(String s) {
		String lowerExp = s.toLowerCase();
		return !lowerExp.contains("erf(")
				&& !lowerExp.contains("gamma(")
				&& !lowerExp.contains("gammaRegularized(")
				&& !lowerExp.contains("cbrt(")
				&& !lowerExp.contains("csc(")
				&& !lowerExp.contains("csch(")
				&& !lowerExp.contains("sec(")
				&& !lowerExp.contains("cot(")
				&& !lowerExp.contains("coth(")
				&& !lowerExp.contains("sech(")
				&& !lowerExp.contains("if");

	}

	protected void addTextPackage() {
		StringBuilder packages = new StringBuilder();
		if (codePreamble.indexOf("amssymb") == -1) {
			packages.append("amssymb,");
		}
		if (codePreamble.indexOf("fancyhdr") == -1) {
			packages.append("fancyhdr,");
		}
		if (codePreamble.indexOf("txfonts") == -1) {
			packages.append("txfonts,");
		}
		if (codePreamble.indexOf("pxfonts") == -1) {
			packages.append("pxfonts,");
		}
		if (packages.length() != 0) {
			packages.delete(packages.length() - 1, packages.length());
			codePreamble.append("\\usepackage{" + packages.toString() + "}\n");
		}
	}

	protected class Info {

		private double alpha;
		private int y;
		private double angle;
		private FillType fillType;
		private GColor linecolor;

		public Info(GeoElementND geo) {

			alpha = geo.getAlphaValue();
			y = geo.getHatchingDistance();
			angle = geo.getHatchingAngle();
			fillType = geo.getFillType();
			linecolor = geo.getObjectColor();

			if (geo.getParentAlgorithm() instanceof AlgoBarChart) {

				boolean setAlpha = false;

				ChartStyle algo = ((AlgoBarChart) geo.getParentAlgorithm()).getStyle();
				if (algo.getBarColor(barNumber) != null) {
					linecolor = algo.getBarColor(barNumber);
					setAlpha = true;
				}
				if (algo.getBarHatchDistance(barNumber) != -1) {
					y = algo.getBarHatchDistance(barNumber);
				}
				if (algo.getBarHatchAngle(barNumber) != -1) {
					angle = algo.getBarHatchAngle(barNumber);
				}
				if (algo.getBarFillType(barNumber) != null) {
					fillType = FillType.values()[algo.getBarFillType(barNumber)
							.ordinal()];
				}
				if (algo.getBarAlpha(barNumber) != -1 && setAlpha) {
					alpha = algo.getBarColor(barNumber).getAlpha() / 255.0;
				}
			}
		}

		public double getAlpha() {
			return alpha;
		}

		public int getY() {
			return y;
		}

		public double getAngle() {
			return angle;
		}

		public FillType getFillType() {
			return fillType;
		}

		public GColor getLinecolor() {
			return linecolor;
		}

	}

	private void drawCurveCartesian(GeoElementND geo) {
		if (!isLatexFunction(
				geo.toValueString(StringTemplate.noLocalDefault))) {
			GeoCurveCartesian curve = (GeoCurveCartesian) geo;
			Function f = curve.getFunX();
			if (f.getFunctionExpression().getOperation() == Operation.IF_LIST) {
				ListValue exl = (ListValue) f.getFunctionExpression().getLeft();
				ListValue exr = (ListValue) f.getFunctionExpression()
						.getRight();
				double[] paramValues = new double[exl.size() + 2];
				paramValues[0] = 0;
				for (int i = 0; i < exl.size(); i++) {
					paramValues[i + 1] = exl.getListElement(i).wrap().getRight()
							.evaluateDouble();
				}

				// extra if needed for eg when exrsv.length is one more than
				// exlsv.length
				// {t < 0.25, t < 0.5, t < 0.75}
				// ie add 1.0 to end
				paramValues[exl.size() + 1] = curve.getMaxParameter();

				GeoCurveCartesian[] curves = new GeoCurveCartesian[exr.size()];
				for (int i = 0; i < exr.size(); i++) {
					curves[i] = new GeoCurveCartesian(this.construction);
					curves[i].setFunctionX(asFunction(exr.getListElement(i)));
				}

				f = curve.getFunY();
				exr = (ListValue) f.getFunctionExpression().getRight();

				for (int i = 0; i < exr.size(); i++) {
					curves[i].setFunctionY(asFunction(exr.getListElement(i)));
					curves[i].setInterval(paramValues[i], paramValues[i + 1]);
					curves[i].setAllVisualProperties((GeoElement) geo, false);
				}
				boolean fill = fillSpline(curves);
				if (!fill) {
					for (int i = 0; i < curves.length; i++) {
						drawSingleCurveCartesian(curves[i], true);
					}
				}
			} else {
				StringBuilder lines = drawNoLatexFunction(curve, curve.getMaxParameter(),
						curve.getMinParameter(), 400, getLineTemplate(geo));
				code.append(lines);
			}
		} else {
			drawSingleCurveCartesian((GeoCurveCartesian) geo, true);
		}
	}

	private Function asFunction(ExpressionValue listElement) {
		return new Function(listElement.deepCopy(kernel).wrap(),
				new FunctionVariable(kernel, "t"));
	}

	protected String getLineTemplate(GeoElementND geo) {
		return "";
	}

	protected double firstDefinedValue(GeoFunction f, double a, double b) {
		double x = a;
		double step = (b - a) / 100;
		while (x <= b) {
			double y = f.value(x);
			if (!Double.isNaN(y)) {
				if (DoubleUtil.isEqual(x, a)) {
					return a;
				} else if (step < PRECISION_XRANGE_FUNCTION) {
					return x;
				} else {
					return firstDefinedValue(f, x - step, x);
				}
			}
			x += step;
		}
		return b;
	}

	protected double maxDefinedValue(GeoFunction f, double a, double b) {
		double x = a;
		double step1 = (b - a) / 100;
		while (x <= b) {
			double y = f.value(x);
			if (Double.isNaN(y)) {
				if (step1 < PRECISION_XRANGE_FUNCTION) {
					return x - step1;
				}
				return maxDefinedValue(f, x - step1, x);
			}
			x += step1;
		}
		return b;
	}

	protected static void renameFunc(StringBuilder sb, String nameFunc,
			String nameNew) {
		int ind = sb.indexOf(nameFunc);
		while (ind > -1) {
			sb.replace(ind, ind + nameFunc.length(), nameNew);
			ind = sb.indexOf(nameFunc);
		}
	}

	/**
	 * @param st
	 *            multiline string
	 * @param sb
	 *            output builder (gets filled with st using LaTeX linebreaks \\
	 *            instead of \n)
	 * @param font
	 *            font
	 * @return estimated max width
	 */
	protected int getWidth(String st, StringBuilder sb, GFont font) {
		int width = 0;
		String[] lines = st.split("\n");

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			width = Math.max(width, (int) Math.ceil(
					StringUtil.getPrototype().estimateLength(line, font)));
			sb.append(line);
			if (i < lines.length - 1) {
				sb.append(" \\\\ ");
			}
		}
		return width;
	}

	protected boolean drawAngleAs(GeoAngle geo, int rightAngleStyleDot) {
		return DoubleUtil.isEqual(geo.getValue(), Kernel.PI_HALF)
				&& geo.isEmphasizeRightAngle()
				&& euclidianView.getRightAngleStyle() == rightAngleStyleDot;
	}

}