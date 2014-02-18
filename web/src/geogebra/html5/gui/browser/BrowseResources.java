package geogebra.html5.gui.browser;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface BrowseResources extends ClientBundle {
	
	static BrowseResources INSTANCE = GWT.create(BrowseResources.class);
	
	@Source("icons/png/view_zoom.png")
	ImageResource search();

	@Source("icons/png/android/button_cancel.png")
	ImageResource dialog_cancel();

	@Source("icons/png/web/arrow_go_previous_purple.png")
	ImageResource back();
	
	@Source("icons/png/web/document_viewer.png")
	ImageResource document_viewer();
	
	@Source("icons/png/web/document_edit.png")
	ImageResource document_edit();
	
	@Source("icons/png/web/open-from-location_geogebratube.png")
	ImageResource location_tube();
	
	@Source("icons/png/web/open-from-location_googledrive.png")
	ImageResource location_drive();
	
	@Source("icons/png/web/open-from-location_local-storage.png")
	ImageResource location_local();

}
