package org.geogebra.common.exam.restrictions.realschule;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.main.settings.config.equationforms.DefaultEquationBehaviour;

public class RealschuleEquationBehaviour extends DefaultEquationBehaviour {

	@Override
	public LinearEquationRepresentable.Form getLinearAlgebraInputEquationForm() {
		return LinearEquationRepresentable.Form.USER;
	}

	@Override
	public QuadraticEquationRepresentable.Form getConicAlgebraInputEquationForm() {
		return QuadraticEquationRepresentable.Form.USER;
	}
}