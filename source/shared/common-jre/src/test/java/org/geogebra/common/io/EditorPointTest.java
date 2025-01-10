package org.geogebra.common.io;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorPointTest {
	private static final String point3D = "(1,2,3)";
	private static final String emptyPoint3D = "(?,?,?)";
	private static EditorChecker checker;
	private static final AppCommon app = AppCommonFactory.create();

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
	public void setUp() {
		checker = new EditorChecker(app);
	}

	@Test
	public void testInitialEmptyPoint() {
		checker.convertFormula(emptyPoint3D)
				.checkPlaceholders("|,_,_");
	}

	@Test
	public void testEmptyPointWithCursorInTheMiddle() {
		checker.convertFormula(emptyPoint3D)
				.right(1)
				.checkPlaceholders("_,|,_");
	}

	@Test
	public void testEmptyPointWithCursorLast() {
		checker.convertFormula(emptyPoint3D)
				.right(2)
				.checkPlaceholders("_,_,|");
	}

	@Test
	public void testEmptyPointWithCursorLastMoreRightPress() {
		checker.convertFormula(emptyPoint3D)
				.right(2)
				.right(1)
				.checkPlaceholders("_,_,|");
	}

	@Test
	public void testEmptyPointWithCursorBeginMoreLeftPress() {
		checker.convertFormula(emptyPoint3D)
				.right(2)
				.left(4)
				.checkPlaceholders("|,_,_");
	}

	@Test
	public void testPointOnDelete() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.checkPlaceholders("|,2,3");
	}

	@Test
	public void testPointOnDeleteAnRight() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.checkPlaceholders("_,2,3");
	}

	@Test
	public void testPointOnDeleteAnRightDeleteAgain() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.checkPlaceholders("_,|,3");
	}

	@Test
	public void testPointClick() {
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
	public void testPointOnDeleteAnRightDeleteAgainAndBack() {
		checker.convertFormula(point3D)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.left(1)
				.checkPlaceholders("|,_,3");
	}

	@Test
	public void testDeleteFromMultiChars2D() {
		checker.convertFormula("(123,789)")
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.checkPlaceholders("_,789");

	}

	@Test
	public void testDeleteFromMultiCharsFromEnd2D() {
		checker.convertFormula("(123,789)")
				.right(4)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.left(1)
				.checkPlaceholders("123,_");
	}

	@Test
	public void testDeleteFromMultiCharsFromMiddle3D() {
		checker.convertFormula("(123,456,789)")
				.right(4)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.left(1)
				.checkPlaceholders("123,_,789");
	}

	@Test
	public void testDeleteFromMultiChars() {
		checker.convertFormula("(123,456,789)")
				.right(2)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(1)
				.checkPlaceholders("12,456,789");
	}

	@Test
	public void testDeleteFromMultiCharsFromBeginWithBackspace() {
		checker.convertFormula("(123,456,789)")
				.right(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.right(1)
				.checkPlaceholders("_,456,789");
	}

	@Test
	public void testDeleteFromMultiCharsWithBackspace() {
		checker.convertFormula("(123,456,789)")
				.right(11)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.left(1)
				.checkPlaceholders("123,456,_");
	}

	@Test
	public void testDeleteFromMultiCharsFromMiddleWithBackspace() {
		checker.convertFormula("(123,456,789)")
				.right(7)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.left(1)
				.checkPlaceholders("123,_,789");
	}

	@Test
	public void testTypeToMiddleOfEmptyPoint() {
		checker.convertFormula(emptyPoint3D)
				.right(1)
				.type("4")
				.typeKey(JavaKeyCodes.VK_ENTER)
				.checkPlaceholders("_,4,_");
	}

	@Test
	public void testDeleteOneFromMultiCharsFromMiddleWithBackspace() {
		checker.convertFormula("(123,456,789)")
				.right(6)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkPlaceholders("123,46,789");
	}

	@Test
	public void testDeleteFromMultiCharsFromBeginning() {
		checker.convertFormula("(123,456,789)")
				.typeKey(JavaKeyCodes.VK_DELETE)
				.typeKey(JavaKeyCodes.VK_RIGHT)
				.checkPlaceholders("23,456,789");

	}

	@Test
	public void testDeleteFromFractionAndUp() {
		checker.type("1/2")
			.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.typeKey(JavaKeyCodes.VK_UP)
			.checkRaw("MathSequence[FnFRAC[MathSequence[1], MathSequence[]]]");
	}

	@Test
	public void testDeleteEntireFraction() {
		checker.type("1/2")
			.repeatKey(JavaKeyCodes.VK_BACK_SPACE, 4)
			.checkRaw("MathSequence[]");
	}

	@Test
	public void testDeleteFromFractionBrackets() {
		checker.type("1/(2)")
			.repeatKey(JavaKeyCodes.VK_BACK_SPACE, 6)
			.checkRaw("MathSequence[]");
	}

	@Test
	public void testRightArrowCanExitFraction() {
		checker.type("1/(2)")
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.right(2)
				.checkPath(1);
	}

	@Test
	public void checkBackspace() {
		checker.type("a b(")
				.left(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkRaw("MathSequence[FnAPPLY[MathSequence[a, b], MathSequence[]]]");

	}

	@Test
	public void testCursorCanExitFromEmptyPointInAV() {
		checker.convertFormulaForAV(emptyPoint3D)
				.right(2)
				.typeKey(JavaKeyCodes.VK_DELETE)
				.right(3)
				.checkCursorIsAtRoot();
	}

	@Test
	public void testCursorCanGoBackToEmptyPointInAV() {
		checker.convertFormulaForAV(emptyPoint3D)
				.right(3)
				.left(1)
				.checkPlaceholders("_,_,|");
	}
}
