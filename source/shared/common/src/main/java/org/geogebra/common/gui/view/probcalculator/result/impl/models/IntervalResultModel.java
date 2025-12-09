/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
