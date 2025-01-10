package org.geogebra.web.editor;

import org.geogebra.keyboard.web.KeyboardListener;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * Virtual keyboard bindings for ReTeX
 */
public class MathFieldProcessing implements KeyboardListener {

	protected MathFieldW mf;

	/**
	 * @param mf
	 *            math input field
	 * 
	 */
	public MathFieldProcessing(MathFieldW mf) {
		this.mf = mf;
	}

	@Override
	public void setFocus(boolean focus) {
		mf.setFocus(focus);
	}

	@Override
	public void onEnter() {
		mf.getKeyListener().onKeyPressed(new KeyEvent(JavaKeyCodes.VK_ENTER));
		mf.getKeyListener().onKeyReleased(new KeyEvent(JavaKeyCodes.VK_ENTER));
	}

	@Override
	public void onBackSpace() {
		mf.getKeyListener()
				.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_BACK_SPACE));
	}

	@Override
	public void onArrow(ArrowType type) {
		int arrowType = JavaKeyCodes.VK_RIGHT;
		switch (type) {
		case right:
			arrowType = JavaKeyCodes.VK_RIGHT;
			break;
		case left:
			arrowType = JavaKeyCodes.VK_LEFT;
			break;
		case up:
			arrowType = JavaKeyCodes.VK_UP;
			break;
		case down:
			arrowType = JavaKeyCodes.VK_DOWN;
			break;
		}

		mf.getKeyListener().onKeyPressed(new KeyEvent(arrowType));
		mf.readPosition();
	}

	@Override
	public void insertString(String text) {
		KeyboardInputAdapter.onKeyboardInput(mf.getInternal(), text);
	}

	/**
	 * @param text
	 *            text to be inserted
	 */
	public void autocomplete(String text) {
		mf.deleteCurrentWord();
		insertString(text);
		mf.setFocus(true);
	}

	@Override
	public boolean isSVCell() {
		return false;
	}

	@Override
	public void endEditing() {
		mf.blur();
	}

	@Override
	public MathFieldW getField() {
		return mf;
	}

	@Override
	public void onKeyboardClosed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void ansPressed() {
		// only for AV (subclass)
	}

	@Override
	public boolean requestsAns() {
		return false;
	}
}
