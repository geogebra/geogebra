package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationForm;

/**
 * Equation behaviour for the standalone Graphing app.
 *
 * @See <a href="https://geogebra-jira.atlassian.net/wiki/spaces/A/pages/836141057/Standalone+Graphing">Standalone Graphing Wiki page</a>
 */
public final class EquationBehaviourStandaloneGraphing extends DefaultEquationBehaviour {

	/**
	 * From <a href="https://geogebra-jira.atlassian.net/wiki/spaces/A/pages/836141057/Standalone+Graphing">Wiki</a>:
	 * "When manually entered, Lines, Conics, Implicit Equations and Functions are restricted to
	 * user/input form."
	 * @return
	 */
	@Override
	public EquationForm.Linear getLinearAlgebraInputEquationForm() {
		return EquationForm.Linear.USER;
	}

	@Override
	public EquationForm.Linear getLineCommandEquationForm() {
		return EquationForm.Linear.EXPLICIT;
	}

	@Override
	public EquationForm.Linear getRayCommandEquationForm() {
		return EquationForm.Linear.USER;
	}

	@Override
	public EquationForm.Quadric getConicAlgebraInputEquationForm() {
		return EquationForm.Quadric.USER;
	}

	@Override
	public EquationForm.Quadric getConicCommandEquationForm() {
		return EquationForm.Quadric.USER;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return false;
	}
}
