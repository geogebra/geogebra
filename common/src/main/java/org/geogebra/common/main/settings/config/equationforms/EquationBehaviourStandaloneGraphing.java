package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationLinear;
import org.geogebra.common.kernel.EquationQuadric;

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
	 */
	@Override
	public EquationLinear.Form getLinearAlgebraInputEquationForm() {
		return EquationLinear.Form.USER;
	}

	@Override
	public EquationLinear.Form getLineCommandEquationForm() {
		return EquationLinear.Form.EXPLICIT;
	}

	@Override
	public EquationLinear.Form getRayCommandEquationForm() {
		return EquationLinear.Form.USER;
	}

	@Override
	public EquationQuadric.Form getConicAlgebraInputEquationForm() {
		return EquationQuadric.Form.IMPLICIT;
	}

	@Override
	public EquationQuadric.Form getConicCommandEquationForm() {
		return EquationQuadric.Form.USER;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return false;
	}
}
