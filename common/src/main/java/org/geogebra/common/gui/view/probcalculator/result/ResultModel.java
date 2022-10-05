package org.geogebra.common.gui.view.probcalculator.result;

import java.util.List;

/**
 * This class represents a result model.
 */
public interface ResultModel {

	/**
	 * Get the entries of the model
	 * @return entries
	 */
	List<ResultEntry> getEntries();
}
