package geogebra.web.gui.images;

import geogebra.web.gui.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.resources.client.ClientBundle;

public interface SvgPerspectiveResources extends PerspectiveResources, ClientBundle{
	@Source("icons/svg/web/menu_icons/menu_view_algebra.svg")
	SVGResource menu_icon_algebra();
	
	@Source("icons/svg/web/menu_icons/perspectives_geometry.svg")
	SVGResource menu_icon_geometry();
	
	@Source("icons/svg/web/menu_icons/menu_view_cas.svg")
	SVGResource menu_icon_cas();
	
	@Source("icons/svg/web/menu_icons/menu_view_graphics.svg")
	SVGResource menu_icon_graphics();
	
	@Source("icons/svg/web/menu_icons/menu_view_graphics1.svg")
	SVGResource menu_icon_graphics1();
	
	@Source("icons/svg/web/menu_icons/menu_view_graphics2.svg")
	SVGResource menu_icon_graphics2();
	
	@Source("icons/svg/web/menu_icons/menu_view_spreadsheet.svg")
	SVGResource menu_icon_spreadsheet();
	
	@Source("icons/svg/web/menu_icons/perspectives_algebra_3Dgraphics.svg")
	SVGResource menu_icon_graphics3D();
	
	@Source("icons/svg/web/menu_icons/menu_view_construction_protocol.svg")
	SVGResource menu_icon_construction_protocol();
	
	@Source("icons/svg/web/menu_icons/menu_view_probability.svg")
	SVGResource menu_icon_probability();
	
	@Source("icons/svg/web/menu_icons/menu_view_algebra.svg")
	SVGResource styleBar_algebraView();

	@Source("icons/svg/web/menu_icons/menu_view_graphics.svg")
	SVGResource styleBar_graphicsView();

	@Source("icons/svg/web/menu_icons/menu_view_cas.svg")
	SVGResource styleBar_CASView();

	@Source("icons/svg/web/menu_icons/menu_view_construction_protocol.svg")
	SVGResource styleBar_ConstructionProtocol();

	@Source("icons/svg/web/menu_icons/perspectives_algebra_3Dgraphics.svg")
	SVGResource styleBar_graphics3dView();

	@Source("icons/svg/web/menu_icons/menu_view_graphics2.svg")
	SVGResource styleBar_graphics2View();

	@Source("icons/svg/web/menu_icons/menu_view_spreadsheet.svg")
	SVGResource styleBar_spreadsheetView();
}
