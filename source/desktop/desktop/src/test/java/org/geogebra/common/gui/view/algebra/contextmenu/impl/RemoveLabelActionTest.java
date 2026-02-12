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

package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.contextmenu.AlgebraContextMenuItem;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Test;

public class RemoveLabelActionTest extends BaseSymbolicTest {

	@Test
	public void shouldForceLabelsForSliders() {
		GeoElement integral = add("Integral(sin(x))");
		GeoNumeric constant = (GeoNumeric) lookup("c_{1}");
		constant.initAlgebraSlider();
		List<AlgebraContextMenuItem> items = ContextMenuFactory.makeAlgebraContextMenu(
				constant, ap, app.getConfig().getAppCode(), app.getSettings().getAlgebra(),
				Set.of());
		assertFalse("Remove label should not be available for sliders in CAS",
				items.contains(AlgebraContextMenuItem.RemoveLabel));
		items = ContextMenuFactory.makeAlgebraContextMenu(
				integral, ap, app.getConfig().getAppCode(), app.getSettings().getAlgebra(),
				Set.of());
		assertTrue("Remove label should be available for integral in CAS",
				items.contains(AlgebraContextMenuItem.RemoveLabel));
	}
}
