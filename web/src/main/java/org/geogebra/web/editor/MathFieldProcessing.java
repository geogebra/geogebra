package org.geogebra.web.editor;

import org.geogebra.common.main.KeyboardLocale;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.keyboard.KeyboardConstants;
import org.geogebra.web.keyboard.KeyboardListener;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.web.MathFieldW;

public class MathFieldProcessing implements KeyboardListener {

	private MathFieldW mf;

	public MathFieldProcessing(MathFieldW mf) {
		this.mf = mf;
	}

	@Override
	public void setFocus(boolean focus) {
		mf.setFocus(focus);

	}

	@Override
	public void onEnter() {
		mf.getKeyListener().onKeyPressed(new KeyEvent(KeyEvent.VK_ENTER));

	}

	@Override
	public void onBackSpace() {
		mf.getKeyListener().onKeyPressed(new KeyEvent(KeyEvent.VK_BACK_SPACE));

	}

	@Override
	public void onArrow(ArrowType type) {
		int arrowType = type == ArrowType.left ? KeyEvent.VK_LEFT
				: KeyEvent.VK_RIGHT;

		mf.getKeyListener().onKeyPressed(new KeyEvent(arrowType));

	}

	@Override
	public void insertString(String text) {
		if (text.equals(KeyboardConstants.A_POWER_X)) {
			mf.getKeyListener().onKeyTyped(new KeyEvent(0, 0, '^'));
		}
		else if (text.equals(Unicode.DIVIDE)) {
			mf.getKeyListener().onKeyTyped(new KeyEvent(0, 0, '/'));
		} else {
			for (int i = 0; i < text.length(); i++) {
				mf.getKeyListener()
						.onKeyTyped(new KeyEvent(0, 0, text.charAt(i)));
			}
		}

	}

	@Override
	public void scrollCursorIntoView() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean resetAfterEnter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateForNewLanguage(KeyboardLocale localization) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setKeyBoardModeText(boolean text) {
		// TODO Auto-generated method stub

	}

	public void autocomplete(String text) {
		this.mf.deleteCurrentWord();
		insertString(text);
		if (text.contains("[") || text.contains("(")) {
			mf.selectNextArgument();
			mf.setFocus(true);
		}

	}

	@Override
	public boolean isSVCell() {
		return false;
	}

	@Override
	public void endEditing() {
		// TODO Auto-generated method stub

	}

	@Override
	public MathFieldW getField() {
		return mf;
	}

}
