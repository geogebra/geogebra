package org.geogebra.web.full.css;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.resources.client.Resource;
import org.gwtproject.resources.client.TextResource;

@Resource
public interface GuiResources extends ClientBundle {

	GuiResources INSTANCE = new GuiResourcesImpl();

	@Source("org/geogebra/common/icons/svg/web/ic_get_app_black_24px.svg")
	SVGResource get_app();

	@Source("org/geogebra/common/icons/png/web/ggb_logo_back.png")
	ImageResource header_back();

	// end PHONE GUI

	// TOOLBAR
	// Little triangles for tools with submenu
	@Source("org/geogebra/common/icons/png/web/toolbar-further-tools-icon.png")
	ImageResource toolbar_further_tools();

	// Icons
	@Source("org/geogebra/common/menu_icons/p20/menu-file-export.png")
	ImageResource menu_icons_file_export();

	@Source("org/geogebra/common/icons/png/web/menu_icons24/menu_view_exam.png")
	ImageResource menu_icon_exam24();

	@Source("org/geogebra/common/menu_icons/p20/menu-options.png")
	ImageResource menu_icon_options();

	@Source("org/geogebra/common/icons/png/web/menu-help.png")
	ImageResource icon_help();

	@Source("org/geogebra/common/menu_icons/p20/menu-tools.png")
	ImageResource menu_icon_tools();

	// OTHER MENUICONS

	@Source("org/geogebra/common/stylingbar/p20/stylingbar_dots.png")
	ImageResource menu_dots();

	// SUBMENUS
	@Source("org/geogebra/common/icons/png/web/arrow-submenu-right.png")
	ImageResource arrow_submenu_right();

	@Source("org/geogebra/common/icons/png/web/arrow-submenu-left.png")
	ImageResource arrow_submenu_left();

	// DIALOGS
	@Source("org/geogebra/common/icons/png/web/triangle_right.png")
	ImageResource triangle_right();

	@Source("org/geogebra/common/icons/png/web/triangle_down.png")
	ImageResource triangle_down();

	// STYLEBAR
	@Source("org/geogebra/common/icons/png/web/dockbar_triangle_left.png")
	ImageResource dockbar_triangle_left();

	@Source("org/geogebra/common/icons/png/web/dockbar_triangle_right.png")
	ImageResource dockbar_triangle_right();

	// PROPABILITY CALCULATOR
	@Source("org/geogebra/common/icons/png/web/probability_calculator_cumulative_distribution.png")
	ImageResource cumulative_distribution();

	@Source("org/geogebra/common/icons/svg/web/probability/interval_between.svg")
	SVGResource interval_between();

	@Source("org/geogebra/common/icons/svg/web/probability/interval_left.svg")
	SVGResource interval_left();

	@Source("org/geogebra/common/icons/svg/web/probability/interval_right.svg")
	SVGResource interval_right();

	@Source("org/geogebra/common/icons/svg/web/probability/interval_two_tailed.svg")
	SVGResource interval_two_tailed();

	@Source("org/geogebra/common/icons/svg/web/probability/normal_overlay.svg")
	SVGResource normal_overlay();

	@Source("org/geogebra/common/icons/svg/web/probability/bar_chart.svg")
	SVGResource bar_chart();

	@Source("org/geogebra/common/icons/svg/web/probability/line_graph.svg")
	SVGResource line_graph();

	@Source("org/geogebra/common/icons/svg/web/probability/step_graph.svg")
	SVGResource step_graph();

	// OBJECT PROPERTIES
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_properties_object.png")
	ImageResource properties_object();

	// DECORATIONS
	// ANGLE
	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_angle_1line.svg")
	SVGResource deco_angle_1line();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_angle_2lines.svg")
	SVGResource deco_angle_2lines();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_angle_3lines.svg")
	SVGResource deco_angle_3lines();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_angle_1stroke.svg")
	SVGResource deco_angle_1stroke();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_angle_2strokes.svg")
	SVGResource deco_angle_2strokes();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_angle_3strokes.svg")
	SVGResource deco_angle_3strokes();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_angle_arrow-up.svg")
	SVGResource deco_angle_arrow_up();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_angle_arrow-down.svg")
	SVGResource deco_angle_arrow_down();

	// SEGMENT
	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_segment_line.svg")
	SVGResource deco_segment_none();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_segment_1stroke.svg")
	SVGResource deco_segment_1stroke();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_segment_2strokes.svg")
	SVGResource deco_segment_2strokes();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_segment_3strokes.svg")
	SVGResource deco_segment_3strokes();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_segment_1arrow.svg")
	SVGResource deco_segment_1arrow();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_segment_2arrows.svg")
	SVGResource deco_segment_2arrows();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_segment_3arrows.svg")
	SVGResource deco_segment_3arrows();

	// AXES
	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_axes_line.svg")
	SVGResource deco_axes_none();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_axes_arrow.svg")
	SVGResource deco_axes_arrow();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_axes_arrows.svg")
	SVGResource deco_axes_arrows();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_axes_arrow_filled.svg")
	SVGResource deco_axes_arrow_filled();

	@Source("org/geogebra/common/icons/svg/web/decoration/decoration_axes_arrows_filled.svg")
	SVGResource deco_axes_arrows_filled();

	/*
	 * STYLINGBARS
	 * 
	 */

	@Source("org/geogebra/common/stylingbar/p24/stylebar_more.png")
	ImageResource stylebar_more();

	// Show construction protocol icon in navigation bar
	@Source("org/geogebra/common/icons_view_perspectives/p24/menu_view_construction_protocol.png")
	ImageResource icons_view_construction_protocol_p24();

	@Source("org/geogebra/web/pub/js/properties_keys_en.js")
	TextResource propertiesKeysJS();

	@Source("org/geogebra/common/icons/png/web/arrow-submenu-up.png")
	ImageResource arrow_submenu_up();
}
