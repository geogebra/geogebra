package org.geogebra.web.full.css;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;

/**
 * SVG icons for toolbar that require sync loading
 */
@Resource
public interface ToolbarSvgResourcesSync extends ClientBundle {
	/** singleton instance */
	ToolbarSvgResourcesSync INSTANCE = new ToolbarSvgResourcesSyncImpl();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_tool.svg")
	SVGResource mode_tool_32();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_checkbox.svg")
	SVGResource mode_showcheckbox_32();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_slider.svg")
	SVGResource mode_slider_32();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_showhidelabel.svg")
	SVGResource mode_showhidelabel_32();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_web.svg")
	SVGResource mode_extension();

	// ONLY ADD FILES HERE IF THEY NEED SYNC LOADING
	// (i.e. they are used not only for the toolbar, but also in other places)

}
