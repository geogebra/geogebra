package org.geogebra.web.full.css;

import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SassResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

@SuppressWarnings("javadoc")
public interface GuiResources extends ClientBundle, StylesProvider {

	GuiResources INSTANCE = GWT.create(GuiResources.class);

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

	@Source("org/geogebra/common/icons/png/web/dockbar_drag.png")
	ImageResource dockbar_drag();

	@Source("org/geogebra/common/menu_icons/p20/menu-view-close.png")
	ImageResource dockbar_close();

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

	@Source("org/geogebra/common/icons/png/web/probability_calculator_normal_overlay.png")
	ImageResource normal_overlay();

	// OBJECT PROPERTIES
	@Source("org/geogebra/common/icons/png/web/little-triangle-down.png")
	ImageResource little_triangle_down();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_properties_object.png")
	ImageResource properties_object();

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

	/*
	 * STYLINGBARS
	 * 
	 */

	@Source("org/geogebra/common/stylingbar/p24/stylebar_more.png")
	ImageResource stylebar_more();

	// PLAY, PAUSE, REWIND, ETC. USED IN ALGEBRA VIEW, EV AND IN NAVIGATION BAR

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

	@Source("org/geogebra/web/resources/css/fonts.css")
	TextResource fonts();

	@Source("org/geogebra/web/resources/scss/exam.scss")
	SassResource examStyle();

	@Override
	@Source("org/geogebra/web/resources/scss/mow.scss")
	SassResource mowStyle();

	@Override
	@Source("org/geogebra/web/resources/scss/mow-toolbar.scss")
	SassResource mowToolbarStyle();

	@Source("org/geogebra/web/resources/scss/web-styles.scss")
	SassResource modernStyle();

	@Source("org/geogebra/web/resources/scss/spreadsheet.scss")
	SassResource spreadsheetStyle();

	@Override
	@Source("org/geogebra/web/resources/scss/open-screen.scss")
	SassResource openScreenStyle();

	@Source("org/geogebra/web/resources/scss/av-styles.scss")
	SassResource avStyleScss();

	@Source("org/geogebra/web/resources/scss/general.scss")
	SassResource generalStyle();

	@Source("org/geogebra/web/resources/scss/headerbar.scss")
	SassResource headerbarStyle();

	@Source("org/geogebra/web/resources/scss/av.scss")
	SassResource avStyle();

	@Source("org/geogebra/web/resources/scss/toolbar-styles.scss")
	SassResource toolBarStyleScss();

	@Source("org/geogebra/web/resources/scss/tableview.scss")
	SassResource tableViewStyleScss();

	@Source("org/geogebra/web/pub/js/properties_keys_en.js")
	TextResource propertiesKeysJS();

	@Source("org/geogebra/web/resources/scss/menu-styles.scss")
	SassResource menuStyleScss();

	@Source("org/geogebra/web/resources/scss/popup-styles.scss")
	SassResource popupStyleScss();

	@Override
	@Source("org/geogebra/web/resources/scss/settings-styles.scss")
	SassResource settingsStyleScss();

	@Source("org/geogebra/web/resources/scss/perspectives-popup.scss")
	SassResource perspectivesPopupScss();

	@Source("org/geogebra/web/resources/scss/snackbar.scss")
	SassResource snackbarScss();

	@Source("org/geogebra/common/icons/png/web/arrow-submenu-up.png")
	ImageResource arrow_submenu_up();

	@Source("org/geogebra/web/resources/scss/print.scss")
	SassResource printStyle();

	@Override
	@Source("org/geogebra/web/resources/scss/component-styles.scss")
	SassResource componentStyles();

	@Source("org/geogebra/web/resources/scss/scientific-layout.scss")
	SassResource scientificLayoutScss();

	@Source("org/geogebra/web/resources/scss/header.scss")
	SassResource headerScss();

	@Override
	@Source("org/geogebra/web/resources/scss/dialog-styles.scss")
	SassResource dialogStylesScss();

	@Source("org/geogebra/web/resources/scss/evaluator-styles.scss")
	SassResource evaluatorScss();

}
