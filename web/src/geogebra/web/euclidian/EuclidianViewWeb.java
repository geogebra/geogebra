package geogebra.web.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;
import geogebra.common.factories.AwtFactory;
import geogebra.common.io.MyXMLio;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.web.awt.GFontW;
import geogebra.web.awt.GGraphics2DW;
import geogebra.web.main.AppWeb;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;

public abstract class EuclidianViewWeb extends EuclidianView {
	public geogebra.web.awt.GGraphics2DW g2p = null;
	private GGraphics2D g2dtemp;
	public geogebra.web.awt.GGraphics2DW g4copy = null;
	private geogebra.common.awt.GColor backgroundColor = GColor.white;
	
	private AnimationScheduler.AnimationCallback repaintCallback = new AnimationScheduler.AnimationCallback() {
		public void execute(double ts) {
			doRepaint2();
		}
	};

	private AnimationScheduler repaintScheduler = AnimationScheduler.get();
	
	public EuclidianViewWeb(EuclidianController ec, EuclidianSettings settings) {
	    super(ec, settings);
    }
	
	@Override
	protected final void drawActionObjects(GGraphics2D g)
	{
		//not part of canvas, not needed
	}

	@Override
	protected final void setAntialiasing(GGraphics2D g2)
	{
		//always on
	}
	
	@Override
    public final GFont getFont() {
		return new GFontW(g2p.getFont());
    }
	

    public final GColor getBackgroundCommon() {
	    return backgroundColor ;
    }
	@Override
    public final void setBackground(GColor bgColor) {
		if (bgColor != null)
			backgroundColor = AwtFactory.prototype.newColor(
			bgColor.getRed(),
			bgColor.getGreen(),
			bgColor.getBlue(),
			bgColor.getAlpha());
    }
	
	@Override
	public final GGraphics2D getTempGraphics2D(GFont fontForGraphics)
	{
		// TODO
		if (this.g2dtemp == null)
			this.g2dtemp = new geogebra.web.awt.GGraphics2DW(Canvas.createIfSupported());
		this.g2dtemp.setFont(fontForGraphics);
		return this.g2dtemp;
	}
	
	@Override
    protected final MyZoomer newZoomer() {
	    return new MyZoomerW(this);
    }
	

	@Override
    public final void paintBackground(geogebra.common.awt.GGraphics2D g2) {
		((geogebra.web.awt.GGraphics2DW)g2).drawGraphics(
				(geogebra.web.awt.GGraphics2DW)bgGraphics, 0, 0, null);
	}
	
	public void doRepaint() {
			repaintScheduler.requestAnimationFrame(repaintCallback);
	}
	
	/**
     * This doRepaint method should be used instead of repaintView in cases
     * when the repaint should be done immediately
     */
	public abstract void doRepaint2();
	
	/**
	 * Gets the coordinate space width of the &lt;canvas&gt;.
	 * 
	 * @return the logical width
	 */
	public int getWidth()
	{
		return this.g2p.getCoordinateSpaceWidth();
	}
	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	public int getHeight()
	{
		return this.g2p.getCoordinateSpaceHeight();
	}
	
	public void clearView() {
		resetLists();
		updateBackgroundImage(); // clear traces and images
		// resetMode();
    }
	
	@Override
	protected final void setHeight(int h)
	{
		//TODO: not clear what should we do
	}

	@Override
	protected final void setWidth(int h)
	{
		//TODO: not clear what should we do
	}
	
	@Override
    public final GGraphics2DW getGraphicsForPen() {
	    return g2p;
    }
	
	public final boolean isShowing() {
	  	return
	  			g2p != null &&
	  			g2p.getCanvas() != null &&
	  			g2p.getCanvas().isAttached() &&
	  			g2p.getCanvas().isVisible();
    }

	public String getExportImageDataUrl(double scale, boolean transparency) {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);

		Canvas c4 = Canvas.createIfSupported();
		c4.setCoordinateSpaceWidth(width);
		c4.setCoordinateSpaceHeight(height);
		c4.setWidth(width+"px");
		c4.setHeight(height+"px");
		g4copy = new geogebra.web.awt.GGraphics2DW(c4);
		this.app.exporting = true;
		exportPaintPre(g4copy, scale, transparency);
		drawObjects(g4copy);
		this.app.exporting = false;
		return g4copy.getCanvas().toDataUrl();
	}
	
	public void exportPaintPre(geogebra.common.awt.GGraphics2D g2d, double scale,
			boolean transparency) {
		g2d.scale(scale, scale);

		// clipping on selection rectangle
		if (getSelectionRectangle() != null) {
			GRectangle rect = getSelectionRectangle();
			g2d.setClip(0, 0, (int)rect.getWidth(), (int)rect.getHeight());
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
				paintBackground(g2d);
			}
		} else {
			// just clear the background if transparency is disabled (clear =
			// draw background color)
			drawBackground(g2d, !transparency);
		}

		setAntialiasing(g2d);
	}
	
	/**
	 * repaintView just calls this method
	 */
    public final void repaint() {

    	if (getEuclidianController().isCollectingRepaints()){
    		getEuclidianController().setCollectedRepaints(true);
    		return;
    	}

    	//TODO: enable this code if this view can be detached
    	//if (!isShowing())
    	//	return;

    	((AppWeb)app).getTimerSystem().viewRepaint(this);
    }
    
    public void setCoordinateSpaceSize(int width, int height) {
		g2p.setCoordinateSpaceWidth(width);
		g2p.setCoordinateSpaceHeight(height);
		try {
			((AppWeb)app).syncAppletPanelSize(width, height, evNo);

			// just resizing the AbsolutePanelSmart, not the whole of DockPanel
			g2p.getCanvas().getElement().getParentElement().getStyle().setWidth(width, Style.Unit.PX);
			g2p.getCanvas().getElement().getParentElement().getStyle().setHeight(height, Style.Unit.PX);
		} catch (Exception exc) {
			App.debug("Problem with the parent element of the canvas");
		}
	}

	public void synCanvasSize() {
		setCoordinateSpaceSize(g2p.getOffsetWidth(), g2p.getOffsetHeight());
	}
	
	public String getCanvasBase64WithTypeString() {

		// TODO: make this more perfect, like in Desktop

		double ratio = g2p.getCoordinateSpaceWidth();
		ratio /= g2p.getCoordinateSpaceHeight() * 1.0;
		double thx = MyXMLio.THUMBNAIL_PIXELS_X;
		double thy = MyXMLio.THUMBNAIL_PIXELS_Y;
		if (ratio < 1)
			thx *= ratio;
		else if (ratio > 1)
			thy /= ratio;

		Canvas canv = Canvas.createIfSupported();
		canv.setCoordinateSpaceHeight((int)thy);
		canv.setCoordinateSpaceWidth((int)thx);
		canv.setWidth((int)thx+"px");
		canv.setHeight((int)thy+"px");
		Context2d c2 = canv.getContext2d();

		//g2p.getCanvas().getContext2d().drawImage(((GGraphics2DW)bgGraphics).getCanvas().getCanvasElement(), 0, 0, (int)thx, (int)thy);
		c2.drawImage(((GGraphics2DW)bgGraphics).getCanvas().getCanvasElement(), 0, 0, (int)thx, (int)thy);
		c2.drawImage(g2p.getCanvas().getCanvasElement(), 0, 0, (int)thx, (int)thy);

		return canv.toDataUrl();
	}
	

}
