package org.geogebra.common.gui.view.probcalculator.result.impl.models;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.gui.view.probcalculator.result.ResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.InputEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.TextEntry;

public class TwoTailedResultModel extends AbstractResultModel {

	private static final String GREATER_THAN_OR_EQUAL_TO_X =
			SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X;
	private static final String X_GREATER_THAN = "X >";

	private TextEntry probabilityOfEntry;
	private TextEntry probabilityOfLessThanOrEqual;
	private TextEntry greaterSign;
	private TextEntry twoTailedResult;
	private TextEntry resultEntry;
	private TextEntry endProbabilityOf;
	private TextEntry endProbabilityEquals;

	private InputEntry lowEntry;
	private InputEntry highEntry;

	/**
	 * @param localization localization
	 */
	public TwoTailedResultModel(Localization localization) {
		String probabilityOf = localization.getMenu("ProbabilityOf");
		String xLessThanOrEqual = localization.getMenu("XLessThanOrEqual");
		String endProbability = localization.getMenu("EndProbabilityOf");

		probabilityOfLessThanOrEqual = new TextEntry(probabilityOf + " " + xLessThanOrEqual);
		probabilityOfEntry = new TextEntry(probabilityOf);
		endProbabilityOf = new TextEntry(" " + endProbabilityOf + PLUS_SIGN);
		endProbabilityEquals = new TextEntry(endProbabilityOf + EQUALS_SIGN);

		lowEntry = new InputEntry("");
		highEntry = new InputEntry("");
		greaterSign = new TextEntry(GREATER_THAN_OR_EQUAL_TO_X);
		twoTailedResult = new TextEntry("");
		resultEntry = new TextEntry("");
	}

	@Override
	public void setLow(String low) {
		lowEntry = new InputEntry(low);
		twoTailedResult = new TextEntry(low + PLUS_SIGN + highEntry + EQUALS_SIGN);
	}

	@Override
	public void setHigh(String high) {
		highEntry = new InputEntry(high);
		twoTailedResult = new TextEntry(lowEntry + PLUS_SIGN + high + EQUALS_SIGN);
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
		return Arrays.asList(probabilityOfLessThanOrEqual, lowEntry, endProbabilityOf,
				probabilityOfEntry, greaterSign, highEntry, endProbabilityEquals, twoTailedResult,
				resultEntry);
	}

	public void setGreaterThan() {
		greaterSign = new TextEntry(X_GREATER_THAN);
	}

	public void setGreaterThanOrEqualTo() {
		greaterSign = new TextEntry(GREATER_THAN_OR_EQUAL_TO_X);
	}
}
