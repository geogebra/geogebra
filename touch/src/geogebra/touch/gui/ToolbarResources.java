package geogebra.touch.gui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ToolbarResources extends ClientBundle{
	public static final ToolbarResources INSTANCE = GWT.create(ToolbarResources.class);
	
	@Source("icons/png/toolbar/tools_specialline_best_fit_line.png")
	ImageResource best_fit_line();

	@Source("icons/png/toolbar/tools_actionobject_checkbox_show_hide_objects.png")
	ImageResource checkbox_to_show_hide_objects();

	@Source("icons/png/toolbar/tools_circleandarc_circle_through_three_points.png")
	ImageResource circle_through_three_points();

	@Source("icons/png/toolbar/tools_circleandarc_circle_with_center_radius.png")
	ImageResource circle_with_center_and_radius();

	@Source("icons/png/toolbar/tools_circleandarc_circle_with_center_through_point.png")
	ImageResource circle_with_center_through_point();

	@Source("icons/png/toolbar/tools_circleandarc_circular_arc_with_center_between_two_points.png")
	ImageResource circular_arc_with_center_between_two_points();

	@Source("icons/png/toolbar/tools_circleandarc_circular_sector_with_center_between_two_points.png")
	ImageResource circular_sector_with_center_between_two_points();

	@Source("icons/png/toolbar/tools_circleandarc_circumcircular_arc_through_three_points.png")
	ImageResource circumcircular_arc_through_three_points();

	@Source("icons/png/toolbar/tools_circleandarc_circumcircular_sector_through_three_points.png")
	ImageResource circumcircular_sector_through_three_points();
	
	@Source("icons/png/toolbar/tools_measurement_angle.png")
	ImageResource angle();
	
	@Source("icons/png/toolbar/tools_measurement_angle_with_given_size.png")
	ImageResource angle_fixed();

	@Source("icons/png/toolbar/tools_specialline_angle_bisector.png")
	ImageResource angle_bisector();

	@Source("icons/png/toolbar/tools_measurement_area.png")
	ImageResource area();
	
	@Source("icons/png/toolbar/tools_point_attach_detach_point.png")
	ImageResource attach_detach_point();
	
	@Source("icons/png/toolbar/tools_circleandarc_compasses.png")
	ImageResource compasses();

	@Source("icons/png/toolbar/tools_point_complex_number.png")
	ImageResource complex_number();

	@Source("icons/png/toolbar/tools_conicsection_conic_through_5_points.png")
	ImageResource conic_through_5_points();

	@Source("icons/png/toolbar/tools_generaltools_copy_visual_style.png")
	ImageResource copy_visual_style();

	// ToolBar

	@Source("icons/png/toolbar/tools_measurement_create_list.png")
	ImageResource create_list();

	@Source("icons/png/toolbar/tools_generaltools_delete.png")
	ImageResource delete_object();

	@Source("icons/png/toolbar/tools_conicsection_ellipse.png")
	ImageResource ellipse();

	@Source("icons/png/toolbar/tools_cas_evaluate.png")
	ImageResource evaluate();

	@Source("icons/png/toolbar/tools_cas_expand.png")
	ImageResource expand();

	@Source("icons/png/toolbar/tools_cas_factor.png")
	ImageResource factor();
	
	@Source("icons/png/toolbar/tools_cas_derivative.png")
	ImageResource derivative();
	
	@Source("icons/png/toolbar/tools_cas_solve_numerically.png")
	ImageResource nsolve();
	
	@Source("icons/png/toolbar/tools_spreadsheet_two_variable_regression_analysis.png")
	ImageResource two_variable();

	@Source("icons/png/toolbar/tools_specialobject_freehand_shape.png")
	ImageResource freehand_shape();

	@Source("icons/png/toolbar/tools_specialobject_function_inspector.png")
	ImageResource function_inspector();

	@Source("icons/png/toolbar/tools_conicsection_hyperbola.png")
	ImageResource hyperbola();

	@Source("icons/png/toolbar/tools_actionobject_insert_button.png")
	ImageResource insert_button();

	@Source("icons/png/toolbar/tools_specialobject_insert_image.png")
	ImageResource insert_image();

	@Source("icons/png/toolbar/tools_actionobject_insert_input_box.png")
	ImageResource insert_input_box();

	@Source("icons/png/toolbar/tools_specialobject_insert_text.png")
	ImageResource insert_text();

	@Source("icons/png/toolbar/tools_cas_integral.png")
	ImageResource integral();

	@Source("icons/png/toolbar/tools_point_intersect_two_objects.png")
	ImageResource intersect_two_objects();

	@Source("icons/png/toolbar/tools_cas_keep_input.png")
	ImageResource keep_input();

	@Source("icons/png/toolbar/tools_generaltools_label.png")
	ImageResource label();

	@Source("icons/png/toolbar/tools_line_line_through_two_points.png")
	ImageResource line_through_two_points();

	@Source("icons/png/toolbar/tools_specialline_locus.png")
	ImageResource locus();

	@Source("icons/png/toolbar/tools_point_midpoint_or_center.png")
	ImageResource midpoint_or_center();

	@Source("icons/png/toolbar/tools_movement_move.png")
	ImageResource move();

	@Source("icons/png/toolbar/tools_generaltools_move_graphics_view.png")
	ImageResource move_graphics_view();

	@Source("icons/png/toolbar/tools_point_new_point.png")
	ImageResource new_point();

	@Source("icons/png/toolbar/tools_cas_numeric.png")
	ImageResource numeric();

	@Source("icons/png/toolbar/tools_conicsection_parabola.png")
	ImageResource parabola();

	@Source("icons/png/toolbar/tools_specialline_parallel_line.png")
	ImageResource parallel_line();

	@Source("icons/png/toolbar/tools_specialobject_pen.png")
	ImageResource pen();

	@Source("icons/png/toolbar/tools_specialline_perpendicular_bisector.png")
	ImageResource perpendicular_bisector();

	@Source("icons/png/toolbar/tools_specialline_perpendicular_line.png")
	ImageResource perpendicular_line();

	@Source("icons/png/toolbar/tools_point_point_on_object.png")
	ImageResource point_on_object();

	@Source("icons/png/toolbar/tools_specialline_polar_or_diameter_line.png")
	ImageResource polar_or_diameter_line();

	@Source("icons/png/toolbar/tools_polygon_polygon.png")
	ImageResource polygon();

	@Source("icons/png/toolbar/tools_line_polyline_between_points.png")
	ImageResource polyline_between_points();

	@Source("icons/png/toolbar/tools_specialobject_probability_calculator.png")
	ImageResource probability_calculator();
	
	@Source("icons/png/toolbar/tools_line_ray_through_two_points.png")
	ImageResource ray_through_two_points();

	@Source("icons/png/toolbar/tools_movement_record_to_spreadsheet.png")
	ImageResource record_to_spreadsheet();

	@Source("icons/png/toolbar/tools_transformation_reflect_object_about_circle.png")
	ImageResource reflect_object_about_circle();

	@Source("icons/png/toolbar/tools_transformation_reflect_object_about_line.png")
	ImageResource reflect_object_about_line();

	@Source("icons/png/toolbar/tools_transformation_reflect_object_about_point.png")
	ImageResource reflect_object_about_point();

	@Source("icons/png/toolbar/tools_polygon_regular_polygon.png")
	ImageResource regular_polygon();

	@Source("icons/png/toolbar/tools_specialobject_relation_between_two_objects.png")
	ImageResource relation_between_two_objects();

	@Source("icons/png/toolbar/tools_polygon_rigid_polygon.png")
	ImageResource rigid_polygon();

	@Source("icons/png/toolbar/tools_movement_rotate_around_point.png")
	ImageResource rotate_around_point();

	@Source("icons/png/toolbar/tools_transformation_rotate_object_about_point_by_angle.png")
	ImageResource rotate_object_about_point_by_angle();

	@Source("icons/png/toolbar/tools_line_segment_between_two_points.png")
	ImageResource segment_between_two_points();

	@Source("icons/png/toolbar/tools_line_segment_with_given_length_from_point.png")
	ImageResource segment_with_given_length_from_point();

	@Source("icons/png/toolbar/tools_circleandarc_semicircle.png")
	ImageResource semicircle();

	@Source("icons/png/toolbar/tools_generaltools_label.png")
	ImageResource show_hide_label();

	@Source("icons/png/toolbar/tools_generaltools_show_hide_object.png")
	ImageResource show_hide_object();
	
	@Source("icons/png/toolbar/tools_actionobject_slider.png")
	ImageResource slider();

	@Source("icons/png/toolbar/tools_measurement_slope.png")
	ImageResource slope();

	@Source("icons/png/toolbar/tools_cas_solve.png")
	ImageResource solve();

	@Source("icons/png/toolbar/tools_cas_solve_numerically.png")
	ImageResource solve_numerically();

	@Source("icons/png/toolbar/tools_cas_substitute.png")
	ImageResource substitute();

	@Source("icons/png/toolbar/tools_line_vector_between_two_points.png")
	ImageResource vector_between_two_points();

	@Source("icons/png/toolbar/tools_line_vector_from_point.png")
	ImageResource vector_from_point();

	@Source("icons/png/toolbar/tools_polygon_vector_polygon.png")
	ImageResource vector_polygon();

	@Source("icons/png/toolbar/tools_generaltools_zoom_in.png")
	ImageResource zoom_in();

	@Source("icons/png/toolbar/tools_generaltools_zoom_out.png")
	ImageResource zoom_out();

	@Source("icons/png/toolbar/tools_specialline_tangents.png")
	ImageResource tangents();

	@Source("icons/png/toolbar/tools_transformation_translate_object_by_vector.png")
	ImageResource translate_object_by_vector();
	
	@Source("icons/png/toolbar/tools_transformation_dilate_object_from_point_by_factor.png")
	ImageResource dilate_object_from_point_by_factor();

	@Source("icons/png/toolbar/tools_measurement_distance_or_length.png")
	ImageResource distance_or_length();
	
	@Source("icons/png/toolbar/tools_spreadsheet_create_polyline.png")
	ImageResource create_polyline();
	
	@Source("icons/png/toolbar/tools_spreadsheet_create_matrix.png")
	ImageResource create_matrix();
	
	@Source("icons/png/toolbar/tools_spreadsheet_create_list_of_points.png")
	ImageResource create_point_list();

	@Source("icons/png/toolbar/tools_spreadsheet_create_table.png")
	ImageResource create_table();

	@Source("icons/png/toolbar/tools_spreadsheet_maximum.png")
	ImageResource max();
	
	@Source("icons/png/toolbar/tools_spreadsheet_minimum.png")
	ImageResource min();
	
	@Source("icons/png/toolbar/tools_spreadsheet_mean.png")
	ImageResource mean();
	
	@Source("icons/png/toolbar/tools_spreadsheet_count.png")
	ImageResource count();
	
	@Source("icons/png/toolbar/tools_spreadsheet_sum.png")
	ImageResource sum();
	
	@Source("icons/png/toolbar/tools_spreadsheet_multiple_variable_analysis.png")
	ImageResource multiple_variable();
	
	@Source("icons/png/toolbar/tools_spreadsheet_one_variable_analysis.png")
	ImageResource one_variable();

}
