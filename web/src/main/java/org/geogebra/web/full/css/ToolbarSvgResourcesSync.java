package org.geogebra.web.full.css;

import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * SVG icons for toolbar that require sync loading
 */
@SuppressWarnings("javadoc")
public interface ToolbarSvgResourcesSync extends ClientBundle {
	/** singleton instance */
	ToolbarSvgResourcesSync INSTANCE = GWT
			.create(ToolbarSvgResourcesSync.class);

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_tool.svg")
	SVGResource mode_tool_32();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_showcheckbox.svg")
	SVGResource mode_showcheckbox_32();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_slider.svg")
	SVGResource mode_slider_32();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_showhidelabel.svg")
	SVGResource mode_showhidelabel_32();

	@Source("org/geogebra/common/icons/svg/web/toolIcons/mode_extension.svg")
	SVGResource mode_extension();

	// ONLY ADD FILES HERE IF THEY NEED SYNC LOADING
	// (i.e. they are used not only for the toolbar, but also in other places)

}
