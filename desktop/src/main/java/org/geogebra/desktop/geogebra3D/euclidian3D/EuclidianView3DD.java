package org.geogebra.desktop.geogebra3D.euclidian3D;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
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
import org.geogebra.common.awt.GPointWithZ;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian3D.Mouse3DEvent;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.RendererType;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GDimensionD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.euclidian.EuclidianControllerListeners;
import org.geogebra.desktop.euclidian.EuclidianViewJPanelD;
import org.geogebra.desktop.euclidian.MyZoomerD;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.export.GraphicExportDialog;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererCheckGLVersionD;
import org.geogebra.desktop.geogebra3D.euclidianInput3D.Mouse3DEventD;
import org.geogebra.desktop.io.MyImageIO;
import org.geogebra.desktop.main.AppD;

/**
 * 3D view for desktop
 * 
 * @author mathieu
 * 
 */
public class EuclidianView3DD extends EuclidianView3D
		implements EuclidianViewInterfaceD {

	/** Java component for this view */
	protected EuclidianViewJPanelD evjpanel;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 * @param settings
	 *            settings
	 */
	public EuclidianView3DD(EuclidianController3D ec,
			EuclidianSettings settings) {

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
		evjpanel = new EuclidianViewJPanelD(this);

		canvas = (Component) renderer.getCanvas();
		getJPanel().setLayout(new BorderLayout());
		getJPanel().add(BorderLayout.CENTER, canvas);

		// register Listener
		((EuclidianControllerListeners) getEuclidianController())
				.addListenersTo(canvas);
		canvas.setFocusable(true);

	}

	@Override
	protected Renderer createRenderer() {

		// set stereo on/off
		getCompanion().setIsStereoBuffered(((App3D) app).isStereo3D());

		// lines below for testing

		// return new RendererCheckGLVersionD(this, !app.isApplet(),
		// RendererType.SHADER);

//		 return new RendererCheckGLVersionD(this, !app.isApplet(),
//		 RendererType.GL2);


		// we don't want shaders with win os < vista
		if (!app.isApplet() && !AppD.WINDOWS_VISTA_OR_EARLIER) {
			return new RendererCheckGLVersionD(this, canUseCanvas());
		}

		if (app.useShaders()) {
			return new RendererCheckGLVersionD(this, !app.isApplet(), RendererType.SHADER);
		}

		return new RendererCheckGLVersionD(this, canUseCanvas(), RendererType.GL2);

	}

	private boolean canUseCanvas() {
		if (app.isApplet()) {
			return false;
		}

		// TODO remove that (quick fix for jogl 2.3.2)
		if (AppD.MAC_OS) {
			Log.debug("XXXXXXXXXXXXXXX mac osx");
			return false;
		}

		return true;
	}

	@Override
	public void setBackground(GColor updatedColor, GColor applyedColor) {
		super.setBackground(updatedColor, applyedColor);
		evjpanel.setBackground(GColorD.getAwtColor(bgApplyedColor));
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
		return new GFontD(evjpanel.getFont());
	}

	/**
	 * @return mouse position
	 */
	@Override
	public java.awt.Point getMousePosition() {
		return evjpanel.getMousePosition();
	}

	/**
	 * @see JPanel#getFontMetrics(Font)
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
		// not needed for 3D
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
	final public GGraphics2D getTempGraphics2D(GFont font) {
		g2Dtemp.setFont(GFontD.getAwtFont(font));

		return new GGraphics2DD(g2Dtemp);
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
		return ((AppD) app).getShiftDown();
	}

	@Override
	protected void setDefault2DCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public App3D getApplication() {
		return (App3D) this.app;
	}

	@Override
	public GBufferedImage getExportImage(double scale) {
		return getExportImage(scale, false, ExportType.PNG);
	}

	@Override
	public GBufferedImage getExportImage(double scale, boolean transparency,
			ExportType exportType)
			throws OutOfMemoryError {
		getRenderer().needExportImage(scale, true);

		return getRenderer().getExportImage();
	}

	private boolean exportToClipboard;
	private File exportFile;
	private int exportDPI;

	@Override
	public void exportImagePNG(double scale, boolean transparency, int dpi,
			File file, boolean exportToClipboard0, ExportType exportType) {

		exportDPI = dpi;
		exportFile = file;
		this.exportToClipboard = exportToClipboard0;

		getRenderer().needExportImage(scale, false);
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
			BufferedImage img = GBufferedImageD
					.getAwtBufferedImage(getRenderer().getExportImage());
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

	@Override
	public boolean suggestRepaint() {
		return false;
		// only for web
	}

	@Override
	public void exportPaintPre(GGraphics2D g2d, double scale,
			boolean transparency) {
		Log.error("exportPaintPre unimplemented");

	}

	@Override
	public void repaintView() {
		// done by FPS animator
	}

	@Override
	protected void drawBackgroundImage(GGraphics2D g2d) {
		// nothing to do here
	}

	@Override
	public Mouse3DEvent createMouse3DEvent(GPointWithZ mouse3DLoc) {
		return new Mouse3DEventD(mouse3DLoc, getJPanel());
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

}
