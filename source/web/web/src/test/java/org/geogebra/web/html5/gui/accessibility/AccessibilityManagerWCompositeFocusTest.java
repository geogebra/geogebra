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

package org.geogebra.web.html5.gui.accessibility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.compositefocus.FocusableComposite;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AccessibilityManagerWCompositeFocusTest {

	private AccessibilityManagerInterface accessibilityManager;

	@Before
	public void setUp() {
		AppWFull app = AppMocker.mockGeometry();
		accessibilityManager = app.getAccessibilityManager();
	}

	@Test
	public void hasFocusInCompositeShouldBeFalseWhenNoneIsRegistered() {
		assertFalse(accessibilityManager.hasFocusInComposite());
		assertFalse(accessibilityManager.focusNextInComposite());
		assertFalse(accessibilityManager.focusPreviousInComposite());
		assertFalse(accessibilityManager.handlesEnterInComposite());
	}

	@Test
	public void unregisterShouldClearActiveCompositeFocusState() {
		FocusableComposite composite = mock(FocusableComposite.class);
		when(composite.isFocused()).thenReturn(true);
		when(composite.hasFocus()).thenReturn(true);
		when(composite.focusNext()).thenReturn(true);
		when(composite.handlesEnterKeyForSelectedPart()).thenReturn(true);

		accessibilityManager.registerCompositeFocusContainer(composite);

		assertTrue(accessibilityManager.hasFocusInComposite());
		assertTrue(accessibilityManager.focusNextInComposite());
		assertTrue(accessibilityManager.handlesEnterInComposite());

		accessibilityManager.unregisterCompositeFocusContainer(composite);

		assertFalse(accessibilityManager.hasFocusInComposite());
		assertFalse(accessibilityManager.focusNextInComposite());
		assertFalse(accessibilityManager.handlesEnterInComposite());
	}
}
