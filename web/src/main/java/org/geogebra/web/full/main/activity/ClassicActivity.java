package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.AppConfig;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;

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

	public DockPanelW createAVPanel() {
		return new AlgebraDockPanelW(new DockPanelDecorator());
	}

}
