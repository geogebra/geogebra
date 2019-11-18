package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.AppConfigGeometry;
import org.geogebra.web.full.css.MaterialDesignResources;
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
		return MaterialDesignResources.INSTANCE.geometry();
	}
}
