package geogebra.html5.gui.browser;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface BrowseResources extends ClientBundle {
	
	static BrowseResources INSTANCE = GWT.create(BrowseResources.class);
	
	@Source("icons/png/web/button_search.png")
	ImageResource search();

	@Source("icons/png/web/button_cancel.png")
	ImageResource dialog_cancel();

	@Source("icons/png/web/arrow_go_previous_purple.png")
	ImageResource back();
	
	@Source("icons/png/web/document_view.png")
	ImageResource document_view();
	
	@Source("icons/png/web/document_edit.png")
	ImageResource document_edit();
	
	@Source("icons/png/web/document_delete.png")
	ImageResource document_delete();
	
	@Source("icons/png/web/document_delete_active.png")
	ImageResource document_delete_active();
	
	@Source("icons/png/web/open-from-location_geogebratube.png")
	ImageResource location_tube();
	
	@Source("icons/png/web/open-from-location_googledrive.png")
	ImageResource location_drive();
	
	@Source("icons/png/web/open-from-location_skydrive.png")
	ImageResource location_skydrive();
	
	@Source("icons/png/web/open-from-location_local-storage.png")
	ImageResource location_local();

	@Source("icons/png/web/profile-options-arrow.png")
	ImageResource arrow_options();

	@Source("icons/png/web/menu_icons/menu-edit.png")
	ImageResource document_rename();

}
