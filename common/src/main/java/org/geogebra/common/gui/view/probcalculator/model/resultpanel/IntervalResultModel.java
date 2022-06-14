package org.geogebra.common.gui.view.probcalculator.model.resultpanel;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.model.entry.AbstractEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.InputEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.StaticTextEntry;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.Localization;

public class IntervalResultModel extends AbstractResultModel {

	private InputEntry low;
	private InputEntry high;
	private StaticTextEntry result;

	public IntervalResultModel(Localization localization) {
		super(localization);
		low = new InputEntry();
		high = new InputEntry();
		result = new StaticTextEntry();
	}

	@Override
	public void updateResult(String result) {
		this.result.setText(result);
	}

	@Override
	List<AbstractEntry> createEntries() {
		return Arrays.asList(
				new StaticTextEntry(getLocalization().getMenu("ProbabilityOf")),
				low,
				new StaticTextEntry(SpreadsheetViewInterface.X_BETWEEN),
				high,
				new StaticTextEntry(getLocalization().getMenu("EndProbabilityOf") + " = "),
				result
		);
	}

	public void setLow(String low) {
		this.low.setText(low);
	}

	public void setHigh(String high) {
		this.high.setText(high);
	}
}
