package org.geogebra.common.kernel;

import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.EvalInfo;

/**
 * Provides a way to customize (override) the default behavior of line- and conic-creating
 * code (e.g., {@link org.geogebra.common.kernel.algos.AlgoJoinPoints AlgoJoinPoints},
 * {@link org.geogebra.common.kernel.commands.AlgebraProcessor#processLine(Equation, ExpressionNode, EvalInfo) AlgebraProcessor's processLine}, etc).
 * <p/>
 * If an app (or exam type) requires certain equation forms to be enforced, return one of the
 * documented valid values from the corresponding method(s).
 * <p/>
 * @apiNote If we need more fine-grained overrides, we can add methods for specific cases here.
 * @implNote The equation form for {@link org.geogebra.common.kernel.geos.GeoLine GeoLine} and
 * subclasses is initalized from the construction defaults (via the call to
 * {@code setConstructionDefaults()} in the {@code GeoLine} constructor). By default, the
 * equation style in the construction defaults for {@code GeoLine} and subclasses is set to
 * {@code EQUATION_IMPLICIT}, but this default setting may be overridden (in the Classic app).
 *
 * @See <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation forms matrix"</a>.
 */
public interface EquationBehaviour {

	/**
	 * Customize the equation form for lines, implicit equations and functions (e.g., "y = x").
	 * @return One of the constants defined in
	 * {@link org.geogebra.common.kernel.EquationForm.Linear}, or null if the equation form
	 * should be taken from the construction defaults (see note in header).
	 * @see EquationBehaviour#getConicAlgebraInputEquationForm()
	 * @see EquationBehaviour#getOtherAlgebraInputEquationForm()
	 */
	EquationForm.Linear getLinearAlgebraInputEquationForm();

	/**
	 * Customize the equation form for lines created from a command or tool.
	 * @return One of the constants defined in
	 * {@link org.geogebra.common.kernel.EquationForm.Linear}, or null if the equation form
	 * should be taken from the construction defaults (see note in header).
	 * @see org.geogebra.common.kernel.algos.AlgoJoinPoints
	 */
	EquationForm.Linear getLineCommandEquationForm();

	/**
	 * Customize the equation form for lines created from a FitLine command.
	 * @return One of the constants defined in
	 * {@link org.geogebra.common.kernel.EquationForm.Linear}, or null if the equation form
	 * should be taken from the construction defaults (see note in header).
	 * @see org.geogebra.common.kernel.algos.AlgoJoinPoints
	 */
	EquationForm.Linear getFitLineCommandEquationForm();

	/**
	 * Customize the equation form for rays created from a command or tool.
	 * @return One of the constants defined in
	 * {@link org.geogebra.common.kernel.EquationForm.Linear}, or null if the equation form
	 * should be taken from the construction defaults (see note in header).
	 * @see org.geogebra.common.kernel.algos.AlgoJoinPointsRay
	 */
	EquationForm.Linear getRayCommandEquationForm();

	/**
	 * Customize the equation form for conics created from user input (e.g., "y = xx").
	 * @return One of the constants defined in
	 * {@link org.geogebra.common.kernel.EquationForm.Quadric}, or null if the default equation form
	 * should be used.
	 */
	EquationForm.Quadric getConicAlgebraInputEquationForm();

	/**
	 * Customize the equation form for conics created from a (Parabola, etc) command or tool.
	 * @return One of the constants defined in
	 * {@link org.geogebra.common.kernel.EquationForm.Quadric}, or null if the default equation form
	 * should be used.
	 */
	EquationForm.Quadric getConicCommandEquationForm();

	EquationForm.Other getOtherAlgebraInputEquationForm();

	/**
	 * Whether this EquationBehaviour allows the equation forms to be changed by the user.
	 * Since this is currently an all-or-none property (see spreadsheet linked in header)
	 * there's only one method for all equation forms. If we need finer granularity, we can
	 * split this into multiple methods.
	 * @return true if the equation forms can be changed, false otherwise.
	 * TODO could this be overriden for certain exams?
	 */
	boolean allowsChangingEquationFormsByUser();
}
