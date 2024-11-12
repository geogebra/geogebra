package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.AppConfig;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.full.main.NullHeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;

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

	@Override
	public DockPanelW createAVPanel() {
		return new AlgebraDockPanelW(null, true);
	}

	@Override
	public HeaderResizer getHeaderResizer(GeoGebraFrameW frame) {
		return NullHeaderResizer.get();
	}
}
