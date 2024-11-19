package org.geogebra.common.main.settings.config.equationforms;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.ConicEquationRepresentable;

public class EquationBehaviourGeometry extends DefaultEquationBehaviour {

	@Override
	public LinearEquationRepresentable.Form getLinearAlgebraInputEquationForm() {
		return LinearEquationRepresentable.Form.USER;
	}

	@Override
	public LinearEquationRepresentable.Form getLineCommandEquationForm() {
		return LinearEquationRepresentable.Form.EXPLICIT;
	}

	@Nonnull
	@Override
	public LinearEquationRepresentable.Form getRayCommandEquationForm() {
		return LinearEquationRepresentable.Form.EXPLICIT;
	}

	@Override
	public ConicEquationRepresentable.Form getConicAlgebraInputEquationForm() {
		return ConicEquationRepresentable.Form.USER;
	}

	@Override
	public ConicEquationRepresentable.Form getConicCommandEquationForm() {
		return ConicEquationRepresentable.Form.IMPLICIT;
	}
}
