package org.geogebra.web.editor;

import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.web.html5.gui.util.KeyboardLocale;

public class KeyboardContext implements HasKeyboard {

	@Override
	public void updateKeyboardHeight() {
		// TODO Auto-generated method stub

	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return 800;
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

			public String getMenu(String string) {
				return string.replace("Function.", "");
			}

			public String getCommand(String string) {
				return string;
			}
		};
	}

	public boolean needsSmallKeyboard() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasKeyboardInProbCalculator() {
		return true;
	}

}
