package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.commands.selector.CommandSelector;
import org.geogebra.common.kernel.commands.selector.SciCalcCommandSelectorFactory;
import org.geogebra.common.main.settings.AppConfigScientific;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

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
}
