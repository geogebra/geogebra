package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.config.AppConfigMixedReality;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;

/**
 * Activity for restricted AV app in mixed reality
 */
public class MixedRealityActivity extends BaseActivity {

	/**
	 * New MR activity
	 */
	public MixedRealityActivity() {
		super(new AppConfigMixedReality());
	}

	@Override
	public DockPanelW createAVPanel() {
		return new AlgebraDockPanelW(null, true);
	}

}
