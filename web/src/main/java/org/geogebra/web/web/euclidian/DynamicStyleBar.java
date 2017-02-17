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
		Log.debug("newpos: " + newPos[0] + " " + newPos[1]);
		this.getElement().getStyle().setLeft(newPos[0], Unit.PX);
		this.getElement().getStyle().setTop(newPos[1], Unit.PX);
	}
}
