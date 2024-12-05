package org.geogebra.common.properties.impl.objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.undo.UndoSavingPropertyObserver;
import org.junit.Test;

public class IsFixedObjectPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoElement point = addAvInput("(1,2)");
		try {
			new IsFixedObjectProperty(getLocalization(), point);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorThrowsError() {
		getApp().setGraphingConfig();
		GeoElement f = addAvInput("f: x");
		assertThrows(NotApplicablePropertyException.class,
				() -> new IsFixedObjectProperty(getLocalization(), f));
	}

	@Test
	public void fixedPropShouldBeUndoable() {
		getKernel().setUndoActive(true);
		getKernel().initUndoInfo();
		GeoElement point = addAvInput("pt=(1,2)");
		getApp().storeUndoInfo();
		ValuedProperty<Boolean> prop = new GeoElementPropertiesFactory().createFixObjectProperty(
				getApp().getLocalization(), Collections.singletonList(point));
		assert prop != null;
		prop.addValueObserver(new UndoSavingPropertyObserver(getConstruction().getUndoManager()));
		prop.setValue(true);
		assertThat(point.isLocked(), is(true));
		getKernel().undo();
		assertThat(lookup("pt").isLocked(), is(false));
		getKernel().redo();
		assertThat(lookup("pt").isLocked(), is(true));
	}
}