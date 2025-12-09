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

package org.geogebra.common.gui.dialog.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.test.UndoRedoTester;
import org.junit.Before;
import org.junit.Test;

public class DefineFunctionHandlerTest extends BaseUnitTest {
	private DefineFunctionHandler handler;
	private Construction cons;

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Before
	public void setUp() {
		getApp().setScientificConfig();
		handler = new DefineFunctionHandler(getApp().getKernel());
		cons = getConstruction();
	}

	@Test
	public void testChangeF() {
		GeoEvaluatable f = createFunction(cons, "f");
		handler.handle("2x", f);
		functionShouldBe("f(x) = 2x", f);
	}

	@Test
	public void testChangeFWhenNoStructuresEnabled() {
		AlgebraProcessor ap = cons.getKernel().getAlgebraProcessor();
		ap.setEnableStructures(false);
		GeoEvaluatable f = createFunction(cons, "f");
		handler.handle("2x", f);
		assertFalse(ap.enableStructures());
		functionShouldBe("f(x) = 2x", f);
	}

	private GeoEvaluatable createFunction(Construction construction, String label) {
		GeoFunction function = new GeoFunction(construction);
		function.rename(label);
		function.setAuxiliaryObject(true);
		return function;
	}

	@Test
	public void testChangeFToUndefined() {
		GeoEvaluatable f = createFunction(cons, "f");
		handler.handle("2x", f);
		functionShouldBe("f(x) = 2x", f);
		handler.handle("", f);
		functionShouldBe("f(x) = ?", f);
	}

	@Test
	public void testUndefinedVariableShouldGiveError() {
		GeoEvaluatable f = createFunction(cons, "f");
		handler.handle("ln(z)", f);
		assertTrue(handler.hasErrorOccurred());
	}

	@Test
	public void testSimpleUndo() {
		GeoEvaluatable f = createFunction(cons, "f");
		App app = getApp();
		UndoRedoTester undoRedo = new UndoRedoTester(app);
		undoRedo.setupUndoRedo();
		handler.handle("x", f);
		app.storeUndoInfo();
		handler.handle("2x", f);
		app.storeUndoInfo();
		functionShouldBe("f(x) = x", undoRedo.getAfterUndo("f"));
	}

	private void functionShouldBe(String expected, GeoEvaluatable geoEvaluatable) {
		assertEquals(expected, geoEvaluatable.toString(StringTemplate.defaultTemplate));
	}

}
