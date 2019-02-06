package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AppConfigMixedReality;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.gui.view.algebra.AlgebraMenuItemCollectionMR;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuActionCollection;

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

	@Override
	public MenuActionCollection<GeoElement> getAVMenuItems(AlgebraViewW view) {
		return new AlgebraMenuItemCollectionMR(view);
	}

}
