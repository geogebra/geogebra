package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.EquationForm;

/**
 * Equation behavior that doesn't apply any customization to the default equation forms.
 *
 * @apiNote Subclass and override for app-specific differences.
 *
 * @See <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation form matrix"</a>.
 */
public class DefaultEquationBehaviour implements EquationBehaviour {

	@Override
	public EquationForm.Linear getLinearAlgebraInputEquationForm() {
		return null;
	}

	@Override
	public EquationForm.Linear getLineCommandEquationForm() {
		return null;
	}

	@Override
	public EquationForm.Linear getFitLineCommandEquationForm() {
		return EquationForm.Linear.EXPLICIT;
	}

	@Override
	public EquationForm.Linear getRayCommandEquationForm() {
		return null;
	}

	@Override
	public EquationForm.Quadric getConicAlgebraInputEquationForm() {
		return null;
	}

	@Override
	public EquationForm.Quadric getConicCommandEquationForm() {
		return null;
	}

	@Override
	public EquationForm.Other getOtherAlgebraInputEquationForm() {
		return null;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return true;
	}
}
