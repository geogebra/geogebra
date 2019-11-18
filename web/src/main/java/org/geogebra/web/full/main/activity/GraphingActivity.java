package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.AppConfigGraphing;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.resources.SVGResource;

/**
 * Specific behavior for graphing app
 */
public class GraphingActivity extends BaseActivity {

	/**
	 * Graphing activity
	 */
	public GraphingActivity() {
		super(new AppConfigGraphing());
	}

	@Override
	public SVGResource getIcon() {
		return MaterialDesignResources.INSTANCE.graphing();
	}

	@Override
	public SVGResource getExamIcon() {
		return MaterialDesignResources.INSTANCE.exam_graphing();
	}
}
