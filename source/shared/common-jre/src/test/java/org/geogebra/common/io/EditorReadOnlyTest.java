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

package org.geogebra.common.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.input.KeyboardInputAdapter;
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

@Issue("APPS-8143")
class EditorReadOnlyTest {

	private TestMathField mathField;
	private MathFieldInternal internal;
	private final List<Integer> unhandledArrows = new ArrayList<>();

	@BeforeAll
	static void setupFactoryProvider() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		AwtFactory.setPrototypeIfNull(new AwtFactoryCommon());
	}

	@BeforeEach
	void setUp() {
		mathField = new TestMathField();
		mathField.insertString("12");
		internal = mathField.getInternal();
		internal.setUnhandledKeyListener((keyCode, keyboardType) -> unhandledArrows.add(keyCode));
		internal.setReadOnly(true);
	}

	@Test
	void keyboardInputIsIgnored() {
		KeyboardInputAdapter.onKeyboardInput(internal, "3");
		KeyboardInputAdapter.onCommandInput(internal, "sin");
		internal.insertString("4");
		assertEquals("12", internal.getText());
	}

	@Test
	void typingIsIgnored() {
		internal.onKeyTyped(new KeyEvent(0, 0, '3', KeyEvent.KeyboardType.EXTERNAL));
		internal.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_BACK_SPACE, KeyEvent.KeyboardType.EXTERNAL));
		assertEquals("12", internal.getText());
	}

	@Test
	void arrowKeysArePassedToUnhandledKeyListener() {
		internal.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_LEFT, KeyEvent.KeyboardType.EXTERNAL));
		internal.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_RIGHT, KeyEvent.KeyboardType.EXTERNAL));
		assertEquals(List.of(JavaKeyCodes.VK_LEFT, JavaKeyCodes.VK_RIGHT), unhandledArrows);
	}

	@Test
	void cursorIsHidden() {
		internal.update();
		assertNull(mathField.icon.cursorPosition);
		internal.setReadOnly(false);
		internal.update();
		assertNotNull(mathField.icon.cursorPosition);
	}

	private static class TestMathField extends MathFieldCommon {
		private TeXIcon icon;

		TestMathField() {
			super(new TemplateCatalog(), null);
		}

		@Override
		public void setTeXIcon(TeXIcon icon) {
			this.icon = icon;
		}
	}
}
