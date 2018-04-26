package org.geogebra.web.editor;

import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.web.html5.gui.util.KeyboardLocale;

/**
 * Dummy keyboard listener
 * 
 * @author Zbynek
 */
public class KeyboardContext implements HasKeyboard {

	@Override
	public void updateKeyboardHeight() {
		// TODO Auto-generated method stub
	}

	@Override
	public double getInnerWidth() {
		// TODO Auto-generated method stub
		return 798;
	}

	@Override
	public KeyboardLocale getLocalization() {
		return new KeyboardLocale() {

			@Override
			public String getFunction(String string) {
				// TODO Auto-generated method stub
				return string;
			}

			@Override
			public String getLocaleStr() {
				// TODO Auto-generated method stub
				return "en";
			}

			@Override
			public String getKeyboardRow(int i) {
				// TODO Auto-generated method stub
				return i == 1 ? "qQwWeErRtTyYuUiIoOpP"
						: (i == 2 ? "aAsSdDfFgGhHjJkKlL''" : "zZxXcCvVbBnNmM");
			}

			@Override
			public String getMenu(String string) {
				return string.replace("Function.", "");
			}

			@Override
			public String getCommand(String string) {
				return string;
			}
		};
	}

	@Override
	public boolean needsSmallKeyboard() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateCenterPanelAndViews() {
		// TODO Auto-generated method stub
	}

}
