package org.geogebra.web.web.gui.browser;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface BrowseResources extends ClientBundle {
	
	static BrowseResources INSTANCE = GWT.create(BrowseResources.class);
	
	@Source("org/geogebra/common/icons/png/web/button_search.png")
	ImageResource search();

	@Source("org/geogebra/common/icons/png/web/button_cancel.png")
	ImageResource dialog_cancel();

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
	
	@Source("org/geogebra/common/icons/png/web/open-from-location_skydrive.png")
	ImageResource location_skydrive();
	
	@Source("org/geogebra/common/icons/png/web/open-from-location_local-storage.png")
	ImageResource location_local();

	@Source("org/geogebra/common/icons/png/web/profile-options-arrow.png")
	ImageResource arrow_options();

	@Source("org/geogebra/common/icons/png/web/document_favourite_selected.png")
	ImageResource favorite();

	@Source("org/geogebra/common/icons/png/web/document_favourite.png")
	ImageResource not_favorite();
}
