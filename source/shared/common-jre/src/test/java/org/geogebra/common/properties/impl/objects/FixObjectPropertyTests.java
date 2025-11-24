package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.undo.UndoSavingPropertyObserver;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FixObjectPropertyTests extends BaseAppTestSetup {

	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)", // Free point
			"Point(Circle((0,0), 2))", // Point on circle
			"PointIn(Circle((0,0), 2))" // Point in circle
	})
	public void testSuccessfulConstruction(String input) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement(input);
		assertDoesNotThrow(() -> new FixObjectProperty(getLocalization(), geoElement));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{1, 2, 3}", // List
			"Intersect(y=x,y=-x)", // Intersect Point
			"Point(Circle((0,0), 2), 4)" // Point on path with parameter
	})
	public void testConstructingNotApplicableProperty(String input) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement(input);
		assertThrows(NotApplicablePropertyException.class,
				() -> new FixObjectProperty(getLocalization(), geoElement));
	}

	@Test
	public void testUndoRedo() {
		setupApp(SuiteSubApp.GRAPHING);
		getKernel().setUndoActive(true);
		getKernel().initUndoInfo();
		GeoPoint geoPoint = evaluateGeoElement("A = (1, 2)");
		getApp().storeUndoInfo();
		BooleanProperty fixObjectProperty = new GeoElementPropertiesFactory()
				.createFixObjectProperty(getLocalization(), List.of(geoPoint));
		fixObjectProperty.addValueObserver(
				new UndoSavingPropertyObserver(getApp().getUndoManager()));

		fixObjectProperty.setValue(true);
		assertTrue(getKernel().lookupLabel("A").isLocked());

		getKernel().undo();
		assertFalse(getKernel().lookupLabel("A").isLocked());

		getKernel().redo();
		assertTrue(getKernel().lookupLabel("A").isLocked());
	}
}
