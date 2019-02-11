package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.AppConfigCas;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.resources.SVGResource;

/**
 * Specific behavior for CAS app
 */
public class CASActivity extends BaseActivity {

	/**
	 * Graphing activity
	 */
	public CASActivity() {
		super(new AppConfigCas());
	}

	@Override
	public SVGResource getIcon(){
		return SvgPerspectiveResources.INSTANCE.menu_icon_cas();
	}

}
