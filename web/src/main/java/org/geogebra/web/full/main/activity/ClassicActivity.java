package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.AppConfig;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.gui.menubar.ClassicMenuItemProvider;
import org.geogebra.web.full.gui.menubar.MainMenuItemProvider;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuActionCollection;
import org.geogebra.web.full.gui.view.algebra.contextmenu.AlgebraMenuItemCollectionClassic;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.full.main.NullHeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;

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
	public MainMenuItemProvider getMenuItemProvider(AppW app) {
		return new ClassicMenuItemProvider(app);
	}

	@Override
	public MenuActionCollection<GeoElement> getAVMenuItems(AlgebraViewW view) {
		return new AlgebraMenuItemCollectionClassic(view);
	}

	@Override
	public HeaderResizer getHeaderResizer(GeoGebraFrameW frame) {
		return NullHeaderResizer.get();
	}
}
