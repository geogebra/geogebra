package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;

/**
 * Equation behavior that doesn't apply any customization to the default equation forms.
 *
 * @apiNote Subclass and override for app-specific differences.
 *
 * @See <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation form matrix"</a>.
 */
public class DefaultEquationBehaviour implements EquationBehaviour {

	@Override
	public LinearEquationRepresentable.Form getLinearAlgebraInputEquationForm() {
		return null;
	}

	@Override
	public LinearEquationRepresentable.Form getLineCommandEquationForm() {
		return null;
	}

	@Override
	public LinearEquationRepresentable.Form getFitLineCommandEquationForm() {
		return LinearEquationRepresentable.Form.EXPLICIT;
	}

	@Override
	public LinearEquationRepresentable.Form getRayCommandEquationForm() {
		return null;
	}

	@Override
	public QuadraticEquationRepresentable.Form getConicAlgebraInputEquationForm() {
		return null;
	}

	@Override
	public QuadraticEquationRepresentable.Form getConicCommandEquationForm() {
		return null;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return true;
	}
}
