package org.geogebra.web.web.gui.images;

import org.geogebra.web.web.gui.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.resources.client.ClientBundle;

public interface SvgPerspectiveResources extends PerspectiveResources, ClientBundle{
	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_algebra.svg")
	SVGResource menu_icon_algebra();
	
	@Source("org/geogebra/common/icons/svg/web/menu_icons/perspectives_geometry.svg")
	SVGResource menu_icon_geometry();
	
	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_cas.svg")
	SVGResource menu_icon_cas();
	
	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_graphics.svg")
	SVGResource menu_icon_graphics();
	
	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_graphics1.svg")
	SVGResource menu_icon_graphics1();
	
	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_graphics2.svg")
	SVGResource menu_icon_graphics2();
	
	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_graphics_extra.svg")
	SVGResource menu_icon_graphics_extra();

	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_spreadsheet.svg")
	SVGResource menu_icon_spreadsheet();
	
	@Source("org/geogebra/common/icons/svg/web/menu_icons/perspectives_algebra_3Dgraphics.svg")
	SVGResource menu_icon_graphics3D();
	
	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_construction_protocol.svg")
	SVGResource menu_icon_construction_protocol();
	
	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_probability.svg")
	SVGResource menu_icon_probability();
	
	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylingbar_icon_algebra.svg")
	SVGResource styleBar_algebraView();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylingbar_icon_graphics.svg")
	SVGResource styleBar_graphicsView();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylingbar_icon_cas.svg")
	SVGResource styleBar_CASView();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylingbar_icon_construction_protocol.svg")
	SVGResource styleBar_ConstructionProtocol();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylingbar_icon_graphics3D.svg")
	SVGResource styleBar_graphics3dView();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylingbar_icon_graphics2.svg")
	SVGResource styleBar_graphics2View();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylingbar_icon_spreadsheet.svg")
	SVGResource styleBar_spreadsheetView();
	
	@Source("org/geogebra/common/icons/svg/web/menu-button-open-search.svg")
	SVGResource button_open_search();
	
	@Source("org/geogebra/common/icons/svg/web/menu-button-open-menu.svg")
	SVGResource button_open_menu();
	
	@Source("org/geogebra/common/icons/svg/web/menu-edit-undo.svg")
	SVGResource button_undo();
	
	@Source("org/geogebra/common/icons/svg/web/menu-edit-redo.svg")
	SVGResource button_redo();
}
