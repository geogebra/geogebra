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

package org.geogebra.common.properties.impl.undo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.ObjectColorProperty;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UndoSavingPropertyObserverTest extends BaseAppTestSetup {
	private static final GColor C1 = GColor.newColorRGB(0x670000);
	private static final GColor C2 = GColor.newColorRGB(0x9E4848);

	private ObjectColorProperty property;
	private GColor originalColor;

	@BeforeEach
	void setup() throws Exception {
		setupApp(SuiteSubApp.GRAPHING);
		getKernel().setUndoActive(true);
		getKernel().initUndoInfo();
		GeoElement point = evaluateGeoElement("A = (1, 2)");
		getApp().storeUndoInfo();
		originalColor = point.getObjectColor();
		property = new ObjectColorProperty(getLocalization(), point);
		property.addValueObserver(new UndoSavingPropertyObserver(getApp().getUndoManager()));
	}

	@Test
	void setValueOutsideBatchStoresUndoPointEachTime() {
		property.setValue(C1);
		property.setValue(C2);
		getKernel().undo();
		assertEquals(C1, lookup("A").getObjectColor());
	}

	@Test
	void batchStoresSingleUndoPoint() {
		property.beginSetValue();
		property.setValue(C1);
		property.setValue(C2);
		property.endSetValue();
		assertEquals(C2, lookup("A").getObjectColor());
		getKernel().undo();
		assertEquals(originalColor, lookup("A").getObjectColor());
	}

	@Test
	void batchWithoutSetValueStoresNothing() {
		int historySize = getApp().getUndoManager().getHistorySize();
		property.beginSetValue();
		property.endSetValue();
		assertEquals(historySize, getApp().getUndoManager().getHistorySize());
	}
}
