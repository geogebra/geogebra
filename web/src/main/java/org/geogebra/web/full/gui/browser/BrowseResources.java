package org.geogebra.web.full.gui.browser;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.resources.client.Resource;

@Resource
public interface BrowseResources extends ClientBundle {

	BrowseResources INSTANCE = new BrowseResourcesImpl();

	@Source("org/geogebra/common/icons/png/web/arrow_go_previous_grey.png")
	ImageResource back();

	@Source("org/geogebra/common/icons/png/web/open-from-location_geogebratube.png")
	ImageResource location_tube();

	@Source("org/geogebra/common/icons/png/web/open-from-location_googledrive.png")
	ImageResource location_drive();

	@Source("org/geogebra/common/icons/png/web/open-from-location_local-storage.png")
	ImageResource location_local();

}
