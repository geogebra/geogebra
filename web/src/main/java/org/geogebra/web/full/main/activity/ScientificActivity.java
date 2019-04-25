package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.commands.selector.CommandSelectorFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.AppConfigScientific;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.gui.layout.scientific.ScientificDockPanelDecorator;
import org.geogebra.web.full.gui.menubar.MainMenuItemProvider;
import org.geogebra.web.full.gui.menubar.ScientificMenuItemProvider;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.full.gui.view.algebra.AVItemHeaderScientific;
import org.geogebra.web.full.gui.view.algebra.AlgebraItemHeader;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuActionCollection;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.AlgebraMenuItemCollectionScientific;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.GlobalHeader;

/**
 * Activity for scientific calculator
 *
 * @author Zbynek
 */
public class ScientificActivity extends BaseActivity {

	/**
	 * Activity for scientific calculator
	 */
	public ScientificActivity() {
		super(new AppConfigScientific());
	}

	@Override
	public void start(AppW app) {
		
		app.getKernel().getAlgebraProcessor()
				.setCommandSelector(CommandSelectorFactory.createSciCalcCommandSelector());
		initHeaderButtons(app);
		app.forceEnglishCommands();
		app.setRightClickEnabledForAV(false);
	}

	private static void initHeaderButtons(AppW app) {
		initMenuToggleButton(app);
		GlobalHeader.INSTANCE.initSettingButtonIfOnHeader();
		GlobalHeader.INSTANCE.initUndoRedoButtonsIfOnHeader();
	}

	private static void initMenuToggleButton(AppW app) {
		if (GlobalHeader.isInDOM()) {
			MenuToggleButton btn = new MenuToggleButton(app);
			btn.setExternal(true);
			btn.addToGlobalHeader();
		}
	}

	@Override
	public SVGResource getNumericIcon() {
		return MaterialDesignResources.INSTANCE.equal_sign_white();
	}

	@Override
	public SVGResource getOutputPrefixIcon() {
		return MaterialDesignResources.INSTANCE.equal_sign_black();
	}

	@Override
	public void initStylebar(DockPanelW dockPanelW) {
		dockPanelW.showStyleBarPanel(false);
	}

	@Override
	public DockPanelW createAVPanel() {
		return new AlgebraDockPanelW(new ScientificDockPanelDecorator(), false);
	}

	@Override
	public AlgebraItemHeader createAVItemHeader(RadioTreeItem radioTreeItem) {
		return new AVItemHeaderScientific();
	}

	@Override
	public MenuActionCollection<GeoElement> getAVMenuItems(AlgebraViewW view) {
		return new AlgebraMenuItemCollectionScientific(view);
	}

	@Override
	public ErrorHandler createAVErrorHandler(RadioTreeItem radioTreeItem, boolean valid,
			boolean allowSliders, boolean withSliders) {
		return ErrorHelper.silent();
	}

	@Override
	public MainMenuItemProvider getMenuItemProvider(AppW app) {
		return new ScientificMenuItemProvider(app);
	}

	@Override
	public void showSettingsView(AppW app) {
		app.getGuiManager().showSciSettingsView();
	}

	@Override
	public SVGResource getIcon() {
		return MaterialDesignResources.INSTANCE.scientific();
	}

	@Override
	public boolean useValidInput() {
		return false;
	}
}
