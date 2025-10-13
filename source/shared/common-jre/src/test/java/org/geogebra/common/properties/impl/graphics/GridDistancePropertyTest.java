package org.geogebra.common.properties.impl.graphics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GridDistancePropertyTest extends BaseAppTestSetup {

	GridStyleProperty gridStyleProperty;
	GridDistanceProperty gridDistX;
	GridDistanceProperty gridDistY;
	GridDistanceProperty gridDistR;
	private EuclidianSettings evSettings;

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		LocalizationJre loc = getApp().getLocalization();
		evSettings = getApp().getSettings().getEuclidian(1);
		EuclidianView euclidianView = getApp().getEuclidianView1();

		gridDistX = new GridDistanceProperty(
				getAlgebraProcessor(), loc, euclidianView, "x", 0);
		gridDistY = new GridDistanceProperty(
				getAlgebraProcessor(), loc, euclidianView, "y", 1);
		gridDistR = new GridDistanceProperty(
				getAlgebraProcessor(), loc, euclidianView, "r", 0);
	}

	@Test
	void nameShouldDependOnGridType() {
		assertTrue(gridDistX.isAvailable(), "Should be available by default");
		assertTrue(gridDistY.isAvailable(), "Should be available by default");
		assertFalse(gridDistR.isAvailable(), "Should not be available by default");
		gridStyleProperty = new GridStyleProperty(getApp().getLocalization(), evSettings);
		gridStyleProperty.setValue(EuclidianView.GRID_POLAR);
		assertFalse(gridDistX.isAvailable(), "Should not be available for polar");
		assertFalse(gridDistY.isAvailable(), "Should not be available for polar");
		assertTrue(gridDistR.isAvailable(), "Should be available for polar");
	}

	@Test
	void shouldBeAvailableWhenFixed() {
		assertFalse(gridDistX.isEnabled(), "Should be disabled by default");
		assertFalse(gridDistY.isEnabled(), "Should be disabled by default");
		GridFixedDistanceProperty fixedDistanceProperty = new GridFixedDistanceProperty(
				getApp().getLocalization(), evSettings);
		fixedDistanceProperty.setValue(true);
		assertTrue(gridDistX.isEnabled(), "Should be enabled when fixed");
		assertTrue(gridDistY.isEnabled(), "Should be enabled when fixed");
	}
}
