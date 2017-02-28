package org.geogebra.web.web.euclidian;

import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

public class DynamicStyleBar extends EuclidianStyleBarW {

	public DynamicStyleBar(EuclidianView ev) {
		super(ev, -1);
		addStyleName("DynamicStyleBar");
	}

	/**
	 * Sets the position of dynamic style bar. newPos position of right top
	 * corner of bounding box of drawable
	 */
	@Override
	public void setPosition(GRectangle2D gRectangle2D, boolean hasBoundingBox) {
		boolean oldVisible = this.isVisible();
		this.setVisible(true);

		// make sure it reflects selected geos
		setOpen(true);
		setMode(EuclidianConstants.MODE_MOVE);
		updateStyleBar();

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
			left = gRectangle2D.getMaxX() - height / 2.0;  //TODO
			top = gRectangle2D.getMinY();
		}
			
		this.getElement().getStyle().setLeft(left, Unit.PX);
		this.getElement().getStyle().setTop(top, Unit.PX);
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
}
