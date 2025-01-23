package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.main.settings.config.equationforms.DefaultEquationBehaviour;

/**
 * APPS-5926:
 * - For any Conic, Line, Equation, Function or Implicit Equation manually
 *   entered  by the user, restrict the equation form to “Input Form”.
 * - Setting to change the form is disabled
 */
public class CvteEquationBehaviour extends DefaultEquationBehaviour {

	@Override
	public LinearEquationRepresentable.Form getLinearAlgebraInputEquationForm() {
		return LinearEquationRepresentable.Form.USER;
	}

	@Override
	public QuadraticEquationRepresentable.Form getConicAlgebraInputEquationForm() {
		return QuadraticEquationRepresentable.Form.USER;
	}
}
