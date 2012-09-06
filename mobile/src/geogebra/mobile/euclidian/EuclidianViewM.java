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
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.javax.swing.GBox;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.settings.Settings;
import geogebra.mobile.algebra.AlgebraViewM;
import geogebra.mobile.controller.MobileEuclidianController;
import geogebra.web.awt.GGraphics2DW;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.googlecode.mgwt.dom.client.event.mouse.TouchEndToMouseUpHandler;
import com.googlecode.mgwt.dom.client.event.mouse.TouchMoveToMouseMoveHandler;
import com.googlecode.mgwt.dom.client.event.mouse.TouchStartToMouseDownHandler;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class EuclidianViewM extends EuclidianView
{
	// set in setCanvas
	private GGraphics2DW g2p = null;
	private Canvas canvas;

	private GColor backgroundColor = GColor.white;
	private GGraphics2D g2dtemp;

	public EuclidianViewM(MobileEuclidianController ec)
	{
		super(ec, new Settings().getEuclidian(1));

		this.setAllowShowMouseCoords(false);
	}
	
	//for tests only
	private AlgebraViewM algebraView;
	public void setAlgebraViewM(AlgebraViewM algebraView)
	{
		this.algebraView = algebraView;
	}

	void addEventToTree(String event)
	{
		this.algebraView.addEventName(event);
	}
	//end
	
	/**
	 * This method has to be called before using g2p.
	 * 
	 * @param c
	 *            : a new Canvas
	 * 
	 */
	public void initCanvas(Canvas c)
	{

		this.canvas = c;
		this.g2p = new GGraphics2DW(this.canvas);

		// / try with addDomHandler

		// this.canvas.addDomHandler(new ClickHandler()
		// {
		// @Override
		// public void onClick(ClickEvent event)
		// {
		// event.preventDefault();
		// ((MobileEuclidianController) EuclidianViewM.this
		// .getEuclidianController()).onClick(event);
		//
		// }
		//
		// }, ClickEvent.getType());
		
	
		// with 'translated' handlers
		this.canvas.addMouseDownHandler(new TouchStartToMouseDownHandler(new TouchStartHandler()
		{
			@Override
      public void onTouchStart(TouchStartEvent event)
      {
				addEventToTree("onTouchStart - with translated handler - EuclidianViewM");
				//Window.alert("onTouchStart - with translated handler - EuclidianViewM");
	      GWT.log("onTouchStart - with translated handler - EuclidianViewM");
      }
		}));
		
		this.canvas.addMouseMoveHandler(new TouchMoveToMouseMoveHandler(new TouchMoveHandler()
		{

			@Override
      public void onTouchMove(TouchMoveEvent event)
      {
				event.preventDefault();
				addEventToTree("onTouchMove - with translated handler - EuclidianViewM");
				//Window.alert("onTouchMove - with translated handler - EuclidianViewM");
	      GWT.log("onTouchMove - with translated handler - EuclidianViewM");
      }
			
		}));
		
		this.canvas.addMouseUpHandler(new TouchEndToMouseUpHandler(new TouchEndHandler() 
		{

			@Override
      public void onTouchEnd(TouchEndEvent event)
      {
				addEventToTree("onTouchEnd - with translated handler - EuclidianViewM");
				GWT.log("onTouchEnd - with translated handler - EuclidianViewM");
				//Window.alert("onTouchEnd - with translated handler - EuclidianViewM");
      }
			
		}));
		
		//with addHandler
		this.canvas.addHandler(new TouchStartHandler()
		{

			@Override
      public void onTouchStart(TouchStartEvent event)
      {
				addEventToTree("onTouchStart - mit addHanlder - EuclidianViewM");
				GWT.log("onTouchStart - mit addHanlder - EuclidianViewM");
				//Window.alert("onTouchStart - mit addHanlder - EuclidianViewM");
      }
			
		}, TouchStartEvent.getType());
		
		this.canvas.addHandler(new TouchMoveHandler()
		{

			@Override
      public void onTouchMove(TouchMoveEvent event)
      {
				addEventToTree("onTouchMove - mit addHanlder - EuclidianViewM");
				event.preventDefault();
				GWT.log("onTouchMove - mit addHanlder - EuclidianViewM");
				//Window.alert("onTouchMove - mit addHanlder - EuclidianViewM");
      }
			
		}, TouchMoveEvent.getType());
		
		this.canvas.addHandler(new TouchEndHandler()
		{

			@Override
      public void onTouchEnd(TouchEndEvent event)
      {
				addEventToTree("onTouchEnd - mit addHanlder - EuclidianViewM");
				GWT.log("onTouchEnd - mit addHanlder - EuclidianViewM");
				//Window.alert("onTouchEnd - mit addHanlder - EuclidianViewM");
      }
			
		}, TouchEndEvent.getType());
		
		
		// with tapHandler
		this.canvas.addHandler(new TapHandler(){

			@Override
      public void onTap(TapEvent event)
      {
				addEventToTree("onTap - mit addHanlder - EuclidianViewM");
				GWT.log("onTap - mit addHanlder - EuclidianViewM");
				//Window.alert("onTap - mit addHanlder - EuclidianViewM");
      }
			
		}, TapEvent.getType());
		
		// with domHandler and touch
		this.canvas.addDomHandler(new TouchStartHandler()
		{

			@Override
      public void onTouchStart(TouchStartEvent event)
      {
				addEventToTree("onTouchStart - mit addDomHanlder - EuclidianViewM");
				GWT.log("onTouchStart - mit addDomHanlder - EuclidianViewM");
				//Window.alert("onTouchStart - mit addDomHanlder - EuclidianViewM");
      }
			
		}, TouchStartEvent.getType());
		
		this.canvas.addDomHandler(new TouchMoveHandler()
		{

			@Override
      public void onTouchMove(TouchMoveEvent event)
      {
				event.preventDefault();
				addEventToTree("onTouchMove - mit addDomHanlder - EuclidianViewM");
				GWT.log("onTouchMove - mit addDomHanlder - EuclidianViewM");
				//Window.alert("onTouchMove - mit addDomHanlder - EuclidianViewM");
      }
			
		}, TouchMoveEvent.getType());
		
		this.canvas.addDomHandler(new TouchEndHandler()
		{

			@Override
      public void onTouchEnd(TouchEndEvent event)
      {
				addEventToTree("onTouchEnd - mit addDomHanlder - EuclidianViewM");
				GWT.log("onTouchEnd - mit addDomHanlder - EuclidianViewM");
      }
			
		}, TouchEndEvent.getType());
		
		
		// with domHanlder
		this.canvas.addDomHandler(new MouseDownHandler()
		{

			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				addEventToTree("onMouseDown - mit addDomHanlder - EuclidianViewM");
				GWT.log("onMouseDown - mit addDomHanlder - EuclidianViewM");
				((MobileEuclidianController) EuclidianViewM.this
						.getEuclidianController()).onMouseDown(event);
			}

		}, MouseDownEvent.getType());

		this.canvas.addDomHandler(new MouseMoveHandler()
		{

			@Override
			public void onMouseMove(MouseMoveEvent event)
			{
				event.preventDefault();
				addEventToTree("onMouseMove - mit addDomHanlder - EuclidianViewM");
				GWT.log("onMouseMove - mit addDomHanlder - EuclidianViewM");
				((MobileEuclidianController) EuclidianViewM.this
						.getEuclidianController()).onMouseMove(event);
			}

		}, MouseMoveEvent.getType());

		this.canvas.addDomHandler(new MouseUpHandler()
		{
			@Override
			public void onMouseUp(MouseUpEvent event)
			{
				addEventToTree("onMouseUp - mit addDomHanlder - EuclidianViewM");
				GWT.log("onMouseUp - mit addDomHanlder - EuclidianViewM");
				((MobileEuclidianController) EuclidianViewM.this
						.getEuclidianController()).onMouseUp(event);
			}
		}, MouseUpEvent.getType());
		// // end try with addDomHandler

		
		// mouseHandler
		this.canvas.addMouseDownHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				addEventToTree("onMouseDown - direkt - EuclidianViewM");
				GWT.log("onMouseDown - direkt - EuclidianViewM");
				((MobileEuclidianController) EuclidianViewM.this.getEuclidianController()).onMouseDown(event);
			}
		});

		this.canvas.addMouseMoveHandler(new MouseMoveHandler()
		{
			@Override
			public void onMouseMove(MouseMoveEvent event)
			{
				event.preventDefault();
				addEventToTree("onMouseMove - direkt - EuclidianViewM");
				GWT.log("onMouseMove - direkt - EuclidianViewM");
				((MobileEuclidianController) EuclidianViewM.this.getEuclidianController()).onMouseMove(event);
			}
		});

		this.canvas.addMouseUpHandler(new MouseUpHandler()
		{
			@Override
			public void onMouseUp(MouseUpEvent event)
			{
				addEventToTree("onMouseUp - direkt - EuclidianViewM");
				GWT.log("onMouseUp - direkt - EuclidianViewM");
				((MobileEuclidianController) EuclidianViewM.this.getEuclidianController()).onMouseUp(event);
			}
		});

		
		//TouchHandler - direkt
		this.canvas.addTouchStartHandler(new com.google.gwt.event.dom.client.TouchStartHandler()
		{
			@Override
      public void onTouchStart(com.google.gwt.event.dom.client.TouchStartEvent event)
      {
				addEventToTree("onTouchStart - direkt - EuclidianViewM");	
				GWT.log("onTouchStart - direkt - EuclidianViewM");	      
      }			
		});
		
		this.canvas.addTouchEndHandler(new com.google.gwt.event.dom.client.TouchEndHandler()
		{
			@Override
      public void onTouchEnd(com.google.gwt.event.dom.client.TouchEndEvent event)
      {
				addEventToTree("onTouchEnd - direkt - EuclidianViewM");	 
				GWT.log("onTouchEnd - direkt - EuclidianViewM");	 
      }			
		});
		
		this.canvas.addTouchMoveHandler(new com.google.gwt.event.dom.client.TouchMoveHandler()
		{
			@Override
      public void onTouchMove(com.google.gwt.event.dom.client.TouchMoveEvent event)
      {
				event.preventDefault();
				addEventToTree("onTouchMove - direkt - EuclidianViewM");	   
				GWT.log("onTouchMove - direkt - EuclidianViewM");	      	      
      }			
		});
		
		// clickHandler
		this.canvas.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				addEventToTree("onClick - direkt - EuclidianViewM");
				GWT.log("onClick - direkt - EuclidianViewM");
				//((MobileEuclidianController) EuclidianViewM.this.getEuclidianController()).onClick(event);
			}
		});

		updateFonts();
		initView(true);
		attachView();
	}

	@Override
	public void repaint()
	{
		// TODO
		if (getAxesColor() == null)
		{
			setAxesColor(geogebra.common.awt.GColor.black);
		}
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
			this.g2dtemp = new geogebra.web.awt.GGraphics2DW(
					Canvas.createIfSupported());
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
		// TODO
		Window.enableScrolling(false);

		int width = Window.getClientWidth();
		int height = Window.getClientHeight();

		this.canvas.setSize(width + "px", height + "px");

		this.g2p.setCoordinateSpaceWidth(width);
		this.g2p.setCoordinateSpaceHeight(height);
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
		((GGraphics2DW) g2).drawGraphics((GGraphics2DW) this.bgGraphics, 0, 0,
				null);
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
			this.backgroundColor = AwtFactory.prototype.newColor(
					bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(),
					bgColor.getAlpha());
		}
	}

	@Override
	public void setPreferredSize(GDimension preferredSize)
	{
	}

	@Override
	protected void doDrawPoints(GeoImage gi, List<GPoint> penPoints2,
			GColor penColor, int penLineStyle, int penSize)
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

	@Override
	public boolean isShowing()
	{
		return false;
	}

}
