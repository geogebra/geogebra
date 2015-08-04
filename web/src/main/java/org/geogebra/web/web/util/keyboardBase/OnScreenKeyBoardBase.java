package org.geogebra.web.web.util.keyboardBase;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.Language;
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
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 * @param app
	 *            {@link App}
	 * @return instance of onScreenKeyBoard
	 */
	public static OnScreenKeyBoardBase getInstance(
			UpdateKeyBoardListener listener, App app) {

		if (instances == null) {
			instances = new HashMap<App,OnScreenKeyBoardBase>();
		}
		OnScreenKeyBoardBase instance = instances.get(app);
		if (instance == null) {
			instance = new OnScreenKeyBoardBase(app);
			instances.put(app, instance);
		}

		instance.setListener(listener);
		return instance;
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

	@Override
	public void show() {
		this.keyboardWanted = true;
		updateSize();
		checkLanguage();
		setStyleName();//maybe not needed always, but definitely in Win8 app
		super.show();
	}
}