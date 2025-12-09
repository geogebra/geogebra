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

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.implicit.GeoImplicit;

/**
 * Provides a way to customize (override) the default behavior of line- and conic-creating
 * code (e.g., {@link org.geogebra.common.kernel.algos.AlgoJoinPoints AlgoJoinPoints},
 * {@link org.geogebra.common.kernel.commands.AlgebraProcessor#processLine(Equation, ExpressionNode, EvalInfo) AlgebraProcessor's processLine}, etc).
 * @apiNote If we need more fine-grained overrides, we can add methods for specific cases here.
 * @implNote The equation form for {@link org.geogebra.common.kernel.geos.GeoLine GeoLine} and
 * subclasses is initialized from the construction defaults (via the call to
 * {@code setConstructionDefaults()} in the {@code GeoLine} constructor). By default, the
 * equation style in the construction defaults for {@code GeoLine} and subclasses is set to
 * {@code EQUATION_IMPLICIT}, but this default setting may be overridden (in the Classic app).
 *
 * @see <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation forms matrix</a>
 */
public interface EquationBehaviour {

	/**
	 * Customize the equation form for lines, implicit equations and functions (e.g., "y = x").
	 * @return One of the values defined in
	 * {@link LinearEquationRepresentable.Form}, or null if the equation form
	 * should be taken from the construction defaults (see note in header).
	 * @see EquationBehaviour#getConicAlgebraInputEquationForm()
	 */
	LinearEquationRepresentable.Form getLinearAlgebraInputEquationForm();

	/**
	 * Customize the equation form for lines created from a command or tool.
	 * @return One of the values defined in
	 * {@link LinearEquationRepresentable.Form}, or null if the equation form
	 * should be taken from the construction defaults (see note in header).
	 * @see org.geogebra.common.kernel.algos.AlgoJoinPoints
	 */
	LinearEquationRepresentable.Form getLineCommandEquationForm();

	/**
	 * Customize the equation form for lines created from a FitLine command.
	 * @return One of the values defined in
	 * {@link LinearEquationRepresentable.Form}, or null if the equation form
	 * should be taken from the construction defaults (see note in header).
	 * @see org.geogebra.common.kernel.algos.AlgoJoinPoints
	 */
	LinearEquationRepresentable.Form getFitLineCommandEquationForm();

	/**
	 * Customize the equation form for rays created from a command or tool.
	 * @return One of the values defined in
	 * {@link LinearEquationRepresentable.Form}, or null if the equation form
	 * should be taken from the construction defaults (see note in header).
	 * @see org.geogebra.common.kernel.algos.AlgoJoinPointsRay
	 */
	LinearEquationRepresentable.Form getRayCommandEquationForm();

	/**
	 * Customize the equation form for conics created from user input (e.g., "y = xx").
	 * @return One of the values defined in
	 * {@link QuadraticEquationRepresentable.Form}, or null if the default equation form
	 * should be used.
	 */
	QuadraticEquationRepresentable.Form getConicAlgebraInputEquationForm();

	/**
	 * Customize the equation form for conics created from a (Parabola, etc) command or tool.
	 * @return One of the values defined in
	 * {@link QuadraticEquationRepresentable.Form}, or null if the default equation form
	 * should be used.
	 */
	QuadraticEquationRepresentable.Form getConicCommandEquationForm();

	/**
	 * Whether this EquationBehaviour allows the equation forms to be changed by the user.
	 * Since this is currently an all-or-none property (see spreadsheet linked in header)
	 * there's only one method for all equation forms. If we need finer granularity, we can
	 * split this into multiple methods.
	 * @return true if the equation forms can be changed, false otherwise.
	 */
	boolean allowsChangingEquationFormsByUser();

	/**
	 * @param flag Whether changing the equation form by the user should be allowed.
	 */
	void allowChangingEquationFormsByUser(boolean flag);

	default GeoImplicit.Form getCurveAlgebraInputEquationForm() {
		return GeoImplicit.Form.USER;
	}
}
