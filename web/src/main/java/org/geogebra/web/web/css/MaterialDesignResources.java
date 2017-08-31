package org.geogebra.web.web.css;

import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

@SuppressWarnings("javadoc")
public interface MaterialDesignResources extends ClientBundle {

	MaterialDesignResources INSTANCE = GWT.create(MaterialDesignResources.class);
	/** NEW MATERIAL DESIGN ICONS */

	// dynamic stylebar
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/color_black.svg")
	SVGResource color_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/dynStylebar/label_settings.png")
	ImageResource label_settings();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_format_size_black_24px.svg")
	SVGResource text_size_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_format_bold_black_24px.svg")
	SVGResource text_bold_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_format_italic_black_24px.svg")
	SVGResource text_italic_black();

	// context menu icon resources
	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/more_vert_black.png")
	ImageResource more_vert_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/more_vert_purple.png")
	ImageResource more_vert_purple();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/rename_black.png")
	ImageResource rename_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/cut_black.png")
	ImageResource cut_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/duplicate_black.png")
	ImageResource duplicate_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/delete_black.png")
	ImageResource delete_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/label_black.png")
	ImageResource label_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/angle_black.png")
	ImageResource angle_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/pin_black.png")
	ImageResource pin_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/unpin_black.png")
	ImageResource unpin_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/lock_black.png")
	ImageResource lock_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/unlock_black.png")
	ImageResource unlock_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/trace_black.png")
	ImageResource trace_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/trace_off_black.png")
	ImageResource trace_off_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/play_black.png")
	ImageResource play_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/play_purple.png")
	ImageResource play_purple();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/record_to_spreadsheet_black.png")
	ImageResource record_to_spreadsheet_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/settings_black.png")
	ImageResource settings_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/zoom_in_black.png")
	ImageResource zoom_in_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/home_black.png")
	ImageResource home_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/show_all_objects_black.png")
	ImageResource show_all_objects_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/arrow_drop_right_black.png")
	ImageResource arrow_drop_right_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/arrow_drop_left_black.png")
	ImageResource arrow_drop_left_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/check_black.png")
	ImageResource check_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/expand_black.png")
	ImageResource expand_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/collapse_black.png")
	ImageResource collapse_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/sort_black.png")
	ImageResource sort_black();

	// @Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/content_copy_black.png")
	// ImageResource copy_content_black();

	// plus menu icon resources

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/ic_add_black_24px.svg")
	SVGResource add_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/add_purple.svg")
	SVGResource add_purple();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/ic_help_outline_black_24px.svg")
	SVGResource icon_help_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/ic_format_quote_black_24px.svg")
	SVGResource icon_quote_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/ic_image_black_24px.svg")
	SVGResource insert_photo_black();

	// av icons
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/arrow_black.svg")
	SVGResource arrow_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/av/mode_toggle_numeric_white.png")
	ImageResource modeToggleNumeric();

	@Source("org/geogebra/common/icons/png/matDesignIcons/av/mode_toggle_symbolic_white.png")
	ImageResource modeToggleSymbolic();

	@Source("org/geogebra/common/icons/png/matDesignIcons/av/speed_up_black.png")
	ImageResource speed_up_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/av/speed_down_black.png")
	ImageResource speed_down_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/ic_pause_circle_outline_black_24px.svg")
	SVGResource pause_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/pause_purple.svg")
	SVGResource pause_purple();

	// ev icons
	@Source("org/geogebra/common/icons/png/matDesignIcons/ev/more_border.png")
	ImageResource more_border();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/menu.svg")
	SVGResource menu_black_border();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_settings_black_24px.svg")
	SVGResource gere();

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

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/axes_black.svg")
	SVGResource axes_black();

	// Toolbar resoures

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/ic_more_vert_white_24px.svg")
	SVGResource more_vert_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/ic_keyboard_arrow_up_white_24px.svg")
	SVGResource toolbar_open_portrait_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/ic_keyboard_arrow_down_white_24px.svg")
	SVGResource toolbar_close_portrait_white();

	@Source("org/geogebra/common/icons/png/matDesignIcons/toolBar/arrow_up_black.png")
	ImageResource toolbar_open_portrait_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/toolBar/arrow_down_black.png")
	ImageResource toolbar_close_portrait_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/ic_keyboard_arrow_right_white_24px.svg")
	SVGResource toolbar_open_landscape_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/ic_keyboard_arrow_left_white_24px.svg")
	SVGResource toolbar_close_landscape_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/ic_menu_white_24px.svg")
	SVGResource toolbar_menu_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/menu.svg")
	SVGResource toolbar_menu_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/toolBar/av_tab_graphing_white.png")
	ImageResource toolbar_algebra_graphing();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/av_tab_geometry_white.svg")
	SVGResource toolbar_algebra_geometry();

	@Source("org/geogebra/common/icons/png/matDesignIcons/toolBar/tools_tab_white.png")
	ImageResource toolbar_tools();

	@Source("org/geogebra/common/icons/png/matDesignIcons/toolBar/filter_list_black.png")
	ImageResource filter_list_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/toolBar/move_white.svg")
	SVGResource mode_move();

	// Burger Menu resources
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_content_copy_black_24px.svg")
	SVGResource copy_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_content_paste_black_24px.svg")
	SVGResource paste_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_mode_edit_black_24px.svg")
	SVGResource edit_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/graphing.svg")
	SVGResource graphing();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/geometry.svg")
	SVGResource geometry();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/whiteboard.png")
	ImageResource whiteboard();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/geogebra_logo_transparent.png")
	ImageResource geogebra_logo_transparent();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_insert_drive_file_black_24px.svg")
	SVGResource insert_file_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_search_black_24px.svg")
	SVGResource search_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_save_black_24px.svg")
	SVGResource save_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_share_black_24px.svg")
	SVGResource share_black();

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

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_language_black_24px.svg")
	SVGResource language_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/font_size_black.png")
	ImageResource font_size_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_forum_black_24px.svg")
	SVGResource forum_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_info_outline_black_24px.svg")
	SVGResource info_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_bug_report_black_24px.svg")
	SVGResource bug_report_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/multiple-worksheets.svg")
	SVGResource tutorial_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/book-logo.svg")
	SVGResource manual_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/tools_black.png")
	ImageResource tools_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/tools_customize_black.png")
	ImageResource tools_customize_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/tools_create_black.png")
	ImageResource tools_create_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/new_exercise_black.png")
	ImageResource new_exercise_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/signin_black.png")
	ImageResource signin_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/signout_black.png")
	ImageResource signout_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/burgerMenu/person_black.png")
	ImageResource person_black();
}
