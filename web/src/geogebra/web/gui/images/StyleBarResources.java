package geogebra.web.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface StyleBarResources extends ClientBundle {
	StyleBarResources INSTANCE = GWT.create(StyleBarResources.class);
	
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
}
