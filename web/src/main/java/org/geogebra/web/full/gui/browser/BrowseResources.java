package org.geogebra.web.full.gui.browser;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.resources.client.Resource;

@Resource
public interface BrowseResources extends ClientBundle {
	
	BrowseResources INSTANCE = new BrowseResourcesImpl();
	
	@Source("org/geogebra/common/icons/png/web/button_search.png")
	ImageResource search();

	@Source("org/geogebra/common/icons/png/web/arrow_go_previous_grey.png")
	ImageResource back();
	
	// @Source("org/geogebra/common/icons/png/web/document_view.png")
	@Source("org/geogebra/common/menu_icons/p20/menu-edit-view.png")
	ImageResource document_view();
	
	@Source("org/geogebra/common/icons/png/web/document_edit.png")
	ImageResource document_edit();
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-rename.png")
	ImageResource document_rename();
	
	@Source("org/geogebra/common/icons/png/web/document_delete.png")
	ImageResource document_delete();
	
	@Source("org/geogebra/common/icons/png/web/document_delete_active.png")
	ImageResource document_delete_active();
	
	@Source("org/geogebra/common/icons/png/web/open-from-location_geogebratube.png")
	ImageResource location_tube();
	
	@Source("org/geogebra/common/icons/png/web/open-from-location_googledrive.png")
	ImageResource location_drive();

	@Source("org/geogebra/common/icons/png/web/open-from-location_local-storage.png")
	ImageResource location_local();

	@Source("org/geogebra/common/icons/png/web/profile-options-arrow.png")
	ImageResource arrow_options();

	@Source("org/geogebra/common/icons_all/p25/icon-heart-filled.png")
	ImageResource favorite();

	@Source("org/geogebra/common/icons_all/p25/icon-heart-filled-hover.png")
	ImageResource favorite_hover();

	@Source("org/geogebra/common/icons_all/p25/icon-heart.png")
	ImageResource not_favorite();

	@Source("org/geogebra/common/icons_all/p25/icon-heart-hover.png")
	ImageResource not_favorite_hover();
}
