package org.geogebra.common.main.settings.config.equationforms;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.EquationLinear;
import org.geogebra.common.kernel.EquationQuadric;

public class EquationBehaviourGeometry extends DefaultEquationBehaviour {

	@Override
	public EquationLinear.Form getLinearAlgebraInputEquationForm() {
		return EquationLinear.Form.USER;
	}

	@Nonnull
	@Override
	public EquationLinear.Form getRayCommandEquationForm() {
		return EquationLinear.Form.EXPLICIT;
	}

	@Override
	public EquationQuadric.Form getConicAlgebraInputEquationForm() {
		return EquationQuadric.Form.USER;
	}

	@Override
	public EquationQuadric.Form getConicCommandEquationForm() {
		return EquationQuadric.Form.IMPLICIT;
	}
}
