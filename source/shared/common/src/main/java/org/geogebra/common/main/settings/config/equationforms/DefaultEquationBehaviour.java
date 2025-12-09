/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.main.settings.config.equationforms;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;

/**
 * Equation behavior that doesn't apply any customization to the default equation forms.
 *
 * @apiNote Subclass and override for app-specific differences.
 *
 * @see <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation form matrix</a>
 */
public class DefaultEquationBehaviour implements EquationBehaviour {

	protected boolean allowChangingEquationFormsByUser = true;

	@Override
	public LinearEquationRepresentable.Form getLinearAlgebraInputEquationForm() {
		return LinearEquationRepresentable.Form.USER;
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
		return QuadraticEquationRepresentable.Form.USER;
	}

	@Override
	public QuadraticEquationRepresentable.Form getConicCommandEquationForm() {
		return null;
	}

	@Override
	public boolean allowsChangingEquationFormsByUser() {
		return allowChangingEquationFormsByUser;
	}

	@Override
	public void allowChangingEquationFormsByUser(boolean flag) {
		allowChangingEquationFormsByUser = flag;
	}
}
