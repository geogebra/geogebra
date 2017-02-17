package org.geogebra.web.web.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style.Unit;

public class DynamicStyleBar extends EuclidianStyleBarW {

	public DynamicStyleBar(EuclidianView ev) {
		super(ev, -1);
		addStyleName("DynamicStyleBar");
		((GeoGebraFrameW)((AppW) ev.getApplication()).getAppletFrame()).add(this);
	}

	/**
	 * Sets the position of dynamic style bar. newPos position of right top
	 * corner of bounding box of drawable
	 */
	@Override
	public void setPosition(double[] newPos) {
		boolean oldVisible = this.isVisible();
		this.setVisible(true);

		// Calculates the x param. of distance between the start of dynamic
		// stylebar and the three dot button.
		this.getElement().getStyle().setTop(-10000, Unit.PX);
		int move = this.getViewButton().getAbsoluteLeft()
				- this.getAbsoluteLeft();

		int height = this.getOffsetHeight();
		this.setVisible(oldVisible);

		newPos[0] -= move;
		newPos[1] -= height + 5;

		Log.debug("newpos: " + newPos[0] + " " + newPos[1]);

		this.getElement().getStyle().setLeft(newPos[0], Unit.PX);
		this.getElement().getStyle().setTop(newPos[1], Unit.PX);
	}
}
