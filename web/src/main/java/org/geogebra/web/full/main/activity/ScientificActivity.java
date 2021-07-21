package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.config.AppConfigScientific;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.gui.layout.scientific.ScientificDockPanelDecorator;
import org.geogebra.web.full.gui.layout.scientific.ScientificHeaderResizer;
import org.geogebra.web.full.gui.view.algebra.AVItemHeaderScientific;
import org.geogebra.web.full.gui.view.algebra.AlgebraItemHeader;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuItemCollection;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.AlgebraMenuItemCollectionScientific;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.GlobalHeader;

/**
 * Activity for scientific calculator
 *
 * @author Zbynek
 */
public class ScientificActivity extends BaseActivity {

	private ScientificHeaderResizer headerResizer = null;

	/**
	 * Activity for scientific calculator
	 */
	public ScientificActivity() {
		super(new AppConfigScientific());
	}

	@Override
	public void start(AppW app) {
		
		app.getKernel().getAlgebraProcessor()
				.addCommandFilter(CommandFilterFactory.createSciCalcCommandFilter());
		initHeaderButtons(app);
		app.forceEnglishCommands();
		app.setRightClickEnabledForAV(false);
		app.getAppletFrame().updateArticleHeight();
	}

	private static void initHeaderButtons(AppW app) {
		app.getGuiManager().menuToGlobalHeader();
		GlobalHeader.INSTANCE.initSettingButtonIfOnHeader();
		GlobalHeader.INSTANCE.initUndoRedoButtonsIfOnHeader();
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
	public MenuItemCollection<GeoElement> getAVMenuItems(AlgebraViewW view) {
		return new AlgebraMenuItemCollectionScientific(view);
	}

	@Override
	public ErrorHandler createAVErrorHandler(RadioTreeItem radioTreeItem, boolean valid,
			boolean allowSliders, boolean withSliders) {
		return ErrorHelper.silent();
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

	@Override
	public HeaderResizer getHeaderResizer(GeoGebraFrameW frame) {
		if (headerResizer == null) {
			headerResizer = new ScientificHeaderResizer(frame);
		}
		return headerResizer;
	}
}
