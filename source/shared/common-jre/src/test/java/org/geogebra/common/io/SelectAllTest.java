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

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

class SelectAllTest {
	private static final AppCommon app = AppCommonFactory.create();

	/**
	 * Setup LaTeX
	 */
	@BeforeAll
	static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Test
	void testPointSimpleCoordinateX() {
		EditorChecker checker = new EditorChecker(app);
		checker.parse("(321.45,4)")
				.setModifiers(KeyEvent.CTRL_MASK)
				.protect()
				.left(8)
				.typeKey(JavaKeyCodes.VK_A)
				.type("0")
				.checkAsciiMath("(0,4)");
	}

	@Test
	void testPointSimpleCoordinateXFromMiddle() {
		EditorChecker checker = new EditorChecker(app);
		checker.parse("(321.45,4)")
				.protect()
				.left(4)
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_A)
				.type("0")
				.checkAsciiMath("(0,4)");
	}

	@Test
	void testPoint3DCoordinateDeleteMiddle() {
		EditorChecker checker = new EditorChecker(app);
		checker.parse("(1,2,3)")
				.protect()
				.left(4)
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_A)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_A)
				.type("0")
				.checkAsciiMath("(1,0,3)");
	}

	@Test
	void testPointDeleteLast() {
		EditorChecker checker = new EditorChecker(app);
		checker.parse("(,)")
				.protect()
				.left(1)
				.insert("3")
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_A)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.checkAsciiMath("(,)");
	}

	@Test
	void testPointSelectFirst() {
		EditorChecker checker = new EditorChecker(app);
		checker.parse("(,,)")
				.protect()
				.withPlaceholders()
				.left(20)
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_A)
				.right(1)
				.type("4")
				.checkAsciiMath("(,4,)");
	}

	@Test
	void listShouldSelectAllElements() {
		EditorChecker checker = new EditorChecker(app);
		checker.parse("{1,2,3}")
				.left(1)
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_A)
				.type(" ")
				.checkAsciiMath(" ");
	}
}
