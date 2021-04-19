package org.geogebra.web.full.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

@SuppressWarnings("javadoc")
public interface StyleBarResources extends ClientBundle {
	StyleBarResources INSTANCE = GWT.create(StyleBarResources.class);
	
	//EUCLIDIAN STYLEBAR:

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_show_or_hide_the_axes.png")
	ImageResource axes();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_standardview.png")
	ImageResource standard_view();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_view_all_objects.png")
	ImageResource view_all_objects();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_point_capturing.png")
	ImageResource magnet();
	
	//DELETE STYLEBAR

	//TEXT


	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_object_fixed.png")
	ImageResource objectFixed();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_object_unfixed.png")
	ImageResource objectUnfixed();

	//ALGEBRA STYLEBAR

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_algebraview_sort_objects_by.png")
	ImageResource sortObjects();

	// SPREADSHEET
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_spreadsheetview_show_input_bar.png")
	ImageResource description();
	
}
