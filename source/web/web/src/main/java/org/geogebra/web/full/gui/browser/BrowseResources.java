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

package org.geogebra.web.full.gui.browser;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.resources.client.Resource;

/**
 * Icons for Open File View
 */
@Resource
public interface BrowseResources extends ClientBundle {

	BrowseResources INSTANCE = new BrowseResourcesImpl();

	@Source("org/geogebra/common/icons/png/web/arrow_go_previous_grey.png")
	ImageResource back();

	@Source("org/geogebra/common/icons/png/web/open-from-location_geogebratube.png")
	ImageResource location_tube();

	@Source("org/geogebra/common/icons/png/web/open-from-location_googledrive.png")
	ImageResource location_drive();

	@Source("org/geogebra/common/icons/png/web/open-from-location_local-storage.png")
	ImageResource location_local();

}
