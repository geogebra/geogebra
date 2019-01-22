package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.AppConfigMixedReality;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;

public class MixedRealityActivity extends BaseActivity {

	public MixedRealityActivity() {
		super(new AppConfigMixedReality());
	}

	@Override
	public boolean showObjectSettingsFromAV() {
		return false;
	}

	@Override
	public DockPanelW createAVPanel() {
		return new AlgebraDockPanelW(null);
	}

}
