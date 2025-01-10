package org.geogebra.web.full.css;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.resources.client.Resource;

@Resource
public interface MaterialDesignResources extends ClientBundle, ResourceIconProvider {

	MaterialDesignResources INSTANCE = new MaterialDesignResourcesImpl();

	/* NEW MATERIAL DESIGN ICONS */

	@Source("org/geogebra/common/icons/svg/web/header/baseline-apps-24px.svg")
	SVGResource apps_black();

	@Source("org/geogebra/common/icons/svg/web/header/timer.svg")
	SVGResource timer();

	// settings panel
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/settings/ic_arrow_drop_up_black_24px.svg")
	SVGResource arrow_drop_up();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/settings/ic_arrow_drop_down_black_24px.svg")
	SVGResource arrow_drop_down();

	// dynamic stylebar
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/format_color_reset-24px.svg")
	SVGResource no_color();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/baseline-format_color_text-24px.svg")
	SVGResource text_color();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/color_black.svg")
	SVGResource color_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/border-all-variant.svg")
	SVGResource color_border();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_crop_black_24px.svg")
	SVGResource crop_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_opacity_black_24px.svg")
	SVGResource opacity_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_texture_black_24px.svg")
	SVGResource filling_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_format_size_black_24px.svg")
	SVGResource text_size_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/format-text.svg")
	SVGResource text();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_format_bold_black_24px.svg")
	SVGResource text_bold_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_format_italic_black_24px.svg")
	SVGResource text_italic_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/format_underlined-24px.svg")
	SVGResource text_underline_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/border_all-24px.svg")
	SVGResource border_all();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/border_clear-24px.svg")
	SVGResource border_clear();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/border_inner-24px.svg")
	SVGResource border_inner();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/border_outer-24px.svg")
	SVGResource border_outer();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/format_align_left-24px.svg")
	SVGResource horizontal_align_left();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/format_align_center-24px.svg")
	SVGResource horizontal_align_center();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/format_align_right-24px.svg")
	SVGResource horizontal_align_right();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/vertical_align_bottom-24px.svg")
	SVGResource vertical_align_bottom();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/vertical_align_middle-24px.svg")
	SVGResource vertical_align_middle();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/vertical_align_top-24px.svg")
	SVGResource vertical_align_top();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/table_heading_column.svg")
	SVGResource table_heading_column();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/table_heading_row.svg")
	SVGResource table_heading_row();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/chart_line.svg")
	SVGResource table_line_chart();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/chart_bar.svg")
	SVGResource table_bar_chart();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/chart_pie.svg")
	SVGResource table_pie_chart();

	// context menu icon resources
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/plane_black.svg")
	SVGResource plane_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_more_vert_black_24px.svg")
	SVGResource more_vert_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/rename_black.svg")
	SVGResource rename_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_content_cut_black_24px.svg")
	SVGResource cut_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/duplicate_black.svg")
	SVGResource duplicate_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_delete_black_24px.svg")
	SVGResource delete_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_label_outline_black_24px.svg")
	SVGResource label_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/angle_black.svg")
	SVGResource angle_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/pin_black.svg")
	SVGResource pin_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_lock_outline_black_24px.svg")
	SVGResource lock_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_lock_open_black_24px.svg")
	SVGResource lock_open_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/trace_black.png")
	ImageResource trace_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/record_to_spreadsheet_black.svg")
	SVGResource record_to_spreadsheet_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_home_black_24px.svg")
	SVGResource home_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/show_all_objects_black.svg")
	SVGResource show_all_objects_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/arrow_drop_right_black.svg")
	SVGResource arrow_drop_right_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/arrow_drop_left_black.svg")
	SVGResource arrow_drop_left_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_check_black_24px.svg")
	SVGResource check_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_check_white_24px.svg")
	SVGResource check_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/check_border.svg")
	SVGResource check_border();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_expand_more_black_24px.svg")
	SVGResource expand_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_expand_less_black_24px.svg")
	SVGResource collapse_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/format_subscript.svg")
	SVGResource format_subscript();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/format_superscript.svg")
	SVGResource format_superscript();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/format_list_bulleted-24px.svg")
	SVGResource bulletList();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/format_list_numbered-24px.svg")
	SVGResource numberedList();

	// LINES

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_line_dash_dot.svg")
	SVGResource line_dash_dot();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_line_dashed_long.svg")
	SVGResource line_dashed_long();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_line_dashed_short.svg")
	SVGResource line_dashed_short();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_line_dotted.svg")
	SVGResource line_dotted();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_line_solid.svg")
	SVGResource line_solid();

	// POINTS

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_filled.svg")
	SVGResource point_full();

	// this used to be the icon for point_full(), that has a new one now
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_full.svg")
	SVGResource point_no_outline();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_empty.svg")
	SVGResource point_empty();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_cross.svg")
	SVGResource point_cross();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_cross_diag.svg")
	SVGResource point_cross_diag();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_diamond_full.svg")
	SVGResource point_diamond();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_diamond_empty.svg")
	SVGResource point_diamond_empty();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_up.svg")
	SVGResource point_up();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_down.svg")
	SVGResource point_down();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_left.svg")
	SVGResource point_left();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/stylingbar_point_right.svg")
	SVGResource point_right();

	// PATTERNS

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/filling_cross_hatching.svg")
	SVGResource pattern_cross_hatching();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/filling_dots.svg")
	SVGResource pattern_dots();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/filling_filled.svg")
	SVGResource pattern_filled();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/filling_hatching.svg")
	SVGResource pattern_hatching();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/filling_honeycomb.svg")
	SVGResource pattern_honeycomb();

	// plus menu icon resources

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/wrong_input.svg")
	SVGResource wrong_input();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/baseline-clear-24px.svg")
	SVGResource clear();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/add.svg")
	SVGResource add_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/ic_format_quote_black_24px.svg")
	SVGResource icon_quote_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/ic_image_black_24px.svg")
	SVGResource insert_photo_black();

	// av icons
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/equal_sign_white.svg")
	SVGResource equal_sign_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/equal_sign.svg")
	SVGResource equal_sign_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/mode_symbolic_white.svg")
	SVGResource modeToggleSymbolic();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/fraction_white.svg")
	SVGResource fraction_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/engineering_notation_white.svg")
	SVGResource engineering_notation_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/speed_up_black.svg")
	SVGResource speed_up_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/speed_down_black.svg")
	SVGResource speed_down_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/baseline-timeline-24px.svg")
	SVGResource special_points();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/baseline-label-24px.svg")
	SVGResource label();

	// ev icons
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_settings_black_24px.svg")
	SVGResource gear();

	// @Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/gridSVG.svg")
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/point_capturing.svg")
	SVGResource snap_to_grid();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/settings.svg")
	SVGResource settings_border();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/undo.svg")
	SVGResource undo_border();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/redo.svg")
	SVGResource redo_border();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_refresh_black_24px.svg")
	SVGResource refresh_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/grid_black.svg")
	SVGResource grid_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/isometric.svg")
	SVGResource grid_isometric();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/polar.svg")
	SVGResource grid_polar();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/minor_gridlines.svg")
	SVGResource minor_gridlines();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/axes_black.svg")
	SVGResource axes_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/projection_orthographic_black.svg")
	SVGResource projection_orthographic();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/projection_perspective_black.svg")
	SVGResource projection_perspective();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/projection_glasses_black.svg")
	SVGResource projection_glasses();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/projection_oblique_black.svg")
	SVGResource projection_oblique();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/baseline-error-24px.svg")
	SVGResource exam_error();

	// Toolbar resources
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/ic_keyboard_arrow_down_black_24px.svg")
	SVGResource toolbar_close_portrait_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/ic_keyboard_arrow_left_black_24px.svg")
	SVGResource toolbar_close_landscape_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/baseline-menu-24px.svg")
	SVGResource toolbar_menu_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/av_tab_graphing_black.svg")
	SVGResource toolbar_algebra_graphing();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/tools_tab_black.svg")
	SVGResource toolbar_tools();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/baseline-table_chart-24px-black.svg")
	SVGResource toolbar_table_view_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/distribution.svg")
	SVGResource toolbar_distribution();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/spreadsheet.svg")
	SVGResource toolbar_spreadsheet();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/move_white.svg")
	SVGResource mode_move();
	
	// Notes Toolbox
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/toolbox/mouse_cursor.svg")
	SVGResource mouse_cursor();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/toolbox/tool_shapes.svg")
	SVGResource shapes();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/toolbox/add_box.svg")
	SVGResource apps();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/toolbox/text_fields.svg")
	SVGResource texts();

	// Burger Menu resources
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_content_copy_black_24px.svg")
	SVGResource copy_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_content_paste_black_24px.svg")
	SVGResource paste_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_mode_edit_black_24px.svg")
	SVGResource edit_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/scientific.svg")
	SVGResource scientific();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/mebis-logo.png")
	ImageResource mebis();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_search_black_24px.svg")
	SVGResource search_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_collections_black_24px.svg")
	SVGResource export_image_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_file_download_black_24px.svg")
	SVGResource file_download_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_print_black_24px.svg")
	SVGResource print_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_undo_black_24px.svg")
	SVGResource undo_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_redo_black_24px.svg")
	SVGResource redo_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_select_all_black_24px.svg")
	SVGResource select_all_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/geogebra.svg")
	SVGResource geogebra_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/geogebra_color.svg")
	SVGResource geogebra_color();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dialog/google_classroom.svg")
	SVGResource google_classroom();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_info_outline_black_24px.svg")
	SVGResource info_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_bug_report_black_24px.svg")
	SVGResource bug_report_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/multiple-worksheets.svg")
	SVGResource tutorial_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/book-logo.svg")
	SVGResource manual_black();

	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_tools.svg")
	SVGResource tools_black();

	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_tools_customize.svg")
	SVGResource tools_customize_black();

	@Source("org/geogebra/common/icons/svg/web/menu_icons/menu_tools_create.svg")
	SVGResource tools_create_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/signin_black.svg")
	SVGResource signin_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/signout_black.svg")
	SVGResource signout_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_person_black_24px.svg")
	SVGResource person_black();

	// MOW resources
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/arrow-alt-circle-down.svg")
	SVGResource download();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/file_plus.svg")
	SVGResource file_plus();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/file.svg")
	SVGResource file();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/ic_add_white_24px.svg")
	SVGResource add_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-folder_open-24px.svg")
	SVGResource mow_pdf_open_folder();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/rename_box_black_24px.svg")
	SVGResource mow_rename();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-people-24px.svg")
	SVGResource mow_card_shared();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-groups-24px.svg")
	SVGResource mow_card_multiuser();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-lock-24px.svg")
	SVGResource mow_card_private();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-public-24px.svg")
	SVGResource mow_card_public();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/openFileView/link_black_24dp.svg")
	SVGResource resource_card_shared();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/lightbulb_outline_black_24px.svg")
	SVGResource mow_lightbulb();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/drag_pad.svg")
	SVGResource move_canvas();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/add.svg")
	SVGResource newFileMenu();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_search_black_24px.svg")
	SVGResource openFileMenu();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_insert_drive_file_black_24px.svg")
	SVGResource fileMenu();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_file_download_black_24px.svg")
	SVGResource downloadMenu();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/show_chart-black-24px.svg")
	SVGResource statistics();

	// EUCLIDIAN STYLEBAR (CLASSIC)

	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylebar_empty.svg")
	SVGResource stylebar_empty();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/delete_large.svg")
	SVGResource delete_large();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/delete_medium.svg")
	SVGResource delete_medium();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/delete_small.svg")
	SVGResource delete_small();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/algebra_description.svg")
	SVGResource description();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_algebraview_sort_objects_by.png")
	ImageResource sortObjects();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/export.svg")
	SVGResource prob_calc_export();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/stylebar_angle_interval.svg")
	SVGResource stylingbar_angle_interval();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentStart/start_arrow.svg")
	SVGResource stylingbar_start_arrow();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentStart/start_arrow_filled.svg")
	SVGResource stylingbar_start_arrow_filled();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentStart/start_circle.svg")
	SVGResource stylingbar_start_circle();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentStart/start_circle_outlined.svg")
	SVGResource stylingbar_start_circle_outlined();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentStart/start_default.svg")
	SVGResource stylingbar_start_default();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentStart/start_line.svg")
	SVGResource stylingbar_start_line();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentStart/start_square.svg")
	SVGResource stylingbar_start_square();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentStart/start_square_outlined.svg")
	SVGResource stylingbar_start_square_outlined();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentEnd/end_arrow.svg")
	SVGResource stylingbar_end_arrow();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentEnd/end_arrow_filled.svg")
	SVGResource stylingbar_end_arrow_filled();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentEnd/end_circle.svg")
	SVGResource stylingbar_end_circle();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentEnd/end_circle_outlined.svg")
	SVGResource stylingbar_end_circle_outlined();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentEnd/end_default.svg")
	SVGResource stylingbar_end_default();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentEnd/end_line.svg")
	SVGResource stylingbar_end_line();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentEnd/end_square.svg")
	SVGResource stylingbar_end_square();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/segmentEnd/end_square_outlined.svg")
	SVGResource stylingbar_end_square_outlined();

	// DIALOG

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dialog/bar_chart_black.svg")
	SVGResource bar_chart_black();

	// EUCLIDIAN 3D STYLEBAR

	@Source("org/geogebra/common/icons/svg/web/stylingbar/3d/stylingbar_graphics3D_plane.svg")
	SVGResource plane();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/3d/stylingbar_graphics3D_axes_plane.svg")
	SVGResource axes_plane();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/3d/stylingbar_graphics3D_rotateview_play.svg")
	SVGResource rotateViewPlay();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/3d/stylingbar_graphics3D_rotateview_pause.svg")
	SVGResource rotateViewPause();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/3d/stylingbar_graphics3D_standardview_rotate.svg")
	SVGResource standardViewRotate();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/3d/stylingbar_graphics3D_view_xy.svg")
	SVGResource viewXY();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/3d/stylingbar_graphics3D_view_xz.svg")
	SVGResource viewXZ();

	@Source("org/geogebra/common/icons/svg/web/stylingbar/3d/stylingbar_graphics3D_view_yz.svg")
	SVGResource viewYZ();

	// ICONS USED IN DIALOG

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dialog/upload_black_24dp.svg")
	SVGResource upload();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dialog/no_photography_black_24dp.svg")
	SVGResource no_camera();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dialog/camera_white.svg")
	SVGResource camera_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/openFileView/folder_black_24dp.svg")
	SVGResource open_local_file();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/openFileView/drive_icon_24px.svg")
	SVGResource google_drive();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/openFileView/login_black_24dp.svg")
	SVGResource login();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/openFileView/visibility_black_24dp.svg")
	SVGResource visibility();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dataImport/upload_file_black_24dp.svg")
	SVGResource upload_file();
}