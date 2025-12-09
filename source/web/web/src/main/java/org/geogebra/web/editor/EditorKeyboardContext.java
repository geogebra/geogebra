/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.editor;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.LocalizationI;
import org.geogebra.keyboard.base.impl.TemplateKeyProvider;
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

	@Override
	public TemplateKeyProvider getTemplateKeyProvider() {
		return null;
	}

	@Override
	public void showMatrixInputDialog(Consumer<String> ignore) {
		// Not needed
	}
}
