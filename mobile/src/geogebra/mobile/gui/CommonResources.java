package geogebra.mobile.gui;

import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.ui.SVGResource.Validated;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface CommonResources extends ClientBundle
{

	public static CommonResources INSTANCE = GWT.create(CommonResources.class);

	@Source("icons/svg/tools/actionobject/checkbox_to_show_hide_objects.svg")
	@Validated(validated = false)
	SVGResource checkbox_to_show_hide_objects();

	@Source("icons/svg/tools/actionobject/insert_button.svg")
	@Validated(validated = false)
	SVGResource insert_button();

	@Source("icons/svg/tools/actionobject/insert_input_box.svg")
	@Validated(validated = false)
	SVGResource insert_input_box();

	@Source("icons/svg/tools/actionobject/slider.svg")
	@Validated(validated = false)
	SVGResource slider();

	@Source("icons/svg/tools/cas/derivative.svg")
	@Validated(validated = false)
	SVGResource derivative();

	@Source("icons/svg/tools/cas/evaluate.svg")
	@Validated(validated = false)
	SVGResource evaluate();

	@Source("icons/svg/tools/cas/expand.svg")
	@Validated(validated = false)
	SVGResource expand();

	@Source("icons/svg/tools/cas/factor.svg")
	@Validated(validated = false)
	SVGResource factor();

	@Source("icons/svg/tools/cas/integral.svg")
	@Validated(validated = false)
	SVGResource integral();

	@Source("icons/svg/tools/cas/keep_input.svg")
	@Validated(validated = false)
	SVGResource keep_input();

	@Source("icons/svg/tools/cas/numeric.svg")
	@Validated(validated = false)
	SVGResource numeric();

	@Source("icons/svg/tools/cas/solve_numerically.svg")
	@Validated(validated = false)
	SVGResource solve_numerically();

	@Source("icons/svg/tools/cas/solve.svg")
	@Validated(validated = false)
	SVGResource solve();

	@Source("icons/svg/tools/cas/substitute.svg")
	@Validated(validated = false)
	SVGResource substitute();

	@Source("icons/svg/tools/circleandarc/circle_through_three_points.svg")
	@Validated(validated = false)
	SVGResource circle_through_three_points();

	@Source("icons/svg/tools/circleandarc/circle_with_center_and_radius.svg")
	@Validated(validated = false)
	SVGResource circle_with_center_and_radius();

	@Source("icons/svg/tools/circleandarc/circle_with_center_through_point.svg")
	@Validated(validated = false)
	SVGResource circle_with_center_through_point();

	@Source("icons/svg/tools/circleandarc/circular_arc_with_center_between_two_points.svg")
	@Validated(validated = false)
	SVGResource circular_arc_with_center_between_two_points();

	@Source("icons/svg/tools/circleandarc/circular_sector_with_center_between_two_points.svg")
	@Validated(validated = false)
	SVGResource circular_sector_with_center_between_two_points();

	@Source("icons/svg/tools/circleandarc/circumcircular_arc_through_three_points.svg")
	@Validated(validated = false)
	SVGResource circumcircular_arc_through_three_points();

	@Source("icons/svg/tools/circleandarc/circumcircular_sector_through_three_points.svg")
	@Validated(validated = false)
	SVGResource circumcircular_sector_through_three_points();

	@Source("icons/svg/tools/circleandarc/compasses.svg")
	@Validated(validated = false)
	SVGResource compasses();

	@Source("icons/svg/tools/circleandarc/semicircle.svg")
	@Validated(validated = false)
	SVGResource semicircle();

	@Source("icons/svg/tools/conicsection/conic_through_5_points.svg")
	@Validated(validated = false)
	SVGResource conic_through_5_points();

	@Source("icons/svg/tools/conicsection/ellipse.svg")
	@Validated(validated = false)
	SVGResource ellipse();

	@Source("icons/svg/tools/conicsection/hyperbola.svg")
	@Validated(validated = false)
	SVGResource hyperbola();

	@Source("icons/svg/tools/conicsection/parabola.svg")
	@Validated(validated = false)
	SVGResource parabola();

	@Source("icons/svg/tools/generaltools/copy_visual_style.svg")
	@Validated(validated = false)
	SVGResource copy_visual_style();

	@Source("icons/svg/tools/generaltools/move_graphics_view.svg")
	@Validated(validated = false)
	SVGResource move_graphics_view();

	@Source("icons/svg/tools/generaltools/show_hide_label.svg")
	@Validated(validated = false)
	SVGResource show_hide_label();

	@Source("icons/svg/tools/generaltools/show_hide_object.svg")
	@Validated(validated = false)
	SVGResource show_hide_object();

	@Source("icons/svg/tools/generaltools/zoom_in.svg")
	@Validated(validated = false)
	SVGResource zoom_in();

	@Source("icons/svg/tools/generaltools/zoom_out.svg")
	@Validated(validated = false)
	SVGResource zoom_out();

	@Source("icons/svg/tools/line/line_through_two_points.svg")
	@Validated(validated = false)
	SVGResource line_through_two_points();

	@Source("icons/svg/tools/line/polyline_between_points.svg")
	@Validated(validated = false)
	SVGResource polyline_between_points();

	@Source("icons/svg/tools/line/ray_through_two_points.svg")
	@Validated(validated = false)
	SVGResource ray_through_two_points();

	@Source("icons/svg/tools/line/segment_between_two_points.svg")
	@Validated(validated = false)
	SVGResource segment_between_two_points();

	@Source("icons/svg/tools/line/segment_with_given_length_from_point.svg")
	@Validated(validated = false)
	SVGResource segment_with_given_length_from_point();

	@Source("icons/svg/tools/line/vector_between_two_points.svg")
	@Validated(validated = false)
	SVGResource vector_between_two_points();

	@Source("icons/svg/tools/line/vector_from_point.svg")
	@Validated(validated = false)
	SVGResource vector_from_point();

	@Source("icons/svg/tools/measurement/angle_with_given_size.svg")
	@Validated(validated = false)
	SVGResource angle_with_given_size();

	@Source("icons/svg/tools/measurement/angle.svg")
	@Validated(validated = false)
	SVGResource angle();

	@Source("icons/svg/tools/measurement/area.svg")
	@Validated(validated = false)
	SVGResource area();

	@Source("icons/svg/tools/measurement/create_list.svg")
	@Validated(validated = false)
	SVGResource create_list();

	@Source("icons/svg/tools/measurement/distance_or_length.svg")
	@Validated(validated = false)
	SVGResource distance_or_length();

	@Source("icons/svg/tools/measurement/slope.svg")
	@Validated(validated = false)
	SVGResource slope();

	@Source("icons/svg/tools/movement/move.svg")
	@Validated(validated = false)
	SVGResource move();

	@Source("icons/svg/tools/movement/record_to_spreadsheet.svg")
	@Validated(validated = false)
	SVGResource record_to_spreadsheet();

	@Source("icons/svg/tools/movement/rotate_around_point.svg")
	@Validated(validated = false)
	SVGResource rotate_around_point();

	@Source("icons/svg/tools/point/attach_detach_point.svg")
	@Validated(validated = false)
	SVGResource attach_detach_point();

	@Source("icons/svg/tools/point/complex_number.svg")
	@Validated(validated = false)
	SVGResource complex_number();

	@Source("icons/svg/tools/point/intersect_two_objects.svg")
	@Validated(validated = false)
	SVGResource intersect_two_objects();

	@Source("icons/svg/tools/point/midpoint_or_center.svg")
	@Validated(validated = false)
	SVGResource midpoint_or_center();

	@Source("icons/svg/tools/point/new_point.svg")
	@Validated(validated = false)
	SVGResource new_point();

	@Source("icons/svg/tools/point/point_on_object.svg")
	@Validated(validated = false)
	SVGResource point_on_object();

	@Source("icons/svg/tools/polygon/polygon.svg")
	@Validated(validated = false)
	SVGResource polygon();

	@Source("icons/svg/tools/polygon/regular_polygon.svg")
	@Validated(validated = false)
	SVGResource regular_polygon();

	@Source("icons/svg/tools/polygon/rigid_polygon.svg")
	@Validated(validated = false)
	SVGResource rigid_polygon();

	@Source("icons/svg/tools/polygon/vector_polygon.svg")
	@Validated(validated = false)
	SVGResource vector_polygon();

	@Source("icons/svg/tools/specialline/angle_bisector.svg")
	@Validated(validated = false)
	SVGResource angle_bisector();

	@Source("icons/svg/tools/specialline/best_fit_line.svg")
	@Validated(validated = false)
	SVGResource best_fit_line();

	@Source("icons/svg/tools/specialline/locus.svg")
	@Validated(validated = false)
	SVGResource locus();

	@Source("icons/svg/tools/specialline/parallel_line.svg")
	@Validated(validated = false)
	SVGResource parallel_line();

	@Source("icons/svg/tools/specialline/perpendicular_bisector.svg")
	@Validated(validated = false)
	SVGResource perpendicular_bisector();

	@Source("icons/svg/tools/specialline/perpendicular_line.svg")
	@Validated(validated = false)
	SVGResource perpendicular_line();

	@Source("icons/svg/tools/specialline/polar_or_diameter_line.svg")
	@Validated(validated = false)
	SVGResource polar_or_diameter_line();

	@Source("icons/svg/tools/specialline/tangents.svg")
	@Validated(validated = false)
	SVGResource tangents();

	@Source("icons/svg/tools/specialobject/freehand_shape.svg")
	@Validated(validated = false)
	SVGResource freehand_shape();

	@Source("icons/svg/tools/specialobject/function_inspector.svg")
	@Validated(validated = false)
	SVGResource function_inspector();

	@Source("icons/svg/tools/specialobject/insert_image.svg")
	@Validated(validated = false)
	SVGResource insert_image();

	@Source("icons/svg/tools/specialobject/insert_text.svg")
	@Validated(validated = false)
	SVGResource insert_text();

	@Source("icons/svg/tools/specialobject/pen.svg")
	@Validated(validated = false)
	SVGResource pen();

	@Source("icons/svg/tools/specialobject/probability_calculator.svg")
	@Validated(validated = false)
	SVGResource probability_calculator();

	@Source("icons/svg/tools/specialobject/relation_between_two_objects.svg")
	@Validated(validated = false)
	SVGResource relation_between_two_objects();

	@Source("icons/svg/tools/transformation/dilate_object_from_point_by_factor.svg")
	@Validated(validated = false)
	SVGResource dilate_object_from_point_by_factor();

	@Source("icons/svg/tools/transformation/reflect_object_about_circle.svg")
	@Validated(validated = false)
	SVGResource reflect_object_about_circle();

	@Source("icons/svg/tools/transformation/reflect_object_about_line.svg")
	@Validated(validated = false)
	SVGResource reflect_object_about_line();

	@Source("icons/svg/tools/transformation/reflect_object_about_point.svg")
	@Validated(validated = false)
	SVGResource reflect_object_about_point();

	@Source("icons/svg/tools/transformation/rotate_object_about_point_by_angle.svg")
	@Validated(validated = false)
	SVGResource rotate_object_about_point_by_angle();

	@Source("icons/svg/tools/transformation/translate_object_by_vector.svg")
	@Validated(validated = false)
	SVGResource translate_object_by_vector();

	@Source("icons/svg/tools/generaltools/delete_object.svg")
	@Validated(validated = false)
	SVGResource delete_object();

}
