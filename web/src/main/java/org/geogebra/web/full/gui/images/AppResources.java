package org.geogebra.web.full.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

@SuppressWarnings("javadoc")
public interface AppResources extends ClientBundle {
	
	AppResources INSTANCE = GWT.create(AppResources.class);

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options.png")
	ImageResource view_properties16();

	@Source("org/geogebra/common/icons/png/web/general/options-layout24.png")
	ImageResource options_layout24();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-pin.png")
	ImageResource pin();

	@Source("org/geogebra/common/icons/png/web/general/geogebra32.png")
	ImageResource geogebraLogo();

	@Source("org/geogebra/common/icons/png/web/general/aux_folder.gif")
	ImageResource aux_folder();

	@Source("org/geogebra/common/icons/png/web/general/color_chooser_check.png")
	ImageResource color_chooser_check();

	@Source("org/geogebra/common/icons/png/web/general/corner1.png")
	ImageResource corner1();

	@Source("org/geogebra/common/icons/png/web/general/corner2.png")
	ImageResource corner2();

	@Source("org/geogebra/common/icons/png/web/general/corner4.png")
	ImageResource corner4();

	@Source("org/geogebra/common/icons/png16x16/cumulative_distribution.png")
	ImageResource cumulative_distribution();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-delete.png")
	ImageResource delete_small();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-cut.png")
	ImageResource edit_cut();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-paste.png")
	ImageResource edit_paste();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit.png")
	ImageResource edit();

	@Source("org/geogebra/common/icons/png/web/general/empty.gif")
	ImageResource empty();

	@Source("org/geogebra/common/icons/png/web/general/export.png")
	ImageResource export();

	@Source("org/geogebra/common/icons/png/web/general/geogebra.png")
	ImageResource geogebra();

	@Source("org/geogebra/common/icons/png/web/general/geogebra64.png")
	ImageResource geogebra64();

	@Source("org/geogebra/common/icons/png/web/general/header_column.png")
	ImageResource header_column();

	@Source("org/geogebra/common/icons/png/web/general/algebra_hidden.png")
	ImageResource hidden();

	@Source("org/geogebra/common/icons/png/web/general/line_graph.png")
	ImageResource line_graph();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_object_fixed.png")
	ImageResource objectFixed();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options-labeling.png")
	ImageResource mode_showhidelabel_16();

	@Source("org/geogebra/common/icons/png/web/menu_icons/mode_showhideobject.png")
	ImageResource mode_showhideobject_16();

	@Source("org/geogebra/common/icons/png/web/general/options-defaults224.png")
	ImageResource options_defaults224();

	@Source("org/geogebra/common/icons/png/web/general/osculating_circle.png")
	ImageResource osculating_circle();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-edit-rename.png")
	ImageResource rename();

	@Source("org/geogebra/common/icons/png/web/general/algebra_shown.png")
	ImageResource shown();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-record-to-spreadsheet.png")
	ImageResource spreadsheettrace();

	@Source("org/geogebra/common/icons/png/web/general/table.gif")
	ImageResource table();

	@Source("org/geogebra/common/icons/png/web/general/tangent_line.png")
	ImageResource tangent_line();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-trace-on.png")
	ImageResource trace_on();

	@Source("org/geogebra/common/icons/png/web/general/arrow_dockbar_triangle_down.png")
	ImageResource triangle_down();

	@Source("org/geogebra/common/icons/png/web/general/xy_segments.png")
	ImageResource xy_segments();

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-file-open.png")
	ImageResource zoom16();

	@Source("org/geogebra/common/icons/png16x16/step_graph.png")
	ImageResource step_graph();

	@Source("org/geogebra/common/icons/png16x16/bar_graph.png")
	ImageResource bar_graph();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_dots.png")
	ImageResource dots();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylebar_more.png")
	ImageResource more();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_dots_active.png")
	ImageResource dots_active();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_statistics.png")
	ImageResource dataview_showstatistics();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_data.png")
	ImageResource dataview_showdata();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_2nd_plot.png")
	ImageResource dataview_showplot2();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylebar_angle_interval.png")
	ImageResource stylingbar_angle_interval();

	@Source("org/geogebra/common/icons_view_perspectives/p24/menu_view_algebra.png")
	ImageResource options_algebra24();
	
	/*
	 * NEW ICONS USING MATERIAL DESIGN GUIDELINES
	 * 
	 * icon color = #000000 + active opacity: 0.54, inactive opacity: 0.26
	 */

	@Source("org/geogebra/common/icons/png/web/context_menu_20/angle.png")
	ImageResource angle20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/animation.png")
	ImageResource animation20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/copy.png")
	ImageResource copy20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/cut.png")
	ImageResource cut20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/delete.png")
	ImageResource delete20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/duplicate.png")
	ImageResource duplicate20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/label.png")
	ImageResource label20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/label_off.png")
	ImageResource label_off20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/lock.png")
	ImageResource lock20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/paste.png")
	ImageResource paste20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/pin.png")
	ImageResource pin20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/unpin.png")
	ImageResource unpin20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/properties.png")
	ImageResource properties20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/record_to_spreadsheet.png")
	ImageResource record_to_spreadsheet20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/rename.png")
	ImageResource rename20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/trace.png")
	ImageResource trace20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/trace_off.png")
	ImageResource trace_off20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/unlock.png")
	ImageResource unlock20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/zoom.png")
	ImageResource zoom20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/standard_view.png")
	ImageResource standard_view20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/show_all_objects.png")
	ImageResource show_all_objects20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/grid.png")
	ImageResource grid20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/axes.png")
	ImageResource axes20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/plane.png")
	ImageResource plane20();

	@Source("org/geogebra/common/icons/png/web/context_menu_20/edit.png")
	ImageResource edit20();

}
