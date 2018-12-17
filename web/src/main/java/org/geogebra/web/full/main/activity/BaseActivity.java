package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.AppConfig;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * General activity for all apps
 * 
 * @author Zbynek
 */
public class BaseActivity implements GeoGebraActivity {

	private AppConfig config;

	/**
	 * @param appConfig
	 *            config
	 */
	public BaseActivity(AppConfig appConfig) {
		this.config = appConfig;
	}

	@Override
	public AppConfig getConfig() {
		return this.config;
	}

	@Override
	public void start(AppW appW) {
		// nothing to do
	}

	public SVGResource getNumericIcon() {
		return MaterialDesignResources.INSTANCE.modeToggleNumeric();
	}

	public SVGResource getOutputPrefixIcon() {
		return MaterialDesignResources.INSTANCE.arrow_black();
	}
}
