package org.geogebra.common.io;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class DeleteInMatrixTest {
	private static final String MATRIX_1_TO_9 = "{{1,2,3},{4,5,6},{7,8,9}}";
	private static final String ROW_MATRIX = "{{1,2,3}}";
	public static final String MATRIX_MULIFIGURE = "{{123,456},{321,654}}";
	private static final AppCommon app = AppCommonFactory.create();

	/**
	 * Setup LaTeX
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Test
	public void testSelectionShouldDelete1OnlyFromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.shiftRightTwice()
				.shouldDeleteOnly(1);
	}

	@Test
	public void testSelectionShouldDelete1OnlyFromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.right()
				.shiftLeftTwice()
				.shouldDeleteOnly(1);
	}

	@Test
	public void testSelectionShouldDelete2OnlyFromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.rightTimes(2)
				.shiftRightTwice()
				.shouldDeleteOnly(2);
	}

	@Test
	public void testSelectionShouldDelete2OnlyFromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.rightTimes(3)
				.shiftLeftTwice()
				.shouldDeleteOnly(2);
	}

	@Test
	public void testSelectionShouldDelete3OnlyFromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.rightTimes(4)
				.shiftRightTwice()
				.shouldDeleteOnly(3);
	}

	@Test
	public void testSelectionShouldDelete3OnlyFromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.rightTimes(5)
				.shiftLeftTwice()
				.shouldDeleteOnly(3);
	}

	@Test
	public void testSelectionShouldDelete4OnlyFromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.down()
				.shiftRightTwice()
				.shouldDeleteOnly(4);
	}

	@Test
	public void testSelectionShouldDelete4OnlyFromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.down()
				.right()
				.shiftLeftTwice()
				.shouldDeleteOnly(4);
	}

	@Test
	public void testSelectionShouldDelete5OnlyFromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.down()
				.rightTimes(3)
				.shiftLeftTwice()
				.shouldDeleteOnly(5);
	}

	@Test
	public void testSelectionShouldDelete5OnlyFromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.down()
				.rightTimes(4)
				.shiftLeftTwice()
				.shouldDeleteOnly(5);
	}

	@Test
	public void testSelectionShouldDelete6OnlyFromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.down()
				.rightTimes(4)
				.shiftRightTwice()
				.shouldDeleteOnly(6);
	}

	@Test
	public void testSelectionShouldDelete6OnlyFromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.down()
				.rightTimes(5)
				.shiftLeftTwice()
				.shouldDeleteOnly(6);
	}

	@Test
	public void testSelectionShouldDelete7OnlyFromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.downTimes(2)
				.shiftRightTwice()
				.shouldDeleteOnly(7);
	}

	@Test
	public void testSelectionShouldDelete7OnlyFromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.downTimes(2)
				.right()
				.shiftLeftTwice()
				.shouldDeleteOnly(7);
	}

	@Test
	public void testSelectionShouldDelete8OnlyFromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.downTimes(2)
				.rightTimes(2)
				.shiftRightTwice()
				.shouldDeleteOnly(8);
	}

	@Test
	public void testSelectionShouldDelete8OnlyFromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.downTimes(2)
				.rightTimes(3)
				.shiftLeftTwice()
				.shouldDeleteOnly(8);
	}

	@Test
	public void testSelectionShouldDelete9OnlyFromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.downTimes(2)
				.rightTimes(4)
				.shiftRightTwice()
				.shouldDeleteOnly(9);
	}

	@Test
	public void testSelectionShouldDelete9OnlyFromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_1_TO_9);
		checker.downTimes(2)
				.rightTimes(5)
				.shiftLeftTwice()
				.shouldDeleteOnly(9);
	}

	@Test
	public void testShouldDelete123FromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_MULIFIGURE);
		checker.shiftOn().rightTimes(4)
				.shouldDeleteOnly(123);
	}

	@Test
	public void testShouldDelete123FromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_MULIFIGURE);
		checker.rightTimes(3)
				.shiftOn().leftTimes(4)
				.shouldDeleteOnly(123);
	}

	@Test
	public void testShouldDelete456FromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_MULIFIGURE);
		checker.rightTimes(3)
				.shiftOn().rightTimes(4)
				.shouldDeleteOnly(456);
	}

	@Test
	public void testShouldDelete456FromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_MULIFIGURE);
		checker.rightTimes(6)
				.shiftOn().leftTimes(4)
				.shouldDeleteOnly(456);
	}

	@Test
	public void testShouldDelete321FromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_MULIFIGURE);
		checker.down()
				.shiftOn()
				.rightTimes(4)
				.shouldDeleteOnly(321);
	}

	@Test
	public void testShouldDelete321FromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_MULIFIGURE);
		checker.down()
				.rightTimes(3)
				.shiftOn().leftTimes(4)
				.shouldDeleteOnly(321);
	}

	@Test
	public void testShouldDelete654FromLeft() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_MULIFIGURE);
		checker.down()
				.rightTimes(4)
				.shiftOn()
				.rightTimes(4)
				.shouldDeleteOnly(654);
	}

	@Test
	public void testShouldDelete654FromRight() {
		MatrixChecker checker = new MatrixChecker(app, MATRIX_MULIFIGURE);
		checker.down()
				.rightTimes(6)
				.shiftOn().leftTimes(4)
				.shouldDeleteOnly(654);
	}

	@Test
	public void testRowSelectionShouldDelete1Only() {
		MatrixChecker checker = new MatrixChecker(app, ROW_MATRIX);
		checker.shiftOn()
				.right()
				.backspace(2)
				.shouldDeleteOnly(1);
	}

	@Test
	public void testRowSelectionShouldDelete2Only() {
		MatrixChecker checker = new MatrixChecker(app, ROW_MATRIX);
		checker.rightTimes(2)
				.shiftOn()
				.right()
				.backspace(2)
				.shouldDeleteOnly(2);
	}

	@Test
	public void testRowSelectionShouldDelete2OnlyWith3Backspace() {
		MatrixChecker checker = new MatrixChecker(app, ROW_MATRIX);
		checker.rightTimes(2)
				.shiftOn()
				.right()
				.shouldDeleteOnly(2);
	}

	@Test
	public void testRowSelectionShouldDelete3Only() {
		MatrixChecker checker = new MatrixChecker(app, ROW_MATRIX);
		checker.rightTimes(4)
				.shiftOn()
				.right()
				.backspace(1)
				.backspace(1)
				.shouldDeleteOnly(3);
	}

	@Test
	public void testRowShouldDelete3Only() {
		MatrixChecker checker = new MatrixChecker(app, ROW_MATRIX);
		checker.rightTimes(3)
				.backspace(2)
				.shouldDeleteOnly(2);

	}

	@Test
	public void testCanTypeAndDeleteBrackets() {
		MatrixChecker checker = new MatrixChecker(app, ROW_MATRIX);
		checker.rightTimes(3)
				.type("(")
				.backspace(1)
				.shouldBeUnchanged();

	}
}