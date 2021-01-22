package org.geogebra.web.full.gui.images;

import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * SVGs of the main buttons
 */
@SuppressWarnings("javadoc")
public interface SvgPerspectiveResources extends ClientBundle {

	SvgPerspectiveResources INSTANCE = GWT.create(SvgPerspectiveResources.class);

	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_algebra.svg")
	SVGResource menu_icon_algebra();

	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_cas.svg")
	SVGResource menu_icon_cas();

	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_graphics.svg")
	SVGResource menu_icon_exam();

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

	@Source("org/geogebra/common/icons/svg/web/menu-edit-redo.svg")
	SVGResource menu_header_redo();

	@Source("org/geogebra/common/icons/svg/web/menu-edit-undo.svg")
	SVGResource menu_header_undo();

	@Source("org/geogebra/common/icons/svg/web/menu-button-open-search.svg")
	SVGResource menu_header_open_search();

	@Source("org/geogebra/common/icons/svg/web/menu-button-open-menu.svg")
	SVGResource menu_header_open_menu();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylebar_icon_graphics_extra.svg")
	SVGResource styleBar_graphics_extra();

	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_view_whiteboard.svg")
	SVGResource menu_icon_whiteboard();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_algebra.svg")
	SVGResource menu_icon_algebra_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_graphics.svg")
	SVGResource menu_icon_geometry_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_cas.svg")
	SVGResource menu_icon_cas_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_graphics2.svg")
	SVGResource menu_icon_graphics2_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_graphics_extra.svg")
	SVGResource menu_icon_graphics_extra_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_spreadsheet.svg")
	SVGResource menu_icon_spreadsheet_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_3d.svg")
	SVGResource menu_icon_graphics3D_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_construction_protocol.svg")
	SVGResource menu_icon_construction_protocol_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_probability.svg")
	SVGResource menu_icon_probability_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_whiteboard.svg")
	SVGResource menu_icon_whiteboard_transparent();

	@Source("org/geogebra/common/icons/svg/web/menuIconsTransparent/menu_view_exam.svg")
	SVGResource menu_icon_exam_transparent();

	@Source("org/geogebra/common/icons/svg/web/menu_icons/cas.svg")
	SVGResource cas_white_bg();

	// StyleBar

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/settings.svg")
	SVGResource settings();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/ic_arrow_back_black_24px.svg")
	SVGResource menu_header_back();

}
