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
		ValuedProperty<Boolean> prop = new GeoElementPropertiesFactory()
				.createIsFixedObjectProperty(getApp().getLocalization(),
						Collections.singletonList(point));
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