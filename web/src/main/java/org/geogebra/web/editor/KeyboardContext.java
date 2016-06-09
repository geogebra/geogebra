package org.geogebra.web.editor;

import org.geogebra.common.main.KeyboardLocale;
import org.geogebra.web.html5.util.keyboard.HasKeyboard;

public class KeyboardContext implements HasKeyboard {

	public void updateKeyboardHeight() {
		// TODO Auto-generated method stub

	}

	public double getHeight() {
		// TODO Auto-generated method stub
		return 600;
	}

	public double getWidth() {
		// TODO Auto-generated method stub
		return 800;
	}

	public KeyboardLocale getLocalization() {
		return new KeyboardLocale() {

			public String getFunction(String string) {
				// TODO Auto-generated method stub
				return string;
			}

			public String getLocaleStr() {
				// TODO Auto-generated method stub
				return "en";
			}

			public String getKeyboardRow(int i) {
				// TODO Auto-generated method stub
				return i == 1 ? "qQwWeErRtTyYuUiIoOpP"
						: (i == 2 ? "aAsSdDfFgGhHjJkKlL''" : "zZxXcCvVbBnNmM");
			}
		};
	}

}
