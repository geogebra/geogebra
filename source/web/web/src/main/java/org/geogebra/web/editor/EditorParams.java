package org.geogebra.web.editor;

import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.html5.bridge.AttributeProvider;

public class EditorParams {
	private final AttributeProvider element;
	private final MathFieldW mathField;

	/**
	 *
	 * @param element where the params come from.
	 * @param mathField to setup.
	 */
	public EditorParams(AttributeProvider element, MathFieldW mathField) {
		this.element = element;
		this.mathField = mathField;
		process();
	}

	private void process() {
		String backgroundColor = getBackgroundColor();
		if (!"".equals(backgroundColor)) {
			mathField.setBackgroundColor(backgroundColor);
		}
		String foregroundColor = getForegroundColor();
		if (!"".equals(foregroundColor)) {
			mathField.setForegroundColor(foregroundColor);
		}
		mathField.setFontSize(getFontSize());

		if (isTextMode()) {
			mathField.setPlainTextMode(true);
		}
	}

	private boolean isTextMode() {
		return isTrue("textmode");
	}

	private double getFontSize() {
		return toDouble(element.getAttribute("fontsize"), 16.0);
	}

	private String getForegroundColor() {
		return element.getAttribute("editorforegroundcolor");
	}

	private String getBackgroundColor() {
		return element.getAttribute("editorbackgroundcolor");
	}

	private boolean isTrue(String attribute) {
		return "true".equals(element.getAttribute(attribute));
	}

	private double toDouble(String attribute, double fallback) {
		if (!"".equals(attribute)) {
			try {
				return Double.parseDouble(attribute);
			} catch (NumberFormatException ex) {
				// fallback
			}
		}
		return fallback;
	}

	public boolean isPreventFocus() {
		return isTrue("preventfocus");
	}
}
