package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.commands.selector.CommandSelector;
import org.geogebra.common.kernel.commands.selector.SciCalcCommandSelectorFactory;
import org.geogebra.common.main.settings.AppConfigScientific;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.scientific.ScientificDockPanelDecorator;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
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
		CommandSelector commandSelector = new SciCalcCommandSelectorFactory()
				.createCommandSelector();
		app.getKernel().getAlgebraProcessor()
				.setCommandSelector(commandSelector);
		initHeaderButtons(app);
	}

	private void initHeaderButtons(AppW app) {
		initMenuToggleButton(app);
		GlobalHeader.INSTANCE.initSettingButtonIfOnHeader();
		GlobalHeader.INSTANCE.initUndoRedoButtonsIfOnHeader();
	}

	private void initMenuToggleButton(AppW app) {
		MenuToggleButton btn = new MenuToggleButton(app);
		btn.setExternal(true);
		btn.addToGlobalHeader();
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
		return new AlgebraDockPanelW(new ScientificDockPanelDecorator());
	}
}
