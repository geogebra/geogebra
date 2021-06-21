package org.geogebra.common.io;

import org.geogebra.common.main.App;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

class MatrixChecker extends EditorChecker {
	private final String matrix;

	protected MatrixChecker(App app, String matrix) {
		super(app);
		this.matrix = matrix;
		matrixFromParser(matrix);
	}

	public MatrixChecker shiftOn() {
		return (MatrixChecker) this.setModifiers(KeyEvent.SHIFT_MASK);
	}

	public MatrixChecker right() {
		return (MatrixChecker) typeKey(JavaKeyCodes.VK_RIGHT);
	}

	public MatrixChecker down() {
		return (MatrixChecker) typeKey(JavaKeyCodes.VK_DOWN);
	}

	public MatrixChecker delete() {
		return (MatrixChecker) typeKey(JavaKeyCodes.VK_DELETE);
	}

	public MatrixChecker rightTimes(int times) {
		return (MatrixChecker) repeatKey(JavaKeyCodes.VK_RIGHT, times);
	}

	public MatrixChecker downTimes(int times) {
		return (MatrixChecker) repeatKey(JavaKeyCodes.VK_DOWN, times);
	}

	public MatrixChecker leftTimes(int times) {
		return (MatrixChecker) repeatKey(JavaKeyCodes.VK_LEFT, times);
	}

	public MatrixChecker left() {
		return (MatrixChecker) repeatKey(JavaKeyCodes.VK_LEFT, 1);
	}

	public MatrixChecker shiftRightTwice() {
		return shiftOn().rightTimes(2);
	}

	public MatrixChecker shiftLeftTwice() {
		return shiftOn().leftTimes(2);
	}

	public void shouldDeleteOnly(Integer number) {
		setModifiers(0);
		delete()
		.checkAsciiMath(matrix.replace(number.toString(), ""));

	}

	public MatrixChecker backspace(int times) {
		return (MatrixChecker) repeatKey(JavaKeyCodes.VK_BACK_SPACE, times);
	}

	@Override
	public MatrixChecker type(String input) {
		return (MatrixChecker) super.type(input);
	}

	public void shouldBeUnchanged() {
		checkAsciiMath(matrix);
	}
}
