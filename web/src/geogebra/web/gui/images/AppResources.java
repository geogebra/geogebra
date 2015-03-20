package geogebra.web.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;

public interface AppResources extends ClientBundleWithLookup {
	
	AppResources INSTANCE = GWT.create(AppResources.class);
//	public static String iconString = "empty.gif";
//	
//	@Source("geogebra/web/gui/images/" + iconString)
//	ImageResource icon();
//	
	
	//@Source("geogebra/web/gui/images/view-properties16.png")
	@Source("icons/png/web/menu_icons/menu-options.png")
	ImageResource view_properties16();
	
	@Source("geogebra/web/gui/images/options-layout24.png")
	ImageResource options_layout24();
	
	//@Source("geogebra/web/gui/images/pin.png")
	@Source("icons/png/web/menu_icons/menu-pin.png")
	ImageResource pin();
	
	@Source("geogebra/web/gui/images/geogebra32.png")
	ImageResource GeoGebraTube();

	@Source("geogebra/web/gui/images/applications-graphics.png")
	ImageResource application_graphics();
	
	@Source("geogebra/web/gui/images/apply.png")
	ImageResource apply();
	
	@Source("geogebra/web/gui/images/arrow-in.png")
	ImageResource arrow_in();
	
	@Source("geogebra/web/gui/images/arrow-out.png")
	ImageResource arrow_out();
	
	@Source("geogebra/web/gui/images/aux_folder.gif")
	ImageResource aux_folder();
	
	/*@Source("geogebra/web/gui/images/auxiliary.png")
	ImageResource auxiliary();*/
	
	/*@Source("geogebra/web/gui/images/stylingbar_graphicsview_show_or_hide_the_axes.png")
	ImageResource axes();*/
	
	@Source("geogebra/web/gui/images/border_all.png")
	ImageResource border_all();
	
	@Source("geogebra/web/gui/images/border_bottom.png")
	ImageResource border_bottom();
	
	@Source("geogebra/web/gui/images/border_frame.png")
	ImageResource border_frame();
	
	@Source("geogebra/web/gui/images/border_inside.png")
	ImageResource border_inside();
	
	@Source("geogebra/web/gui/images/border_left.png")
	ImageResource border_left();
	
	@Source("geogebra/web/gui/images/border_none.png")
	ImageResource border_none();
	
	@Source("geogebra/web/gui/images/border_right.png")
	ImageResource border_right();
	
	@Source("geogebra/web/gui/images/border_top.png")
	ImageResource border_top();
	
	@Source("geogebra/web/gui/images/cas.png")
	ImageResource cas();
	
	@Source("geogebra/web/gui/images/checkbox16.gif")
	ImageResource checkbox16();
	
	@Source("geogebra/web/gui/images/color_chooser_check.png")
	ImageResource color_chooser_check();
	
	@Source("geogebra/web/gui/images/configure-24.png")
	ImageResource configure_24();
	
	@Source("geogebra/web/gui/images/configure-32.png")
	ImageResource configure_32();
	
	@Source("geogebra/web/gui/images/corner1.png")
	ImageResource corner1();
	
	@Source("geogebra/web/gui/images/corner2.png")
	ImageResource corner2();
	
	@Source("geogebra/web/gui/images/corner4.png")
	ImageResource corner4();
	
	@Source("icons/png16x16/cumulative_distribution.png")
	ImageResource cumulative_distribution();
	
	@Source("geogebra/web/gui/images/cursor_arrow.png")
	ImageResource cursor_arrow();
	
	@Source("geogebra/web/gui/images/cursor_grab.gif")
	ImageResource cursor_grab();
	
	@Source("geogebra/web/gui/images/cursor_grabbing.gif")
	ImageResource cursor_grabbing();
	
	@Source("geogebra/web/gui/images/cursor_large_cross.gif")
	ImageResource cursor_large_cross();
	
	@Source("geogebra/web/gui/images/cursor_zoomin.gif")
	ImageResource cursor_zoomin();
	
	@Source("geogebra/web/gui/images/cursor_zoomout.gif")
	ImageResource cursor_zoomout();
	
	//@Source("geogebra/web/gui/images/delete_small.gif")
	@Source("icons/png/web/menu_icons/menu-edit-delete.png")
	ImageResource delete_small();
	
	/*@Source("geogebra/web/gui/images/document-new.png")
	ImageResource document_new();*/
	
	/*@Source("geogebra/web/gui/images/document-open.png")
	ImageResource document_open();*/
	
	@Source("geogebra/web/gui/images/document-print-preview.png")
	ImageResource document_print_preview();
	
	@Source("geogebra/web/gui/images/document-print.png")
	ImageResource document_print();
	
	@Source("geogebra/web/gui/images/document-properties.png")
	ImageResource document_properties();
	
	/*@Source("geogebra/web/gui/images/document-save.png")
	ImageResource document_save();*/
	
	@Source("geogebra/web/gui/images/edit-clear.png")
	ImageResource edit_clear();
	
	//@Source("geogebra/web/gui/images/edit-copy.png")
	@Source("icons/png/web/menu_icons/menu-edit-copy.png")
	ImageResource edit_copy();
	
	//@Source("geogebra/web/gui/images/edit-cut.png")
	@Source("icons/png/web/menu_icons/menu-edit-cut.png")
	ImageResource edit_cut();
	
	//@Source("geogebra/web/gui/images/edit-paste.png")
	@Source("icons/png/web/menu_icons/menu-edit-paste.png")
	ImageResource edit_paste();
	
	/*@Source("geogebra/web/gui/images/edit-redo.png")
	ImageResource edit_redo();
	
	@Source("geogebra/web/gui/images/edit-undo.png")
	ImageResource edit_undo();*/
	
	//@Source("geogebra/web/gui/images/edit.png")
	@Source("icons/png/web/menu_icons/menu-edit.png")
	ImageResource edit();
	
	@Source("geogebra/web/gui/images/empty.gif")
	ImageResource empty();
	
	@Source("geogebra/web/gui/images/euclidian.png")
	ImageResource euclidian();
	
	@Source("geogebra/web/gui/images/exit.png")
	ImageResource exit();
	
	/*@Source("geogebra/web/gui/images/export_small.png")
	ImageResource export_small();*/
	
	@Source("geogebra/web/gui/images/export-html.png")
	ImageResource export_html();
	
	@Source("geogebra/web/gui/images/export.png")
	ImageResource export();
	
	@Source("geogebra/web/gui/images/folder.png")
	ImageResource folder();
	
	@Source("geogebra/web/gui/images/font.png")
	ImageResource font();
	
	@Source("geogebra/web/gui/images/format-justify-center.png")
	ImageResource format_justify_center();
	
	@Source("geogebra/web/gui/images/format-justify-left.png")
	ImageResource format_justify_left();
	
	@Source("geogebra/web/gui/images/format-justify-right.png")
	ImageResource format_justify_right();
	
	@Source("geogebra/web/gui/images/format-text-bold.png")
	ImageResource format_text_bold();
	
	@Source("geogebra/web/gui/images/format-text-italic.png")
	ImageResource  format_text_italic();
	
	@Source("geogebra/web/gui/images/formula_bar.png")
	ImageResource formula_bar();
	
	@Source("geogebra/web/gui/images/geogebra.png")
	ImageResource geogebra();
	
	@Source("geogebra/web/gui/images/geogebra32.png")
	ImageResource geogebra32();
	
	@Source("geogebra/web/gui/images/geogebra64.png")
	ImageResource geogebra64();
	
	@Source("geogebra/web/gui/images/globe.png")
	ImageResource globe();
	
	@Source("geogebra/web/gui/images/go-down.png")
	ImageResource go_down();
	
	@Source("geogebra/web/gui/images/go-last-gray.png")
	ImageResource go_last_gray();
	
	@Source("geogebra/web/gui/images/go-next-gray.png")
	ImageResource go_next_gray();
	
	@Source("geogebra/web/gui/images/go-next.png")
	ImageResource go_next();
	
	@Source("geogebra/web/gui/images/go-previous-gray.png")
	ImageResource go_previous_gray();
	
	@Source("geogebra/web/gui/images/go-previous.png")
	ImageResource go_previous();
	
	@Source("geogebra/web/gui/images/go-previous24.png")
	ImageResource go_previous24();
	
	@Source("geogebra/web/gui/images/go-up.png")
	ImageResource go_up();
	
	/*@Source("geogebra/web/gui/images/stylingbar_graphicsview_show_or_hide_the_grid.png")
	ImageResource grid();*/
	
	@Source("geogebra/web/gui/images/header_column.png")
	ImageResource header_column();
	
	@Source("geogebra/web/gui/images/header_row.png")
	ImageResource header_row();
	
	@Source("geogebra/web/gui/images/help.png")
	ImageResource help();
	
	@Source("geogebra/web/gui/images/algebra_hidden.png")
	ImageResource hidden();
	
	@Source("geogebra/web/gui/images/image-x-generic.png")
	ImageResource image_x_generic();
	
	@Source("geogebra/web/gui/images/info.gif")
	ImageResource info();
	
	@Source("geogebra/web/gui/images/inputhelp_left_16x16.png")
	ImageResource inputhelp_left_16x16();
	
	@Source("geogebra/web/gui/images/inputhelp_left_18x18.png")
	ImageResource inputhelp_left_18x18();
	
	@Source("geogebra/web/gui/images/inputhelp_left_20x20.png")
	ImageResource inputhelp_left_20x20();
	
	@Source("geogebra/web/gui/images/inputhelp_right_16x16.png")
	ImageResource inputhelp_right_16x16();
	
	@Source("geogebra/web/gui/images/inputhelp_right_18x18.png")
	ImageResource inputhelp_right_18x18();
	
	@Source("geogebra/web/gui/images/inputhelp_right_20x20.png")
	ImageResource inputhelp_right_20x20();
	
	@Source("geogebra/web/gui/images/keyboard.png")
	ImageResource keyboard();
	
	@Source("geogebra/web/gui/images/line_graph.png")
	ImageResource line_graph();
	
	@Source("geogebra/web/gui/images/list-add.png")
	ImageResource list_add();
	
	@Source("geogebra/web/gui/images/lock.png")
	ImageResource lock();
	
	/*@Source("geogebra/web/gui/images/stylingbar_graphicsview_point_capturing.png")
	ImageResource magnet();*/
	
	@Source("geogebra/web/gui/images/magnet2.gif")
	ImageResource magnet2();
	
	@Source("geogebra/web/gui/images/mode_angle_16.gif")
	ImageResource mode_angle_16();
	
	//@Source("geogebra/web/gui/images/mode_copyvisualstyle_16.png")
	@Source("icons/png/web/menu_icons/menu-edit-copy-visual-style.png")
	ImageResource mode_copyvisualstyle_16();
	
	@Source("geogebra/web/gui/images/mode_point_16.gif")
	ImageResource mode_point_16();
	
	//@Source("geogebra/web/gui/images/mode_showhidelabel_16.gif")
	@Source("icons/png/web/menu_icons/menu-options-labeling.png")
	ImageResource mode_showhidelabel_16();
	
	//@Source("geogebra/web/gui/images/mode_showhideobject_16.gif")
	@Source("icons/png/web/menu_icons/mode_showhideobject.png")
	ImageResource mode_showhideobject_16();
	
	@Source("geogebra/web/gui/images/nav_fastforward.png")
	ImageResource nav_fastforward();
	
	@Source("geogebra/web/gui/images/nav_rewind.png")
	ImageResource nav_rewind();
	
	@Source("geogebra/web/gui/images/nav_skipback.png")
	ImageResource nav_skipback();
	
	@Source("geogebra/web/gui/images/nav_skipforward.png")
	ImageResource nav_skipforward();
	
	@Source("geogebra/web/gui/images/nav_play.png")
	ImageResource nav_play();

	@Source("geogebra/web/gui/images/nav_pause.png")
	ImageResource nav_pause();

	/*@Source("geogebra/web/gui/images/dialog-error-32px.png")
	ImageResource dialog_error();
	
	@Source("geogebra/web/gui/images/dialog-info-32px.png")
	ImageResource dialog_info();
	
	@Source("geogebra/web/gui/images/dialog-question-32px.png")
	ImageResource dialog_question();
	
	@Source("geogebra/web/gui/images/dialog-warning-32px.png")
	ImageResource dialog_warning();*/
	
	@Source("geogebra/web/gui/images/options_btn.png")
	ImageResource options_btn();
	
	@Source("geogebra/web/gui/images/options-advanced.png")
	ImageResource options_advanced();
	
	@Source("geogebra/web/gui/images/options-advanced24.png")
	ImageResource options_advanced24();
	
	@Source("geogebra/web/gui/images/options-defaults224.png")
	ImageResource options_defaults224();
	
	@Source("geogebra/web/gui/images/options-large.png")
	ImageResource options_large();
	
	/*@Source("geogebra/web/gui/images/options-objects24.png")
	ImageResource options_objects24();*/
	
	@Source("geogebra/web/gui/images/osculating_circle.png")
	ImageResource osculating_circle();
	
	@Source("geogebra/web/gui/images/paste.gif")
	ImageResource paste();
	
	@Source("geogebra/web/gui/images/perspective.gif")
	ImageResource perspective();
	
	//@Source("geogebra/web/gui/images/rename.png")
	@Source("icons/png/web/menu_icons/menu-edit-rename.png")
	ImageResource rename();
	
	@Source("geogebra/web/gui/images/right_angle.gif")
	ImageResource right_angle();
	
	@Source("geogebra/web/gui/images/separator.gif")
	ImageResource separator();
	
	@Source("geogebra/web/gui/images/algebra_shown.png")
	ImageResource shown();
	
	@Source("geogebra/web/gui/images/spreadsheet_grid.png")
	ImageResource spreadsheet_grid();
	
	@Source("geogebra/web/gui/images/spreadsheet.png")
	ImageResource spreadsheet();
	
	@Source("geogebra/web/gui/images/spreadsheettrace_button.gif")
	ImageResource spreadsheettrace_button();
	
	@Source("geogebra/web/gui/images/spreadsheettrace_hover.gif")
	ImageResource spreadsheettrace_hover();
	
	//@Source("geogebra/web/gui/images/spreadsheettrace.gif")
	@Source("icons/png/web/menu_icons/menu-record-to-spreadsheet.png")
	ImageResource spreadsheettrace();
	
	@Source("geogebra/web/gui/images/table.gif")
	ImageResource table();
	
	@Source("geogebra/web/gui/images/tangent_line.png")
	ImageResource tangent_line();
	
	@Source("geogebra/web/gui/images/text-editor.png")
	ImageResource text_editor();
	
	@Source("geogebra/web/gui/images/text-html.png")
	ImageResource text_html();
	
	@Source("geogebra/web/gui/images/text-x-generic.png")
	ImageResource text_x_generic();
	
	@Source("geogebra/web/gui/images/tool.png")
	ImageResource tool();
	
	//@Source("geogebra/web/gui/images/trace_on.gif")
	@Source("icons/png/web/menu_icons/menu-trace-on.png")
	ImageResource trace_on();
	
	@Source("geogebra/web/gui/images/tree-close.png")
	ImageResource tree_close();
	
	@Source("geogebra/web/gui/images/tree-open.png")
	ImageResource tree_open();
	
	@Source("geogebra/web/gui/images/tree.png")
	ImageResource tree();
	
	@Source("geogebra/web/gui/images/arrow_dockbar_triangle_down.png")
	ImageResource triangle_down();
	
	@Source("geogebra/web/gui/images/arrow_dockbar_triangle_up.png")
	ImageResource triangle_up();
	
	/*@Source("geogebra/web/gui/images/arrow_dockbar_triangle_right.png")
	ImageResource triangle_right();*/
	
	@Source("geogebra/web/gui/images/users.png")
	ImageResource users();
	
	@Source("geogebra/web/gui/images/view_btn.png")
	ImageResource view_btn();
	
	
	
	@Source("geogebra/web/gui/images/view-maximize.png")
	ImageResource view_maximize();
	
	@Source("geogebra/web/gui/images/view-move.png")
	ImageResource view_move();
	
	@Source("geogebra/web/gui/images/view-refresh.png")
	ImageResource view_refresh();
	

	
	@Source("geogebra/web/gui/images/view-unmaximize.png")
	ImageResource view_unmaximize();
	
	@Source("geogebra/web/gui/images/view-unwindow.png")
	ImageResource view_unwindow();
	
	@Source("geogebra/web/gui/images/view-window.png")
	ImageResource view_window();
	
	@Source("geogebra/web/gui/images/wiki.png")
	ImageResource wiki();
	
	@Source("geogebra/web/gui/images/xy_segments.png")
	ImageResource xy_segments();
	
	@Source("geogebra/web/gui/images/xy_table.png")
	ImageResource xy_table();
	
	//@Source("geogebra/web/gui/images/zoom16.gif")
	@Source("icons/png/web/menu_icons/menu-file-open.png")
	ImageResource zoom16();
	
	/*@Source("geogebra/web/gui/images/drive_icon_16.png")
	ImageResource drive_icon_16();
	
	@Source("geogebra/web/gui/images/skydrive_icon_16.png")
	ImageResource skydrive_icon_16();
	
	@Source("geogebra/web/gui/images/social-facebook.png")
	ImageResource social_facebook();
	
	@Source("geogebra/web/gui/images/social-google.png")
	ImageResource social_google();
	
	@Source("geogebra/web/gui/images/social-openid.png")
	ImageResource social_openid();
	
	@Source("geogebra/web/gui/images/social-twitter.png")
	ImageResource social_twitter();*/

	/*@Source("geogebra/web/gui/images/stylingbar_graphicsview_standardview.png")
	ImageResource standard_view();*/
	
	// now in GUI Resources
	/*@Source("geogebra/web/gui/images/menuBarSubMenuIconLTR.gif")
	ImageResource menuBarSubMenuIconLTR();

	@Source("geogebra/web/gui/images/menuBarSubMenuIconRTL.gif")
	ImageResource menuBarSubMenuIconRTL();*/

	/*@Source("icons/png30x30/stylingbar_point-full.png")
	ImageResource point_full();
	
	@Source("icons/png30x30/stylingbar_point-empty.png")
	ImageResource point_empty();
	
	@Source("icons/png30x30/stylingbar_point-cross.png")
	ImageResource point_cross();
	
	@Source("icons/png30x30/stylingbar_point-cross-diag.png")
	ImageResource point_cross_diag();
	
	@Source("icons/png30x30/stylingbar_point-diamond-full.png")
	ImageResource point_diamond();
	
	@Source("icons/png30x30/stylingbar_point-diamond-empty.png")
	ImageResource point_diamond_empty();
	
	@Source("icons/png30x30/stylingbar_point-up.png")
	ImageResource point_up();
	
	@Source("icons/png30x30/stylingbar_point-down.png")
	ImageResource point_down();
	
	@Source("icons/png30x30/stylingbar_point-left.png")
	ImageResource point_left();
	
	@Source("icons/png30x30/stylingbar_point-right.png")
	ImageResource point_right();*/

	/*@Source("icons/png16x16/interval-left.png")
	ImageResource interval_left();
	
	@Source("icons/png16x16/interval-right.png")
	ImageResource interval_right();
	
	@Source("icons/png16x16/interval-between.png")
	ImageResource interval_between();*/

	@Source("icons/png16x16/step_graph.png")
	ImageResource step_graph();
	
	@Source("icons/png16x16/bar_graph.png")
	ImageResource bar_graph();

	@Source("icons/png/web/stylingbar/stylingbar_dots.png")
	ImageResource dots();

	@Source("icons/png/web/stylingbar/stylingbar_dots_active.png")
	ImageResource dots_active();

	/*@Source("icons/png16x16/export16.png")
	ImageResource export16();*/
	
	/*@Source("icons/png16x16/normal-overlay.png")
	ImageResource normal_overlay();*/
	

	@Source("icons/png/web/stylingbar/stylingbar_data_analysis_show_statistics.png")
	ImageResource dataview_showstatistics();

	@Source("icons/png/web/stylingbar/stylingbar_data_analysis_show_data.png")
	ImageResource dataview_showdata();

	@Source("icons/png/web/stylingbar/stylingbar_data_analysis_show_2nd_plot.png")
	ImageResource dataview_showplot2();


	/*
	//@Source("icons/png30x30/stylingbar_delete_small.png")
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_delete_small.png")
	ImageResource eraser_small();

	//@Source("icons/png30x30/stylingbar_delete_medium.png")
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_delete_medium.png")
	ImageResource eraser_medium();
	
	//@Source("icons/png30x30/stylingbar_delete_big.png")
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_delete_large.png")
	ImageResource eraser_big();
	*/

	@Source("icons/png/web/stylingbar/stylebar_angle_interval.png")
	ImageResource stylingbar_angle_interval();

	
}
