package org.geogebra.common.properties.impl.distribution;

import org.geogebra.common.gui.view.probcalculator.PropertyResultPanel;
import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.ResultModel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.NumericPropertyUtil;

/**
 * The result property on the distribution view.
 */
public class ProbabilityResultProperty implements Property {

	/**
	 * The selected result model.
	 */
	private PropertyResultPanel resultPanel;
	private NumericPropertyUtil util;
	private boolean frozen = false;

	/**
	 * @param processor algebra processor
	 * @param resultPanel result panel
	 */
	public ProbabilityResultProperty(AlgebraProcessor processor, PropertyResultPanel resultPanel) {
		this.resultPanel = resultPanel;
		this.util = new NumericPropertyUtil(processor);
	}

	public ResultModel getModel() {
		return resultPanel.getModel();
	}

	/**
	 * Checks if some text is a valid input for an editable entry.
	 *
	 * @param entry the entry for which to validate the input
	 * @param text the input to validate
	 * @return `true` if `text` is a valid input to `entry`, `false` otherwise.
	 */
	public boolean isValidInput(EditableResultEntry entry, String text) {
		return util.isNumber(text);
	}

	/**
	 * Parses the text and passes it to the ResultPanel as the value of the entry.
	 * @param entry input field
	 * @param text value of the input field
	 */
	public void setValue(EditableResultEntry entry, String text) {
		if (isFrozen()) {
			return;
		}
		GeoNumberValue value = util.parseInputString(text);
		resultPanel.setValue(entry, value);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getRawName() {
		return "ProbabilityResult";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isFrozen() {
		return frozen;
	}

	@Override
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
}
