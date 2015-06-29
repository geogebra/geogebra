package org.geogebra.web.web.gui.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PngPerspectiveResources extends PerspectiveResources, ClientBundle{
	/*@Source("org/geogebra/web/web/gui/images/view_close.png")
	ImageResource view_close();*/
	
	
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu_view_algebra.png")
	ImageResource menu_icon_algebra();
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/perspectives_geometry.png")
	ImageResource menu_icon_geometry();
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu_view_cas.png")
	ImageResource menu_icon_cas();
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu_view_graphics.png")
	ImageResource menu_icon_graphics();
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu_view_graphics1.png")
	ImageResource menu_icon_graphics1();
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu_view_graphics2.png")
	ImageResource menu_icon_graphics2();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu_view_graphics2.png")
	ImageResource menu_icon_graphics_for_plane();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu_view_spreadsheet.png")
	ImageResource menu_icon_spreadsheet();
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/perspectives_algebra_3Dgraphics.png")
	ImageResource menu_icon_graphics3D();
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu_view_construction_protocol.png")
	ImageResource menu_icon_construction_protocol();
	
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu_view_probability.png")
	ImageResource menu_icon_probability();
	
	// StyleBar
		@Source("org/geogebra/common/icons/png24x24/stylebar_icon_algebra.png")
		ImageResource styleBar_algebraView();

		@Source("org/geogebra/common/icons/png24x24/stylebar_icon_graphics.png")
		ImageResource styleBar_graphicsView();

		@Source("org/geogebra/common/icons/png24x24/stylebar_icon_cas.png")
		ImageResource styleBar_CASView();

		@Source("org/geogebra/common/icons/png24x24/stylebar_icon_construction_protocol.png")
		ImageResource styleBar_ConstructionProtocol();

		@Source("org/geogebra/common/icons/png24x24/stylebar_icon_3Dgraphics.png")
		ImageResource styleBar_graphics3dView();

		@Source("org/geogebra/common/icons/png24x24/stylebar_icon_graphics2.png")
		ImageResource styleBar_graphics2View();

		@Source("org/geogebra/common/icons/png24x24/stylebar_icon_spreadsheet.png")
		ImageResource styleBar_spreadsheetView();
		
		@Source("org/geogebra/common/icons/png/web/menu-button-open-search.png")
		ImageResource button_open_search();
		
		@Source("org/geogebra/common/icons/png/web/menu-button-open-menu.png")
		ImageResource button_open_menu();
		
		//REDO UNDO
		@Source("org/geogebra/common/icons/png/web/menu_edit_undo.png")
		ImageResource button_undo();
		
		@Source("org/geogebra/common/icons/png/web/menu_edit_redo.png")
		ImageResource button_redo();
}
