package geogebra.mobile.gui.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.javax.swing.GBox;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.settings.Settings;
import geogebra.mobile.controller.MobileController;
import geogebra.web.awt.GGraphics2DW;
import geogebra.web.awt.GRectangleW;
import geogebra.web.euclidian.EuclidianViewWeb;
import geogebra.web.main.DrawEquationWeb;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.Window;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class EuclidianViewM extends EuclidianViewWeb
{
	// set in setCanvas
	private Canvas canvas;

	
	protected Hits hits; 

	private static int SELECTION_DIAMETER_MIN = 25; // taken from geogebra.common.euclidian.draw.DrawPoint
	
	// accepting range for hitting a point is multiplied with this factor 
	// (for anything other see App)
	private int selectionFactor = 3; 
	
	public EuclidianViewM(MobileController ec)
	{
		super(ec, new Settings().getEuclidian(1));

		this.setAllowShowMouseCoords(false);
		
		this.hits = new Hits(); 
	}

	/**
	 * This method has to be called before using g2p.
	 * 
	 * @param c
	 *          : a new Canvas
	 * 
	 */
	public void initCanvas(Canvas c,LayoutPanel p)
	{
		this.canvas = c;
		this.g2p = new GGraphics2DW(this.canvas);
		TouchEventController touchController = new TouchEventController((MobileController) EuclidianViewM.this.getEuclidianController());

		p.addDomHandler(touchController, TouchStartEvent.getType());
		p.addDomHandler(touchController, TouchEndEvent.getType());
		p.addDomHandler(touchController, TouchMoveEvent.getType());

		// Listeners for Desktop
		p.addDomHandler(touchController, MouseDownEvent.getType());
		p.addDomHandler(touchController, MouseMoveEvent.getType());
		p.addDomHandler(touchController, MouseUpEvent.getType());
		p.addDomHandler(touchController, MouseWheelEvent.getType());

		updateFonts();
		initView(true);
		attachView();

	}

	@Override
	public void repaint()
	{
		if (getEuclidianController().isCollectingRepaints()){
    		getEuclidianController().setCollectedRepaints(true);
    		return;
    	}
		System.out.println("repaint");

		if (getAxesColor() == null)
		{
			setAxesColor(geogebra.common.awt.GColor.black);
		}
		((DrawEquationWeb)this.app.getDrawEquation()).clearLaTeXes(this);    	
		updateSize();
		paint(this.g2p);
	}

	/**
	 * this version also adds points that are very close to the hit point
	 */
	@Override
	public void setHits(GPoint p){
		super.setHits(p); 
		this.hits = super.getHits(); 
		
		if(this.hits.size() == 0){			
			GRectangleW rect = new GRectangleW(); 
			int size = EuclidianViewM.SELECTION_DIAMETER_MIN * this.selectionFactor;  
			rect.setBounds(p.x - (size/2), p.y - (size/2), size, size); 
			this.setHits(rect); 
			this.hits = super.getHits(); 
		}
	}
	
	@Override
	public Hits getHits(){		
		return this.hits;		
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
	public void setPreferredSize(GDimension preferredSize)
	{
	}

	@Override
	protected void doDrawPoints(GeoImage gi, List<GPoint> penPoints2, GColor penColor, int penLineStyle, int penSize)
	{
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

	@Override
  protected void drawResetIcon(GGraphics2D g)
  {
	  // TODO Auto-generated method stub
	  
  }
	
	public Canvas getCanvas(){
		return this.canvas;
	}

}
