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

	/**
	 * @param localization localization
	 */
	public ProbabilityResultProperty(AlgebraProcessor processor, PropertyResultPanel resultPanel) {
		this.resultPanel = resultPanel;
		this.util = new NumericPropertyUtil(processor);
	}

	public ResultModel getModel() {
		return resultPanel.getModel();
	}

	public void setValue(EditableResultEntry entry, String text) {
		GeoNumberValue value = util.parseInputString(text);
		resultPanel.setValue(entry, value);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
