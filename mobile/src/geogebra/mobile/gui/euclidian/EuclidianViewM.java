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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.Window;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class EuclidianViewM extends EuclidianView
{
	int oldDistance;

	// set in setCanvas
	GGraphics2DW g2p = null;
	Canvas canvas;

	private GColor backgroundColor = GColor.white;
	private GGraphics2D g2dtemp;

	public EuclidianViewM(MobileController ec)
	{
		super(ec, new Settings().getEuclidian(1));

		this.setAllowShowMouseCoords(false);
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

		this.canvas.addTouchStartHandler(new com.google.gwt.event.dom.client.TouchStartHandler()
		{
			@Override
			public void onTouchStart(com.google.gwt.event.dom.client.TouchStartEvent event)
			{
				if (event.getTouches().length() == 1)
				{
					event.preventDefault();
					((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchStart(event.getTouches().get(0).getPageX(), event.getTouches()
					    .get(0).getPageY());
				}
				else if (event.getTouches().length() == 2)
				{
					EuclidianViewM.this.oldDistance = (int) (Math.pow((event.getTouches().get(0).getPageX() - event.getTouches().get(1).getPageX()), 2) + Math
					    .pow((event.getTouches().get(0).getPageY() - event.getTouches().get(1).getPageY()), 2));
				}
			}

		});

		this.canvas.addTouchMoveHandler(new com.google.gwt.event.dom.client.TouchMoveHandler()
		{

			@Override
			public void onTouchMove(com.google.gwt.event.dom.client.TouchMoveEvent event)
			{
				event.preventDefault();

				if (event.getTouches().length() == 1)
				{
					// proceed normally
					((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchMove(event.getTouches().get(0).getPageX(),
					    event.getTouches().get(0).getPageY());
				}
				else if (event.getTouches().length() == 2)
				{
					Touch first, second;
					int centerX, centerY, newDistance;

					first = event.getTouches().get(0);
					second = event.getTouches().get(1);

					centerX = (first.getPageX() + second.getPageX()) / 2;
					centerY = (first.getPageY() + second.getPageY()) / 2;

					if (EuclidianViewM.this.oldDistance > 0)
					{
						newDistance = (int) (Math.pow((first.getPageX() - second.getPageX()), 2) + Math.pow((first.getPageY() - second.getPageY()), 2));

						if (newDistance / EuclidianViewM.this.oldDistance > 1.1 || newDistance / EuclidianViewM.this.oldDistance < 0.9)
						{
							((MobileController) EuclidianViewM.this.getEuclidianController()).onPinch(centerX, centerY, newDistance
							    / EuclidianViewM.this.oldDistance);
							EuclidianViewM.this.oldDistance = newDistance;
						}
					}
				}
			}
		});

		this.canvas.addTouchEndHandler(new com.google.gwt.event.dom.client.TouchEndHandler()
		{

			@Override
			public void onTouchEnd(com.google.gwt.event.dom.client.TouchEndEvent event)
			{
				event.preventDefault();
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchEnd(event.getChangedTouches().get(0).getPageX(), event
				    .getChangedTouches().get(0).getPageY());

			}

		});

		// Listeners for Desktop
		this.canvas.addMouseDownHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				event.preventDefault();
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchStart(event.getClientX(), event.getClientY());
			}
		});

		this.canvas.addMouseMoveHandler(new MouseMoveHandler()
		{

			@Override
			public void onMouseMove(MouseMoveEvent event)
			{
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchMove(event.getClientX(), event.getClientY());
			}
		});

		this.canvas.addMouseUpHandler(new MouseUpHandler()
		{

			@Override
			public void onMouseUp(MouseUpEvent event)
			{
				event.preventDefault();
				((MobileController) EuclidianViewM.this.getEuclidianController()).onTouchEnd(event.getClientX(), event.getClientY());
			}
		});

		this.canvas.addMouseWheelHandler(new MouseWheelHandler()
		{
			@Override
			public void onMouseWheel(MouseWheelEvent event)
			{
				int scale = event.getDeltaY();

				((MobileController) EuclidianViewM.this.getEuclidianController()).onPinch(event.getClientX(), event.getClientY(), scale);
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
