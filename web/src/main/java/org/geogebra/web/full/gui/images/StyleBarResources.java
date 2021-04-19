package org.geogebra.web.full.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

@SuppressWarnings("javadoc")
public interface StyleBarResources extends ClientBundle {
	StyleBarResources INSTANCE = GWT.create(StyleBarResources.class);
	//ALGEBRA STYLEBAR

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_algebraview_sort_objects_by.png")
	ImageResource sortObjects();

	// SPREADSHEET
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_spreadsheetview_show_input_bar.png")
	ImageResource description();
	
}
