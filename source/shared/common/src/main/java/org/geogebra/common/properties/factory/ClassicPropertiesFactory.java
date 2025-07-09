package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.CASSettings;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.algebra.AlgebraDescriptionProperty;
import org.geogebra.common.properties.impl.algebra.ShowAuxiliaryProperty;
import org.geogebra.common.properties.impl.algebra.SortByProperty;

/**
 * Factory for properties of the Classic app.
 */
public class ClassicPropertiesFactory extends DefaultPropertiesFactory {

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return Arrays.asList(
				createGeneralProperties(app, localization, propertiesRegistry),
				createStructuredGraphicsProperties(app, localization, propertiesRegistry),
				createAlgebraProperties(app, localization, propertiesRegistry),
				createSpreadsheetProperties(localization, app),
				createCASProperties(localization, app));
	}

	@Override
	protected PropertiesArray createAlgebraProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return new PropertiesArray("Algebra", localization,
				registerProperties(propertiesRegistry,
						new SortByProperty(app.getSettings().getAlgebra(), localization),
						new AlgebraDescriptionProperty(app, localization),
						new ShowAuxiliaryProperty(app, localization)));
	}

	private PropertiesArray createSpreadsheetProperties(Localization localization, App app) {
		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		List<Property> props = List.of(
				new SimpleBooleanProperty(localization, "ShowInputField",
						settings::showFormulaBar, settings::setShowFormulaBar),
				new SimpleBooleanProperty(localization, "ShowRowHeader",
						settings::showGrid, settings::setShowGrid),
				new SimpleBooleanProperty(localization, "ShowRowHeader",
						settings::showRowHeader, settings::setShowRowHeader),
				new SimpleBooleanProperty(localization, "ShowColumnHeader",
						settings::showColumnHeader, settings::setShowColumnHeader),
				new SimpleBooleanProperty(localization, "ShowHorizontalScrollbars",
						settings::showHScrollBar, settings::setShowHScrollBar),
				new SimpleBooleanProperty(localization, "ShowVerticalScrollbars",
						settings::showVScrollBar, settings::setShowVScrollBar),
				new SimpleBooleanProperty(localization, "UseButtonsAndCheckboxes",
						settings::allowSpecialEditor, settings::setAllowSpecialEditor),
				new SimpleBooleanProperty(localization, "AllowTooltips",
						settings::allowToolTips, settings::setAllowToolTips),
				new SimpleBooleanProperty(localization, "RequireEquals",
						settings::equalsRequired, settings::setEqualsRequired),
				new SimpleBooleanProperty(localization, "UseAutoComplete",
						settings::isEnableAutoComplete, settings::setEnableAutoComplete),
				createNavBarProperty(app, App.VIEW_SPREADSHEET));
		return new PropertiesArray("Spreadsheet", localization, props);
	}

	private Property createNavBarProperty(App app, int viewID) {
		return new SimpleBooleanProperty(app.getLocalization(), "NavigationBar",
				() -> app.showConsProtNavigation(viewID),
				flag -> app.setShowConstructionProtocolNavigation(flag, viewID));
	}

	private PropertiesArray createCASProperties(Localization localization, App app) {
		CASSettings settings = app.getSettings().getCasSettings();
		List<Property> props = List.of(
				new SimpleBooleanProperty(localization, "CASShowRationalExponentsAsRoots",
						settings::getShowExpAsRoots, settings::setShowExpAsRoots),
				createNavBarProperty(app, App.VIEW_CAS));
		return new PropertiesArray("CAS", localization, props);
	}

}
