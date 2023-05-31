package org.geogebra.web.full.gui.images;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.resources.client.Resource;

@Resource
public interface AppResources extends ClientBundle {
	
	AppResources INSTANCE = new AppResourcesImpl();

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

	@Source("org/geogebra/common/icons/png/web/general/empty.gif")
	ImageResource empty();

	@Source("org/geogebra/common/icons/png/web/general/geogebra64.png")
	ImageResource geogebra64();

	@Source("org/geogebra/common/icons/png/web/general/algebra_hidden.png")
	ImageResource hidden();

	@Source("org/geogebra/common/icons/png/web/general/osculating_circle.png")
	ImageResource osculating_circle();

	@Source("org/geogebra/common/icons/png/web/general/algebra_shown.png")
	ImageResource shown();

	@Source("org/geogebra/common/icons/png/web/general/table.gif")
	ImageResource table();

	@Source("org/geogebra/common/icons/png/web/general/tangent_line.png")
	ImageResource tangent_line();

	@Source("org/geogebra/common/icons/png/web/general/xy_segments.png")
	ImageResource xy_segments();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_statistics.png")
	ImageResource dataview_showstatistics();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_data.png")
	ImageResource dataview_showdata();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_data_analysis_show_2nd_plot.png")
	ImageResource dataview_showplot2();
}
