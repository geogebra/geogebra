package geogebra.touch.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;

public interface CommonResourcesIconTest extends ClientBundleWithLookup
{

	public static CommonResourcesIconTest INSTANCE = GWT.create(CommonResourcesIconTest.class);

	/*
	 * @Source("icons/svg/point_standard.svg")
	 * 
	 * @Validated(validated = false) SVGResource point_standard();
	 * 
	 * @Source("icons/svg/arrows/arrow-in.svg")
	 * 
	 * @Validated(validated = false) SVGResource arrowIn();
	 * 
	 * @Source("icons/svg/arrows/arrow-out.svg")
	 * 
	 * @Validated(validated = false) SVGResource arrowOut();
	 * 
	 * @Source("icons/svg/arrows/cursor_arrow.svg")
	 * 
	 * @Validated(validated = false) SVGResource cursor_arrow();
	 * 
	 * @Source("icons/svg/arrows/dockbar-triangle-left.svg")
	 * 
	 * @Validated(validated = false) SVGResource dockbarTriangleLeft();
	 * 
	 * @Source("icons/svg/arrows/dockbar-triangle-right.svg")
	 * 
	 * @Validated(validated = false) SVGResource dockbarTriangleRight();
	 * 
	 * @Source("icons/svg/arrows/go-down.svg")
	 * 
	 * @Validated(validated = false) SVGResource goDown();
	 * 
	 * @Source("icons/svg/arrows/go-last-gray.svg")
	 * 
	 * @Validated(validated = false) SVGResource goLastGray();
	 * 
	 * @Source("icons/svg/arrows/go-next-gray.svg")
	 * 
	 * @Validated(validated = false) SVGResource goNextGray();
	 * 
	 * @Source("icons/svg/arrows/go-next.svg")
	 * 
	 * @Validated(validated = false) SVGResource goNext();
	 * 
	 * @Source("icons/svg/arrows/go-previous-gray.svg")
	 * 
	 * @Validated(validated = false) SVGResource goPreviousGray();
	 * 
	 * @Source("icons/svg/arrows/go-previous.svg")
	 * 
	 * @Validated(validated = false) SVGResource goPrevious();
	 * 
	 * @Source("icons/svg/arrows/go-previous24.svg")
	 * 
	 * @Validated(validated = false) SVGResource goPrevious24();
	 * 
	 * @Source("icons/svg/arrows/go-up.svg")
	 * 
	 * @Validated(validated = false) SVGResource goUp();
	 * 
	 * @Source("icons/svg/arrows/inputhelp-left.svg")
	 * 
	 * @Validated(validated = false) SVGResource inputhelpLeft();
	 * 
	 * @Source("icons/svg/arrows/inputhelp-right.svg")
	 * 
	 * @Validated(validated = false) SVGResource inputhelpRight();
	 * 
	 * @Source("icons/svg/arrows/nav_fastforward.svg")
	 * 
	 * @Validated(validated = false) SVGResource nav_fastforward();
	 * 
	 * @Source("icons/svg/arrows/nav_rewind.svg")
	 * 
	 * @Validated(validated = false) SVGResource nav_rewind();
	 * 
	 * @Source("icons/svg/arrows/nav_skipback.svg")
	 * 
	 * @Validated(validated = false) SVGResource nav_skipback();
	 * 
	 * @Source("icons/svg/arrows/nav_skipforward.svg")
	 * 
	 * @Validated(validated = false) SVGResource nav_skipforward();
	 * 
	 * @Source("icons/svg/arrows/triangle-down-rollover.svg")
	 * 
	 * @Validated(validated = false) SVGResource triangleDownRollover();
	 * 
	 * @Source("icons/svg/arrows/triangle-down.svg")
	 * 
	 * @Validated(validated = false) SVGResource triangleDown();
	 * 
	 * @Source("icons/svg/arrows/triangle-up-rollover.svg")
	 * 
	 * @Validated(validated = false) SVGResource triangleUpRollover();
	 * 
	 * @Source("icons/svg/arrows/triangle-up.svg")
	 * 
	 * @Validated(validated = false) SVGResource triangleUp();
	 * 
	 * @Source("icons/svg/contextmenu/edit_text.svg")
	 * 
	 * @Validated(validated = false) SVGResource edit_text();
	 * 
	 * @Source("icons/svg/contextmenu/rename.svg")
	 * 
	 * @Validated(validated = false) SVGResource rename();
	 * 
	 * @Source("icons/svg/contextmenu/trace_on.svg")
	 * 
	 * @Validated(validated = false) SVGResource trace_on();
	 * 
	 * @Source("icons/svg/general/geogebra_tube_writing.svg")
	 * 
	 * @Validated(validated = false) SVGResource geogebra_tube_writing();
	 * 
	 * @Source("icons/svg/general/geogebra-logo.svg")
	 * 
	 * @Validated(validated = false) SVGResource geogebraLogo();
	 * 
	 * @Source("icons/svg/menu/edit/properties_advanced.svg")
	 * 
	 * @Validated(validated = false) SVGResource properties_advanced();
	 * 
	 * @Source("icons/svg/menu/edit/properties_defaults.svg")
	 * 
	 * @Validated(validated = false) SVGResource properties_defaults();
	 * 
	 * @Source("icons/svg/menu/edit/properties_graphics.svg")
	 * 
	 * @Validated(validated = false) SVGResource properties_graphics();
	 * 
	 * @Source("icons/svg/menu/edit/properties_layout.svg")
	 * 
	 * @Validated(validated = false) SVGResource properties_layout();
	 * 
	 * @Source("icons/svg/menu/edit/properties_object.svg")
	 * 
	 * @Validated(validated = false) SVGResource properties_object();
	 * 
	 * @Source("icons/svg/menu/edit/redo.svg")
	 * 
	 * @Validated(validated = false) SVGResource redo();
	 * 
	 * @Source("icons/svg/menu/edit/undo.svg")
	 * 
	 * @Validated(validated = false) SVGResource undo();
	 * 
	 * @Source("icons/svg/menu/file/close.svg")
	 * 
	 * @Validated(validated = false) SVGResource close();
	 * 
	 * @Source("icons/svg/menu/file/document-print.svg")
	 * 
	 * @Validated(validated = false) SVGResource documentPrint();
	 * 
	 * @Source("icons/svg/menu/file/document-print22.svg")
	 * 
	 * @Validated(validated = false) SVGResource documentPrint22();
	 * 
	 * @Source("icons/svg/menu/file/export_dynamix_worksheet_as_webpage.svg")
	 * 
	 * @Validated(validated = false) SVGResource
	 * export_dynamix_worksheet_as_webpage();
	 * 
	 * @Source("icons/svg/menu/file/export_graphics_view_as_picture.svg")
	 * 
	 * @Validated(validated = false) SVGResource
	 * export_graphics_view_as_picture();
	 * 
	 * @Source("icons/svg/menu/file/export_graphics_view_to_clipboard.svg")
	 * 
	 * @Validated(validated = false) SVGResource
	 * export_graphics_view_to_clipboard();
	 * 
	 * @Source("icons/svg/menu/file/open.svg")
	 * 
	 * @Validated(validated = false) SVGResource open();
	 * 
	 * @Source("icons/svg/menu/file/print_preview.svg")
	 * 
	 * @Validated(validated = false) SVGResource print_preview();
	 * 
	 * @Source("icons/svg/menu/file/share.svg")
	 * 
	 * @Validated(validated = false) SVGResource share();
	 * 
	 * @Source("icons/svg/menu/help/about_license.svg")
	 * 
	 * @Validated(validated = false) SVGResource about_license();
	 * 
	 * @Source("icons/svg/menu/help/geogebra_tube.svg")
	 * 
	 * @Validated(validated = false) SVGResource geogebra_tube();
	 * 
	 * @Source("icons/svg/menu/help/help.svg")
	 * 
	 * @Validated(validated = false) SVGResource help();
	 * 
	 * @Source("icons/svg/menu/options/font_size.svg")
	 * 
	 * @Validated(validated = false) SVGResource font_size();
	 * 
	 * @Source("icons/svg/menu/options/point_capturing.svg")
	 * 
	 * @Validated(validated = false) SVGResource point_capturing();
	 * 
	 * @Source("icons/svg/menu/options/save.svg")
	 * 
	 * @Validated(validated = false) SVGResource save();
	 * 
	 * @Source("icons/svg/menu/tools/create_new_tool.svg")
	 * 
	 * @Validated(validated = false) SVGResource create_new_tool();
	 * 
	 * @Source("icons/svg/menu/tools/manage_tools.svg")
	 * 
	 * @Validated(validated = false) SVGResource manage_tools();
	 * 
	 * @Source("icons/svg/menu/view/Algebra.svg")
	 * 
	 * @Validated(validated = false) SVGResource algebra();
	 * 
	 * @Source("icons/svg/menu/view/CAS.svg")
	 * 
	 * @Validated(validated = false) SVGResource cAS();
	 * 
	 * @Source("icons/svg/menu/view/construction_protocol.svg")
	 * 
	 * @Validated(validated = false) SVGResource construction_protocol();
	 * 
	 * @Source("icons/svg/menu/view/graphics.svg")
	 * 
	 * @Validated(validated = false) SVGResource graphics();
	 * 
	 * @Source("icons/svg/menu/view/graphics2.svg")
	 * 
	 * @Validated(validated = false) SVGResource graphics2();
	 * 
	 * @Source("icons/svg/menu/view/keyboard.svg")
	 * 
	 * @Validated(validated = false) SVGResource keyboard();
	 * 
	 * @Source("icons/svg/menu/view/properties.svg")
	 * 
	 * @Validated(validated = false) SVGResource properties();
	 * 
	 * @Source("icons/svg/menu/view/refresh_view.svg")
	 * 
	 * @Validated(validated = false) SVGResource refresh_view();
	 * 
	 * @Source("icons/svg/menu/window/new_window.svg")
	 * 
	 * @Validated(validated = false) SVGResource new_window();
	 * 
	 * @Source("icons/svg/stylingbar/algebraview/auxiliary_objects.svg")
	 * 
	 * @Validated(validated = false) SVGResource auxiliary_objects();
	 * 
	 * @Source("icons/svg/stylingbar/algebraview/sort_objects_by.svg")
	 * 
	 * @Validated(validated = false) SVGResource sort_objects_by();
	 * 
	 * @Source("icons/svg/stylingbar/graphicsview/absolute_position_on_screen.svg")
	 * 
	 * @Validated(validated = false) SVGResource absolute_position_on_screen();
	 * 
	 * @Source("icons/svg/stylingbar/graphicsview/set_point_capture_style.svg")
	 * 
	 * @Validated(validated = false) SVGResource set_point_capture_style();
	 * 
	 * @Source("icons/svg/stylingbar/graphicsview/show_hide_label.svg")
	 * 
	 * @Validated(validated = false) SVGResource show_hide_label();
	 * 
	 * @Source("icons/svg/stylingbar/graphicsview/show_or_hide_the_axes.svg")
	 * 
	 * @Validated(validated = false) SVGResource show_or_hide_the_axes();
	 * 
	 * @Source("icons/svg/stylingbar/graphicsview/show_or_hide_the_grid.svg")
	 * 
	 * @Validated(validated = false) SVGResource show_or_hide_the_grid();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/align_center.svg")
	 * 
	 * @Validated(validated = false) SVGResource align_center();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/align_left.svg")
	 * 
	 * @Validated(validated = false) SVGResource align_left();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/align_right.svg")
	 * 
	 * @Validated(validated = false) SVGResource align_right();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/set_border_all.svg")
	 * 
	 * @Validated(validated = false) SVGResource set_border_all();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/set_border_buttom.svg")
	 * 
	 * @Validated(validated = false) SVGResource set_border_buttom();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/set_border_frame.svg")
	 * 
	 * @Validated(validated = false) SVGResource set_border_frame();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/set_border_inside.svg")
	 * 
	 * @Validated(validated = false) SVGResource set_border_inside();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/set_border_left.svg")
	 * 
	 * @Validated(validated = false) SVGResource set_border_left();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/set_border_none.svg")
	 * 
	 * @Validated(validated = false) SVGResource set_border_none();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/set_border_right.svg")
	 * 
	 * @Validated(validated = false) SVGResource set_border_right();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/set_border_top.svg")
	 * 
	 * @Validated(validated = false) SVGResource set_border_top();
	 * 
	 * @Source("icons/svg/stylingbar/spreadsheetview/show_input_bar.svg")
	 * 
	 * @Validated(validated = false) SVGResource show_input_bar();
	 * 
	 * @Source("icons/svg/tools/actionobject/checkbox_to_show_hide_objects.svg")
	 * 
	 * @Validated(validated = false) SVGResource checkbox_to_show_hide_objects();
	 * 
	 * @Source("icons/svg/tools/actionobject/insert_button.svg")
	 * 
	 * @Validated(validated = false) SVGResource insert_button();
	 * 
	 * @Source("icons/svg/tools/actionobject/insert_input_box.svg")
	 * 
	 * @Validated(validated = false) SVGResource insert_input_box();
	 * 
	 * @Source("icons/svg/tools/actionobject/slider.svg")
	 * 
	 * @Validated(validated = false) SVGResource slider();
	 * 
	 * @Source("icons/svg/tools/cas/derivative.svg")
	 * 
	 * @Validated(validated = false) SVGResource derivative();
	 * 
	 * @Source("icons/svg/tools/cas/evaluate.svg")
	 * 
	 * @Validated(validated = false) SVGResource evaluate();
	 * 
	 * @Source("icons/svg/tools/cas/expand.svg")
	 * 
	 * @Validated(validated = false) SVGResource expand();
	 * 
	 * @Source("icons/svg/tools/cas/factor.svg")
	 * 
	 * @Validated(validated = false) SVGResource factor();
	 * 
	 * @Source("icons/svg/tools/cas/integral.svg")
	 * 
	 * @Validated(validated = false) SVGResource integral();
	 * 
	 * @Source("icons/svg/tools/cas/keep_input.svg")
	 * 
	 * @Validated(validated = false) SVGResource keep_input();
	 * 
	 * @Source("icons/svg/tools/cas/numeric.svg")
	 * 
	 * @Validated(validated = false) SVGResource numeric();
	 * 
	 * @Source("icons/svg/tools/cas/solve_numerically.svg")
	 * 
	 * @Validated(validated = false) SVGResource solve_numerically();
	 * 
	 * @Source("icons/svg/tools/cas/solve.svg")
	 * 
	 * @Validated(validated = false) SVGResource solve();
	 * 
	 * @Source("icons/svg/tools/cas/substitute.svg")
	 * 
	 * @Validated(validated = false) SVGResource substitute();
	 * 
	 * @Source("icons/svg/tools/circleandarc/circle_through_three_points.svg")
	 * 
	 * @Validated(validated = false) SVGResource circle_through_three_points();
	 * 
	 * @Source("icons/svg/tools/circleandarc/circle_with_center_and_radius.svg")
	 * 
	 * @Validated(validated = false) SVGResource circle_with_center_and_radius();
	 * 
	 * @Source("icons/svg/tools/circleandarc/circle_with_center_through_point.svg")
	 * 
	 * @Validated(validated = false) SVGResource
	 * circle_with_center_through_point();
	 * 
	 * @Source(
	 * "icons/svg/tools/circleandarc/circular_arc_with_center_between_two_points.svg"
	 * )
	 * 
	 * @Validated(validated = false) SVGResource
	 * circular_arc_with_center_between_two_points();
	 * 
	 * @Source(
	 * "icons/svg/tools/circleandarc/circular_sector_with_center_between_two_points.svg"
	 * )
	 * 
	 * @Validated(validated = false) SVGResource
	 * circular_sector_with_center_between_two_points();
	 * 
	 * @Source(
	 * "icons/svg/tools/circleandarc/circumcircular_arc_through_three_points.svg")
	 * 
	 * @Validated(validated = false) SVGResource
	 * circumcircular_arc_through_three_points();
	 * 
	 * @Source(
	 * "icons/svg/tools/circleandarc/circumcircular_sector_through_three_points.svg"
	 * )
	 * 
	 * @Validated(validated = false) SVGResource
	 * circumcircular_sector_through_three_points();
	 * 
	 * @Source("icons/svg/tools/circleandarc/compasses.svg")
	 * 
	 * @Validated(validated = false) SVGResource compasses();
	 * 
	 * @Source("icons/svg/tools/circleandarc/semicircle.svg")
	 * 
	 * @Validated(validated = false) SVGResource semicircle();
	 * 
	 * @Source("icons/svg/tools/conicsection/conic_through_5_points.svg")
	 * 
	 * @Validated(validated = false) SVGResource conic_through_5_points();
	 * 
	 * @Source("icons/svg/tools/conicsection/ellipse.svg")
	 * 
	 * @Validated(validated = false) SVGResource ellipse();
	 * 
	 * @Source("icons/svg/tools/conicsection/hyperbola.svg")
	 * 
	 * @Validated(validated = false) SVGResource hyperbola();
	 * 
	 * @Source("icons/svg/tools/conicsection/parabola.svg")
	 * 
	 * @Validated(validated = false) SVGResource parabola();
	 * 
	 * @Source("icons/svg/tools/generaltools/copy_visual_style.svg")
	 * 
	 * @Validated(validated = false) SVGResource copy_visual_style();
	 * 
	 * @Source("icons/svg/tools/generaltools/delete.svg")
	 * 
	 * @Validated(validated = false) SVGResource delete();
	 * 
	 * @Source("icons/svg/tools/generaltools/label.svg")
	 * 
	 * @Validated(validated = false) SVGResource label();
	 * 
	 * @Source("icons/svg/tools/generaltools/move_graphics_view.svg")
	 * 
	 * @Validated(validated = false) SVGResource move_graphics_view();
	 * 
	 * @Source("icons/svg/tools/generaltools/show_hide_object.svg")
	 * 
	 * @Validated(validated = false) SVGResource show_hide_object();
	 * 
	 * @Source("icons/svg/tools/generaltools/zoom_in.svg")
	 * 
	 * @Validated(validated = false) SVGResource zoom_in();
	 * 
	 * @Source("icons/svg/tools/generaltools/zoom_out.svg")
	 * 
	 * @Validated(validated = false) SVGResource zoom_out();
	 * 
	 * @Source("icons/svg/tools/line/line_through_two_points.svg")
	 * 
	 * @Validated(validated = false) SVGResource line_through_two_points();
	 * 
	 * @Source("icons/svg/tools/line/polyline_between_points.svg")
	 * 
	 * @Validated(validated = false) SVGResource polyline_between_points();
	 * 
	 * @Source("icons/svg/tools/line/ray_through_two_points.svg")
	 * 
	 * @Validated(validated = false) SVGResource ray_through_two_points();
	 * 
	 * @Source("icons/svg/tools/line/segment_between_two_points.svg")
	 * 
	 * @Validated(validated = false) SVGResource segment_between_two_points();
	 * 
	 * @Source("icons/svg/tools/line/segment_with_given_length_from_point.svg")
	 * 
	 * @Validated(validated = false) SVGResource
	 * segment_with_given_length_from_point();
	 * 
	 * @Source("icons/svg/tools/line/vector_between_two_points.svg")
	 * 
	 * @Validated(validated = false) SVGResource vector_between_two_points();
	 * 
	 * @Source("icons/svg/tools/line/vector_from_point.svg")
	 * 
	 * @Validated(validated = false) SVGResource vector_from_point();
	 * 
	 * @Source("icons/svg/tools/measurement/angle_with_given_size.svg")
	 * 
	 * @Validated(validated = false) SVGResource angle_with_given_size();
	 * 
	 * @Source("icons/svg/tools/measurement/angle.svg")
	 * 
	 * @Validated(validated = false) SVGResource angle();
	 * 
	 * @Source("icons/svg/tools/measurement/area.svg")
	 * 
	 * @Validated(validated = false) SVGResource area();
	 * 
	 * @Source("icons/svg/tools/measurement/distance_or_length.svg")
	 * 
	 * @Validated(validated = false) SVGResource distance_or_length();
	 * 
	 * @Source("icons/svg/tools/measurement/slope.svg")
	 * 
	 * @Validated(validated = false) SVGResource slope();
	 * 
	 * @Source("icons/svg/tools/movement/move.svg")
	 * 
	 * @Validated(validated = false) SVGResource move();
	 * 
	 * @Source("icons/svg/tools/movement/record_to_spreadsheet.svg")
	 * 
	 * @Validated(validated = false) SVGResource record_to_spreadsheet();
	 * 
	 * @Source("icons/svg/tools/movement/rotate_around_point.svg")
	 * 
	 * @Validated(validated = false) SVGResource rotate_around_point();
	 * 
	 * @Source("icons/svg/tools/point/attach_detach_point.svg")
	 * 
	 * @Validated(validated = false) SVGResource attach_detach_point();
	 * 
	 * @Source("icons/svg/tools/point/complex_number.svg")
	 * 
	 * @Validated(validated = false) SVGResource complex_number();
	 * 
	 * @Source("icons/svg/tools/point/intersect_two_objects.svg")
	 * 
	 * @Validated(validated = false) SVGResource intersect_two_objects();
	 * 
	 * @Source("icons/svg/tools/point/midpoint_or_center.svg")
	 * 
	 * @Validated(validated = false) SVGResource midpoint_or_center();
	 * 
	 * @Source("icons/svg/tools/point/new_point.svg")
	 * 
	 * @Validated(validated = false) SVGResource new_point();
	 * 
	 * @Source("icons/svg/tools/point/point_on_object.svg")
	 * 
	 * @Validated(validated = false) SVGResource point_on_object();
	 * 
	 * @Source("icons/svg/tools/polygon/polygon.svg")
	 * 
	 * @Validated(validated = false) SVGResource polygon();
	 * 
	 * @Source("icons/svg/tools/polygon/regular_polygon.svg")
	 * 
	 * @Validated(validated = false) SVGResource regular_polygon();
	 * 
	 * @Source("icons/svg/tools/polygon/rigid_polygon.svg")
	 * 
	 * @Validated(validated = false) SVGResource rigid_polygon();
	 * 
	 * @Source("icons/svg/tools/polygon/vector_polygon.svg")
	 * 
	 * @Validated(validated = false) SVGResource vector_polygon();
	 * 
	 * @Source("icons/svg/tools/specialline/angle_bisector.svg")
	 * 
	 * @Validated(validated = false) SVGResource angle_bisector();
	 * 
	 * @Source("icons/svg/tools/specialline/best_fit_line.svg")
	 * 
	 * @Validated(validated = false) SVGResource best_fit_line();
	 * 
	 * @Source("icons/svg/tools/specialline/locus.svg")
	 * 
	 * @Validated(validated = false) SVGResource locus();
	 * 
	 * @Source("icons/svg/tools/specialline/parallel_line.svg")
	 * 
	 * @Validated(validated = false) SVGResource parallel_line();
	 * 
	 * @Source("icons/svg/tools/specialline/perpendicular_bisector.svg")
	 * 
	 * @Validated(validated = false) SVGResource perpendicular_bisector();
	 * 
	 * @Source("icons/svg/tools/specialline/perpendicular_line.svg")
	 * 
	 * @Validated(validated = false) SVGResource perpendicular_line();
	 * 
	 * @Source("icons/svg/tools/specialline/polar_or_diameter_line.svg")
	 * 
	 * @Validated(validated = false) SVGResource polar_or_diameter_line();
	 * 
	 * @Source("icons/svg/tools/specialline/tangents.svg")
	 * 
	 * @Validated(validated = false) SVGResource tangents();
	 * 
	 * @Source("icons/svg/tools/specialobject/freehand_shape.svg")
	 * 
	 * @Validated(validated = false) SVGResource freehand_shape();
	 * 
	 * @Source("icons/svg/tools/specialobject/function_inspector.svg")
	 * 
	 * @Validated(validated = false) SVGResource function_inspector();
	 * 
	 * @Source("icons/svg/tools/specialobject/insert_image.svg")
	 * 
	 * @Validated(validated = false) SVGResource insert_image();
	 * 
	 * @Source("icons/svg/tools/specialobject/insert_text.svg")
	 * 
	 * @Validated(validated = false) SVGResource insert_text();
	 * 
	 * @Source("icons/svg/tools/specialobject/pen.svg")
	 * 
	 * @Validated(validated = false) SVGResource pen();
	 * 
	 * @Source("icons/svg/tools/specialobject/relation_between_two_objects.svg")
	 * 
	 * @Validated(validated = false) SVGResource relation_between_two_objects();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/count.svg")
	 * 
	 * @Validated(validated = false) SVGResource count();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/create_list_of_points.svg")
	 * 
	 * @Validated(validated = false) SVGResource create_list_of_points();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/create_list.svg")
	 * 
	 * @Validated(validated = false) SVGResource create_list();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/create_matrix.svg")
	 * 
	 * @Validated(validated = false) SVGResource create_matrix();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/create_polyline.svg")
	 * 
	 * @Validated(validated = false) SVGResource create_polyline();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/create_table.svg")
	 * 
	 * @Validated(validated = false) SVGResource create_table();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/maximum.svg")
	 * 
	 * @Validated(validated = false) SVGResource maximum();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/mean.svg")
	 * 
	 * @Validated(validated = false) SVGResource mean();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/minimum.svg")
	 * 
	 * @Validated(validated = false) SVGResource minimum();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/multiple_variable_analysis.svg")
	 * 
	 * @Validated(validated = false) SVGResource multiple_variable_analysis();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/one_variable_analysis.svg")
	 * 
	 * @Validated(validated = false) SVGResource one_variable_analysis();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/probability_calculator.svg")
	 * 
	 * @Validated(validated = false) SVGResource probability_calculator();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/sum.svg")
	 * 
	 * @Validated(validated = false) SVGResource sum();
	 * 
	 * @Source("icons/svg/tools/spreadsheet/two_variable_regression_analysis.svg")
	 * 
	 * @Validated(validated = false) SVGResource
	 * two_variable_regression_analysis();
	 * 
	 * @Source("icons/svg/tools/transformation/dilate_object_from_point_by_factor.svg"
	 * )
	 * 
	 * @Validated(validated = false) SVGResource
	 * dilate_object_from_point_by_factor();
	 * 
	 * @Source("icons/svg/tools/transformation/reflect_object_about_circle.svg")
	 * 
	 * @Validated(validated = false) SVGResource reflect_object_about_circle();
	 * 
	 * @Source("icons/svg/tools/transformation/reflect_object_about_line.svg")
	 * 
	 * @Validated(validated = false) SVGResource reflect_object_about_line();
	 * 
	 * @Source("icons/svg/tools/transformation/reflect_object_about_point.svg")
	 * 
	 * @Validated(validated = false) SVGResource reflect_object_about_point();
	 * 
	 * @Source("icons/svg/tools/transformation/rotate_object_about_point_by_angle.svg"
	 * )
	 * 
	 * @Validated(validated = false) SVGResource
	 * rotate_object_about_point_by_angle();
	 * 
	 * @Source("icons/svg/tools/transformation/translate_object_by_vector.svg")
	 * 
	 * @Validated(validated = false) SVGResource translate_object_by_vector();
	 * 
	 * @Source("icons/svg/unsorted/applications-graphics.svg")
	 * 
	 * @Validated(validated = false) SVGResource applicationsGraphics();
	 * 
	 * @Source("icons/svg/unsorted/apply.svg")
	 * 
	 * @Validated(validated = false) SVGResource apply();
	 * 
	 * @Source("icons/svg/unsorted/aux_folder.svg")
	 * 
	 * @Validated(validated = false) SVGResource aux_folder();
	 * 
	 * @Source("icons/svg/unsorted/check.svg")
	 * 
	 * @Validated(validated = false) SVGResource check();
	 * 
	 * @Source("icons/svg/unsorted/corner1.svg")
	 * 
	 * @Validated(validated = false) SVGResource corner1();
	 * 
	 * @Source("icons/svg/unsorted/corner2.svg")
	 * 
	 * @Validated(validated = false) SVGResource corner2();
	 * 
	 * @Source("icons/svg/unsorted/corner4.svg")
	 * 
	 * @Validated(validated = false) SVGResource corner4();
	 * 
	 * @Source("icons/svg/unsorted/cumulative_distribution.svg")
	 * 
	 * @Validated(validated = false) SVGResource cumulative_distribution();
	 * 
	 * @Source("icons/svg/unsorted/empty.svg")
	 * 
	 * @Validated(validated = false) SVGResource empty();
	 * 
	 * @Source("icons/svg/unsorted/euclidian.svg")
	 * 
	 * @Validated(validated = false) SVGResource euclidian();
	 * 
	 * @Source("icons/svg/unsorted/folder.svg")
	 * 
	 * @Validated(validated = false) SVGResource folder();
	 * 
	 * @Source("icons/svg/unsorted/format-text-bold.svg")
	 * 
	 * @Validated(validated = false) SVGResource formatTextBold();
	 * 
	 * @Source("icons/svg/unsorted/format-text-italic.svg")
	 * 
	 * @Validated(validated = false) SVGResource formatTextItalic();
	 * 
	 * @Source("icons/svg/unsorted/globe.svg")
	 * 
	 * @Validated(validated = false) SVGResource globe();
	 * 
	 * @Source("icons/svg/unsorted/hidden.svg")
	 * 
	 * @Validated(validated = false) SVGResource hidden();
	 * 
	 * @Source("icons/svg/unsorted/line_graph.svg")
	 * 
	 * @Validated(validated = false) SVGResource line_graph();
	 * 
	 * @Source("icons/svg/unsorted/list-add.svg")
	 * 
	 * @Validated(validated = false) SVGResource listAdd();
	 * 
	 * @Source("icons/svg/unsorted/lock.svg")
	 * 
	 * @Validated(validated = false) SVGResource lock();
	 * 
	 * @Source("icons/svg/unsorted/options-defaults24.svg")
	 * 
	 * @Validated(validated = false) SVGResource optionsDefaults24();
	 * 
	 * @Source("icons/svg/unsorted/osculating_circle.svg")
	 * 
	 * @Validated(validated = false) SVGResource osculating_circle();
	 * 
	 * @Source("icons/svg/unsorted/paste.svg")
	 * 
	 * @Validated(validated = false) SVGResource paste();
	 * 
	 * @Source("icons/svg/unsorted/perspective.svg")
	 * 
	 * @Validated(validated = false) SVGResource perspective();
	 * 
	 * @Source("icons/svg/unsorted/right_angle.svg")
	 * 
	 * @Validated(validated = false) SVGResource right_angle();
	 * 
	 * @Source("icons/svg/unsorted/separator.svg")
	 * 
	 * @Validated(validated = false) SVGResource separator();
	 * 
	 * @Source("icons/svg/unsorted/shown.svg")
	 * 
	 * @Validated(validated = false) SVGResource shown();
	 * 
	 * @Source("icons/svg/unsorted/tangent_line.svg")
	 * 
	 * @Validated(validated = false) SVGResource tangent_line();
	 * 
	 * @Source("icons/svg/unsorted/text-x-generic.svg")
	 * 
	 * @Validated(validated = false) SVGResource textXGeneric();
	 * 
	 * @Source("icons/svg/unsorted/tree-close.svg")
	 * 
	 * @Validated(validated = false) SVGResource treeClose();
	 * 
	 * @Source("icons/svg/unsorted/tree-open.svg")
	 * 
	 * @Validated(validated = false) SVGResource treeOpen();
	 * 
	 * @Source("icons/svg/unsorted/users.svg")
	 * 
	 * @Validated(validated = false) SVGResource users();
	 * 
	 * @Source("icons/svg/unsorted/wiki.svg")
	 * 
	 * @Validated(validated = false) SVGResource wiki();
	 * 
	 * @Source("icons/svg/unsorted/xy_segments.svg")
	 * 
	 * @Validated(validated = false) SVGResource xy_segments();
	 * 
	 * @Source("icons/svg/unsorted/xy_table.svg")
	 * 
	 * @Validated(validated = false) SVGResource xy_table();
	 * 
	 * @Source("icons/svg/unsorted/zoom16.svg")
	 * 
	 * @Validated(validated = false) SVGResource zoom16();
	 * 
	 * @Source("icons/svg/unsorted/cas/cas-keyboard.svg")
	 * 
	 * @Validated(validated = false) SVGResource casKeyboard();
	 * 
	 * @Source("icons/svg/unsorted/cas/cas.svg")
	 * 
	 * @Validated(validated = false) SVGResource cas();
	 * 
	 * @Source("icons/svg/unsorted/cas/cascopy-dynamic.svg")
	 * 
	 * @Validated(validated = false) SVGResource cascopyDynamic();
	 * 
	 * @Source("icons/svg/unsorted/cas/cascopy-static.svg")
	 * 
	 * @Validated(validated = false) SVGResource cascopyStatic();
	 * 
	 * @Source("icons/svg/unsorted/cursor/cursor_grab.svg")
	 * 
	 * @Validated(validated = false) SVGResource cursor_grab();
	 * 
	 * @Source("icons/svg/unsorted/cursor/cursor_grabbing.svg")
	 * 
	 * @Validated(validated = false) SVGResource cursor_grabbing();
	 * 
	 * @Source("icons/svg/unsorted/cursor/cursor_large_cross.svg")
	 * 
	 * @Validated(validated = false) SVGResource cursor_large_cross();
	 * 
	 * @Source("icons/svg/unsorted/edit/edit-clear.svg")
	 * 
	 * @Validated(validated = false) SVGResource editClear();
	 * 
	 * @Source("icons/svg/unsorted/edit/edit-cut.svg")
	 * 
	 * @Validated(validated = false) SVGResource editCut();
	 * 
	 * @Source("icons/svg/unsorted/edit/edit-paste.svg")
	 * 
	 * @Validated(validated = false) SVGResource editPaste();
	 * 
	 * @Source("icons/svg/unsorted/spreadsheet/header_column.svg")
	 * 
	 * @Validated(validated = false) SVGResource header_column();
	 * 
	 * @Source("icons/svg/unsorted/spreadsheet/header_row.svg")
	 * 
	 * @Validated(validated = false) SVGResource header_row();
	 * 
	 * @Source("icons/svg/unsorted/spreadsheet/spreadsheet_grid.svg")
	 * 
	 * @Validated(validated = false) SVGResource spreadsheet_grid();
	 * 
	 * @Source("icons/svg/unsorted/spreadsheet/spreadsheet.svg")
	 * 
	 * @Validated(validated = false) SVGResource spreadsheet();
	 * 
	 * @Source("icons/svg/unsorted/view/view_btn.svg")
	 * 
	 * @Validated(validated = false) SVGResource view_btn();
	 * 
	 * @Source("icons/svg/unsorted/view/view-close.svg")
	 * 
	 * @Validated(validated = false) SVGResource viewClose();
	 * 
	 * @Source("icons/svg/unsorted/view/view-maximize.svg")
	 * 
	 * @Validated(validated = false) SVGResource viewMaximize();
	 * 
	 * @Source("icons/svg/unsorted/view/view-move.svg")
	 * 
	 * @Validated(validated = false) SVGResource viewMove();
	 * 
	 * @Source("icons/svg/unsorted/view/view-unmaximize.svg")
	 * 
	 * @Validated(validated = false) SVGResource viewUnmaximize();
	 * 
	 * @Source("icons/svg/unsorted/view/view-unwindow.svg")
	 * 
	 * @Validated(validated = false) SVGResource viewUnwindow();
	 * 
	 * @Source("icons/svg/unsorted/view/view-window.svg")
	 * 
	 * @Validated(validated = false) SVGResource viewWindow();
	 */

}
