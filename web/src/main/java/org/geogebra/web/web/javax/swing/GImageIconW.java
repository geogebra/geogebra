package org.geogebra.web.web.javax.swing;

import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.main.App;

public class GImageIconW extends GImageIcon {

	private String impl;

	public GImageIconW(String imageHtml) {
		// impl = new NoDragImage(imageHtml, 32);
		// impl = SafeHtmlUtils.fromString(imageHtml);
		// impl = UriUtils.fromString(imageHtml);
		impl = imageHtml;
	}

	public String getImpl() {
		App.debug("image impl: " + impl);
		return impl;
	}

	// private Image impl;
	//
	// public GImageIconW(String imageHtml) {
	// impl = new NoDragImage(imageHtml);
	// }
	//
	// public Image getImpl() {
	// return impl;
	// }

}
