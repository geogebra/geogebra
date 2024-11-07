package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.EquationLinear;
import org.geogebra.common.kernel.EquationQuadric;

/**
 * Equation behavior that doesn't apply any customization to the default equation forms.
 *
 * @apiNote Subclass and override for app-specific differences.
 *
 * @See <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation form matrix"</a>.
 */
public class DefaultEquationBehaviour implements EquationBehaviour {

	@Override
	public EquationLinear.Form getLinearAlgebraInputEquationForm() {
		return null;
	}

	@Override
	public EquationLinear.Form getLineCommandEquationForm() {
		return null;
	}

	@Override
	public EquationLinear.Form getFitLineCommandEquationForm() {
		return EquationLinear.Form.EXPLICIT;
	}

	@Override
	public EquationLinear.Form getRayCommandEquationForm() {
		return null;
	}

	@Override
	public EquationQuadric.Form getConicAlgebraInputEquationForm() {
		return null;
	}

	@Override
	public EquationQuadric.Form getConicCommandEquationForm() {
		return null;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return true;
	}
}
