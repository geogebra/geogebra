package org.geogebra.common.gui.view.probcalculator.result.impl.models;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.ResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.InputEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.TextEntry;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.Localization;

public class RightResultModel extends AbstractResultModel {

	private static final String LESS_THAN_OR_EQUAL_TO_X =
			SpreadsheetViewInterface.LESS_THAN_OR_EQUAL_TO_X;

	private TextEntry probabilityOf;
	private TextEntry lessThan;

	private InputEntry lowEntry;
	private InputEntry resultEntry;

	/**
	 * @param localization localization
	 */
	public RightResultModel(Localization localization) {
		String endProbabilityOf = localization.getMenu("EndProbabilityOf");

		probabilityOf = new TextEntry(localization.getMenu("ProbabilityOf"));
		lessThan = new TextEntry(LESS_THAN_OR_EQUAL_TO_X + endProbabilityOf + EQUALS_SIGN);
		lowEntry = new InputEntry("");
		resultEntry = new InputEntry("");
	}

	@Override
	public void setLow(String low) {
		lowEntry = new InputEntry(low);
	}

	@Override
	public void setHigh(String high) {
		// no-op
	}

	@Override
	public void setResult(String result) {
		resultEntry = new InputEntry(result);
	}

	@Override
	public EditableResultEntry getLow() {
		return lowEntry;
	}

	@Override
	public EditableResultEntry getHigh() {
		return null;
	}

	@Override
	public EditableResultEntry getResult() {
		return resultEntry;
	}

	@Override
	public List<ResultEntry> getEntries() {
		return Arrays.asList(probabilityOf, lowEntry, lessThan, resultEntry);
	}
}
