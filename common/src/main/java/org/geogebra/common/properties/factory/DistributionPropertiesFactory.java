package org.geogebra.common.properties.factory;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.distribution.DistributionTypeProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;

public class DistributionPropertiesFactory implements PropertiesFactory {

	private final ProbabilityCalculatorView probabilityCalculatorView;

	/**
	 * Distribution properties factory
	 * @param probabilityCalculatorView view
	 */
	public DistributionPropertiesFactory(ProbabilityCalculatorView probabilityCalculatorView) {
		this.probabilityCalculatorView = probabilityCalculatorView;
	}

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {

		PropertiesArray array = new PropertiesArray(localization.getMenu("Distribution"),
				new DistributionTypeProperty(localization, probabilityCalculatorView));
		return Arrays.asList(array);
	}
}
