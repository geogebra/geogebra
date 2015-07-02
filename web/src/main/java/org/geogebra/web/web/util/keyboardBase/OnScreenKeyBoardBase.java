package org.geogebra.web.web.util.keyboardBase;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.Language;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;

/**
 * on screen keyboard containing mathematical symbols and formulas
 */
public class OnScreenKeyBoardBase extends KBBase {

	private static HashMap<App, OnScreenKeyBoardBase> instances;

	/**
	 * creates a keyboard instance
	 * 
	 * @param textField
	 *            the textField to receive the key-events
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 * @param app
	 *            {@link App}
	 * @return instance of onScreenKeyBoard
	 */
	public static OnScreenKeyBoardBase getInstance(MathKeyboardListener textField,
			UpdateKeyBoardListener listener, App app) {

		if (instances == null) {
			instances = new HashMap<App,OnScreenKeyBoardBase>();
		}
		OnScreenKeyBoardBase instance = instances.get(app);
		if (instance == null) {
			instance = new OnScreenKeyBoardBase(app);
			instances.put(app, instance);
		}

		instance.setTextField(textField);

		instance.setListener(listener);
		return instance;
	}

	/**
	 * updates the textField of the current instance, if the instance is not
	 * null
	 * 
	 * @param app
	 *            the App for which the textField is updated
	 * 
	 * @param textField
	 *            the new textField
	 */
	public static void setInstanceTextField(App app,
	        MathKeyboardListener textField) {
		if (instances != null && instances.get(app) != null) {
			instances.get(app).setTextField(textField);
		}
	}


	/**
	 * should not be called; use getInstance instead
	 * 
	 * @param app
	 */
	private OnScreenKeyBoardBase(App app) {
		super(true);
		this.app = app;
		this.loc = (LocalizationW) app.getLocalization();
		addStyleName("KeyBoard");
		createKeyBoard();
		if (app.has(Feature.KOREAN_KEYBOARD)) {
			// this is needed until we support other fonts than Latin
			supportedLocales.put(Language.Korean.localeGWT, "ko");
		}
		initAccentAcuteLetters();
		initAccentGraveLetters();
		initAccentCaronLetters();
		initAccentCircumflexLetters();
	}

	/**
	 * The text field to be used
	 * 
	 * @param textField
	 *            the text field connected to the keyboard
	 */
	@Override
	public void setTextField(MathKeyboardListener textField) {
		// TODO remove
	}

	@Override
	public void show() {
		this.keyboardWanted = true;
		updateSize();
		checkLanguage();
		setStyleName();//maybe not needed always, but definitely in Win8 app
		super.show();
	}

}