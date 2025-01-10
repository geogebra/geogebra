package org.geogebra.common.gui.view.probcalculator.result.impl.models;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.ResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.InputEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.TextEntry;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.Localization;

public class TwoTailedResultModel extends AbstractResultModel {

	private static final String GREATER_THAN_OR_EQUAL_TO_X =
			SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X;
	private static final String X_GREATER_THAN = "X >";

	private TextEntry probabilityOf;
	private TextEntry probabilityOfLessThanOrEqual;
	private TextEntry greaterSign;
	private TextEntry twoTailedResult;
	private TextEntry result;
	private TextEntry endProbabilityOf;
	private TextEntry endProbabilityEquals;

	private InputEntry low;
	private InputEntry high;

	/**
	 * @param localization localization
	 */
	public TwoTailedResultModel(Localization localization) {
		String probabilityOfText = localization.getMenu("ProbabilityOf");
		String xLessThanOrEqualText = localization.getMenu("XLessThanOrEqual");
		String endProbabilityOfText = localization.getMenu("EndProbabilityOf");

		probabilityOfLessThanOrEqual =
				new TextEntry(probabilityOfText + " " + xLessThanOrEqualText);
		probabilityOf = new TextEntry(probabilityOfText);
		endProbabilityOf = new TextEntry(" " + endProbabilityOfText + PLUS_SIGN);
		endProbabilityEquals = new TextEntry(endProbabilityOfText + EQUALS_SIGN);

		low = new InputEntry("");
		high = new InputEntry("");
		greaterSign = new TextEntry(GREATER_THAN_OR_EQUAL_TO_X);
		twoTailedResult = new TextEntry("");
		result = new TextEntry("");
	}

	@Override
	public void setLow(String low) {
		this.low = new InputEntry(low);
	}

	@Override
	public void setHigh(String high) {
		this.high = new InputEntry(high);
	}

	public void setTwoTailedResult(String leftResult, String rightResult) {
		twoTailedResult = new TextEntry(leftResult + PLUS_SIGN + rightResult + EQUALS_SIGN);
	}

	@Override
	public void setResult(String result) {
		this.result = new TextEntry(result);
	}

	@Override
	public EditableResultEntry getLow() {
		return low;
	}

	@Override
	public EditableResultEntry getHigh() {
		return high;
	}

	@Override
	public EditableResultEntry getResult() {
		return null;
	}

	@Override
	public List<ResultEntry> getEntries() {
		return Arrays.asList(probabilityOfLessThanOrEqual, low, endProbabilityOf,
				probabilityOf, greaterSign, high, endProbabilityEquals,
				twoTailedResult,
				result);
	}

	public void setGreaterThan() {
		greaterSign = new TextEntry(X_GREATER_THAN);
	}

	public void setGreaterThanOrEqualTo() {
		greaterSign = new TextEntry(GREATER_THAN_OR_EQUAL_TO_X);
	}
}
