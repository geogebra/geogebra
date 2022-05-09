package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

/**
 * Interface for the table of values view.
 */
public interface TableValues extends View, StatisticsView {

	/**
	 * Show the column for the Evaluatable object.
	 * @param evaluatable object to evaluate in table
	 */
	void showColumn(GeoEvaluatable evaluatable);

	/**
	 * Hide the column for the Evaluatable object.
	 * @param evaluatable object to hide in table
	 */
	void hideColumn(GeoEvaluatable evaluatable);

	/**
	 * Return the column index for the evaluatable.
	 * Returns -1, if the column does not exist.
	 * @param evaluatable object to check
	 * @return the column of the evaluatable, or -1 if it's not present
	 */
	int getColumn(GeoEvaluatable evaluatable);

	/**
	 * Returns the evaluatable for the column,
	 * or null, if index is out of range.
	 * @param column index
	 * @return evaluatable or null
	 */
	GeoEvaluatable getEvaluatable(int column);

	/**
	 * Set the values parameters. This method assumes valuesMin &lt; valuesMax,
	 * and valuesStep &gt;0 are all finite real numbers.
	 * <p>
	 * If the min/max/step combination requires too many datapoints, an
	 * InvalidValuesException is thrown.
	 * <p>
	 * Creates an undo point. Set the values in Settings directly to avoid that.
	 * @param valuesMin lower value of x-values
	 * @param valuesMax upper value of x-values
	 * @param valuesStep step of the x-values
	 * @throws InvalidValuesException values set in table view are invalid
	 */
	void setValues(double valuesMin, double valuesMax, double valuesStep) throws
			InvalidValuesException;

	/**
	 * Get the lower value of the x-values.
	 * @return the lower value of x-values
	 */
	double getValuesMin();

	/**
	 * Get the upper value of the x-values.
	 * @return the upper value of x-values
	 */
	double getValuesMax();

	/**
	 * Get the step of the x-values/
	 * @return the step of the x-values
	 */
	double getValuesStep();

	/**
	 * Returns the values Geo Element. This can be changed directly.
	 * When the list elements are changed, call {@link GeoElement#notifyUpdate()}
	 * so that the Table Values view is notified.
	 * @return values
	 */
	GeoList getValues();

	/**
	 * Clears the values (x) column.
	 */
	void clearValues();

	/**
	 * Sets an element.
	 * @param element element
	 * @param column column
	 * @param rowIndex row index
	 */
	void set(GeoElement element, GeoList column, int rowIndex);

	/**
	 * Test if the table values view is empty.
	 * @return true if no table values yet
	 */
	boolean isEmpty();

	/**
	 * Get the table values model. Objects can register themselves
	 * as listeners to this model. Also table row and column information
	 * is available through this model.
	 * @return the table values model
	 */
	TableValuesModel getTableValuesModel();

	/**
	 * Get the dimensions. This object gives information about cell
	 * and header sizes.
	 * @return table values dimensions
	 */
	TableValuesDimensions getTableValuesDimensions();

	/**
	 * Get the lower value of the x-values as a formatted string
	 * @return the lower value of x-values as a formatted string
	 */
	String getValuesMinStr();

	/**
	 * Get the upper value of the x-values as a formatted string
	 * @return the upper value of x-values as a formatted string
	 */
	String getValuesMaxStr();

	/**
	 * Get the step of the x-values as a formatted string
	 * @return the step of the x-values as a formatted string
	 */
	String getValuesStepStr();

	/**
	 * Return the object for processing input.
	 * @return processor
	 */
	TableValuesProcessor getProcessor();

	void addAndShow(GeoElement geo);
}
