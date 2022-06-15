package org.geogebra.common.gui.view.probcalculator.model.resultpanel;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.model.entry.AbstractEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.InputEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.TextEntry;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.Localization;

public class RightResultModel extends AbstractResultModel {

	private static final String LESS_THAN_OR_EQUAL_TO_X =
			SpreadsheetViewInterface.LESS_THAN_OR_EQUAL_TO_X;

	private InputEntry low;
	private InputEntry result;

	public RightResultModel(Localization localization) {
		super(localization);
		low = new InputEntry();
		result = new InputEntry();
	}

	@Override
	public void updateLow(String low) {
		this.low.setText(low);
	}

	@Override
	public void updateHigh(String high) {
		// no-op
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
				new TextEntry(LESS_THAN_OR_EQUAL_TO_X + endProbabilityOf + EQUALS_SIGN),
				result
		);
	}
}
