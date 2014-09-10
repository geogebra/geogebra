package geogebra.html5.euclidian;

import geogebra.common.awt.GBasicStroke;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;
import geogebra.common.euclidian.draw.DrawList;
import geogebra.common.factories.AwtFactory;
import geogebra.common.io.MyXMLio;
import geogebra.common.javax.swing.GBox;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.Log;
import geogebra.html5.Browser;
import geogebra.html5.awt.GFontW;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.gawt.BufferedImage;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.javax.swing.GBoxW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.html5.main.TimerSystemW;
import geogebra.html5.util.ImageLoadCallback;
import geogebra.html5.util.ImageWrapper;

import java.util.List;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianViewW extends EuclidianView implements EuclidianViewWInterface  {
	
	public static int DELAY_UNTIL_MOVE_FINISH = 150;

	public static final int DELAY_BETWEEN_MOVE_EVENTS = 30;
	
	public geogebra.html5.awt.GGraphics2DW g2p = null;
	private GGraphics2D g2dtemp;
	public geogebra.html5.awt.GGraphics2DW g4copy = null;
	private geogebra.common.awt.GColor backgroundColor = GColor.white;
	
	private AnimationScheduler.AnimationCallback repaintCallback = new AnimationScheduler.AnimationCallback() {
		public void execute(double ts) {
			doRepaint2();
		}
	};

	private AnimationScheduler repaintScheduler = AnimationScheduler.get();

	private long lastRepaint;
	
	public boolean isInFocus = false;

	private AppW app = (AppW) super.app;

	protected ImageElement resetImage, playImage, pauseImage, upArrowImage,
	downArrowImage;
	
	protected EuclidianPanelWAbstract EVPanel;
	private MsZoomer msZoomer;
	/**
	 * @param euclidianViewPanel
	 * @param euclidiancontroller
	 * @param showAxes
	 * @param showGrid
	 * @param evNo 
	 * @param settings
	 */
	public EuclidianViewW(EuclidianPanelWAbstract euclidianViewPanel,
            EuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid, int evNo, EuclidianSettings settings) {
		
		super(euclidiancontroller, settings);
		
		EVPanel = euclidianViewPanel;
		
		initBaseComponents(euclidianViewPanel, euclidiancontroller, evNo);
    }
	
	/**
	 * @param euclidiancontroller
	 * @param showAxes
	 * @param showGrid
	 * @param settings
	 */
	public EuclidianViewW(EuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid, EuclidianSettings settings) {
		
		this(euclidiancontroller, settings);
		
	}
	
	/**
	 * @param euclidiancontroller
	 * @param settings
	 */
	public EuclidianViewW(EuclidianController euclidiancontroller, EuclidianSettings settings) {
		super(euclidiancontroller, settings);
		
		EVPanel = newMyEuclidianViewPanel();
		
		initBaseComponents(EVPanel, euclidiancontroller, -1);
		
		
	}
	

	
	
	/**
	 * @param list
	 *            list
	 * @param b
	 *            whether the list should be drawn as combobox
	 */
	public void drawListAsComboBox(GeoList list, boolean b) {

		list.setDrawAsComboBox(b);

		DrawList d = (DrawList) getDrawable(list);
		d.resetDrawType();

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
			this.g2dtemp = new geogebra.html5.awt.GGraphics2DW(Canvas.createIfSupported());
		this.g2dtemp.setFont(fontForGraphics);
		return this.g2dtemp;
	}
	
	@Override
    protected final MyZoomer newZoomer() {
	    return new MyZoomerW(this);
    }
	

	@Override
    public final void paintBackground(geogebra.common.awt.GGraphics2D g2) {
		if(this.isGridOrAxesShown() || this.hasBackgroundImages() || this.tracing
				|| app.showResetIcon() || kernel.needToShowAnimationButton()){
			((geogebra.html5.awt.GGraphics2DW)g2).drawGraphics(
				(geogebra.html5.awt.GGraphics2DW)bgGraphics, 0, 0, null);
		}else{
			((geogebra.html5.awt.GGraphics2DW)g2).fillWith(this.getBackgroundCommon());
		}
		
	}
	
	
	/**
     * This doRepaint method should be used instead of repaintView in cases
     * when the repaint should be done immediately
     */
	public final void doRepaint2()
	{
		long time = System.currentTimeMillis();
		((DrawEquationWeb) this.app.getDrawEquation()).clearLaTeXes(this);
		this.updateBackgroundIfNecessary();
		paint(this.g2p);
		getEuclidianController().setCollectedRepaints(false);
		lastRepaint = System.currentTimeMillis() - time;
		GeoGebraProfiler.addRepaint(lastRepaint);
		
	}
	
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
		if(app.getGuiManager()!=null){
			((AppW)app).getGuiManager().clearAbsolutePanels();
		}
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
		g4copy = new geogebra.html5.awt.GGraphics2DW(c4);
		this.app.exporting = true;
		exportPaintPre(g4copy, scale, transparency);
		drawObjects(g4copy);
		this.app.exporting = false;
		return g4copy.getCanvas().toDataUrl();
	}
	
	public BufferedImage getExportImage(double scale) {
		return getExportImage(scale, false);
	}
	
	public BufferedImage getExportImage(double scale, boolean transparency) {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);
		BufferedImage img = new BufferedImage(width, height, 0, true);
		exportPaint(new GGraphics2DW(img.getCanvas()), scale, transparency);
		return img;
	}
	
	/**
	 * @param canvas
	 *            canvas
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 */
	public void exportPaint(Canvas canvas, double scale) {
		exportPaint(new GGraphics2DW(canvas), scale, false);
	}
	
	@Override
	protected void exportPaintPre(geogebra.common.awt.GGraphics2D g2d, double scale,
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
    public void repaint() {

    	// TODO: this is a temporary hack until the timer system can handle TextPreview view
    	// (or ignore timer system because text preview only draws one geo)
    	if(getViewID() == App.VIEW_TEXT_PREVIEW || getViewID() < 0){
    		doRepaint();
    		return;
    	}
    	if (getEuclidianController().isCollectingRepaints()){
    		getEuclidianController().setCollectedRepaints(true);
    		return;
    	}

    	if (waitForRepaint == TimerSystemW.SLEEPING_FLAG){
    		getApplication().ensureTimerRunning();
    		waitForRepaint = TimerSystemW.EUCLIDIAN_LOOPS;
    	}
    }

	private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;
    
    /**
	 * schedule a repaint
	 */
	public void doRepaint() {		
		repaintScheduler.requestAnimationFrame(repaintCallback);
	}
	
	/**
	 * timer system suggests a repaint
	 */
	public boolean suggestRepaint(){
				
				
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG){
			return false;
		}

		if (waitForRepaint == TimerSystemW.REPAINT_FLAG){
			if (isShowing()){
				doRepaint();
				waitForRepaint = TimerSystemW.SLEEPING_FLAG;
			}
			return true;
		}
		
		waitForRepaint--;
		return true;
	}
	


    
    public void setCoordinateSpaceSize(int width, int height) {
    	int oldWidth = g2p.getCoordinateSpaceWidth();
    	int oldHeight = g2p.getCoordinateSpaceHeight();
		g2p.setCoordinateSpaceSize(width, height);
		try {
			((AppW)app).syncAppletPanelSize(width - oldWidth, height - oldHeight, evNo);

			// just resizing the AbsolutePanelSmart, not the whole of DockPanel
			g2p.getCanvas().getElement().getParentElement().getStyle().setWidth(width, Style.Unit.PX);
			g2p.getCanvas().getElement().getParentElement().getStyle().setHeight(height, Style.Unit.PX);
			getEuclidianController().calculateEnvironment();
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
		if(bgGraphics!=null)
			c2.drawImage(((GGraphics2DW)bgGraphics).getCanvas().getCanvasElement(), 0, 0, (int)thx, (int)thy);
		c2.drawImage(g2p.getCanvas().getCanvasElement(), 0, 0, (int)thx, (int)thy);

		return canv.toDataUrl();
	}

	@Override
	protected void updateSizeKeepDrawables() {
		if ((getWidth() <= 0) || (getHeight() <= 0)) {
			return;
		}

		// real world values
		companion.setXYMinMaxForUpdateSize();
		setRealWorldBounds();

		try {
			createImage();
		} catch (Exception e) {
			bgImage = null;
			bgGraphics = null;
		}

		updateBackgroundImage();
	}
	
	public void createImage() {
		bgImage = new BufferedImage(getWidth(), getHeight(), 0, false);
		bgGraphics = bgImage.createGraphics();
	}

	
	public double getMinSamplePoints() {
		return 40;
	}

	/*public double getMaxBendOfScreen() {
		return MAX_BEND_OFF_SCREEN;
	}*

	public double getMaxBend() {
		return MAX_BEND;
	}

	public int getMaxDefinedBisections() {
		return MAX_DEFINED_BISECTIONS;
	}*/
	
	public double getMinPixelDistance() {
		return this.g2p == null || g2p.getScale() <= 1 ? 0.5 : 1 ;
	}

	/*public int getMaxZeroCount() {
		return MAX_ZERO_COUNT;
	}*/

	public double getMaxPixelDistance() {
		return this.g2p == null || g2p.getScale() <= 1 ? 15 : 30 ;
	}

	public static void resetDelay() {
		DELAY_UNTIL_MOVE_FINISH = 150;
    }

	/*public int getMaxProblemBisections() {
		return MAX_PROBLEM_BISECTIONS;
	}*/
	public long getLastRepaintTime() {
		return lastRepaint;
	}

	
	
	/**
	 * @return new panel
	 */
	protected MyEuclidianViewPanel newMyEuclidianViewPanel(){
		return new MyEuclidianViewPanel(this);
	}

	private void initBaseComponents(EuclidianPanelWAbstract euclidianViewPanel,
            EuclidianController euclidiancontroller, int evNo) {
		
	    Canvas canvas = euclidianViewPanel.getCanvas();
		setEvNo(evNo, canvas);
	 
		this.g2p = new geogebra.html5.awt.GGraphics2DW(canvas);	
		g2p.setView(this);

		updateFonts();
		initView(true);
		attachView();
	
		((EuclidianControllerW)euclidiancontroller).setView(this);
		

		if(this.getViewID() != App.VIEW_TEXT_PREVIEW){
			registerKeyHandlers(canvas);
			registerMouseTouchGestureHandlers(euclidianViewPanel, (EuclidianControllerW) euclidiancontroller);
		}
		
		canvas.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent be) {
				focusLost();
			}
		});
		
		canvas.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent fe) {
				focusGained();
			}
		});
		
		
		//euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, KeyPressEvent.getType());
//		euclidianViewPanel.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
//		euclidianViewPanel.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
//		euclidianViewPanel.addKeyPressHandler(this.app.getGlobalKeyDispatcher());
		
		if ((evNo == 1) || (evNo == 2)) {
			EuclidianSettings es = this.app.getSettings().getEuclidian(evNo);
			settingsChanged(es);
			es.addListener(this);
		}
    }

	private void setEvNo(int evNo, Canvas canvas) {
	    if (evNo == 2) {
			canvas.getElement().setId("View_"+ App.VIEW_EUCLIDIAN2);
		} else if(evNo == 1) {
			canvas.getElement().setId("View_"+ App.VIEW_EUCLIDIAN);
		} else {
			canvas.getElement().setId("View_"+ getViewID());
		}
		this.evNo = evNo;
    }
	
	
	
	private void registerKeyHandlers(Canvas canvas){
		
		canvas.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyPressHandler(this.app.getGlobalKeyDispatcher());
		
	}
	
	private void registerMouseTouchGestureHandlers(EuclidianPanelWAbstract euclidianViewPanel, EuclidianControllerW euclidiancontroller){
		Widget evPanel = euclidianViewPanel.getAbsolutePanel();
		evPanel.addDomHandler(euclidiancontroller, MouseWheelEvent.getType());
		
		evPanel.addDomHandler(euclidiancontroller, MouseMoveEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseOverEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseOutEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseDownEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, MouseUpEvent.getType());
		
		if(Browser.supportsPointerEvents()){
			msZoomer = new MsZoomer((IsEuclidianController) euclidianController);
			MsZoomer.attachTo(evPanel.getElement(),msZoomer);
			return;
		}
		
		if(((AppW)app).getLAF() != null){
			if(((AppW)app).getLAF().registerHandlers(evPanel, euclidiancontroller))
			
			return;
		}
		
		evPanel.addDomHandler(euclidiancontroller, TouchStartEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchEndEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchMoveEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchCancelEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureStartEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureChangeEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureEndEvent.getType());
		
	}
	
	// STROKES
	protected static geogebra.html5.awt.GBasicStrokeW standardStroke = new geogebra.html5.awt.GBasicStrokeW(1.0f, GBasicStroke.CAP_ROUND, GBasicStroke.JOIN_ROUND);

	protected static geogebra.html5.awt.GBasicStrokeW selStroke = new geogebra.html5.awt.GBasicStrokeW(
			1.0f + EuclidianStyleConstants.SELECTION_ADD, GBasicStroke.CAP_ROUND, GBasicStroke.JOIN_ROUND);

	protected boolean unitAxesRatio;

	private Object preferredSize;

	static public geogebra.html5.awt.GBasicStrokeW getDefaultStroke() {
		return standardStroke;
	}

	static public geogebra.html5.awt.GBasicStrokeW getDefaultSelectionStroke() {
		return selStroke;
	}
	
	
	/* Code for dashed lines removed in r23713*/

	
	
	/**
	 * Gets pixel width of the &lt;canvas&gt;.
	 * 
	 * @return the physical width in pixels
	 */
	public int getPhysicalWidth() {
		return g2p.getOffsetWidth();
	}
	
	/**
	 * Gets pixel height of the &lt;canvas&gt;.
	 * 
	 * @return the physical height in pixels
	 */
	public int getPhysicalHeight() {
		return g2p.getOffsetHeight();
	}
	
	public int getAbsoluteTop() {
		return g2p.getAbsoluteTop();
	}
	
	public int getAbsoluteLeft() {
		return g2p.getAbsoluteLeft();
	}

	public EuclidianControllerW getEuclidianController() {
		return (EuclidianControllerW)euclidianController;
	}

	@Override
    protected void initCursor() {
		setDefaultCursor();
    }

	@Override
    protected void setStyleBarMode(int mode) {
		if (hasStyleBar()) {
			getStyleBar().setMode(mode);
		}
    }


	private ImageElement getResetImage() {
		if (resetImage == null) {
			resetImage = this.app.getRefreshViewImage();
		}
		return resetImage;
	}

	private ImageElement getPlayImage() {
		if (playImage == null) {
			playImage = this.app.getPlayImage();
		}
		return playImage;
	}

	private ImageElement getPauseImage() {
		if (pauseImage == null) {
			pauseImage = this.app.getPauseImage();
		}
		return pauseImage;
	}

	public boolean hitAnimationButton(int x, int y) {
		// draw button in focused EV only
				if (!drawPlayButtonInThisView()) {
					return false;
				}

				return kernel.needToShowAnimationButton() && (x <= 20)
						&& (y >= (getHeight() - 20));
    }

	@Override
    public boolean requestFocusInWindow() {
		//without tabindex canvas cannot receive mouse events in IE
		if(Browser.isIE()){
			g2p.getCanvas().setTabIndex(10000);
		}
		g2p.getCanvas().getCanvasElement().focus();	
		focusGained();
		return true;
    }

	public void focusLost() {
		if (isInFocus) {
			this.isInFocus = false;
			this.app.focusLost();
		}
	}

	public void focusGained() {
		if (!isInFocus && !App.isFullAppGui()) {
			this.isInFocus = true;
			this.app.focusGained();
		}
	}

	public void setDefaultCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_default");
    }

	public void setHitCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_hit");
    }
	

	
	@Override
	protected EuclidianStyleBar newEuclidianStyleBar(){
		if(app.getGuiManager() == null){
			return null;
		}
		return app.getGuiManager().newEuclidianStylebar(this);
	}
	
	@Override
	final protected void drawAnimationButtons(final geogebra.common.awt.GGraphics2D g2) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return;
		}

		final int x = 6;
		final int y = getHeight() - 22;

		if (highlightAnimationButtons) {
			// draw filled circle to highlight button
			g2.setColor(geogebra.common.awt.GColor.darkGray);
		} else {
			g2.setColor(geogebra.common.awt.GColor.lightGray);
		}

		g2.setStroke(geogebra.common.euclidian.EuclidianStatic.getDefaultStroke());

		// draw pause or play button
		g2.drawRect(x - 2, y - 2, 18, 18);
		final ImageElement img = kernel.isAnimationRunning() ? getPauseImage()
				: getPlayImage();
		if (img.getPropertyBoolean("complete")) {
			g2.drawImage(new BufferedImage(img), null, x, y);
		} else {
			ImageWrapper.nativeon(img,
				"load",
				new ImageLoadCallback() {
					public void onLoad() {
						g2.drawImage(new BufferedImage(img), null, x, y);
					}
				}
			);
		}
	}

	

	@Override
  public void setPreferredSize(GDimension preferredSize) {
	if(this.preferredSize!= null && this.preferredSize.equals(preferredSize)){
		return;
	}
	this.preferredSize = preferredSize;
    g2p.setPreferredSize(preferredSize);
    updateSize();
    setReIniting(false);
  }

	/**
	 * Updates the size of the canvas and coordinate system
	 * @param width the new width (in pixel)
	 * @param height the new height (in pixel)
	 */
	public void setPreferredSize(int width, int height) {
		setPreferredSize(new geogebra.html5.awt.GDimensionW(width, height));
	}

	public void setDragCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		if (this.app.useTransparentCursorWhenDragging) {
			g2p.getCanvas().addStyleName("cursor_transparent");
		} else {
			g2p.getCanvas().addStyleName("cursor_drag");
		}
    }

	public void setToolTipText(String plainTooltip) {
	    ToolTipManagerW.sharedInstance().showToolTip(plainTooltip);    
    }

	public void setResizeXAxisCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_resizeXAxis");
    }

	public void setResizeYAxisCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_resizeYAxis");
    }
	

	public void setMoveCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_move");
    }

	@Override
    public void setTransparentCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_transparent");
    }

	@Override
    public void setEraserCursor() {
		Log.warn("setEraserCursor() unimplemented");	    
    }

	public boolean hasFocus() {
	    // changed to return true, otherwise Arrow keys don't work to pan the view, see GlobalKeyDispatcher
		//return isInFocus;
		return true;
    }

	@Override
    public void add(GBox box) {
		if (EVPanel != null)
			EVPanel.getAbsolutePanel().add(
	    		GBoxW.getImpl(box),
	    		(int)box.getBounds().getX(), (int)box.getBounds().getY());
    }

	@Override
    public void remove(GBox box) {
		if (EVPanel != null)
			EVPanel.getAbsolutePanel().remove(
	    		GBoxW.getImpl(box));
	}

	@Override
	protected void drawResetIcon(geogebra.common.awt.GGraphics2D g){
		int w = getWidth();
		((GGraphics2DW)g).getCanvas().getContext2d().drawImage(
			getResetImage(), w - 18, 2);
	}


	public void synCanvasSizeWithApp(int canvasWidth, int canvasHeight) {
		g2p.setWidth(canvasWidth);
		g2p.setHeight(canvasHeight);
		setCoordinateSpaceSize(g2p.getOffsetWidth(), g2p.getOffsetHeight());
    }

	@Override
    protected void doDrawPoints(GeoImage gi, List<GPoint> penPoints2,
            GColor penColor, int penLineStyle, int penSize) {
	    App.debug("doDrawPoints() unimplemented");
	    
    }
	
	/*needed because set the id of canvas*/
	@Override
    public void setEuclidianViewNo(int evNo) {
		if (evNo >= 2) {
			this.evNo = evNo;
			this.g2p.getCanvas().getElement().setId("View_"+App.VIEW_EUCLIDIAN2);
		}
	}


	public void requestFocus() {
	    App.debug("unimplemented");
    }


	public void resetMsZoomer() {
	    if(msZoomer!= null){
	    	msZoomer.reset();
	    }
    }
	
	public Canvas getCanvas() {
	    return g2p.getCanvas();
    }
	
}
