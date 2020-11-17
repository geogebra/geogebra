package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ResourceIconProvider;
import org.geogebra.web.full.gui.layout.BaseHeaderResizer;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.view.algebra.AVErrorHandler;
import org.geogebra.web.full.gui.view.algebra.AlgebraItemHeader;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MarblePanel;
import org.geogebra.web.full.gui.view.algebra.MenuItemCollection;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.AlgebraMenuItemCollection;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DefaultExportedApi;
import org.geogebra.web.html5.main.ExportedApi;
import org.geogebra.web.resources.SVGResource;

/**
 * General activity for all apps
 *
 * @author Zbynek
 */
public class BaseActivity implements GeoGebraActivity {

	private AppConfig config;
	private BaseHeaderResizer headerResizer;

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

	@Override
	public MenuItemCollection<GeoElement> getAVMenuItems(AlgebraViewW view) {
		return new AlgebraMenuItemCollection(view);
	}

	@Override
	public ErrorHandler createAVErrorHandler(RadioTreeItem radioTreeItem, boolean valid,
			boolean allowSliders, boolean withSliders) {
		return new AVErrorHandler(radioTreeItem, valid, allowSliders, withSliders);
	}

	@Override
	public void showSettingsView(AppW app) {
		app.getDialogManager().showPropertiesDialog(OptionType.GLOBAL, null);
	}

	@Override
	public SVGResource getIcon() {
		// default implementation: classic and suite
		return MaterialDesignResources.INSTANCE.geogebra_color();
	}

	@Override
	public boolean useValidInput() {
		return true;
	}

	@Override
	public HeaderResizer getHeaderResizer(GeoGebraFrameW frame) {
		if (headerResizer == null) {
			headerResizer =	new BaseHeaderResizer(frame);
		}
		return headerResizer;
	}

	@Override
	public ResourceIconProvider getResourceIconProvider() {
		return MaterialDesignResources.INSTANCE;
	}

	@Override
	public boolean isWhiteboard() {
		return false;
	}

	@Override
	public ExportedApi getExportedApi() {
		return new DefaultExportedApi();
	}

	@Override
	public SVGResource getExamIcon() {
		return null;
	}

	@Override
	public void markSearchOpen() {
		// nothing to do
	}

	@Override
	public void markSaveOpen() {
		// nothing to do
	}
}
