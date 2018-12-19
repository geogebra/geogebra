package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.AppConfig;
import org.geogebra.web.full.gui.layout.DockPanelW;

/**
 * Activity for the classic app
 * 
 * @author Zbynek
 */
public class ClassicActivity extends BaseActivity {

	/**
	 * @param appConfig
	 *            config
	 */
	public ClassicActivity(AppConfig appConfig) {
		super(appConfig);
	}

	@Override
	public void initStylebar(DockPanelW dockPanelW) {
		dockPanelW.initToggleButton();
	}

}
