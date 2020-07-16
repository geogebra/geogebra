package org.geogebra.common.main.settings;

import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.main.AppConfigDefault;

/**
 * Config for Evaluator
 *
 */
public class AppConfigEvaluator extends AppConfigDefault {

	@Override
	public String getForcedPerspective() {
		return Perspective.EVALUATOR + "";
	}

	@Override
	public String getAppTitle() {
		return "Evaluator";
	}

	@Override
	public String getAppCode() {
		return "evaluator";
	}

	@Override
	public String getTutorialKey() {
		return "evaluator_tutorials";
	}

	@Override
	public boolean isCASEnabled() {
		return false;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return CommandFilterFactory.createNoCasCommandFilter();
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
		return null;
	}

	@Override
	public int getEnforcedLineEquationForm() {
		return -1;
	}

	@Override
	public int getEnforcedConicEquationForm() {
		return -1;
	}

	@Override
	public boolean shouldHideEquations() {
		return false;
	}

}
