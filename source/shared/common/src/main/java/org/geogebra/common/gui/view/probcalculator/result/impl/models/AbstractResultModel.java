package org.geogebra.common.gui.view.probcalculator.result.impl.models;

import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.ResultModel;

public abstract class AbstractResultModel implements ResultModel {

	static final String PLUS_SIGN = " + ";
	static final String EQUALS_SIGN = " = ";

	public abstract EditableResultEntry getLow();

	public abstract EditableResultEntry getHigh();

	public abstract EditableResultEntry getResult();

	public abstract void setLow(String low);

	public abstract void setHigh(String high);

	public abstract void setResult(String result);
}
