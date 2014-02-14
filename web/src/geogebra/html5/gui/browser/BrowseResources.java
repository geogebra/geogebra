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

	@Source("icons/png/arrow_go_previous_gray.png")
	ImageResource back();
	
	@Source("icons/png/android/document_viewer.png")
	ImageResource document_viewer();
	
	@Source("icons/png/android/document_edit.png")
	ImageResource document_edit();

}
