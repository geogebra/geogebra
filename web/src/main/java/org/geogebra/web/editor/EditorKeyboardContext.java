package org.geogebra.web.editor;

import java.util.List;

import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.LocalizationI;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.web.html5.bridge.AttributeProvider;

import elemental2.dom.DomGlobal;

public class EditorKeyboardContext implements HasKeyboard {

	private final AttributeProvider element;

	public EditorKeyboardContext(AttributeProvider el) {
		this.element = el;
	}

	@Override
	public void updateKeyboardHeight() {
		// not needed
	}

	@Override
	public double getInnerWidth() {
		return DomGlobal.document.body.clientWidth;
	}

	@Override
	public LocalizationI getLocalization() {
		return new LocalizationI() {

			@Override
			public String getLanguageTag() {
				return "en-US";
			}

			@Override
			public String getCommand(String key) {
				return key;
			}

			@Override
			public String getMenu(String key) {
				return key;
			}

			@Override
			public String getKeyboardRow(int i) {
				String[] rows = {"qQwWeErRtTyYuUiIoOpP",
						"aAsSdDfFgGhHjJkKlL",
						"zZxXcCvVbBnNmM"};
				return rows[i - 1];
			}
		};
	}

	@Override
	public boolean attachedToEqEditor() {
		return false;
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		return AppKeyboardType.fromName(element.getAttribute("keyboardtype"));
	}

	@Override
	public InputBoxType getInputBoxType() {
		return null;
	}

	@Override
	public List<String> getInputBoxFunctionVars() {
		return null;
	}

}
