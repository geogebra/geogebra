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

package org.geogebra.web.full.gui.view.algebra;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormatFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.web.full.gui.view.algebra.compositefocus.AddGeosSetup;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AlgebraViewWStyleChangeCompositeFocusTest extends AddGeosSetup {

	@Before
	public void setup() {
		initApp();
	}

	@Test
	public void styleChangeShouldKeepCompositeTraversalWorking() {
		GeoElement geo = add("1/2");
		RadioTreeItem item = new ItemFactory().createAVItem(geo);
		AlgebraSettings settings = getApp().getSettings().getAlgebra();
		AccessibilityManagerInterface accessibilityManager = getApp().getAccessibilityManager();
		assertNotNull(item);

		getApp().getSelectionManager().addSelectedGeoForEV(geo);
		assertTrue(accessibilityManager.focusNextInComposite());

		settings.setStyle(AlgebraStyle.DEFINITION);
		item.rebuild();

		getApp().getSelectionManager().addSelectedGeoForEV(geo);
		assertTrue(accessibilityManager.focusNextInComposite());
	}

	@Test
	public void removingOutputFormatButtonShouldRebuildCompositeWithoutRecursion() {
		GeoElement geo = add("1/2");
		RadioTreeItem item = new ItemFactory().createAVItem(geo);
		AlgebraSettings settings = getApp().getSettings().getAlgebra();
		AlgebraOutputFormatFilter denyAllFormats = (g, f) -> false;
		assertNotNull(item);

		settings.setStyle(AlgebraStyle.DEFINITION_AND_VALUE);
		item.rebuild();
		assertNotNull(new RadioTreeItemFocusAccess(item).outputFormatButton());

		settings.addAlgebraOutputFormatFilter(denyAllFormats);
		item.rebuild();
		assertNull(new RadioTreeItemFocusAccess(item).outputFormatButton());
	}
}
