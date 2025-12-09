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

package org.geogebra.common.gui.popup.autocompletion;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.EditorTyper;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.controller.EditorState;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class InputSuggestionsTest extends BaseUnitTest {
	private MathFieldCommon mathField;
	private EditorState editorState;

	/**
	 * Reset LaTeX factory
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Before
	public void setupMathField() {
		mathField = new MathFieldCommon(new TemplateCatalog(), null);
		editorState = mathField.getInternal().getEditorState();
	}

	@Test
	public void testHasSuggestions() {
		shouldHaveSuggestions("Int");
		shouldHaveSuggestions("Poi");
	}
	
	private void shouldHaveSuggestions(String text) {
		shouldHaveSuggestions(text, null);
	}

	private void shouldHaveSuggestions(String text, GeoElement geo) {
		assertFalse(isSuggestionsPrevented(text, geo)) ;
	}

	private void shouldPreventSuggestions(String text) {
		shouldPreventSuggestions(text, null);
	}

	private void shouldPreventSuggestions(String text, GeoElement geo) {
		assertTrue(isSuggestionsPrevented(text, geo));
	}

	private boolean isSuggestionsPrevented(String text, GeoElement geo) {
		mathField.insertString(text);
		InputSuggestions suggestions = new InputSuggestions(geo);
		return suggestions.isPreventedFor(editorState);
	}

	@Test
	public void testSuggestionsShouldBePrevented() {
		shouldPreventSuggestions("\"Int");
		shouldPreventSuggestions("Text(\"Int");
		shouldPreventSuggestions("v_Point");
		shouldPreventSuggestions("v^Point");
	}

	@Test
	public void testGetCommand() {
		assertThat(getCommandFor("a+Seg"), equalTo("Seg"));
		assertThat(getCommandFor("a_Seg"), equalTo(""));
	}

	private String getCommandFor(String text) {
		new EditorTyper(mathField).type(text);
		InputSuggestions suggestions = new InputSuggestions(null);
		return suggestions.getCommand(mathField);
	}

	@Test
	public void testSuggestionsShouldBePreventedForTextObjects() {
		GeoText geo = add("Text(\"Change me\")");
		shouldPreventSuggestions("Int", geo);
		shouldPreventSuggestions("Lin", geo);
	}

	@Test
	public void testHaveSuggestionsForNonTextObjects() {
		GeoFunction geo = add("x");
		shouldHaveSuggestions("Int", geo);
		shouldHaveSuggestions("Lin", geo);
	}
}