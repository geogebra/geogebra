package geogebra.mobile.gui.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.factories.AwtFactory;
import geogebra.common.javax.swing.GBox;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.settings.Settings;
import geogebra.mobile.controller.MobileController;
import geogebra.web.awt.GGraphics2DW;
import geogebra.web.euclidian.MyZoomerW;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.logging.client.LoggingPopup;
import com.google.gwt.user.client.Window;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.dom.client.recognizer.pinch.PinchEvent;
import com.googlecode.mgwt.dom.client.recognizer.pinch.PinchHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class EuclidianViewM extends EuclidianView
{
	static Logger logger = Logger.getLogger("");
	LoggingPopup popup;

	// set in setCanvas
	GGraphics2DW g2p = null;
	Canvas canvas;

	private GColor backgroundColor = GColor.white;
	private GGraphics2D g2dtemp;

	public EuclidianViewM(MobileController ec)
	{
		super(ec, new Settings().getEuclidian(1));

		this.setAllowShowMouseCoords(false);

		logger.addHandler(new HasWidgetsLogHandler(new LoggingPopup()));
	}

	/**
	 * This method has to be called before using g2p.
	 * 
	 * @param c
	 *          : a new Canvas
	 * 
	 */
	public void initCanvas(Canvas c)
	{
		this.canvas = c;
		this.g2p = new GGraphics2DW(this.canvas);

		TouchDelegate touchDelegate = new TouchDelegate(this.canvas);
		this.canvas.addTouchStartHandler(new com.google.gwt.event.dom.client.TouchStartHandler(){

			@Override
			public void onTouchStart(
					com.google.gwt.event.dom.client.TouchStartEvent event) {
				event.preventDefault();
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchStart(event.getTouches().get(0).getPageX(), event.getTouches()
				    .get(0).getPageY());
				
			}
			
		});
		
		touchDelegate.addTouchStartHandler(new TouchStartHandler()
		{

			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				event.preventDefault();
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchStart(event.getTouches().get(0).getPageX(), event.getTouches()
				    .get(0).getPageY());

				// EuclidianViewM.logger.log(Level.INFO, event.toDebugString() + " (" +
				// event.getTouches().get(0).getPageX() + "/"
				// + event.getTouches().get(0).getPageY() + ")");
				// EuclidianViewM.logger.log(Level.INFO, event.toDebugString() + " " +
				// event.getTouches().length());

			}
		});

		this.canvas.addTouchMoveHandler(new com.google.gwt.event.dom.client.TouchMoveHandler()
		{

			@Override
			public void onTouchMove(
					com.google.gwt.event.dom.client.TouchMoveEvent event) {
				event.preventDefault();
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchMove(event.getTouches().get(0).getPageX(), event.getTouches().get(0)
				    .getPageY());				
			}
			
		});
		
		touchDelegate.addTouchMoveHandler(new TouchMoveHandler()
		{

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
				event.preventDefault();
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchMove(event.getTouches().get(0).getPageX(), event.getTouches().get(0)
				    .getPageY());
				// EuclidianViewM.logger.log(Level.INFO, event.toDebugString() + " " +
				// event.getTouches().length());
			}
		});

		this.canvas.addTouchEndHandler(new com.google.gwt.event.dom.client.TouchEndHandler()
		{

			@Override
			public void onTouchEnd(
					com.google.gwt.event.dom.client.TouchEndEvent event) {
				event.preventDefault();
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchEnd(event.getChangedTouches().get(0).getPageX(), event
				    .getChangedTouches().get(0).getPageY());
				
			}
			
		});
		touchDelegate.addTouchEndHandler(new TouchEndHandler()
		{
			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
				event.preventDefault();
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchEnd(event.getChangedTouches().get(0).getPageX(), event
				    .getChangedTouches().get(0).getPageY());

				// EuclidianViewM.logger.log(Level.INFO, event.toDebugString() + " (" +
				// event.getChangedTouches().get(0).getPageX() + "/"
				// + event.getChangedTouches().get(0).getPageY() + ")");
				// EuclidianViewM.logger.log(Level.INFO, event.toDebugString() + " " +
				// event.getTouches().length());
			}
		});
		
		this.canvas.addGestureChangeHandler(new GestureChangeHandler() {
			
			@Override
			public void onGestureChange(GestureChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
		});

		touchDelegate.addPinchHandler(new PinchHandler()
		{

			@Override
			public void onPinch(PinchEvent event)
			{
				 ((MobileController)EuclidianViewM.this.getEuclidianController()).onPinch(event.getX(),
				 event.getY(), event.getScaleFactor());
				 
//				 EuclidianViewM.logger.log(Level.INFO, event.toDebugString() + " (" +
//				 event.getX() + "/" + event.getY() + ")" + "; " +
//				 event.getScaleFactor());
			}
		});

		this.canvas.addMouseWheelHandler(new MouseWheelHandler()
		{

			@Override
			public void onMouseWheel(MouseWheelEvent event)
			{
				int scale = event.getDeltaY();
				
			 ((MobileController)
				 EuclidianViewM.this.getEuclidianController()).onPinch(event.getClientX(),
				 event.getClientY(), scale);

				EuclidianViewM.logger.log(Level.INFO, event.toDebugString() + " (" + event.getClientX() + "/" + event.getClientY() + ")" + "; " + scale);
			}
		});

		updateFonts();
		initView(true);
		attachView();

	}

	@Override
	public void repaint()
	{
		if (getAxesColor() == null)
		{
			setAxesColor(geogebra.common.awt.GColor.black);
		}

		updateSize();
		paint(this.g2p);
	}

	@Override
	public GColor getBackgroundCommon()
	{
		// TODO
		return this.backgroundColor;
	}

	@Override
	public boolean hitAnimationButton(AbstractEvent event)
	{
		return false;
	}

	@Override
	public void setDefaultCursor()
	{
	}

	@Override
	public void setHitCursor()
	{
	}

	@Override
	public EuclidianStyleBar getStyleBar()
	{
		return null;
	}

	@Override
	public void setDragCursor()
	{
	}

	@Override
	public void setToolTipText(String plainTooltip)
	{
	}

	@Override
	public void setResizeXAxisCursor()
	{
	}

	@Override
	public void setResizeYAxisCursor()
	{
	}

	@Override
	public void setMoveCursor()
	{
	}

	@Override
	public boolean hasFocus()
	{
		return false;
	}

	@Override
	public void requestFocus()
	{
	}

	@Override
	public int getWidth()
	{
		// TODO
		return this.g2p.getCoordinateSpaceWidth();
	}

	@Override
	public int getHeight()
	{
		// TODO
		return this.g2p.getCoordinateSpaceHeight();
	}

	@Override
	public EuclidianController getEuclidianController()
	{
		// TODO
		return this.euclidianController;
	}

	@Override
	public void clearView()
	{
		resetLists();
		updateBackgroundImage();
	}

	@Override
	public GGraphics2D getTempGraphics2D(GFont fontForGraphics)
	{
		// TODO
		if (this.g2dtemp == null)
			this.g2dtemp = new geogebra.web.awt.GGraphics2DW(Canvas.createIfSupported());
		this.g2dtemp.setFont(fontForGraphics);
		return this.g2dtemp;
	}

	@Override
	public GFont getFont()
	{
		return null;
	}

	@Override
	protected void setHeight(int h)
	{
	}

	@Override
	protected void setWidth(int h)
	{
	}

	@Override
	protected void initCursor()
	{
	}

	@Override
	protected void setStyleBarMode(int mode)
	{
	}

	@Override
	public void updateSize()
	{
		Window.enableScrolling(false);

		int width = Window.getClientWidth();
		int height = Window.getClientHeight();

		this.g2p.setCoordinateSpaceWidth(width);
		this.g2p.setCoordinateSpaceHeight(height);

		this.canvas.setSize(width + "px", height + "px");
	}

	@Override
	public boolean requestFocusInWindow()
	{
		return false;
	}

	@Override
	public void paintBackground(GGraphics2D g2)
	{
		// TODO
		((GGraphics2DW) g2).drawGraphics((GGraphics2DW) this.bgGraphics, 0, 0, null);
	}

	@Override
	protected void drawActionObjects(GGraphics2D g)
	{
	}

	@Override
	protected void setAntialiasing(GGraphics2D g2)
	{
		// TODO
	}

	@Override
	public void setBackground(GColor bgColor)
	{
		if (bgColor != null)
		{
			this.backgroundColor = AwtFactory.prototype.newColor(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), bgColor.getAlpha());
		}
	}

	@Override
	public void setPreferredSize(GDimension preferredSize)
	{
	}

	@Override
	protected void doDrawPoints(GeoImage gi, List<GPoint> penPoints2, GColor penColor, int penLineStyle, int penSize)
	{
	}

	@Override
	protected MyZoomer newZoomer()
	{
		return new MyZoomerW(this);
	}

	@Override
	public void add(GBox box)
	{
	}

	@Override
	public void remove(GBox box)
	{
	}

	@Override
	public void setTransparentCursor()
	{
	}

	@Override
	public void setEraserCursor()
	{
	}

	@Override
	public GGraphics2D getGraphicsForPen()
	{
		return null;
	}

	@Override
	public boolean isShowing()
	{
		return false;
	}

}
