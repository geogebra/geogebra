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

package org.geogebra.web.html5.css;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;

/**
 * Icons for zoom panel.
 */
@Resource
public interface ZoomPanelResources extends ClientBundle {

	ZoomPanelResources INSTANCE = new ZoomPanelResourcesImpl();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_fullscreen_black_18px.svg")
	SVGResource fullscreen_black18();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_fullscreen_exit_black_18px.svg")
	SVGResource fullscreen_exit_black18();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_home_black_24px.svg")
	SVGResource home_zoom_black18();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/target.svg")
	SVGResource target();
}
