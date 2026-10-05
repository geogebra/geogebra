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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AVRedefinitionTest {

	private AppWFull app;

	@Before
	public void setup() {
		app = AppMocker.mockGraphing();
	}

	@Test
	public void syntaxErrorShouldBeRejectedWithGenericError() {
		RadioTreeItem item = getItem(add("a=1"));
		assertFalse(item.stopEditing("1+", "1+", null));
		assertEquals(app.getLocalization().getInvalidInputError(), item.errorMessage);
	}

	@Test
	public void circularDefinitionShouldBeRejectedWithError() {
		add("a=1");
		RadioTreeItem item = getItem(add("b=2a"));
		assertFalse(item.stopEditing("2b", "2b", null));
		assertNotNull(item.errorMessage);
	}

	@Test
	public void validRedefinitionShouldBeAccepted() {
		RadioTreeItem item = getItem(add("a=1"));
		List<GeoElementND> results = new ArrayList<>();

		assertTrue(item.stopEditing("2", "2", results::add));

		assertEquals(1, results.size());
		assertNull(item.errorMessage);
		assertNull(item.lastInput);
	}

	@Test
	public void trailingOperatorShouldBeIgnoredLikeForNewInput() {
		GeoElement a = add("a=1");
		RadioTreeItem item = getItem(a);

		assertTrue(item.stopEditing("a=2+", "a=2", null));

		assertEquals(2, a.getKernel().lookupLabel("a").evaluateDouble(), 0);
	}

	private GeoElement add(String command) {
		return (GeoElement)
				app.getKernel().getAlgebraProcessor().processAlgebraCommand(command, false)[0];
	}

	private RadioTreeItem getItem(GeoElement geo) {
		return app.getAlgebraView().getNode(geo);
	}
}
