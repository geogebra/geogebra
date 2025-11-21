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

public class FixObjectPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSuccessfulConstruction() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		assertDoesNotThrow(() -> new FixObjectProperty(getLocalization(), geoElement));
	}

	@Test
	public void testConstructingNotApplicableProperty() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("{1, 2, 3}");
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
