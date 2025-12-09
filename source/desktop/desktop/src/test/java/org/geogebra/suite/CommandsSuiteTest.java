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
 
package org.geogebra.suite;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.UndoRedoTester;
import org.junit.Before;
import org.junit.Test;

public class CommandsSuiteTest extends BaseSuiteTest {

	private AppCommon app;
	private UndoRedoTester undoRedo;

	@Before
	public void setUp() {
		app = getApp();
		undoRedo = new UndoRedoTester(app);
		undoRedo.setupUndoRedo();
	}

	@Test
	public void testSolveEnabled() {
		GeoElement element = add("Solve(x)");
		outputShouldBe(element, "l1 = {x = 0}");
	}

	private static void outputShouldBe(GeoElement element, String value) {
		assertThat(element.toString(StringTemplate.defaultTemplate), is(value));
	}

	@Test
	public void testIntegralEnabled() {
		GeoElement element = add("Integral(x)");
		outputShouldBe(element, "f(x) = 1 / 2 x²");
	}

	@Test
	public void testNSolveShouldBeDefined() {
		add("V(h) = 4h^3 - 102h^2 +630h");
		add("l1 = NSolve(V(h))");
		app.storeUndoInfo();
		undoRedo.undo();
		GeoElement element = undoRedo.getAfterRedo("l1");
		outputShouldBe(element, "l1 = {h = 0, h = 10.5, h = 15}");
	}

	@Test
	public void testNSolveWithFunctionShouldBeDefined() {
		add("g(h) = h");
		GeoElement l1 = add("l1 = NSolve(g(h) + h = 2)");
		outputShouldBe(l1, "l1 = {h = 1}");
		app.storeUndoInfo();
		undoRedo.undo();
		GeoElement element = undoRedo.getAfterRedo("l1");
		outputShouldBe(element, "l1 = {h = 1}");
	}

}
