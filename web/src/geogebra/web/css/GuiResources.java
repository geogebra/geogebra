package geogebra.web.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResources extends ClientBundle {

	GuiResources INSTANCE = GWT.create(GuiResources.class);

	// @Source("geogebra/resources/images/ggb4-splash-h120.png")
	// ImageResource getGeoGebraWebSplash();

	@Source("geogebra/resources/images/spinner.gif")
	ImageResource getGeoGebraWebSpinner();

	@Source("geogebra/resources/images/spacer.png")
	ImageResource spacer();

	@Source("icons/png/web/algebra-view-tree-open.png")
	ImageResource algebra_tree_open();

	@Source("icons/png/web/algebra-view-tree-closed.png")
	ImageResource algebra_tree_closed();

	@Source("icons/png/web/ggb_logo_back.png")
	ImageResource header_back();

	// PHONE GUI
	@Source("icons/png/menu_view_algebra.png")
	ImageResource algebraView();

	@Source("icons/png/menu_view_graphics.png")
	ImageResource graphicsView();

	@Source("icons/png/web/menu-button-open-search.png")
	ImageResource browseView();

	@Source("icons/png/web/menu-button-open-menu.png")
	ImageResource options();

	// end PHONE GUI

	// StyleBar

	// end StyleBar

	/*
	 * @Source("geogebra/resources/images/triangle-down.png") ImageResource
	 * triangle_down();
	 */

	@Source("geogebra/resources/images/splash-ggb4.svg")
	TextResource ggb4Splash();

	@Source("geogebra/resources/js/gif.worker.js")
	TextResource gifWorkerJs();

	@Source("geogebra/common/main/xml/default-preferences.xml")
	TextResource preferencesXML();

	// TOOLBAR
	// Little triangles for tools with submenu
	@Source("icons/png/web/toolbar-further-tools-icon.png")
	ImageResource toolbar_further_tools();

	// SMART MENUBAR

	@Source("icons/png/web/menuBarSubMenuIconRTL.png")
	ImageResource menuBarSubMenuIconRTL();

	@Source("icons/png/web/menuBarSubMenuIconLTR.png")
	ImageResource menuBarSubMenuIconLTR();

	// Icons
	@Source("icons/png/web/menu_icons/menu-file.png")
	ImageResource menu_icon_file();

	@Source("icons/png/web/menu_icons/menu-file-new.png")
	ImageResource menu_icon_file_new();

	@Source("icons/png/web/menu_icons/menu-file-open.png")
	ImageResource menu_icon_file_open();

	@Source("icons/png/web/menu_icons/menu-file-save.png")
	ImageResource menu_icon_file_save();

	@Source("icons/png/web/menu_icons/menu-file-share.png")
	ImageResource menu_icon_file_share();

	@Source("icons/png/web/menu_icons/menu-file-export.png")
	ImageResource menu_icons_file_export();

	@Source("icons/png/web/menu_icons/menu-edit.png")
	ImageResource menu_icon_edit();

	@Source("icons/png/web/menu_icons/menu-edit-undo.png")
	ImageResource menu_icon_edit_undo();

	@Source("icons/png/web/menu_icons/menu-edit-redo.png")
	ImageResource menu_icon_edit_redo();

	@Source("icons/png/web/menu_icons/menu-edit-copy.png")
	ImageResource menu_icon_edit_copy();

	@Source("icons/png/web/menu_icons/menu-edit-paste.png")
	ImageResource menu_icon_edit_paste();

	@Source("icons/png/web/menu_icons/menu-edit-delete.png")
	ImageResource menu_icon_edit_delete();

	@Source("icons/png/web/menu_icons/menu-perspectives.png")
	ImageResource menu_icon_perspectives();

	@Source("icons/png/web/menu_icons/menu-view.png")
	ImageResource menu_icon_view();

	@Source("icons/png/web/menu_icons/menu-options.png")
	ImageResource menu_icon_options();

	@Source("icons/png/web/menu_icons/menu-options-point-capturing.png")
	ImageResource menu_icon_options_point_capturing();

	@Source("icons/png/web/menu_icons/menu-options-labeling.png")
	ImageResource menu_icon_options_labeling();

	@Source("icons/png/web/menu_icons/menu-options-font-size.png")
	ImageResource menu_icon_options_font_size();

	@Source("icons/png/web/menu_icons/menu-options-language.png")
	ImageResource menu_icon_options_language();

	@Source("icons/png/web/menu_icons/menu-help.png")
	ImageResource menu_icon_help();

	@Source("icons/png/web/menu_icons/menu-help-about.png")
	ImageResource menu_icon_help_about();

	@Source("icons/png/web/menu_icons/menu-tools.png")
	ImageResource menu_icon_tools();

	@Source("icons/png/web/menu_icons/menu-tools-new.png")
	ImageResource menu_icon_tools_new();

	@Source("icons/png/web/menu_icons/menu-tools-customize.png")
	ImageResource menu_icon_tools_customize();

	// OTHER MENUICONS

	@Source("icons/png/web/menu_icons/menu-signed-in-m.png")
	ImageResource menu_icon_signed_in_m();

	@Source("icons/png/web/menu_icons/menu-signed-in-f.png")
	ImageResource menu_icon_signed_in_f();

	@Source("icons/png/web/menu_icons/menu-sign-in.png")
	ImageResource menu_icon_sign_in();

	@Source("icons/png/web/menu_icons/menu-sign-out.png")
	ImageResource menu_icon_sign_out();

	// SUBMENUS
	@Source("icons/png/web/arrow-submenu-right.png")
	ImageResource arrow_submenu_right();

	@Source("icons/png/web/arrow-submenu-up.png")
	ImageResource arrow_submenu_up();

	// ALGEBRA INPUT
	@Source("icons/png/web/dockbar_triangle_left_darker.png")
	ImageResource input_help_left();

	@Source("icons/png/web/dockbar_triangle_up_darker.png")
	ImageResource input_help_up();

	// DIALOGS
	@Source("icons/png/web/triangle_right.png")
	ImageResource triangle_right();

	@Source("icons/png/web/triangle_down.png")
	ImageResource triangle_down();

	// STYLEBAR
	@Source("icons/png/web/dockbar_triangle_left.png")
	ImageResource dockbar_triangle_left();

	@Source("icons/png/web/dockbar_triangle_right.png")
	ImageResource dockbar_triangle_right();

	@Source("icons/png/web/dockbar_drag.png")
	ImageResource dockbar_drag();

	@Source("icons/png/web/menu_icons/menu-view-close.png")
	ImageResource dockbar_close();

	@Source("icons/png/web/menu_icons/menu-file-new.png")
	ImageResource dockbar_open();

	// PROPABILITY CALCULATOR
	@Source("icons/png/web/probability_calculator_cumulative_distribution.png")
	ImageResource cumulative_distribution();

	@Source("icons/png/web/probability_calculator_export.png")
	ImageResource prob_calc_export();

	@Source("icons/png/web/probability_calculator_interval_between.png")
	ImageResource interval_between();

	@Source("icons/png/web/probability_calculator_interval_left.png")
	ImageResource interval_left();

	@Source("icons/png/web/probability_calculator_interval_right.png")
	ImageResource interval_right();

	@Source("icons/png/web/probability_calculator_line_graph.png")
	ImageResource line_graph();

	@Source("icons/png/web/probability_calculator_normal_overlay.png")
	ImageResource normal_overlay();

	@Source("icons/png/web/probability_calculator_step_graph.png")
	ImageResource step_graph();

	// OBJECT PROPERTIES
	@Source("icons/png/web/little-triangle-down.png")
	ImageResource little_triangle_down();

	@Source("icons/png/web/little-triangle-down-active.png")
	ImageResource little_triangle_down_active();

	@Source("icons/png/web/stylingbar/stylingbar_properties_object.png")
	ImageResource properties_object();

	/*
	 * @Source("icons/png/web/stylingbar/stylingbar_properties_graphics_view.png"
	 * ) ImageResource properties_graphics();
	 * 
	 * @Source("icons/png/web/stylingbar/stylingbar_properties_graphics_view2.png"
	 * ) ImageResource properties_graphics2();
	 */

	// DECORATIONS
	// ANGLE
	@Source("icons/png/web/decoration_icons/decoration_angle_1line.png")
	ImageResource deco_angle_1line();

	@Source("icons/png/web/decoration_icons/decoration_angle_2lines.png")
	ImageResource deco_angle_2lines();

	@Source("icons/png/web/decoration_icons/decoration_angle_3lines.png")
	ImageResource deco_angle_3lines();

	@Source("icons/png/web/decoration_icons/decoration_angle_1stroke.png")
	ImageResource deco_angle_1stroke();

	@Source("icons/png/web/decoration_icons/decoration_angle_2strokes.png")
	ImageResource deco_angle_2strokes();

	@Source("icons/png/web/decoration_icons/decoration_angle_3strokes.png")
	ImageResource deco_angle_3strokes();

	@Source("icons/png/web/decoration_icons/decoration_angle_arrow-up.png")
	ImageResource deco_angle_arrow_up();

	@Source("icons/png/web/decoration_icons/decoration_angle_arrow-down.png")
	ImageResource deco_angle_arrow_down();

	// SEGMENT
	@Source("icons/png/web/decoration_icons/decoration_segment_line.png")
	ImageResource deco_segment_none();

	@Source("icons/png/web/decoration_icons/decoration_segment_1stroke.png")
	ImageResource deco_segment_1stroke();

	@Source("icons/png/web/decoration_icons/decoration_segment_2strokes.png")
	ImageResource deco_segment_2strokes();

	@Source("icons/png/web/decoration_icons/decoration_segment_3strokes.png")
	ImageResource deco_segment_3strokes();

	@Source("icons/png/web/decoration_icons/decoration_segment_1arrow.png")
	ImageResource deco_segment_1arrow();

	@Source("icons/png/web/decoration_icons/decoration_segment_2arrows.png")
	ImageResource deco_segment_2arrows();

	@Source("icons/png/web/decoration_icons/decoration_segment_3arrows.png")
	ImageResource deco_segment_3arrows();

	// AXES
	@Source("icons/png/web/decoration_icons/decoration_axes_line.png")
	ImageResource deco_axes_none();

	@Source("icons/png/web/decoration_icons/decoration_axes_arrow.png")
	ImageResource deco_axes_arrow();

	@Source("icons/png/web/decoration_icons/decoration_axes_arrows.png")
	ImageResource deco_axes_arrows();

	@Source("icons/png/web/decoration_icons/decoration_axes_arrow_filled.png")
	ImageResource deco_axes_arrow_filled();

	@Source("icons/png/web/decoration_icons/decoration_axes_arrows_filled.png")
	ImageResource deco_axes_arrows_filled();

	// ONSCREENKEYBOARD
	@Source("geogebra/web/gui/images/view_close.png")
	ImageResource keyboard_close();

	// ALGEBRA VIEW
	@Source("geogebra/web/gui/images/view-close.png")
	ImageResource algebraViewDeleteEntry();
}
