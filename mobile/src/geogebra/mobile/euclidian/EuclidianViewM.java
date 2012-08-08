package geogebra.mobile.euclidian;

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
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.main.settings.Settings;
import geogebra.web.awt.GGraphics2DW;
import geogebra.web.gui.app.GeoGebraAppFrame;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;

public class EuclidianViewM extends EuclidianView
{

	// set in setCanvas
	private geogebra.web.awt.GGraphics2DW g2p = null;
	private Canvas canvas;

	private GColor backgroundColor = GColor.white;
	
	public EuclidianViewM(EuclidianController ec)
	{
		this(ec, new boolean[] { true, true }, true, new Settings().getEuclidian(1));
	}

	public EuclidianViewM(EuclidianController euclidiancontroller, boolean[] showAxes, boolean showGrid, EuclidianSettings settings)
	{
		// super(euclidiancontroller, settings);

		super(euclidiancontroller, settings);
	}

	/**
	 * This method has to be called before using g2p
	 * 
	 * @param c
	 *          : a new Canvas
	 * 
	 */
	public void initCanvas(Canvas c)
	{
		this.canvas = c;
		this.g2p = new GGraphics2DW(this.canvas);

		updateFonts();
		initView(true);
		attachView();

	}

	@Override
	public void repaint()
	{
		// TODO
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
		return null;
	}

	@Override
	public void clearView()
	{
	}

	@Override
	public GGraphics2D getTempGraphics2D(GFont fontForGraphics)
	{
		return null;
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
		// TODO
//		 this.canvas.setSize("100%", "100%");
		
//		this.g2p.getCanvas().getElement().getParentElement().getStyle().setWidth(100, Style.Unit.PCT); 
//		System.out.println(this.g2p.getCanvas().getElement().getParentElement().getStyle().getWidth()); 
		
		int width = 1000;
		int height = 500;
		
		this.g2p.setCoordinateSpaceWidth(width);
		this.g2p.setCoordinateSpaceHeight(height);
		try
		{
			this.g2p.getCanvas().getElement().getParentElement().getStyle().setWidth(width, Style.Unit.PX);
			this.g2p.getCanvas().getElement().getParentElement().getStyle().setHeight(height, Style.Unit.PX);
		}
		catch (Exception e)
		{
			App.debug("Problem with the parent element of the canvas");
		}

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
		return null;
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

}
