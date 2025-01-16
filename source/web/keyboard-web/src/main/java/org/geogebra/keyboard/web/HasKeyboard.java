package org.geogebra.keyboard.web;

import java.util.List;

import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.LocalizationI;
import org.geogebra.keyboard.base.impl.TemplateKeyProvider;

/**
 *
 */
public interface HasKeyboard {
	/**
	 * update keyboard height
	 */
	void updateKeyboardHeight();

	/**
	 * @return inner width
	 */
	double getInnerWidth();

	/**
	 * @return localization
	 */
	LocalizationI getLocalization();

	/**
	 * @return true if in whiteboard, where keyboard is used for equation editor
	 */
	boolean attachedToEqEditor();

	/**
	 * @return the keyboard type based on the app, see {@link AppKeyboardType}
	 */
	AppKeyboardType getKeyboardType();

	/**
	 * @return keyboard type specific to the input box geo type, see {@link InputBoxType}
	 */
	InputBoxType getInputBoxType();

	/**
	 * @return list of function vars if the inputbox is connected to a function
	 */
	List<String> getInputBoxFunctionVars();

	TemplateKeyProvider getTemplateKeyProvider();
}
