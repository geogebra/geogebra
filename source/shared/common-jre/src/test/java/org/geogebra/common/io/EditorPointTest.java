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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

class EditorPointTest {
	private static final String point3D = "(1,2,3)";
	private static final String emptyPoint3D = "(?,?,?)";
	private static EditorChecker checker;
	private static final AppCommon app = AppCommonFactory.create();

	/**
	 * Reset LaTeX factory
	 */
	@BeforeAll
	static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@BeforeEach
	void setUp() {
		checker = new EditorChecker(app);
	}

	@Test
	void testInitialEmptyPoint() {
		checker.convertFormula(emptyPoint3D)
				.checkPlaceholders("|,_,_");
	}

	@Test
	void testEmptyPointWithCursorInTheMiddle() {
		checker.convertFormula(emptyPoint3D)
				.right(1)
				.checkPlaceholders("_,|,_");
	}

	@Test
	void testEmptyPointWithCursorLast() {
		checker.convertFormula(emptyPoint3D)
				.right(2)
				.checkPlaceholders("_,_,|");
	}

	@Test
	void testEmptyPointWithCursorLastMoreRightPress() {
		checker.convertFormula(emptyPoint3D)
				.right(2)
				.right(1)
				.checkPlaceholders("_,_,|");
	}

	@Test
	void testEmptyPointWithCursorBeginMoreLeftPress() {
		checker.convertFormula(emptyPoint3D)
				.right(2)
				.left(4)
				.checkPlaceholders("|,_,_");
	}

	@Test
	void testPointOnDelete() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.checkPlaceholders("|,2,3");
	}

	@Test
	void testPointOnDeleteAnRight() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.checkPlaceholders("_,2,3");
	}

	@Test
	void testPointOnDeleteAnRightDeleteAgain() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.checkPlaceholders("_,|,3");
	}

	@Test
	void testPointClick() {
		checker.convertFormula(emptyPoint3D)
				.click(3, 0)
				.checkPlaceholders("|,_,_");
		checker.convertFormula(emptyPoint3D)
				.click(20, 0)
				.checkPlaceholders("_,|,_");
		checker.convertFormula(emptyPoint3D)
				.click(40, 0)
				.checkPlaceholders("_,_,|");
	}

	@Test
	void testPointOnDeleteAnRightDeleteAgainAndBack() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.left(1)
				.checkPlaceholders("|,_,3");
	}

	@Test
	void testDeleteFromMultiChars2D() {
		checker.convertFormula("(123,789)")
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.checkPlaceholders("_,789");

	}

	@Test
	void testDeleteFromMultiCharsFromEnd2D() {
		checker.convertFormula("(123,789)")
				.right(4)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.left(1)
				.checkPlaceholders("123,_");
	}

	@Test
	void testDeleteFromMultiCharsFromMiddle3D() {
		checker.convertFormula("(123,456,789)")
				.right(4)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.left(1)
				.checkPlaceholders("123,_,789");
	}

	@Test
	void testDeleteFromMultiChars() {
		checker.convertFormula("(123,456,789)")
				.right(2)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.checkPlaceholders("12,456,789");
	}

	@Test
	void testDeleteFromMultiCharsFromBeginWithBackspace() {
		checker.convertFormula("(123,456,789)")
				.right(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.right(1)
				.checkPlaceholders("_,456,789");
	}

	@Test
	void testDeleteFromMultiCharsWithBackspace() {
		checker.convertFormula("(123,456,789)")
				.right(11)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.left(1)
				.checkPlaceholders("123,456,_");
	}

	@Test
	void testDeleteFromMultiCharsFromMiddleWithBackspace() {
		checker.convertFormula("(123,456,789)")
				.right(7)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.left(1)
				.checkPlaceholders("123,_,789");
	}

	@Test
	void testTypeToMiddleOfEmptyPoint() {
		checker.convertFormula(emptyPoint3D)
				.right(1)
				.type("4")
				.typeKey(JavaKeyCodes.VK_ENTER)
				.checkPlaceholders("_,4,_");
	}

	@Test
	void testDeleteOneFromMultiCharsFromMiddleWithBackspace() {
		checker.convertFormula("(123,456,789)")
				.right(6)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkPlaceholders("123,46,789");
	}

	@Test
	void testDeleteFromMultiCharsFromBeginning() {
		checker.convertFormula("(123,456,789)")
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_RIGHT)
				.checkPlaceholders("23,456,789");

	}

	@Test
	void testDeleteFromFractionAndUp() {
		checker.type("1/2")
			.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_UP)
			.checkRaw("SequenceNode[FnFRAC[SequenceNode[1], SequenceNode[]]]");
	}

	@Test
	void testDeleteEntireFraction() {
		checker.type("1/2")
			.repeatKey(JavaKeyCodes.VK_BACK_SPACE, 4)
			.checkRaw("SequenceNode[]");
	}

	@Test
	void testDeleteFromFractionBrackets() {
		checker.type("1/(2)")
			.repeatKey(JavaKeyCodes.VK_BACK_SPACE, 6)
			.checkRaw("SequenceNode[]");
	}

	@Test
	void testRightArrowCanExitFraction() {
		checker.type("1/(2)")
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.right(2)
				.checkCaret(1);
	}

	@Test
	void checkBackspace() {
		checker.type("a b(")
				.left(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkRaw("SequenceNode[FnAPPLY[SequenceNode[a, b], SequenceNode[]]]");

	}

	@Test
	void testCursorCanExitFromEmptyPointInAV() {
		checker.convertFormulaForAV(emptyPoint3D)
				.right(2)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(3)
				.checkCursorIsAtRoot();
	}

	@Test
	void testCursorCanGoBackToEmptyPointInAV() {
		checker.convertFormulaForAV(emptyPoint3D)
				.right(3)
				.left(1)
				.checkPlaceholders("_,_,|");
	}
}
