package org.geogebra.web.web.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;

public class DynamicStyleBar extends EuclidianStyleBarW {

	public DynamicStyleBar(EuclidianView ev) {
		super(ev, -1);
		((GeoGebraFrameW)((AppW) ev.getApplication()).getAppletFrame()).add(this);
	}
}
