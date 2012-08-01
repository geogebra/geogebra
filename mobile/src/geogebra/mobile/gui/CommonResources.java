package geogebra.mobile.gui;

import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.ui.SVGResource.Validated;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface CommonResources extends ClientBundle
{

	public static CommonResources INSTANCE = GWT.create(CommonResources.class);

	@Source("icons/svg/Tools/actionobject/checkbox_to_show_hide_objects.svg")
	@Validated(validated = false)
	SVGResource checkbox_to_show_hide_objects();

	@Source("icons/svg/Tools/actionobject/insert_button.svg")
	@Validated(validated = false)
	SVGResource insert_button();

	@Source("icons/svg/Tools/actionobject/insert_input_box.svg")
	@Validated(validated = false)
	SVGResource insert_input_box();

	@Source("icons/svg/Tools/actionobject/slider.svg")
	@Validated(validated = false)
	SVGResource slider();

	@Source("icons/svg/Tools/cas/derivative.svg")
	@Validated(validated = false)
	SVGResource derivative();

	@Source("icons/svg/Tools/cas/evaluate.svg")
	@Validated(validated = false)
	SVGResource evaluate();

	@Source("icons/svg/Tools/cas/expand.svg")
	@Validated(validated = false)
	SVGResource expand();

	@Source("icons/svg/Tools/cas/factor.svg")
	@Validated(validated = false)
	SVGResource factor();

	@Source("icons/svg/Tools/cas/integral.svg")
	@Validated(validated = false)
	SVGResource integral();

	@Source("icons/svg/Tools/cas/keep_input.svg")
	@Validated(validated = false)
	SVGResource keep_input();

	@Source("icons/svg/Tools/cas/numeric.svg")
	@Validated(validated = false)
	SVGResource numeric();

	@Source("icons/svg/Tools/cas/solve_numerically.svg")
	@Validated(validated = false)
	SVGResource solve_numerically();

	@Source("icons/svg/Tools/cas/solve.svg")
	@Validated(validated = false)
	SVGResource solve();

	@Source("icons/svg/Tools/cas/substitute.svg")
	@Validated(validated = false)
	SVGResource substitute();

	@Source("icons/svg/Tools/circleandarc/circle_through_three_points.svg")
	@Validated(validated = false)
	SVGResource circle_through_three_points();

	@Source("icons/svg/Tools/circleandarc/circle_with_center_and_radius.svg")
	@Validated(validated = false)
	SVGResource circle_with_center_and_radius();

	@Source("icons/svg/Tools/circleandarc/circle_with_center_through_point.svg")
	@Validated(validated = false)
	SVGResource circle_with_center_through_point();

	@Source("icons/svg/Tools/circleandarc/circular_arc_with_center_between_two_points.svg")
	@Validated(validated = false)
	SVGResource circular_arc_with_center_between_two_points();

	@Source("icons/svg/Tools/circleandarc/circular_sector_with_center_between_two_points.svg")
	@Validated(validated = false)
	SVGResource circular_sector_with_center_between_two_points();

	@Source("icons/svg/Tools/circleandarc/circumcircular_arc_through_three_points.svg")
	@Validated(validated = false)
	SVGResource circumcircular_arc_through_three_points();

	@Source("icons/svg/Tools/circleandarc/circumcircular_sector_through_three_points.svg")
	@Validated(validated = false)
	SVGResource circumcircular_sector_through_three_points();

	@Source("icons/svg/Tools/circleandarc/compasses.svg")
	@Validated(validated = false)
	SVGResource compasses();

	@Source("icons/svg/Tools/circleandarc/semicircle.svg")
	@Validated(validated = false)
	SVGResource semicircle();

	@Source("icons/svg/Tools/conicsection/conic_through_5_points.svg")
	@Validated(validated = false)
	SVGResource conic_through_5_points();

	@Source("icons/svg/Tools/conicsection/ellipse.svg")
	@Validated(validated = false)
	SVGResource ellipse();

	@Source("icons/svg/Tools/conicsection/hyperbola.svg")
	@Validated(validated = false)
	SVGResource hyperbola();

	@Source("icons/svg/Tools/conicsection/parabola.svg")
	@Validated(validated = false)
	SVGResource parabola();

	@Source("icons/svg/Tools/generaltools/copy_visual_style.svg")
	@Validated(validated = false)
	SVGResource copy_visual_style();

	@Source("icons/svg/Tools/generaltools/move_graphics_view.svg")
	@Validated(validated = false)
	SVGResource move_graphics_view();

	@Source("icons/svg/Tools/generaltools/show_hide_label.svg")
	@Validated(validated = false)
	SVGResource show_hide_label();

	@Source("icons/svg/Tools/generaltools/show_hide_object.svg")
	@Validated(validated = false)
	SVGResource show_hide_object();

	@Source("icons/svg/Tools/generaltools/zoom_in.svg")
	@Validated(validated = false)
	SVGResource zoom_in();

	@Source("icons/svg/Tools/generaltools/zoom_out.svg")
	@Validated(validated = false)
	SVGResource zoom_out();

	@Source("icons/svg/Tools/line/line_through_two_points.svg")
	@Validated(validated = false)
	SVGResource line_through_two_points();

	@Source("icons/svg/Tools/line/polyline_between_points.svg")
	@Validated(validated = false)
	SVGResource polyline_between_points();

	@Source("icons/svg/Tools/line/ray_through_two_points.svg")
	@Validated(validated = false)
	SVGResource ray_through_two_points();

	@Source("icons/svg/Tools/line/segment_between_two_points.svg")
	@Validated(validated = false)
	SVGResource segment_between_two_points();

	@Source("icons/svg/Tools/line/segment_with_given_length_from_point.svg")
	@Validated(validated = false)
	SVGResource segment_with_given_length_from_point();

	@Source("icons/svg/Tools/line/vector_between_two_points.svg")
	@Validated(validated = false)
	SVGResource vector_between_two_points();

	@Source("icons/svg/Tools/line/vector_from_point.svg")
	@Validated(validated = false)
	SVGResource vector_from_point();

	@Source("icons/svg/Tools/measurement/angle_with_given_size.svg")
	@Validated(validated = false)
	SVGResource angle_with_given_size();

	@Source("icons/svg/Tools/measurement/angle.svg")
	@Validated(validated = false)
	SVGResource angle();

	@Source("icons/svg/Tools/measurement/area.svg")
	@Validated(validated = false)
	SVGResource area();

	@Source("icons/svg/Tools/measurement/create_list.svg")
	@Validated(validated = false)
	SVGResource create_list();

	@Source("icons/svg/Tools/measurement/distance_or_length.svg")
	@Validated(validated = false)
	SVGResource distance_or_length();

	@Source("icons/svg/Tools/measurement/slope.svg")
	@Validated(validated = false)
	SVGResource slope();

	@Source("icons/svg/Tools/movement/move.svg")
	@Validated(validated = false)
	SVGResource move();

	@Source("icons/svg/Tools/movement/record_to_spreadsheet.svg")
	@Validated(validated = false)
	SVGResource record_to_spreadsheet();

	@Source("icons/svg/Tools/movement/rotate_around_point.svg")
	@Validated(validated = false)
	SVGResource rotate_around_point();

	@Source("icons/svg/Tools/point/attach_detach_point.svg")
	@Validated(validated = false)
	SVGResource attach_detach_point();

	@Source("icons/svg/Tools/point/complex_number.svg")
	@Validated(validated = false)
	SVGResource complex_number();

	@Source("icons/svg/Tools/point/intersect_two_objects.svg")
	@Validated(validated = false)
	SVGResource intersect_two_objects();

	@Source("icons/svg/Tools/point/midpoint_or_center.svg")
	@Validated(validated = false)
	SVGResource midpoint_or_center();

	@Source("icons/svg/Tools/point/new_point.svg")
	@Validated(validated = false)
	SVGResource new_point();

	@Source("icons/svg/Tools/point/point_on_object.svg")
	@Validated(validated = false)
	SVGResource point_on_object();

	@Source("icons/svg/Tools/polygon/polygon.svg")
	@Validated(validated = false)
	SVGResource polygon();

	@Source("icons/svg/Tools/polygon/regular_polygon.svg")
	@Validated(validated = false)
	SVGResource regular_polygon();

	@Source("icons/svg/Tools/polygon/rigid_polygon.svg")
	@Validated(validated = false)
	SVGResource rigid_polygon();

	@Source("icons/svg/Tools/polygon/vector_polygon.svg")
	@Validated(validated = false)
	SVGResource vector_polygon();

	@Source("icons/svg/Tools/specialline/angle_bisector.svg")
	@Validated(validated = false)
	SVGResource angle_bisector();

	@Source("icons/svg/Tools/specialline/best_fit_line.svg")
	@Validated(validated = false)
	SVGResource best_fit_line();

	@Source("icons/svg/Tools/specialline/locus.svg")
	@Validated(validated = false)
	SVGResource locus();

	@Source("icons/svg/Tools/specialline/parallel_line.svg")
	@Validated(validated = false)
	SVGResource parallel_line();

	@Source("icons/svg/Tools/specialline/perpendicular_bisector.svg")
	@Validated(validated = false)
	SVGResource perpendicular_bisector();

	@Source("icons/svg/Tools/specialline/perpendicular_line.svg")
	@Validated(validated = false)
	SVGResource perpendicular_line();

	@Source("icons/svg/Tools/specialline/polar_or_diameter_line.svg")
	@Validated(validated = false)
	SVGResource polar_or_diameter_line();

	@Source("icons/svg/Tools/specialline/tangents.svg")
	@Validated(validated = false)
	SVGResource tangents();

	@Source("icons/svg/Tools/specialobject/freehand_shape.svg")
	@Validated(validated = false)
	SVGResource freehand_shape();

	@Source("icons/svg/Tools/specialobject/function_inspector.svg")
	@Validated(validated = false)
	SVGResource function_inspector();

	@Source("icons/svg/Tools/specialobject/insert_image.svg")
	@Validated(validated = false)
	SVGResource insert_image();

	@Source("icons/svg/Tools/specialobject/insert_text.svg")
	@Validated(validated = false)
	SVGResource insert_text();

	@Source("icons/svg/Tools/specialobject/pen.svg")
	@Validated(validated = false)
	SVGResource pen();

	@Source("icons/svg/Tools/specialobject/probability_calculator.svg")
	@Validated(validated = false)
	SVGResource probability_calculator();

	@Source("icons/svg/Tools/specialobject/relation_between_two_objects.svg")
	@Validated(validated = false)
	SVGResource relation_between_two_objects();

	@Source("icons/svg/Tools/transformation/dilate_object_from_point_by_factor.svg")
	@Validated(validated = false)
	SVGResource dilate_object_from_point_by_factor();

	@Source("icons/svg/Tools/transformation/reflect_object_about_circle.svg")
	@Validated(validated = false)
	SVGResource reflect_object_about_circle();

	@Source("icons/svg/Tools/transformation/reflect_object_about_line.svg")
	@Validated(validated = false)
	SVGResource reflect_object_about_line();

	@Source("icons/svg/Tools/transformation/reflect_object_about_point.svg")
	@Validated(validated = false)
	SVGResource reflect_object_about_point();

	@Source("icons/svg/Tools/transformation/rotate_object_about_point_by_angle.svg")
	@Validated(validated = false)
	SVGResource rotate_object_about_point_by_angle();

	@Source("icons/svg/Tools/transformation/translate_object_by_vector.svg")
	@Validated(validated = false)
	SVGResource translate_object_by_vector();

	@Source("icons/svg/Tools/generaltools/delete_object.svg")
	@Validated(validated = false)
	SVGResource delete_object();

}
