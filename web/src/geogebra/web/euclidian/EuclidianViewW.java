package geogebra.web.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.javax.swing.GBox;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.debug.Log;
import geogebra.html5.Browser;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.euclidian.IsEuclidianController;
import geogebra.html5.euclidian.MsZoomer;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.javax.swing.GBoxW;
import geogebra.html5.util.ImageLoadCallback;
import geogebra.html5.util.ImageWrapper;
import geogebra.web.main.AppW;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianViewW extends EuclidianViewWeb {
	
	
	
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
	 * @param evNo
	 * @param settings
	 */
	public EuclidianViewW(EuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid, EuclidianSettings settings) {
		super(euclidiancontroller, settings);
		
		EVPanel = new MyEuclidianViewPanel(this);
		
		initBaseComponents(EVPanel, euclidiancontroller, -1);
		
		
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
		
		evPanel.addDomHandler(euclidiancontroller, TouchStartEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchEndEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchMoveEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, TouchCancelEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureStartEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureChangeEvent.getType());
		evPanel.addDomHandler(euclidiancontroller, GestureEndEvent.getType());
		
	}
	
	// STROKES
	protected static MyBasicStrokeW standardStroke = new MyBasicStrokeW(1.0f);

	protected static MyBasicStrokeW selStroke = new MyBasicStrokeW(
			1.0f + EuclidianStyleConstants.SELECTION_ADD);

	protected boolean unitAxesRatio;

	static public MyBasicStrokeW getDefaultStroke() {
		return standardStroke;
	}

	static public MyBasicStrokeW getDefaultSelectionStroke() {
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
	
	public geogebra.common.euclidian.EuclidianStyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = new EuclidianStyleBarW(this);
		}

		return styleBar;
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
			g2.drawImage(new geogebra.html5.awt.GBufferedImageW(img), null, x, y);
		} else {
			ImageWrapper.nativeon(img,
				"load",
				new ImageLoadCallback() {
					public void onLoad() {
						g2.drawImage(new geogebra.html5.awt.GBufferedImageW(img), null, x, y);
					}
				}
			);
		}
	}

	

	@Override
  public void setPreferredSize(GDimension preferredSize) {
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

	@Override
	public void updateVisualStyle(GeoElement geo) {
		super.updateVisualStyle(geo);

		if (styleBar!=null)
			styleBar.updateVisualStyle(geo);
	}

	public void resetMsZoomer() {
	    if(msZoomer!= null){
	    	msZoomer.reset();
	    }
    }
	
	private class MyEuclidianViewPanel extends AbsolutePanel implements
			EuclidianPanelWAbstract {

			private Canvas canvas;
			private EuclidianView ev;

			public MyEuclidianViewPanel(EuclidianView ev) {
				super();
				this.ev = ev;
				canvas = Canvas.createIfSupported();
				canvas.getElement().getStyle().setPosition(Style.Position.RELATIVE);
				canvas.getElement().getStyle().setZIndex(0);
				add(canvas);

}

			public AbsolutePanel getAbsolutePanel() {
				return this;
			}

			public Panel getEuclidianPanel() {
				return this;
			}

			public Canvas getCanvas() {
				return canvas;
			}

			public EuclidianView getEuclidianView() {

				return ev;
			}

			public void onResize() {
				//	ev.setCoordinateSpaceSizeDirectly(100, 100);
			}

			public void deferredOnResize() {
			}

			public void updateNavigationBar() {
				// TODO Auto-generated method stub

			}

	}
	
}
