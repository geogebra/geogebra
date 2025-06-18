package org.geogebra.common.gui.view.table.regression;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;

public interface RegressionSpecification {

	/**
	 * @return label for selection dropdown
	 */
	String getLabel();

	/**
	 * @param kernel kernel
	 * @param points input data as a tuple (x-coordinates, y-coordinates)
	 * @return regression command
	 */
	Command buildCommand(Kernel kernel, MyVecNode points);

	/**
	 * @return LaTeX formula of the model, may be null if we don't want to show it
	 */
	@CheckForNull String getFormula();

	/**
	 * @return ordered coefficient names (concatenated)
	 */
	String getCoeffOrdering();

	/**
	 * @return whether correlation coefficient should be shown
	 */
	boolean hasCorrelationCoefficient();

	/**
	 * @return whether we're allowed to plot this in the graphics view
	 */
	boolean canPlot();

	/**
	 * @return whether coefficient of determination is allowed to be shown
	 */
	boolean hasCoefficientOfDetermination();
}
