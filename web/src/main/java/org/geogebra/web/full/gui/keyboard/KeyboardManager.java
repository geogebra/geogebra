package org.geogebra.web.full.gui.keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboardW;

public class KeyboardManager {

	private AppW app;

	/**
	 * Constructor
	 *
	 * @param appWFull
	 * 			the application
	 */
	public KeyboardManager(AppW appWFull) {
		this.app = appWFull;
	}

	/**
	 *
	 * @return list of view ids which have keyboard.
	 */
	public List<Integer> getKeyboardViews() {
		ArrayList<Integer> keyboardViews = getKeyboardViewsNoEV();
		if (app.getKernel().getConstruction().hasInputBoxes()) {
			keyboardViews.add(App.VIEW_EUCLIDIAN);
			keyboardViews.add(App.VIEW_EUCLIDIAN2);
		}
		return keyboardViews;
	}

	private ArrayList<Integer> getKeyboardViewsNoEV() {
		ArrayList<Integer> keyboardViews = new ArrayList<>();
		if (app.showAlgebraInput()
				&& app.getInputPosition() == InputPosition.algebraView) {
			keyboardViews.add(App.VIEW_ALGEBRA);
		}
		keyboardViews.addAll(Arrays.asList(App.VIEW_CAS, App.VIEW_SPREADSHEET,
				App.VIEW_PROBABILITY_CALCULATOR));
		return keyboardViews;
	}

	/**
	 * Update keyboard style.
	 *
	 * @param keyBoard to update.
	 */
	public void updateStyle(VirtualKeyboardW keyBoard) {
		Dom.toggleClass(keyBoard.asWidget(), "detached", shouldDetach());
	}

	/**
	 *
	 * @return keyboard is detachable, no view uses it
	 */
	public boolean shouldDetach() {
		for (Integer viewId : this.getKeyboardViewsNoEV()) {
			if (app.showView(viewId)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param keyboard
	 *            keyboard
	 * @return height inside of the geogebra window
	 */
	public int estimateKeyboardHeight(VirtualKeyboardW keyboard) {
		int realHeight = keyboard.getOffsetHeight();
		if (realHeight > 0) {
			return realHeight;
		}
		int newHeight = app.needsSmallKeyboard() ? TabbedKeyboard.SMALL_HEIGHT
				: TabbedKeyboard.BIG_HEIGHT;
		// add switcher height
		newHeight += 40;

		return newHeight;
	}

}
