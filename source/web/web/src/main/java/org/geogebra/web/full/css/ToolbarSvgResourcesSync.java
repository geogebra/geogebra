/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
