package org.geogebra.common.gui.view.probcalculator.model.resultpanel;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.model.entry.AbstractEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.InputEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.TextEntry;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.Localization;

public class TwoTailedResultModel extends AbstractResultModel {

	private static final String GREATER_THAN_OR_EQUAL_TO_X =
			SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X;
	private static final String X_GREATER_THAN = "X >";

	private InputEntry low;
	private InputEntry high;
	private TextEntry greaterSign;
	private TextEntry twoTailedResult;
	private TextEntry result;

	/**
	 * @param localization localization
	 */
	public TwoTailedResultModel(Localization localization) {
		super(localization);
		low = new InputEntry();
		high = new InputEntry();
		greaterSign = new TextEntry(GREATER_THAN_OR_EQUAL_TO_X);
		twoTailedResult = new TextEntry();
		result = new TextEntry();
	}

	@Override
	public void updateLow(String low) {
		this.low.setText(low);
		twoTailedResult.setText(low + PLUS_SIGN + high + EQUALS_SIGN);
	}

	@Override
	public void updateHigh(String high) {
		this.high.setText(high);
		twoTailedResult.setText(low + PLUS_SIGN + high + EQUALS_SIGN);
	}

	@Override
	public void updateResult(String result) {
		this.result.setText(result);
	}

	@Override
	List<AbstractEntry> createEntries() {
		String probabilityOf = getLocalization().getMenu("ProbabilityOf");
		String xLessThanOrEqual = getLocalization().getMenu("XLessThanOrEqual");
		String endProbabilityOf = getLocalization().getMenu("EndProbabilityOf");

		return Arrays.asList(
				new TextEntry(probabilityOf + " " + xLessThanOrEqual),
				low,
				new TextEntry(" " + endProbabilityOf + PLUS_SIGN),

				new TextEntry(probabilityOf),
				greaterSign,
				high,
				new TextEntry(endProbabilityOf + EQUALS_SIGN),

				twoTailedResult,
				result
		);
	}

	public void setGreaterThan() {
		greaterSign.setText(X_GREATER_THAN);
	}

	public void setGreaterThanOrEqualTo() {
		greaterSign.setText(GREATER_THAN_OR_EQUAL_TO_X);
	}
}
