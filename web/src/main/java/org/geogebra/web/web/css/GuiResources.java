package org.geogebra.web.web.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResources extends ClientBundle {

	GuiResources INSTANCE = GWT.create(GuiResources.class);

	// @Source("org/geogebra/web/resources/images/ggb4-splash-h120.png")
	// ImageResource getGeoGebraWebSplash();

	@Source("org/geogebra/web/resources/images/spinner.gif")
	ImageResource getGeoGebraWebSpinner();

	@Source("org/geogebra/web/resources/images/spacer.png")
	ImageResource spacer();

	@Source("org/geogebra/common/icons/png/web/algebra-view-tree-open.png")
	ImageResource algebra_tree_open();

	@Source("org/geogebra/common/icons/png/web/algebra-view-tree-closed.png")
	ImageResource algebra_tree_closed();

	@Source("org/geogebra/common/icons/png/web/ggb_logo_back.png")
	ImageResource header_back();

	// PHONE GUI
	@Source("org/geogebra/common/icons/png/menu_view_algebra.png")
	ImageResource algebraView();

	@Source("org/geogebra/common/icons/png/menu_view_graphics.png")
	ImageResource graphicsView();

	@Source("org/geogebra/common/icons/png/web/menu-button-open-search.png")
	ImageResource browseView();

	@Source("org/geogebra/common/icons/png/web/menu-button-open-menu.png")
	ImageResource options();

	// end PHONE GUI

	// StyleBar

	// end StyleBar

	/*
	 * @Source("org/geogebra/web/resources/images/triangle-down.png") ImageResource
	 * triangle_down();
	 */

	@Source("org/geogebra/web/resources/images/splash-ggb4.svg")
	TextResource ggb4Splash();

	@Source("org/geogebra/web/resources/js/gif.worker.js")
	TextResource gifWorkerJs();

	// TOOLBAR
	// Little triangles for tools with submenu
	@Source("org/geogebra/common/icons/png/web/toolbar-further-tools-icon.png")
	ImageResource toolbar_further_tools();

	// HEADER MENU
	@Source("org/geogebra/common/menu_header/p32/menu_edit_redo.png")
	ImageResource menu_header_redo();

	@Source("org/geogebra/common/menu_header/p32/menu_edit_redo_hover.png")
	ImageResource menu_header_redo_hover();

	@Source("org/geogebra/common/menu_header/p32/menu_edit_redo.png")
	ImageResource menu_header_undo();

	@Source("org/geogebra/common/menu_header/p32/menu_edit_redo_hover.png")
	ImageResource menu_header_undo_hover();

	@Source("org/geogebra/common/menu_header/p32/menu_open_search.png")
	ImageResource menu_header_open_search();

	@Source("org/geogebra/common/menu_header/p32/menu_open_search_hover.png")
	ImageResource menu_header_open_search_hover();

	@Source("org/geogebra/common/menu_header/p32/menu_open_menu.png")
	ImageResource menu_header_open_menu();

	@Source("org/geogebra/common/menu_header/p32/menu_open_menu_hover.png")
	ImageResource menu_header_open_menu_hover();

	// SMART MENUBAR

	@Source("org/geogebra/common/icons/png/web/menuBarSubMenuIconRTL.png")
	ImageResource menuBarSubMenuIconRTL();

	@Source("org/geogebra/common/icons/png/web/menuBarSubMenuIconLTR.png")
	ImageResource menuBarSubMenuIconLTR();

	// Icons
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-file.png")
	ImageResource menu_icon_file();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-file-new.png")
	ImageResource menu_icon_file_new();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-file-open.png")
	ImageResource menu_icon_file_open();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-file-save.png")
	ImageResource menu_icon_file_save();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-file-share.png")
	ImageResource menu_icon_file_share();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-file-export.png")
	ImageResource menu_icons_file_export();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit.png")
	ImageResource menu_icon_edit();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-undo.png")
	ImageResource menu_icon_edit_undo();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-redo.png")
	ImageResource menu_icon_edit_redo();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-copy.png")
	ImageResource menu_icon_edit_copy();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-paste.png")
	ImageResource menu_icon_edit_paste();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-delete.png")
	ImageResource menu_icon_edit_delete();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-perspectives.png")
	ImageResource menu_icon_perspectives();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-view.png")
	ImageResource menu_icon_view();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options.png")
	ImageResource menu_icon_options();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options-point-capturing.png")
	ImageResource menu_icon_options_point_capturing();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options-labeling.png")
	ImageResource menu_icon_options_labeling();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options-font-size.png")
	ImageResource menu_icon_options_font_size();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options-language.png")
	ImageResource menu_icon_options_language();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-help.png")
	ImageResource menu_icon_help();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-help-about.png")
	ImageResource menu_icon_help_about();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-tools.png")
	ImageResource menu_icon_tools();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-tools-new.png")
	ImageResource menu_icon_tools_new();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-tools-customize.png")
	ImageResource menu_icon_tools_customize();

	// Menu icons views and perspectives
	@Source("org/geogebra/common/icons_view_perspectives/p20/menu_view_algebra.png")
	ImageResource menu_icon_algebra();

	@Source("org/geogebra/common/icons_view_perspectives/p20/menu_view_cas.png")
	ImageResource menu_icon_cas();

	@Source("org/geogebra/common/icons_view_perspectives/p20/menu_view_construction_protocol.png")
	ImageResource menu_icon_construction_protocol();

	@Source("org/geogebra/common/icons_view_perspectives/p20/menu_view_graphics.png")
	ImageResource menu_icon_graphics();

	// @Source("org/geogebra/common/icons_view_perspectives/p20/menu_view_graphics_extra.png")
	// ImageResource menu_icon_graphics_extra();

	@Source("org/geogebra/common/icons_view_perspectives/p20/menu_view_graphics2.png")
	ImageResource menu_icon_graphics2();

	@Source("org/geogebra/common/icons_view_perspectives/p20/menu_view_input_bar.png")
	ImageResource menu_icon_input_bar();

	@Source("org/geogebra/common/icons_view_perspectives/p20/menu_view_probability.png")
	ImageResource menu_icon_probability();

	@Source("org/geogebra/common/icons_view_perspectives/p20/menu_view_spreadsheet.png")
	ImageResource menu_icon_spreadsheet();

	@Source("org/geogebra/common/icons_view_perspectives/p20/perspectives_algebra_3Dgraphics.png")
	ImageResource menu_icon_3Dgraphics();

	@Source("org/geogebra/common/icons_view_perspectives/p20/perspectives_geometry.png")
	ImageResource menu_icon_geometry();


	// OTHER MENUICONS

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-signed-in-m.png")
	ImageResource menu_icon_signed_in_m();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-signed-in-f.png")
	ImageResource menu_icon_signed_in_f();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-sign-in.png")
	ImageResource menu_icon_sign_in();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-sign-out.png")
	ImageResource menu_icon_sign_out();

	// SUBMENUS
	@Source("org/geogebra/common/icons/png/web/arrow-submenu-right.png")
	ImageResource arrow_submenu_right();

	@Source("org/geogebra/common/icons/png/web/arrow-submenu-up.png")
	ImageResource arrow_submenu_up();

	// ALGEBRA INPUT
	@Source("org/geogebra/common/icons/png/web/dockbar_triangle_left_darker.png")
	ImageResource input_help_left();

	@Source("org/geogebra/common/icons/png/web/dockbar_triangle_up_darker.png")
	ImageResource input_help_up();

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

	@Source("org/geogebra/common/icons/png/web/dockbar_drag.png")
	ImageResource dockbar_drag();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-view-close.png")
	ImageResource dockbar_close();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-file-new.png")
	ImageResource dockbar_open();

	// PROPABILITY CALCULATOR
	@Source("org/geogebra/common/icons/png/web/probability_calculator_cumulative_distribution.png")
	ImageResource cumulative_distribution();

	@Source("org/geogebra/common/icons/png/web/probability_calculator_export.png")
	ImageResource prob_calc_export();

	@Source("org/geogebra/common/icons/png/web/probability_calculator_interval_between.png")
	ImageResource interval_between();

	@Source("org/geogebra/common/icons/png/web/probability_calculator_interval_left.png")
	ImageResource interval_left();

	@Source("org/geogebra/common/icons/png/web/probability_calculator_interval_right.png")
	ImageResource interval_right();

	@Source("org/geogebra/common/icons/png/web/probability_calculator_line_graph.png")
	ImageResource line_graph();

	@Source("org/geogebra/common/icons/png/web/probability_calculator_normal_overlay.png")
	ImageResource normal_overlay();

	@Source("org/geogebra/common/icons/png/web/probability_calculator_step_graph.png")
	ImageResource step_graph();

	// OBJECT PROPERTIES
	@Source("org/geogebra/common/icons/png/web/little-triangle-down.png")
	ImageResource little_triangle_down();

	@Source("org/geogebra/common/icons/png/web/little-triangle-down-active.png")
	ImageResource little_triangle_down_active();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_properties_object.png")
	ImageResource properties_object();

	/*
	 * @Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_properties_graphics_view.png"
	 * ) ImageResource properties_graphics();
	 * 
	 * @Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_properties_graphics_view2.png"
	 * ) ImageResource properties_graphics2();
	 */

	// DECORATIONS
	// ANGLE
	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_angle_1line.png")
	ImageResource deco_angle_1line();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_angle_2lines.png")
	ImageResource deco_angle_2lines();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_angle_3lines.png")
	ImageResource deco_angle_3lines();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_angle_1stroke.png")
	ImageResource deco_angle_1stroke();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_angle_2strokes.png")
	ImageResource deco_angle_2strokes();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_angle_3strokes.png")
	ImageResource deco_angle_3strokes();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_angle_arrow-up.png")
	ImageResource deco_angle_arrow_up();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_angle_arrow-down.png")
	ImageResource deco_angle_arrow_down();

	// SEGMENT
	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_segment_line.png")
	ImageResource deco_segment_none();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_segment_1stroke.png")
	ImageResource deco_segment_1stroke();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_segment_2strokes.png")
	ImageResource deco_segment_2strokes();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_segment_3strokes.png")
	ImageResource deco_segment_3strokes();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_segment_1arrow.png")
	ImageResource deco_segment_1arrow();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_segment_2arrows.png")
	ImageResource deco_segment_2arrows();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_segment_3arrows.png")
	ImageResource deco_segment_3arrows();

	// AXES
	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_axes_line.png")
	ImageResource deco_axes_none();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_axes_arrow.png")
	ImageResource deco_axes_arrow();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_axes_arrows.png")
	ImageResource deco_axes_arrows();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_axes_arrow_filled.png")
	ImageResource deco_axes_arrow_filled();

	@Source("org/geogebra/common/icons/png/web/decoration_icons/decoration_axes_arrows_filled.png")
	ImageResource deco_axes_arrows_filled();

	// ALGEBRA VIEW SPECIAL EDITING
	@Source("org/geogebra/common/icons/png/web/stylingbar/algebra_delete.png")
	ImageResource algebra_delete();

	@Source("org/geogebra/common/icons/png/web/stylingbar/algebra_delete_dark.png")
	ImageResource algebra_delete_dark();

	@Source("org/geogebra/common/icons/png/web/stylingbar/algebra_delete_hover.png")
	ImageResource algebra_delete_hover();

	@Source("org/geogebra/common/icons/png/web/stylingbar/algebra_matrix_size.png")
	ImageResource algebra_matrix_size();

	@Source("org/geogebra/common/icons/png/web/stylingbar/algebra_matrix_size_hover.png")
	ImageResource algebra_matrix_size_hover();

	@Source("org/geogebra/common/icons/png/web/stylingbar/algebra_new.png")
	ImageResource algebra_new();

	@Source("org/geogebra/common/icons/png/web/stylingbar/algebra_new_hover.png")
	ImageResource algebra_new_hover();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-remove.png")
	ImageResource algebra_remove();

	@Source("org/geogebra/common/icons/png/web/menu_icons/stylingbar_algebraview_new_matrix.png")
	ImageResource algebra_new_matrix();

	@Source("org/geogebra/common/icons/png/web/menu_icons/stylingbar_algebraview_new_piecewise_function.png")
	ImageResource algebra_new_piecewise();

	@Source("org/geogebra/common/icons/png/web/menu_icons/stylingbar_algebraview_new_parametric_curve.png")
	ImageResource algebra_new_parametric();
	
	// PLAY, PAUSE, REWIND, ETC. USED IN ALGEBRA VIEW, EV AND IN NAVIGATION BAR
	@Source("org/geogebra/common/icons_play/p24/nav_play_circle.png")
	ImageResource icons_play_circle();

	@Source("org/geogebra/common/icons_play/p24/nav_play_circle_hover.png")
	ImageResource icons_play_circle_hover();

	@Source("org/geogebra/common/icons_play/p24/nav_pause_circle.png")
	ImageResource icons_play_pause_circle();

	@Source("org/geogebra/common/icons_play/p24/nav_pause_circle_hover.png")
	ImageResource icons_play_pause_circle_hover();

	@Source("org/geogebra/common/icons_play/p24/nav_skipback.png")
	ImageResource icons_play_skipback();

	@Source("org/geogebra/common/icons_play/p24/nav_skipback_hover.png")
	ImageResource icons_play_skipback_hover();

	@Source("org/geogebra/common/icons_play/p24/nav_rewind.png")
	ImageResource icons_play_rewind();

	@Source("org/geogebra/common/icons_play/p24/nav_rewind_hover.png")
	ImageResource icons_play_rewind_hover();

	@Source("org/geogebra/common/icons_play/p24/nav_fastforward.png")
	ImageResource icons_play_fastforward();

	@Source("org/geogebra/common/icons_play/p24/nav_fastforward_hover.png")
	ImageResource icons_play_fastforward_hover();

	@Source("org/geogebra/common/icons_play/p24/nav_skipforward.png")
	ImageResource icons_play_skipforward();

	@Source("org/geogebra/common/icons_play/p24/nav_skipforward_hover.png")
	ImageResource icons_play_skipforward_hover();

	// Show construction protocol icon in navigation bar
	@Source("org/geogebra/common/icons_view_perspectives/p24/menu_view_construction_protocol.png")
	ImageResource icons_view_construction_protocol_p24();

	// PLAY, PAUSE, REWIND, ARROWS, CANCEL
	// USED FOR BUTTON FILLINGS
	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_down.png")
	ImageResource icons_fillings_arrow_big_down();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_up.png")
	ImageResource icons_fillings_arrow_big_up();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_left.png")
	ImageResource icons_fillings_arrow_big_left();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_right.png")
	ImageResource icons_fillings_arrow_big_right();

	@Source("org/geogebra/common/icons_fillings/p18/filling_fastforward.png")
	ImageResource icons_fillings_fastforward();

	@Source("org/geogebra/common/icons_fillings/p18/filling_rewind.png")
	ImageResource icons_fillings_rewind();

	@Source("org/geogebra/common/icons_fillings/p18/filling_skipback.png")
	ImageResource icons_fillings_skipback();

	@Source("org/geogebra/common/icons_fillings/p18/filling_skipforward.png")
	ImageResource icons_fillings_skipforward();

	@Source("org/geogebra/common/icons_fillings/p18/filling_play.png")
	ImageResource icons_fillings_play();

	@Source("org/geogebra/common/icons_fillings/p18/filling_pause.png")
	ImageResource icons_fillings_pause();

	@Source("org/geogebra/common/icons_fillings/p18/filling_cancel.png")
	ImageResource icons_fillings_cancel();

	// DATA COLLECTION
	@Source("org/geogebra/web/web/gui/images/datacollection_on.png")
	ImageResource datacollection_on();
	
	@Source("org/geogebra/web/web/gui/images/datacollection_off.png")
	ImageResource datacollection_off();

	@Source("org/geogebra/web/web/gui/images/collapse24.png")
	ImageResource collapse();

	@Source("org/geogebra/web/web/gui/images/expand24.png")
	ImageResource expand();
}
