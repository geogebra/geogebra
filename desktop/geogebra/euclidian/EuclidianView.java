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
import geogebra.common.awt.Rectangle;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.DrawBoolean;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.GetViewId;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.factories.AwtFactory;
import geogebra.common.factories.FormatFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.common.plugin.EuclidianStyleConstants;
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
import java.awt.RenderingHints;
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

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
		((EuclidianController)euclidianController).setPen(new EuclidianPen(getApplication(), this));

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

	

	/*
	 * public void detachView() { kernel.detach(this); clearView();
	 * //kernel.notifyRemoveAll(this); }
	 */

	
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


	
	
	public void setDragCursor() {

		if (getApplication().useTransparentCursorWhenDragging) {
			setCursor(getApplication().getTransparentCursor());
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
			defaultCursor = getCursorForImage(getApplication()
					.getInternalImage("cursor_zoomin.gif"));
			break;

		case EuclidianConstants.MODE_ZOOM_OUT:
			defaultCursor = getCursorForImage(getApplication()
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

		
	public int getWidth() {
		return evjpanel.getWidth();
	}
	
	public int getHeight() {
		return evjpanel.getHeight();
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
				Font titleFont = getApplication().getBoldFontCommon().deriveFont(Font.BOLD,
						getApplication().getBoldFont().getSize() + 2);
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
			if (getApplication().isPrintScaleString()) {
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
				g2d.setFont(getApplication().getPlainFont());
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
		getApplication().exporting = true;
		exportPaintPre(g2d, scale, transparency);
		drawObjects(g2d);
		getApplication().exporting = false;
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
				geogebra.awt.Graphics2D.getAwtGraphics(g2d).drawImage(geogebra.awt.BufferedImage.getAwtBufferedImage(bgImage), 0, 0, evjpanel);
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

	




	protected void drawResetIcon(geogebra.common.awt.Graphics2D g){
		// need to use getApplet().width rather than width so that
					// it works with applet rescaling
					int w = getApplication().onlyGraphicsViewShowing() ? getApplication().getApplet().width
							: getWidth() + 2;
					geogebra.awt.Graphics2D.getAwtGraphics(g).drawImage(getResetImage(), w - 18, 2, null);
	}
	private Image getResetImage() {
		if (resetImage == null) {
			resetImage = getApplication().getRefreshViewImage();
		}
		return resetImage;
	}

	private Image getPlayImage() {
		if (playImage == null) {
			playImage = getApplication().getPlayImage();
		}
		return playImage;
	}

	private Image getPauseImage() {
		if (pauseImage == null) {
			pauseImage = getApplication().getPauseImage();
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
		if ((evp = getApplication().getGuiManager().getLayout().getDockManager().getFocusedEuclidianPanel()) == null) {
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
				evjpanel.paintChildren(
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

	
	public Drawable newDrawBoolean( GeoBoolean geo) {
		return new DrawBoolean(this,geo);
	}

	public Drawable newDrawButton( GeoButton geo) {
		return new DrawButton(this,geo);
	}

	public Drawable newDrawTextField(GeoTextField geo) {
		return new DrawTextField(this,geo);
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
		return evjpanel.getBackground();
	}
	
	public geogebra.common.awt.Color getBackgroundCommon() {
		return new geogebra.awt.Color(evjpanel.getBackground());
	}

	public void setBackground(geogebra.common.awt.Color bgColor) {
		evjpanel.setBackground(geogebra.awt.Color.getAwtColor(bgColor));
	}

	public Color getGridColor() {
		return geogebra.awt.Color.getAwtColor(gridColor);
	}

	

	/*
	 * --> moved to Kernel and Kernel3D public String getModeText(int mode) {
	 * 
	 * return getKernel().getModeText(mode); }
	 */

	
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
		g2Dtemp.setFont(getApplication().getPlainFont());
		return g2Dtemp;
	}

	

	public Graphics2D getGraphicsForPen() {
		return (Graphics2D) evjpanel.getGraphics();

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
