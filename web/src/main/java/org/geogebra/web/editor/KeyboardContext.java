package org.geogebra.web.editor;

import org.geogebra.common.main.KeyboardLocale;
import org.geogebra.web.html5.util.keyboard.HasKeyboard;

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
		};
	}

	public boolean needsSmallKeyboard() {
		// TODO Auto-generated method stub
		return false;
	}

}
