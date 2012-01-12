/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.euclidian;

import geogebra.common.awt.Font;
import geogebra.common.euclidian.DrawLine;
import geogebra.common.euclidian.DrawList;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.DrawableList;
import geogebra.common.euclidian.DrawableList.DrawableIterator;
import geogebra.common.euclidian.DrawBoxPlot;
import geogebra.common.euclidian.DrawConic;
import geogebra.common.euclidian.DrawConicPart;
import geogebra.common.euclidian.DrawImplicitPoly;
import geogebra.common.euclidian.DrawInequality;
import geogebra.common.euclidian.DrawIntegral;
import geogebra.common.euclidian.DrawIntegralFunctions;
import geogebra.common.euclidian.DrawLocus;
import geogebra.common.euclidian.DrawParametricCurve;
import geogebra.common.euclidian.DrawPoint;
import geogebra.common.euclidian.DrawPolyLine;
import geogebra.common.euclidian.DrawPolygon;
import geogebra.common.euclidian.DrawRay;
import geogebra.common.euclidian.DrawSegment;
import geogebra.common.euclidian.DrawSlider;
import geogebra.common.euclidian.DrawSlope;
import geogebra.common.euclidian.DrawText;
import geogebra.common.euclidian.DrawUpperLowerSum;
import geogebra.common.euclidian.DrawVector;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.GetViewId;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.factories.AwtFactory;
import geogebra.common.factories.FormatFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.algos.AlgoBoxPlot;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import geogebra.common.kernel.algos.AlgoIntegralFunctions;
import geogebra.common.kernel.algos.AlgoSlope;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.ParametricCurve;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.MyMath;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.main.Application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;

import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Point;
import geogebra.common.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

/**
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class EuclidianView extends AbstractEuclidianView implements EuclidianViewInterface,
		Printable, SettingListener {

	protected static final long serialVersionUID = 1L;
	


	// STROKES

	// protected static MyBasicStroke thinStroke = new MyBasicStroke(1.0f);

	// axes strokes
	protected static geogebra.common.awt.BasicStroke defAxesStroke = 
			new geogebra.awt.BasicStroke( new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

	
	//protected Line2D.Double tempLine = new Line2D.Double();
	protected Ellipse2D.Double circle = new Ellipse2D.Double(); // polar grid
																// circles

	protected EuclidianViewJPanel evjpanel;

	
	protected static RenderingHints defRenderingHints = new RenderingHints(null);
	{
		defRenderingHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		defRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		defRenderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);

		// This ensures fast image drawing. Note that DrawImage changes
		// this hint for scaled and sheared images to improve their quality
		defRenderingHints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	}

	


	// axesNumberingDistances /
	// 2

	// added by Loic BEGIN
	// right angle
	// int rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_SQUARE;

	// END


	

	

	// temp
	// public static final int DRAW_MODE_DIRECT_DRAW = 0;
	// public static final int DRAW_MODE_BACKGROUND_IMAGE = 1;

	
	protected Image resetImage, playImage, pauseImage, upArrowImage,
			downArrowImage;

	// temp image
	private final Graphics2D g2Dtemp = new BufferedImage(5, 5,
			BufferedImage.TYPE_INT_RGB).createGraphics();
	// public Graphics2D lastGraphics2D;

	protected Cursor defaultCursor;

	// set EuclidianView no - 2 for 2nd EulidianView, 1 for 1st EuclidianView
	// and Applet
	// EVNO_GENERAL for others

	public EuclidianView(AbstractEuclidianController ec, boolean[] showAxes,
			boolean showGrid, EuclidianSettings settings) {
		this(ec, showAxes, showGrid, 1, settings);
	}

	/**
	 * Creates EuclidianView
	 * 
	 * @param ec
	 *            controller
	 * @param showAxes
	 * @param showGrid
	 * @param evno
	 *            number of this view
	 */
	public EuclidianView(AbstractEuclidianController ec, boolean[] showAxes,
			boolean showGrid, int evno, EuclidianSettings settings) {

		super(ec, settings);

		evNo = evno;
		setApplication(((EuclidianController)ec).getApplication());

		evjpanel = new EuclidianViewJPanel(this);

		this.showAxes[0] = showAxes[0];
		this.showAxes[1] = showAxes[1];
		this.showGrid = showGrid;

		

		printScaleNF = FormatFactory.prototype.getNumberFormat();
		printScaleNF.setGroupingUsed(false);
		printScaleNF.setMaximumFractionDigits(5);

		// algebra controller will take care of our key events
		evjpanel.setFocusable(true);

		evjpanel.setLayout(null);
		evjpanel.setMinimumSize(new Dimension(20, 20));
		((EuclidianController)euclidianController).setView(this);
		((EuclidianController)euclidianController).setPen(new EuclidianPen((Application)getApplication(), this));

		attachView();

		// register Listener
		evjpanel.addMouseMotionListener((EuclidianController)euclidianController);
		evjpanel.addMouseListener((EuclidianController)euclidianController);
		evjpanel.addMouseWheelListener((EuclidianController)euclidianController);
		evjpanel.addComponentListener((EuclidianController)euclidianController);

		
		initView(false);

		// updateRightAngleStyle(app.getLocale());

		// enable drop transfers
		evjpanel.setTransferHandler(new EuclidianViewTransferHandler(this));

		// settings from XML for EV1, EV2
		// not for eg probability calculator
		if ((evNo == 1) || (evNo == 2)) {
			EuclidianSettings es = getApplication().getSettings().getEuclidian(evNo);
			settingsChanged(es);
			es.addListener(this);
		}
	}

	@Override
	public Application getApplication() {
		return (Application)application;
	}

	public geogebra.common.euclidian.EuclidianStyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = new EuclidianStyleBar(this);
		}

		return styleBar;
	}

	public boolean hasStyleBar() {
		return styleBar != null;
	}

	public void updateRightAngleStyle(Locale locale) {
		// change rightAngleStyle for German to
		// EuclidianView.RIGHT_ANGLE_STYLE_DOT
		if (getRightAngleStyle() != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE) {
			if (locale.getLanguage().equals("de")
					|| locale.getLanguage().equals("hu")) {
				setRightAngleStyle(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT);
			} else {
				setRightAngleStyle(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE);
			}
		}
	}

	protected void initView(boolean repaint) {
		// preferred size
		evjpanel.setPreferredSize(null);

		// init grid's line type
		setGridLineStyle(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		setAxesLineStyle(EuclidianStyleConstants.AXES_LINE_TYPE_ARROW);
		setAxesColor(geogebra.common.awt.Color.black); // Michael Borcherds 2008-01-26 was darkgray
		setGridColor(geogebra.common.awt.Color.lightGray);
		setBackground(geogebra.common.awt.Color.white);

		// showAxes = true;
		// showGrid = false;
		pointCapturingMode = EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;

		// added by Loic BEGIN
		// app.rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_SQUARE;
		// END

		showAxesNumbers[0] = true;
		showAxesNumbers[1] = true;
		axesLabels[0] = null;
		axesLabels[1] = null;
		axesUnitLabels[0] = null;
		axesUnitLabels[1] = null;
		piAxisUnit[0] = false;
		piAxisUnit[1] = false;
		axesTickStyles[0] = EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR;
		axesTickStyles[1] = EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR;

		// for axes labeling with numbers
		automaticAxesNumberingDistances[0] = true;
		automaticAxesNumberingDistances[1] = true;

		// distances between grid lines
		automaticGridDistance = true;

		setStandardCoordSystem(repaint);
	}

	public boolean hasPreferredSize() {
		Dimension prefSize = evjpanel.getPreferredSize();

		return (prefSize != null) && (prefSize.width > MIN_WIDTH)
				&& (prefSize.height > MIN_HEIGHT);
	}

	protected void resetLists() {
		DrawableMap.clear();
		stickyPointList.clear();
		allDrawableList.clear();
		bgImageList.clear();

		for (int i = 0; i <= getApplication().maxLayerUsed; i++) {
			drawLayers[i].clear(); // Michael Borcherds 2008-02-29
		}

		setToolTipText(null);
	}

	/*
	 * public void detachView() { kernel.detach(this); clearView();
	 * //kernel.notifyRemoveAll(this); }
	 */

	/**
	 * Returns the bounding box of all Drawable objects in this view in screen
	 * coordinates.
	 */
	//@Override
	public Rectangle getBounds() {
		Rectangle result = null;
		
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			Rectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null) {
					result = AwtFactory.prototype.newRectangle(bb); // changed () to (bb) bugfix,
												// otherwise top-left of screen
												// is always included
				}
				// add bounding box of list element
				result.add(bb);
			}
		}

		// Cong Liu
		if (result == null) {
			result = AwtFactory.prototype.newRectangle(0, 0, 0, 0);
		}
		return result;
	}

	//


	//@Override
	public void setToolTipText(String plain) {
		if ((tooltipsInThisView == EuclidianStyleConstants.TOOLTIPS_ON)
				|| (tooltipsInThisView == EuclidianStyleConstants.TOOLTIPS_AUTOMATIC)) {
			evjpanel.setToolTipText(plain);
		}
	}

	// added by Loic BEGIN
	
	// END
	final void addBackgroundImage(DrawImage img) {
		bgImageList.addUnique(img);
		// drawImageList.remove(img);

		// Michael Borcherds 2008-02-29
		int layer = img.getGeoElement().getLayer();
		drawLayers[layer].remove(img);
	}

	final void removeBackgroundImage(DrawImage img) {
		bgImageList.remove(img);
		// drawImageList.add(img);

		// Michael Borcherds 2008-02-29
		int layer = img.getGeoElement().getLayer();
		drawLayers[layer].add(img);
	}

	
	
	public void setDragCursor() {

		if (((Application)getApplication()).useTransparentCursorWhenDragging) {
			setCursor(((Application)getApplication()).getTransparentCursor());
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

	}

	public void setMoveCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	public void setResizeXAxisCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
	}

	public void setResizeYAxisCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
	}

	public void setHitCursor() {
		if (defaultCursor == null) {
			setCursor(Cursor.getDefaultCursor());
		} else {
			setCursor(defaultCursor);
		}
	}

	public void setDefaultCursor() {
		if (defaultCursor == null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else {
			setCursor(defaultCursor);
		}
	}

	@Override
	protected void initCursor() {
		defaultCursor = null;

		switch (mode) {
		case EuclidianConstants.MODE_ZOOM_IN:
			defaultCursor = getCursorForImage(((Application)getApplication())
					.getInternalImage("cursor_zoomin.gif"));
			break;

		case EuclidianConstants.MODE_ZOOM_OUT:
			defaultCursor = getCursorForImage(((Application)getApplication())
					.getInternalImage("cursor_zoomout.gif"));
			break;
		}

		setDefaultCursor();
	}

	protected Cursor getCursorForImage(Image image) {
		if (image == null) {
			return null;
		}

		// Query for custom cursor support
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getBestCursorSize(32, 32);
		int colors = tk.getMaximumCursorColors();
		if (!d.equals(new Dimension(0, 0)) && (colors != 0)) {
			// load cursor image
			if (image != null) {
				try {
					// Create custom cursor from the image
					Cursor cursor = tk.createCustomCursor(image,
						((geogebra.awt.Point)AwtFactory.prototype.newPoint(16, 16)).getAwtPoint(), "custom cursor");
					return cursor;
				} catch (Exception exc) {
					// Catch exceptions so that we don't try to set a null
					// cursor
					AbstractApplication
							.debug("Unable to create custom cursor.");
				}
			}
		}
		return null;
	}

	
	

	

	

	public void setPreview(Previewable p) {
		if (previewDrawable != null) {
			previewDrawable.disposePreview();
		}
		previewDrawable = p;
	}

	
	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	final public void setAnimatedRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax, int steps, boolean storeUndo) {
		if (zoomerRW == null) {
			zoomerRW = new MyZoomerRW();
		}
		zoomerRW.init(xmin, xmax, ymin, ymax, steps, storeUndo);
		zoomerRW.startAnimation();
	}

	protected MyZoomerRW zoomerRW;

	int widthTemp, heightTemp;
	double xminTemp, xmaxTemp, yminTemp, ymaxTemp;

	final public void setTemporaryCoordSystemForExport() {
		widthTemp = getWidth();
		heightTemp = getHeight();
		xminTemp = getXmin();
		xmaxTemp = getXmax();
		yminTemp = getYmin();
		ymaxTemp = getYmax();

		try {
			GeoPoint2 export1 = (GeoPoint2) getApplication().getKernel().lookupLabel(
					AbstractEuclidianView.EXPORT1);
			GeoPoint2 export2 = (GeoPoint2) getApplication().getKernel().lookupLabel(
					AbstractEuclidianView.EXPORT2);

			if ((export1 == null) || (export2 == null)) {
				return;
			}

			double[] xy1 = new double[2];
			double[] xy2 = new double[2];
			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);

			setRealWorldCoordSystem(Math.min(xy1[0], xy2[0]),
					Math.max(xy1[0], xy2[0]), Math.min(xy1[1], xy2[1]),
					Math.max(xy1[1], xy2[1]));

		} catch (Exception e) {
			restoreOldCoordSystem();
		}
	}

	final public void restoreOldCoordSystem() {
		setWidth(widthTemp);
		setHeight(heightTemp);
		setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
	}

	
	public int getWidth() {
		return evjpanel.getWidth();
	}
	
	public int getHeight() {
		return evjpanel.getHeight();
	}

	/*
	 * used for rescaling applets when the reset button is hit use
	 * setTemporarySize(-1, -1) to disable
	 */
	public void setTemporarySize(int w, int h) {
		setWidth(w);
		setHeight(h);
		updateSize();
	}

	@Override
	public void updateSize() {

		// record the old coord system

		setWidth(getWidth());
		setHeight(getHeight());
		if ((getWidth() <= 0) || (getHeight() <= 0)) {
			return;
		}

		// real world values
		setRealWorldBounds();

		// ================================================
		// G.Sturr 8/27/10: test: rescale on window resize
		//
		// reset the coord system so that our view dimensions are restored
		// using the new scaling factors.

		// setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);

		GraphicsConfiguration gconf = evjpanel.getGraphicsConfiguration();
		try {
			createImage(gconf);
		} catch (OutOfMemoryError e) {
			bgImage = null;
			bgGraphics = null;
			System.gc();
		}

		updateBackgroundImage();
		updateAllDrawables(true);
	}

	private void createImage(GraphicsConfiguration gc) {
		if (gc != null) {
			bgImage = new geogebra.awt.BufferedImage(gc.createCompatibleImage(getWidth(), getHeight()));
			bgGraphics = bgImage.createGraphics();
			if (antiAliasing) {
				setAntialiasing(bgGraphics);
			}
		}
	}

	// move view:
	/*
	 * protected void setDrawMode(int mode) { if (mode != drawMode) { drawMode =
	 * mode; if (mode == DRAW_MODE_BACKGROUND_IMAGE) updateBackgroundImage(); }
	 * }
	 */

	/**
	 * change showing flag of the axis
	 * 
	 * @param axis
	 *            id of the axis
	 * @param flag
	 *            show/hide
	 * @param update
	 *            update (or not) the background image
	 */
	public void setShowAxis(int axis, boolean flag, boolean update) {
		if (flag == showAxes[axis]) {
			return;
		}

		showAxes[axis] = flag;

		if (update) {
			updateBackgroundImage();
		}

	}

	public void setShowAxes(boolean flag, boolean update) {
		setShowAxis(AXIS_X, flag, false);
		setShowAxis(AXIS_Y, flag, true);
	}

	/**
	 * sets the visibility of x and y axis
	 * 
	 * @param xAxis
	 * @param yAxis
	 * @deprecated use
	 *             {@link EuclidianViewInterface#setShowAxes(boolean, boolean)}
	 *             or
	 *             {@link EuclidianViewInterface#setShowAxis(int, boolean, boolean)}
	 *             instead
	 */
	@Deprecated
	public void showAxes(boolean xAxis, boolean yAxis) {

		/*
		 * if (xAxis == showAxes[0] && yAxis == showAxes[1]) return;
		 * 
		 * showAxes[0] = xAxis; showAxes[1] = yAxis; updateBackgroundImage();
		 */

		setShowAxis(AXIS_X, xAxis, false);
		setShowAxis(AXIS_Y, yAxis, true);

	}

	public void setDefRenderingHints(geogebra.common.awt.Graphics2D g2){
		g2.setRenderingHints(defRenderingHints);
	}

		public static void setAntialiasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	
	@Override
	public void setAntialiasing(geogebra.common.awt.Graphics2D g2) {
		setAntialiasing(geogebra.awt.Graphics2D.getAwtGraphics(g2));
	}

	

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0) {
			return (NO_SUCH_PAGE);
		} 
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform oldTransform = g2d.getTransform();

			g2d.translate(pageFormat.getImageableX(),
					pageFormat.getImageableY());

			// construction title
			int y = 0;
			Construction cons = kernel.getConstruction();
			String title = cons.getTitle();
			if (!title.equals("")) {
				Font titleFont = ((Application)getApplication()).getBoldFontCommon().deriveFont(Font.BOLD,
						((Application)getApplication()).getBoldFont().getSize() + 2);
				g2d.setFont(geogebra.awt.Font.getAwtFont(titleFont));
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getAscent();
				g2d.drawString(title, 0, y);
			}

			// construction author and date
			String author = cons.getAuthor();
			String date = cons.getDate();
			String line = null;
			if (!author.equals("")) {
				line = author;
			}
			if (!date.equals("")) {
				if (line == null) {
					line = date;
				} else {
					line = line + " - " + date;
				}
			}

			// scale string:
			// Scale in cm: 1:1 (x), 1:2 (y)
			String scaleString = null;
			if (((Application)getApplication()).isPrintScaleString()) {
				StringBuilder sb = new StringBuilder(
						getApplication().getPlain("ScaleInCentimeter"));
				if (printingScale <= 1) {
					sb.append(": 1:");
					sb.append(printScaleNF.format(1 / printingScale));
				} else {
					sb.append(": ");
					sb.append(printScaleNF.format(printingScale));
					sb.append(":1");
				}

				// add yAxis scale too?
				if (getScaleRatio() != 1.0) {
					sb.append(" (x), ");
					double yPrintScale = (printingScale * getYscale()) / getXscale();
					if (yPrintScale < 1) {
						sb.append("1:");
						sb.append(printScaleNF.format(1 / yPrintScale));
					} else {
						sb.append(printScaleNF.format(yPrintScale));
						sb.append(":1");
					}
					sb.append(" (y)");
				}
				scaleString = sb.toString();
			}

			if (scaleString != null) {
				if (line == null) {
					line = scaleString;
				} else {
					line = line + " - " + scaleString;
				}
			}

			if (line != null) {
				g2d.setFont(((Application)getApplication()).getPlainFont());
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getHeight();
				g2d.drawString(line, 0, y);
			}
			if (y > 0) {
				g2d.translate(0, y + 20); // space between title and drawing
			}

			double scale = (PRINTER_PIXEL_PER_CM / getXscale()) * printingScale;
			exportPaint(g2d, scale);

			// clear page margins at bottom and right
			double pagewidth = pageFormat.getWidth();
			double pageheight = pageFormat.getHeight();
			double xmargin = pageFormat.getImageableX();
			double ymargin = pageFormat.getImageableY();

			g2d.setTransform(oldTransform);
			g2d.setClip(null);
			g2d.setPaint(Color.white);

			Rectangle2D.Double rect = new Rectangle2D.Double();
			rect.setFrame(0, pageheight - ymargin, pagewidth, ymargin);
			g2d.fill(rect);
			rect.setFrame(pagewidth - xmargin, 0, xmargin, pageheight);
			g2d.fill(rect);

			System.gc();
			return (PAGE_EXISTS);
		
	}

	public void exportPaint(Graphics2D g2d, double scale) {
		exportPaint(new geogebra.awt.Graphics2D(g2d), scale, false);
	}

	/**
	 * Scales construction and draws it to g2d.
	 * 
	 * @param g2d
	 * @param scale
	 * 
	 * @param transparency
	 *            states if export should be optimized for eps. Note: if this is
	 *            set to false, no traces are drawn.
	 * 
	 */
	public void exportPaint(geogebra.common.awt.Graphics2D g2d, double scale, boolean transparency) {
		((Application)getApplication()).exporting = true;
		exportPaintPre(g2d, scale, transparency);
		drawObjects(g2d);
		((Application)getApplication()).exporting = false;
	}

	public void exportPaintPre(geogebra.common.awt.Graphics2D g2d, double scale) {
		exportPaintPre(g2d, scale, false);
	}

	public void exportPaintPre(geogebra.common.awt.Graphics2D g2d, double scale,
			boolean transparency) {
		g2d.scale(scale, scale);

		// clipping on selection rectangle
		if (selectionRectangle != null) {
			Rectangle rect = selectionRectangle;
			g2d.setClip(0, 0, (int)rect.getWidth(), (int)rect.getHeight());
			g2d.translate(-rect.getX(), -rect.getY());
			// Application.debug(rect.x+" "+rect.y+" "+rect.width+" "+rect.height);
		} else {
			// use points Export_1 and Export_2 to define corner
			try {
				// Construction cons = kernel.getConstruction();
				GeoPoint2 export1 = (GeoPoint2) kernel.lookupLabel(EXPORT1);
				GeoPoint2 export2 = (GeoPoint2) kernel.lookupLabel(EXPORT2);
				double[] xy1 = new double[2];
				double[] xy2 = new double[2];
				export1.getInhomCoords(xy1);
				export2.getInhomCoords(xy2);
				double x1 = xy1[0];
				double x2 = xy2[0];
				double y1 = xy1[1];
				double y2 = xy2[1];
				x1 = (x1 / getInvXscale()) + getxZero();
				y1 = getyZero() - (y1 / getInvYscale());
				x2 = (x2 / getInvXscale()) + getxZero();
				y2 = getyZero() - (y2 / getInvYscale());
				int x = (int) Math.min(x1, x2);
				int y = (int) Math.min(y1, y2);
				int exportWidth = (int) Math.abs(x1 - x2) + 2;
				int exportHeight = (int) Math.abs(y1 - y2) + 2;

				g2d.setClip(0, 0, exportWidth, exportHeight);
				g2d.translate(-x, -y);
			} catch (Exception e) {
				// or take full euclidian view
				g2d.setClip(0, 0, getWidth(), getHeight());
			}
		}

		// DRAWING
		if (isTracing() || hasBackgroundImages()) {
			// draw background image to get the traces
			if (bgImage == null) {
				drawBackgroundWithImages(g2d, transparency);
			} else {
				geogebra.awt.Graphics2D.getAwtGraphics(g2d).drawImage(geogebra.awt.BufferedImage.getAwtBufferedImage(bgImage), 0, 0, (JPanel)evjpanel);
			}
		} else {
			// just clear the background if transparency is disabled (clear =
			// draw background color)
			drawBackground(g2d, !transparency);
		}

		geogebra.awt.Graphics2D.getAwtGraphics(g2d).setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		setAntialiasing(g2d);
	}

	/**
	 * Tells if there are any traces in the background image.
	 * 
	 * @return true if there are any traces in background
	 */
	protected boolean isTracing() {
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			if (it.next().isTracing()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tells if there are any images in the background.
	 * 
	 * @return whether there are any images in the background.
	 */
	protected boolean hasBackgroundImages() {
		return bgImageList.size() > 0;
	}

	/**
	 * Returns image of drawing pad sized according to the given scale factor.
	 * 
	 * @param scale
	 * @return image of drawing pad sized according to the given scale factor.
	 * @throws OutOfMemoryError
	 */
	public BufferedImage getExportImage(double scale) throws OutOfMemoryError {
		return getExportImage(scale, false);
	}

	public BufferedImage getExportImage(double scale, boolean transparency)
			throws OutOfMemoryError {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);
		BufferedImage img = createBufferedImage(width, height, transparency);
		exportPaint(new geogebra.awt.Graphics2D(img.createGraphics()), scale, transparency);
		img.flush();
		return img;
	}

	protected BufferedImage createBufferedImage(int width, int height) {
		return createBufferedImage(width, height, false);
	}

	protected BufferedImage createBufferedImage(int width, int height,
			boolean transparency) throws OutOfMemoryError {

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		GraphicsDevice gs = ge.getDefaultScreenDevice();

		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		BufferedImage bufImg = gc
				.createCompatibleImage(width, height,
						(transparency ? Transparency.TRANSLUCENT
								: Transparency.BITMASK));

		// Graphics2D g = (Graphics2D)bufImg.getGraphics();

		// g.setBackground(new Color(0,0,0,0));

		// g.clearRect(0,0,width,height);

		return bufImg;

	}

	@Override
	final public geogebra.common.awt.Graphics2D getBackgroundGraphics() {
		return bgGraphics;
	}




	protected void drawResetIcon(geogebra.common.awt.Graphics2D g){
		// need to use getApplet().width rather than width so that
					// it works with applet rescaling
					int w = ((Application)getApplication()).onlyGraphicsViewShowing() ? ((Application)getApplication()).getApplet().width
							: getWidth() + 2;
					geogebra.awt.Graphics2D.getAwtGraphics(g).drawImage(getResetImage(), w - 18, 2, null);
	}
	private Image getResetImage() {
		if (resetImage == null) {
			resetImage = ((Application)getApplication()).getRefreshViewImage();
		}
		return resetImage;
	}

	private Image getPlayImage() {
		if (playImage == null) {
			playImage = ((Application)getApplication()).getPlayImage();
		}
		return playImage;
	}

	private Image getPauseImage() {
		if (pauseImage == null) {
			pauseImage = ((Application)getApplication()).getPauseImage();
		}
		return pauseImage;
	}

	

	

	// =================================================
	// Draw Axes
	// =================================================

	// G.Sturr: 2010-8-9
	// Modified drawAxes() to allow variable
	// crossing points and positive-only axes



	/*
	 * #******************************************** drawAxes
	 * ********************************************
	 */
	
	/**
	 * Finds maximum pixel width and height needed to draw current x and y axis
	 * labels. return[0] = max width, return[1] = max height
	 * 
	 * @param g2
	 * @return point (width,height)
	 */
	public Point getMaximumLabelSize(geogebra.common.awt.Graphics2D g2) {

		Point max = new Point(0, 0);

		g2.setFont(getFontAxes());
		FontRenderContext frc = geogebra.awt.Graphics2D.getAwtGraphics(g2).getFontRenderContext();

		int yAxisHeight = positiveAxes[1] ? (int) getyZero() : getHeight();
		int maxY = positiveAxes[1] ? (int) getyZero() : getHeight() - SCREEN_BORDER;

		double rw = getYmax() - (getYmax() % axesNumberingDistances[1]);
		double pix = getyZero() - (rw * getYscale());
		double axesStep = getYscale() * axesNumberingDistances[1]; // pixelstep

		for (; pix <= yAxisHeight; rw -= axesNumberingDistances[1], pix += axesStep) {
			if (pix <= maxY) {
				if (showAxesNumbers[1]) {
					String strNum = kernel.formatPiE(rw, axesNumberFormat[1]);

					sb.setLength(0);
					sb.append(strNum);
					if ((axesUnitLabels[1] != null) && !piAxisUnit[1]) {
						sb.append(axesUnitLabels[1]);
					}

					TextLayout layout = new TextLayout(sb.toString(), geogebra.awt.Font.getAwtFont(getFontAxes()),
							frc);
					// System.out.println(layout.getAdvance() + " : " + sb);
					if (max.x < layout.getAdvance()) {
						max.x = (int) layout.getAdvance();
					}
				}
			}
		}
		FontMetrics fm = geogebra.awt.Graphics2D.getAwtGraphics(g2).getFontMetrics();
		max.y += fm.getAscent();

		return max;
	}

	final protected void drawGrid(geogebra.common.awt.Graphics2D g2) {
		g2.setColor(gridColor);
		g2.setStroke(gridStroke);

		// vars for handling positive-only axes
		double xCrossPix = this.getxZero() + (axisCross[1] * getXscale());
		double yCrossPix = this.getyZero() - (axisCross[0] * getYscale());
		int yAxisEnd = positiveAxes[1] ? (int) yCrossPix : getHeight();
		int xAxisStart = positiveAxes[0] ? (int) xCrossPix : 0;

		// set the clipping region to the region defined by the axes
		Shape oldClip = geogebra.awt.Graphics2D.getAwtGraphics(g2).getClip();
		if (gridType != GRID_POLAR) {
			g2.setClip(xAxisStart, 0, getWidth(), yAxisEnd);
		}

		switch (gridType) {

		case GRID_CARTESIAN:

			// vertical grid lines
			double tickStep = getXscale() * gridDistances[0];
			double start = getxZero() % tickStep;
			double pix = start;

			for (int i = 0; pix <= getWidth(); i++) {
				// int val = (int) Math.round(i);
				// g2.drawLine(val, 0, val, height);
				tempLine.setLine(pix, 0, pix, getHeight());
				g2.draw(tempLine);

				pix = start + (i * tickStep);
			}

			// horizontal grid lines
			tickStep = getYscale() * gridDistances[1];
			start = getyZero() % tickStep;
			pix = start;

			for (int j = 0; pix <= getHeight(); j++) {
				// int val = (int) Math.round(j);
				// g2.drawLine(0, val, width, val);
				tempLine.setLine(0, pix, getWidth(), pix);
				g2.draw(tempLine);

				pix = start + (j * tickStep);
			}

			break;

		case GRID_ISOMETRIC:

			double tickStepX = getXscale() * gridDistances[0] * Math.sqrt(3.0);
			double startX = getxZero() % (tickStepX);
			double startX2 = getxZero() % (tickStepX / 2);
			double tickStepY = getYscale() * gridDistances[0];
			double startY = getyZero() % tickStepY;

			// vertical
			pix = startX2;
			for (int j = 0; pix <= getWidth(); j++) {
				tempLine.setLine(pix, 0, pix, getHeight());
				g2.draw(tempLine);
				pix = startX2 + ((j * tickStepX) / 2.0);
			}

			// extra lines needed because it's diagonal
			int extra = (int) ((((getHeight() * getXscale()) / getYscale()) * Math.sqrt(3.0)) / tickStepX) + 3;

			// positive gradient
			pix = startX + (-(extra + 1) * tickStepX);
			for (int j = -extra; pix <= getWidth(); j += 1) {
				tempLine.setLine(
						pix,
						startY - tickStepY,
						pix
								+ (((getHeight() + tickStepY) * Math.sqrt(3) * getXscale()) / getYscale()),
						(startY - tickStepY) + getHeight() + tickStepY);
				g2.draw(tempLine);
				pix = startX + (j * tickStepX);
			}

			// negative gradient
			pix = startX;
			for (int j = 0; pix <= (getWidth() + ((((getHeight() * getXscale()) / getYscale()) + tickStepY) * Math
					.sqrt(3.0))); j += 1)
			// for (int j=0; j<=kk; j+=1)
			{
				tempLine.setLine(
						pix,
						startY - tickStepY,
						pix
								- (((getHeight() + tickStepY) * Math.sqrt(3) * getXscale()) / getYscale()),
						(startY - tickStepY) + getHeight() + tickStepY);
				g2.draw(tempLine);
				pix = startX + (j * tickStepX);
			}

			break;

		case GRID_POLAR: // G.Sturr 2010-8-13

			// find minimum grid radius
			double min;
			if ((getxZero() > 0) && (getxZero() < getWidth()) && (getyZero() > 0)
					&& (getyZero() < getHeight())) {
				// origin onscreen: min = 0
				min = 0;
			} else {
				// origin offscreen: min = distance to closest screen border
				double minW = Math
						.min(Math.abs(getxZero()), Math.abs(getxZero() - getWidth()));
				double minH = Math.min(Math.abs(getyZero()),
						Math.abs(getyZero() - getHeight()));
				min = Math.min(minW, minH);
			}

			// find maximum grid radius
			// max = max distance of origin to screen corners
			double d1 = MyMath.length(getxZero(), getyZero()); // upper left
			double d2 = MyMath.length(getxZero(), getyZero() - getHeight()); // lower left
			double d3 = MyMath.length(getxZero() - getWidth(), getyZero()); // upper right
			double d4 = MyMath.length(getxZero() - getWidth(), getyZero() - getHeight()); // lower
																		// right
			double max = Math.max(Math.max(d1, d2), Math.max(d3, d4));

			// draw the grid circles
			// note: x tick intervals are used for the radius intervals,
			// it is assumed that the x/y scaling ratio is 1:1
			double tickStepR = getXscale() * gridDistances[0];
			double r = min - (min % tickStepR);
			while (r <= max) {
				circle.setFrame(getxZero() - r, getyZero() - r, 2 * r, 2 * r);
				g2.draw(new geogebra.awt.GenericShape(circle));
				r = r + tickStepR;

			}

			// draw the radial grid lines
			double angleStep = gridDistances[2];
			double y1,
			y2,
			m;

			// horizontal axis
			tempLine.setLine(0, getyZero(), getWidth(), getyZero());
			g2.draw(tempLine);

			// radial lines
			for (double a = angleStep; a < Math.PI; a = a + angleStep) {

				if (Math.abs(a - (Math.PI / 2)) < 0.0001) {
					// vertical axis
					tempLine.setLine(getxZero(), 0, getxZero(), getHeight());
				} else {
					m = Math.tan(a);
					y1 = (m * (getxZero())) + getyZero();
					y2 = (m * (getxZero() - getWidth())) + getyZero();
					tempLine.setLine(0, y1, getWidth(), y2);
				}
				g2.draw(tempLine);
			}

			break;
		}

		// reset the clipping region
		g2.setClip(oldClip);
	}

	


	final protected void drawAnimationButtons(geogebra.common.awt.Graphics2D g2) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return;
		}

		int x = 6;
		int y = getHeight() - 22;

		if (highlightAnimationButtons) {
			// draw filled circle to highlight button
			g2.setColor(geogebra.common.awt.Color.darkGray);
		} else {
			g2.setColor(geogebra.common.awt.Color.lightGray);
		}

		g2.setStroke(EuclidianStatic.getDefaultStroke());

		// draw pause or play button
		g2.drawRect(x - 2, y - 2, 18, 18);
		Image img = kernel.isAnimationRunning() ? getPauseImage()
				: getPlayImage();
		geogebra.awt.Graphics2D.getAwtGraphics(g2).drawImage(img, x, y, null);
	}

	public final boolean hitAnimationButton(MouseEvent e) {
		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return false;
		}

		return kernel.needToShowAnimationButton() && (e.getX() <= 20)
				&& (e.getY() >= (getHeight() - 20));
	}

	private boolean drawPlayButtonInThisView() {

		// just one view
		if ( getApplication().getGuiManager() == null) {
			return true;
		}
		GetViewId evp;
		// eg ev1 just closed
		if ((evp = ((Application)getApplication()).getGuiManager().getLayout().getDockManager().getFocusedEuclidianPanel()) == null) {
			return true;
		}

		return !((getApplication().getGuiManager() != null) && (this.getViewID() != evp
				.getViewId()));
	}

	/**
	 * Updates highlighting of animation buttons.
	 * 
	 * @return whether status was changed
	 */
	public final boolean setAnimationButtonsHighlighted(boolean flag) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return false;
		}

		if (flag == highlightAnimationButtons) {
			return false;
		} else {
			highlightAnimationButtons = flag;
			return true;
		}
	}

	

	

		public void drawActionObjects(geogebra.common.awt.Graphics2D g2){
		// TODO layers for Buttons and Textfields
			// for cross-platform UI the stroke must be reset to show buttons
			// properly, see #442
			g2.setStroke(EuclidianStatic.getDefaultStroke());
				((geogebra.euclidian.EuclidianViewJPanel)evjpanel).paintChildren(
						geogebra.awt.Graphics2D.getAwtGraphics(g2)); // draws Buttons and Textfields
	}
	
	/*
	 * protected void drawObjects(Graphics2D g2, int layer) { // draw images
	 * drawImageList.drawAll(g2);
	 * 
	 * // draw HotEquations // all in layer 0 currently // layer -1 means draw
	 * all if (layer == 0 || layer == -1) paintChildren(g2);
	 * 
	 * // draw Geometric objects drawGeometricObjects(g2, layer); }
	 */

	/**
	 * Draws all GeoElements except images.
	 * 
	 * protected void drawGeometricObjects(Graphics2D g2, int layer) {
	 * 
	 * if (previewDrawable != null && (layer == app.getMaxLayer() || layer ==
	 * -1)) { // Michael Borcherds 2008-02-26 only draw once
	 * previewDrawable.drawPreview(g2); }
	 * 
	 * // draw lists of objects drawListList.drawAll(g2);
	 * 
	 * // draw polygons drawPolygonList.drawAll(g2);
	 * 
	 * // draw conics drawConicList.drawAll(g2);
	 * 
	 * // draw angles and numbers drawNumericList.drawAll(g2);
	 * 
	 * // draw functions drawFunctionList.drawAll(g2);
	 * 
	 * // draw lines drawLineList.drawAll(g2);
	 * 
	 * // draw segments drawSegmentList.drawAll(g2);
	 * 
	 * // draw vectors drawVectorList.drawAll(g2);
	 * 
	 * // draw locus drawLocusList.drawAll(g2);
	 * 
	 * // draw points drawPointList.drawAll(g2);
	 * 
	 * // draw text drawTextList.drawAll(g2);
	 * 
	 * // boolean are not drawn as they are JToggleButtons and children of the
	 * view }
	 */

	// for use in AlgebraController
	final public void mouseMovedOver(GeoElement geo) {
		Hits geos = null;
		if (geo != null) {
			tempArrayList.clear();
			tempArrayList.add(geo);
			geos = tempArrayList;
		}
		boolean repaintNeeded = ((EuclidianController)euclidianController).refreshHighlighting(geos);
		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
	}

	protected Hits tempArrayList = new Hits();

	// for use in AlgebraController
	final public void clickedGeo(GeoElement geo, MouseEvent e) {
		if (geo == null) {
			return;
		}

		tempArrayList.clear();
		tempArrayList.add(geo);
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		boolean changedKernel = ((EuclidianController)euclidianController).processMode(tempArrayList,
				event);
		if (changedKernel) {
			getApplication().storeUndoInfo();
		}
		kernel.notifyRepaint();
	}

	// ggb3D 2009-02-05

	

	// ggb3D 2009-02-05 (end)

	/**
	 * Returns array of GeoElements whose visual representation is inside of the
	 * given screen rectangle
	 * 
	 * @param rect
	 * @return elements drawn inside rectangle
	 */
	final public ArrayList<GeoElement> getHits(Rectangle rect) {
		foundHits.clear();

		if (rect == null) {
			return foundHits;
		}
		geogebra.awt.Rectangle rect2 =  new geogebra.awt.Rectangle(rect);
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && d.isInside(rect2)) {
				foundHits.add(geo);
			}
		}
		return foundHits;
	}

	/**
	 * returns array of GeoElements of type geoclass whose visual representation
	 * is at streen coords (x,y). order: points, vectors, lines, conics
	 * 
	 * @param p
	 * @param geoclass
	 * @param result
	 * @return array of GeoElements of type geoclass drawn at coords (x,y)
	 */
	final public ArrayList<GeoElement> getHits(Point p,
			Class<GeoPoint2> geoclass, ArrayList<GeoElement> result) {
		return getHits(getHits(p), geoclass, false, result);
	}

	/**
	 * Returns array of GeoElements NOT of type geoclass out of hits
	 * 
	 * @param hits
	 * @param geoclass
	 * @param result
	 * @return array of GeoElements NOT of type geoclass out of hits
	 */
	final public static ArrayList<GeoElement> getOtherHits(
			ArrayList<GeoElement> hits, Class<GeoPoint2> geoclass,
			ArrayList<GeoElement> result) {
		return getHits(hits, geoclass, true, result);
	}

	final public static ArrayList<GeoElement> getHits(
			ArrayList<GeoElement> hits, Class<GeoPoint2> geoclass,
			ArrayList<GeoElement> result) {
		return getHits(hits, geoclass, false, result);
	}

	/**
	 * Stores all GeoElements of type geoclass to result list.
	 * 
	 * @param hits
	 * @param geoclass
	 * 
	 * @param other
	 *            == true: returns array of GeoElements NOT of type geoclass out
	 *            of hits.
	 * @param result
	 * @return either null (if result is emty) or result
	 */
	final protected static ArrayList<GeoElement> getHits(
			ArrayList<GeoElement> hits, Class<GeoPoint2> geoclass,
			boolean other, ArrayList<GeoElement> result) {
		if (hits == null) {
			return null;
		}

		result.clear();
		for (int i = 0; i < hits.size(); ++i) {
			boolean success = geoclass.isInstance(hits.get(i));
			if (other) {
				success = !success;
			}
			if (success) {
				result.add(hits.get(i));
			}
		}
		return result.size() == 0 ? null : result;
	}

	/**
	 * returns array of GeoElements whose visual representation is on top of
	 * screen coords of Point p. If there are points at location p only the
	 * points are returned. Otherwise all GeoElements are returned.
	 * 
	 * @param p
	 * @return list of hit GeoElements
	 * 
	 * @see EuclidianController#mousePressed(MouseEvent)
	 * @see EuclidianController#mouseMoved(MouseEvent)
	 */
	final public ArrayList<GeoElement> getTopHits(Point p) {
		return getTopHits(getHits(p));
	}

	/**
	 * if there are GeoPoints in hits, all these points are returned. Otherwise
	 * hits is returned.
	 * 
	 * @param hits
	 * @return either GeoPoints (if selected) or all hits
	 * 
	 * @see EuclidianController#mousePressed(MouseEvent)
	 * @see EuclidianController#mouseMoved(MouseEvent)
	 */
	final public ArrayList<GeoElement> getTopHits(ArrayList<GeoElement> hits) {
		if (hits == null) {
			return null;
		}

		// point in there?
		if (containsGeoPoint(hits)) {
			getHits(hits, GeoPoint2.class, false, topHitsList);
			return topHitsList;
		} else {
			return hits;
		}
	}

	/*
	 * interface View implementation
	 */

	
	


	
	


	

	public Drawable newDrawAngle(GeoAngle geo) {
		return new DrawAngle(this,geo);
	}
	public Drawable newDrawBoolean( GeoBoolean geo) {
		return new DrawBoolean(this,geo);
	}
	public Drawable newDrawButton( GeoButton geo) {
		return new DrawButton(this,geo);
	}
	public Drawable newDrawTextField(GeoTextField geo) {
		return new DrawTextField(this,geo);
	}
	
	public Drawable newDrawImage(GeoImage geo) {
		return new DrawImage(this,geo);
	}
	
	public Drawable newDrawText(GeoText geo) {
		return new DrawText(this,geo);
	}

	public void clearView() {
		evjpanel.removeAll(); // remove hotEqns
		resetLists();
		initView(false);
		updateBackgroundImage(); // clear traces and images
		// resetMode();
	}

	
	public String getXML() {
		StringBuilder sb = new StringBuilder();
		getXML(sb, false);
		return sb.toString();
	}

	/**
	 * returns settings in XML format
	 * 
	 * @param sb
	 * @param asPreference
	 */
	public void getXML(StringBuilder sb, boolean asPreference) {
		sb.append("<euclidianView>\n");
		if (evNo >= 2) {
			sb.append("\t<viewNumber ");
			sb.append("viewNo=\"");
			sb.append(evNo);
			sb.append("\"");
			sb.append("/>\n");
		}

		if ((getWidth() > MIN_WIDTH) && (getHeight() > MIN_HEIGHT)) {
			sb.append("\t<size ");
			sb.append(" width=\"");
			sb.append(getWidth());
			sb.append("\"");
			sb.append(" height=\"");
			sb.append(getHeight());
			sb.append("\"");
			sb.append("/>\n");
		}
		if (!isZoomable() && !asPreference) {
			sb.append("\t<coordSystem");
			sb.append(" xMin=\"");
			sb.append(((GeoNumeric) xminObject).getLabel());
			sb.append("\"");
			sb.append(" xMax=\"");
			sb.append(((GeoNumeric) xmaxObject).getLabel());
			sb.append("\"");
			sb.append(" yMin=\"");
			sb.append(((GeoNumeric) yminObject).getLabel());
			sb.append("\"");
			sb.append(" yMax=\"");
			sb.append(((GeoNumeric) ymaxObject).getLabel());
			sb.append("\"");
			sb.append("/>\n");
		} else {
			sb.append("\t<coordSystem");
			sb.append(" xZero=\"");
			sb.append(getxZero());
			sb.append("\"");
			sb.append(" yZero=\"");
			sb.append(getyZero());
			sb.append("\"");
			sb.append(" scale=\"");
			sb.append(getXscale());
			sb.append("\"");
			sb.append(" yscale=\"");
			sb.append(getYscale());
			sb.append("\"");
			sb.append("/>\n");
		}
		// NOTE: the attribute "axes" for the visibility state of
		// both axes is no longer needed since V3.0.
		// Now there are special axis tags, see below.
		sb.append("\t<evSettings axes=\"");
		sb.append(showAxes[0] || showAxes[1]);
		sb.append("\" grid=\"");
		sb.append(showGrid);
		sb.append("\" gridIsBold=\""); //
		sb.append(gridIsBold); // Michael Borcherds 2008-04-11
		sb.append("\" pointCapturing=\"");
		sb.append(getPointCapturingMode());
		sb.append("\" rightAngleStyle=\"");
		sb.append(getApplication().rightAngleStyle);
		if (asPreference) {
			sb.append("\" allowShowMouseCoords=\"");
			sb.append(getAllowShowMouseCoords());

			sb.append("\" allowToolTips=\"");
			sb.append(getAllowToolTips());
		}

		sb.append("\" checkboxSize=\"");
		sb.append(getApplication().booleanSize); // Michael Borcherds 2008-05-12

		sb.append("\" gridType=\"");
		sb.append(getGridType()); // cartesian/isometric/polar

		sb.append("\"/>\n");

		// background color
		sb.append("\t<bgColor r=\"");
		sb.append(getBackground().getRed());
		sb.append("\" g=\"");
		sb.append(getBackground().getGreen());
		sb.append("\" b=\"");
		sb.append(getBackground().getBlue());
		sb.append("\"/>\n");

		// axes color
		sb.append("\t<axesColor r=\"");
		sb.append(axesColor.getRed());
		sb.append("\" g=\"");
		sb.append(axesColor.getGreen());
		sb.append("\" b=\"");
		sb.append(axesColor.getBlue());
		sb.append("\"/>\n");

		// grid color
		sb.append("\t<gridColor r=\"");
		sb.append(gridColor.getRed());
		sb.append("\" g=\"");
		sb.append(gridColor.getGreen());
		sb.append("\" b=\"");
		sb.append(gridColor.getBlue());
		sb.append("\"/>\n");

		// axes line style
		sb.append("\t<lineStyle axes=\"");
		sb.append(axesLineType);
		sb.append("\" grid=\"");
		sb.append(gridLineStyle);
		sb.append("\"/>\n");

		// axis settings
		for (int i = 0; i < 2; i++) {
			sb.append("\t<axis id=\"");
			sb.append(i);
			sb.append("\" show=\"");
			sb.append(showAxes[i]);
			sb.append("\" label=\"");
			sb.append(axesLabels[i] == null ? "" : StringUtil
					.encodeXML(axesLabels[i]));
			sb.append("\" unitLabel=\"");
			sb.append(axesUnitLabels[i] == null ? "" : StringUtil
					.encodeXML(axesUnitLabels[i]));
			sb.append("\" tickStyle=\"");
			sb.append(axesTickStyles[i]);
			sb.append("\" showNumbers=\"");
			sb.append(showAxesNumbers[i]);

			// the tick distance should only be saved if
			// it isn't calculated automatically
			if (!automaticAxesNumberingDistances[i]) {
				sb.append("\" tickDistance=\"");
				sb.append(axesNumberingDistances[i]);
			}

			// axis crossing values
			if (drawBorderAxes[i]) {
				sb.append("\" axisCrossEdge=\"");
				sb.append(true);
			} else if (!Kernel.isZero(axisCross[i])
					&& !drawBorderAxes[i]) {
				sb.append("\" axisCross=\"");
				sb.append(axisCross[i]);
			}

			// positive direction only flags
			if (positiveAxes[i]) {
				sb.append("\" positiveAxis=\"");
				sb.append(positiveAxes[i]);
			}

			sb.append("\"/>\n");
		}

		// grid distances
		if (!automaticGridDistance || (// compatibility to v2.7:
				EuclidianStyleConstants.automaticGridDistanceFactor != EuclidianStyleConstants.DEFAULT_GRID_DIST_FACTOR)) {
			sb.append("\t<grid distX=\"");
			sb.append(gridDistances[0]);
			sb.append("\" distY=\"");
			sb.append(gridDistances[1]);
			sb.append("\" distTheta=\"");
			// polar angle step added in v4.0
			sb.append(gridDistances[2]);
			sb.append("\"/>\n");
		}

		sb.append("</euclidianView>\n");
	}

	/***************************************************************************
	 * ANIMATED ZOOMING
	 **************************************************************************/

	/**
	 * Zooms around fixed point (px, py)
	 */
	public final void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo) {
		if (!isZoomable()) {
			return;
		}
		if (zoomer == null) {
			zoomer = new MyZoomer();
		}
		zoomer.init(px, py, zoomFactor, steps, storeUndo);
		zoomer.startAnimation();

	}

	
	protected MyZoomer zoomer;

	/**
	 * Zooms towards the given axes scale ratio. Note: Only the y-axis is
	 * changed here. ratio = yscale / xscale;
	 * 
	 * @param newRatio
	 * @param storeUndo
	 */
	@Override
	public final void zoomAxesRatio(double newRatio, boolean storeUndo) {
		if (!isZoomable()) {
			return;
		}
		if (isUnitAxesRatio()) {
			return;
		}
		if (axesRatioZoomer == null) {
			axesRatioZoomer = new MyAxesRatioZoomer();
		}
		axesRatioZoomer.init(newRatio, storeUndo);
		axesRatioZoomer.startAnimation();
	}

	protected MyAxesRatioZoomer axesRatioZoomer;

	public final void setViewShowAllObjects(boolean storeUndo) {

		double x0RW = getXmin();
		double x1RW;
		double y0RW;
		double y1RW;
		double y0RWfunctions = 0;
		double y1RWfunctions = 0;
		double factor = 0.03d; // don't want objects at edge
		double xGap = 0;

		TreeSet<GeoElement> allFunctions = kernel.getConstruction()
				.getGeoSetLabelOrder(GeoClass.FUNCTION);

		int noVisible = 0;
		// count no of visible functions
		Iterator<GeoElement> it = allFunctions.iterator();
		while (it.hasNext()) {
			if (((GeoFunction) (it.next())).isEuclidianVisible()) {
				noVisible++;
			}
		}
		;

		Rectangle rect = getBounds();
		if (Kernel.isZero(rect.getHeight())
				|| Kernel.isZero(rect.getWidth())) {
			if (noVisible == 0) {
				return; // no functions or objects
			}

			// just functions
			x0RW = Double.MAX_VALUE;
			x1RW = -Double.MAX_VALUE;
			y0RW = Double.MAX_VALUE;
			y1RW = -Double.MAX_VALUE;

			// Application.debug("just functions");

		} else {

			// get bounds of points, circles etc
			x0RW = toRealWorldCoordX(rect.getMinX());
			x1RW = toRealWorldCoordX(rect.getMaxX());
			y0RW = toRealWorldCoordY(rect.getMaxY());
			y1RW = toRealWorldCoordY(rect.getMinY());
		}

		xGap = (x1RW - x0RW) * factor;

		boolean ok = false;

		if (noVisible != 0) {

			// if there are functions we don't want to zoom in horizintally
			x0RW = Math.min(getXmin(), x0RW);
			x1RW = Math.max(getXmax(), x1RW);

			if (Kernel.isEqual(x0RW, getXmin())
					&& Kernel.isEqual(x1RW, getXmax())) {
				// just functions (at sides!), don't need a gap
				xGap = 0;
			} else {
				xGap = (x1RW - x0RW) * factor;
			}

			// Application.debug("checking functions from "+x0RW+" to "+x1RW);

			y0RWfunctions = Double.MAX_VALUE;
			y1RWfunctions = -Double.MAX_VALUE;

			it = allFunctions.iterator();

			while (it.hasNext()) {
				GeoFunction fun = (GeoFunction) (it.next());
				double abscissa;
				// check 100 random heights
				for (int i = 0; i < 200; i++) {

					if (i == 0) {
						abscissa = fun.evaluate(x0RW); // check far left
					} else if (i == 1) {
						abscissa = fun.evaluate(x1RW); // check far right
					} else {
						abscissa = fun.evaluate(x0RW
								+ (Math.random() * (x1RW - x0RW)));
					}

					if (!Double.isInfinite(abscissa) && !Double.isNaN(abscissa)) {
						ok = true;
						if (abscissa > y1RWfunctions) {
							y1RWfunctions = abscissa;
						}
						// no else: there **might** be just one value
						if (abscissa < y0RWfunctions) {
							y0RWfunctions = abscissa;
						}
					}
				}
			}

		}

		if (!Kernel.isZero(y1RWfunctions - y0RWfunctions) && ok) {
			y0RW = Math.min(y0RW, y0RWfunctions);
			y1RW = Math.max(y1RW, y1RWfunctions);
			// Application.debug("min height "+y0RW+" max height "+y1RW);
		}

		// don't want objects at edge
		double yGap = (y1RW - y0RW) * factor;

		final double x0RW2 = x0RW - xGap;
		final double x1RW2 = x1RW + xGap;
		final double y0RW2 = y0RW - yGap;
		final double y1RW2 = y1RW + yGap;

		setAnimatedRealWorldCoordSystem(x0RW2, x1RW2, y0RW2, y1RW2, 10,
				storeUndo);

	}

	public final void setStandardView(boolean storeUndo) {
		if (!isZoomable()) {
			return;
		}
		final double xzero, yzero;

		// check if the window is so small that we need custom
		// positions.
		if (getWidth() < (XZERO_STANDARD * 3)) {
			xzero = getWidth() / 3.0;
		} else {
			xzero = XZERO_STANDARD;
		}

		if (getHeight() < (YZERO_STANDARD * 1.6)) {
			yzero = getHeight() / 1.6;
		} else {
			yzero = YZERO_STANDARD;
		}

		if (getScaleRatio() != 1.0) {
			// set axes ratio back to 1
			if (axesRatioZoomer == null) {
				axesRatioZoomer = new MyAxesRatioZoomer();
			}
			axesRatioZoomer.init(1, false);

			Thread waiter = new Thread() {
				@Override
				public void run() {
					// wait until zoomer has finished
					axesRatioZoomer.startAnimation();
					while (axesRatioZoomer.isRunning()) {
						try {
							Thread.sleep(100);
						} catch (Exception e) {
						}
					}
					setAnimatedCoordSystem(xzero, yzero, 0, SCALE_STANDARD, 15,
							false);
				}
			};
			waiter.start();
		} else {
			setAnimatedCoordSystem(xzero, yzero, 0, SCALE_STANDARD, 15, false);
		}
		if (storeUndo) {
			getApplication().storeUndoInfo();
		}
	}

	/**
	 * Sets coord system of this view. Just like setCoordSystem but with
	 * previous animation.
	 * 
	 * @param ox
	 *            x coord of old origin
	 * @param oy
	 *            y coord of old origin
	 * @param newScale
	 */
	final public void setAnimatedCoordSystem(double ox, double oy, double f,
			double newScale, int steps, boolean storeUndo) {

		ox += (getXZero() - ox) * f;
		oy += (getYZero() - oy) * f;

		if (!Kernel.isEqual(getXscale(), newScale)) {
			// different scales: zoom back to standard view
			double factor = newScale / getXscale();
			zoom((ox - (getxZero() * factor)) / (1.0 - factor),
					(oy - (getyZero() * factor)) / (1.0 - factor), factor, steps,
					storeUndo);
		} else {
			// same scales: translate view to standard origin
			// do this with the following action listener
			if (mover == null) {
				mover = new MyMover();
			}
			mover.init(ox, oy, storeUndo);
			mover.startAnimation();
		}
	}

	protected MyMover mover;

	protected class MyZoomer implements ActionListener {
		static final int MAX_STEPS = 15; // frames

		static final int DELAY = 10;

		static final int MAX_TIME = 400; // millis

		protected Timer timer; // for animation

		protected double px, py; // zoom point

		protected double factor;

		protected int counter, steps;

		protected double oldScale, newScale, add, dx, dy;

		protected long startTime;

		protected boolean storeUndo;

		public MyZoomer() {
			timer = new Timer(DELAY, this);
		}

		public void init(double px, double py, double zoomFactor, int steps,
				boolean storeUndo) {
			this.px = px;
			this.py = py;
			// this.zoomFactor = zoomFactor;
			this.storeUndo = storeUndo;

			oldScale = getXscale();
			newScale = getXscale() * zoomFactor;
			this.steps = Math.min(MAX_STEPS, steps);
		}

		public synchronized void startAnimation() {
			if (timer == null) {
				return;
			}
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = (newScale - oldScale) / steps;
			dx = getxZero() - px;
			dy = getyZero() - py;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			factor = newScale / oldScale;
			setCoordSystem(px + (dx * factor), py + (dy * factor), newScale,
					newScale * getScaleRatio());

			if (storeUndo) {
				getApplication().storeUndoInfo();
			}
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if ((counter == steps) || (time > MAX_TIME)) { // end of animation
				stopAnimation();
			} else {
				factor = 1.0 + ((counter * add) / oldScale);
				setCoordSystem(px + (dx * factor), py + (dy * factor), oldScale
						* factor, oldScale * factor * getScaleRatio());
			}
		}
	}

	protected class MyZoomerRW implements ActionListener {
		static final int MAX_STEPS = 15; // frames

		static final int DELAY = 10;

		static final int MAX_TIME = 400; // millis

		protected Timer timer; // for animation

		protected int counter, steps;

		protected long startTime;

		protected boolean storeUndo;

		protected double x0, x1, y0, y1, xminOld, xmaxOld, yminOld, ymaxOld;

		public MyZoomerRW() {
			timer = new Timer(DELAY, this);
		}

		public void init(double x0, double x1, double y0, double y1, int steps,
				boolean storeUndo) {
			this.x0 = x0;
			this.x1 = x1;
			this.y0 = y0;
			this.y1 = y1;

			xminOld = getXmin();
			xmaxOld = getXmax();
			yminOld = getYmin();
			ymaxOld = getYmax();
			// this.zoomFactor = zoomFactor;
			this.storeUndo = storeUndo;

			this.steps = Math.min(MAX_STEPS, steps);
		}

		public synchronized void startAnimation() {
			if (timer == null) {
				return;
			}
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			setRealWorldCoordSystem(x0, x1, y0, y1);

			if (storeUndo) {
				getApplication().storeUndoInfo();
			}
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if ((counter == steps) || (time > MAX_TIME)) { // end of animation
				stopAnimation();
			} else {
				double i = counter;
				double j = steps - counter;
				setRealWorldCoordSystem(((x0 * i) + (xminOld * j)) / steps,
						((x1 * i) + (xmaxOld * j)) / steps,
						((y0 * i) + (yminOld * j)) / steps,
						((y1 * i) + (ymaxOld * j)) / steps);
			}
		}
	}

	// changes the scale of the y-Axis continously to reach
	// the given scale ratio yscale / xscale
	protected class MyAxesRatioZoomer implements ActionListener {

		protected Timer timer; // for animation

		protected double factor;

		protected int counter;

		protected double oldScale, newScale, add;

		protected long startTime;

		protected boolean storeUndo;

		public MyAxesRatioZoomer() {
			timer = new Timer(MyZoomer.DELAY, this);
		}

		public void init(double ratio, boolean storeUndo) {
			// this.ratio = ratio;
			this.storeUndo = storeUndo;

			// zoomFactor = ratio / scaleRatio;
			oldScale = getYscale();
			newScale = getXscale() * ratio; // new yscale
		}

		public synchronized void startAnimation() {
			if (timer == null) {
				return;
			}
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = (newScale - oldScale) / MyZoomer.MAX_STEPS;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			setCoordSystem(getxZero(), getyZero(), getXscale(), newScale);
			if (storeUndo) {
				getApplication().storeUndoInfo();
			}
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if ((counter == MyZoomer.MAX_STEPS) || (time > MyZoomer.MAX_TIME)) { // end
				// of
				// animation
				stopAnimation();
			} else {
				factor = 1.0 + ((counter * add) / oldScale);
				setCoordSystem(getxZero(), getyZero(), getXscale(), oldScale * factor);
			}
		}

		final synchronized boolean isRunning() {
			return timer.isRunning();
		}
	}

	// used for animated moving of euclidian view to standard origin
	protected class MyMover implements ActionListener {
		protected double dx, dy, add;

		protected int counter;

		protected double ox, oy; // new origin

		protected Timer timer;

		protected long startTime;

		protected boolean storeUndo;

		public MyMover() {
			timer = new Timer(MyZoomer.DELAY, this);
		}

		public void init(double ox, double oy, boolean storeUndo) {
			this.ox = ox;
			this.oy = oy;
			this.storeUndo = storeUndo;
		}

		public synchronized void startAnimation() {
			dx = getxZero() - ox;
			dy = getyZero() - oy;
			if (Kernel.isZero(dx) && Kernel.isZero(dy)) {
				return;
			}

			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = 1.0 / MyZoomer.MAX_STEPS;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			setCoordSystem(ox, oy, getXscale(), getYscale());
			if (storeUndo) {
				getApplication().storeUndoInfo();
			}
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if ((counter == MyZoomer.MAX_STEPS) || (time > MyZoomer.MAX_TIME)) { // end
				// of
				// animation
				stopAnimation();
			} else {
				double factor = 1.0 - (counter * add);
				setCoordSystem(ox + (dx * factor), oy + (dy * factor), getXscale(),
						getYscale());
			}
		}
	}

	public Color getAxesColor() {
		return geogebra.awt.Color.getAwtColor(axesColor);
	}

	

	public Color getBackground() {
		return ((geogebra.euclidian.EuclidianViewJPanel)evjpanel).getBackground();
	}
	
	public geogebra.common.awt.Color getBackgroundCommon() {
		return new geogebra.awt.Color(((geogebra.euclidian.EuclidianViewJPanel)evjpanel).getBackground());
	}

	public void setBackground(geogebra.common.awt.Color bgColor) {
		((geogebra.euclidian.EuclidianViewJPanel)evjpanel).setBackground(geogebra.awt.Color.getAwtColor(bgColor));
	}

	public Color getGridColor() {
		return geogebra.awt.Color.getAwtColor(gridColor);
	}

	

	/*
	 * --> moved to Kernel and Kernel3D public String getModeText(int mode) {
	 * 
	 * return getKernel().getModeText(mode); }
	 */

	public int getSelectedWidth() {
		if (selectionRectangle == null) {
			return getWidth();
		} else {
			return (int)selectionRectangle.getWidth();
		}
	}

	public int getSelectedHeight() {
		if (selectionRectangle == null) {
			return getHeight();
		} else {
			return (int)selectionRectangle.getHeight();
		}
	}

	public int getExportWidth() {
		if (selectionRectangle != null) {
			return (int)selectionRectangle.getWidth();
		}
		try {
			GeoPoint2 export1 = (GeoPoint2) kernel.lookupLabel(EXPORT1);
			GeoPoint2 export2 = (GeoPoint2) kernel.lookupLabel(EXPORT2);
			double[] xy1 = new double[2];
			double[] xy2 = new double[2];
			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);
			double x1 = xy1[0];
			double x2 = xy2[0];
			x1 = (x1 / getInvXscale()) + getxZero();
			x2 = (x2 / getInvXscale()) + getxZero();

			return (int) Math.abs(x1 - x2) + 2;
		} catch (Exception e) {
			return getWidth();
		}

	}

	public int getExportHeight() {
		if (selectionRectangle != null) {
			return (int)selectionRectangle.getHeight();
		}

		try {
			GeoPoint2 export1 = (GeoPoint2) kernel.lookupLabel(EXPORT1);
			GeoPoint2 export2 = (GeoPoint2) kernel.lookupLabel(EXPORT2);
			double[] xy1 = new double[2];
			double[] xy2 = new double[2];
			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);
			double y1 = xy1[1];
			double y2 = xy2[1];
			y1 = getyZero() - (y1 / getInvYscale());
			y2 = getyZero() - (y2 / getInvYscale());

			return (int) Math.abs(y1 - y2) + 2;
		} catch (Exception e) {
			return getHeight();
		}

	}

	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}

	public EuclidianController getEuclidianController() {
		return (EuclidianController)euclidianController;
	}

	@Override
	final public geogebra.common.awt.Graphics2D getTempGraphics2D(geogebra.common.awt.Font font) {
		g2Dtemp.setFont(geogebra.awt.Font.getAwtFont(font)); // Michael Borcherds 2008-06-11 bugfix for
								// Corner[text,n]
		return new geogebra.awt.Graphics2D(g2Dtemp);
	}

	final public Graphics2D getTempGraphics2D() {
		g2Dtemp.setFont(((Application)getApplication()).getPlainFont());
		return g2Dtemp;
	}

	public void resetMaxLayerUsed() {
		getApplication().maxLayerUsed = 0;
	}

	public void resetXYMinMaxObjects() {
		if ((evNo == 1) || (evNo == 2)) {
			EuclidianSettings es = getApplication().getSettings().getEuclidian(evNo);
			// this is necessary in File->New because there might have been
			// dynamic xmin bounds
			GeoNumeric xmao = new GeoNumeric(kernel.getConstruction(),
					xmaxObject.getNumber().getDouble());
			GeoNumeric xmio = new GeoNumeric(kernel.getConstruction(),
					xminObject.getNumber().getDouble());
			GeoNumeric ymao = new GeoNumeric(kernel.getConstruction(),
					ymaxObject.getNumber().getDouble());
			GeoNumeric ymio = new GeoNumeric(kernel.getConstruction(),
					yminObject.getNumber().getDouble());
			es.setXmaxObject(xmao, false);
			es.setXminObject(xmio, false);
			es.setYmaxObject(ymao, false);
			es.setYminObject(ymio, true);
		}
	}

	// ///////////////////////////////////////
	// previewables

	public Previewable createPreviewPolygon(ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolygon(this, selectedPoints);
	}

	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints) {
		return new DrawAngle(this, selectedPoints);
	}

	public Previewable createPreviewPolyLine(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolyLine(this, selectedPoints);
	}

	public void updatePreviewable() {
		Point mouseLoc = AwtFactory.prototype.newPoint(getEuclidianController().mouseLoc.x,getEuclidianController().mouseLoc.y);
		getPreviewDrawable().updateMousePos(toRealWorldCoordX(mouseLoc.x),
				toRealWorldCoordY(mouseLoc.y));
	}


	@Override
	public geogebra.common.awt.GeneralPath getBoundingPath() {
		java.awt.geom.GeneralPath gs = new java.awt.geom.GeneralPath();
		gs.moveTo(0, 0);
		gs.lineTo(getWidth(), 0);
		gs.lineTo(getWidth(), getHeight());
		gs.lineTo(0, getHeight());
		gs.lineTo(0, 0);
		gs.closePath();
		return new geogebra.awt.GeneralPath(gs);
	}

	
	

	
	
	public Graphics2D getGraphicsForPen() {
		return (Graphics2D) evjpanel.getGraphics();

	}

	public void drawPoints(GeoImage ge, double[] x, double[] y) {
		ArrayList<java.awt.Point> ptList = new ArrayList<java.awt.Point>();

		AbstractApplication.debug("x0" + x[0]);
		for (int i = 0; i < x.length; i++) {
			int xi = toScreenCoordX(x[i]);
			int yi = toScreenCoordY(y[i]);
			if (ge.getCorner(1) != null) {
				int w = ge.getFillImage().getWidth();
				int h = ge.getFillImage().getHeight();

				double cx[] = new double[3], cy[] = new double[3];
				for (int j = 0; j < (ge.getCorner(2) != null ? 3 : 2); j++) {
					cx[j] = ge.getCorner(j).x;
					cy[j] = ge.getCorner(j).y;
				}
				if (ge.getCorner(2) == null) {
					cx[2] = cx[0] - ((h * (cy[1] - cy[0])) / w);
					cy[2] = cy[0] + ((h * (cx[1] - cx[0])) / w);
				}
				double dx1 = cx[1] - cx[0];
				double dx2 = cx[2] - cx[0];
				double dy1 = cy[1] - cy[0];
				double dy2 = cy[2] - cy[0];
				double ratio1 = (((x[i] - cx[0]) * dy2) - (dx2 * (y[i] - cy[0])))
						/ ((dx1 * dy2) - (dx2 * dy1));
				double ratio2 = ((-(x[i] - cx[0]) * dy1) + (dx1 * (y[i] - cy[0])))
						/ ((dx1 * dy2) - (dx2 * dy1));
				AbstractApplication.debug(cx[2] + "," + cy[2] + "," + h + ","
						+ w);
				xi = (int) Math.round(w * ratio1);
				yi = (int) Math.round(h * (1 - ratio2));

			} else if (ge.getCorner(0) != null) {
				xi = xi - toScreenCoordX(ge.getCorner(0).x);
				yi = ge.getFillImage().getHeight()
						+ (yi - toScreenCoordY(ge.getCorner(0).y));
			}
			ptList.add(new java.awt.Point(xi, yi));
		}
		this.getEuclidianController().getPen().doDrawPoints(ge, ptList);

	}

	
	public void setCursor(Cursor cursor) {
		((JPanel)evjpanel).setCursor(cursor);
	}

	public boolean hasFocus() {
		return evjpanel.hasFocus();
	}

	public void repaint() {
		evjpanel.repaint();
	}
	
	public void add(Component comp) {
		evjpanel.add(comp);
	}
	
	public void remove(Component comp) {
		evjpanel.remove(comp);
	}

	public JPanel getJPanel() {
		// TODO Auto-generated method stub
		return evjpanel;
	}

	public void requestFocus() {
		evjpanel.requestFocus();		
	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		return new geogebra.awt.Font(evjpanel.getFont());
	}

	public Graphics2D getGraphics() {
		return (Graphics2D) evjpanel.getGraphics();
	}

	public java.awt.Point getMousePosition() {
		return evjpanel.getMousePosition();
	}

	public FontMetrics getFontMetrics(java.awt.Font font) {
		return evjpanel.getFontMetrics(font);
	}

	public boolean isShowing() {
		return evjpanel.isShowing();
	}

	@Override
	public boolean requestFocusInWindow() {
		return evjpanel.requestFocusInWindow();	
	}
	
	public void setPreferredSize(Dimension preferredSize) {
		evjpanel.setPreferredSize(preferredSize);
	}
	
	public void setPreferredSize(geogebra.common.awt.Dimension preferredSize) {
		evjpanel.setPreferredSize(geogebra.awt.Dimension.getAWTDimension(preferredSize));
	}
	
	public void revalidate() {
		evjpanel.revalidate();
	}
	
	public void addMouseListener(MouseListener ml) {
		evjpanel.addMouseListener(ml);
	}
	
	public void removeMouseListener(MouseListener ml) {
		evjpanel.removeMouseListener(ml);
	}
	
	public void addMouseMotionListener(MouseMotionListener mml) {
		evjpanel.addMouseMotionListener(mml);
	}
	
	public void removeMouseMotionListener(MouseMotionListener mml) {
		evjpanel.removeMouseMotionListener(mml);
	}
	
	public void addMouseWheelListener(MouseWheelListener mwl) {
		evjpanel.addMouseWheelListener(mwl);
	}
	
	public void removeMouseWheelListener(MouseWheelListener mwl) {
		evjpanel.removeMouseWheelListener(mwl);
	}

	public void dispatchEvent(ComponentEvent componentEvent) {
		evjpanel.dispatchEvent(componentEvent);
	}
	
	public void setBorder(Border border) {
		evjpanel.setBorder(border)	;
	}
	
	public void addComponentListener(
			ComponentListener componentListener) {
		evjpanel.addComponentListener(componentListener);
		
	}
	
	public void setSize(Dimension dimension) {
		evjpanel.setSize(dimension);
		
	}

	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return evjpanel.getPreferredSize();
	}

	protected void processMouseEvent(MouseEvent e) {
		evjpanel.processMouseEventImpl(e);
	}

	@Override
	protected void setHeight(int height) {
	}

	@Override
	protected void setWidth(int width) {
	}

	
	
	

	@Override
	protected void setStyleBarMode(int mode) {
		if (hasStyleBar()) {
			getStyleBar().setMode(mode);
		}
	}

	public boolean hitAnimationButton(AbstractEvent e) {
		return hitAnimationButton(geogebra.euclidian.event.MouseEvent.getEvent(e));
	}

	public void setHits(java.awt.Rectangle rect) {
		setHits(new geogebra.awt.Rectangle(rect));
	}
}
