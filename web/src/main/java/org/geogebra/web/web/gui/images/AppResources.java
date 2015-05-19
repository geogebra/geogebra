package org.geogebra.web.web.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;

public interface AppResources extends ClientBundleWithLookup {
	
	AppResources INSTANCE = GWT.create(AppResources.class);
//	public static String iconString = "empty.gif";
//	
//	@Source("org/geogebra/web/web/gui/images/" + iconString)
//	ImageResource icon();
//	
	
	//@Source("org/geogebra/web/web/gui/images/view-properties16.png")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options.png")
	ImageResource view_properties16();
	
	@Source("org/geogebra/web/web/gui/images/options-layout24.png")
	ImageResource options_layout24();
	
	//@Source("org/geogebra/web/web/gui/images/pin.png")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-pin.png")
	ImageResource pin();
	
	@Source("org/geogebra/web/web/gui/images/geogebra32.png")
	ImageResource GeoGebraTube();

	@Source("org/geogebra/web/web/gui/images/applications-graphics.png")
	ImageResource application_graphics();
	
	@Source("org/geogebra/web/web/gui/images/apply.png")
	ImageResource apply();
	
	@Source("org/geogebra/web/web/gui/images/arrow-in.png")
	ImageResource arrow_in();
	
	@Source("org/geogebra/web/web/gui/images/arrow-out.png")
	ImageResource arrow_out();
	
	@Source("org/geogebra/web/web/gui/images/aux_folder.gif")
	ImageResource aux_folder();
	
	/*@Source("org/geogebra/web/web/gui/images/auxiliary.png")
	ImageResource auxiliary();*/
	
	/*@Source("org/geogebra/web/web/gui/images/stylingbar_graphicsview_show_or_hide_the_axes.png")
	ImageResource axes();*/
	
	@Source("org/geogebra/web/web/gui/images/border_all.png")
	ImageResource border_all();
	
	@Source("org/geogebra/web/web/gui/images/border_bottom.png")
	ImageResource border_bottom();
	
	@Source("org/geogebra/web/web/gui/images/border_frame.png")
	ImageResource border_frame();
	
	@Source("org/geogebra/web/web/gui/images/border_inside.png")
	ImageResource border_inside();
	
	@Source("org/geogebra/web/web/gui/images/border_left.png")
	ImageResource border_left();
	
	@Source("org/geogebra/web/web/gui/images/border_none.png")
	ImageResource border_none();
	
	@Source("org/geogebra/web/web/gui/images/border_right.png")
	ImageResource border_right();
	
	@Source("org/geogebra/web/web/gui/images/border_top.png")
	ImageResource border_top();
	
	@Source("org/geogebra/web/web/gui/images/cas.png")
	ImageResource cas();
	
	@Source("org/geogebra/web/web/gui/images/checkbox16.gif")
	ImageResource checkbox16();
	
	@Source("org/geogebra/web/web/gui/images/color_chooser_check.png")
	ImageResource color_chooser_check();
	
	@Source("org/geogebra/web/web/gui/images/configure-24.png")
	ImageResource configure_24();
	
	@Source("org/geogebra/web/web/gui/images/configure-32.png")
	ImageResource configure_32();
	
	@Source("org/geogebra/web/web/gui/images/corner1.png")
	ImageResource corner1();
	
	@Source("org/geogebra/web/web/gui/images/corner2.png")
	ImageResource corner2();
	
	@Source("org/geogebra/web/web/gui/images/corner4.png")
	ImageResource corner4();
	
	@Source("org/geogebra/common/icons/png16x16/cumulative_distribution.png")
	ImageResource cumulative_distribution();
	
	@Source("org/geogebra/web/web/gui/images/cursor_arrow.png")
	ImageResource cursor_arrow();
	
	@Source("org/geogebra/web/web/gui/images/cursor_grab.gif")
	ImageResource cursor_grab();
	
	@Source("org/geogebra/web/web/gui/images/cursor_grabbing.gif")
	ImageResource cursor_grabbing();
	
	@Source("org/geogebra/web/web/gui/images/cursor_large_cross.gif")
	ImageResource cursor_large_cross();
	
	@Source("org/geogebra/web/web/gui/images/cursor_zoomin.gif")
	ImageResource cursor_zoomin();
	
	@Source("org/geogebra/web/web/gui/images/cursor_zoomout.gif")
	ImageResource cursor_zoomout();
	
	//@Source("org/geogebra/web/web/gui/images/delete_small.gif")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-delete.png")
	ImageResource delete_small();
	
	/*@Source("org/geogebra/web/web/gui/images/document-new.png")
	ImageResource document_new();*/
	
	/*@Source("org/geogebra/web/web/gui/images/document-open.png")
	ImageResource document_open();*/
	
	@Source("org/geogebra/web/web/gui/images/document-print-preview.png")
	ImageResource document_print_preview();
	
	@Source("org/geogebra/web/web/gui/images/document-print.png")
	ImageResource document_print();
	
	@Source("org/geogebra/web/web/gui/images/document-properties.png")
	ImageResource document_properties();
	
	/*@Source("org/geogebra/web/web/gui/images/document-save.png")
	ImageResource document_save();*/
	
	@Source("org/geogebra/web/web/gui/images/edit-clear.png")
	ImageResource edit_clear();
	
	//@Source("org/geogebra/web/web/gui/images/edit-copy.png")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-copy.png")
	ImageResource edit_copy();
	
	//@Source("org/geogebra/web/web/gui/images/edit-cut.png")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-cut.png")
	ImageResource edit_cut();
	
	//@Source("org/geogebra/web/web/gui/images/edit-paste.png")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-paste.png")
	ImageResource edit_paste();
	
	/*@Source("org/geogebra/web/web/gui/images/edit-redo.png")
	ImageResource edit_redo();
	
	@Source("org/geogebra/web/web/gui/images/edit-undo.png")
	ImageResource edit_undo();*/
	
	//@Source("org/geogebra/web/web/gui/images/edit.png")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit.png")
	ImageResource edit();
	
	@Source("org/geogebra/web/web/gui/images/empty.gif")
	ImageResource empty();
	
	@Source("org/geogebra/web/web/gui/images/euclidian.png")
	ImageResource euclidian();
	
	@Source("org/geogebra/web/web/gui/images/exit.png")
	ImageResource exit();
	
	/*@Source("org/geogebra/web/web/gui/images/export_small.png")
	ImageResource export_small();*/
	
	@Source("org/geogebra/web/web/gui/images/export-html.png")
	ImageResource export_html();
	
	@Source("org/geogebra/web/web/gui/images/export.png")
	ImageResource export();
	
	@Source("org/geogebra/web/web/gui/images/folder.png")
	ImageResource folder();
	
	@Source("org/geogebra/web/web/gui/images/font.png")
	ImageResource font();
	
	@Source("org/geogebra/web/web/gui/images/format-justify-center.png")
	ImageResource format_justify_center();
	
	@Source("org/geogebra/web/web/gui/images/format-justify-left.png")
	ImageResource format_justify_left();
	
	@Source("org/geogebra/web/web/gui/images/format-justify-right.png")
	ImageResource format_justify_right();
	
	@Source("org/geogebra/web/web/gui/images/format-text-bold.png")
	ImageResource format_text_bold();
	
	@Source("org/geogebra/web/web/gui/images/format-text-italic.png")
	ImageResource  format_text_italic();
	
	@Source("org/geogebra/web/web/gui/images/formula_bar.png")
	ImageResource formula_bar();
	
	@Source("org/geogebra/web/web/gui/images/geogebra.png")
	ImageResource geogebra();
	
	@Source("org/geogebra/web/web/gui/images/geogebra32.png")
	ImageResource geogebra32();
	
	@Source("org/geogebra/web/web/gui/images/geogebra64.png")
	ImageResource geogebra64();
	
	@Source("org/geogebra/web/web/gui/images/globe.png")
	ImageResource globe();
	
	@Source("org/geogebra/web/web/gui/images/go-down.png")
	ImageResource go_down();
	
	@Source("org/geogebra/web/web/gui/images/go-last-gray.png")
	ImageResource go_last_gray();
	
	@Source("org/geogebra/web/web/gui/images/go-next-gray.png")
	ImageResource go_next_gray();
	
	@Source("org/geogebra/web/web/gui/images/go-next.png")
	ImageResource go_next();
	
	@Source("org/geogebra/web/web/gui/images/go-previous-gray.png")
	ImageResource go_previous_gray();
	
	@Source("org/geogebra/web/web/gui/images/go-previous.png")
	ImageResource go_previous();
	
	@Source("org/geogebra/web/web/gui/images/go-previous24.png")
	ImageResource go_previous24();
	
	@Source("org/geogebra/web/web/gui/images/go-up.png")
	ImageResource go_up();
	
	/*@Source("org/geogebra/web/web/gui/images/stylingbar_graphicsview_show_or_hide_the_grid.png")
	ImageResource grid();*/
	
	@Source("org/geogebra/web/web/gui/images/header_column.png")
	ImageResource header_column();
	
	@Source("org/geogebra/web/web/gui/images/header_row.png")
	ImageResource header_row();
	
	@Source("org/geogebra/web/web/gui/images/help.png")
	ImageResource help();
	
	@Source("org/geogebra/web/web/gui/images/algebra_hidden.png")
	ImageResource hidden();
	
	@Source("org/geogebra/web/web/gui/images/image-x-generic.png")
	ImageResource image_x_generic();
	
	@Source("org/geogebra/web/web/gui/images/info.gif")
	ImageResource info();
	
	@Source("org/geogebra/web/web/gui/images/inputhelp_left_16x16.png")
	ImageResource inputhelp_left_16x16();
	
	@Source("org/geogebra/web/web/gui/images/inputhelp_left_18x18.png")
	ImageResource inputhelp_left_18x18();
	
	@Source("org/geogebra/web/web/gui/images/inputhelp_left_20x20.png")
	ImageResource inputhelp_left_20x20();
	
	@Source("org/geogebra/web/web/gui/images/inputhelp_right_16x16.png")
	ImageResource inputhelp_right_16x16();
	
	@Source("org/geogebra/web/web/gui/images/inputhelp_right_18x18.png")
	ImageResource inputhelp_right_18x18();
	
	@Source("org/geogebra/web/web/gui/images/inputhelp_right_20x20.png")
	ImageResource inputhelp_right_20x20();
	
	@Source("org/geogebra/web/web/gui/images/keyboard.png")
	ImageResource keyboard();
	
	@Source("org/geogebra/web/web/gui/images/line_graph.png")
	ImageResource line_graph();
	
	@Source("org/geogebra/web/web/gui/images/list-add.png")
	ImageResource list_add();
	
	@Source("org/geogebra/web/web/gui/images/object_fixed.png")
	ImageResource lock();
	
	@Source("org/geogebra/web/web/gui/images/object_unfixed.png")
	ImageResource unlock();

	/*@Source("org/geogebra/web/web/gui/images/stylingbar_graphicsview_point_capturing.png")
	ImageResource magnet();*/
	
	@Source("org/geogebra/web/web/gui/images/magnet2.gif")
	ImageResource magnet2();
	
	@Source("org/geogebra/web/web/gui/images/mode_angle_16.gif")
	ImageResource mode_angle_16();
	
	//@Source("org/geogebra/web/web/gui/images/mode_copyvisualstyle_16.png")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-copy-visual-style.png")
	ImageResource mode_copyvisualstyle_16();
	
	@Source("org/geogebra/web/web/gui/images/mode_point_16.gif")
	ImageResource mode_point_16();
	
	//@Source("org/geogebra/web/web/gui/images/mode_showhidelabel_16.gif")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options-labeling.png")
	ImageResource mode_showhidelabel_16();
	
	//@Source("org/geogebra/web/web/gui/images/mode_showhideobject_16.gif")
	@Source("org/geogebra/common/icons/png/web/menu_icons/mode_showhideobject.png")
	ImageResource mode_showhideobject_16();
	
	@Source("org/geogebra/web/web/gui/images/nav_fastforward.png")
	ImageResource nav_fastforward();
	
	@Source("org/geogebra/web/web/gui/images/nav_rewind.png")
	ImageResource nav_rewind();
	
	@Source("org/geogebra/web/web/gui/images/nav_skipback.png")
	ImageResource nav_skipback();
	
	@Source("org/geogebra/web/web/gui/images/nav_skipforward.png")
	ImageResource nav_skipforward();
	
	@Source("org/geogebra/web/web/gui/images/nav_play.png")
	ImageResource nav_play();

	@Source("org/geogebra/web/web/gui/images/nav_pause.png")
	ImageResource nav_pause();

	/*@Source("org/geogebra/web/web/gui/images/dialog-error-32px.png")
	ImageResource dialog_error();
	
	@Source("org/geogebra/web/web/gui/images/dialog-info-32px.png")
	ImageResource dialog_info();
	
	@Source("org/geogebra/web/web/gui/images/dialog-question-32px.png")
	ImageResource dialog_question();
	
	@Source("org/geogebra/web/web/gui/images/dialog-warning-32px.png")
	ImageResource dialog_warning();*/
	
	@Source("org/geogebra/web/web/gui/images/options_btn.png")
	ImageResource options_btn();
	
	@Source("org/geogebra/web/web/gui/images/options-advanced.png")
	ImageResource options_advanced();
	
	@Source("org/geogebra/web/web/gui/images/options-advanced24.png")
	ImageResource options_advanced24();
	
	@Source("org/geogebra/web/web/gui/images/options-defaults224.png")
	ImageResource options_defaults224();
	
	@Source("org/geogebra/web/web/gui/images/options-large.png")
	ImageResource options_large();
	
	/*@Source("org/geogebra/web/web/gui/images/options-objects24.png")
	ImageResource options_objects24();*/
	
	@Source("org/geogebra/web/web/gui/images/osculating_circle.png")
	ImageResource osculating_circle();
	
	@Source("org/geogebra/web/web/gui/images/paste.gif")
	ImageResource paste();
	
	@Source("org/geogebra/web/web/gui/images/perspective.gif")
	ImageResource perspective();
	
	//@Source("org/geogebra/web/web/gui/images/rename.png")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-rename.png")
	ImageResource rename();
	
	@Source("org/geogebra/web/web/gui/images/right_angle.gif")
	ImageResource right_angle();
	
	@Source("org/geogebra/web/web/gui/images/separator.gif")
	ImageResource separator();
	
	@Source("org/geogebra/web/web/gui/images/algebra_shown.png")
	ImageResource shown();
	
	@Source("org/geogebra/web/web/gui/images/spreadsheet_grid.png")
	ImageResource spreadsheet_grid();
	
	@Source("org/geogebra/web/web/gui/images/spreadsheet.png")
	ImageResource spreadsheet();
	
	@Source("org/geogebra/web/web/gui/images/spreadsheettrace_button.gif")
	ImageResource spreadsheettrace_button();
	
	@Source("org/geogebra/web/web/gui/images/spreadsheettrace_hover.gif")
	ImageResource spreadsheettrace_hover();
	
	//@Source("org/geogebra/web/web/gui/images/spreadsheettrace.gif")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-record-to-spreadsheet.png")
	ImageResource spreadsheettrace();
	
	@Source("org/geogebra/web/web/gui/images/table.gif")
	ImageResource table();
	
	@Source("org/geogebra/web/web/gui/images/tangent_line.png")
	ImageResource tangent_line();
	
	@Source("org/geogebra/web/web/gui/images/text-editor.png")
	ImageResource text_editor();
	
	@Source("org/geogebra/web/web/gui/images/text-html.png")
	ImageResource text_html();
	
	@Source("org/geogebra/web/web/gui/images/text-x-generic.png")
	ImageResource text_x_generic();
	
	@Source("org/geogebra/web/web/gui/images/tool.png")
	ImageResource tool();
	
	//@Source("org/geogebra/web/web/gui/images/trace_on.gif")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-trace-on.png")
	ImageResource trace_on();
	
	@Source("org/geogebra/web/web/gui/images/tree-close.png")
	ImageResource tree_close();
	
	@Source("org/geogebra/web/web/gui/images/tree-open.png")
	ImageResource tree_open();
	
	@Source("org/geogebra/web/web/gui/images/tree.png")
	ImageResource tree();
	
	@Source("org/geogebra/web/web/gui/images/arrow_dockbar_triangle_down.png")
	ImageResource triangle_down();
	
	@Source("org/geogebra/web/web/gui/images/arrow_dockbar_triangle_up.png")
	ImageResource triangle_up();
	
	/*@Source("org/geogebra/web/web/gui/images/arrow_dockbar_triangle_right.png")
	ImageResource triangle_right();*/
	
	@Source("org/geogebra/web/web/gui/images/users.png")
	ImageResource users();
	
	@Source("org/geogebra/web/web/gui/images/view_btn.png")
	ImageResource view_btn();
	
	
	
	@Source("org/geogebra/web/web/gui/images/view-maximize.png")
	ImageResource view_maximize();
	
	@Source("org/geogebra/web/web/gui/images/view-move.png")
	ImageResource view_move();
	
	@Source("org/geogebra/web/web/gui/images/view-refresh.png")
	ImageResource view_refresh();
	

	
	@Source("org/geogebra/web/web/gui/images/view-unmaximize.png")
	ImageResource view_unmaximize();
	
	@Source("org/geogebra/web/web/gui/images/view-unwindow.png")
	ImageResource view_unwindow();
	
	@Source("org/geogebra/web/web/gui/images/view-window.png")
	ImageResource view_window();
	
	@Source("org/geogebra/web/web/gui/images/wiki.png")
	ImageResource wiki();
	
	@Source("org/geogebra/web/web/gui/images/xy_segments.png")
	ImageResource xy_segments();
	
	@Source("org/geogebra/web/web/gui/images/xy_table.png")
	ImageResource xy_table();
	
	//@Source("org/geogebra/web/web/gui/images/zoom16.gif")
	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-file-open.png")
	ImageResource zoom16();
	
	/*@Source("org/geogebra/web/web/gui/images/drive_icon_16.png")
	ImageResource drive_icon_16();
	
	@Source("org/geogebra/web/web/gui/images/skydrive_icon_16.png")
	ImageResource skydrive_icon_16();
	
	@Source("org/geogebra/web/web/gui/images/social-facebook.png")
	ImageResource social_facebook();
	
	@Source("org/geogebra/web/web/gui/images/social-google.png")
	ImageResource social_google();
	
	@Source("org/geogebra/web/web/gui/images/social-openid.png")
	ImageResource social_openid();
	
	@Source("org/geogebra/web/web/gui/images/social-twitter.png")
	ImageResource social_twitter();*/

	/*@Source("org/geogebra/web/web/gui/images/stylingbar_graphicsview_standardview.png")
	ImageResource standard_view();*/
	
	// now in GUI Resources
	/*@Source("org/geogebra/web/web/gui/images/menuBarSubMenuIconLTR.gif")
	ImageResource menuBarSubMenuIconLTR();

	@Source("org/geogebra/web/web/gui/images/menuBarSubMenuIconRTL.gif")
	ImageResource menuBarSubMenuIconRTL();*/

	/*
	 * @Source("org/geogebra/common/icons/png30x30/stylingbar_point-full.png")
	 * ImageResource point_full();
	 * 
	 * @Source("org/geogebra/common/icons/png30x30/stylingbar_point-empty.png")
	 * ImageResource point_empty();
	 */

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-cross.png")
	ImageResource point_cross();

	/*
	 * @Source("org/geogebra/common/icons/png30x30/stylingbar_point-cross-diag.png"
	 * ) ImageResource point_cross_diag();
	 * 
	 * @Source(
	 * "org/geogebra/common/icons/png30x30/stylingbar_point-diamond-full.png")
	 * ImageResource point_diamond();
	 * 
	 * @Source(
	 * "org/geogebra/common/icons/png30x30/stylingbar_point-diamond-empty.png")
	 * ImageResource point_diamond_empty();
	 * 
	 * @Source("org/geogebra/common/icons/png30x30/stylingbar_point-up.png")
	 * ImageResource point_up();
	 */
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-down.png")
	ImageResource point_down();
	
	/*
	 * @Source("org/geogebra/common/icons/png30x30/stylingbar_point-left.png")
	 * ImageResource point_left();
	 */
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-right.png")
	ImageResource point_right();

	/*@Source("org/geogebra/common/icons/png16x16/interval-left.png")
	ImageResource interval_left();
	
	@Source("org/geogebra/common/icons/png16x16/interval-right.png")
	ImageResource interval_right();
	
	@Source("org/geogebra/common/icons/png16x16/interval-between.png")
	ImageResource interval_between();*/

	@Source("org/geogebra/common/icons/png16x16/step_graph.png")
	ImageResource step_graph();
	
	@Source("org/geogebra/common/icons/png16x16/bar_graph.png")
	ImageResource bar_graph();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_dots.png")
	ImageResource dots();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_dots_active.png")
	ImageResource dots_active();

	/*@Source("org/geogebra/common/icons/png16x16/export16.png")
	ImageResource export16();*/
	
	/*@Source("org/geogebra/common/icons/png16x16/normal-overlay.png")
	ImageResource normal_overlay();*/
	

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_statistics.png")
	ImageResource dataview_showstatistics();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_data.png")
	ImageResource dataview_showdata();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_2nd_plot.png")
	ImageResource dataview_showplot2();


	/*
	//@Source("org/geogebra/common/icons/png30x30/stylingbar_delete_small.png")
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_delete_small.png")
	ImageResource eraser_small();

	//@Source("org/geogebra/common/icons/png30x30/stylingbar_delete_medium.png")
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_delete_medium.png")
	ImageResource eraser_medium();
	
	//@Source("org/geogebra/common/icons/png30x30/stylingbar_delete_big.png")
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_delete_large.png")
	ImageResource eraser_big();
	*/

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylebar_angle_interval.png")
	ImageResource stylingbar_angle_interval();

	
}
