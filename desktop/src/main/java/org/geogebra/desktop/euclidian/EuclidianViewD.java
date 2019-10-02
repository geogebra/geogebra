/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.desktop.euclidian;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EVProperty;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GDimensionD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.export.GraphicExportDialog;
import org.geogebra.desktop.io.MyImageIO;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * 
 * @author Markus Hohenwarter
 */
public class EuclidianViewD extends EuclidianView
		implements EuclidianViewInterfaceD, Printable {

	// temp
	// public static final int DRAW_MODE_DIRECT_DRAW = 0;
	// public static final int DRAW_MODE_BACKGROUND_IMAGE = 1;

	/** reset image in applets */
	protected Image resetImage;
	/** play image for animations */
	protected Image playImage;
	/** pause image for animations */
	protected Image pauseImage;
	/** play image for animations */
	protected Image playImageHL;
	/** pause image for animations */
	protected Image pauseImageHL;

	// public Graphics2D lastGraphics2D;
	/** default mouse cursor */
	protected Cursor defaultCursor;

	/** Java component for this view */
	protected EuclidianViewJPanelD evjpanel;

	// set EuclidianView no - 2 for 2nd EulidianView, 1 for 1st EuclidianView
	// and Applet
	// EVNO_GENERAL for others

	/**
	 * @param ec
	 *            controller
	 * @param showAxes
	 *            whether to show x-axis and y-axis
	 * @param showGrid
	 *            whether to show grid
	 * @param settings
	 *            settings
	 */
	public EuclidianViewD(EuclidianController ec, boolean[] showAxes,
			boolean showGrid, EuclidianSettings settings) {
		this(ec, showAxes, showGrid, 1, settings);
	}

	/**
	 * Creates EuclidianView
	 * 
	 * @param ec
	 *            controller
	 * @param showAxes
	 *            whether x-axis and y-axis should be shown
	 * @param showGrid
	 *            whether grid should be shown
	 * @param evno
	 *            number of this view
	 * @param settings
	 *            euclidian settings
	 */
	public EuclidianViewD(EuclidianController ec, boolean[] showAxes,
			boolean showGrid, int evno, EuclidianSettings settings) {

		super(ec, evno, settings);
		viewTextField = new ViewTextFieldD(this);
		evjpanel = new EuclidianViewJPanelD(this);

		setApplication(ec.getApplication());

		setShowAxis(0, showAxes[0], false);
		setShowAxis(1, showAxes[1], false);
		this.showGrid = showGrid;

		// algebra controller will take care of our key events

		euclidianController.setView(this);

		attachView();

		initView(false);

		// updateRightAngleStyle(app.getLocale());

		EuclidianSettings es = null;
		if (settings != null) {
			es = settings;
			// settings from XML for EV1, EV2
			// not for eg probability calculator
		} else if ((evNo == 1) || (evNo == 2)) {
			es = getApplication().getSettings().getEuclidian(evNo);
		}

		if (es != null) {
			settingsChanged(es);
			es.addListener(this);
		}
	}

	@Override
	protected void initView(boolean repaint) {
		initPanel(repaint);
		super.initView(repaint);
	}

	/**
	 * @return whether preferred size is defined and greater than minimum
	 */
	public boolean hasPreferredSize() {
		Dimension prefSize = getPreferredSize();

		return (prefSize != null) && (prefSize.width > MIN_WIDTH)
				&& (prefSize.height > MIN_HEIGHT);
	}

	/**
	 * Switch to drag cursor
	 */
	public void setDragCursor() {

		if (getMode() == EuclidianConstants.MODE_TRANSLATEVIEW) {
			setGrabbingCursor();
		}

		else if (getApplication().useTransparentCursorWhenDragging()) {
			setCursor(getApplication().getTransparentCursor());
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

	}

	private void setTransparentCursor() {

		setCursor(getApplication().getTransparentCursor());
	}

	private void setMoveCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	private void setResizeXAxisCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
	}

	private void setResizeYAxisCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
	}

	/**
	 * Set the cursor to grabbing hand
	 */
	public void setGrabbingCursor() {
		// TODO gui/image/cursor..
		setCursor(getCursorForImage(GuiResourcesD.CURSOR_GRABBING));
	}

	/**
	 * Switch to hit cursor
	 */
	public void setHitCursor() {
		if (defaultCursor == null) {
			setCursor(Cursor.getDefaultCursor());
		} else {
			setCursor(defaultCursor);
		}
	}

	/**
	 * Switch to default cursor
	 */
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

		switch (getMode()) {
		default:
			// do nothing
			break;
		case EuclidianConstants.MODE_ZOOM_IN:
			defaultCursor = getCursorForImage(GuiResourcesD.CURSOR_ZOOMIN);
			break;

		case EuclidianConstants.MODE_ZOOM_OUT:
			defaultCursor = getCursorForImage(GuiResourcesD.CURSOR_ZOOMOUT);
			break;

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			defaultCursor = getCursorForImage(GuiResourcesD.CURSOR_GRAB);
			break;
		}

		setDefaultCursor();
	}

	/**
	 * @param name
	 *            cursor resource
	 * @return cursor
	 */
	protected Cursor getCursorForImage(ImageResourceD name) {

		return getCursorForImage(getApplication().getInternalImage(name));

	}

	/**
	 * @param image
	 *            image file
	 * @return cursor created from image
	 */
	private static Cursor getCursorForImage(Image image) {
		if (image == null) {
			return null;
		}

		// Query for custom cursor support
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getBestCursorSize(32, 32);
		int colors = tk.getMaximumCursorColors();
		if (!d.equals(new Dimension(0, 0)) && (colors != 0)) {
			// load cursor image
			try {
				// Create custom cursor from the image
				Cursor cursor = tk.createCustomCursor(image, new Point(16, 16),
						"custom cursor");
				return cursor;
			} catch (Exception exc) {
				// Catch exceptions so that we don't try to set a null
				// cursor
				Log.debug("Unable to create custom cursor.");
			}

		}
		return null;
	}

	/**
	 * @param g2d
	 *            graphics
	 * @param scaleString
	 *            title
	 * @param pageFormat
	 *            format
	 * @param app
	 *            application
	 * @return height
	 */
	public static int printTitle(Graphics2D g2d, String scaleString,
			PageFormat pageFormat, AppD app) {
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		// construction title
		int y = 0;
		Construction cons = app.getKernel().getConstruction();
		String title = cons.getTitle();
		if (!"".equals(title)) {
			GFont titleFont = app.getBoldFontCommon().deriveFont(GFont.BOLD,
					app.getBoldFont().getSize() + 2);
			g2d.setFont(GFontD.getAwtFont(titleFont));
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
		if (!"".equals(author)) {
			line = author;
		}
		if (!"".equals(date)) {
			if (line == null) {
				line = date;
			} else {
				line = line + " - " + date;
			}
		}

		if (scaleString != null) {
			if (line == null) {
				line = scaleString;
			} else {
				line = line + " - " + scaleString;
			}
		}

		if (line != null) {
			g2d.setFont(app.getPlainFont());
			g2d.setColor(Color.black);
			// Font fn = g2d.getFont();
			FontMetrics fm = g2d.getFontMetrics();
			y += fm.getHeight();
			g2d.drawString(line, 0, y);
		}
		return y;
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex0) {
		int pageIndex = ((AppD)kernel.getApplication()).getPrintPreview().adjustIndex(pageIndex0);
		if (pageIndex > 0) {
			return (NO_SUCH_PAGE);
		}
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform oldTransform = g2d.getTransform();
		int h = printTitle(g2d, getScaleString(), pageFormat, getApplication());
		if (h > 0) {
			g2d.translate(0, h + 20);
		}
		double scale = (PRINTER_PIXEL_PER_CM / getXscale()) * printingScale;
		exportPaint(g2d, scale, ExportType.PRINTING);

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

		return (PAGE_EXISTS);

	}

	/**
	 * @param g2d
	 *            graphics
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 * @param exportType
	 *            export type
	 */
	public void exportPaint(Graphics2D g2d, double scale,
			ExportType exportType) {
		exportPaint(new GGraphics2DD(g2d), scale, false, exportType);
	}

	@Override
	public void exportImagePNG(double scale, boolean transparency, int dpi,
			File file, boolean exportToClipboard, ExportType exportType) {

		try {
			GBufferedImage img = getExportImage(scale, transparency,
					exportType);
			MyImageIO.write(GBufferedImageD.getAwtBufferedImage(img), "png",
					dpi, file);
			if (exportToClipboard) {
				GraphicExportDialog.sendToClipboard(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {
		// need to use getApplet().width rather than width so that
		// it works with applet rescaling
		int w = getWidth() + 2;
		GGraphics2DD.getAwtGraphics(g).drawImage(getResetImage(), w - 18, 2,
				null);
	}

	private Image getResetImage() {
		if (resetImage == null) {
			resetImage = getApplication().getRefreshViewImage();
		}
		return resetImage;
	}

	private Image getPlayImage(boolean highlight) {
		if (playImage == null) {
			playImage = getApplication().getPlayImageCircle();
			playImageHL = getApplication().getPlayImageCircleHover();
		}
		return highlight ? playImageHL : playImage;
	}

	private Image getPauseImage(boolean highlight) {
		if (pauseImage == null) {
			pauseImage = getApplication().getPauseImageCircle();
			pauseImageHL = getApplication().getPauseImageCircleHover();
		}
		return highlight ? pauseImageHL : pauseImage;
	}

	@Override
	final protected void drawAnimationButtons(GGraphics2D g2) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return;
		}

		int x = 3;
		int y = getHeight() - 27;

		/*
		 * if (highlightAnimationButtons) { // draw filled circle to highlight
		 * button g2.setColor(GColor.DARK_GRAY); } else {
		 * g2.setColor(GColor.LIGHT_GRAY); }
		 * 
		 * g2.setStroke(EuclidianStatic .getDefaultStroke());
		 * 
		 * // draw pause or play button g2.drawRect(x - 2, y - 2, 18, 18);
		 */
		Image img = kernel.isAnimationRunning()
				? getPauseImage(highlightAnimationButtons)
				: getPlayImage(highlightAnimationButtons);
		GGraphics2DD.getAwtGraphics(g2).drawImage(img, x, y, null);
	}

	@Override
	public final boolean hitAnimationButton(int x, int y) {
		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return false;
		}

		return kernel.needToShowAnimationButton() && (x <= 27)
				&& (y >= (getHeight() - 27));
	}

	@Override
	public EuclidianController getEuclidianController() {
		return euclidianController;
	}

	/**
	 * @return graphics of the underlying component
	 */
	@Override
	public GGraphics2D getGraphicsForPen() {
		return new GGraphics2DD((Graphics2D) evjpanel.getGraphics());

	}

	@Override
	public void setBoldAxes(boolean bold) {
		// TODO Auto-generated method stub

	}

	@Override
	public AppD getApplication() {
		return (AppD) super.getApplication();
	}

	// ////////////////////////////
	// EVJPANEL
	// ////////////////////////////

	/**
	 * @param cursor
	 *            new cursor
	 */
	@Override
	public void setCursor(Cursor cursor) {
		evjpanel.setCursor(cursor);
	}

	@Override
	public boolean hasFocus() {
		return evjpanel.hasFocus();
	}

	@Override
	public void repaint() {
		this.updateBackgroundIfNecessary();
		evjpanel.repaint();
	}

	@Override
	public void paintBackground(GGraphics2D g2) {
		g2.drawImage(bgImage, 0, 0);
	}

	@Override
	public void add(Box box) {
		evjpanel.add(box);
	}

	/**
	 * @return underlying component
	 */
	@Override
	public JPanel getJPanel() {
		return evjpanel;
	}

	/**
	 * This view should be focused
	 */
	@Override
	public void requestFocus() {
		evjpanel.requestFocus();
	}

	@Override
	public GFont getFont() {
		// TODO Auto-generated method stub
		return new GFontD(evjpanel.getFont());
	}

	/**
	 * @return mouse position
	 */
	@Override
	public Point getMousePosition() {
		return evjpanel.getMousePosition();
	}

	/**
	 * @see JPanel#getFontMetrics(java.awt.Font)
	 * @param font
	 *            font
	 * @return font metrics
	 */
	public FontMetrics getFontMetrics(Font font) {
		return evjpanel.getFontMetrics(font);
	}

	/**
	 * @return whethe this view is visible
	 */
	@Override
	public boolean isShowing() {
		return evjpanel.isShowing();
	}

	@Override
	public boolean requestFocusInWindow() {
		return evjpanel.requestFocusInWindow();
	}

	/**
	 * @see JPanel#setPreferredSize(Dimension)
	 * @param preferredSize
	 *            prefered size
	 */
	public void setPreferredSize(Dimension preferredSize) {
		evjpanel.setPreferredSize(preferredSize);
	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {
		evjpanel.setPreferredSize(GDimensionD.getAWTDimension(preferredSize));
	}

	/**
	 * @see JPanel#revalidate()
	 */
	public void revalidate() {
		evjpanel.revalidate();
	}

	/**
	 * @see JPanel#addMouseListener(MouseListener)
	 * @param ml
	 *            mouse listener
	 */
	public void addMouseListener(MouseListener ml) {
		evjpanel.addMouseListener(ml);
	}

	/**
	 * @see JPanel#removeComponentListener(ComponentListener)
	 * @param ml
	 *            mouse listener
	 */
	public void removeMouseListener(MouseListener ml) {
		evjpanel.removeMouseListener(ml);
	}

	/**
	 * @see JPanel#addMouseMotionListener(MouseMotionListener)
	 * @param mml
	 *            mouse motion listener
	 */
	public void addMouseMotionListener(MouseMotionListener mml) {
		evjpanel.addMouseMotionListener(mml);
	}

	/**
	 * @see JPanel#removeMouseMotionListener(MouseMotionListener)
	 * @param mml
	 *            mouse motion listener
	 */
	public void removeMouseMotionListener(MouseMotionListener mml) {
		evjpanel.removeMouseMotionListener(mml);
	}

	/**
	 * @see JPanel#addMouseWheelListener(MouseWheelListener)
	 * @param mwl
	 *            mouse wheel listener
	 */
	public void addMouseWheelListener(MouseWheelListener mwl) {
		evjpanel.addMouseWheelListener(mwl);
	}

	/**
	 * @see JPanel#removeMouseWheelListener(MouseWheelListener)
	 * @param mwl
	 *            mouse wheel listener
	 */
	public void removeMouseWheelListener(MouseWheelListener mwl) {
		evjpanel.removeMouseWheelListener(mwl);
	}

	/**
	 * @see JPanel#dispatchEvent(AWTEvent)
	 * @param componentEvent
	 *            component event
	 */
	public void dispatchEvent(ComponentEvent componentEvent) {
		evjpanel.dispatchEvent(componentEvent);
	}

	/**
	 * @see JPanel#setBorder(Border)
	 * @param border
	 *            new border
	 */
	@Override
	public void setBorder(Border border) {
		evjpanel.setBorder(border);
	}

	/**
	 * @see JPanel#addComponentListener(ComponentListener)
	 * @param componentListener
	 *            component listener
	 */
	public void addComponentListener(ComponentListener componentListener) {
		evjpanel.addComponentListener(componentListener);

	}

	/**
	 * @param dimension
	 *            new size
	 */
	public void setSize(Dimension dimension) {
		evjpanel.setSize(dimension);

	}

	/**
	 * @return prefered size
	 */
	public Dimension getPreferredSize() {
		return evjpanel.getPreferredSize();
	}

	/**
	 * @see EuclidianViewJPanelD#processMouseEventImpl(MouseEvent)
	 * @param e
	 *            mouse event
	 */
	protected void processMouseEvent(MouseEvent e) {
		evjpanel.processMouseEventImpl(e);
	}

	/**
	 * Initializes this panel
	 * 
	 * @param repaint
	 *            ignored parameter
	 */
	protected void initPanel(boolean repaint) {
		// preferred size
		evjpanel.setPreferredSize(null);
	}

	// @Override
	@Override
	public void setToolTipText(String plain) {
		if ((tooltipsInThisView == EuclidianStyleConstants.TOOLTIPS_ON)
				|| (tooltipsInThisView == EuclidianStyleConstants.TOOLTIPS_AUTOMATIC)) {
			evjpanel.setToolTipText(plain);
		}
	}

	@Override
	public int getWidth() {
		return evjpanel.getWidth();
	}

	@Override
	public int getHeight() {
		return evjpanel.getHeight();
	}

	@Override
	protected void updateSizeKeepDrawables() {

		// record the old coord system
		if ((getWidth() <= 0) || (getHeight() <= 0)) {
			return;
		}

		// real world values
		companion.setXYMinMaxForUpdateSize();
		if (app.getKernel().getConstruction() != null) {
			app.getKernel().getConstruction()
					.notifyEuclidianViewCE(EVProperty.SIZE);
		}
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
			resetBackgroundAndCache();
		}

		updateBackgroundImage();
	}

	private void createImage(GraphicsConfiguration gc) {
		if (gc != null) {
			bgImage = new GBufferedImageD(
					gc.createCompatibleImage(getWidth(), getHeight()));
			bgGraphics = bgImage.createGraphics();
			bgGraphics.setAntialiasing();
		}
	}

	@Override
	public void clearView() {
		evjpanel.removeAll(); // remove hotEqns
		resetLists();
		removeTextField();
		updateBackgroundImage(); // clear traces and images
		// We call this on file loading, so we don't want to mess up the
		// settings we have just loaded using initView
	}

	@Override
	public GColor getBackgroundCommon() {
		return GColorD.newColor(evjpanel.getBackground());
	}

	@Override
	public void setBackground(GColor bgColor) {
		evjpanel.setBackground(GColorD.getAwtColor(bgColor));
	}

	// temp image
	private final GGraphics2D g2Dtemp = new GGraphics2DD(
			new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB)
					.createGraphics());
	private boolean printScaleString;
	private ScreenReaderAdapter screenReader = new ScreenReaderAdapterD();

	/**
	 * @return temporary graphics that is stored in this view
	 */
	final public GGraphics2D getTempGraphics2D() {
		g2Dtemp.setFont(getApplication().getPlainFontCommon());
		return g2Dtemp;
	}

	@Override
	final public GGraphics2D getTempGraphics2D(GFont font) {
		g2Dtemp.setFont(font);

		return g2Dtemp;
	}

	@Override
	final protected void setStyleBarMode(int mode) {
		if (hasStyleBar()) {
			getStyleBar().setMode(mode);
		}
	}

	/**
	 * 
	 * @return new euclidian style bar
	 */
	@Override
	protected EuclidianStyleBarD newEuclidianStyleBar() {
		return new EuclidianStyleBarD(this);
	}

	@Override
	protected MyZoomerD newZoomer() {
		return new MyZoomerD(this);
	}

	/**
	 * @return whether to print scalestring
	 */
	public boolean isPrintScaleString() {
		return printScaleString;
	}

	/**
	 * 
	 * @param printScaleString
	 *            whether to print scalestring
	 */
	public void setPrintScaleString(boolean printScaleString) {
		this.printScaleString = printScaleString;
	}

	private String getScaleString() {
		if (isPrintScaleString()) {
			Localization loc = getApplication().getLocalization();
			StringBuilder sb = new StringBuilder(
					loc.getMenu("ScaleInCentimeter"));
			if (printingScale <= 1) {
				sb.append(": 1:");
				sb.append(printScaleNF.format(1 / printingScale));
			} else {
				sb.append(": ");
				sb.append(printScaleNF.format(printingScale));
				sb.append(":1");
			}

			// add yAxis scale too?
			if (!DoubleUtil.isEqual(getScaleRatio(), 1.0)) {
				sb.append(" (x), ");
				double yPrintScale = (printingScale * getYscale())
						/ getXscale();
				if (yPrintScale < 1) {
					sb.append("1:");
					sb.append(printScaleNF.format(1 / yPrintScale));
				} else {
					sb.append(printScaleNF.format(yPrintScale));
					sb.append(":1");
				}
				sb.append(" (y)");
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public boolean suggestRepaint() {
		return false;
		// only used in web for now
	}

	@Override
	public void closeDropdowns() {
		closeAllDropDowns();
	}

	@Override
	public void setCursor(EuclidianCursor cursor) {
		switch (cursor) {
		case HIT:
			setHitCursor();
			return;
		case DRAG:
			setDragCursor();
			return;
		case MOVE:
			setMoveCursor();
			return;
		case DEFAULT:
			setDefaultCursor();
			return;
		case RESIZE_X:
			setResizeXAxisCursor();
			return;
		case RESIZE_Y:
			setResizeYAxisCursor();
			return;
		case TRANSPARENT:
			setTransparentCursor();
			return;
		}

	}

	@Override
	public ScreenReaderAdapter getScreenReader() {
		return screenReader;
	}

	@Override
	protected EuclidianStyleBar newDynamicStyleBar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawStringWithOutline(GGraphics2D g2c, String text, double x,
			double y, GColor col) {
		
		// no outline if label color == background color
		if (!app.isExporting() && g2c instanceof GGraphics2DD
				&& !col.equals(getBackgroundCommon())
				&& !app.fileVersionBefore(LABEL_OUTLINES_FROM)) {
			g2c.setColor(getBackgroundCommon());
			g2c.drawString(text, x + 1, y);
			g2c.drawString(text, x - 1, y);
			g2c.drawString(text, x, y + 1);
			g2c.drawString(text, x, y - 1);
		}
		// default (no outline)
		super.drawStringWithOutline(g2c, text, x, y, col);
	}

}
