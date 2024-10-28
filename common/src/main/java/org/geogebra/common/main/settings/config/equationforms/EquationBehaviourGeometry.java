package org.geogebra.common.main.settings.config.equationforms;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.EquationForm;

public class EquationBehaviourGeometry extends DefaultEquationBehaviour {

	@Override
	public EquationForm.Linear getLinearAlgebraInputEquationForm() {
		return EquationForm.Linear.USER;
	}

	@Nonnull
	@Override
	public EquationForm.Linear getLineCommandEquationForm() {
		return EquationForm.Linear.EXPLICIT;
	}

	@Nonnull
	@Override
	public EquationForm.Linear getRayCommandEquationForm() {
		return EquationForm.Linear.EXPLICIT;
	}

	@Override
	public EquationForm.Quadric getConicAlgebraInputEquationForm() {
		return EquationForm.Quadric.USER;
	}

	@Override
	public EquationForm.Quadric getConicCommandEquationForm() {
		return EquationForm.Quadric.IMPLICIT;
	}
}
