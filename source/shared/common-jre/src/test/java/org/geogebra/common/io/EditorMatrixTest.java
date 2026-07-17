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
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EditorMatrixTest {
	private static final String matrix3x3 = "{{1,2,3}, {4,5,6}, {7,8,9}}";
	private static EditorChecker checker;
	private static AppCommon app = AppCommonFactory.create();

	@BeforeEach
	void setUp() {
		checker = new EditorChecker(app);
	}

	@Test
	void testCaretInitialPosition() {
		checker.matrixFromParser(matrix3x3).checkCaret(0, 0, 0);
	}

	@Test
	void testCaretShouldStayInsideOnHomeInitially() {
		checker.matrixFromParser(matrix3x3).typeKey(JavaKeyCodes.VK_HOME).checkCaret(0, 0, 0);
	}

	@Test
	void testCaretShouldStayInsideOnHome() {
		checker.matrixFromParser(matrix3x3)
				.right(6)
				.typeKey(JavaKeyCodes.VK_HOME).checkCaret(0, 0, 0);
	}

	@Test
	void testCaretShouldStayInsideOnEnd() {
		checker.matrixFromParser(matrix3x3).typeKey(JavaKeyCodes.VK_END).checkCaret(1, 8, 0);
	}

	@Test
	void testCaretShouldStayInsideOnLeftArrow() {
		checker.matrixFromParser(matrix3x3)
			.right(6)
			.left(20).checkCaret(0, 0, 0);
	}

	@Test
	void testCaretShouldStayInsideOnRightArrow() {
		checker.matrixFromParser(matrix3x3).right(20).checkCaret(1, 8, 0);
	}

	@Test
	void testCaretShouldStayInsideOnUpArrow() {
		checker.matrixFromParser(matrix3x3)
				.repeatKey(JavaKeyCodes.VK_DOWN, 2)
				.repeatKey(JavaKeyCodes.VK_UP, 20).checkCaret(0, 0, 0);
	}

	@Test
	void testCaretShouldStayInsideOnDownArrow() {
		checker.matrixFromParser(matrix3x3).repeatKey(JavaKeyCodes.VK_DOWN, 20).checkCaret(0, 6, 0);
	}
}
