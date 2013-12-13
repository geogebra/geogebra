package geogebra.web.gui.view.data;

import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.gui.view.data.PlotPanelEuclidianViewCommon;
import geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.kernel.Kernel;
import geogebra.html5.Browser;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.euclidian.IsEuclidianController;
import geogebra.html5.euclidian.MsZoomer;
import geogebra.html5.event.HasOffsets;
import geogebra.html5.event.PointerEvent;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.GuiManagerW;

import java.util.LinkedList;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 *
 *Plot panel for ProbabilityCalculator
 */
public class PlotPanelEuclidianViewW extends EuclidianViewW implements PlotPanelEuclidianViewInterface {
	
	private EuclidianControllerW ec;
	public PlotPanelEuclidianViewCommon commonFields;
	
	private MyPointerHandler myPointerHandler;

	/*************************************************
	 * Construct the panel
	 */
	public PlotPanelEuclidianViewW(Kernel kernel, ScheduledCommand exportAction) {
		super(new PlotPanelEuclidianControllerW(kernel), PlotPanelEuclidianViewCommon.showAxes, PlotPanelEuclidianViewCommon.showGrid,
				1, null);

		
		
		if (commonFields == null) {
			setCommonFields();
		}
		/*Mouse handling needed? We will see...
		 * 
		 * 
		 * // enable/disable mouseListeners
		setMouseEnabled(false, true);
		setMouseMotionEnabled(false);
		setMouseWheelEnabled(false);
		this.addMouseMotionListener(new MyMouseMotionListener());

		 * 
		 * 
		 * 
		 * 
		 */
		
		// set preferred size so that updateSize will work and this EV can be
		// properly initialized
		setPreferredSize(new GDimensionW(300, 200));
		updateSize();

		
		
	}
	
	private void setCommonFields() {
		// set fields
				commonFields = new PlotPanelEuclidianViewCommon(
						false);	
				commonFields.setPlotSettings(new PlotSettings());

				setViewId(kernel);
				
				this.ec = this.getEuclidianController();
	}
	
	/*********** End Constructor **********************/

	/**
	 * Overrides EuclidianView setMode method so that no action is taken on a
	 * mode change.
	 */
	@Override
	public void setMode(int mode) {
		// .... do nothing
	}
	
	/** Returns viewID */
	@Override
	public int getViewID() {
		if (commonFields == null) {
			setCommonFields();
		}
		return commonFields.getViewID();
	}


	public void setViewId(Kernel kernel) {
	    // get viewID from GuiManager
		commonFields.setViewID(((GuiManagerW) kernel.getApplication().getGuiManager())
				.assignPlotPanelID(this));
    }

	public void setEVParams() {
	    commonFields.setEVParams(this);
    }

	public double getPixelOffset() {
		return (30 * getApplication().getFontSize()
				) / 12.0;
    }
	
	@Override
    public void updateSizeKeepDrawables() {
		super.updateSizeKeepDrawables();
	}
	
	/**
	 * Mouse listener class to handle right click trigger for the context menu.
	 * Right click events are consumed to prevent the EuclidianController from
	 * handling right-clicks as well.
	 */
	
	private class MyPointerHandler implements MouseDownHandler, MouseUpHandler, 
	MouseMoveHandler, MouseOutHandler, MouseOverHandler, MouseWheelHandler, TouchStartHandler, TouchEndHandler, 
	TouchMoveHandler, TouchCancelHandler, GestureStartHandler, GestureEndHandler, GestureChangeHandler, HasOffsets, IsEuclidianController {
		

		private MsZoomer msZoomer;
		private HandlerRegistration mw;
		private HandlerRegistration me;
		private HandlerRegistration mo;
		private HandlerRegistration mout;
		private HandlerRegistration md;
		private HandlerRegistration mu;
		private HandlerRegistration ts;
		private HandlerRegistration te;
		private HandlerRegistration tm;
		private HandlerRegistration tc;
		private HandlerRegistration gs;
		private HandlerRegistration gc;
		private HandlerRegistration ge;

		public MyPointerHandler(EuclidianPanelWAbstract evp) {
			Widget evPanel = evp.getAbsolutePanel();
			mw = evPanel.addDomHandler(this, MouseWheelEvent.getType());
			
			me = evPanel.addDomHandler(this, MouseMoveEvent.getType());
			mo = evPanel.addDomHandler(this, MouseOverEvent.getType());
			mout = evPanel.addDomHandler(this, MouseOutEvent.getType());
			md = evPanel.addDomHandler(this, MouseDownEvent.getType());
			mu = evPanel.addDomHandler(this, MouseUpEvent.getType());
			
			if(Browser.supportsPointerEvents()){
				msZoomer = new MsZoomer(this);
				MsZoomer.attachTo(evPanel.getElement(),msZoomer);
				return;
			}
			
			ts = evPanel.addDomHandler(this, TouchStartEvent.getType());
			te = evPanel.addDomHandler(this, TouchEndEvent.getType());
			tm = evPanel.addDomHandler(this, TouchMoveEvent.getType());
			tc = evPanel.addDomHandler(this, TouchCancelEvent.getType());
			gs = evPanel.addDomHandler(this, GestureStartEvent.getType());
			gc = evPanel.addDomHandler(this, GestureChangeEvent.getType());
			ge = evPanel.addDomHandler(this, GestureEndEvent.getType());
		}

		public void removeHandlers() {
	        mw.removeHandler();
	        me.removeHandler();
	        mo.removeHandler();
	        mout.removeHandler();
	        md.removeHandler();
	        mu.removeHandler();
	        ts.removeHandler();
	        te.removeHandler();
	        tm.removeHandler();
	        tc.removeHandler();
	        gs.removeHandler();
	        gc.removeHandler();
	        ge.removeHandler();
        }

		public void onGestureChange(GestureChangeEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onGestureEnd(GestureEndEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onGestureStart(GestureStartEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onTouchCancel(TouchCancelEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onTouchMove(TouchMoveEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onTouchEnd(TouchEndEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onTouchStart(TouchStartEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onMouseWheel(MouseWheelEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onMouseOver(MouseOverEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onMouseOut(MouseOutEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onMouseMove(MouseMoveEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onMouseUp(MouseUpEvent event) {
	
        }

		public void onMouseDown(MouseDownEvent event) {
	       
	        
        }

		public void setExternalHandling(boolean b) {
	        // TODO Auto-generated method stub
	        
        }

		public void twoTouchStart(double x1, double y1, double x2, double y2) {
	        // TODO Auto-generated method stub
	        
        }

		public void twoTouchMove(double x1, double y1, double x2, double y2) {
	        // TODO Auto-generated method stub
	        
        }

		public void setDefaultEventType(PointerEventType pointerEventType) {
	        // TODO Auto-generated method stub
	        
        }

		public LinkedList<PointerEvent> getMouseEventPool() {
	        // TODO Auto-generated method stub
	        return null;
        }

		public LinkedList<PointerEvent> getTouchEventPool() {
	        // TODO Auto-generated method stub
	        return null;
        }

		public int mouseEventX(int clientX) {
	        // TODO Auto-generated method stub
	        return 0;
        }

		public int mouseEventY(int clientY) {
	        // TODO Auto-generated method stub
	        return 0;
        }

		public int touchEventX(int clientX) {
	        // TODO Auto-generated method stub
	        return 0;
        }

		public int touchEventY(int clientY) {
	        // TODO Auto-generated method stub
	        return 0;
        }

		public int getEvID() {
	        // TODO Auto-generated method stub
	        return 0;
        }

		public PointerEventType getDefaultEventType() {
	        // TODO Auto-generated method stub
	        return null;
        }
		
	}

	public void setMouseMotionEnabled(boolean enable) {
	
    }

	private void removeMyPointerHandler(MyPointerHandler handler) {
		handler.removeHandlers();
    }

	public void setMouseEnabled(boolean enableECMouseListener, boolean enableMyMouseListener) {
		if (myPointerHandler == null) {
			myPointerHandler = new MyPointerHandler(EVPanel);
		}
		removeMyPointerHandler(myPointerHandler);	    
    }

	public Widget getComponent() {
	    return EVPanel.getAbsolutePanel();
    }
	
	


}
