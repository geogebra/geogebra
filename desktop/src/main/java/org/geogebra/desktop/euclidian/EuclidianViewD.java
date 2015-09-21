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
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.Border;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.desktop.awt.GBasicStrokeD;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceDesktop;
import org.geogebra.desktop.export.GraphicExportDialog;
import org.geogebra.desktop.export.PrintPreview;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.io.MyImageIO;
import org.geogebra.desktop.main.AppD;

/**
 * 
 * @author Markus Hohenwarter
 */
public class EuclidianViewD extends EuclidianView implements
		EuclidianViewInterfaceDesktop, Printable {

	/**
	 * Rendering hints for the graphics
	 */
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
	protected EuclidianViewJPanel evjpanel;

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

		evjpanel = new EuclidianViewJPanel(this);

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

	public void setDragCursor() {

		if (getMode() == EuclidianConstants.MODE_TRANSLATEVIEW) {
			setGrabbingCursor();
		}

		else if (getApplication().useTransparentCursorWhenDragging) {
			setCursor(getApplication().getTransparentCursor());
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

	}

	@Override
	public void setTransparentCursor() {

		setCursor(getApplication().getTransparentCursor());
	}

	@Override
	public void setEraserCursor() {

		setCursor(getApplication().getEraserCursor());

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

	/**
	 * Set the cursor to grabbing hand
	 */
	public void setGrabbingCursor() {
		// TODO gui/image/cursor..
		setCursor(getCursorForImage("grabbing"));
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

		switch (getMode()) {
		case EuclidianConstants.MODE_ZOOM_IN:
			defaultCursor = getCursorForImage("zoomin");
			break;

		case EuclidianConstants.MODE_ZOOM_OUT:
			defaultCursor = getCursorForImage("zoomout");
			break;

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			defaultCursor = getCursorForImage("grab");
			break;
		}

		setDefaultCursor();
	}

	protected Cursor getCursorForImage(String name) {
		return getCursorForImage(getApplication()
		.getInternalImage("/gui/images/cursor_"+name+".gif"));
		
	}
	/**
	 * @param image
	 *            image file
	 * @return cursor created from image
	 */
	private Cursor getCursorForImage(Image image) {
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
				Cursor cursor = tk.createCustomCursor(image,
						new java.awt.Point(16, 16), "custom cursor");
				return cursor;
			} catch (Exception exc) {
				// Catch exceptions so that we don't try to set a null
				// cursor
				App.debug("Unable to create custom cursor.");
			}

		}
		return null;
	}

	@Override
	public void setDefRenderingHints(org.geogebra.common.awt.GGraphics2D g2) {
		g2.setRenderingHints(defRenderingHints);
	}

	public static int printTitle(Graphics2D g2d, String scaleString,
			PageFormat pageFormat, AppD app) {
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		// construction title
		int y = 0;
		Construction cons = app.getKernel().getConstruction();
		String title = cons.getTitle();
		if (!title.equals("")) {
			GFont titleFont = app.getBoldFontCommon().deriveFont(GFont.BOLD,
					app.getBoldFont().getSize() + 2);
			g2d.setFont(org.geogebra.desktop.awt.GFontD.getAwtFont(titleFont));
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

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if(!PrintPreview.justPreview){
			pageIndex = PrintPreview.computePageIndex(pageIndex);
		}
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
	 */
	public void exportPaint(Graphics2D g2d, double scale,
			ExportType exportType) {
		exportPaint(new GGraphics2DD(g2d), scale, false, exportType);
	}

	@Override
	public void exportPaintPre(org.geogebra.common.awt.GGraphics2D g2d,
			double scale, boolean transparency) {
		g2d.scale(scale, scale);

		// clipping on selection rectangle
		if (getSelectionRectangle() != null) {
			GRectangle rect = getSelectionRectangle();
			g2d.setClip(0, 0, (int) rect.getWidth(), (int) rect.getHeight());
			g2d.translate(-rect.getX(), -rect.getY());
			// Application.debug(rect.x+" "+rect.y+" "+rect.width+" "+rect.height);
		} else {
			// use points Export_1 and Export_2 to define corner
			try {
				// Construction cons = kernel.getConstruction();
				GeoPoint export1 = (GeoPoint) kernel.lookupLabel(EXPORT1);
				GeoPoint export2 = (GeoPoint) kernel.lookupLabel(EXPORT2);
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
				GGraphics2DD.getAwtGraphics(g2d).setRenderingHint(
						RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				GGraphics2DD.getAwtGraphics(g2d).drawImage(
						GBufferedImageD.getAwtBufferedImage(bgImage), 0, 0,
						getJPanel());
			}
		} else {
			// just clear the background if transparency is disabled (clear =
			// draw background color)
			drawBackground(g2d, !transparency);
		}

		GGraphics2DD.getAwtGraphics(g2d).setRenderingHint(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		setAntialiasing(g2d);
	}

	/**
	 * Returns image of drawing pad sized according to the given scale factor.
	 * 
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 * @return image of drawing pad sized according to the given scale factor.
	 * @throws OutOfMemoryError
	 *             if the requested image is too big
	 */
	public BufferedImage getExportImage(double scale) throws OutOfMemoryError {
		return getExportImage(scale, false);
	}

	/**
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 * @param transparency
	 *            true for transparent image
	 * @return image
	 * @throws OutOfMemoryError
	 *             if the requested image is too big
	 */
	public BufferedImage getExportImage(double scale, boolean transparency)
			throws OutOfMemoryError {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);
		BufferedImage img = createBufferedImage(width, height, transparency);
		exportPaint(new GGraphics2DD(img.createGraphics()), scale, transparency,
				ExportType.PNG);
		img.flush();
		return img;
	}
	
	public void exportImagePNG(double scale, boolean transparency, int dpi,
			File file, boolean exportToClipboard) {
		
		try {
			BufferedImage img = getExportImage(scale, transparency);
			MyImageIO.write(img, "png", dpi, file);
			if (exportToClipboard) {
				GraphicExportDialog.sendToClipboard(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * @param width
	 *            pixel width
	 * @param height
	 *            pixel height
	 * @param transparency
	 *            true for transparent
	 * @return image
	 * @throws OutOfMemoryError
	 *             if the requested image is too big
	 */
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
	protected void drawResetIcon(org.geogebra.common.awt.GGraphics2D g) {
		// need to use getApplet().width rather than width so that
		// it works with applet rescaling
		int w = getApplication().onlyGraphicsViewShowing() ? getApplication()
				.getApplet().width : getWidth() + 2;
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
	final protected void drawAnimationButtons(org.geogebra.common.awt.GGraphics2D g2) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return;
		}

		int x = 3;
		int y = getHeight() - 27;

		/*
		 * if (highlightAnimationButtons) { // draw filled circle to highlight
		 * button g2.setColor(org.geogebra.common.awt.GColor.DARK_GRAY); } else
		 * { g2.setColor(org.geogebra.common.awt.GColor.LIGHT_GRAY); }
		 * 
		 * g2.setStroke(org.geogebra.common.euclidian.EuclidianStatic
		 * .getDefaultStroke());
		 * 
		 * // draw pause or play button g2.drawRect(x - 2, y - 2, 18, 18);
		 */
		Image img = kernel.isAnimationRunning() ? getPauseImage(highlightAnimationButtons)
				: getPlayImage(highlightAnimationButtons);
		GGraphics2DD.getAwtGraphics(g2).drawImage(img, x, y, null);
	}

	public final boolean hitAnimationButton(int x, int y) {
		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return false;
		}

		return kernel.needToShowAnimationButton() && (x <= 27)
				&& (y >= (getHeight() - 27));
	}

	public EuclidianController getEuclidianController() {
		return euclidianController;
	}

	/**
	 * @return graphics of the underlying component
	 */
	@Override
	public org.geogebra.common.awt.GGraphics2D getGraphicsForPen() {
		return new GGraphics2DD((Graphics2D) evjpanel.getGraphics());

	}

	@Override
	protected void doDrawPoints(GeoImage gi,
			List<org.geogebra.common.awt.GPoint> penPoints2,
			org.geogebra.common.awt.GColor penColor, int penLineStyle, int penSize) {
		PolyBezier pb = new PolyBezier(penPoints2);
		BufferedImage penImage2 = gi.getFillImage() == null ? null
				: (BufferedImage) ((MyImageD) gi.getFillImage()).getImage();
		boolean giNeedsInit = false;
		if (penImage2 == null) {
			giNeedsInit = true;
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();

			GraphicsDevice gs = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			penImage2 = gc.createCompatibleImage(Math.max(300, getWidth()),
					Math.max(getHeight(), 200), Transparency.BITMASK);
		}
		Graphics2D g2d = (Graphics2D) penImage2.getGraphics();

		setAntialiasing(g2d);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);

		g2d.setStroke(GBasicStrokeD.getAwtStroke(org.geogebra.common.euclidian.EuclidianStatic.getStroke(
				2 * penSize,
				(penPoints2.size() <= 2) ? EuclidianStyleConstants.LINE_TYPE_FULL
						: penLineStyle)));
		g2d.setColor(GColorD.getAwtColor(penColor));

		g2d.draw(pb.gp);
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();

		app.refreshViews(); // clear trace
		// TODO -- did we need the following line?
		// ev.getGraphics().drawImage(penImage2, penOffsetX, penOffsetY, null);

		if (giNeedsInit) {
			String fileName = ((AppD) app).createImage(new MyImageD(penImage2),
					"penimage.png");
			// Application.debug(fileName);
			GeoImage geoImage = null;
			// if (gi == null)
			// geoImage = new GeoImage(app.getKernel().getConstruction());
			// else
			geoImage = gi;
			geoImage.setImageFileName(fileName);
			geoImage.setTooltipMode(GeoElement.TOOLTIP_OFF);
			GeoPoint corner = (new GeoPoint(app.getKernel().getConstruction(),
					null, ev.toRealWorldCoordX(0),
					ev.toRealWorldCoordY(penImage2.getHeight()), 1.0));
			GeoPoint corner2 = (new GeoPoint(app.getKernel().getConstruction(),
					null, ev.toRealWorldCoordX(penImage2.getWidth()),
					ev.toRealWorldCoordY(penImage2.getHeight()), 1.0));
			corner.setLabelVisible(false);
			corner2.setLabelVisible(false);
			corner.update();
			corner2.update();
			// if (gi == null)
			// geoImage.setLabel(null);
			geoImage.setCorner(corner, 0);
			geoImage.setCorner(corner2, 1);

			// need 3 corner points if axes ratio isn't 1:1
			if (!Kernel.isEqual(ev.getXscale(), ev.getYscale())) {
				GeoPoint corner4 = (new GeoPoint(app.getKernel()
						.getConstruction(), null, ev.toRealWorldCoordX(0),
						ev.toRealWorldCoordY(0), 1.0));
				corner4.setLabelVisible(false);
				corner4.update();
				geoImage.setCorner(corner4, 2);
			}

			geoImage.update();

			GeoImage.updateInstances(app);

		}

		// doesn't work as all changes are in the image not the XML
		// app.storeUndoInfo();
		app.setUnsaved();

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
	public void setCursor(Cursor cursor) {
		evjpanel.setCursor(cursor);
	}

	public boolean hasFocus() {
		return evjpanel.hasFocus();
	}

	public void repaint() {
		this.updateBackgroundIfNecessary();
		evjpanel.repaint();
	}

	@Override
	public void paintBackground(org.geogebra.common.awt.GGraphics2D g2) {
		g2.drawImage(bgImage, null, 0, 0);
	}

	@Override
	public void add(org.geogebra.common.javax.swing.GBox box) {
		evjpanel.add(((org.geogebra.desktop.javax.swing.BoxD) box).getImpl());
	}

	@Override
	public void remove(org.geogebra.common.javax.swing.GBox box) {
		evjpanel.remove(((org.geogebra.desktop.javax.swing.BoxD) box).getImpl());
	}

	/**
	 * @return underlying component
	 */
	public JPanel getJPanel() {
		return evjpanel;
	}

	/**
	 * This view should be focused
	 */
	public void requestFocus() {
		evjpanel.requestFocus();
	}

	@Override
	public GFont getFont() {
		// TODO Auto-generated method stub
		return new org.geogebra.desktop.awt.GFontD(evjpanel.getFont());
	}

	/**
	 * @return mouse position
	 */
	public java.awt.Point getMousePosition() {
		return evjpanel.getMousePosition();
	}

	/**
	 * @see JPanel#getFontMetrics(java.awt.Font)
	 * @param font
	 *            font
	 * @return font metrics
	 */
	public FontMetrics getFontMetrics(java.awt.Font font) {
		return evjpanel.getFontMetrics(font);
	}

	/**
	 * @return whethe this view is visible
	 */
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
	public void setPreferredSize(org.geogebra.common.awt.GDimension preferredSize) {
		evjpanel.setPreferredSize(org.geogebra.desktop.awt.GDimensionD
				.getAWTDimension(preferredSize));
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
	 * @see EuclidianViewJPanel#processMouseEventImpl(MouseEvent)
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
	public void setToolTipText(String plain) {
		if ((tooltipsInThisView == EuclidianStyleConstants.TOOLTIPS_ON)
				|| (tooltipsInThisView == EuclidianStyleConstants.TOOLTIPS_AUTOMATIC)) {
			evjpanel.setToolTipText(plain);
		}
	}

	public int getWidth() {
		return evjpanel.getWidth();
	}

	public int getHeight() {
		return evjpanel.getHeight();
	}

	@Override
	protected void updateSizeKeepDrawables() {

		// record the old coord system

		setWidth(getWidth());
		setHeight(getHeight());
		if ((getWidth() <= 0) || (getHeight() <= 0)) {
			return;
		}

		// real world values
		companion.setXYMinMaxForUpdateSize();
		app.getKernel().getConstruction().notifyEuclidianViewCE(true);
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
		}

		updateBackgroundImage();
	}

	private void createImage(GraphicsConfiguration gc) {
		if (gc != null) {
			bgImage = new org.geogebra.desktop.awt.GBufferedImageD(
					gc.createCompatibleImage(getWidth(), getHeight()));
			bgGraphics = bgImage.createGraphics();
			if (antiAliasing) {
				setAntialiasing(bgGraphics);
			}
		}
	}

	@Override
	public void drawActionObjects(org.geogebra.common.awt.GGraphics2D g2) {
		// TODO layers for Buttons and Textfields
		// for cross-platform UI the stroke must be reset to show buttons
		// properly, see #442
		g2.setStroke(org.geogebra.common.euclidian.EuclidianStatic
				.getDefaultStroke());
		evjpanel.paintChildren(org.geogebra.desktop.awt.GGraphics2DD.getAwtGraphics(g2)); // draws
																				// Buttons
																				// and
																				// Textfields
	}

	public void clearView() {
		evjpanel.removeAll(); // remove hotEqns
		resetLists();
		updateBackgroundImage(); // clear traces and images
		// We call this on file loading, so we don't want to mess up the settings we have just loaded using initView
	}

	public org.geogebra.common.awt.GColor getBackgroundCommon() {
		return new org.geogebra.desktop.awt.GColorD(evjpanel.getBackground());
	}

	@Override
	public void setBackground(org.geogebra.common.awt.GColor bgColor) {
		evjpanel.setBackground(org.geogebra.desktop.awt.GColorD.getAwtColor(bgColor));
	}

	// temp image
	private final Graphics2D g2Dtemp = new BufferedImage(5, 5,
			BufferedImage.TYPE_INT_RGB).createGraphics();
	private boolean printScaleString;

	/**
	 * @return temporary graphics that is stored in this view
	 */
	final public Graphics2D getTempGraphics2D() {
		g2Dtemp.setFont(getApplication().getPlainFont());
		return g2Dtemp;
	}

	@Override
	final public org.geogebra.common.awt.GGraphics2D getTempGraphics2D(
			org.geogebra.common.awt.GFont font) {
		g2Dtemp.setFont(org.geogebra.desktop.awt.GFontD.getAwtFont(font)); // Michael
																// Borcherds
																// 2008-06-11
																// bugfix for
		// Corner[text,n]
		return new org.geogebra.desktop.awt.GGraphics2DD(g2Dtemp);
	}

	/**
	 * Sets antialiasing of given graphics to ON (both for text and drawings)
	 * 
	 * @param g2
	 *            graphics
	 */
	final public static void setAntialiasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	@Override
	final public void setAntialiasing(org.geogebra.common.awt.GGraphics2D g2) {
		setAntialiasing(org.geogebra.desktop.awt.GGraphics2DD.getAwtGraphics(g2));
	}

	@Override
	final protected void setHeight(int height) {
		//
	}

	@Override
	final protected void setWidth(int width) {
		//
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
	protected EuclidianStyleBarD newEuclidianStyleBar() {
		return new EuclidianStyleBarD(this);
	}

	@Override
	protected MyZoomerD newZoomer() {
		return new MyZoomerD(this);
	}

	public boolean isPrintScaleString() {
		return printScaleString;
	}

	public void setPrintScaleString(boolean printScaleString) {
		this.printScaleString = printScaleString;
	}

	public String getScaleString() {
		if (isPrintScaleString()) {
			StringBuilder sb = new StringBuilder(getApplication().getPlain(
					"ScaleInCentimeter"));
			if (printingScale <= 1) {
				sb.append(": 1:");
				sb.append(printScaleNF.format(1 / printingScale));
			} else {
				sb.append(": ");
				sb.append(printScaleNF.format(printingScale));
				sb.append(":1");
			}

			// add yAxis scale too?
			if (!Kernel.isEqual(getScaleRatio(), 1.0)) {
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

	public boolean suggestRepaint() {
		return false;
		// only used in web for now
	}

}
