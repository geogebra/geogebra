package geogebra.html5.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResources extends ClientBundle {
	
	GuiResources INSTANCE = GWT.create(GuiResources.class);
	
	//@Source("geogebra/resources/images/ggb4-splash-h120.png")
	//ImageResource getGeoGebraWebSplash();
	
	@Source("geogebra/resources/images/spinner.gif")
	ImageResource getGeoGebraWebSpinner();
	
	@Source("geogebra/resources/images/nav_play.png")
	ImageResource navPlay();
	
	@Source("geogebra/resources/images/nav_pause.png")
	ImageResource navPause();
	
	@Source("geogebra/resources/images/view-refresh.png")
	ImageResource viewRefresh();
	
	@Source("geogebra/resources/images/spacer.png")
	ImageResource spacer();
	
	@Source("icons/png/web/algebra-view-tree-open.png")
	ImageResource algebra_tree_open();
	
	@Source("icons/png/web/algebra-view-tree-closed.png")
	ImageResource algebra_tree_closed();
	
	/*@Source("geogebra/resources/images/triangle-down.png")
	ImageResource triangle_down();*/

	@Source("geogebra/resources/images/splash-ggb4.svg")
	TextResource ggb4Splash();
	
	@Source("geogebra/resources/js/zipjs/dataview.js")
	TextResource dataViewJs();
	
	@Source("geogebra/resources/js/zipjs/zip-3.js")
	TextResource zipJs();
	
	@Source("geogebra/pub/js/zipjs/deflate.js")
	TextResource deflateJs();

	@Source("geogebra/pub/js/zipjs/inflate.js")
	TextResource inflateJs();
	
	@Source("geogebra/resources/js/zipjs/base64.js")
	TextResource base64Js();
	
	@Source("geogebra/resources/js/zipjs/arraybuffer.js")
	TextResource arrayBufferJs();

	
	@Source("geogebra/resources/css/mathquillggb.css")
	TextResource mathquillggbCss();
	
	@Source("geogebra/common/main/xml/default-preferences.xml")
	TextResource preferencesXML();
	
	@Source("geogebra/resources/js/jquery-1.7.2.min.js")
	TextResource jQueryJs();

	@Source("geogebra/resources/js/mathquillggb.js")
	TextResource mathquillggbJs();

	@Source("geogebra/pub/js/properties_keys_en.js")
	TextResource propertiesKeysJS();
	
	@Source("geogebra/resources/images/spinner.html")
	TextResource ggbSpinnerHtml();
	
	@Source("geogebra/resources/images/ggbSplash.html")
	TextResource ggbSplashHtml();
	
	@Source("geogebra/resources/css/web-styles.css")
	TextResource modernStyle();
	
	
	
	// TOOLBAR
	// Little triangles for tools with submenu
	@Source("icons/png/web/toolbar-further-tools-icon.png")
	ImageResource toolbar_further_tools();
	
	//REDO UNDO
	@Source("icons/png/web/menu_edit_undo.png")
	ImageResource button_undo();
	
	@Source("icons/png/web/menu_edit_redo.png")
	ImageResource button_redo();
	
	//SMART TOOLBAR: Open menu and open search
	@Source("icons/png/web/menu-button-open-search.png")
	ImageResource button_open_search();
	
	@Source("icons/png/web/menu-button-open-menu.png")
	ImageResource button_open_menu();
	
	
	
	//SMART MENUBAR
	
	@Source("icons/png/web/menuBarSubMenuIconRTL.png")
	ImageResource menuBarSubMenuIconRTL();
	
	@Source("icons/png/web/menuBarSubMenuIconLTR.png")
	ImageResource menuBarSubMenuIconLTR();
	
	//Icons
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
	
	@Source("icons/png/web/menu_icons/menu-perspectives-algebra.png")
	ImageResource menu_icon_perspectives_algebra();
	
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
	
	@Source("icons/png/web/menu_icons/menu-perspectives-basic-geometry.png")
	ImageResource menu_icon_perspectives_basic_geometry();
	
	@Source("icons/png/web/menu_icons/menu-perspectives-geometry.png")
	ImageResource menu_icon_perspectives_geometry();
	
	@Source("icons/png/web/menu_icons/menu-perspectives-spreadsheet.png")
	ImageResource menu_icon_perspectives_spreadsheet();
	
	@Source("icons/png/web/menu_icons/menu-perspectives-cas.png")
	ImageResource menu_icon_perspectives_cas();
	
	@Source("icons/png/web/menu_icons/menu-help.png")
	ImageResource menu_icon_help();
	
	@Source("icons/png/web/menu_icons/menu-help-about.png")
	ImageResource menu_icon_help_about();
	
	
	//SUBMENUS
	@Source("icons/png/web/arrow-submenu-right.png")
	ImageResource arrow_submenu_right();
	
	@Source("icons/png/web/arrow-submenu-up.png")
	ImageResource arrow_submenu_up();
	
	
	
	
	//ALGEBRA INPUT
	@Source("icons/png/web/dockbar_triangle_left.png")
	ImageResource input_help_left();
	
	@Source("icons/png/web/dockbar_triangle_up.png")
	ImageResource input_help_up();
	
	
	
	//DIALOGS
	@Source("icons/png/web/triangle_right.png")
	ImageResource triangle_right();
	
	@Source("icons/png/web/triangle_down.png")
	ImageResource triangle_down();
	
	//INFO, WARNING, QUESTION, ERROR
	@Source("icons/png/web/dialog-error.png")
	ImageResource dialog_error();
	
	@Source("icons/png/web/dialog-info.png")
	ImageResource dialog_info();
	
	@Source("icons/png/web/dialog-question.png")
	ImageResource dialog_question();
	
	@Source("icons/png/web/dialog-warning.png")
	ImageResource dialog_warning();
	
	
	
	//STYLEBAR
	@Source("icons/png/web/dockbar_triangle_left.png")
	ImageResource dockbar_triangle_left();
	
	@Source("icons/png/web/dockbar_triangle_right.png")
	ImageResource dockbar_triangle_right();
	
	@Source("icons/png/web/dockbar_drag.png")
	ImageResource dockbar_drag();
	
	@Source("icons/png/web/dockbar_close.png")
	ImageResource dockbar_close();
	
	
	//PROPABILITY CALCULATOR
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
	
	
	//OBJECT PROPERTIES
	@Source("icons/png/web/little-triangle-down.png")
	ImageResource little_triangle_down();
	
	@Source("icons/png/web/little-triangle-down-active.png")
	ImageResource little_triangle_down_active();
	
	@Source("icons/png/web/stylingbar/stylingbar_properties_object.png")
	ImageResource properties_object();
	
	@Source("icons/png/web/stylingbar/stylingbar_properties_graphics_view.png")
	ImageResource properties_graphics();
	
	@Source("icons/png/web/stylingbar/stylingbar_properties_graphics_view2.png")
	ImageResource properties_graphics2();
	
	//DECORATIONS
	//ANGLE
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
	
	//SEGMENT
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
	
	//AXES
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
}

