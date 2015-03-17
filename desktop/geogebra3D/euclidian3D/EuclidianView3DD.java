package geogebra3D.euclidian3D;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.debug.Log;
import geogebra.euclidian.EuclidianControllerListeners;
import geogebra.euclidian.EuclidianViewD;
import geogebra.euclidian.EuclidianViewJPanel;
import geogebra.euclidian.MyZoomerD;
import geogebra.euclidianND.EuclidianViewInterfaceDesktop;
import geogebra.export.GraphicExportDialog;
import geogebra.io.MyImageIO;
import geogebra.main.AppD;
import geogebra3D.App3D;
import geogebra3D.euclidian3D.opengl.RendererD;
import geogebra3D.euclidian3D.opengl.RendererGLPickingGL2;
import geogebra3D.euclidian3D.opengl.RendererShaders;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * 3D view for desktop
 * 
 * @author mathieu
 * 
 */
public class EuclidianView3DD extends EuclidianView3D implements
		EuclidianViewInterfaceDesktop {

	/** Java component for this view */
	protected EuclidianViewJPanel evjpanel;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 * @param settings
	 *            settings
	 */
	public EuclidianView3DD(EuclidianController3D ec, EuclidianSettings settings) {

		super(ec, settings);

		initView(false);

		EuclidianSettings es = null;
		if (settings != null) {
			es = settings;
		} else {
			es = getApplication().getSettings().getEuclidian(3);
		}

		if (es != null) {
			settingsChanged(es);
			es.addListener(this);
		}

	}

	private Component canvas;

	@Override
	protected void createPanel() {
		evjpanel = new EuclidianViewJPanel(this);

		canvas = (Component) ((RendererD) renderer).canvas;
		getJPanel().setLayout(new BorderLayout());
		getJPanel().add(BorderLayout.CENTER, canvas);

		// register Listener
		((EuclidianControllerListeners) getEuclidianController())
				.addListenersTo(canvas);
		canvas.setFocusable(true);

	}

	@Override
	protected Renderer createRenderer() {

		if (((App3D) app).useShaders()) {
			return new RendererShaders(this, !app.isApplet());
		}
		return new RendererGLPickingGL2(this, !app.isApplet());

	}

	@Override
	public void setBackground(GColor color) {
		if (color != null) {
			this.bgColor = color;
			if (renderer != null) {
				renderer.setWaitForUpdateClearColor();
			}
			evjpanel.setBackground(geogebra.awt.GColorD.getAwtColor(bgColor));
		}

	}

	@Override
	public void setTransparentCursor() {

		setCursor(((AppD) app).getTransparentCursor());

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
	public void paintBackground(geogebra.common.awt.GGraphics2D g2) {
		g2.drawImage(bgImage, null, 0, 0);
	}

	@Override
	public void add(geogebra.common.javax.swing.GBox box) {
		evjpanel.add(((geogebra.javax.swing.BoxD) box).getImpl());
	}

	@Override
	public void remove(geogebra.common.javax.swing.GBox box) {
		evjpanel.remove(((geogebra.javax.swing.BoxD) box).getImpl());
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
		return new geogebra.awt.GFontD(evjpanel.getFont());
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
	public void setPreferredSize(geogebra.common.awt.GDimension preferredSize) {
		evjpanel.setPreferredSize(geogebra.awt.GDimensionD
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
		canvas.addMouseListener(ml);
	}

	/**
	 * @see JPanel#removeComponentListener(ComponentListener)
	 * @param ml
	 *            mouse listener
	 */
	public void removeMouseListener(MouseListener ml) {
		canvas.removeMouseListener(ml);
	}

	/**
	 * @see JPanel#addMouseMotionListener(MouseMotionListener)
	 * @param mml
	 *            mouse motion listener
	 */
	public void addMouseMotionListener(MouseMotionListener mml) {
		canvas.addMouseMotionListener(mml);
	}

	/**
	 * @see JPanel#removeMouseMotionListener(MouseMotionListener)
	 * @param mml
	 *            mouse motion listener
	 */
	public void removeMouseMotionListener(MouseMotionListener mml) {
		canvas.removeMouseMotionListener(mml);
	}

	/**
	 * @see JPanel#addMouseWheelListener(MouseWheelListener)
	 * @param mwl
	 *            mouse wheel listener
	 */
	public void addMouseWheelListener(MouseWheelListener mwl) {
		canvas.addMouseWheelListener(mwl);
	}

	/**
	 * @see JPanel#removeMouseWheelListener(MouseWheelListener)
	 * @param mwl
	 *            mouse wheel listener
	 */
	public void removeMouseWheelListener(MouseWheelListener mwl) {
		canvas.removeMouseWheelListener(mwl);
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
		canvas.addComponentListener(componentListener);

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
			bgImage = new geogebra.awt.GBufferedImageD(
					gc.createCompatibleImage(getWidth(), getHeight()));
			bgGraphics = bgImage.createGraphics();
			if (antiAliasing) {
				setAntialiasing(bgGraphics);
			}
		}
	}

	@Override
	public void drawActionObjects(geogebra.common.awt.GGraphics2D g2) {
		// TODO layers for Buttons and Textfields
		// for cross-platform UI the stroke must be reset to show buttons
		// properly, see #442
		g2.setStroke(geogebra.common.euclidian.EuclidianStatic
				.getDefaultStroke());
		evjpanel.paintChildren(geogebra.awt.GGraphics2DD.getAwtGraphics(g2)); // draws
																				// Buttons
																				// and
																				// Textfields
	}

	// temp image
	private final Graphics2D g2Dtemp = new BufferedImage(5, 5,
			BufferedImage.TYPE_INT_RGB).createGraphics();

	/**
	 * @return temporary graphics that is stored in this view
	 */
	final public Graphics2D getTempGraphics2D() {
		g2Dtemp.setFont(((AppD) app).getPlainFont());
		return g2Dtemp;
	}

	@Override
	final public geogebra.common.awt.GGraphics2D getTempGraphics2D(
			geogebra.common.awt.GFont font) {
		g2Dtemp.setFont(geogebra.awt.GFontD.getAwtFont(font)); // Michael
																// Borcherds
																// 2008-06-11
																// bugfix for
		// Corner[text,n]
		return new geogebra.awt.GGraphics2DD(g2Dtemp);
	}

	@Override
	final public void setAntialiasing(geogebra.common.awt.GGraphics2D g2) {
		EuclidianViewD.setAntialiasing(geogebra.awt.GGraphics2DD
				.getAwtGraphics(g2));
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

	@Override
	protected MyZoomerD newZoomer() {
		return new MyZoomerD(this);
	}

	@Override
	protected boolean getShiftDown() {
		return AppD.getShiftDown();
	}

	@Override
	protected void setDefault2DCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public App3D getApplication() {
		return (App3D) this.app;
	}

	public BufferedImage getExportImage(double scale) {
		return getExportImage(scale, false);
	}

	public BufferedImage getExportImage(double scale, boolean transparency)
			throws OutOfMemoryError {
		((RendererD) getRenderer()).needExportImage(scale);

		return ((RendererD) getRenderer()).getExportImage();
	}

	private boolean exportToClipboard;
	private File exportFile;
	private int exportDPI;

	@Override
	public void exportImagePNG(double scale, boolean transparency, int dpi,
			File file, boolean exportToClipboard) {

		exportDPI = dpi;
		exportFile = file;
		this.exportToClipboard = exportToClipboard;

		((RendererD) getRenderer()).needExportImage(scale);
	}

	/**
	 * write current renderer's image to current export file
	 */
	public void writeExportImage() {

		if (exportFile == null) {
			Log.debug("exportFile not set");
			return;
		}

		try {
			BufferedImage img = ((RendererD) getRenderer()).getExportImage();
			MyImageIO.write(img, "png", exportDPI, exportFile);
			if (exportToClipboard) {
				GraphicExportDialog.sendToClipboard(exportFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		return new EuclidianStyleBar3D(this);
	}

	public boolean suggestRepaint() {
		return false;
		// only for web
	}

	@Override
	public void exportPaintPre(GGraphics2D g2d, double scale,
			boolean transparency) {
		Log.error("exportPaintPre unimplemented");

	}
}
