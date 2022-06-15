package org.geogebra.common.gui.view.probcalculator.model.resultpanel;

import java.util.List;

import org.geogebra.common.gui.view.probcalculator.model.entry.AbstractEntry;
import org.geogebra.common.main.Localization;

public abstract class AbstractResultModel {

	static final String PLUS_SIGN = " + ";
	static final String EQUALS_SIGN = " = ";

	private List<AbstractEntry> entries;
	private Localization localization;

	public AbstractResultModel(Localization localization) {
		this.localization = localization;
	}

	public abstract void updateLow(String low);

	public abstract void updateHigh(String high);

	public abstract void updateResult(String result);

	abstract List<AbstractEntry> createEntries();

	/**
	 * @return returns the entries.
	 * If it's null, then first calls createEntries() and assigns the result to the entries.
	 */
	public List<AbstractEntry> getEntries() {
		if (entries == null) {
			entries = createEntries();
		}
		return entries;
	}

	Localization getLocalization() {
		return localization;
	}
}
