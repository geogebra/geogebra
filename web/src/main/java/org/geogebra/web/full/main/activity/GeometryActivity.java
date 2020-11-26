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
