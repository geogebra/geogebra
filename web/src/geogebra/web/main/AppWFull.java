package geogebra.web.main;

import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.util.keyboard.OnScreenKeyBoard;

import com.google.gwt.user.client.ui.Widget;

public abstract class AppWFull extends AppW {

	protected AppWFull(ArticleElement ae, int dimension, GLookAndFeelI laf) {
		super(ae, dimension, laf);
	}

	@Override
	public void showKeyboard(Widget textField) {
		getAppletFrame().showKeyBoard(true, textField);
		if (textField != null) {
			CancelEventTimer.keyboardSetVisible();
		}
	}

	@Override
	public void updateKeyBoardField(Widget field) {
		OnScreenKeyBoard.setInstanceTextField(this, field);
	}

	@Override
	public void hideKeyboard() {
		getAppletFrame().showKeyBoard(false, null);
	}
}
