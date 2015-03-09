package geogebra.web.util.keyboard;

import com.google.gwt.user.client.ui.Widget;

public interface UpdateKeyBoardListener {

	public abstract void showInputField();

	public abstract void keyBoardNeeded(boolean show, Widget textField);
}
