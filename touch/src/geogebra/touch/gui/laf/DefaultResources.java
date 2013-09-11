package geogebra.touch.gui.laf;

import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.ui.SVGResource.Validated;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface DefaultResources extends ClientBundle {
	static DefaultResources INSTANCE = GWT.create(DefaultResources.class);

	// Dialogs

	@Source("icons/svg/algebra_hidden.svg")
	@Validated(validated = false)
	SVGResource algebra_hidden();

	@Source("icons/svg/algebra_shown.svg")
	@Validated(validated = false)
	SVGResource algebra_shown();

	// Header

	@Source("icons/svg/arrow_cursor_finger.svg")
	@Validated(validated = false)
	SVGResource arrow_cursor_finger();

	@Source("icons/svg/arrow_cursor_grab.svg")
	@Validated(validated = false)
	SVGResource arrow_cursor_grab();

	// show - hide (AlgebraView and StylingBar)

	@Source("icons/svg/empty.svg")
	@Validated(validated = false)
	SVGResource arrow_go_next();

	@Source("icons/svg/empty.svg")
	@Validated(validated = false)
	SVGResource arrow_go_previous();


	// AlgebraView

	@Source("icons/svg/arrow_go_previous_gray.svg")
	@Validated(validated = false)
	SVGResource back();


	@Source("icons/svg/empty.svg")
	@Validated(validated = false)
	SVGResource color();

	
	@Source("icons/svg/button_cancel.svg")
	@Validated(validated = false)
	SVGResource dialog_cancel();

	@Source("icons/svg/button_trashcan.svg")
	@Validated(validated = false)
	SVGResource dialog_trash();

	@Source("icons/svg/empty.svg")
	@Validated(validated = false)
	SVGResource document_edit();

	@Source("icons/svg/document-new.svg")
	@Validated(validated = false)
	SVGResource document_new();

	@Source("icons/svg/document-open.svg")
	@Validated(validated = false)
	SVGResource document_open();

	@Source("icons/svg/document-save.svg")
	@Validated(validated = false)
	SVGResource document_save();

	@Source("icons/svg/document-save.svg")
	@Validated(validated = false)
	SVGResource document_share();

	@Source("icons/svg/empty.svg")
	@Validated(validated = false)
	SVGResource document_viewer();

	
	@Source("icons/svg/empty.svg")
	@Validated(validated = false)
	SVGResource icon_fx();

	@Source("icons/svg/empty.svg")
	@Validated(validated = false)
	SVGResource icon_question();

	@Source("icons/svg/empty.svg")
	@Validated(validated = false)
	SVGResource icon_warning();

	
	@Source("icons/svg/stylingbar_line-dash-dot.svg")
	@Validated(validated = false)
	SVGResource line_dash_dot();

	@Source("icons/svg/stylingbar_line-dashed-long.svg")
	@Validated(validated = false)
	SVGResource line_dashed_long();

	@Source("icons/svg/stylingbar_line-dashed-short.svg")
	@Validated(validated = false)
	SVGResource line_dashed_short();

	@Source("icons/svg/stylingbar_line-dotted.svg")
	@Validated(validated = false)
	SVGResource line_dotted();

	@Source("icons/svg/stylingbar_line-solid.svg")
	@Validated(validated = false)
	SVGResource line_solid();

	
	@Source("icons/svg/optionsBoxArrow.svg")
	@Validated(validated = false)
	SVGResource optionsBoxArrow();

	
	@Source("icons/svg/stylingbar_graphicsview_point_capturing.svg")
	@Validated(validated = false)
	SVGResource point_capturing();

	
	@Source("gif/progress_indicator.gif")
	ImageResource progressIndicator();

	@Source("icons/svg/properties_defaults_2.svg")
	@Validated(validated = false)
	SVGResource properties_default();

	@Source("icons/svg/android/elem_radioButtonActive.svg")
	@Validated(validated = false)
	SVGResource radioButtonActive();

	@Source("icons/svg/android/elem_radioButtonInactive.svg")
	@Validated(validated = false)
	SVGResource radioButtonInactive();

	@Source("icons/svg/menu_edit_redo.svg")
	@Validated(validated = false)
	SVGResource redo();

	
	// GeoGebraTube View
	@Source("icons/svg/view_zoom.svg")
	@Validated(validated = false)
	SVGResource search();

	
	@Source("icons/svg/stylingbar_spreadsheetview_show_input_bar.svg")
	@Validated(validated = false)
	SVGResource show_input_bar();

	@Source("icons/svg/stylingbar_graphicsview_show_or_hide_the_axes.svg")
	@Validated(validated = false)
	SVGResource show_or_hide_the_axes();

	@Source("icons/svg/stylingbar_graphicsview_show_or_hide_the_grid.svg")
	@Validated(validated = false)
	SVGResource show_or_hide_the_grid();

	
	// New Styles
	@Source("icons/svg/subToolbarArrow.svg")
	@Validated(validated = false)
	SVGResource subToolBarArrow();


	@Source("icons/svg/arrow_dockbar_triangle_down.svg")
	@Validated(validated = false)
	SVGResource triangle_down();

	@Source("icons/svg/arrow_dockbar_triangle_left.svg")
	@Validated(validated = false)
	SVGResource triangle_left();

	@Source("icons/svg/arrow_dockbar_triangle_right.svg")
	@Validated(validated = false)
	SVGResource triangle_right();

	@Source("icons/svg/menu_edit_undo.svg")
	@Validated(validated = false)
	SVGResource undo();
}
