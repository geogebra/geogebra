package org.geogebra.common.gui.view.probcalculator.result.impl.models;

import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.ResultModel;

public abstract class AbstractResultModel implements ResultModel {

	static final String PLUS_SIGN = " + ";
	static final String EQUALS_SIGN = " = ";

	/**
	 * @return editable entry for lower bound
	 */
	public abstract EditableResultEntry getLow();

	/**
	 * @return editable entry for upper bound
	 */
	public abstract EditableResultEntry getHigh();

	/**
	 * @return editable entry for result
	 */
	public abstract EditableResultEntry getResult();

	/**
	 * @param low lower bound value
	 */
	public abstract void setLow(String low);

	/**
	 * @param high upper bound value
	 */
	public abstract void setHigh(String high);

	/**
	 * @param result result value
	 */
	public abstract void setResult(String result);
}
