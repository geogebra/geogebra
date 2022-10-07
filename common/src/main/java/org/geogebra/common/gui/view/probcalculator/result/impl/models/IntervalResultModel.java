package org.geogebra.common.gui.view.probcalculator.result.impl.models;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.ResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.InputEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.TextEntry;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.Localization;

public class IntervalResultModel extends AbstractResultModel {

	private TextEntry probabilityOf;
	private TextEntry endProbabilityOf;
	private TextEntry between;
	private TextEntry resultEntry;

	private InputEntry lowEntry;
	private InputEntry highEntry;

	/**
	 * @param localization localization
	 */
	public IntervalResultModel(Localization localization) {
		probabilityOf = new TextEntry(localization.getMenu("ProbabilityOf"));
		endProbabilityOf = new TextEntry(localization.getMenu("EndProbabilityOf") + " = ");
		between = new TextEntry(SpreadsheetViewInterface.X_BETWEEN);
		lowEntry = new InputEntry("");
		highEntry = new InputEntry("");
		resultEntry = new TextEntry("");
	}

	@Override
	public void setLow(String low) {
		lowEntry = new InputEntry(low);
	}

	@Override
	public void setHigh(String high) {
		highEntry = new InputEntry(high);
	}

	@Override
	public void setResult(String result) {
		resultEntry = new TextEntry(result);
	}

	@Override
	public EditableResultEntry getLow() {
		return lowEntry;
	}

	@Override
	public EditableResultEntry getHigh() {
		return highEntry;
	}

	@Override
	public EditableResultEntry getResult() {
		return null;
	}

	@Override
	public List<ResultEntry> getEntries() {
		return Arrays.asList(probabilityOf, lowEntry, between, highEntry, endProbabilityOf,
				resultEntry);
	}
}
