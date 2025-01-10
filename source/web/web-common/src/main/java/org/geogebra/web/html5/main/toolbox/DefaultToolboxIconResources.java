package org.geogebra.web.html5.main.toolbox;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;

@Resource
public interface DefaultToolboxIconResources extends ClientBundle {

	DefaultToolboxIconResources INSTANCE = new DefaultToolboxIconResourcesImpl();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/toolbox/mouse_cursor.svg")
	SVGResource mouse_cursor();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_pen.svg")
	SVGResource pen();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_highlighter.svg")
	SVGResource highlighter();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_eraser.svg")
	SVGResource eraser();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/add.svg")
	SVGResource add_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/toolbox/tool_shapes.svg")
	SVGResource shapes();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/toolbox/text_fields.svg")
	SVGResource texts();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_media_text.svg")
	SVGResource text();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_equation.svg")
	SVGResource equation();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dialog/upload_black_24dp.svg")
	SVGResource upload();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_image.svg")
	SVGResource image();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_camera.svg")
	SVGResource camera();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_pdf.svg")
	SVGResource pdf();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/openFileView/link_black_24dp.svg")
	SVGResource link();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_web.svg")
	SVGResource web();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_video.svg")
	SVGResource video();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_audio.svg")
	SVGResource audio();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/toolbox/add_box.svg")
	SVGResource apps();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_geogebra.svg")
	SVGResource geogebra();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_mindmap.svg")
	SVGResource mindmap();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_table.svg")
	SVGResource table();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_graspablemath.svg")
	SVGResource grasphmath();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_ruler.svg")
	SVGResource ruler();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_protractor.svg")
	SVGResource ruler_protractor();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_triangleprotractor.svg")
	SVGResource ruler_triangle();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/target.svg")
	SVGResource target();

}
