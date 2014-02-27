package geogebra.touch.gui.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.javax.swing.GBox;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.javax.swing.GBoxW;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class EuclidianViewT extends EuclidianViewWeb {

	private Canvas canvas;
	private EuclidianViewPanel panel;
	
	public static final int SLIDER_OFFSET_T = 110;

	EuclidianViewT(final EuclidianViewPanel euclidianViewPanel,
			final TouchController ec, final Widget widget, final int width,
			final int height) {
		super(ec, new Settings().getEuclidian(1));

		ec.setView(this);

		this.setAllowShowMouseCoords(false);
		this.setRightAngleStyle(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT);

		this.init(euclidianViewPanel, widget, width, height);
		// make sure we listen to the changes of settings, eg if file is loaded
		if (this.evNo == 1 || this.evNo == 2) {
			final EuclidianSettings es = this.app.getSettings().getEuclidian(
					this.evNo);
			this.settingsChanged(es);
			es.addListener(this);
		}
	}

	@Override
	public void setShowAxesRatio(boolean b) {
		//never show axes ratio (prevents move!)
		super.setShowAxesRatio(false);
	}

	@Override
	public void add(final GBox box) {
		this.panel.addBox(GBoxW.getImpl(box), (int) box.getBounds().getX(),
				(int) box.getBounds().getY());
	}

	@Override
	protected void doDrawPoints(final GeoImage gi,
			final List<GPoint> penPoints2, final GColor penColor,
			final int penLineStyle, final int penSize) {
	}

	@Override
	protected boolean drawPlayButtonInThisView() {
		return true;
	}

	@Override
	protected void drawResetIcon(final GGraphics2D g) {
		// FIXME implement!
		throw new UnsupportedOperationException();
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	@Override
	public EuclidianController getEuclidianController() {
		return this.euclidianController;
	}

	@Override
	public int getSliderOffsetY() {
		return EuclidianViewT.SLIDER_OFFSET_T;
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	/**
	 * This method has to be called before using g2p.
	 * 
	 */
	private void init(final EuclidianViewPanel euclidianViewPanel,
			final Widget widget, final int width, final int height) {
		this.panel = euclidianViewPanel;
		this.canvas = Canvas.createIfSupported();
		this.g2p = new GGraphics2DW(this.canvas);
		this.setCoordinateSpaceSize(width, height);

		final TouchEventController touchController = new TouchEventController(
				(TouchController) this.getEuclidianController(), widget);
		TouchEntryPoint.getLookAndFeel().attachExternalEvents(this,
				euclidianViewPanel.getElement());
		euclidianViewPanel.addDomHandler(touchController,
				TouchStartEvent.getType());
		euclidianViewPanel.addDomHandler(touchController,
				TouchEndEvent.getType());
		euclidianViewPanel.addDomHandler(touchController,
				TouchMoveEvent.getType());

		// Listeners for Desktop
		euclidianViewPanel.addDomHandler(touchController,
				MouseDownEvent.getType());
		euclidianViewPanel.addDomHandler(touchController,
				MouseMoveEvent.getType());
		euclidianViewPanel.addDomHandler(touchController,
				MouseUpEvent.getType());
		euclidianViewPanel.addDomHandler(touchController,
				MouseOutEvent.getType());
		euclidianViewPanel.addDomHandler(touchController,
				MouseWheelEvent.getType());

		this.updateFonts();
		this.initView(true);
		this.attachView();
		this.doRepaint();
	}

	@Override
	protected void initCursor() {
	}

	@Override
	public void remove(final GBox box) {
		this.panel.removeBox(GBoxW.getImpl(box));
	}

	@Override
	public void requestFocus() {
	}

	@Override
	public boolean requestFocusInWindow() {
		this.g2p.getCanvas().getCanvasElement().focus();
		return true;
	}

	@Override
	public void setDefaultCursor() {
	}

	@Override
	public void setDragCursor() {
	}

	@Override
	public void setEraserCursor() {
	}

	@Override
	public void setHitCursor() {
	}

	@Override
	public void setMoveCursor() {
	}

	void setPixelSize(final int width, final int height) {
		this.g2p.setCoordinateSpaceSize(width, height);
		this.canvas.setPixelSize(width, height);
		this.updateSize();
		this.doRepaint2();
	}

	@Override
	public void setPreferredSize(final GDimension preferredSize) {
	}

	@Override
	public void setResizeXAxisCursor() {
	}

	@Override
	public void setResizeYAxisCursor() {
	}

	@Override
	protected void setStyleBarMode(final int mode) {
	}

	@Override
	public void setToolTipText(final String plainTooltip) {
	}

	@Override
	public void setTransparentCursor() {
	}

	@Override
	public boolean hitAnimationButton(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		// TODO Auto-generated method stub
		return null;
	}
}
