package org.geogebra.common.gui.view.probcalculator.model.resultpanel;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.model.entry.AbstractEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.InputEntry;
import org.geogebra.common.gui.view.probcalculator.model.entry.StaticTextEntry;
import org.geogebra.common.main.Localization;

public class LeftResultModel extends AbstractResultModel {

	private InputEntry high;
	private InputEntry result;

	/**
	 * @param localization localization
	 */
	public LeftResultModel(Localization localization) {
		super(localization);
		high = new InputEntry();
		result = new InputEntry();
	}

	@Override
	public void updateLow(String low) {
		// no-op
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
		String xLessThanOrEqual = getLocalization().getMenu("XLessThanOrEqual");
		String endProbabilityOf = getLocalization().getMenu("EndProbabilityOf");

		return Arrays.asList(
				new StaticTextEntry(probabilityOf + " " + xLessThanOrEqual),
				high,
				new StaticTextEntry(endProbabilityOf + EQUALS_SIGN),
				result
		);
	}
}
