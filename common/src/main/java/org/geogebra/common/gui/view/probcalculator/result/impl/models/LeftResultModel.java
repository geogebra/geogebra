package org.geogebra.common.gui.view.probcalculator.result.impl.models;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.ResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.InputEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.TextEntry;
import org.geogebra.common.main.Localization;

public class LeftResultModel extends AbstractResultModel {

	private TextEntry lessThanOrEqual;
	private TextEntry endProbabilityOf;

	private InputEntry highEntry;
	private InputEntry resultEntry;

	/**
	 * @param localization localization
	 */
	public LeftResultModel(Localization localization) {
		String probabilityOf = localization.getMenu("ProbabilityOf");
		String xLessThanOrEqual = localization.getMenu("XLessThanOrEqual");
		lessThanOrEqual = new TextEntry(probabilityOf + " " + xLessThanOrEqual);
		endProbabilityOf = new TextEntry(localization.getMenu("EndProbabilityOf") + EQUALS_SIGN);

		highEntry = new InputEntry("");
		resultEntry = new InputEntry("");
	}

	@Override
	public void setLow(String low) {
		// no-op
	}

	@Override
	public void setHigh(String high) {
		highEntry = new InputEntry(high);
	}

	@Override
	public void setResult(String result) {
		resultEntry = new InputEntry(result);
	}

	@Override
	public EditableResultEntry getLow() {
		return null;
	}

	@Override
	public EditableResultEntry getHigh() {
		return highEntry;
	}

	@Override
	public EditableResultEntry getResult() {
		return resultEntry;
	}

	@Override
	public List<ResultEntry> getEntries() {
		return Arrays.asList(lessThanOrEqual, highEntry, endProbabilityOf, resultEntry);
	}
}
