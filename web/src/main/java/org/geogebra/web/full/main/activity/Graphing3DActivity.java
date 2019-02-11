package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.AppConfigGraphing3D;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.resources.SVGResource;

/**
 * Specific behavior for graphing app
 */
public class Graphing3DActivity extends BaseActivity {

	/**
	 * Graphing activity
	 */
	public Graphing3DActivity() {
		super(new AppConfigGraphing3D());
	}

	@Override
	public SVGResource getIcon(){
		return MaterialDesignResources.INSTANCE.graphing();
	}

}
