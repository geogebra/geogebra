package geogebra.mobile.gui;

import geogebra.mobile.gui.elements.AlgebraViewPanel;
import geogebra.mobile.gui.elements.EuclidianViewPanel;
import geogebra.mobile.gui.elements.toolbar.ToolButton;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;

public class IconTestGUI implements GeoGebraMobileGUI
{
	private RootPanel rootPanel;

	public IconTestGUI()
	{

		// set viewport and other settings for mobile
		MGWT.applySettings(MGWTSettings.getAppSetting());

		// this will create a link element at the end of head
		MGWTStyle.getTheme().getMGWTClientBundle().getMainCss().ensureInjected();

		// append your own css as last thing in the head
		MGWTStyle.injectStyleSheet("TabletGUI.css");

		this.rootPanel = RootPanel.get();
		
//		for (ResourcePrototype r : CommonResourcesIconTestIconTest.INSTANCE.getResources()));)
//		{
//			r.getName()));;
//			this.rootPanel.add(new ToolButton(new ToolButton(((SVGResource) r)));
//		}
		

		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.about_license()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.absolute_position_on_screen()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.advanced()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.algebra()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.align_center()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.align_left()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.align_right()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.angle()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.angle_bisector()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.angle_with_given_size()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.area()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.attach_detach_point()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.auxiliary_objects()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.best_fit_line()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.cas()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.cAS()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.cascopyDynamic()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.cascopyStatic()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.casKeyboard()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.checkbox_to_show_hide_objects()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.circle_through_three_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.circle_with_center_and_radius()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.circle_with_center_through_point()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.circular_arc_with_center_between_two_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.circular_sector_with_center_between_two_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.circumcircular_arc_through_three_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.circumcircular_sector_through_three_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.close()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.compasses()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.complex_number()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.conic_through_5_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.construction_protocol()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.copy_visual_style()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.corner1()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.corner2()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.corner4()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.count()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.create_list()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.create_list_of_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.create_matrix()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.create_new_tool()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.create_polyline()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.create_table()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.delete()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.delete_object()));
//		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.derivative()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.dilate_object_from_point_by_factor()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.distance_or_length()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.ellipse()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.evaluate()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.expand()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.export_dynamix_worksheet_as_webpage()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.export_graphics_view_as_picture()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.export_graphics_view_to_clipboard()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.factor()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.font_size()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.freehand_shape()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.function_inspector()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.geogebra_tube()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.geogebraLogo()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.graphics()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.graphics2()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.help()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.hyperbola()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.insert_button()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.insert_image()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.insert_input_box()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.insert_text()));
//		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.integral()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.intersect_two_objects()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.keep_input()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.keyboard()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.labelling()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.layout()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.line_through_two_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.locus()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.manage_tools()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.maximum()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.mean()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.midpoint_or_center()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.minimum()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.move()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.move_graphics_view()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.multiple_variable_analysis()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.new_point()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.new_window()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.numeric()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.object_properties()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.one_variable_analysis()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.open()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.open_webpage()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.parabola()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.parallel_line()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.pen()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.perpendicular_bisector()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.perpendicular_line()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.point_capturing()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.point_on_object()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.polar_or_diameter_line()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.polygon()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.polyline_between_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.print_preview()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.probability_calculator()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.properties_advanced()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.properties_defaults()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.properties_graphics()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.properties_layout()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.properties_object()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.ray_through_two_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.record_to_spreadsheet()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.redo()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.reflect_object_about_circle()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.reflect_object_about_line()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.reflect_object_about_point()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.refresh_view()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.regular_polygon()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.relation_between_two_objects()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.rigid_polygon()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.rotate_around_point()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.rotate_object_about_point_by_angle()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.save()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.save_settings()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.segment_between_two_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.segment_with_given_length_from_point()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.semicircle()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.set_border_all()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.set_border_buttom()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.set_border_frame()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.set_border_inside()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.set_border_left()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.set_border_none()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.set_border_right()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.set_border_top()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.set_point_capture_style()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.share()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.show_hide_label()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.show_hide_object()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.show_input_bar()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.show_or_hide_the_axes()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.show_or_hide_the_grid()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.slider()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.slope()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.solve()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.solve_numerically()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.sort_objects_by()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.spreadsheet()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.substitute()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.sum()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.tangents()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.translate_object_by_vector()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.two_variable_regression_analysis()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.undo()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.vector_between_two_points()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.vector_from_point()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.vector_polygon()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.viewMaximize()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.viewUnmaximize()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.xy_segments()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.xy_table()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.zoom_in()));
		this.rootPanel.add(new ToolButton(CommonResourcesIconTest.INSTANCE.zoom_out()));
	}

	@Override
	public EuclidianViewPanel getEuclidianViewPanel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
