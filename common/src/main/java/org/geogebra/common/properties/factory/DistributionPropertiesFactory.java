package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.gui.view.probcalculator.PropertyResultPanel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.distribution.DistributionTypeProperty;
import org.geogebra.common.properties.impl.distribution.IntervalProperty;
import org.geogebra.common.properties.impl.distribution.IsCumulativeProperty;
import org.geogebra.common.properties.impl.distribution.ParameterProperty;
import org.geogebra.common.properties.impl.distribution.ProbabilityResultProperty;

public class DistributionPropertiesFactory implements PropertiesFactory {

	private final ProbabilityCalculatorView probabilityCalculatorView;
	private String[][] labels;

	/**
	 * Distribution properties factory
	 * @param probabilityCalculatorView view
	 */
	public DistributionPropertiesFactory(ProbabilityCalculatorView probabilityCalculatorView) {
		this.probabilityCalculatorView = probabilityCalculatorView;
	}

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		ensureLabelsExist(localization);

		List<Property> properties = new ArrayList<>(Arrays.asList(
				new DistributionTypeProperty(localization, probabilityCalculatorView),
				new IsCumulativeProperty(localization, probabilityCalculatorView),
				new IntervalProperty(localization, probabilityCalculatorView)
		));

		ProbabilityCalculatorSettings.Dist distribution =
				probabilityCalculatorView.getSelectedDist();
		int count = ProbabilityManager.getParmCount(distribution);
		for (int parameterIndex = 0; parameterIndex < count; parameterIndex++) {
			ParameterProperty property = new ParameterProperty(
					localization,
					app.getKernel().getAlgebraProcessor(),
					probabilityCalculatorView,
					parameterIndex,
					labels[distribution.ordinal()][parameterIndex]
			);
			properties.add(property);
		}
		properties.add(new ProbabilityResultProperty(app.getKernel().getAlgebraProcessor(),
						(PropertyResultPanel) probabilityCalculatorView.getResultPanel()));

		PropertiesArray array = new PropertiesArray(localization.getMenu("Distribution"),
				registerProperties(propertiesRegistry, properties));
		return Arrays.asList(array);
	}

	private void ensureLabelsExist(Localization localization) {
		if (labels == null) {
			labels = probabilityCalculatorView.getProbManager()
					.getParameterLabelArrayPrefixed(localization);
		}
	}
}
