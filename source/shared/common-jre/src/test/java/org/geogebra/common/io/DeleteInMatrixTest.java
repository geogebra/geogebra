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
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

class DeleteInMatrixTest {
	private static final String MATRIX_1_TO_9 = "{{1,2,3},{4,5,6},{7,8,9}}";
	private static final String ROW_MATRIX = "{{1,2,3}}";
	private static final String MATRIX_MULIFIGURE = "{{123,456},{321,654}}";
	private static final String MATRIX_FRAC = "{{1/(2*sqrt(2)),456},{321,654}}";
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
	void testSelectionShouldDelete1OnlyFromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.shiftOn().right(2)
				.shouldDeleteOnly(1);
	}

	private EditorChecker newMatrixChecker(String input) {
		EditorChecker checker = new EditorChecker(DeleteInMatrixTest.app);
		return checker.matrixFromParser(input);
	}

	@Test
	void testSelectionShouldDelete1OnlyFromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.right(1)
				.shiftOn().left(2)
				.shouldDeleteOnly(1);
	}

	@Test
	void testSelectionShouldDelete2OnlyFromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.right(2)
				.shiftOn().right(2)
				.shouldDeleteOnly(2);
	}

	@Test
	void testSelectionShouldDelete2OnlyFromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.right(3)
				.shiftOn().left(2)
				.shouldDeleteOnly(2);
	}

	@Test
	void testSelectionShouldDelete3OnlyFromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.right(4)
				.shiftOn().right(2)
				.shouldDeleteOnly(3);
	}

	@Test
	void testSelectionShouldDelete3OnlyFromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.right(5)
				.shiftOn().left(2)
				.shouldDeleteOnly(3);
	}

	@Test
	void testSelectionShouldDelete4OnlyFromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(1)
				.shiftOn().right(2)
				.shouldDeleteOnly(4);
	}

	@Test
	void testSelectionShouldDelete4OnlyFromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(1)
				.right(1)
				.shiftOn().left(2)
				.shouldDeleteOnly(4);
	}

	@Test
	void testSelectionShouldDelete5OnlyFromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(1)
				.right(3)
				.shiftOn().left(2)
				.shouldDeleteOnly(5);
	}

	@Test
	void testSelectionShouldDelete5OnlyFromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(1)
				.right(4)
				.shiftOn().left(2)
				.shouldDeleteOnly(6);
	}

	@Test
	void testSelectionShouldDelete6OnlyFromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(1)
				.right(4)
				.shiftOn().right(2)
				.shouldDeleteOnly(6);
	}

	@Test
	void testSelectionShouldDelete6OnlyFromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(1)
				.right(5)
				.shiftOn().left(2)
				.shouldDeleteOnly(6);
	}

	@Test
	void testSelectionShouldDelete7OnlyFromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(2)
				.shiftOn().right(2)
				.shouldDeleteOnly(7);
	}

	@Test
	void testSelectionShouldDelete7OnlyFromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(2)
				.right(1)
				.shiftOn().left(2)
				.shouldDeleteOnly(7);
	}

	@Test
	void testSelectionShouldDelete8OnlyFromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(2)
				.right(2)
				.shiftOn().right(2)
				.shouldDeleteOnly(8);
	}

	@Test
	void testSelectionShouldDelete8OnlyFromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(2)
				.right(3)
				.shiftOn().left(2)
				.shouldDeleteOnly(8);
	}

	@Test
	void testSelectionShouldDelete9OnlyFromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(2)
				.right(4)
				.shiftOn().right(2)
				.shouldDeleteOnly(9);
	}

	@Test
	void testSelectionShouldDelete9OnlyFromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_1_TO_9);
		checker.down(2)
				.right(5)
				.shiftOn().left(2)
				.shouldDeleteOnly(9);
	}

	@Test
	void testShouldDelete123FromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_MULIFIGURE);
		checker.shiftOn().right(4)
				.shouldDeleteOnly(123);
	}

	@Test
	void testShouldDelete123FromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_MULIFIGURE);
		checker.right(3)
				.shiftOn().left(4)
				.shouldDeleteOnly(123);
	}

	@Test
	void testShouldDelete456FromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_MULIFIGURE);
		checker.right(4)
				.shiftOn().right(3)
				.shouldDeleteOnly(456);
	}

	@Test
	void testShouldDelete123FromLeftWithSkip() {
		EditorChecker checker = newMatrixChecker(MATRIX_MULIFIGURE);
		checker.right(3)
				.shiftOn().right(4)
				.shouldDeleteOnly(123);
	}

	@Test
	void testShouldDelete456FromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_MULIFIGURE);
		checker.right(6)
				.shiftOn().left(4)
				.shouldDeleteOnly(456);
	}

	@Test
	void testShouldDelete321FromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_MULIFIGURE);
		checker.down(1)
				.shiftOn()
				.right(4)
				.shouldDeleteOnly(321);
	}

	@Test
	void testShouldDelete321FromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_MULIFIGURE);
		checker.down(1)
				.right(3)
				.shiftOn().left(4)
				.shouldDeleteOnly(321);
	}

	@Test
	void testShouldDelete654FromLeft() {
		EditorChecker checker = newMatrixChecker(MATRIX_MULIFIGURE);
		checker.down(1)
				.right(4)
				.shiftOn()
				.right(4)
				.shouldDeleteOnly(654);
	}

	@Test
	void testShouldDelete654FromRight() {
		EditorChecker checker = newMatrixChecker(MATRIX_MULIFIGURE);
		checker.down(1)
				.right(6)
				.shiftOn().left(4)
				.shouldDeleteOnly(654);
	}

	@Test
	void testRowSelectionShouldDelete1Only() {
		EditorChecker checker = newMatrixChecker(ROW_MATRIX);
		checker.shiftOn()
				.right(1)
				.backspace(2)
				.shouldDeleteOnly(1);
	}

	@Test
	void testRowSelectionShouldDelete2Only() {
		EditorChecker checker = newMatrixChecker(ROW_MATRIX);
		checker.right(2)
				.shiftOn()
				.right(1)
				.backspace(2)
				.shouldDeleteOnly(2);
	}

	@Test
	void testRowSelectionShouldDelete2OnlyWith3Backspace() {
		EditorChecker checker = newMatrixChecker(ROW_MATRIX);
		checker.right(2)
				.shiftOn()
				.right(1)
				.shouldDeleteOnly(2);
	}

	@Test
	void testRowSelectionShouldDelete3Only() {
		EditorChecker checker = newMatrixChecker(ROW_MATRIX);
		checker.right(4)
				.shiftOn()
				.right(1)
				.backspace(1)
				.backspace(1)
				.shouldDeleteOnly(3);
	}

	@Test
	void testRowShouldDelete3Only() {
		EditorChecker checker = newMatrixChecker(ROW_MATRIX);
		checker.right(3)
				.backspace(2)
				.shouldDeleteOnly(2);

	}

	@Test
	void testCanTypeAndDeleteBrackets() {
		EditorChecker checker = newMatrixChecker(ROW_MATRIX);
		checker.right(3)
				.type("(")
				.backspace(1)
				.checkAsciiMath(ROW_MATRIX);

	}

	@Test
	void testSelectAndReplaceElement11() {
		selectAllAndDelete(MATRIX_MULIFIGURE, 1, "123");
	}

	private void selectAllAndDelete(String matrix, int right, String target) {
		EditorChecker checker = newMatrixChecker(matrix);
		checker.right(right)
				.ctrlA()
				.typeKey(JavaKeyCodes.VK_DELETE);
		checker.checkAsciiMath(matrix.replace(target, ""));
	}

	@Test
	void testSelectAndReplaceElement11FromMiddle() {
		selectAllAndDelete(MATRIX_MULIFIGURE, 3, "123");
	}

	@Test
	void testSelectAndReplaceLatexElement11FromMiddle() {
		selectAllAndDelete(MATRIX_FRAC, 2, "1/(2*sqrt(2))");
	}
}
