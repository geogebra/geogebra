package org.geogebra.common.properties.factory;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertiesList;
import org.geogebra.common.properties.impl.general.CoordinatesProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LabelingProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingProperty;

public class CasPropertiesFactory implements PropertiesFactory {

	private PropertiesFactory basePropertiesFactory = new BasePropertiesFactory();

	@Override
	public PropertiesList createGeneralProperties(
			App app,
			Localization localization,
			LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {

		Kernel kernel = app.getKernel();

		return new PropertiesList(
				new RoundingProperty(app, localization),
				new LabelingProperty(app, localization),
				new CoordinatesProperty(kernel, localization),
				new FontSizeProperty(app, localization),
				new LanguageProperty(app, localization, onLanguageSetCallback));
	}

	@Override
	public PropertiesList createAlgebraProperties(App app, Localization localization) {
		return basePropertiesFactory.createAlgebraProperties(app, localization);
	}

	@Override
	public PropertiesList createGraphicsProperties(App app, Localization localization) {
		return basePropertiesFactory.createGraphicsProperties(app, localization);
	}
}
