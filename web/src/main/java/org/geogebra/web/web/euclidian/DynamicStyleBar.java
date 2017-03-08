package org.geogebra.web.web.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.web.gui.util.PopupMenuButtonW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Widget;

public class DynamicStyleBar extends EuclidianStyleBarW {

	public DynamicStyleBar(EuclidianView ev) {
		super(ev, -1);
		addStyleName("DynamicStyleBar");
		
//		app.getSelectionManager().addSelectionListener(new GeoElementSelectionListener() {
//			@Override
//			public void geoElementSelected(GeoElement geo,
//					boolean addToSelection) {
//				if(addToSelection){
//					return;
//				}
//				DynamicStyleBar.this.setVisible(addToSelection);
//			}
//		});
		stopPointer(getElement());
	}

	private native void stopPointer(Element element) /*-{
		if ($wnd.PointerEvent) {
			var evts = [ "PointerDown", "PointerUp" ];
			for ( var k in evts) {
				element.addEventListener(evts[k].toLowerCase(), function(e) {
					e.stopPropagation()
				});
			}
		}

	}-*/;

	/**
	 * Sets the position of dynamic style bar. newPos position of right top
	 * corner of bounding box of drawable
	 */
	@Override
	public void setPosition(GRectangle2D gRectangle2D, boolean hasBoundingBox) {
		
		//Log.printStacktrace("setPosition");
		Log.debug("rectangle:" + gRectangle2D.getMinX() + " " + gRectangle2D.getMaxX()
		+ " " + gRectangle2D.getMinY() + " " + gRectangle2D.getMaxY());
		Log.debug("hasboundinbox: " + hasBoundingBox);
		
		boolean oldVisible = this.isVisible();
		this.setVisible(true);

		// make sure it reflects selected geos
		setOpen(true);
		//updateButtons();
		setMode(EuclidianConstants.MODE_MOVE);
		super.updateStyleBar();

		// Calculates the x param. of distance between the start of dynamic
		// stylebar and the three dot button.
		this.getElement().getStyle().setTop(-10000, Unit.PX);
		
//		int move = this.getViewButton().getAbsoluteLeft()
//				- this.getAbsoluteLeft();
		int move = this.getOffsetWidth(); 
		int height = this.getOffsetHeight();
		this.setVisible(oldVisible);

				
		double left, top;
		if(hasBoundingBox){
			left = gRectangle2D.getMaxX() - move;
			top = gRectangle2D.getMinY() - height - 5;
			
			//if there is no enough place on the top of bounding box, dynamic stylebar will be visible at the bottom of bounding box.
			if (top < 0){
				top = gRectangle2D.getMaxY() + 5;
			}
			
		} else { //line has no bounding box
			left = gRectangle2D.getMaxX() - height / 2.0;
			top = gRectangle2D.getMinY();
		}
		
		if(left<0){
			left = 0;
		}
		
		if(left + this.getOffsetWidth() > app.getActiveEuclidianView().getWidth()){
			left = app.getActiveEuclidianView().getWidth() - this.getOffsetWidth();
		}
		
		
		Log.debug("left: " + left + "top: " + top);
		this.getElement().getStyle().setLeft(left, Unit.PX);
		this.getElement().getStyle().setTop(top, Unit.PX);
	}
	
	public void updateStyleBar(){
        //ArrayList<GeoElement> selGeos = app.getSelectionManager().getSelectedGeos();
		ArrayList<GeoElement> selGeos = app.getJustCreatedGeos();
        if (selGeos != null && selGeos.size() > 0 && !isDrawingPredecessor(selGeos.get(selGeos.size()-1))){
        	Log.debug("add dynamic stylebar");
            EuclidianView view = app.getGuiManager().getActiveEuclidianView();
            Drawable dr = ((Drawable) view.getDrawableFor(selGeos.get(0)));
            if (dr.getBounds() != null){
                setPosition(dr.getBounds(), true);
                setVisible(true);
            }
            else setVisible(false);
            
        } else {
            setVisible(false);
        }        
    }
	

	
	public boolean isDrawingPredecessor(GeoElement geo) {
		Log.debug("geo: " + geo.getAlgebraDescriptionDefault());
		if(app.getMode() == EuclidianConstants.MODE_MOVE){
			return false;
		}
		if (geo instanceof GeoPoint
				&& app.getMode() != EuclidianConstants.MODE_POINT
				&& app.getMode() != EuclidianConstants.MODE_SHAPE_LINE) {
			return true;
		}

		if (geo instanceof GeoSegment && app
				.getMode() != EuclidianConstants.MODE_SEGMENT) {
			return true;
		}

		// Log.debug("has children? - " + geo.getAlgebraDescriptionDefault());
		// if (!geo.hasChildren()) {
		// Log.debug("HAS NO"); 
		// return true;
		// }
		// Log.debug("HAS!");

		return false;
	}
	

	@Override
	public boolean isDynamicStylebarHit(int x, int y) {
		return isWidgetHit(this, x, y);
	}
	
	private static boolean isWidgetHit(Widget w, int x, int y) {
		if (w == null) {
			return false;
		}
		int left = w.getAbsoluteLeft();
		int top = w.getAbsoluteTop();
		int right = left + w.getOffsetWidth();
		int bottom = top + w.getOffsetHeight();

		return (x > left && x < right && y > top && y < bottom);
	}
	
	private void addHandlers(){

	addDomHandler(new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			event.stopPropagation();
		}
	}, ClickEvent.getType());
	
	addDomHandler(new MouseDownHandler() {

		@Override
		public void onMouseDown(MouseDownEvent event) {
			event.stopPropagation();
		}
	}, MouseDownEvent.getType());
	
	
	addDomHandler(new TouchStartHandler() {
		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.stopPropagation();
		}
	}, TouchStartEvent.getType());

	addDomHandler(new TouchEndHandler() {
		@Override
		public void onTouchEnd(TouchEndEvent event) {
			event.stopPropagation();
		}
	}, TouchEndEvent.getType());
	
	}
}




