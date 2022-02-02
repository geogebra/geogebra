package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.web.html5.gui.util.HasResource;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.gwtproject.resources.client.ResourcePrototype;

public class GImageIconW extends GImageIcon implements HasResource {

	private String impl;

	public GImageIconW(String imageHtml) {
		impl = imageHtml;
	}

	public String getImpl() {
		return impl;
	}

	@Override
	public void setResource(ResourcePrototype res) {
		impl = NoDragImage.safeURI(res);
	}
}
