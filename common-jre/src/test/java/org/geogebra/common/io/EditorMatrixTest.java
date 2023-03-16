package org.geogebra.common.io;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.JavaKeyCodes;

public class EditorMatrixTest {
	private static final String matrix3x3 = "{{1,2,3}, {4,5,6}, {7,8,9}}";
	private static EditorChecker checker;
	private static AppCommon app = AppCommonFactory.create();

	@Before
	public void setUp() {
		checker = new EditorChecker(app);
	}

	@Test
	public void testCaretInitialPosition() {
		checker.matrixFromParser(matrix3x3).checkPath(0, 0, 0);
	}

	@Test
	public void testCaretShouldStayInsideOnHomeInitially() {
		checker.matrixFromParser(matrix3x3).typeKey(JavaKeyCodes.VK_HOME).checkPath(0, 0, 0);
	}

	@Test
	public void testCaretShouldStayInsideOnHome() {
		checker.matrixFromParser(matrix3x3)
				.right(6)
				.typeKey(JavaKeyCodes.VK_HOME).checkPath(0, 0, 0);
	}

	@Test
	public void testCaretShouldStayInsideOnEnd() {
		checker.matrixFromParser(matrix3x3).typeKey(JavaKeyCodes.VK_END).checkPath(1, 8, 0);
	}

	@Test
	public void testCaretShouldStayInsideOnLeftArrow() {
		checker.matrixFromParser(matrix3x3)
			.right(6)
			.left(20).checkPath(0, 0, 0);
	}

	@Test
	public void testCaretShouldStayInsideOnRightArrow() {
		checker.matrixFromParser(matrix3x3).right(20).checkPath(1, 8, 0);
	}

	@Test
	public void testCaretShouldStayInsideOnUpArrow() {
		checker.matrixFromParser(matrix3x3)
				.repeatKey(JavaKeyCodes.VK_DOWN, 2)
				.repeatKey(JavaKeyCodes.VK_UP, 20).checkPath(0, 0, 0);
	}

	@Test
	public void testCaretShouldStayInsideOnDownArrow() {
		checker.matrixFromParser(matrix3x3).repeatKey(JavaKeyCodes.VK_DOWN, 20).checkPath(0, 6, 0);
	}
}
