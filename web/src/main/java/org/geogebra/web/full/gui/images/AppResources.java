package org.geogebra.web.full.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

@SuppressWarnings("javadoc")
public interface AppResources extends ClientBundle {
	
	AppResources INSTANCE = GWT.create(AppResources.class);

	@Source("org/geogebra/common/icons/png/web/general/options-layout24.png")
	ImageResource options_layout24();

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

	@Source("org/geogebra/common/icons/png/web/menu_icons/menu-options-labeling.png")
	ImageResource mode_showhidelabel_16();

	@Source("org/geogebra/common/icons/png/web/general/options-defaults224.png")
	ImageResource options_defaults224();

	@Source("org/geogebra/common/icons/png/web/general/osculating_circle.png")
	ImageResource osculating_circle();

	@Source("org/geogebra/common/icons/png/web/general/algebra_shown.png")
	ImageResource shown();

	@Source("org/geogebra/common/icons/png/web/general/table.gif")
	ImageResource table();

	@Source("org/geogebra/common/icons/png/web/general/tangent_line.png")
	ImageResource tangent_line();

	@Source("org/geogebra/common/icons/png/web/general/arrow_dockbar_triangle_down.png")
	ImageResource triangle_down();

	@Source("org/geogebra/common/icons/png/web/general/xy_segments.png")
	ImageResource xy_segments();

	@Source("org/geogebra/common/icons/png16x16/step_graph.png")
	ImageResource step_graph();

	@Source("org/geogebra/common/icons/png16x16/bar_graph.png")
	ImageResource bar_graph();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_statistics.png")
	ImageResource dataview_showstatistics();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_data.png")
	ImageResource dataview_showdata();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_2nd_plot.png")
	ImageResource dataview_showplot2();
}
