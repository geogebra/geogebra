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

public class GridAnglePropertyTest extends BaseAppTestSetup {

	GridStyleProperty gridStyleProperty;
	GridAngleProperty gridAngle;
	private EuclidianSettings evSettings;

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		LocalizationJre loc = getApp().getLocalization();
		evSettings = getApp().getSettings().getEuclidian(1);
		EuclidianView euclidianView = getApp().getEuclidianView1();
		gridAngle = new GridAngleProperty(getAlgebraProcessor(), loc, euclidianView);
	}

	@Test
	void nameShouldDependOnGridType() {
		assertFalse(gridAngle.isAvailable(), "Should not be available by default");
		gridStyleProperty = new GridStyleProperty(getApp().getLocalization(), evSettings);
		gridStyleProperty.setValue(EuclidianView.GRID_POLAR);
		assertTrue(gridAngle.isAvailable(), "Should be available for polar");
	}

	@Test
	void shouldBeAvailableWhenFixed() {
		assertFalse(gridAngle.isEnabled(), "Should be disabled by default");
		GridFixedDistanceProperty fixedDistanceProperty = new GridFixedDistanceProperty(
				getApp().getLocalization(), evSettings);
		fixedDistanceProperty.setValue(true);
		assertTrue(gridAngle.isEnabled(), "Should be enabled when fixed");
	}
}
