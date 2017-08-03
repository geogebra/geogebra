package org.geogebra.web.html5.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ZoomPanelResources extends ClientBundle {
	static ZoomPanelResources INSTANCE = GWT.create(ZoomPanelResources.class);
	@Source("org/geogebra/common/icons/png/matDesignIcons/ev/fullscreen_black18.png")
	ImageResource fullscreen_black18();

	@Source("org/geogebra/common/icons/png/matDesignIcons/ev/fullscreen_exit_black18.png")
	ImageResource fullscreen_exit_black18();

	@Source("org/geogebra/common/icons/png/matDesignIcons/ev/home_zoom_black18.png")
	ImageResource home_zoom_black18();

	@Source("org/geogebra/common/icons/png/matDesignIcons/ev/remove_black18.png")
	ImageResource remove_black18();

	@Source("org/geogebra/common/icons/png/matDesignIcons/ev/add_black18.png")
	ImageResource add_black18();

}
