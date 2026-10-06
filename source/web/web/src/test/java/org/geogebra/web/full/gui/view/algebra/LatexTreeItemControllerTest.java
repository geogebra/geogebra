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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.event.KeyEvent.KeyboardType;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.TestFormatFactory;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests submission of new algebra input after a previous expression was submitted.
 * Regression coverage for APPS-7932.
 */
@RunWith(GgbMockitoTestRunner.class)
public class LatexTreeItemControllerTest {

	private AppWFull app;
	private RadioTreeItem input;
	private LatexTreeItemController controller;

	@Before
	public void setup() {
		FormatFactory.setPrototypeIfNull(new TestFormatFactory());
		app = AppMocker.mockGraphing();
		input = app.getAlgebraView().getInputTreeItem();
		assertNotNull(input);
		controller = input.getLatexController();
	}

	@Test
	public void secondExpressionShouldBeSubmittedWhenEditorLosesFocus() {
		enterFirstExpressionAndTypeSecond();

		// GgbMockitoTestRunner stubs deferred scheduling. Run the submission that onBlur schedules.
		controller.onEnter(false);

		assertNotNull("Blur must create a labeled second function", app.getKernel().lookupLabel("g"));
		assertEquals("", input.getText());
	}

	@Test
	public void secondExpressionShouldBeSubmittedByEnter() {
		enterFirstExpressionAndTypeSecond();

		pressEnter();

		assertNotNull(app.getKernel().lookupLabel("g"));
		assertEquals("", input.getText());
	}

	@Test
	public void repeatedBlurAfterEnterShouldNotCreateDuplicateFunctions() {
		enterFirstExpressionAndTypeSecond();
		pressEnter();
		GeoElement secondFunction = app.getKernel().lookupLabel("g");
		assertNotNull(secondFunction);

		controller.onEnter(false);
		controller.onEnter(false);

		assertSame(secondFunction, app.getKernel().lookupLabel("g"));
		assertEquals(
				2, app.getKernel().getConstruction().getGeoSetConstructionOrder().size());
		assertEquals("", input.getText());
	}

	@Test
	public void emptyBlurShouldNotCreateFunction() {
		assertTrue(input.enterEditMode(false));
		controller.onEnter(false);
		controller.onEnter(false);

		assertEquals(
				0, app.getKernel().getConstruction().getGeoSetConstructionOrder().size());
		assertEquals("", input.getText());
	}

	@Test
	public void existingFunctionShouldBeRedefinedOnBlur() {
		input.setText("x+1");
		pressEnter();
		GeoElement function = app.getKernel().lookupLabel("f");
		assertNotNull(function);
		RadioTreeItem row = LaTeXTreeItem.of(function);
		assertTrue(row.enterEditMode(false));
		row.setText("x+2");

		row.getLatexController().onEnter(false);
		row.getLatexController().onEnter(false);

		GeoFunction redefined = (GeoFunction) app.getKernel().lookupLabel("f");
		assertNotNull(redefined);
		assertEquals(5, redefined.value(3), 0);
		assertEquals(
				1, app.getKernel().getConstruction().getGeoSetConstructionOrder().size());
	}

	private void enterFirstExpressionAndTypeSecond() {
		input.setText("x^2/23+2x+1");
		pressEnter();
		assertNotNull(app.getKernel().lookupLabel("f"));
		assertEquals("", input.getText());

		// Type into the existing editor without clicking the input or forcing editing=true.
		type("x^2");
		input.getMathField().getInternal().onKeyPressed(new KeyEvent(39, KeyboardType.EXTERNAL));
		type("+2x+1");
		assertEquals("x^(2)+2x+1", input.getText());
		assertNull("Typing should only create a preview", app.getKernel().lookupLabel("g"));
	}

	private void pressEnter() {
		input.getMathField().getInternal().onKeyPressed(new KeyEvent(13, KeyboardType.EXTERNAL));
	}

	private void type(String text) {
		for (char character : text.toCharArray()) {
			input
					.getMathField()
					.getInternal()
					.onKeyTyped(new KeyEvent(0, 0, character, KeyboardType.EXTERNAL));
		}
	}
}
