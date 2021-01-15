package org.geogebra.web.editor;

import javax.annotation.Nullable;

import org.geogebra.common.gui.inputfield.AnsProvider;
import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.keyboard.web.KeyboardConstants;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.meta.FunctionGroup;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * Virtual keyboard bindings for ReTeX
 */
public class MathFieldProcessing implements KeyboardListener {

	private MathFieldW mf;
	private AnsProvider ansProvider;

	@Nullable
	private RadioTreeItem avInput;
	
	/**
	 * @param mf
	 *            math input field
	 * 
	 */
	public MathFieldProcessing(MathFieldW mf) {
		this.mf = mf;
	}

	/**
	 * @param mf
	 *            math field
	 * @param lastItemProvider
	 *            an object with ordered GeoElement collection
	 */
	public MathFieldProcessing(MathFieldW mf, HasLastItem lastItemProvider) {
		this.mf = mf;
		ansProvider = lastItemProvider != null ? new AnsProvider(lastItemProvider) : null;
	}

	/**
	 * @param avInput
	 * 			current AV input (needed for getting the ANS from the previous row)
	 * @param lastItemProvider
	 * 			an object with ordered GeoElement collection
	 */
	public MathFieldProcessing(RadioTreeItem avInput, HasLastItem lastItemProvider) {
		this(avInput.getMathField(), lastItemProvider);
		this.avInput = avInput;
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
		if (text.equals(KeyboardConstants.A_POWER_X)) {
			mf.insertFunction("^");
		} else if (text.equals(Unicode.SUPERSCRIPT_2 + "")) {
			mf.insertFunction("^");
			type("2");
			mf.getKeyListener().onKeyPressed(
					new KeyEvent(JavaKeyCodes.VK_RIGHT, 0, '\0'));
		} else if (text.equals(Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "")) {
			mf.insertFunction("^");
			type("-1");
			mf.getKeyListener()
					.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_RIGHT, 0, '\0'));
		} else if ((Unicode.DIVIDE + "").equals(text)) {
			KeyboardInputAdapter.emulateInput(mf.getInternal(),
					Unicode.DIVIDE + "");
			mf.getInternal().onDivisionInserted();
		} else if ("/".equals(text)) {
			mf.insertFunction("frac");
		} else if (text.charAt(0) == Unicode.SQUARE_ROOT) {
			mf.insertFunction("sqrt");
		} else if ("log10".equals(text)) {
			type("log_10");
			mf.getKeyListener()
					.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_RIGHT, 0, '\0'));
			type("(");
		} else if ("10^".equals(text)) {
			type("10");
			mf.insertFunction("^");
		} else if (text.equals(Unicode.EULER_CHAR + "^")) {
			mf.getKeyListener()
					.onKeyTyped(new KeyEvent(0, 0, Unicode.EULER_CHAR));
			mf.insertFunction("^");
		} else if ("log_".equals(text)) {
			type("log(");
			mf.getKeyListener()
					.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_LEFT, 0, '\0'));
		} else if ("$defint".equals(text) || "$prodeq".equals(text) || "$sumeq".equals(text)
				|| "$limeq".equals(text) || "$vec".equals(text)) {
			mf.insertFunction(text);
		} else {
			if ("d/dx".equals(text)) {
				mf.handleDerivative(text);
				return;
			}
			if (text.contains("/") || text.contains("^")) {
				mf.insertString(text);
				return;
			}
			if (text.length() > 1 && FunctionGroup.isAcceptable(text)) {
				mf.insertFunction(text);
				return;
			}
			type(text);
		}
	}

	private void type(String text) {
		for (int i = 0; i < text.length(); i++) {
			mf.getKeyListener().onKeyTyped(new KeyEvent(0, 0, text.charAt(i)),
					i == text.length() - 1);

		}
		if ("()".equals(text) || "{}".equals(text) || "[]".equals(text)) {
			onArrow(ArrowType.left);
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
	public void setKeyBoardModeText(boolean text) {
		// TODO Auto-generated method stub
	}

	/**
	 * @param text
	 *            text to be inserted
	 */
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
		if (!requestsAns()) {
			return;
		}
		boolean isInputInTextMode = !mf.getInternal().getInputController().getCreateFrac();
		String currentInput = mf.getText();
		String ans =
				isInputInTextMode
						? ansProvider.getAnsForTextInput(avInput.getGeo(), currentInput)
						: ansProvider.getAns(avInput.getGeo(), currentInput);
		mf.insertString(ans);
	}

	@Override
	public boolean requestsAns() {
		return ansProvider != null && avInput != null;
	}
}
