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

package org.geogebra.common.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.ElementSettingsData;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;
import org.geogebra.common.util.ImageManagerCommon;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObjectSettingsSessionTest extends BaseAppTestSetup {
	private final AtomicInteger notifications = new AtomicInteger();
	private GeoElement element;
	private ObjectSettingsSession session;

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
		getApp().setImageManager(new ImageManagerCommon());
		element = evaluateGeoElement("A = (1, 2)");
		session = new ObjectSettingsSession(getApp(), List.of(element));
		session.getTree().subscribe(tree -> notifications.incrementAndGet());
	}

	@Test
	void replacementPublishesNewTreeWithSelectedTab() {
		PropertyView.TabbedPageSelector original = session.getTree().get();
		String advanced = getLocalization().getMenu("Advanced");
		original.setSelectedTabIndex(original.getTabTitles().indexOf(advanced));
		evaluateGeoElement("A = true");

		PropertyView.TabbedPageSelector replacement = session.getTree().get();
		assertEquals(List.of(lookup("A")), session.getElements());
		assertNotSame(original, replacement);
		assertEquals(advanced, replacement.getTabTitles().get(replacement.getSelectedTabIndex()));
		assertEquals(1, notifications.get());
	}

	@Test
	void rebuildOfMultiElementSelectionPublishesOneTree() {
		evaluateGeoElement("B = (3, 4)");
		evaluateGeoElement("C = B + (1, 1)");
		session.dispose();
		session = new ObjectSettingsSession(getApp(), List.of(element, lookup("B"), lookup("C")));
		session.getTree().subscribe(tree -> notifications.incrementAndGet());

		evaluateGeoElement("B = A + (0, 3)");

		assertEquals(List.of(lookup("A"), lookup("B"), lookup("C")), session.getElements());
		assertEquals(1, notifications.get());
	}

	@Test
	void undoReplacesElementsAndKeepsSelectedTab() {
		getKernel().setUndoActive(true);
		getKernel().initUndoInfo();
		evaluateGeoElement("B = (3, 4)");
		getApp().storeUndoInfo();
		PropertyView.TabbedPageSelector original = session.getTree().get();
		String advanced = getLocalization().getMenu("Advanced");
		original.setSelectedTabIndex(original.getTabTitles().indexOf(advanced));

		getApp().getUndoManager().undo();

		PropertyView.TabbedPageSelector replacement = session.getTree().get();
		assertEquals(List.of(lookup("A")), session.getElements());
		assertNotSame(element, lookup("A"));
		assertEquals(advanced, replacement.getTabTitles().get(replacement.getSelectedTabIndex()));
		assertEquals(1, notifications.get());
	}

	@Test
	void unrelatedReplacementKeepsTree() {
		PropertyView.TabbedPageSelector original = session.getTree().get();
		evaluateGeoElement("B = (3, 4)");
		evaluateGeoElement("B = true");

		assertEquals(List.of(element), session.getElements());
		assertSame(original, session.getTree().get());
		assertEquals(0, notifications.get());
	}

	@Test
	void rebuildDetachesPreviousTree() {
		ShowObjectProperty property = new ShowObjectProperty(getLocalization(), element);
		AtomicInteger updates = addObservedCheckbox(property);
		session.rebuild();
		property.setValue(false);

		assertEquals(0, updates.get());
	}

	@Test
	void disposedSessionDetachesTreeAndIgnoresReplacement() {
		ShowObjectProperty property = new ShowObjectProperty(getLocalization(), element);
		AtomicInteger updates = addObservedCheckbox(property);
		session.dispose();
		property.setValue(false);
		evaluateGeoElement("A = true");

		assertEquals(0, updates.get());
		assertEquals(0, notifications.get());
		assertTrue(session.getElements().isEmpty());
	}

	@Test
	void contextReplacementTransfersRetainedData() {
		ObjectSettingsSession.Context context = new ObjectSettingsSession.Context();
		ElementSettingsData data = context.forElement(element);
		GeoElement replacement = evaluateGeoElement("B = (3, 4)");
		context.replace(element, replacement);

		assertSame(data, context.forElement(replacement));
		assertNotSame(data, context.forElement(element));
	}

	private AtomicInteger addObservedCheckbox(ShowObjectProperty property) {
		PropertyView.Checkbox checkbox = (PropertyView.Checkbox) PropertyView.of(property);
		AtomicInteger updates = new AtomicInteger();
		checkbox.setConfigurationUpdateDelegate(updates::incrementAndGet);
		session.getTree().get().getPageContents(0).add(checkbox);
		return updates;
	}
}
