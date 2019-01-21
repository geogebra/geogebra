package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.AppConfig;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.view.algebra.AlgebraItemHeader;
import org.geogebra.web.full.gui.view.algebra.MarblePanel;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * General activity for all apps
 * 
 * @author Zbynek
 */
public class BaseActivity implements GeoGebraActivity {

	private AppConfig config;

	/**
	 * @param appConfig
	 *            config
	 */
	public BaseActivity(AppConfig appConfig) {
		this.config = appConfig;
	}

	@Override
	public AppConfig getConfig() {
		return this.config;
	}

	@Override
	public void start(AppW appW) {
		// nothing to do
	}

	@Override
	public SVGResource getNumericIcon() {
		return MaterialDesignResources.INSTANCE.modeToggleNumeric();
	}

	@Override
	public SVGResource getOutputPrefixIcon() {
		return MaterialDesignResources.INSTANCE.arrow_black();
	}

	@Override
	public void initStylebar(DockPanelW dockPanelW) {
		dockPanelW.initGraphicsSettingsButton();
	}

	@Override
	public DockPanelW createAVPanel() {
		return new ToolbarDockPanelW();
	}

	@Override
	public AlgebraItemHeader createAVItemHeader(RadioTreeItem radioTreeItem) {
		return new MarblePanel(radioTreeItem);
	}
}
