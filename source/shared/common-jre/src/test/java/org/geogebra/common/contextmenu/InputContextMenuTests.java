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

package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.InputContextMenuItem.Expression;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Help;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Text;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class InputContextMenuTests extends BaseUnitTest {

	@Test
	public void testWithHelpDisabled() {
		assertEquals(
				List.of(Expression,
						Text),
				ContextMenuFactory.makeInputContextMenu(false, Set.of())
		);
	}

	@Test
	public void testWithHelpEnabled() {
		assertEquals(
				List.of(Expression,
						Text,
						Help),
				ContextMenuFactory.makeInputContextMenu(true, Set.of())
		);
	}
}
