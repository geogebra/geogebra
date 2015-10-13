package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.main.App;

public class GImageIconW extends GImageIcon {

	private String impl;

	public GImageIconW(String imageHtml) {
		impl = imageHtml;
	}

	public String getImpl() {
		return impl;
	}
}
