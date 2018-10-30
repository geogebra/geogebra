package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.arithmetic.Evaluatable;

/**
 * Interface for the table of values view.
 */
public interface TableValues extends View {

	/**
	 * Show the column for the Evaluatable object.
	 *
	 * @param evaluatable object to evaluate in table
	 */
	void showColumn(Evaluatable evaluatable);

	/**
	 * Hide the column for the Evaluatable object.
	 *
	 * @param evaluatable object to hide in table
	 */
	void hideColumn(Evaluatable evaluatable);

	/**
	 * Set the values parameters.
	 *
	 * @param valuesMin lower value of x-values
	 * @param valuesMax upper value of x-values
	 * @param valuesStep step of the x-values
	 */
	void setValues(double valuesMin, double valuesMax, double valuesStep);

	/**
	 * Get the lower value of the x-values.
	 *
	 * @return the lower value of x-values
	 */
	double getValuesMin();

	/**
	 * Get the upper value of the x-values.
	 *
	 * @return the upper value of x-values
	 */
	double getValuesMax();

	/**
	 * Get the step of the x-values/
	 *
	 * @return the step of the x-values
	 */
	double getValuesStep();

	/**
	 * Test if the table values view is empty.
	 *
	 * @return true if no table values yet
	 */
	boolean isEmpty();

	/**
	 * Get the table values model. Objects can register themselves
	 * as listeners to this model. Also table row and column information
	 * is available through this model.
	 *
	 * @return the table values model
	 */
	TableValuesModel getTableValuesModel();
}
