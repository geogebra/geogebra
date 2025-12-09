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

package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.resources.SVGResource;

/**
 * Specific behaviors of Geometry app
 */
public class GeometryActivity extends BaseActivity {

	/**
	 * New Geometry activity
	 */
	public GeometryActivity() {
		super(new AppConfigGeometry());
	}

	@Override
	public SVGResource getIcon() {
		return SvgPerspectiveResources.INSTANCE.menu_icon_graphics();
	}
}
