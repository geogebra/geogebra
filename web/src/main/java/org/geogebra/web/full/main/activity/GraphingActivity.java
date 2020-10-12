package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
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
		return SvgPerspectiveResources.INSTANCE.menu_icon_algebra_transparent();
	}

	@Override
	public SVGResource getExamIcon() {
		return MaterialDesignResources.INSTANCE.exam_graphing();
	}
}
