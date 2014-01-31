package geogebra.touch.gui.laf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface DefaultResources extends ClientBundle {
	static DefaultResources INSTANCE = GWT.create(DefaultResources.class);

	@Source("geogebra/resources/css/ios7.css")
	TextResource ios7Style();
	
	@Source("geogebra/resources/css/styles-rtl.css")
	TextResource rtlStyle();
	
	@Source("geogebra/resources/css/styles-win8-rtl.css")
	TextResource rtlStyleWin();
	
	@Source("geogebra/resources/css/styles-apple-rtl.css")
	TextResource rtlStyleApple();
	
	@Source("geogebra/resources/css/styles-android-rtl.css")
	TextResource rtlStyleAndroid();
	
	@Source("geogebra/resources/css/styles-additional-rtl.css")
	TextResource additionalRtlStyle();
	
	// Dialogs

	@Source("icons/png/algebra_hidden.png")
	ImageResource algebra_hidden();

	@Source("icons/png/algebra_shown.png")
	ImageResource algebra_shown();
	
	@Source("icons/png/android/kamera.png")
	ImageResource icon_kamera();
	
	@Source("icons/png/android/gallery.png")
	ImageResource icon_gallery();

	// Header

	@Source("icons/png/arrow_cursor_finger.png")
	ImageResource arrow_cursor_finger();

	@Source("icons/png/arrow_cursor_grab.png")
	ImageResource arrow_cursor_grab();

	// show - hide (AlgebraView and StylingBar)

	@Source("icons/png/empty.png")
	ImageResource arrow_go_next();

	@Source("icons/png/empty.png")
	ImageResource arrow_go_previous();


	// AlgebraView

	@Source("icons/png/arrow_go_previous_gray.png")
	ImageResource back();


	@Source("icons/png/empty.png")
	ImageResource color();

	
	@Source("icons/png/android/button_cancel.png")
	ImageResource dialog_cancel();

	@Source("icons/png/android/button_trashcan.png")
	ImageResource dialog_trash();

	@Source("icons/png/empty.png")
	ImageResource document_edit();

	@Source("icons/png/document-new.png")
	ImageResource document_new();

	@Source("icons/png/document-open.png")
	ImageResource document_open();

	@Source("icons/png/document-save.png")
	ImageResource document_save();

	@Source("icons/png/document-save.png")
	ImageResource document_share();

	@Source("icons/png/empty.png")
	ImageResource document_viewer();

	
	@Source("icons/png/empty.png")
	ImageResource icon_fx();

	@Source("icons/png/empty.png")
	ImageResource icon_question();

	@Source("icons/png/empty.png")
	ImageResource icon_warning();

	
	@Source("icons/png/stylingbar/stylingbar_line-dash-dot.png")
	ImageResource line_dash_dot();

	@Source("icons/png/stylingbar/stylingbar_line-dashed-long.png")
	ImageResource line_dashed_long();

	@Source("icons/png/stylingbar/stylingbar_line-dashed-short.png")
	ImageResource line_dashed_short();

	@Source("icons/png/stylingbar/stylingbar_line-dotted.png")
	ImageResource line_dotted();

	@Source("icons/png/stylingbar/stylingbar_line-solid.png")
	ImageResource line_solid();
	
	@Source("icons/png/stylingbar/stylingbar_point-full.png")
	ImageResource point_full();
	
	@Source("icons/png/stylingbar/stylingbar_point-empty.png")
	ImageResource point_empty();
	
	@Source("icons/png/stylingbar/stylingbar_point-cross.png")
	ImageResource point_cross();
	
	@Source("icons/png/stylingbar/stylingbar_point-cross-diag.png")
	ImageResource point_cross_diag();
	
	@Source("icons/png/stylingbar/stylingbar_point-diamond-full.png")
	ImageResource point_diamond();
	
	@Source("icons/png/stylingbar/stylingbar_point-diamond-empty.png")
	ImageResource point_diamond_empty();
	
	@Source("icons/png/stylingbar/stylingbar_point-up.png")
	ImageResource point_up();
	
	@Source("icons/png/stylingbar/stylingbar_point-down.png")
	ImageResource point_down();
	
	@Source("icons/png/stylingbar/stylingbar_point-left.png")
	ImageResource point_left();
	
	@Source("icons/png/stylingbar/stylingbar_point-right.png")
	ImageResource point_right();

	
	@Source("icons/png/optionsBoxArrow.png")
	ImageResource optionsBoxArrow();

	
	@Source("icons/png/stylingbar/stylingbar_graphicsview_point_capturing.png")
	ImageResource point_capturing();

	
	@Source("gif/progress_indicator.gif")
	ImageResource progressIndicator();

	@Source("icons/png/properties_defaults_2.png")
	ImageResource properties_default();

	@Source("icons/png/android/elem_radioButtonActive.png")
	ImageResource radioButtonActive();

	@Source("icons/png/android/elem_radioButtonInactive.png")
	ImageResource radioButtonInactive();

	@Source("icons/png/menu_edit_redo.png")
	ImageResource redo();

	
	// GeoGebraTube View
	@Source("icons/png/view_zoom.png")
	ImageResource search();

	
	@Source("icons/png/stylingbar/stylingbar_spreadsheetview_show_input_bar.png")
	ImageResource show_input_bar();

	@Source("icons/png/stylingbar/stylingbar_graphicsview_show_or_hide_the_axes.png")
	ImageResource show_or_hide_the_axes();

	@Source("icons/png/stylingbar/stylingbar_graphicsview_show_or_hide_the_grid.png")
	ImageResource show_or_hide_the_grid();

	@Source("icons/png/iOS7/stylingbar_graphicsview_standardview.png")
	ImageResource standardView();
	
	// New Styles
	@Source("icons/png/subToolbarArrow.png")
	ImageResource subToolBarArrow();


	/*@Source("icons/png/arrow_dockbar_triangle_down.png")
	ImageResource triangle_down();*/

	@Source("icons/png/arrow_dockbar_triangle_left.png")
	ImageResource triangle_left();

	/*@Source("icons/png/arrow_dockbar_triangle_right.png")
	ImageResource triangle_right();*/
	
	@Source("icons/png/menu_edit_undo.png")
	ImageResource undo();
}
