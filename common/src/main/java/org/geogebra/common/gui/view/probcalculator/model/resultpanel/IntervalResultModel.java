package org.geogebra.common.gui.view.probcalculator.model.resultpanel;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.model.entry.AbstractEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.InputEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.TextEntry;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.Localization;

public class IntervalResultModel extends AbstractResultModel {

	private InputEntry low;
	private InputEntry high;
	private TextEntry result;

	/**
	 * @param localization localization
	 */
	public IntervalResultModel(Localization localization) {
		super(localization);
		low = new InputEntry();
		high = new InputEntry();
		result = new TextEntry();
	}

	@Override
	public void updateLow(String low) {
		this.low.setText(low);
	}

	@Override
	public void updateHigh(String high) {
		this.high.setText(high);
	}

	@Override
	public void updateResult(String result) {
		this.result.setText(result);
	}

	@Override
	List<AbstractEntry> createEntries() {
		String probabilityOf = getLocalization().getMenu("ProbabilityOf");
		String endProbabilityOf = getLocalization().getMenu("EndProbabilityOf");

		return Arrays.asList(
				new TextEntry(probabilityOf),
				low,
				new TextEntry(SpreadsheetViewInterface.X_BETWEEN),
				high,
				new TextEntry(endProbabilityOf + EQUALS_SIGN),
				result
		);
	}
}
