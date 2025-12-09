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

package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

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
import org.geogebra.common.util.NonNullList;

/**
 * Factory for properties of the Classic app.
 */
public class ClassicPropertiesFactory extends DefaultPropertiesFactory {

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return NonNullList.of(
				createGeneralProperties(app, localization, propertiesRegistry),
				createAlgebraProperties(app, localization, propertiesRegistry),
				createStructuredGraphicsProperties(app, localization, propertiesRegistry),
				createStructuredGraphics2Properties(app, localization, propertiesRegistry),
				createStructuredGraphics3DProperties(app, localization, propertiesRegistry),
				createSpreadsheetProperties(localization, app),
				createCASProperties(localization, app));
	}

	@Override
	protected PropertiesArray createStructuredGraphicsProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		if (!app.getGuiManager().showView(App.VIEW_EUCLIDIAN)) {
			return null;
		}

		return super.createStructuredGraphicsProperties(app, localization, propertiesRegistry);
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

	@Override
	protected PropertiesArray createStructuredGraphics2Properties(App app,
			Localization localization, PropertiesRegistry propertiesRegistry) {
		if (!app.getGuiManager().showView(App.VIEW_EUCLIDIAN2)) {
			return null;
		}

		return super.createStructuredGraphics2Properties(app, localization, propertiesRegistry);
	}

	@Override
	protected PropertiesArray createStructuredGraphics3DProperties(App app,
			Localization localization, PropertiesRegistry propertiesRegistry) {
		if (!app.getGuiManager().showView(App.VIEW_EUCLIDIAN3D)) {
			return null;

		}
		return super.createStructuredGraphics3DProperties(app, localization, propertiesRegistry);
	}

	private PropertiesArray createSpreadsheetProperties(Localization localization, App app) {
		if (!app.getGuiManager().showView(App.VIEW_SPREADSHEET)) {
			return null;
		}

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		List<Property> props = List.of(
				new SimpleBooleanProperty(localization, "ShowGridlines",
						settings::showGrid, settings::setShowGrid),
				new SimpleBooleanProperty(localization, "ShowColumnHeader",
						settings::showColumnHeader, settings::setShowColumnHeader),
				new SimpleBooleanProperty(localization, "ShowRowHeader",
						settings::showRowHeader, settings::setShowRowHeader),
				new SimpleBooleanProperty(localization, "ShowVerticalScrollbars",
						settings::showVScrollBar, settings::setShowVScrollBar),
				new SimpleBooleanProperty(localization, "ShowHorizontalScrollbars",
						settings::showHScrollBar, settings::setShowHScrollBar),
				new SimpleBooleanProperty(localization, "UseButtonsAndCheckboxes",
						settings::allowSpecialEditor, settings::setAllowSpecialEditor),
				new SimpleBooleanProperty(localization, "AllowTooltips",
						settings::allowToolTips, settings::setAllowToolTips),
				new SimpleBooleanProperty(localization, "RequireEquals",
						settings::equalsRequired, settings::setEqualsRequired),
				new SimpleBooleanProperty(localization, "UseAutoComplete",
						settings::isEnableAutoComplete, settings::setEnableAutoComplete),
				createNavBarProperty(app, App.VIEW_SPREADSHEET),
				createSpreadsheetAlgebraDescriptionProperty(app));
		return new PropertiesArray("Spreadsheet", localization, props);
	}

	private Property createNavBarProperty(App app, int viewID) {
		return new SimpleBooleanProperty(app.getLocalization(), "NavigationBar",
				() -> app.showConsProtNavigation(viewID),
				flag -> app.setShowConstructionProtocolNavigation(flag, viewID));
	}

	private Property createSpreadsheetAlgebraDescriptionProperty(App app) {
		AlgebraDescriptionProperty property = new AlgebraDescriptionProperty(app,
				app.getLocalization());
		property.usesSpreadsheet(true);

		return property;
	}

	private PropertiesArray createCASProperties(Localization localization, App app) {
		if (!app.getGuiManager().showView(App.VIEW_CAS)) {
			return null;
		}

		CASSettings settings = app.getSettings().getCasSettings();
		List<Property> props = List.of(
				new SimpleBooleanProperty(localization, "CASShowRationalExponentsAsRoots",
						settings::getShowExpAsRoots, settings::setShowExpAsRoots),
				createNavBarProperty(app, App.VIEW_CAS));
		return new PropertiesArray("CAS", localization, props);
	}
}
