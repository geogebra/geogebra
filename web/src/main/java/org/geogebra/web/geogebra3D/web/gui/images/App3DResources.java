package org.geogebra.web.geogebra3D.web.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;

public interface App3DResources extends ClientBundleWithLookup {

	App3DResources INSTANCE = GWT.create(App3DResources.class);

	// public static String iconString = "empty.gif";
	//
	// @Source("org/geogebra/web/web/gui/images/" + iconString)
	// ImageResource icon();
	//

	@Source("menu_view_graphics3D.png")
	ImageResource view_graphics3D();

}
